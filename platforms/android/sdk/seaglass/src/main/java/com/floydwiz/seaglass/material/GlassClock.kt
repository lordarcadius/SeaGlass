package com.floydwiz.seaglass.material

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.util.lerp
import com.floydwiz.seaglass.SeaGlassLabApi

/** Shared time source for the 8 s light drift; read only in draw so it never recomposes. */
@Stable
internal class GlassClock {
    var seconds by mutableFloatStateOf(0f)
        internal set
}

internal val LocalGlassClock = staticCompositionLocalOf { GlassClock() }

internal val LocalClockOwned = staticCompositionLocalOf { false }

@Composable
internal fun rememberGlassClock(running: Boolean, motionScale: Float): GlassClock {
    val clock = remember { GlassClock() }
    LaunchedEffect(running, motionScale) {
        if (running) {
            var start = -1L
            while (true) {
                androidx.compose.runtime.withFrameNanos { nanos ->
                    if (start < 0) start = nanos
                    clock.seconds = (((nanos - start) / 1_000_000f / motionScale) % 16_000f) / 1000f
                }
            }
        }
    }
    return clock
}

/** Gives the glass below its own drift clock, which starts again at zero whenever [key] changes. */
@SeaGlassLabApi
@Composable
public fun GlassClockScope(key: Any?, content: @Composable () -> Unit) {
    key(key) {
        val scale = LocalMotionScale.current * SeaGlassTokens.Motion.slowdown
        val clock = rememberGlassClock(LocalMotionEnabled.current, scale)
        androidx.compose.runtime.CompositionLocalProvider(LocalGlassClock provides clock, content = content)
    }
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
