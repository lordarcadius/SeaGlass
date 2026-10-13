package com.floydwiz.seaglasssample.showcase.catalogue.c2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.selection.GlassCheckbox
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.components.selection.GlassInputChip
import com.floydwiz.seaglass.components.selection.GlassPillSlider
import com.floydwiz.seaglass.components.selection.GlassRadioButton
import com.floydwiz.seaglass.components.selection.GlassRangeSlider
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.GlassSlider
import com.floydwiz.seaglass.components.selection.GlassStepper
import com.floydwiz.seaglass.components.selection.GlassSuggestionChip
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.components.selection.GlassTriStateCheckbox
import com.floydwiz.seaglass.components.selection.SegmentedOption
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.SpecimenTable
import com.floydwiz.seaglasssample.showcase.catalogue.TableRow
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels
import com.floydwiz.seaglasssample.showcase.catalogue.stateRows

private val SwitchNotes = mapOf(
    GlassState.Rest to "well · pearl knob",
    GlassState.Hover to "knob lifts",
    GlassState.Focus to "halo on track",
    GlassState.Press to "knob → clear lens",
    GlassState.Disabled to "dry glass",
)

private val Chips = listOf("Filter · off" to "well", "Filter · on" to "glass", "Input" to "frost · avatar bead", "Suggestion" to "clear")

@Composable
fun SelectionPage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Switch", "Well · Fill · Pearl",
            "A sunken well holds a pearl. Turning on pours lit liquid into the well. While the finger is down the pearl swells into a clear lens that magnifies the liquid beneath it, then settles back on release with spatial.fast.",
        ) {
            ThemePanels {
                val seafoam = LocalRolePalettes.current[GlassRole.Seafoam]
                SpecimenTable(
                    heads = listOf("Off", "On", "On · seafoam"),
                    rows = stateRows(subFor = { SwitchNotes[it] }, height = 52.dp, cells = listOf(
                        { _: GlassState -> var on by remember { mutableStateOf(false) }; GlassSwitch(on, { on = it }, enabled = true) },
                        { _: GlassState -> var on by remember { mutableStateOf(true) }; GlassSwitch(on, { on = it }) },
                        { _: GlassState -> var on by remember { mutableStateOf(true) }; GlassSwitch(on, { on = it }, role = seafoam) },
                    )),
                    labelWidth = 110.dp, cellWidth = 96.dp,
                )
            }
        }

        CatalogueSection(
            "Check and radio", "Well → Glass",
            "Unchecked controls are wells; checking lifts them into glass with a heavy check. Checks use a 9 dp radius on 26 dp so they read as soft squares, never sharp. A selected radio is a glass lens holding a pearl.",
        ) {
            ThemePanels {
                SpecimenTable(
                    heads = listOf("Off", "On", "Mixed", "Error", "Radio off", "Radio on"),
                    rows = stateRows(height = 52.dp, cells = listOf(
                        { _: GlassState -> var on by remember { mutableStateOf(false) }; GlassCheckbox(on, { on = it }) },
                        { _: GlassState -> var on by remember { mutableStateOf(true) }; GlassCheckbox(on, { on = it }) },
                        { _: GlassState -> GlassTriStateCheckbox(ToggleableState.Indeterminate, {}) },
                        { _: GlassState -> var on by remember { mutableStateOf(false) }; GlassCheckbox(on, { on = it }, error = true) },
                        { _: GlassState -> var on by remember { mutableStateOf(false) }; GlassRadioButton(on, { on = !on }) },
                        { _: GlassState -> GlassRadioButton(true, {}) },
                    )),
                    labelWidth = 88.dp, cellWidth = 82.dp,
                )
            }
        }

        CatalogueSection(
            "Segmented control", "Well + Frost lens",
            "Options sit in one shared well; the chosen one is a frost lens. Dragging or pressing turns the lens clear and lets it bulge past the well edge, magnifying the label, so the selection feels like a droplet sliding along a groove.",
        ) {
            ThemePanels {
                val text = listOf("Day", "Week", "Month", "Year").map { SegmentedOption(it) }
                val iconText = listOf("photo" to "Photos", "photo_album" to "Albums", "place" to "Places").map { SegmentedOption(it.second, it.first) }
                val icons = listOf("grid_view", "view_agenda", "view_list", "map").map { SegmentedOption(icon = it) }
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        segRow("Text", null) { var s by remember { mutableIntStateOf(1) }; GlassSegmentedControl(text, s, { s = it }) },
                        segRow("Icon + text", null) { var s by remember { mutableIntStateOf(0) }; GlassSegmentedControl(iconText, s, { s = it }, segmentWidth = 108.dp) },
                        segRow("Icons", null) { var s by remember { mutableIntStateOf(0) }; GlassSegmentedControl(icons, s, { s = it }, segmentWidth = 56.dp) },
                        segRow("Hover", "unselected") { GlassSegmentedControl(text, 1, {}, hoveredIndex = 2) },
                        segRow("Focus", null, GlassState.Focus) { GlassSegmentedControl(text, 1, {}) },
                        segRow("Press", "lens magnifies", GlassState.Press) { GlassSegmentedControl(text, 2, {}) },
                        segRow("Disabled", null) { GlassSegmentedControl(text, 1, {}, enabled = false) },
                    ),
                    cellWidth = 340.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Chips", "Well · Glass · Frost · Clear",
            "Filter chips follow selection-lifts: a sunken well when off, tinted glass with a check when on. Input chips are frost capsules with an avatar bead and a small clear remove lens. Suggestion chips are clear glass with an accent symbol.",
        ) {
            ThemePanels {
                SpecimenTable(
                    heads = listOf("Rest", "Hover", "Focus", "Press", "Disabled"),
                    rows = Chips.mapIndexed { kind, (name, sub) ->
                        TableRow(name, sub, 52.dp, GlassState.entries.map { state ->
                            {
                                androidx.compose.runtime.CompositionLocalProvider(
                                    com.floydwiz.seaglass.material.LocalForcedGlassState provides state.takeUnless { it == GlassState.Rest },
                                ) {
                                    when (kind) {
                                        0 -> GlassFilterChip(false, {}, "Low tide")
                                        1 -> GlassFilterChip(true, {}, "Low tide")
                                        2 -> GlassInputChip("Maya Ortiz", "M", {}, {})
                                        else -> GlassSuggestionChip("Summarize", "auto_awesome", {})
                                    }
                                }
                            }
                        })
                    },
                    labelWidth = 110.dp, cellWidth = 168.dp,
                )
            }
        }

        CatalogueSection(
            "Sliders and stepper", "Well · Fill · Pearl → Clear lens",
            "The track is a 10 dp well filled with lit liquid. The thumb is a pearl capsule at rest and becomes a clear lens with an ink value bubble while dragged. Pill sliders put the symbol inside the liquid for volume and brightness; the stepper is a clear capsule whose pressed side condenses a lens.",
        ) {
            ThemePanels {
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        sliderRow("Rail", "well 10 · pearl 34×22", 64.dp) { var v by remember { mutableFloatStateOf(0.62f) }; GlassSlider(v, { v = it }) },
                        sliderRow("Focus", null, 64.dp, GlassState.Focus) { var v by remember { mutableFloatStateOf(0.62f) }; GlassSlider(v, { v = it }) },
                        sliderRow("Dragging", "clear lens + ink bubble", 96.dp, GlassState.Press) { var v by remember { mutableFloatStateOf(0.62f) }; GlassSlider(v, { v = it }, Modifier.offset(0.dp, 24.dp)) },
                        sliderRow("Range", null, 64.dp) { var v by remember { mutableStateOf(0.24f..0.7f) }; GlassRangeSlider(v, { v = it }) },
                        sliderRow("Stepped", "5 stops", 64.dp) { var v by remember { mutableFloatStateOf(0.6f) }; GlassSlider(v, { v = it }, stepCount = 5) },
                        sliderRow("Disabled", null, 64.dp) { GlassSlider(0.62f, {}, enabled = false) },
                        TableRow("Pill", "volume", 56.dp, listOf({
                            var volume by remember { mutableFloatStateOf(0.62f) }
                            GlassPillSlider(volume, { volume = it }, "volume_up")
                        })),
                        TableRow("Pill · vertical", "brightness · volume", 184.dp, listOf({
                            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.Top) {
                                var light by remember { mutableFloatStateOf(0.62f) }
                                var level by remember { mutableFloatStateOf(0.34f) }
                                GlassPillSlider(light, { light = it }, "light_mode", vertical = true)
                                GlassPillSlider(level, { level = it }, "volume_up", vertical = true)
                            }
                        })),
                        TableRow("Stepper", "rest · pressed +", 60.dp, listOf({
                            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)) {
                                var a by remember { mutableIntStateOf(4) }
                                var b by remember { mutableIntStateOf(4) }
                                GlassStepper(a, { a = it })
                                androidx.compose.runtime.CompositionLocalProvider(com.floydwiz.seaglass.material.LocalForcedGlassState provides GlassState.Press) {
                                    GlassStepper(b, { b = it })
                                }
                            }
                        })),
                    ),
                    cellWidth = 340.dp, labelsAbove = true,
                )
            }
        }
    }
}

private fun segRow(label: String, sub: String?, state: GlassState? = null, content: @Composable () -> Unit) =
    TableRow(label, sub, 56.dp, listOf({
        androidx.compose.runtime.CompositionLocalProvider(com.floydwiz.seaglass.material.LocalForcedGlassState provides state) { content() }
    }))

private fun sliderRow(label: String, sub: String?, height: androidx.compose.ui.unit.Dp, state: GlassState? = null, content: @Composable () -> Unit) =
    TableRow(label, sub, height, listOf({
        androidx.compose.runtime.CompositionLocalProvider(com.floydwiz.seaglass.material.LocalForcedGlassState provides state) {
            Box(Modifier.width(320.dp), contentAlignment = Alignment.CenterStart) { content() }
        }
    }))
