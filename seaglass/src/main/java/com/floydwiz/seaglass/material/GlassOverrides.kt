package com.floydwiz.seaglass.material

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf

/**
 * Values applied on top of the recipe of every glass element below, for exploring a finish.
 * - [blurDp] replaces the blur radius; [tintDelta] is added to the tint opacity.
 * - [grain] replaces the grain opacity; [lightScale] scales the light pool, [rimScale] the rim and [shadowScale] the
 *   drop shadow, caustic and contact shadow.
 */
@Immutable
class GlassOverrides(
    val blurDp: Float? = null,
    val tintDelta: Float = 0f,
    val grain: Float? = null,
    val lightScale: Float = 1f,
    val rimScale: Float = 1f,
    val shadowScale: Float = 1f,
) {
    companion object {
        val None = GlassOverrides()
    }
}

val LocalGlassOverrides = compositionLocalOf { GlassOverrides.None }
