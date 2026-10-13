package com.floydwiz.seaglass.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

/** Haze state glass reads its backdrop from; null means no backdrop is installed. */
internal val LocalHazeState = staticCompositionLocalOf<HazeState?> { null }

/**
 * Installs a backdrop for glass.
 * - [backdrop] is drawn first and marked as the source that glass blurs.
 * - [layer] is optional page content drawn above it, for example a scrolling list that contains glass of its own. It is
 *   a second source above [backdrop]: glass inside it blurs [backdrop], while glass in [content] blurs both.
 * - [content] is drawn above both; glass elements in [content] blur what is behind them.
 * - Adaptive surfaces in [content] sample the luminance of [backdrop] and [layer]; with none, no sampling happens.
 * Glass inside [backdrop] itself has no source below it to blur, so put content that contains glass in [layer].
 */
@Composable
fun GlassBackdrop(
    modifier: Modifier = Modifier,
    backdrop: @Composable BoxScope.() -> Unit,
    layer: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberHazeState()
    val sampler = remember { BackdropSampler() }
    CompositionLocalProvider(LocalHazeState provides state, LocalBackdropSampler provides sampler) {
        Box(modifier) {
            Box(Modifier.matchParentSize().then(sampler.captureModifier())) {
                Box(Modifier.matchParentSize().hazeSource(state, zIndex = 0f), content = backdrop)
                if (layer != null) Box(Modifier.matchParentSize().hazeSource(state, zIndex = 1f), content = layer)
            }
            content()
        }
    }
}
