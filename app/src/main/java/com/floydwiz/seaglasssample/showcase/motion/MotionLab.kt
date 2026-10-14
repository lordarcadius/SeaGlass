package com.floydwiz.seaglasssample.showcase.motion

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.fab.FabMenuItem
import com.floydwiz.seaglass.components.fab.GlassFab
import com.floydwiz.seaglass.components.fab.GlassFabMenu
import com.floydwiz.seaglass.components.feedback.GlassBeadsLoading
import com.floydwiz.seaglass.components.feedback.GlassLinearProgress
import com.floydwiz.seaglass.components.feedback.GlassLoadingIndicator
import com.floydwiz.seaglass.components.feedback.GlassRingProgress
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.components.navigation.GlassTabBar
import com.floydwiz.seaglass.components.navigation.GlassTopBar
import com.floydwiz.seaglass.components.navigation.TabItem
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.GlassSlider
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.components.selection.SegmentedOption
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.LocalMotionEnabled
import com.floydwiz.seaglass.material.GlassClockScope
import com.floydwiz.seaglass.material.LocalMotionScale
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoStrip
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels
import kotlinx.coroutines.delay

private val Tabs = listOf(TabItem("photo_library", "Photos"), TabItem("collections_bookmark", "Albums"), TabItem("explore", "Explore"))
private val Segments = listOf("Years", "Months", "Days", "All").map { SegmentedOption(it) }

@Composable
fun MotionLab(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding, statePicker = false) {
        val scale = LocalMotionScale.current
        var slow by remember { mutableStateOf(false) }
        DisposableEffect(slow) {
            SeaGlassTokens.Motion.slowdown = if (slow) 4f else 1f
            onDispose { SeaGlassTokens.Motion.slowdown = 1f }
        }
        CatalogueSection(
            "Animation settings", "ANIMATOR_DURATION_SCALE",
            "Every choreography follows the system animator duration scale, live: at ×0 loops and morphs stop and changes are instant, above ×1 they slow down. Slow motion below stretches every duration here by four, springs included, while this page is open.",
        ) {
            Text(
                "System animator scale ×${"%.1f".format(scale)} · ${if (LocalMotionEnabled.current) "animations on" else "animations off"}",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Slow motion (×4)", style = MaterialTheme.typography.bodyLarge)
                GlassSwitch(slow, { slow = it })
            }
            if (!LocalMotionEnabled.current) {
                Text(
                    "Animations are off in system settings, so loops are stopped and changes are instant. The cards below show the reduced version.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        CatalogueSection(
            "Press, hover and focus", "spatial.fast scale and lift · effects.fast light",
            "Hover lifts raised glass 2 dp and adds light, press shrinks it to 0.96 and blooms, focus draws the halo. Scale and lift overshoot on spatial.fast; light and halo fade without overshoot on effects.fast. Replay steps through the three states.",
        ) {
            ThemePanels {
                Stage { replay ->
                    var forced by remember { mutableStateOf<GlassState?>(null) }
                    LaunchedEffect(replay) {
                        if (replay == 0) return@LaunchedEffect
                        for (state in listOf(GlassState.Hover, GlassState.Press, GlassState.Focus)) {
                            forced = state
                            delay(900)
                            forced = null
                            delay(500)
                        }
                    }
                    CompositionLocalProvider(LocalForcedGlassState provides forced) {
                        Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            GlassButton("Glass", {})
                            GlassIconButton("favorite", {}, style = GlassButtonStyle.Glass, size = 56.dp)
                            GlassFab("edit", {})
                        }
                    }
                }
            }
        }

        CatalogueSection(
            "Switch", "spatial.fast knob and swell · effects.default pour",
            "The pearl slides on spatial.fast and, while pressed, swells into a 44×38 lens that overflows the track. The liquid pours in on effects.default.",
        ) {
            ThemePanels {
                Stage { replay ->
                    var checked by remember { mutableStateOf(false) }
                    var pressed by remember { mutableStateOf(false) }
                    LaunchedEffect(replay) {
                        if (replay == 0) return@LaunchedEffect
                        repeat(2) {
                            pressed = true
                            delay(600)
                            checked = !checked
                            delay(500)
                            pressed = false
                            delay(1100)
                        }
                    }
                    CompositionLocalProvider(LocalForcedGlassState provides if (pressed) GlassState.Press else null) {
                        GlassSwitch(checked, { checked = it })
                    }
                }
            }
        }

        CatalogueSection(
            "Slider", "spatial.fast lens · ink bubble",
            "The thumb swells into a 54×38 lens with an ink value bubble rising 41 dp above while dragged. Replay drags it across and back.",
        ) {
            ThemePanels {
                Stage { replay ->
                    var value by remember { mutableFloatStateOf(0.25f) }
                    var dragging by remember { mutableStateOf(false) }
                    LaunchedEffect(replay) {
                        if (replay == 0) return@LaunchedEffect
                        dragging = true
                        delay(500)
                        animate(value, 0.85f, animationSpec = tween(1100)) { v, _ -> value = v }
                        animate(value, 0.45f, animationSpec = tween(800)) { v, _ -> value = v }
                        delay(400)
                        dragging = false
                    }
                    CompositionLocalProvider(LocalForcedGlassState provides if (dragging) GlassState.Press else null) {
                        GlassSlider(value, { value = it }, Modifier.size(320.dp, 72.dp))
                    }
                }
            }
        }

        CatalogueSection(
            "Segmented control and tab bar", "Lens stretch · spatial.default settle",
            "The lens stretches between segments with its leading edge ahead of the trailing one, bulging 3 dp past the well (6 dp on the tab bar), then settles on spatial.default. Replay jumps between segments; dragging does the same under a finger.",
        ) {
            ThemePanels { dark ->
                Stage { replay ->
                    var index by remember { mutableIntStateOf(0) }
                    LaunchedEffect(replay) {
                        if (replay == 0) return@LaunchedEffect
                        for (target in listOf(3, 1, 2, 0)) {
                            delay(1100)
                            index = target
                        }
                    }
                    GlassSegmentedControl(Segments, index, { index = it }, segmentWidth = 80.dp)
                    PhotoStrip(dark, 384.dp, 132.dp, crop = 7) {
                        GlassTabBar(Tabs, index.coerceAtMost(Tabs.lastIndex), { index = it }, Modifier.offset(14.dp, 56.dp), onSearch = {})
                    }
                }
            }
        }

        CatalogueSection(
            "Tab bar minimize", "spatial.default morph",
            "On scroll down the bar condenses into the current-tab lens, an accessory capsule and the search lens; scrolling up expands it again, both on spatial.default while the content cross-fades on effects.default.",
        ) {
            ThemePanels { dark ->
                Stage { replay ->
                    var minimized by remember { mutableStateOf(false) }
                    LaunchedEffect(replay) {
                        if (replay == 0) return@LaunchedEffect
                        minimized = true
                        delay(2200)
                        minimized = false
                    }
                    PhotoStrip(dark, 384.dp, 132.dp, crop = 7) {
                        GlassTabBar(
                            Tabs, 0, {}, Modifier.offset(14.dp, 62.dp), minimized = minimized, onSearch = {}, onExpand = { minimized = false },
                            accessory = { Text("Uploading 12 of 20", Modifier.padding(horizontal = 20.dp), maxLines = 1) },
                        )
                    }
                }
            }
        }

        CatalogueSection(
            "Floating action menu", "Melt · 40 ms cascade",
            "The content behind is veiled with a light blur, the pebble melts through an egg into a round glass close lens and frost capsules rise from it 62 dp apart, each 40 ms after the previous, on spatial.fast.",
        ) {
            ThemePanels { dark ->
                Stage { replay ->
                    var expanded by remember { mutableStateOf(false) }
                    LaunchedEffect(replay) {
                        if (replay == 0) return@LaunchedEffect
                        expanded = true
                        delay(2600)
                        expanded = false
                    }
                    val items = remember { listOf(FabMenuItem("reply", "Reply") {}, FabMenuItem("forward", "Forward") {}, FabMenuItem("archive", "Archive") {}) }
                    PhotoStrip(dark, 384.dp, 380.dp, crop = 4) { GlassFabMenu(expanded, { expanded = it }, items, label = "Compose") }
                }
            }
        }

        CatalogueSection(
            "Top bar edge diffusion", "effects.default",
            "The 84 dp diffusion under a top bar fades in as content scrolls beneath it, with no hard bar edge.",
        ) {
            ThemePanels { dark ->
                Stage { replay ->
                    var scrolled by remember { mutableStateOf(false) }
                    LaunchedEffect(replay) {
                        if (replay == 0) return@LaunchedEffect
                        scrolled = false
                        delay(400)
                        scrolled = true
                        delay(2000)
                        scrolled = false
                    }
                    PhotoStrip(dark, 384.dp, 132.dp, crop = 9) {
                        GlassTopBar("Library", scrolled = scrolled, leading = { GlassBarLens("arrow_back", {}) })
                    }
                }
            }
        }

        CatalogueSection(
            "Loading morph", "2.4 s alternate · each interval eased",
            "The loading pebble tumbles through lens, shard, egg and tumble; Replay starts the loop again from the lens. With animations off it holds the lens shape.",
        ) {
            ThemePanels {
                Stage { replay ->
                    key(replay) {
                        Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
                            GlassLoadingIndicator()
                            GlassLoadingIndicator(contained = true)
                        }
                    }
                }
            }
        }

        CatalogueSection(
            "Comet and bead bob", "Comet 1.2 s linear · beads 1.1 s with −0.75 s and −0.4 s phases",
            "The comet is a glass arc that fades to nothing at its tail; the three beads bob in sequence for inline waits.",
        ) {
            ThemePanels {
                Stage { replay ->
                    key(replay) {
                        Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
                            GlassRingProgress(null)
                            GlassBeadsLoading()
                        }
                    }
                }
            }
        }

        CatalogueSection(
            "Indeterminate progress", "1.8 s sweep",
            "The liquid segment sweeps across the well, clipped by a moving window.",
        ) {
            ThemePanels {
                Stage { replay -> key(replay) { GlassLinearProgress(null, Modifier.fillMaxWidth()) } }
            }
        }

        CatalogueSection(
            "Light drift", "8 s alternate · ±5% by ±3%",
            "This card always drifts, on a clock of its own that Replay restarts at zero; elsewhere the light pool drifts on a shared clock only when Animate light drift is on in the controls.",
        ) {
            ThemePanels { dark ->
                Stage { replay ->
                    GlassClockScope(replay) {
                        PhotoStrip(dark, 384.dp, 132.dp, crop = 7) {
                            GlassSurface(GlassFinish.Glass, Modifier.offset(40.dp, 30.dp).size(240.dp, 72.dp), SeaGlassShape.Capsule, reactsTo = emptySet())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.Stage(content: @Composable ColumnScope.(replay: Int) -> Unit) {
    var replay by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        GlassButton("Replay", { replay++ }, style = GlassButtonStyle.Frost, size = GlassButtonSize.S, leadingIcon = "replay")
        content(replay)
    }
}
