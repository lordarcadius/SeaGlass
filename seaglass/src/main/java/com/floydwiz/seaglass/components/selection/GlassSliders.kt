package com.floydwiz.seaglass.components.selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassContentColor
import com.floydwiz.seaglass.material.white
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import kotlin.math.abs
import kotlin.math.roundToInt

private val ThumbWidth = 34.dp
private val ThumbHeight = 22.dp
private val TrackHeight = 10.dp
private val Inset = ThumbWidth / 2

/**
 * Rail slider: a 10 dp well filled with lit liquid and a pearl thumb.
 * - While dragged the thumb becomes a 54×38 clear lens with a 48×30 ink value bubble 45 dp above the track.
 * - [stepCount] intervals draw 4 dp stop dots and snap the value; 0 is continuous.
 */
@Composable
fun GlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    stepCount: Int = 0,
    role: TonalTones = LocalGlassRole.current,
    valueLabel: (Float) -> String = { (it * 100).roundToInt().toString() },
    interactionSource: MutableInteractionSource? = null,
) {
    val span = valueRange.endInclusive - valueRange.start
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)
    RailSlider(
        fractions = listOf(fraction),
        fillFrom = 0f,
        onFractionChange = { _, f -> onValueChange(valueRange.start + f * span) },
        modifier = modifier,
        enabled = enabled,
        stepCount = stepCount,
        role = role,
        labels = listOf(valueLabel(value)),
        interactionSource = interactionSource,
        semanticsValue = value,
        semanticsRange = valueRange,
    )
}

/** Rail slider with two thumbs; dragging moves the nearer one. */
@Composable
fun GlassRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    stepCount: Int = 0,
    role: TonalTones = LocalGlassRole.current,
    valueLabel: (Float) -> String = { (it * 100).roundToInt().toString() },
) {
    val span = valueRange.endInclusive - valueRange.start
    val low = ((value.start - valueRange.start) / span).coerceIn(0f, 1f)
    val high = ((value.endInclusive - valueRange.start) / span).coerceIn(0f, 1f)
    RailSlider(
        fractions = listOf(low, high),
        fillFrom = low,
        onFractionChange = { index, f ->
            val v = valueRange.start + f * span
            onValueChange(if (index == 0) v.coerceAtMost(value.endInclusive)..value.endInclusive else value.start..v.coerceAtLeast(value.start))
        },
        modifier = modifier,
        enabled = enabled,
        stepCount = stepCount,
        role = role,
        labels = listOf(valueLabel(value.start), valueLabel(value.endInclusive)),
        interactionSource = null,
        semanticsValue = value.start,
        semanticsRange = valueRange,
    )
}

@Composable
private fun RailSlider(
    fractions: List<Float>,
    fillFrom: Float,
    onFractionChange: (index: Int, fraction: Float) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    stepCount: Int,
    role: TonalTones,
    labels: List<String>,
    interactionSource: MutableInteractionSource?,
    semanticsValue: Float,
    semanticsRange: ClosedFloatingPointRange<Float>,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val forcedPress = LocalForcedGlassState.current == GlassState.Press
    var dragIndex by remember { mutableIntStateOf(-1) }
    val dragging = enabled && (dragIndex >= 0 || forcedPress)
    val lensAmount by animateFloatAsState(if (dragging) 1f else 0f, SeaGlassTokens.Motion.spatialFast(), label = "sliderLens")
    val activeThumb = if (dragIndex >= 0) dragIndex else fractions.lastIndex
    val density = LocalDensity.current.density

    BoxWithConstraints(modifier.fillMaxWidth().height(48.dp)) {
        val trackLength = maxWidth - ThumbWidth
        val trackPx = trackLength.value * density
        fun snap(f: Float): Float {
            val c = f.coerceIn(0f, 1f)
            return if (stepCount > 0) (c * stepCount).roundToInt() / stepCount.toFloat() else c
        }
        val thumbX = { f: Float -> Inset + trackLength * f }

        Box(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(semanticsValue, semanticsRange)
                    setProgress { target ->
                        val span = semanticsRange.endInclusive - semanticsRange.start
                        onFractionChange(0, snap((target - semanticsRange.start) / span))
                        true
                    }
                }
                .pointerInput(fractions.size, trackPx, enabled, stepCount) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val inset = Inset.toPx()
                        fun fractionAt(x: Float) = snap((x - inset) / trackPx)
                        var index = 0
                        trackAxisDrag(
                            down, horizontal = true,
                            onPress = { p ->
                                val first = fractionAt(p.x)
                                index = fractions.indices.minByOrNull { abs(fractions[it] - first) } ?: 0
                                dragIndex = index
                            },
                            onMove = { onFractionChange(index, fractionAt(it.x)) },
                            onTap = { onFractionChange(index, fractionAt(it.x)); dragIndex = -1 },
                            onCancel = { dragIndex = -1 },
                            onEnd = { dragIndex = -1 },
                        )
                    }
                }
                .focusable(enabled, source)
                .onKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                    val step = if (stepCount > 0) 1f / stepCount else 0.05f
                    when (event.key) {
                        Key.DirectionLeft -> { onFractionChange(fractions.lastIndex, snap(fractions.last() - step)); true }
                        Key.DirectionRight -> { onFractionChange(fractions.lastIndex, snap(fractions.last() + step)); true }
                        else -> false
                    }
                },
            contentAlignment = Alignment.CenterStart,
        ) {
            val high = fractions.last()
            GlassSurface(
                finish = GlassFinish.Well,
                modifier = Modifier.offset(Inset, 0.dp).size(trackLength, TrackHeight),
                shape = SeaGlassShape.Pane(5.dp),
                role = role,
                enabled = enabled,
                reactsTo = emptySet(),
            )
            val fillWidth = maxOf(TrackHeight, trackLength * (high - fillFrom))
            CompositionLocalProvider(LocalForcedGlassState provides null) {
                GlassSurface(
                    finish = GlassFinish.Fill,
                    modifier = Modifier
                        .offset(Inset + trackLength * fillFrom, 0.dp)
                        .size(fillWidth, TrackHeight)
                        .graphicsLayer { alpha = if (enabled) 1f else SeaGlassTokens.State.Disabled.ContentAlpha },
                    shape = SeaGlassShape.Pane(5.dp),
                    role = role,
                    reactsTo = emptySet(),
                )
            }
            if (stepCount > 0) {
                val dark = LocalSeaGlassDark.current
                for (i in 0..stepCount) {
                    val stop = i / stepCount.toFloat()
                    val x = Inset + trackLength * stop + if (i == 0) 3.dp else if (i == stepCount) (-3).dp else 0.dp
                    val on = stop in fillFrom..(high + 0.001f)
                    Box(
                        Modifier
                            .offset(x - 2.dp, 0.dp)
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (on) white(0.9f)
                                else (if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight).copy(alpha = 0.3f),
                            ),
                    )
                }
            }
            fractions.forEachIndexed { index, f ->
                val isLensThumb = index == activeThumb
                GlassSurface(
                    finish = GlassFinish.Pearl,
                    modifier = Modifier
                        .offset(thumbX(f) - ThumbWidth / 2, 0.dp)
                        .size(ThumbWidth, ThumbHeight)
                        .graphicsLayer { alpha = if (isLensThumb) 1f - lensAmount else 1f },
                    shape = SeaGlassShape.Pane(11.dp),
                    role = role,
                    enabled = enabled,
                    interactionSource = if (index == 0) source else null,
                    reactsTo = if (index == 0) setOf(GlassState.Hover, GlassState.Focus) else setOf(GlassState.Hover),
                )
            }
            if (lensAmount > 0.01f) {
                val f = fractions[activeThumb]
                CompositionLocalProvider(LocalForcedGlassState provides null) {
                    GlassSurface(
                        finish = GlassFinish.Lens,
                        modifier = Modifier
                            .offset(thumbX(f) - 27.dp, 0.dp)
                            .size(54.dp, 38.dp)
                            .graphicsLayer { alpha = lensAmount },
                        shape = SeaGlassShape.Pane(19.dp),
                        role = role,
                        reactsTo = emptySet(),
                    )
                    GlassSurface(
                        finish = GlassFinish.Ink,
                        modifier = Modifier
                            .offset(thumbX(f) - 24.dp, (-45).dp)
                            .size(48.dp, 30.dp)
                            .graphicsLayer { alpha = lensAmount },
                        shape = SeaGlassShape.Pane(15.dp),
                        role = role,
                        reactsTo = emptySet(),
                    ) {
                        Text(labels[activeThumb], color = LocalContentColor.current, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/**
 * Pill slider with the symbol inside the liquid, for volume and brightness.
 * - Horizontal 300×48 (radius 24) with a percentage on the right; vertical 64×168 (radius 28).
 * - The liquid grows 2% while dragged.
 */
@Composable
fun GlassPillSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: String,
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val width = if (vertical) 64.dp else 300.dp
    val height = if (vertical) 168.dp else 48.dp
    val radius = if (vertical) 28.dp else 24.dp
    val forcedPress = LocalForcedGlassState.current == GlassState.Press
    var dragging by remember { mutableStateOf(false) }
    val grow by animateFloatAsState(if (enabled && (dragging || forcedPress)) 1.02f else 1f, SeaGlassTokens.Motion.spatialFast(), label = "pillGrow")
    val dark = LocalSeaGlassDark.current
    val onFill = glassContentColor(GlassFinish.Fill, role, dark)
    val fraction = value.coerceIn(0f, 1f)

    Box(
        modifier
            .size(width, height)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f); setProgress { onValueChange(it.coerceIn(0f, 1f)); true } }
            .pointerInput(vertical, enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    fun at(p: androidx.compose.ui.geometry.Offset) =
                        (if (vertical) 1f - p.y / size.height else p.x / size.width).coerceIn(0f, 1f)
                    trackAxisDrag(
                        down, horizontal = !vertical,
                        onPress = { dragging = true },
                        onMove = { onValueChange(at(it)) },
                        onTap = { onValueChange(at(it)); dragging = false },
                        onCancel = { dragging = false },
                        onEnd = { dragging = false },
                    )
                }
            }
            .focusable(enabled, source)
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (event.key) {
                    Key.DirectionLeft, Key.DirectionDown -> { onValueChange((fraction - 0.05f).coerceAtLeast(0f)); true }
                    Key.DirectionRight, Key.DirectionUp -> { onValueChange((fraction + 0.05f).coerceAtMost(1f)); true }
                    else -> false
                }
            },
    ) {
        GlassSurface(
            finish = GlassFinish.Well,
            modifier = Modifier.size(width, height),
            shape = SeaGlassShape.Pane(radius),
            role = role,
            enabled = enabled,
            interactionSource = source,
            reactsTo = setOf(GlassState.Focus),
        )
        CompositionLocalProvider(LocalForcedGlassState provides null) {
            if (vertical) {
                val fillHeight = height * fraction
                GlassSurface(
                    finish = GlassFinish.Fill,
                    modifier = Modifier
                        .offset(0.dp, height - fillHeight)
                        .size(width, fillHeight)
                        .graphicsLayer { scaleX = grow; scaleY = 1f }
                        .graphicsLayer { alpha = if (enabled) 1f else SeaGlassTokens.State.Disabled.ContentAlpha },
                    shape = SeaGlassShape.Pane(radius),
                    role = role,
                    reactsTo = emptySet(),
                )
                SymbolIcon(icon, Modifier.offset(19.dp, height - 42.dp), size = 26.dp, fill = true, tint = onFill)
            } else {
                val fillWidth = width * fraction
                GlassSurface(
                    finish = GlassFinish.Fill,
                    modifier = Modifier
                        .size(fillWidth, height)
                        .graphicsLayer { scaleY = grow; scaleX = 1f }
                        .graphicsLayer { alpha = if (enabled) 1f else SeaGlassTokens.State.Disabled.ContentAlpha },
                    shape = SeaGlassShape.Pane(radius),
                    role = role,
                    reactsTo = emptySet(),
                )
                SymbolIcon(icon, Modifier.offset(16.dp, 13.dp), size = 22.dp, fill = true, tint = onFill)
                Text(
                    "${(fraction * 100).roundToInt()}%",
                    Modifier.offset(width - 76.dp, 15.dp).size(60.dp, 20.dp),
                    color = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            }
        }
    }
}
