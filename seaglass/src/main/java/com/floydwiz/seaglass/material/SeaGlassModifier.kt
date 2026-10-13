package com.floydwiz.seaglass.material

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.shadow.DropShadowPainter
import androidx.compose.ui.graphics.shadow.InnerShadowPainter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassContrast
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalReduceTransparency
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos

/**
 * Draws one finish of the glass stack on this element, back to front:
 * - behind the element: tinted shadow, caustic, contact shadow;
 * - clipped to [shape]: backdrop blur + tint, light pool, grain, volume shadows, rim.
 * Content placed in the element draws above all of it. Each layer obeys [LocalGlassLayers].
 */
@Composable
fun Modifier.seaGlass(
    finish: GlassFinish,
    shape: SeaGlassShape = SeaGlassShape.Capsule,
    role: TonalTones = LocalGlassRole.current,
    tinted: Boolean = false,
    adaptive: Boolean = false,
): Modifier {
    val dark = LocalSeaGlassDark.current
    val contrast = LocalGlassContrast.current
    val reduceTransparency = LocalReduceTransparency.current
    val layers = LocalGlassLayers.current
    val hazeState = LocalHazeState.current
    val clock = LocalGlassClock.current
    val motion = LocalMotionEnabled.current
    val grain = rememberGrainBrush()

    val context = remember(dark, contrast, reduceTransparency, role, adaptive, tinted) {
        GlassContext(dark, contrast, reduceTransparency, role, adaptive, tinted)
    }
    val body = remember(finish, context) { GlassRecipes.body(finish, context) }
    val hazeStyle = remember(body) { (body as? Body.Backdrop)?.toHazeStyle() }

    return this
        .glassBehind(finish, context, shape, layers)
        .clip(shape)
        .glassBody(body, hazeStyle, hazeState, layers.body)
        .glassInner(finish, context, shape, layers, clock, motion, grain)
}

private fun Body.Backdrop.toHazeStyle(): HazeBlurStyle {
    val effects = buildList {
        if (saturation != 1f || brightness != 1f) {
            add(HazeColorEffect.colorFilter(ColorFilter.colorMatrix(saturationBrightness(saturation, brightness))))
        }
        fills.forEach { add(HazeColorEffect.tint(it)) }
    }
    return HazeBlurStyle {
        blurRadius(blur.dp)
        noiseFactor(0f)
        colorEffects(effects)
    }
}

/** CSS `saturate(s) brightness(b)` as one colour matrix. */
private fun saturationBrightness(s: Float, b: Float) = ColorMatrix(
    floatArrayOf(
        (0.213f + 0.787f * s) * b, (0.715f - 0.715f * s) * b, (0.072f - 0.072f * s) * b, 0f, 0f,
        (0.213f - 0.213f * s) * b, (0.715f + 0.285f * s) * b, (0.072f - 0.072f * s) * b, 0f, 0f,
        (0.213f - 0.213f * s) * b, (0.715f - 0.715f * s) * b, (0.072f + 0.928f * s) * b, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    ),
)

private fun Modifier.glassBody(
    body: Body,
    hazeStyle: HazeBlurStyle?,
    hazeState: dev.chrisbanes.haze.HazeState?,
    enabled: Boolean,
): Modifier = when {
    !enabled -> this
    body is Body.Solid -> background(body.color)
    body is Body.Backdrop && hazeState != null && hazeStyle != null ->
        hazeBlur(input = HazeInput.Sources(hazeState), style = hazeStyle)
    body is Body.Backdrop -> drawWithCache {
        onDrawBehind { body.fills.forEach { drawRect(it) } }
    }
    else -> this
}

private fun Modifier.glassBehind(
    finish: GlassFinish,
    context: GlassContext,
    shape: SeaGlassShape,
    layers: GlassLayers,
): Modifier = drawWithCache {
    val density = this.density
    val w = size.width / density
    val h = size.height / density
    val spec = GlassRecipes.layers(finish, context, w, h)

    val shadowPainter = spec.shadow.takeIf { layers.shadow }?.let {
        val spread = -((1f - it.scaleX) * w + (1f - it.scaleY) * h) / 4f
        DropShadowPainter(
            shape,
            Shadow(
                radius = shadowRadiusDp(sigmaDp = it.blur, density = density).dp,
                color = it.color,
                spread = spread.dp,
                offset = DpOffset(0.dp, it.offsetY.dp),
            ),
        )
    }
    val caustic = spec.caustic.takeIf { layers.caustic }?.let { ovalPaint(it, w, h, density) }
    val contact = spec.contact.takeIf { layers.contact }?.let { ovalPaint(it, w, h, density) }

    val layer = obtainGraphicsLayer()
    layer.record(this, layoutDirection, IntSize(size.width.roundToInt(), size.height.roundToInt())) {
        shadowPainter?.let { with(it) { draw(size) } }
        caustic?.let { drawOval(it) }
        contact?.let { drawOval(it) }
    }
    onDrawBehind { drawLayer(layer) }
}

/**
 * Compose passes `Shadow.radius` straight to `BlurMaskFilter`, whose sigma is 0.57735 * radius + 0.5 px.
 * Returns the radius that gives [sigmaDp]; a CSS `filter: blur(R)` has sigma R, a box-shadow blur B has sigma B / 2.
 */
private fun shadowRadiusDp(sigmaDp: Float, density: Float): Float =
    if (sigmaDp <= 0f) 0f else ((sigmaDp * density - 0.5f) / 0.57735f).coerceAtLeast(0f) / density

private class OvalPaint(val paint: Paint, val rect: RectF)

private fun ovalPaint(spec: OvalSpec, w: Float, h: Float, density: Float): OvalPaint {
    val left = spec.insetFraction * w * density
    val right = (w - spec.insetFraction * w) * density
    val bottom = (h + spec.bottom) * density
    val top = bottom - spec.height * density
    val rect = RectF(left, top, right, bottom)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val sigma = spec.blur * density
    if (sigma > 0f) paint.maskFilter = BlurMaskFilter(((sigma - 0.5f) / 0.57735f).coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL)
    if (spec.fades) {
        val rx = rect.width() / 2f
        val ry = rect.height() / 2f
        val gradient = RadialGradient(
            rect.centerX(), rect.centerY(), rx,
            intArrayOf(spec.color.toArgb(), spec.color.transparent.toArgb()),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP,
        )
        gradient.setLocalMatrix(android.graphics.Matrix().apply { setScale(1f, ry / rx, rect.centerX(), rect.centerY()) })
        paint.shader = gradient
    } else {
        paint.color = spec.color.toArgb()
    }
    return OvalPaint(paint, rect)
}

private fun DrawScope.drawOval(oval: OvalPaint) {
    drawContext.canvas.nativeCanvas.drawOval(oval.rect, oval.paint)
}

private fun Modifier.glassInner(
    finish: GlassFinish,
    context: GlassContext,
    shape: SeaGlassShape,
    layers: GlassLayers,
    clock: GlassClock,
    motion: Boolean,
    grain: ShaderBrush,
): Modifier = drawWithCache {
    val density = this.density
    val w = size.width / density
    val h = size.height / density
    val spec = GlassRecipes.layers(finish, context, w, h)

    val lights = if (layers.light) {
        spec.light.map { radial ->
            val rx = radial.rx * size.width
            LightLayer(
                center = Offset(radial.cx * size.width, radial.cy * size.height),
                radius = rx,
                squash = (radial.ry * size.height) / rx,
                brush = Brush.radialGradient(
                    colorStops = arrayOf(0f to radial.color, radial.endStop to radial.color.transparent),
                    center = Offset.Zero,
                    radius = rx,
                ),
            )
        }
    } else {
        emptyList()
    }

    val insets = if (layers.volume) {
        spec.insets.asReversed().map {
            InnerShadowPainter(
                shape,
                Shadow(
                    radius = shadowRadiusDp(sigmaDp = it.blur / 2f, density = density).dp,
                    color = it.color,
                    spread = it.spread.dp,
                    offset = DpOffset(it.x.dp, it.y.dp),
                ),
            )
        }
    } else {
        emptyList()
    }

    val rim = spec.rim.takeIf { layers.rim }
    val rimBrush = rim?.let { rimBrush(it, size.width, size.height) }
    val rimOutline = rim?.let { Outline.Rounded(shape.roundRect(size, density).inset(0.75f * density)) }
    val rimStroke = Stroke(width = SeaGlassTokens.Rim.Width.toPx())

    val grainAlpha = if (layers.grain) spec.grain else 0f

    val staticLayer = obtainGraphicsLayer()
    staticLayer.record(this, layoutDirection, IntSize(size.width.roundToInt(), size.height.roundToInt())) {
        if (grainAlpha > 0f) drawRect(grain, alpha = grainAlpha)
        for (inset in insets) with(inset) { draw(size) }
        if (rimBrush != null && rimOutline != null) drawOutline(rimOutline, rimBrush, style = rimStroke)
    }

    onDrawBehind {
        if (lights.isNotEmpty()) {
            val t = if (motion) clock.seconds else 0f
            val (dx, dy) = driftOffset(driftProgress(t, spec.driftOffsetSeconds), size.width, size.height)
            withTransform({ translate(dx, dy) }) {
                for (light in lights) {
                    withTransform({
                        translate(light.center.x, light.center.y)
                        scale(1f, light.squash, pivot = Offset.Zero)
                    }) {
                        drawCircle(light.brush, radius = light.radius, center = Offset.Zero)
                    }
                }
            }
        }
        drawLayer(staticLayer)
    }
}

private class LightLayer(val center: Offset, val radius: Float, val squash: Float, val brush: Brush)

private fun rimBrush(rim: RimSpec, width: Float, height: Float): Brush {
    val stops = rim.stops.toTypedArray()
    if (rim.angle == 180f) return Brush.verticalGradient(colorStops = stops, startY = 0f, endY = height)
    val radians = Math.toRadians(rim.angle.toDouble())
    val dx = sin(radians).toFloat()
    val dy = -cos(radians).toFloat()
    val half = (abs(width * dx) + abs(height * dy)) / 2f
    val center = Offset(width / 2f, height / 2f)
    return Brush.linearGradient(
        colorStops = stops,
        start = Offset(center.x - dx * half, center.y - dy * half),
        end = Offset(center.x + dx * half, center.y + dy * half),
    )
}

private fun RoundRect.inset(d: Float): RoundRect {
    fun shrink(r: CornerRadius) = CornerRadius((r.x - d).coerceAtLeast(0f), (r.y - d).coerceAtLeast(0f))
    return RoundRect(
        left = left + d, top = top + d, right = right - d, bottom = bottom - d,
        topLeftCornerRadius = shrink(topLeftCornerRadius),
        topRightCornerRadius = shrink(topRightCornerRadius),
        bottomRightCornerRadius = shrink(bottomRightCornerRadius),
        bottomLeftCornerRadius = shrink(bottomLeftCornerRadius),
    )
}
