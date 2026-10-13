package com.floydwiz.seaglass.components.feedback

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalMotionEnabled
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassContentColor
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

private val SweepEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

/**
 * Linear progress: lit liquid poured into a well, 8 dp tall by default.
 * - [progress] null is indeterminate: a liquid segment sweeps across the well every 1.8 s.
 */
@Composable
fun GlassLinearProgress(
    progress: Float?,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    role: TonalTones = LocalGlassRole.current,
) {
    val motion = LocalMotionEnabled.current
    val transition = rememberInfiniteTransition(label = "linearProgress")
    val left by transition.animateFloat(
        0f, 1f,
        infiniteRepeatable(keyframes { durationMillis = SeaGlassTokens.Motion.millis(1800); 0f at 0 using SweepEasing; 0.08f at SeaGlassTokens.Motion.millis(810) using SweepEasing; 1f at SeaGlassTokens.Motion.millis(1800) }), label = "left",
    )
    val right by transition.animateFloat(
        0f, 1f,
        infiniteRepeatable(keyframes { durationMillis = SeaGlassTokens.Motion.millis(1800); 0f at 0 using SweepEasing; 0.7f at SeaGlassTokens.Motion.millis(810) using SweepEasing; 1f at SeaGlassTokens.Motion.millis(1800) }), label = "right",
    )
    val semantics = Modifier.semantics { if (progress != null) progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f) }
    BoxWithConstraints(modifier.fillMaxWidth().height(height).then(semantics)) {
        GlassSurface(GlassFinish.Well, Modifier.size(maxWidth, height), SeaGlassShape.Pane(height / 2), role, reactsTo = emptySet())
        if (progress != null) {
            GlassSurface(
                GlassFinish.Fill,
                Modifier.size(maxOf(height, maxWidth * progress.coerceIn(0f, 1f)), height),
                SeaGlassShape.Pane(height / 2), role, reactsTo = emptySet(),
            )
        } else {
            val (from, to) = if (motion) left to right else 0.3f to 0.7f
            GlassSurface(
                GlassFinish.Fill,
                Modifier.size(maxWidth, height).drawWithContent {
                    val path = Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                Rect(Offset(size.width * from, 0f), Size(size.width * (to - from).coerceAtLeast(0f), size.height)),
                                CornerRadius(size.height / 2),
                            ),
                        )
                    }
                    clipPath(path) { this@drawWithContent.drawContent() }
                },
                SeaGlassShape.Pane(height / 2), role, reactsTo = emptySet(),
            )
        }
    }
}

/**
 * Liquid progress bar, 30 dp tall, carrying a label and a value.
 * - The label sits on the liquid in white-on-fill when the liquid is long enough to hold it, otherwise it starts just
 *   past the liquid in ink; the value sits on the right, on the liquid only when the liquid reaches it.
 */
@Composable
fun GlassLiquidProgress(
    progress: Float,
    label: String,
    valueLabel: String,
    modifier: Modifier = Modifier,
    height: Dp = 30.dp,
    role: TonalTones = LocalGlassRole.current,
) {
    val dark = LocalSeaGlassDark.current
    val onFill = glassContentColor(GlassFinish.Fill, role, dark)
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    val style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight(650))
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    BoxWithConstraints(modifier.fillMaxWidth().height(height)) {
        GlassSurface(GlassFinish.Well, Modifier.size(maxWidth, height), SeaGlassShape.Pane(height / 2), role, reactsTo = emptySet())
        val fill = maxOf(height, maxWidth * progress.coerceIn(0f, 1f))
        GlassSurface(GlassFinish.Fill, Modifier.size(fill, height), SeaGlassShape.Pane(height / 2), role, reactsTo = emptySet())
        val padding = 14.dp
        val labelWidth = with(density) { measurer.measure(label, style).size.width.toDp() }
        val valueWidth = with(density) { measurer.measure(valueLabel, style).size.width.toDp() }
        val labelOnLiquid = fill >= labelWidth + padding * 2
        val valueOnLiquid = fill >= maxWidth - valueWidth - padding
        Text(
            label, Modifier.align(Alignment.CenterStart).padding(start = if (labelOnLiquid) padding else fill + 10.dp),
            color = if (labelOnLiquid) onFill else ink, style = style,
        )
        Text(valueLabel, Modifier.align(Alignment.CenterEnd).padding(end = padding), color = if (valueOnLiquid) onFill else ink, style = style)
    }
}

/**
 * Ring progress: a well ring with rounded caps and a lighter leading edge.
 * - [progress] null is the comet: a glass arc fading to nothing at its tail, one turn every 1.2 s.
 */
@Composable
fun GlassRingProgress(
    progress: Float?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    stroke: Dp = 7.dp,
    role: TonalTones = LocalGlassRole.current,
    content: @Composable () -> Unit = {},
) {
    val dark = LocalSeaGlassDark.current
    val motion = LocalMotionEnabled.current
    val from = if (dark) role[70] else role[60]
    val to = if (dark) role[80] else role[70]
    val track = if (dark) SeaGlassTokens.Veil.RingTrackDark else SeaGlassTokens.Veil.RingTrackLight
    val spin by rememberInfiniteTransition(label = "ring").animateFloat(
        0f, 360f, infiniteRepeatable(tween(SeaGlassTokens.Motion.millis(1200), easing = LinearEasing)), label = "spin",
    )
    Box(modifier.size(size).semantics { if (progress != null) progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f) }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val strokePx = stroke.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft = Offset(inset, inset)
            drawArc(track, 0f, 360f, false, topLeft, arcSize, style = Stroke(strokePx))
            if (progress == null) {
                rotate(if (motion) spin else 0f, center) {
                    drawArc(
                        brush = Brush.sweepGradient(0f to from.copy(alpha = 0f), 0.45f to from.copy(alpha = 0f), 1f to to, center = center),
                        startAngle = 0f, sweepAngle = 360f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(strokePx),
                    )
                }
            } else {
                val sweep = 360f * progress.coerceIn(0f, 1f)
                rotate(-90f, center) {
                    drawArc(
                        brush = Brush.sweepGradient(0f to from, progress.coerceIn(0.001f, 1f) to to, 1f to to, center = center),
                        startAngle = 0f, sweepAngle = sweep, useCenter = false, topLeft = topLeft, size = arcSize,
                        style = Stroke(strokePx, cap = StrokeCap.Round),
                    )
                }
                if (sweep > 0f) {
                    val angle = Math.toRadians((sweep - 90f).toDouble())
                    val radius = this.size.width / 2f - inset
                    val tip = Offset(center.x + radius * Math.cos(angle).toFloat(), center.y + radius * Math.sin(angle).toFloat())
                    drawCircle(role[60].copy(alpha = 0.35f), strokePx * 0.9f, tip)
                    drawCircle(to, strokePx / 2f, tip)
                }
            }
        }
        content()
    }
}
