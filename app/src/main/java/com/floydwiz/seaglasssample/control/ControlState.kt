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
    contrast: SeaGlassContrast = SeaGlassContrast.Standard,
    reduceTransparency: Boolean = false,
    dynamicColor: Boolean = false,
    seedRole: GlassRole = GlassRole.Aqua,
    layers: GlassLayers = GlassLayers.All,
) {
    var themeMode by mutableStateOf(themeMode)
    var contrast by mutableStateOf(contrast)
    var reduceTransparency by mutableStateOf(reduceTransparency)
    var dynamicColor by mutableStateOf(dynamicColor)
    var seedRole by mutableStateOf(seedRole)
    var layers by mutableStateOf(layers)

    companion object {
        val Saver: Saver<ControlState, List<Any>> = Saver(
            save = { listOf(
                    it.themeMode.name, it.contrast.name, it.reduceTransparency, it.dynamicColor, it.seedRole.name,
                    it.layers.shadow, it.layers.caustic, it.layers.contact, it.layers.body,
                    it.layers.light, it.layers.grain, it.layers.volume, it.layers.rim,
                )
            },
            restore = {
                ControlState(
                    themeMode = ThemeMode.valueOf(it[0] as String),
                    contrast = SeaGlassContrast.valueOf(it[1] as String),
                    reduceTransparency = it[2] as Boolean,
                    dynamicColor = it[3] as Boolean,
                    seedRole = GlassRole.valueOf(it[4] as String),
                    layers = GlassLayers(
                        shadow = it[5] as Boolean, caustic = it[6] as Boolean, contact = it[7] as Boolean,
                        body = it[8] as Boolean, light = it[9] as Boolean, grain = it[10] as Boolean,
                        volume = it[11] as Boolean, rim = it[12] as Boolean,
                    ),
                )
            },
        )
    }
}

@Composable
fun rememberControlState(): ControlState = rememberSaveable(saver = ControlState.Saver) { ControlState() }
