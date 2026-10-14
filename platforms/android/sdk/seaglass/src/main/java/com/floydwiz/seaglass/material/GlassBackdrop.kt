package com.floydwiz.seaglass.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalGraphicsContext

/**
 * Installs a backdrop for glass.
 * - [backdrop] is drawn first and is the source that glass blurs.
 * - [layer] is optional page content drawn above it, for example a scrolling list that contains glass of its own. It is
 *   a second source above [backdrop]: glass inside it blurs [backdrop], while glass in [content] blurs both.
 * - [content] is drawn above both; glass elements in [content] blur what is behind them.
 * - Adaptive surfaces in [content] sample the luminance of [backdrop] and [layer]; with none, no sampling happens.
 * Glass inside [backdrop] itself has no source below it to blur, so put content that contains glass in [layer].
 */
@Composable
public fun GlassBackdrop(
    modifier: Modifier = Modifier,
    backdrop: @Composable BoxScope.() -> Unit,
    layer: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val graphicsContext = LocalGraphicsContext.current
    val state = remember { GlassBackdropState(graphicsContext) }
    DisposableEffect(state) { onDispose { state.release() } }
    state.hasLayerSource = layer != null
    val sampler = remember { BackdropSampler() }
    CompositionLocalProvider(LocalGlassBackdrop provides state, LocalBackdropSampler provides sampler) {
        Box(modifier.onSizeChanged { state.size = it }.onGloballyPositioned { state.coordinates = it }) {
            Box(Modifier.matchParentSize().then(sampler.captureModifier())) {
                CompositionLocalProvider(LocalBackdropRank provides BackdropRank.Backdrop) {
                    Box(Modifier.matchParentSize().backdropSource(state.base), content = backdrop)
                }
                if (layer != null) {
                    CompositionLocalProvider(LocalBackdropRank provides BackdropRank.Layer) {
                        Box(Modifier.matchParentSize().backdropSource(state.layer), content = layer)
                    }
                }
            }
            CompositionLocalProvider(LocalBackdropRank provides BackdropRank.Content) { content() }
        }
    }
}
