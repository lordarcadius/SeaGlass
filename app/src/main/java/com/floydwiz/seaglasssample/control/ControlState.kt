package com.floydwiz.seaglasssample.control

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.theme.SeaGlassContrast

enum class ThemeMode(val label: String) { System("System"), Light("Light"), Dark("Dark") }

@Stable
class ControlState(
    themeMode: ThemeMode = ThemeMode.System,
    contrast: SeaGlassContrast = SeaGlassContrast.Standard,
    reduceTransparency: Boolean = false,
    dynamicColor: Boolean = false,
    seedRole: GlassRole = GlassRole.Aqua,
) {
    var themeMode by mutableStateOf(themeMode)
    var contrast by mutableStateOf(contrast)
    var reduceTransparency by mutableStateOf(reduceTransparency)
    var dynamicColor by mutableStateOf(dynamicColor)
    var seedRole by mutableStateOf(seedRole)

    companion object {
        val Saver: Saver<ControlState, List<Any>> = Saver(
            save = { listOf(it.themeMode.name, it.contrast.name, it.reduceTransparency, it.dynamicColor, it.seedRole.name) },
            restore = {
                ControlState(
                    themeMode = ThemeMode.valueOf(it[0] as String),
                    contrast = SeaGlassContrast.valueOf(it[1] as String),
                    reduceTransparency = it[2] as Boolean,
                    dynamicColor = it[3] as Boolean,
                    seedRole = GlassRole.valueOf(it[4] as String),
                )
            },
        )
    }
}

@Composable
fun rememberControlState(): ControlState = rememberSaveable(saver = ControlState.Saver) { ControlState() }
