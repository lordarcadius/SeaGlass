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
 * - [content] is drawn above it; glass elements in [content] blur [backdrop].
 * - Adaptive surfaces in [content] sample the luminance of [backdrop]; with none, no sampling happens.
 */
@Composable
fun GlassBackdrop(
    modifier: Modifier = Modifier,
    backdrop: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberHazeState()
    val sampler = remember { BackdropSampler() }
    CompositionLocalProvider(LocalHazeState provides state, LocalBackdropSampler provides sampler) {
        Box(modifier) {
            Box(Modifier.matchParentSize().then(sampler.captureModifier()).hazeSource(state), content = backdrop)
            content()
        }
    }
}
