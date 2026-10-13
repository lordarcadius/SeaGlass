package com.floydwiz.seaglass.material

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.theme.LocalReduceTransparency
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

/**
 * Edge diffusion under a top bar: content scrolling beneath is blurred and lightened, fading to nothing over [height].
 * - [visible] fades it in on effects.default; reduce transparency draws a plain fading scrim instead.
 * - [fromBottom] mirrors it for content scrolling under a bottom bar.
 */
@Composable
fun GlassEdgeDiffusion(modifier: Modifier = Modifier, height: Dp = 84.dp, visible: Boolean = true, fromBottom: Boolean = false) {
    val dark = LocalSeaGlassDark.current
    val hazeState = LocalHazeState.current
    val reduce = LocalReduceTransparency.current
    val alpha by animateFloatAsState(if (visible) 1f else 0f, SeaGlassTokens.Motion.effectsDefault(), label = "edgeDiffusion")
    val tint = if (dark) rgba(9, 12, 14, 0.55f) else white(0.6f)
    val style = remember(dark, fromBottom) {
        HazeBlurStyle {
            blurRadius(14.dp)
            noiseFactor(0f)
            mask(if (fromBottom) Brush.verticalGradient(0f to Color.Transparent, 0.6f to Color.Black) else Brush.verticalGradient(0.4f to Color.Black, 1f to Color.Transparent))
            colorEffects(
                listOf(
                    HazeColorEffect.colorFilter(ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(1.6f) })),
                    HazeColorEffect.tint(Brush.verticalGradient(if (fromBottom) listOf(tint.transparent, tint) else listOf(tint, tint.transparent))),
                ),
            )
        }
    }
    Box(
        modifier
            .height(height)
            .graphicsLayer { this.alpha = alpha }
            .then(if (hazeState != null && !reduce) Modifier.hazeBlur(HazeInput.Sources(hazeState), style) else Modifier.background(Brush.verticalGradient(if (fromBottom) listOf(tint.transparent, tint) else listOf(tint, tint.transparent)))),
    )
}
