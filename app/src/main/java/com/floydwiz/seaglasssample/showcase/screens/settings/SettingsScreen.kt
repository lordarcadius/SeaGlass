package com.floydwiz.seaglasssample.showcase.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.containers.GlassInsetList
import com.floydwiz.seaglass.components.containers.GlassListRow
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.selection.GlassPillSlider
import com.floydwiz.seaglass.components.selection.GlassSlider
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.control.LocalControls
import com.floydwiz.seaglasssample.control.ThemeMode
import com.floydwiz.seaglasssample.showcase.screens.ScreenTopBar
import kotlin.math.roundToInt

/** Settings: milk inset lists with glass icon tiles, a honey pill slider, tinted rail sliders and switches. */
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val roles = LocalRolePalettes.current
    val controls = LocalControls.current
    val dark = LocalSeaGlassDark.current
    var brightness by remember { mutableFloatStateOf(0.68f) }
    var adaptive by remember { mutableStateOf(true) }
    var media by remember { mutableFloatStateOf(0.62f) }
    var ring by remember { mutableFloatStateOf(0.4f) }
    var alarm by remember { mutableFloatStateOf(0.8f) }
    var doNotDisturb by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding().padding(bottom = 24.dp)) {
        ScreenTopBar("arrow_back", onBack, "search", {})
        Text(
            "Sound & display", Modifier.padding(start = 20.dp, top = 8.dp, bottom = 22.dp), color = MaterialTheme.colorScheme.onSurface,
            fontSize = 30.sp, fontWeight = FontWeight(760), letterSpacing = (-0.3).sp,
        )

        Caption("DISPLAY")
        GlassInsetList(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(start = 10.dp, end = 10.dp, top = 12.dp, bottom = 14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconTile("light_mode", roles[GlassRole.Honey])
                    Text("Brightness", Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface, fontSize = 15.5.sp, fontWeight = FontWeight(550))
                    Text("${(brightness * 100).roundToInt()}%", color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(600))
                }
                Spacer(Modifier.height(10.dp))
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    GlassPillSlider(brightness, { brightness = it }, "brightness_low", role = roles[GlassRole.Honey], width = maxWidth, height = 40.dp, endIcon = "brightness_high")
                }
            }
            HorizontalDivider(Modifier.padding(start = 56.dp, end = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (dark) 0.10f else 0.08f))
            GlassListRow("Adaptive brightness", leadingIcon = "brightness_auto", leadingRole = roles[GlassRole.Cobalt], trailing = { GlassSwitch(adaptive, { adaptive = it }) })
            GlassListRow(
                "Dark theme", leadingIcon = "dark_mode", leadingRole = roles[GlassRole.Lavender], divider = false,
                trailing = { GlassSwitch(dark, { controls.themeMode = if (it) ThemeMode.Dark else ThemeMode.Light }) },
            )
        }

        Caption("VOLUME", Modifier.padding(top = 24.dp))
        GlassInsetList(Modifier.padding(horizontal = 16.dp)) {
            SliderRow("music_note", roles[GlassRole.Aqua], "Media", media, { media = it }, divider = true)
            SliderRow("ring_volume", roles[GlassRole.SeaPink], "Ring", ring, { ring = it }, divider = true)
            SliderRow("alarm", roles[GlassRole.Kelly], "Alarm", alarm, { alarm = it }, divider = false)
        }

        Caption("SOUNDS", Modifier.padding(top = 24.dp))
        GlassInsetList(Modifier.padding(horizontal = 16.dp)) {
            GlassListRow("Ringtone", onClick = {}, leadingIcon = "notifications", leadingRole = roles[GlassRole.Cornflower], trailing = { ValueWithChevron("Low tide") })
            GlassListRow("Vibration", onClick = {}, leadingIcon = "vibration", leadingRole = roles[GlassRole.Seafoam], trailing = { ValueWithChevron("Gentle") })
            GlassListRow(
                "Do not disturb", leadingIcon = "do_not_disturb_on", leadingRole = LocalRolePalettes.current.neutral, divider = false,
                trailing = { GlassSwitch(doNotDisturb, { doNotDisturb = it }) },
            )
        }
    }
}

@Composable
private fun Caption(text: String, modifier: Modifier = Modifier) {
    Text(
        text, modifier.padding(start = 32.dp, bottom = 10.dp), color = glassOnSurfaceVariant(), fontSize = 12.5.sp,
        fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp,
    )
}

@Composable
private fun IconTile(icon: String, role: TonalTones) {
    GlassSurface(GlassFinish.Glass, Modifier.size(32.dp), SeaGlassShape.Pane(11.dp), role, reactsTo = emptySet()) {
        SymbolIcon(icon, size = 19.dp, fill = true, tint = LocalContentColor.current)
    }
}

@Composable
private fun ValueWithChevron(value: String) {
    Text(value, color = glassOnSurfaceVariant(), fontSize = 14.5.sp, fontWeight = FontWeight(500))
    SymbolIcon("chevron_right", size = 20.dp, tint = glassOnSurfaceVariant())
}

@Composable
private fun SliderRow(icon: String, role: TonalTones, title: String, value: Float, onValueChange: (Float) -> Unit, divider: Boolean) {
    val dark = LocalSeaGlassDark.current
    Column {
        Row(Modifier.padding(start = 10.dp, end = 16.dp, top = 10.dp, bottom = 10.dp), Arrangement.spacedBy(14.dp), Alignment.Top) {
            Column(Modifier.padding(top = 12.dp)) { IconTile(icon, role) }
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight(550))
                    Text("${(value * 100).roundToInt()}%", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(600))
                }
                GlassSlider(value, onValueChange, Modifier.fillMaxWidth().padding(top = 10.dp), role = role)
            }
        }
        if (divider) HorizontalDivider(Modifier.padding(start = 56.dp, end = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (dark) 0.10f else 0.08f))
    }
}
