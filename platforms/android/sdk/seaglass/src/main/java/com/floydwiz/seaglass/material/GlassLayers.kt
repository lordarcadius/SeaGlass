package com.floydwiz.seaglass.material

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.floydwiz.seaglass.SeaGlassLabApi
import androidx.compose.runtime.ProvidableCompositionLocal

/** Debug switches for each layer of the material stack. */
@SeaGlassLabApi
@Immutable
public data class GlassLayers(
    val shadow: Boolean = true,
    val caustic: Boolean = true,
    val contact: Boolean = true,
    val body: Boolean = true,
    val light: Boolean = true,
    val grain: Boolean = true,
    val volume: Boolean = true,
    val rim: Boolean = true,
) {
    public companion object {
        public val All: GlassLayers = GlassLayers()
        public val None: GlassLayers = GlassLayers(false, false, false, false, false, false, false, false)
    }
}

@SeaGlassLabApi
public val LocalGlassLayers: ProvidableCompositionLocal<GlassLayers> = staticCompositionLocalOf { GlassLayers.All }
