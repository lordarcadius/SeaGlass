package com.floydwiz.seaglass.components.selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.disabledContentColor
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SegmentedOption(val label: String? = null, val icon: String? = null)

/**
 * Options in one shared well; the chosen one is a frost lens.
 * - Pressing or dragging turns the lens clear, lets it bulge 7 dp past each side and 3 dp past the well, and enlarges
 *   the label under it; on release it settles on the nearest segment on spatial.default.
 * - Left and right arrow keys move the selection.
 */
@Composable
fun GlassSegmentedControl(
    options: List<SegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    segmentWidth: Dp = 80.dp,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
    hoveredIndex: Int? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val density = LocalDensity.current.density
    val segmentPx = segmentWidth.value * density
    val insetPx = 4f * density
    val count = options.size
    val scope = rememberCoroutineScope()
    val forcedPress = LocalForcedGlassState.current == GlassState.Press
    val lensLeft = remember { Animatable(insetPx + selectedIndex * segmentPx) }
    var interacting by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex, segmentPx, interacting) {
        if (!interacting) lensLeft.animateTo(insetPx + selectedIndex * segmentPx, SeaGlassTokens.Motion.spatialDefault())
    }
    val pressing = enabled && (interacting || forcedPress)
    val pressAmount by animateFloatAsState(if (pressing) 1f else 0f, SeaGlassTokens.Motion.spatialFast(), label = "segmentLens")
    val hot = ((lensLeft.value - insetPx) / segmentPx).roundToInt().coerceIn(0, count - 1)

    val dark = LocalSeaGlassDark.current
    Box(
        modifier
            .size(segmentWidth * count + 8.dp, 40.dp)
            .semantics { this.role = Role.RadioButton }
            .pointerInput(count, segmentPx, enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val minLeft = insetPx
                    val maxLeft = insetPx + (count - 1) * segmentPx
                    fun target(x: Float) = (x - segmentPx / 2f).coerceIn(minLeft, maxLeft)
                    fun indexAt(x: Float) = ((target(x) - insetPx) / segmentPx).roundToInt().coerceIn(0, count - 1)
                    var lastX = down.position.x
                    trackAxisDrag(
                        down, horizontal = true,
                        onPress = { interacting = true; scope.launch { lensLeft.snapTo(target(it.x)) } },
                        onMove = { lastX = it.x; scope.launch { lensLeft.snapTo(target(it.x)) } },
                        onTap = { interacting = false; onSelect(indexAt(it.x)) },
                        onCancel = { interacting = false },
                        onEnd = { interacting = false; onSelect(indexAt(lastX)) },
                    )
                }
            }
            .focusable(enabled, source)
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (event.key) {
                    Key.DirectionLeft -> { onSelect((selectedIndex - 1).coerceAtLeast(0)); true }
                    Key.DirectionRight -> { onSelect((selectedIndex + 1).coerceAtMost(count - 1)); true }
                    else -> false
                }
            },
    ) {
        GlassSurface(
            finish = GlassFinish.Well,
            modifier = Modifier.size(segmentWidth * count + 8.dp, 40.dp),
            shape = SeaGlassShape.Pane(20.dp),
            role = role,
            enabled = enabled,
            interactionSource = source,
            reactsTo = setOf(GlassState.Focus),
        )
        CompositionLocalProvider(LocalForcedGlassState provides null) {
            GlassSurface(
                finish = GlassFinish.Frost,
                modifier = Modifier
                    .offset { IntOffset(lensLeft.value.roundToInt(), 4.dp.roundToPx()) }
                    .size(segmentWidth, 32.dp)
                    .graphicsLayer { alpha = 1f - pressAmount },
                shape = SeaGlassShape.Pane(16.dp),
                role = role,
                enabled = enabled,
                reactsTo = emptySet(),
            )
            if (pressAmount > 0.01f) {
                GlassSurface(
                    finish = GlassFinish.Lens,
                    modifier = Modifier
                        .offset { IntOffset((lensLeft.value - 7.dp.toPx()).roundToInt(), (-3).dp.roundToPx()) }
                        .size(segmentWidth + 14.dp, 46.dp)
                        .graphicsLayer { alpha = pressAmount },
                    shape = SeaGlassShape.Pane(23.dp),
                    role = role,
                    reactsTo = emptySet(),
                )
            }
        }
        Row(Modifier.padding(4.dp)) {
            options.forEachIndexed { index, option ->
                val chosen = if (pressing) index == hot else index == selectedIndex
                val onLens = chosen && !pressing && enabled
                val big = pressing && index == hot
                val color = when {
                    !enabled -> disabledContentColor(dark)
                    onLens -> glassOnRole(role)
                    chosen -> if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
                    else -> glassOnSurfaceVariant()
                }
                val itemSource = remember { MutableInteractionSource() }
                Box(
                    Modifier.size(segmentWidth, 32.dp).hoverable(itemSource).semantics { selected = index == selectedIndex },
                    contentAlignment = Alignment.Center,
                ) {
                    CompositionLocalProvider(LocalForcedGlassState provides (if (hoveredIndex == index) GlassState.Hover else null)) {
                    GlassSurface(
                        finish = GlassFinish.Plain,
                        modifier = Modifier.size(segmentWidth, 32.dp),
                        shape = SeaGlassShape.Pane(16.dp),
                        enabled = enabled,
                        interactionSource = itemSource,
                        reactsTo = setOf(GlassState.Hover),
                        contentColor = color,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (option.icon != null) SymbolIcon(option.icon, size = if (big) 21.dp else 18.dp, fill = onLens, tint = color)
                            if (option.label != null) {
                                Text(option.label, color = color, fontSize = if (big) 15.5.sp else 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            }
                        }
                    }
                    }
                }
            }
        }
    }
}
