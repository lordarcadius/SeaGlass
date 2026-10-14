package com.floydwiz.seaglasssample.showcase

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.material.GlassLayers
import com.floydwiz.seaglass.material.LocalGlassLayers
import com.floydwiz.seaglass.theme.SeaGlassContrast
import com.floydwiz.seaglass.theme.SeaGlassTheme

/** [SeaGlassTheme] with the glass layer switches of the control sheet applied. */
@Composable
fun LabTheme(
    layers: GlassLayers,
    dark: Boolean = isSystemInDarkTheme(),
    contrast: SeaGlassContrast = SeaGlassContrast.Standard,
    reduceTransparency: Boolean = false,
    dynamicColor: Boolean = false,
    seedRole: GlassRole = GlassRole.Aqua,
    lightDrift: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalGlassLayers provides layers) {
        SeaGlassTheme(dark, contrast, reduceTransparency, dynamicColor, seedRole, lightDrift, content)
    }
}
