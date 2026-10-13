package com.floydwiz.seaglass.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.preview.GlassPreview
import com.floydwiz.seaglass.preview.LightDarkPreviews
import java.time.LocalDate
import java.time.YearMonth

@LightDarkPreviews
@Composable
private fun GlassTextFieldPreview() = GlassPreview {
    GlassTextField("", {}, "Email", placeholder = "name@example.com", leadingIcon = "mail", supportingText = "Only used for sign-in")
    GlassTextField("maya@tidepool", {}, "Email", leadingIcon = "mail", supportingText = "Add the domain", error = true)
    GlassTextField("", {}, "Email", placeholder = "name@example.com", leadingIcon = "mail", enabled = false)
}

@LightDarkPreviews
@Composable
private fun GlassTextAreaPreview() = GlassPreview {
    GlassTextArea("Low tide at 6:40. Meet by the north rocks.", {}, "Note", supportingText = "Shown to everyone on the walk")
}

@LightDarkPreviews
@Composable
private fun GlassSearchPreview() = GlassPreview {
    GlassSearchField("", {}, placeholder = "Search mail")
    GlassFloatingSearch("", {}, placeholder = "Search photos")
    GlassPromptBar("", {}, onSend = {}, placeholder = "Ask about this photo", attachment = PromptAttachment("Shore walk.jpg"))
    GlassPromptBar("", {}, onSend = {}, listening = true)
}

@LightDarkPreviews
@Composable
private fun GlassPickersPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassPickerChip("event", "Fri, Oct 16", {})
        GlassPickerChip("schedule", "7:30 AM", {})
    }
    GlassTimeWheel(7, 30, false, { _, _, _ -> })
}

@LightDarkPreviews
@Composable
private fun GlassDatePickerPreview() = GlassPreview {
    GlassDatePicker(
        YearMonth.of(2026, 10), {}, DateSelection.Range(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 24)), {},
        LocalDate.of(2026, 10, 13), onCancel = {}, onDone = {},
    )
}
