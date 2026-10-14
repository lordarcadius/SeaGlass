package com.floydwiz.seaglass.material

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import com.floydwiz.seaglass.SeaGlassLabApi
import androidx.compose.runtime.ProvidableCompositionLocal

/**
 * Values applied on top of the recipe of every glass element below, for exploring a finish.
 * - [blurDp] replaces the blur radius; [tintDelta] is added to the tint opacity.
 * - [grain] replaces the grain opacity; [lightScale] scales the light pool, [rimScale] the rim and [shadowScale] the
 *   drop shadow, caustic and contact shadow.
 */
@SeaGlassLabApi
@Immutable
public class GlassOverrides(
    public val blurDp: Float? = null,
    public val tintDelta: Float = 0f,
    public val grain: Float? = null,
    public val lightScale: Float = 1f,
    public val rimScale: Float = 1f,
    public val shadowScale: Float = 1f,
) {
    public companion object {
        public val None: GlassOverrides = GlassOverrides()
    }
}

@SeaGlassLabApi
public val LocalGlassOverrides: ProvidableCompositionLocal<GlassOverrides> = compositionLocalOf { GlassOverrides.None }
