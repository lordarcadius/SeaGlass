package com.floydwiz.seaglass.components.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.preview.GlassPreview
import com.floydwiz.seaglass.preview.LightDarkPreviews

@LightDarkPreviews
@Composable
private fun GlassSwitchPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassSwitch(false, {})
        GlassSwitch(true, {})
        GlassSwitch(true, {}, enabled = false)
    }
}

@LightDarkPreviews
@Composable
private fun GlassCheckRadioPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassCheckbox(false, {})
        GlassCheckbox(true, {})
        GlassTriStateCheckbox(ToggleableState.Indeterminate, {})
        GlassCheckbox(false, {}, error = true)
        GlassRadioButton(false, {})
        GlassRadioButton(true, {})
    }
}

@LightDarkPreviews
@Composable
private fun GlassSegmentedControlPreview() = GlassPreview {
    GlassSegmentedControl(listOf("Day", "Week", "Month", "Year").map { SegmentedOption(it) }, 1, {})
    GlassSegmentedControl(listOf("grid_view", "view_agenda", "view_list").map { SegmentedOption(icon = it) }, 0, {}, segmentWidth = 56.dp)
}

@LightDarkPreviews
@Composable
private fun GlassChipsPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GlassFilterChip(false, {}, "Low tide")
        GlassFilterChip(true, {}, "Low tide")
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GlassInputChip("Maya Ortiz", "M", {}, {})
        GlassSuggestionChip("Summarize", "auto_awesome", {})
    }
}

@LightDarkPreviews
@Composable
private fun GlassSlidersPreview() = GlassPreview {
    GlassSlider(0.62f, {})
    GlassSlider(0.6f, {}, stepCount = 5)
    GlassRangeSlider(0.24f..0.7f, {})
    GlassPillSlider(0.62f, {}, "volume_up")
    GlassStepper(4, {})
}
