package com.floydwiz.seaglasssample.demos.weather

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.containers.GlassInsetList
import com.floydwiz.seaglass.components.containers.GlassListRow
import com.floydwiz.seaglass.components.feedback.GlassRingProgress
import com.floydwiz.seaglass.components.fab.GlassExtendedFab
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.components.selection.GlassPillSlider
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalAdaptiveTint
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.demos.AboutDemoSheet
import com.floydwiz.seaglasssample.demos.DemoChrome
import com.floydwiz.seaglasssample.demos.DemoInfo
import kotlin.math.roundToInt

private enum class Sky(val label: String, val icon: String, val top: Long, val bottom: Long, val darkText: Boolean) {
    Clear("Clear", "wb_sunny", 0xFF5BB8F0, 0xFFD6F0FF, true),
    Cloudy("Cloudy", "cloud", 0xFF8A98A8, 0xFFD8DEE5, true),
    Rain("Rain", "rainy", 0xFF3E4B5E, 0xFF7E8EA3, false),
    Dusk("Dusk", "nights_stay", 0xFF2B2A5C, 0xFFF2A67A, false),
}

private class Place(val name: String, val region: String, val temp: Int, val sky: Sky)

private val Info = DemoInfo(
    "Weather",
    "A sky that follows the conditions, a clear overlay card and hourly items floating over it, a daily list on milk and a time scrubber. The brief asks for glass cards over the sky; the sky is the imagery, so the cards are clear overlay captions with adaptive tint, never stacked on other glass.",
    listOf(
        "Clear overlay card and hourly items over the sky" to "Overlay captions over imagery are clear; adaptive tint on.",
        "Daily list on milk" to "R4: long-form data sits on milk.",
        "Time scrubber pill slider" to "R2 and the fill finish; a lens while dragged.",
        "Conditions switcher (debug chips)" to "R3: unchosen in wells, the chosen one lifts.",
        "Add-location extended FAB" to "The single glass primary action.",
        "Detail panes" to "Milk panes for wind, humidity, UV and sunrise; the UV index is a progress ring.",
    ),
)

/** The sky colour, mixed 92% into the surface on dark themes so it stays dim. */
private fun skyColor(argb: Long, dark: Boolean, surface: Color): Color = if (dark) lerp(Color(argb), surface, 0.92f) else Color(argb)

/** Text colour for page-level type drawn straight on the sky: ink on the bright skies of the light theme, white elsewhere. */
@Composable
private fun skyText(sky: Sky): Color = if (sky.darkText && !LocalSeaGlassDark.current) MaterialTheme.colorScheme.onSurface else Color.White

private val Hours = listOf("Now", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")
private val Days = listOf("Today" to 18, "Sat" to 17, "Sun" to 15, "Mon" to 14, "Tue" to 16, "Wed" to 19)

private fun hourTemp(base: Int, hour: Int) = base + when (hour) { 0, 1 -> -1; in 2..5 -> hour - 1; in 6..8 -> 4 - (hour - 6); else -> 2 - (hour - 9) }

/** Weather demo: Now, Locations and Detail. */
@Composable
fun WeatherDemo(onBack: () -> Unit) {
    var sky by remember { mutableStateOf(Sky.Clear) }
    var screen by remember { mutableIntStateOf(0) }
    var hour by remember { mutableFloatStateOf(0f) }
    var about by remember { mutableStateOf(false) }
    val places = remember { mutableStateListOf(Place("Shore walk", "North rocks", 18, Sky.Clear), Place("Blue hour pier", "East bay", 14, Sky.Dusk), Place("Kelp point", "South head", 12, Sky.Rain)) }
    var place by remember { mutableIntStateOf(0) }
    BackHandler(screen != 0) { screen = 0 }
    val dark = LocalSeaGlassDark.current
    val surface = MaterialTheme.colorScheme.surface
    val top by animateColorAsState(skyColor(sky.top, dark, surface), tween(700), label = "skyTop")
    val bottom by animateColorAsState(skyColor(sky.bottom, dark, surface), tween(700), label = "skyBottom")
    val index = hour.times(Hours.size - 1).roundToInt()
    val temp = hourTemp(places[place].temp, index)

    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = { Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(top, bottom)))) },
    ) {
        if (screen == 1) {
            LocationsScreen(places, place, skyText(sky), { place = it; sky = places[it].sky; screen = 0 }, { places.add(Place("New spot ${places.size + 1}", "Made up", 15, Sky.Cloudy)) })
        } else if (screen == 2) {
            DetailScreen(places[place], skyText(sky))
        } else {
            NowScreen(sky, { sky = it }, places[place], temp, index, hour, { hour = it }, { screen = 2 })
        }
        DemoChrome(onBack, { about = true }, Modifier.align(Alignment.TopStart))
        if (screen == 0) Box(Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(top = 60.dp, end = 16.dp)) { GlassBarLens("pin_drop", { screen = 1 }, description = "Locations") }
    }
    if (about) AboutDemoSheet(Info) { about = false }
}

@Composable
private fun NowScreen(
    sky: Sky, onSky: (Sky) -> Unit, place: Place, temp: Int, index: Int, hour: Float, onHour: (Float) -> Unit, onDetail: () -> Unit,
) {
    val text = skyText(sky)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding().padding(top = 64.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
            GlassSurface(
                GlassFinish.Clear, Modifier.padding(horizontal = 16.dp).fillMaxWidth(), SeaGlassShape.Pane(28.dp),
                onClick = onDetail, contentAlignment = Alignment.CenterStart,
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(place.name, fontSize = 16.sp, fontWeight = FontWeight(650))
                    Text("$temp°", fontSize = 76.sp, fontWeight = FontWeight(300), letterSpacing = (-2).sp, lineHeight = 84.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        SymbolIcon(sky.icon, size = 24.dp, fill = true, tint = glassAccent())
                        Text("${sky.label} · ${Hours[index].let { if (it == "Now") "now" else "$it:00" }}", fontSize = 17.sp, fontWeight = FontWeight(600))
                    }
                }
            }
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), Arrangement.spacedBy(10.dp)) {
                Hours.forEachIndexed { i, label ->
                    GlassSurface(GlassFinish.Clear, Modifier.size(64.dp, 96.dp), SeaGlassShape.Pane(24.dp), reactsTo = emptySet()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(label, fontSize = 12.5.sp, fontWeight = FontWeight(if (i == index) 750 else 550))
                            SymbolIcon(if (i in 4..7 && sky == Sky.Clear) "wb_sunny" else sky.icon, size = 24.dp, fill = true, tint = glassAccent())
                            Text("${hourTemp(place.temp, i)}°", fontSize = 15.sp, fontWeight = FontWeight(700))
                        }
                    }
                }
            }
            GlassPillSlider(hour, onHour, "schedule", Modifier.padding(horizontal = 16.dp), width = 360.dp, height = 44.dp, endIcon = "bedtime")
        }
        GlassInsetList(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Days.forEachIndexed { i, (day, t) ->
                GlassListRow(day, leadingSymbol = if (i % 3 == 0) sky.icon else "partly_cloudy_day", supporting = "${t - 6}° – $t°", divider = i < Days.lastIndex, trailing = { Text("$t°", fontSize = 15.sp, fontWeight = FontWeight(700)) })
            }
        }
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Conditions (debug)", fontSize = 12.5.sp, fontWeight = FontWeight(650), color = text)
            Row(Modifier.horizontalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
                Sky.entries.forEach { GlassFilterChip(sky == it, { onSky(it) }, it.label) }
            }
        }
    }
}

@Composable
private fun LocationsScreen(places: List<Place>, current: Int, titleColor: Color, onPick: (Int) -> Unit, onAdd: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Locations", color = titleColor, fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
            GlassInsetList(Modifier.fillMaxWidth()) {
                places.forEachIndexed { i, p ->
                    GlassListRow(
                        p.name, onClick = { onPick(i) }, leadingSymbol = p.sky.icon, supporting = p.region, selected = i == current, divider = i < places.lastIndex,
                        trailing = { Text("${p.temp}°", fontSize = 17.sp, fontWeight = FontWeight(700)) },
                    )
                }
            }
        }
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
            GlassExtendedFab("Add location", "add_location_alt", onAdd, Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(end = 16.dp, bottom = 24.dp))
        }
    }
}

@Composable
private fun DetailScreen(place: Place, titleColor: Color) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 40.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(place.name, color = titleColor, fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            DetailPane("Wind", "air", "12 km/h", "North-east, gusting 21", Modifier.weight(1f))
            DetailPane("Humidity", "water_drop", "68%", "Dew point 11°", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            GlassCard(Modifier.weight(1f).height(170.dp), shape = SeaGlassShape.Pane(28.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("UV index", fontSize = 14.sp, fontWeight = FontWeight(650), color = glassOnSurfaceVariant())
                    GlassRingProgress(0.45f, size = 84.dp, stroke = 10.dp) { Text("5", fontSize = 22.sp, fontWeight = FontWeight(760)) }
                    Text("Moderate", fontSize = 13.5.sp, fontWeight = FontWeight(600))
                }
            }
            DetailPane("Sunrise", "wb_twilight", "6:12", "Sunset 18:41", Modifier.weight(1f).height(170.dp))
        }
    }
}

@Composable
private fun DetailPane(title: String, icon: String, value: String, note: String, modifier: Modifier = Modifier) {
    GlassCard(modifier.height(170.dp), shape = SeaGlassShape.Pane(28.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                SymbolIcon(icon, size = 20.dp, tint = glassAccent())
                Text(title, fontSize = 14.sp, fontWeight = FontWeight(650), color = glassOnSurfaceVariant())
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 30.sp, fontWeight = FontWeight(740))
            Text(note, fontSize = 13.sp, color = glassOnSurfaceVariant(), fontWeight = FontWeight(500), lineHeight = 18.sp)
        }
    }
}
