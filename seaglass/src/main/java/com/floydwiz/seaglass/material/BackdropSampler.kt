package com.floydwiz.seaglass.material

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt

internal val LocalBackdropSampler = staticCompositionLocalOf<BackdropSampler?> { null }

private const val SampleShrink = 8
private const val SampleIntervalMillis = 300L
private const val BrightPercentile = 0.75f

/**
 * Measures how bright the backdrop is under floating glass.
 * - It captures the backdrop at 1/[SampleShrink] size, only while at least one adaptive surface is registered.
 * - Luminance is the WCAG relative luminance of each pixel; a surface reads the 75th percentile over its bounds.
 * - It re-captures only after the backdrop drew again, at most every 300 ms.
 */
@Stable
internal class BackdropSampler {
    var consumers = 0
    var version by mutableIntStateOf(0)
        private set
    internal var origin = Offset.Zero
    private var columns = 0
    private var rows = 0
    private var luminance = FloatArray(0)
    internal var captureRequested = false
    internal var dirty = true
    internal val tick = mutableIntStateOf(0)

    /** The [BrightPercentile] percentile of luminance in the bounds, so a partly bright area still counts as bright. */
    fun luminanceIn(boundsInWindow: Rect): Float? {
        if (columns == 0) return null
        val left = (((boundsInWindow.left - origin.x) / SampleShrink).toInt()).coerceIn(0, columns - 1)
        val right = (((boundsInWindow.right - origin.x) / SampleShrink).toInt()).coerceIn(left, columns - 1)
        val top = (((boundsInWindow.top - origin.y) / SampleShrink).toInt()).coerceIn(0, rows - 1)
        val bottom = (((boundsInWindow.bottom - origin.y) / SampleShrink).toInt()).coerceIn(top, rows - 1)
        val cells = FloatArray((right - left + 1) * (bottom - top + 1))
        var index = 0
        for (y in top..bottom) for (x in left..right) cells[index++] = luminance[y * columns + x]
        cells.sort()
        return cells[((cells.size - 1) * BrightPercentile).toInt()]
    }

    internal fun update(width: Int, height: Int, argb: IntArray) {
        columns = width
        rows = height
        luminance = FloatArray(argb.size) { relativeLuminance(argb[it]) }
        version++
    }
}

private fun channel(value: Int): Float {
    val c = value / 255f
    return if (c <= 0.04045f) c / 12.92f else ((c + 0.055f) / 1.055f).pow(2.4f)
}

private fun relativeLuminance(argb: Int): Float =
    0.2126f * channel((argb shr 16) and 0xFF) + 0.7152f * channel((argb shr 8) and 0xFF) + 0.0722f * channel(argb and 0xFF)

/** Draws the node as usual and, when asked and a surface is listening, records a shrunk copy and reads it back. */
@Composable
internal fun BackdropSampler.captureModifier(): Modifier {
    val layer = rememberGraphicsLayer()
    val recorded = remember { Channel<IntSize>(Channel.CONFLATED) }
    LaunchedEffect(this) {
        while (true) {
            if (consumers > 0 && dirty) {
                dirty = false
                captureRequested = true
                tick.intValue++
                val size = recorded.receive()
                val bitmap = layer.toImageBitmap()
                val pixels = IntArray(size.width * size.height)
                bitmap.readPixels(pixels, 0, 0, size.width, size.height)
                update(size.width, size.height, pixels)
            }
            delay(SampleIntervalMillis)
        }
    }
    return Modifier
        .onGloballyPositioned { origin = it.positionInWindow() }
        .drawWithContent {
            tick.intValue
            if (captureRequested) {
                captureRequested = false
                val size = IntSize((this.size.width / SampleShrink).roundToInt().coerceAtLeast(1), (this.size.height / SampleShrink).roundToInt().coerceAtLeast(1))
                layer.record(size) {
                    scale(1f / SampleShrink, 1f / SampleShrink, pivot = Offset.Zero) { this@drawWithContent.drawContent() }
                }
                recorded.trySend(size)
            } else {
                dirty = true
            }
            drawContent()
        }
}

/** The luminance a floating surface sampled behind it, after the finish's brightness [lifted], and the tint [amount] it chose. */
class AdaptiveSample(val luminance: Float, val lifted: Float, val amount: Float)

internal class Adaptive(val amount: Float, val sample: AdaptiveSample?, val boundsModifier: Modifier)

/**
 * How much protective tint a floating surface needs, 0..1, in steps of 0.25.
 * - Zero unless [requested] or [LocalAdaptiveTint] is set and the surface sits over a [GlassBackdrop].
 * - Fades in above a luminance of 0.4 and is fully on at 0.55, measured after the finish's own brightness [lift] on the
 *   backdrop; the value is smoothed so scrolling content does not make it flicker.
 * - The returned modifier reports the surface bounds and must be applied to it.
 */
@Composable
internal fun rememberAdaptive(requested: Boolean, lift: Float): Adaptive {
    val sampler = LocalBackdropSampler.current
    val participates = (requested || LocalAdaptiveTint.current) && sampler != null
    var bounds by remember { mutableStateOf<Rect?>(null) }
    if (participates) {
        DisposableEffect(sampler) {
            sampler!!.consumers++
            onDispose { sampler.consumers-- }
        }
    }
    val luminance by remember(sampler, participates) {
        derivedStateOf {
            val box = bounds
            if (participates && box != null) sampler!!.run { version; luminanceIn(box) } else null
        }
    }
    val target = luminance?.let { ((it * lift - 0.4f) / 0.15f).coerceIn(0f, 1f) } ?: 0f
    val smooth by animateFloatAsState(target, tween(400), label = "adaptiveTint")
    val amount = (smooth * 4f).roundToInt() / 4f
    return Adaptive(
        amount,
        luminance?.let { AdaptiveSample(it, (it * lift).coerceAtMost(1f), amount) },
        if (participates) Modifier.onGloballyPositioned { bounds = it.boundsInWindow() } else Modifier,
    )
}
