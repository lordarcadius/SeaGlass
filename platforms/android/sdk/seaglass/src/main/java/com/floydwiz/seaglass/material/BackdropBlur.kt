package com.floydwiz.seaglass.material

import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.GraphicsContext
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.CompositingStrategy
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.ceil
import kotlin.math.roundToInt

/** Where a glass element sits relative to the sources of its [GlassBackdrop]; it sees only the sources below it. */
internal enum class BackdropRank { Backdrop, Layer, Content }

internal val LocalGlassBackdrop = staticCompositionLocalOf<GlassBackdropState?> { null }
internal val LocalBackdropRank = staticCompositionLocalOf { BackdropRank.Content }

private const val TargetBlurSigma = 4.5f
private const val MaxDownscale = 8

internal data class BlurKey(val blur: Float, val saturation: Float, val brightness: Float, val includesLayer: Boolean)

private class BlurVariant(val low: GraphicsLayer, val blurred: GraphicsLayer) {
    var downscale = 1
    var size = IntSize.Zero
    var density = 0f
}

internal fun blurKey(blur: Float, saturation: Float, brightness: Float, includesLayer: Boolean) =
    BlurKey(quantise(blur, 2f), quantise(saturation, 100f), quantise(brightness, 100f), includesLayer)

private fun quantise(value: Float, steps: Float) = (value * steps).roundToInt() / steps

/**
 * The shared blurred copies of one [GlassBackdrop], built once and cropped by every glass element.
 * - [base] and [layer] are the recorded sources; a source re-records only when its own content draws again.
 * - A variant is one blur radius and colour matrix over base (and layer, for elements above it). It is captured
 *   at 1/[MaxDownscale] to 1x scale, blurred by a RenderEffect and cached in an offscreen layer, so its cost does not
 *   depend on how many elements read it. HWUI refreshes it when anything inside a source changes.
 * - Blur radius and colours are quantised. A variant exists only while an element holds its key ([acquire] and
 *   [release]), so a slider that changes the radius every frame frees the layers it leaves behind.
 */
@Stable
internal class GlassBackdropState(private val graphicsContext: GraphicsContext) {
    val base: GraphicsLayer = graphicsContext.createGraphicsLayer()
    val layer: GraphicsLayer = graphicsContext.createGraphicsLayer()
    var hasLayerSource = false
    var size by mutableStateOf(IntSize.Zero)
    var coordinates: LayoutCoordinates? = null
    private val variants = HashMap<BlurKey, BlurVariant>()
    private val holders = HashMap<BlurKey, Int>()

    fun keyFor(blur: Float, saturation: Float, brightness: Float, includesLayer: Boolean) =
        blurKey(blur, saturation, brightness, includesLayer && hasLayerSource)

    fun acquire(key: BlurKey) {
        holders[key] = (holders[key] ?: 0) + 1
    }

    fun release(key: BlurKey) {
        val left = (holders[key] ?: return) - 1
        if (left > 0) {
            holders[key] = left
            return
        }
        holders.remove(key)
        variants.remove(key)?.let {
            graphicsContext.releaseGraphicsLayer(it.low)
            graphicsContext.releaseGraphicsLayer(it.blurred)
        }
    }

    fun release() {
        graphicsContext.releaseGraphicsLayer(base)
        graphicsContext.releaseGraphicsLayer(layer)
        variants.values.forEach {
            graphicsContext.releaseGraphicsLayer(it.low)
            graphicsContext.releaseGraphicsLayer(it.blurred)
        }
        variants.clear()
        holders.clear()
    }

    /** Position of [consumer] inside the backdrop, or null when it is in another window or not attached. */
    fun offsetOf(consumer: LayoutCoordinates): Offset? {
        val root = coordinates ?: return null
        if (!root.isAttached || !consumer.isAttached || root.findRootCoordinates() !== consumer.findRootCoordinates()) return null
        return root.localPositionOf(consumer, Offset.Zero)
    }

    /** Draws the blurred backdrop so that the point at [origin] in the backdrop lands at the scope's top-left. */
    fun DrawScope.drawBlurred(origin: Offset, key: BlurKey) {
        if (key !in holders) return
        val variant = variantFor(key, density)
        if (variant.size == IntSize.Zero) return
        val factor = variant.downscale.toFloat()
        translate(-origin.x, -origin.y) { scale(factor, factor, pivot = Offset.Zero) { drawLayer(variant.blurred) } }
    }

    private fun variantFor(key: BlurKey, density: Float): BlurVariant {
        val variant = variants.getOrPut(key) { BlurVariant(graphicsContext.createGraphicsLayer(), graphicsContext.createGraphicsLayer()) }
        if (variant.size != size || variant.density != density) record(variant, key, density)
        return variant
    }

    private fun record(variant: BlurVariant, key: BlurKey, density: Float) {
        variant.size = size
        variant.density = density
        if (size == IntSize.Zero) return
        val sigma = key.blur * density
        val downscale = (sigma / TargetBlurSigma).roundToInt().coerceIn(1, MaxDownscale)
        variant.downscale = downscale
        val lowSize = IntSize(ceil(size.width / downscale.toFloat()).toInt(), ceil(size.height / downscale.toFloat()).toInt())
        val blur = RenderEffect.createBlurEffect(sigma / downscale, sigma / downscale, Shader.TileMode.CLAMP)
        val effect = if (key.saturation == 1f && key.brightness == 1f) {
            blur
        } else {
            RenderEffect.createColorFilterEffect(ColorFilter.colorMatrix(saturationBrightness(key.saturation, key.brightness)).asAndroidColorFilter(), blur)
        }
        variant.low.renderEffect = effect.asComposeRenderEffect()
        val inverse = 1f / downscale
        variant.low.record(Density(density), LayoutDirection.Ltr, lowSize) {
            scale(inverse, inverse, pivot = Offset.Zero) {
                drawLayer(base)
                if (key.includesLayer) drawLayer(layer)
            }
        }
        variant.blurred.compositingStrategy = CompositingStrategy.Offscreen
        variant.blurred.record(Density(density), LayoutDirection.Ltr, lowSize) { drawLayer(variant.low) }
    }
}

/** Records this node's content into [target] and draws it from there, so blurred copies can reuse it. */
internal fun Modifier.backdropSource(target: GraphicsLayer): Modifier = graphicsLayer().drawWithContent {
    target.record(size = IntSize(size.width.roundToInt(), size.height.roundToInt())) { this@drawWithContent.drawContent() }
    drawLayer(target)
}

/**
 * Draws the blurred backdrop behind this element, then [fills] over it.
 * - Without a backdrop, outside the backdrop's window or inside its base source only the fills draw.
 * - [mask], when set, fades the blurred copy by its alpha before the fills draw.
 * - The element's offset in the backdrop is tracked on each placement, so it keeps its place while it scrolls or animates.
 */
@Composable
internal fun Modifier.backdropBlur(
    backdrop: GlassBackdropState?,
    rank: BackdropRank,
    blur: Float,
    saturation: Float,
    brightness: Float,
    fills: List<Brush>,
    mask: Brush? = null,
): Modifier {
    val state = backdrop?.takeIf { rank != BackdropRank.Backdrop }
    val origin = remember { mutableStateOf(Offset.Unspecified) }
    val key = state?.keyFor(blur, saturation, brightness, rank == BackdropRank.Content)
    if (state != null && key != null) {
        DisposableEffect(state, key) {
            state.acquire(key)
            onDispose { state.release(key) }
        }
    }
    val draw = remember(state, rank, key, fills, mask) {
        val tracked = if (state != null) {
            Modifier.onGloballyPositioned { origin.value = state.offsetOf(it) ?: Offset.Unspecified }
        } else {
            Modifier
        }
        tracked.drawBehind {
            val at = origin.value
            if (state != null && key != null && at.isSpecified && state.size != IntSize.Zero) {
                val bounds = Rect(Offset.Zero, size)
                with(state) {
                    if (mask == null) {
                        drawBlurred(at, key)
                    } else {
                        drawContext.canvas.saveLayer(bounds, Paint())
                        drawBlurred(at, key)
                        drawRect(brush = mask, blendMode = BlendMode.DstIn)
                        drawContext.canvas.restore()
                    }
                }
            }
            fills.forEach { drawRect(it) }
        }
    }
    return this.then(draw)
}
