package com.floydwiz.seaglass.material

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.shadow.DropShadowPainter
import androidx.compose.ui.graphics.shadow.InnerShadowPainter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassContrast
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalReduceTransparency
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class GlassState { Rest, Hover, Focus, Press, Disabled }

val AllInteractiveStates: Set<GlassState> = setOf(GlassState.Hover, GlassState.Focus, GlassState.Press)

/** Forces the state of every glass element below it; used by the catalogue state picker. */
val LocalForcedGlassState = compositionLocalOf<GlassState?> { null }

/**
 * Resolved interaction state of one glass element.
 * - [finish] is the finish actually drawn: disabled becomes Dry, a pressed Plain becomes Clear.
 * - hover and press move the element and its shadow on spatial.fast; the light they add, and focus, fade on
 *   effects.fast so they never overshoot. All are animated fractions read only in draw/layer lambdas.
 */
@Stable
class GlassInteraction internal constructor(
    val finish: GlassFinish,
    val disabled: Boolean,
    internal val hover: State<Float>,
    internal val press: State<Float>,
    internal val hoverLight: State<Float>,
    internal val pressLight: State<Float>,
    internal val focus: State<Float>,
    internal val pressScale: Float,
) {
    val hoverFraction: Float get() = hover.value
    val pressFraction: Float get() = press.value
    val focusFraction: Float get() = focus.value
}

/**
 * [reactsTo] limits which states this element shows; a switch track takes focus and hover but not press.
 * [pressScale] replaces the 0.96 press scale, for example 1.2 for a cluster item that bulges past its bar.
 */
@Composable
fun rememberGlassInteraction(
    finish: GlassFinish,
    enabled: Boolean = true,
    interactionSource: InteractionSource? = null,
    reactsTo: Set<GlassState> = AllInteractiveStates,
    pressScale: Float = SeaGlassTokens.State.Press.Scale,
): GlassInteraction {
    val forced = LocalForcedGlassState.current
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val realHover = source.collectIsHoveredAsState().value
    val realPress = source.collectIsPressedAsState().value
    val realFocus = source.collectIsFocusedAsState().value

    val disabled = !enabled || forced == GlassState.Disabled
    val pressed = GlassState.Press in reactsTo && !disabled && (forced == GlassState.Press || (forced == null && realPress))
    val hovered = GlassState.Hover in reactsTo && !disabled && (forced == GlassState.Hover || (forced == null && realHover))
    val focused = GlassState.Focus in reactsTo && !disabled && (forced == GlassState.Focus || (forced == null && realFocus))

    val drawn = when {
        disabled && finish != GlassFinish.Plain && finish != GlassFinish.Dry -> GlassFinish.Dry
        pressed && finish == GlassFinish.Plain -> GlassFinish.Clear
        else -> finish
    }

    val motion = LocalMotionEnabled.current
    val spatial = if (motion) SeaGlassTokens.Motion.SpatialFast else snap()
    val effects = if (motion) SeaGlassTokens.Motion.EffectsFast else snap()
    val hover = animateFloatAsState(if (hovered) 1f else 0f, spatial, label = "glassHover")
    val press = animateFloatAsState(if (pressed) 1f else 0f, spatial, label = "glassPress")
    val hoverLight = animateFloatAsState(if (hovered) 1f else 0f, effects, label = "glassHoverLight")
    val pressLight = animateFloatAsState(if (pressed) 1f else 0f, effects, label = "glassPressLight")
    val focus = animateFloatAsState(if (focused) 1f else 0f, effects, label = "glassFocus")
    return remember(drawn, disabled, pressScale) { GlassInteraction(drawn, disabled, hover, press, hoverLight, pressLight, focus, pressScale) }
}

internal enum class Stages { All, Frame, Body, Inner }

/**
 * Draws one finish of the glass stack on this element, back to front:
 * - behind the element: focus halo, tinted shadow, caustic, contact shadow;
 * - clipped to [shape]: backdrop blur + tint, light pool, grain, volume shadows, rim, state light.
 * Content placed in the element draws above all of it. Each layer obeys [LocalGlassLayers].
 */
@Composable
fun Modifier.seaGlass(
    finish: GlassFinish,
    shape: SeaGlassShape = SeaGlassShape.Capsule,
    role: TonalTones = LocalGlassRole.current,
    tinted: Boolean = false,
    adaptive: Boolean = false,
    enabled: Boolean = true,
    interactionSource: InteractionSource? = null,
): Modifier {
    val interaction = rememberGlassInteraction(finish, enabled, interactionSource)
    val dark = LocalSeaGlassDark.current
    val content = if (interaction.disabled) disabledContentColor(dark) else glassContentColor(interaction.finish, role, dark)
    return seaGlass(interaction, shape, role, tinted, if (adaptive) 1f else 0f, content, Stages.All)
}

@Composable
internal fun Modifier.seaGlass(
    interaction: GlassInteraction,
    shape: SeaGlassShape,
    role: TonalTones,
    tinted: Boolean,
    adaptive: Float,
    contentColor: Color,
    stages: Stages,
    bodyTransform: Modifier = Modifier,
): Modifier {
    val dark = LocalSeaGlassDark.current
    val contrast = LocalGlassContrast.current
    val reduceTransparency = LocalReduceTransparency.current
    val layers = LocalGlassLayers.current
    val hazeState = LocalHazeState.current
    val clock = LocalGlassClock.current
    val motion = LocalMotionEnabled.current
    val grain = rememberGrainBrush()

    val finish = interaction.finish
    val overrides = LocalGlassOverrides.current
    val context = remember(dark, contrast, reduceTransparency, role, adaptive, tinted, overrides) {
        GlassContext(dark, contrast, reduceTransparency, role, adaptive, tinted, overrides)
    }
    val body = remember(finish, context) { GlassRecipes.body(finish, context) }
    val hazeStyle = remember(body) { (body as? Body.Backdrop)?.toHazeStyle() }

    var chain: Modifier = this
    if (stages == Stages.All || stages == Stages.Frame) {
        chain = chain
            .glassTransform(interaction)
            .glassHalo(context, shape, interaction)
            .glassBehind(finish, context, shape, layers, interaction)
    }
    if (stages == Stages.All || stages == Stages.Body) {
        chain = chain.clip(shape).then(bodyTransform).glassBody(body, hazeStyle, hazeState, layers.body)
    }
    if (stages == Stages.All || stages == Stages.Inner) {
        chain = chain.clip(shape).glassInner(finish, context, shape, layers, clock, motion, grain, interaction, contentColor)
    }
    return chain
}

private fun Modifier.glassTransform(interaction: GlassInteraction): Modifier = graphicsLayer {
    val scale = 1f + (interaction.pressScale - 1f) * interaction.press.value
    scaleX = scale
    scaleY = scale
    if (interaction.finish.isRaised) {
        translationY = -SeaGlassTokens.State.Hover.Lift.toPx() * interaction.hover.value
    }
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

internal fun Modifier.glassBody(
    body: Body,
    hazeStyle: HazeBlurStyle?,
    hazeState: HazeState?,
    enabled: Boolean,
): Modifier = when {
    !enabled -> this
    body is Body.None -> this
    body is Body.Solid -> background(body.color)
    body is Body.Radial -> drawWithCache {
        val layer = radialLayer(body.cx, body.cy, body.rx, body.ry, body.stops, size.width, size.height)
        onDrawBehind { drawRadial(layer) }
    }
    body is Body.Backdrop && hazeState != null && hazeStyle != null ->
        hazeBlur(input = HazeInput.Sources(hazeState), style = hazeStyle)
    body is Body.Backdrop -> drawWithCache {
        onDrawBehind { body.fills.forEach { drawRect(it) } }
    }
    else -> this
}

private class HaloShadow(val blur: Float, val spread: Float, val color: Color)

/** Focus halo from `halo()` in recipes.js, listed top-most first. */
private fun haloShadows(c: GlassContext): List<HaloShadow> {
    val p = c.role
    return when {
        c.level == 2 -> listOf(
            HaloShadow(0f, 3f, if (c.dark) p[90] else p[30]),
            HaloShadow(0f, 5f, if (c.dark) Color.Black else Color.White),
            HaloShadow(18f, 4f, p[60].alpha(0.45f)),
        )
        c.dark -> listOf(
            HaloShadow(0f, 1.5f, p[80].alpha(0.95f)),
            HaloShadow(14f, 3f, p[60].alpha(0.6f)),
            HaloShadow(34f, 10f, p[60].alpha(0.25f)),
        )
        else -> listOf(
            HaloShadow(0f, 1.5f, p[60].alpha(0.85f)),
            HaloShadow(14f, 3f, p[60].alpha(0.42f)),
            HaloShadow(34f, 10f, p[70].alpha(0.22f)),
        )
    }
}

private fun Modifier.glassHalo(context: GlassContext, shape: SeaGlassShape, interaction: GlassInteraction): Modifier =
    drawWithCache {
        val density = this.density
        val path = Path().apply { addRoundRect(shape.roundRect(size, density)) }
        val layer = obtainGraphicsLayer()
        var recorded = false
        onDrawBehind {
            val focus = interaction.focus.value
            if (focus <= 0f) return@onDrawBehind
            if (!recorded) {
                val painters = haloShadows(context).asReversed().map {
                    DropShadowPainter(
                        shape,
                        Shadow(radius = shadowRadiusDp(it.blur / 2f, density).dp, color = it.color, spread = it.spread.dp),
                    )
                }
                layer.record(this, layoutDirection, IntSize(size.width.roundToInt(), size.height.roundToInt())) {
                    for (painter in painters) with(painter) { draw(size) }
                }
                recorded = true
            }
            layer.alpha = focus
            clipPath(path, ClipOp.Difference) { drawLayer(layer) }
        }
    }

private fun Modifier.glassBehind(
    finish: GlassFinish,
    context: GlassContext,
    shape: SeaGlassShape,
    layers: GlassLayers,
    interaction: GlassInteraction,
): Modifier = drawWithCache {
    val density = this.density
    val w = size.width / density
    val h = size.height / density
    val spec = GlassRecipes.layers(finish, context, w, h)
    val intSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
    val raised = finish.isRaised

    val shadowSpec = spec.shadow.takeIf { layers.shadow }
    val shadowPainter = shadowSpec?.let {
        DropShadowPainter(shape, Shadow(radius = shadowRadiusDp(sigmaDp = it.blur, density = density).dp, color = it.color))
    }
    val caustic = spec.caustic.takeIf { layers.caustic }?.let { ovalPaint(it, w, h, density) }
    val contact = spec.contact.takeIf { layers.contact }?.let { ovalPaint(it, w, h, density) }

    val shadowLayer = obtainGraphicsLayer()
    shadowLayer.record(this, layoutDirection, intSize) {
        if (shadowSpec != null && shadowPainter != null) {
            withTransform({
                translate(0f, shadowSpec.offsetY * density)
                scale(shadowSpec.scaleX, shadowSpec.scaleY, pivot = Offset(size.width / 2f, size.height / 2f))
            }) { with(shadowPainter) { draw(size) } }
        }
    }
    val ovalLayer = obtainGraphicsLayer()
    ovalLayer.record(this, layoutDirection, intSize) {
        caustic?.let { drawOval(it) }
        contact?.let { drawOval(it) }
    }

    onDrawBehind {
        if (raised) {
            val hover = interaction.hover.value
            val press = interaction.press.value
            val dy = SeaGlassTokens.State.Hover.ShadowDrop.toPx() * hover - SeaGlassTokens.State.Press.ShadowRise.toPx() * press
            val scale = 1f + (SeaGlassTokens.State.Hover.ShadowScale - 1f) * hover - (1f - SeaGlassTokens.State.Press.ShadowScale) * press
            withTransform({
                translate(0f, dy)
                scale(scale, scale, pivot = Offset(size.width / 2f, size.height / 2f))
            }) { drawLayer(shadowLayer) }
        } else {
            drawLayer(shadowLayer)
        }
        drawLayer(ovalLayer)
    }
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
    interaction: GlassInteraction,
    contentColor: Color,
): Modifier = drawWithCache {
    val density = this.density
    val w = size.width / density
    val h = size.height / density
    val spec = GlassRecipes.layers(finish, context, w, h)
    val raised = finish.isRaised
    val dark = context.dark

    val lights = if (layers.light) {
        spec.light.map { radialLayer(it.cx, it.cy, it.rx, it.ry, listOf(0f to it.color, it.endStop to it.color.transparent), size.width, size.height) }
    } else {
        emptyList()
    }

    val hoverLight = radialLayer(0.5f, 0f, 0.9f, 0.8f, listOf(0f to white(if (dark) 0.14f else 0.32f), 1f to white(0f)), size.width, size.height)
    val focusLight = radialLayer(0.5f, 0f, 0.9f, 0.8f, listOf(0f to white(if (dark) 0.12f else 0.26f), 1f to white(0f)), size.width, size.height)
    val pressBloom = radialLayer(0.5f, 0.55f, 0.75f, 0.95f, listOf(0f to white(if (dark) 0.22f else 0.42f), 1f to white(0f)), size.width, size.height)

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
            withTransform({ translate(dx, dy) }) { lights.forEach { drawRadial(it) } }
        }
        drawLayer(staticLayer)

        val hover = interaction.hoverLight.value
        val press = interaction.pressLight.value
        val focus = interaction.focus.value
        if (raised) {
            if (hover > 0f) drawRadial(hoverLight, hover)
            if (focus > 0f) drawRadial(focusLight, focus)
            if (press > 0f) drawRadial(pressBloom, press)
        } else {
            if (hover > 0f) drawRect(contentColor, alpha = SeaGlassTokens.State.Hover.SurfaceAlpha * hover)
            if (press > 0f) drawRect(contentColor, alpha = SeaGlassTokens.State.Press.SurfaceAlpha * press)
        }
    }
}

/** Elliptical radial gradient resolved to px: drawn with its centre at [center], squashed to [squash]. */
internal class RadialLayer(val center: Offset, val radius: Float, val squash: Float, val brush: Brush)

internal fun radialLayer(
    cx: Float, cy: Float, rx: Float, ry: Float,
    stops: List<Pair<Float, Color>>,
    width: Float, height: Float,
): RadialLayer {
    val radius = rx * width
    return RadialLayer(
        center = Offset(cx * width, cy * height),
        radius = radius,
        squash = (ry * height) / radius,
        brush = Brush.radialGradient(colorStops = stops.toTypedArray(), center = Offset.Zero, radius = radius),
    )
}

internal fun DrawScope.drawRadial(layer: RadialLayer, alpha: Float = 1f) {
    withTransform({
        translate(layer.center.x, layer.center.y)
        scale(1f, layer.squash, pivot = Offset.Zero)
    }) {
        drawCircle(layer.brush, radius = layer.radius, center = Offset.Zero, alpha = alpha)
    }
}

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
