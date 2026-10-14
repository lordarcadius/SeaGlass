package com.floydwiz.seaglasssample.control

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.material.GlassLayers
import com.floydwiz.seaglass.theme.SeaGlassContrast

enum class ThemeMode(val label: String) { System("System"), Light("Light"), Dark("Dark") }

@Stable
class ControlState(
    themeMode: ThemeMode = ThemeMode.System,
    contrast: SeaGlassContrast? = null,
    reduceTransparency: Boolean = false,
    dynamicColor: Boolean = false,
    seedRole: GlassRole = GlassRole.Aqua,
    layers: GlassLayers = GlassLayers.All,
    autoReduceTransparency: Boolean = true,
    fpsOverlay: Boolean = false,
    lightDrift: Boolean = false,
) {
    var themeMode by mutableStateOf(themeMode)

    /** Null follows the system contrast setting. */
    var contrast by mutableStateOf(contrast)
    var reduceTransparency by mutableStateOf(reduceTransparency)

    /** Also reduce transparency while the system reports blur is off. */
    var autoReduceTransparency by mutableStateOf(autoReduceTransparency)
    var dynamicColor by mutableStateOf(dynamicColor)
    var seedRole by mutableStateOf(seedRole)
    var layers by mutableStateOf(layers)

    /** JankStats readout pinned top-left. */
    var fpsOverlay by mutableStateOf(fpsOverlay)

    /** Runs the shared drift clock, which redraws every glass surface each frame. */
    var lightDrift by mutableStateOf(lightDrift)

    var systemContrast by mutableStateOf(SeaGlassContrast.Standard)
    var blurEnabled by mutableStateOf(true)

    val effectiveContrast: SeaGlassContrast get() = contrast ?: systemContrast
    val effectiveReduceTransparency: Boolean get() = reduceTransparency || (autoReduceTransparency && !blurEnabled)

    companion object {
        val Saver: Saver<ControlState, List<Any>> = Saver(
            save = { listOf(
                    it.themeMode.name, it.contrast?.name ?: SystemContrast, it.reduceTransparency, it.dynamicColor, it.seedRole.name,
                    it.layers.shadow, it.layers.caustic, it.layers.contact, it.layers.body,
                    it.layers.light, it.layers.grain, it.layers.volume, it.layers.rim, it.autoReduceTransparency, it.fpsOverlay, it.lightDrift,
                )
            },
            restore = {
                ControlState(
                    themeMode = ThemeMode.valueOf(it[0] as String),
                    contrast = (it[1] as String).takeUnless { name -> name == SystemContrast }?.let(SeaGlassContrast::valueOf),
                    reduceTransparency = it[2] as Boolean,
                    dynamicColor = it[3] as Boolean,
                    seedRole = GlassRole.valueOf(it[4] as String),
                    layers = GlassLayers(
                        shadow = it[5] as Boolean, caustic = it[6] as Boolean, contact = it[7] as Boolean,
                        body = it[8] as Boolean, light = it[9] as Boolean, grain = it[10] as Boolean,
                        volume = it[11] as Boolean, rim = it[12] as Boolean,
                    ),
                    autoReduceTransparency = it[13] as Boolean,
                    fpsOverlay = it[14] as Boolean,
                    lightDrift = it[15] as Boolean,
                )
            },
        )

        const val SystemContrast = "System"
    }
}

@Composable
fun rememberControlState(initial: ControlState = ControlState()): ControlState = rememberSaveable(saver = ControlState.Saver) { initial }
