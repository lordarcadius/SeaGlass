package com.floydwiz.seaglass.components.feedback

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalMotionEnabled
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import kotlin.math.floor

private val MorphEasing = CubicBezierEasing(0.45f, 0f, 0.25f, 1f)
private val Silhouettes = listOf(SeaGlassShape.Lens, SeaGlassShape.Shard, SeaGlassShape.Egg, SeaGlassShape.Tumble)

/**
 * Loading indicator: a glass pebble tumbling through lens, shard, egg and tumble silhouettes on a 2.4 s alternate loop.
 * - [contained] places a smaller pebble inside a 72 dp clear lens for busy surfaces.
 * - Reduced motion holds the lens shape and pulses opacity instead.
 */
@Composable
fun GlassLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    contained: Boolean = false,
    role: TonalTones = LocalGlassRole.current,
) {
    val motion = LocalMotionEnabled.current
    val transition = rememberInfiniteTransition(label = "loading")
    val morph by transition.animateFloat(
        initialValue = 0f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(2400, easing = MorphEasing), RepeatMode.Reverse), label = "morph",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.55f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulse",
    )
    val shape = if (motion) {
        val index = floor(morph).toInt().coerceIn(0, 2)
        SeaGlassShape.lerp(Silhouettes[index], Silhouettes[index + 1], morph - index)
    } else {
        SeaGlassShape.Lens
    }
    val pebbleSize = if (contained) size * 0.68f else size
    Box(modifier.size(if (contained) size * 1.29f else size), contentAlignment = Alignment.Center) {
        if (contained) {
            GlassSurface(GlassFinish.Clear, Modifier.size(size * 1.29f), SeaGlassShape.Lens, role, reactsTo = emptySet())
        }
        GlassSurface(
            finish = GlassFinish.Glass,
            modifier = Modifier.size(pebbleSize).graphicsLayer { alpha = if (motion) 1f else pulse },
            shape = shape,
            role = role,
            reactsTo = emptySet(),
        )
    }
}

/** Three beads that bob in sequence, for inline waits. */
@Composable
fun GlassBeadsLoading(modifier: Modifier = Modifier, role: TonalTones = LocalGlassRole.current) {
    val motion = LocalMotionEnabled.current
    val transition = rememberInfiniteTransition(label = "beads")
    Row(modifier, Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
        listOf(0, 750, 400).forEach { delayMillis ->
            val lift by transition.animateFloat(
                initialValue = 0f, targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = 1100
                        0f at 0 using MorphEasing
                        -7f at 550 using MorphEasing
                        0f at 1100
                    },
                    initialStartOffset = StartOffset(delayMillis, StartOffsetType.FastForward),
                ),
                label = "bead",
            )
            GlassSurface(
                finish = GlassFinish.Bead,
                modifier = Modifier.size(12.dp).graphicsLayer { translationY = if (motion) lift.dp.toPx() else 0f },
                shape = SeaGlassShape.Round,
                role = role,
                reactsTo = emptySet(),
            )
        }
    }
}
