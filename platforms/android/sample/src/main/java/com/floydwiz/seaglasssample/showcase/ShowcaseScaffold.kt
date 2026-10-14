package com.floydwiz.seaglasssample.showcase

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.shape.SeaGlassShape
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglasssample.control.ControlSheet
import com.floydwiz.seaglasssample.control.ControlState
import com.floydwiz.seaglasssample.perf.FpsOverlay
import kotlin.math.roundToInt

/**
 * Wraps every route: the top bar with a back lens and the controls lens (a draggable floating lens instead on full-bleed
 * routes), the FPS overlay and the control sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseScaffold(
    title: String?,
    onBack: (() -> Unit)?,
    fullBleed: Boolean,
    controlsTop: Dp,
    controls: ControlState,
    content: @Composable (PaddingValues) -> Unit,
) {
    var showControls by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentWindowInsets = if (fullBleed) WindowInsets(0) else ScaffoldDefaults.contentWindowInsets,
            topBar = {
                if (!fullBleed) {
                    CenterAlignedTopAppBar(
                        title = { Text(title ?: "Sea Glass") },
                        navigationIcon = {
                            if (onBack != null) Box(Modifier.padding(start = 8.dp)) { GlassBarLens("arrow_back", onBack, description = "Back") }
                        },
                        actions = { Box(Modifier.padding(end = 8.dp)) { GlassBarLens("tune", { showControls = true }, description = "Controls") } },
                    )
                }
            },
            content = content,
        )
        if (fullBleed) FloatingControlLens({ showControls = true }, controlsTop, Modifier.align(Alignment.TopEnd))
        if (controls.fpsOverlay) FpsOverlay(Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = if (fullBleed) 8.dp else 64.dp))
    }
    if (showControls) ControlSheet(controls) { showControls = false }
}

/** The controls trigger on routes without a top bar, an ink lens because it floats over arbitrary content; drag it away from whatever it covers. */
@Composable
private fun FloatingControlLens(onClick: () -> Unit, topOffset: Dp, modifier: Modifier = Modifier) {
    var drag by remember { mutableStateOf(Offset.Zero) }
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + topOffset
    Box(
        modifier
            .padding(top = top, end = 12.dp)
            .offset { IntOffset(drag.x.roundToInt(), drag.y.roundToInt()) }
            .pointerInput(Unit) { detectDragGestures { _, amount -> drag += amount } },
    ) {
        GlassSurface(GlassFinish.Ink, Modifier.size(48.dp), SeaGlassShape.Lens, onClick = onClick) {
            SymbolIcon("tune", size = 24.dp, tint = LocalContentColor.current, contentDescription = "Controls")
        }
    }
}
