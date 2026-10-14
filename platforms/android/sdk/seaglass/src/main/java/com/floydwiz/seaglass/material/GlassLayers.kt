package com.floydwiz.seaglass.material

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/** Debug switches for each layer of the material stack. */
@Immutable
data class GlassLayers(
    val shadow: Boolean = true,
    val caustic: Boolean = true,
    val contact: Boolean = true,
    val body: Boolean = true,
    val light: Boolean = true,
    val grain: Boolean = true,
    val volume: Boolean = true,
    val rim: Boolean = true,
) {
    companion object {
        val All = GlassLayers()
        val None = GlassLayers(false, false, false, false, false, false, false, false)
    }
}

val LocalGlassLayers = staticCompositionLocalOf { GlassLayers.All }
