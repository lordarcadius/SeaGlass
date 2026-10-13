package com.floydwiz.seaglasssample.showcase.materials

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.GlassSlider
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.components.selection.SegmentedOption
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassLayers
import com.floydwiz.seaglass.material.GlassOverrides
import com.floydwiz.seaglass.material.GlassRecipeInfo
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalGlassLayers
import com.floydwiz.seaglass.material.LocalGlassOverrides
import com.floydwiz.seaglass.material.glassBoardBackground
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.showcase.screens.saturateBrightness
import kotlin.math.abs
import kotlin.math.roundToInt

private val Finishes = GlassFinish.entries.filter { it != GlassFinish.Plain }
private val Backdrops = listOf("Photo", "Gradient", "Text", "Solid").map { SegmentedOption(it) }

private const val Paragraph =
    "Sea glass begins as a broken bottle and spends decades in the surf. The waves round every edge, the sand frosts the " +
        "surface, and what the tide leaves behind is soft, cloudy and lit from inside. Hold a piece up to the window and the " +
        "light does not pass through so much as bloom: colour pools near the thickest part, the rim catches a thin bright line " +
        "and the shadow underneath turns the same colour as the glass. A good material copies those habits and nothing else."

/** Materials lab: one finish over a backdrop you pick, with every layer of the recipe adjustable on top. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MaterialsLab(contentPadding: PaddingValues) {
    val dark = LocalSeaGlassDark.current
    var finish by remember { mutableStateOf(GlassFinish.Glass) }
    var backdrop by remember { mutableIntStateOf(0) }
    val recipeBlur = GlassRecipeInfo.blurDp(finish, dark)
    val recipeGrain = GlassRecipeInfo.grain(finish, dark)
    var blur by remember(finish, dark) { mutableFloatStateOf(recipeBlur ?: 0f) }
    var tint by remember { mutableFloatStateOf(0f) }
    var grain by remember(finish, dark) { mutableFloatStateOf(recipeGrain) }
    var light by remember { mutableFloatStateOf(1f) }
    var rim by remember { mutableFloatStateOf(1f) }
    var shadow by remember { mutableFloatStateOf(1f) }
    var layers by remember { mutableStateOf(GlassLayers.All) }

    val overrides = GlassOverrides(
        blurDp = blur.takeIf { recipeBlur != null && abs(it - recipeBlur) > 0.05f },
        tintDelta = tint,
        grain = grain.takeIf { abs(it - recipeGrain) > 0.001f },
        lightScale = light, rimScale = rim, shadowScale = shadow,
    )
    fun reset() {
        blur = recipeBlur ?: 0f; tint = 0f; grain = recipeGrain; light = 1f; rim = 1f; shadow = 1f; layers = GlassLayers.All
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(contentPadding).padding(horizontal = 16.dp).padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Label("Finish")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Finishes.forEach { GlassFilterChip(finish == it, { finish = it }, it.name) }
        }
        Label("Backdrop")
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            GlassSegmentedControl(Backdrops, backdrop, { backdrop = it }, segmentWidth = (maxWidth - 8.dp) / Backdrops.size)
        }

        GlassBackdrop(
            Modifier.fillMaxWidth().height(380.dp).clip(RoundedCornerShape(28.dp)),
            backdrop = { StageBackdrop(backdrop, dark) },
        ) {
            CompositionLocalProvider(LocalGlassOverrides provides overrides, LocalGlassLayers provides layers) {
                Column(Modifier.fillMaxSize(), Arrangement.spacedBy(28.dp, Alignment.CenterVertically), Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Specimen(finish, Modifier.size(150.dp, 56.dp), SeaGlassShape.Capsule)
                        Specimen(finish, Modifier.size(96.dp), SeaGlassShape.Lens)
                    }
                    Specimen(finish, Modifier.size(168.dp, 96.dp), SeaGlassShape.Pane(28.dp))
                }
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Adjust", fontSize = 17.sp, fontWeight = FontWeight(700))
                    GlassButton("Reset", { reset() }, style = GlassButtonStyle.Frost, size = GlassButtonSize.S, leadingIcon = "restart_alt")
                }
                SliderRow("Blur", "${blur.roundToInt()} dp", blur, { blur = it }, 0f..40f, enabled = recipeBlur != null)
                SliderRow("Tint", "%+.2f".format(tint), tint, { tint = it }, -0.3f..0.3f)
                SliderRow("Grain", "%.2f".format(grain), grain, { grain = it }, 0f..0.4f)
                SliderRow("Light", "%.1f×".format(light), light, { light = it }, 0f..1.5f)
                SliderRow("Rim", "%.1f×".format(rim), rim, { rim = it }, 0f..1.5f)
                SliderRow("Shadow", "%.1f×".format(shadow), shadow, { shadow = it }, 0f..2f)
                Label("Layers on this stage only")
                LayerSwitches(layers) { layers = it }
            }
        }
        Text(info(finish, dark, recipeBlur, overrides, layers), color = glassOnSurfaceVariant(), fontSize = 13.sp, lineHeight = 19.sp)
        Label("Approximations in this build")
        Approximations.forEach { Text("• $it", color = glassOnSurfaceVariant(), fontSize = 13.sp, lineHeight = 19.sp) }
    }
}

private val Approximations = listOf(
    "Lens magnification is a 1.12× AGSL shader on Android 13 and later; on Android 12 it is a 1.1× scale.",
    "Backdrop saturation and brightness are a colour matrix applied inside Haze; saturation is not a native Haze setting.",
    "Reduce transparency replaces the blur with solid fills from the neutral palette, and is also entered when the system turns window blur off. Below Android 12 there is no blur at all, so it is always on.",
    "Dialogs, sheets and menus are separate windows that cannot blur the page: sheets, alerts and menus sit on a solid surface and the context menu dims instead of blurring.",
    "Adaptive tint samples the backdrop at one eighth of its size every 300 ms and takes the 95th percentile of luminance, so it reacts a little late and in quarter steps.",
    "Edge diffusion is a Haze blur with a gradient mask, not a progressive blur.",
)

@Composable
private fun Label(text: String) {
    Text(text, color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(650))
}

@Composable
private fun Specimen(finish: GlassFinish, modifier: Modifier, shape: SeaGlassShape) {
    GlassSurface(finish, modifier, shape, reactsTo = emptySet()) {
        Text(finish.name, color = LocalContentColor.current, fontSize = 15.sp, fontWeight = FontWeight(650))
    }
}

@Composable
private fun StageBackdrop(kind: Int, dark: Boolean) {
    when (kind) {
        0 -> Image(
            painterResource(R.drawable.seaglass_photo), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
            colorFilter = if (dark) ColorFilter.colorMatrix(saturateBrightness(1.25f, 0.42f)) else null,
        )
        1 -> Box(Modifier.fillMaxSize().glassBoardBackground(dark))
        2 -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(18.dp)) {
            Text(Paragraph + " " + Paragraph, color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp, lineHeight = 25.sp, fontWeight = FontWeight(550))
        }
        else -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
    }
}

@Composable
private fun SliderRow(label: String, value: String, v: Float, onChange: (Float) -> Unit, range: ClosedFloatingPointRange<Float>, enabled: Boolean = true) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.width(64.dp), fontSize = 14.sp, fontWeight = FontWeight(600))
        GlassSlider(v, onChange, Modifier.weight(1f), enabled = enabled, valueRange = range, valueLabel = { value })
        Text(if (enabled) value else "none", Modifier.width(62.dp), color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(600))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LayerSwitches(layers: GlassLayers, onChange: (GlassLayers) -> Unit) {
    val rows = listOf(
        "Shadow" to layers.shadow, "Caustic" to layers.caustic, "Contact" to layers.contact, "Body" to layers.body,
        "Light" to layers.light, "Grain" to layers.grain, "Volume" to layers.volume, "Rim" to layers.rim,
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { (name, on) ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                GlassSwitch(on, {
                    onChange(
                        when (name) {
                            "Shadow" -> layers.copy(shadow = it)
                            "Caustic" -> layers.copy(caustic = it)
                            "Contact" -> layers.copy(contact = it)
                            "Body" -> layers.copy(body = it)
                            "Light" -> layers.copy(light = it)
                            "Grain" -> layers.copy(grain = it)
                            "Volume" -> layers.copy(volume = it)
                            else -> layers.copy(rim = it)
                        },
                    )
                })
                Text(name, fontSize = 14.sp)
            }
        }
    }
}

private fun info(finish: GlassFinish, dark: Boolean, recipeBlur: Float?, overrides: GlassOverrides, layers: GlassLayers): String {
    val used = buildList {
        if (layers.shadow) add("drop shadow")
        if (layers.caustic) add("caustic")
        if (layers.contact) add("contact shadow")
        if (layers.body) add(if (recipeBlur != null) "body (blur ${recipeBlur.roundToInt()} dp, tint)" else "body (tint)")
        if (layers.light) add("light pool")
        if (layers.grain) add("grain")
        if (layers.volume) add("volume shadows")
        if (layers.rim) add("rim")
    }
    val changed = buildList {
        overrides.blurDp?.let { add("blur ${it.roundToInt()} dp") }
        if (overrides.tintDelta != 0f) add("tint %+.2f".format(overrides.tintDelta))
        overrides.grain?.let { add("grain %.2f".format(it)) }
        if (overrides.lightScale != 1f) add("light %.1f×".format(overrides.lightScale))
        if (overrides.rimScale != 1f) add("rim %.1f×".format(overrides.rimScale))
        if (overrides.shadowScale != 1f) add("shadow %.1f×".format(overrides.shadowScale))
    }
    return "${finish.name} in the ${if (dark) "dark" else "light"} recipe draws: ${used.joinToString(", ").ifEmpty { "nothing, every layer is off" }}. " +
        if (changed.isEmpty()) "No values are overridden." else "Overridden: ${changed.joinToString(", ")}."
}
