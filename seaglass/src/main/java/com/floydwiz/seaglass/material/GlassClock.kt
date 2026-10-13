package com.floydwiz.seaglass.material

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.util.lerp

/** Shared time source for the 8 s light drift; read only in draw so it never recomposes. */
@Stable
class GlassClock {
    var seconds by mutableFloatStateOf(0f)
        internal set
}

val LocalGlassClock = staticCompositionLocalOf { GlassClock() }

internal val LocalClockOwned = staticCompositionLocalOf { false }

@Composable
internal fun rememberGlassClock(running: Boolean, motionScale: Float): GlassClock {
    val clock = remember { GlassClock() }
    LaunchedEffect(running, motionScale) {
        if (running) {
            while (true) {
                androidx.compose.runtime.withFrameNanos { nanos ->
                    clock.seconds = ((nanos / 1_000_000f / motionScale) % 16_000f) / 1000f
                }
            }
        }
    }
    return clock
}

private val DriftEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

/** Position of the alternating ease-in-out drift, 0..1, for an element shifted by [offsetSeconds]. */
internal fun driftProgress(seconds: Float, offsetSeconds: Float): Float {
    val p = ((seconds + offsetSeconds) % 16f) / 8f
    return DriftEasing.transform(if (p > 1f) 2f - p else p)
}

internal fun driftOffset(progress: Float, width: Float, height: Float): Pair<Float, Float> = Pair(
    lerp(-SeaGlassTokens.Light.DriftX, SeaGlassTokens.Light.DriftX, progress) * width,
    lerp(-SeaGlassTokens.Light.DriftY, SeaGlassTokens.Light.DriftY, progress) * height,
)
