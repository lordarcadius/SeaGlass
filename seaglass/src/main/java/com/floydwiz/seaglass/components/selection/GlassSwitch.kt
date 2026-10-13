package com.floydwiz.seaglass.components.selection

import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole

private val TrackWidth = 58.dp
private val TrackHeight = 34.dp
private val KnobSize = 28.dp

/**
 * A sunken well holds a pearl; turning on pours lit liquid into the well.
 * - While pressed the pearl swells into a 44×38 clear lens that overflows the track by 2 dp and magnifies the liquid.
 * - The knob and lens slide on spatial.fast; dragging the knob past either end toggles.
 * - Focus draws the halo on the track.
 */
@Composable
fun GlassSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val forced = LocalForcedGlassState.current
    val pressedNow by source.collectIsPressedAsState()
    val pressed = enabled && (pressedNow || forced == GlassState.Press)
    val pressAmount by animateFloatAsState(if (pressed) 1f else 0f, SeaGlassTokens.Motion.spatialFast(), label = "switchLens")
    val knobX by animateDpAsState(if (checked) 27.dp else 3.dp, SeaGlassTokens.Motion.spatialFast(), label = "switchKnob")
    val pour by animateFloatAsState(if (checked) 1f else 0f, SeaGlassTokens.Motion.effectsDefault(), label = "switchLiquid")

    val toggle = if (onCheckedChange != null) {
        Modifier
            .toggleable(checked, source, null, enabled, Role.Switch, onCheckedChange)
            .pointerInput(checked, enabled) {
                var total = 0f
                detectHorizontalDragGestures(
                    onDragStart = { total = 0f },
                    onDragEnd = {
                        if (enabled && total > 0f && !checked) onCheckedChange(true)
                        if (enabled && total < 0f && checked) onCheckedChange(false)
                    },
                ) { change, delta ->
                    total += delta
                    change.consume()
                }
            }
    } else {
        Modifier
    }

    Box(modifier.then(toggle).minimumInteractiveComponentSize(), contentAlignment = Alignment.Center) {
        Box(Modifier.size(TrackWidth, TrackHeight)) {
            GlassSurface(
                finish = GlassFinish.Well,
                modifier = Modifier.size(TrackWidth, TrackHeight),
                shape = SeaGlassShape.Pane(17.dp),
                role = role,
                enabled = enabled,
                interactionSource = source,
                reactsTo = setOf(GlassState.Hover, GlassState.Focus),
            )
            CompositionLocalProvider(LocalForcedGlassState provides null) {
                GlassSurface(
                    finish = GlassFinish.Fill,
                    modifier = Modifier
                        .offset(2.dp, 2.dp)
                        .size(width = 30.dp + 24.dp * pour, height = 30.dp)
                        .graphicsLayer { alpha = pour * (if (enabled && forced != GlassState.Disabled) 1f else SeaGlassTokens.State.Disabled.ContentAlpha) },
                    shape = SeaGlassShape.Pane(15.dp),
                    role = role,
                )
            }
            GlassSurface(
                finish = GlassFinish.Pearl,
                modifier = Modifier
                    .offset(knobX, 3.dp)
                    .size(KnobSize)
                    .graphicsLayer { alpha = 1f - pressAmount.coerceIn(0f, 1f) },
                shape = SeaGlassShape.Round,
                role = role,
                enabled = enabled,
                interactionSource = source,
                reactsTo = setOf(GlassState.Hover),
            )
            if (pressAmount > 0.01f) {
                val lensX = (knobX + 14.dp - 22.dp).coerceIn((-2).dp, 16.dp)
                CompositionLocalProvider(LocalForcedGlassState provides null) {
                    GlassSurface(
                        finish = GlassFinish.Lens,
                        modifier = Modifier
                            .offset(lensX, (-2).dp)
                            .size(44.dp, 38.dp)
                            .graphicsLayer { alpha = pressAmount.coerceIn(0f, 1f) },
                        shape = SeaGlassShape.Pane(19.dp),
                        role = role,
                        reactsTo = emptySet(),
                    )
                }
            }
        }
    }
}
