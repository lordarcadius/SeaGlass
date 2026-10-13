package com.floydwiz.seaglasssample.showcase.catalogue.c3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.input.DateSelection
import com.floydwiz.seaglass.components.input.GlassDatePicker
import com.floydwiz.seaglass.components.input.GlassFloatingSearch
import com.floydwiz.seaglass.components.input.GlassPickerChip
import com.floydwiz.seaglass.components.input.GlassPromptBar
import com.floydwiz.seaglass.components.input.GlassSearchField
import com.floydwiz.seaglass.components.input.GlassTextArea
import com.floydwiz.seaglass.components.input.GlassTextField
import com.floydwiz.seaglass.components.input.GlassTimeWheel
import com.floydwiz.seaglass.components.input.PromptAttachment
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoStrip
import com.floydwiz.seaglasssample.showcase.catalogue.SpecimenTable
import com.floydwiz.seaglasssample.showcase.catalogue.TableRow
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels
import java.time.LocalDate
import java.time.YearMonth

private class FieldKind(val label: String, val sub: String, val state: GlassState?, val value: String, val error: Boolean = false, val enabled: Boolean = true)

private val FieldKinds = listOf(
    FieldKind("Empty", "well · label above", null, ""),
    FieldKind("Hover", "well darkens 7%", GlassState.Hover, ""),
    FieldKind("Focus", "tinted well + halo", GlassState.Focus, "maya@tidepool.co"),
    FieldKind("Filled", "clear lens to clear", null, "maya@tidepool.co"),
    FieldKind("Error", "sea pink tint + ring", null, "maya@tidepool", error = true),
    FieldKind("Disabled", "dry glass", null, "", enabled = false),
)

@Composable
fun InputPage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Text fields", "Well",
            "A field is an 18 dp well 52 dp tall, with its label outside and above so nothing floats or overlaps. Focus tints the well with the accent and adds the halo; error tints it sea pink with a 1.5 dp ring and swaps the supporting text. The clear button is a 26 dp clear lens.",
        ) {
            ThemePanels {
                SpecimenTable(
                    heads = emptyList(),
                    rows = FieldKinds.map { kind ->
                        TableRow(kind.label, kind.sub, 98.dp, listOf({
                            var text by remember { mutableStateOf(kind.value) }
                            CompositionLocalProvider(LocalForcedGlassState provides kind.state) {
                                GlassTextField(
                                    value = text, onValueChange = { text = it }, label = "Email",
                                    modifier = Modifier.width(320.dp), placeholder = "name@example.com", leadingIcon = "mail",
                                    supportingText = if (kind.error) "Add the domain, like .co or .com" else "Only used for sign-in",
                                    error = kind.error, enabled = kind.enabled,
                                )
                            }
                        }))
                    } + TableRow("Text area", "well · radius 22", 176.dp, listOf({
                        var note by remember { mutableStateOf("Low tide at 6:40. Meet by the north rocks and bring the blue jar for the small pieces.") }
                        GlassTextArea(note, { note = it }, "Note", Modifier.width(360.dp), supportingText = "Shown to everyone on the walk")
                    })),
                    cellWidth = 380.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Search and prompt", "Well in content · Clear when floating",
            "Search inside content is a well. Search that floats over content is a clear capsule with its own lens beside it. The prompt bar is the same clear capsule with a glass send lens and an attachment capsule above it; while listening, the whole bar turns to tinted glass with a live waveform.",
        ) {
            ThemePanels { dark ->
                var search by remember { mutableStateOf("") }
                var focused by remember { mutableStateOf("tide") }
                var floating by remember { mutableStateOf("") }
                var prompt by remember { mutableStateOf("") }
                var listening by remember { mutableStateOf(false) }
                var attachment by remember { mutableStateOf<PromptAttachment?>(null) }
                val thumb = painterResource(R.drawable.seaglass_photo)
                if (attachment == null) attachment = PromptAttachment("Shore walk.jpg", thumb)
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Search", "well", 56.dp, listOf({ GlassSearchField(search, { search = it }, Modifier.width(440.dp), placeholder = "Search mail", onCancel = { search = "" }) })),
                        TableRow("Search · focus", null, 56.dp, listOf({
                            CompositionLocalProvider(LocalForcedGlassState provides GlassState.Focus) {
                                GlassSearchField(focused, { focused = it }, Modifier.width(440.dp), placeholder = "Search mail", onCancel = { focused = "" })
                            }
                        })),
                        TableRow("Floating", "clear + lens", 120.dp, listOf({
                            PhotoStrip(dark, 440.dp, 112.dp, crop = 4) {
                                GlassFloatingSearch(floating, { floating = it }, Modifier.offset(16.dp, 30.dp).width(408.dp), placeholder = "Search photos")
                            }
                        })),
                        TableRow("Prompt bar", "clear + glass send", 150.dp, listOf({
                            PhotoStrip(dark, 440.dp, 140.dp, crop = 2) {
                                GlassPromptBar(
                                    prompt, { prompt = it }, onSend = { prompt = "" },
                                    modifier = Modifier.offset(14.dp, 12.dp).width(412.dp), placeholder = "Ask about this photo",
                                    attachment = attachment, onRemoveAttachment = { attachment = null },
                                    listening = false,
                                )
                            }
                        })),
                        TableRow("Listening", "lavender glass", 110.dp, listOf({
                            PhotoStrip(dark, 440.dp, 100.dp, crop = 6) {
                                GlassPromptBar(
                                    "", {}, onSend = {}, modifier = Modifier.offset(14.dp, 20.dp).width(412.dp),
                                    listening = true, onListeningChange = { listening = it },
                                )
                            }
                        })),
                    ),
                    cellWidth = 440.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Pickers", "Milk pane · Glass lens · Frost band",
            "Pickers are milk panes with 32 dp corners. The chosen date is a glass lens; today is an accent numeral with a bead. A range is a tinted well joining two lenses. The time wheel has a frost band, with values fading and shrinking as they turn away from it.",
        ) {
            ThemePanels {
                val today = LocalDate.of(2026, 10, 13)
                var month by remember { mutableStateOf(YearMonth.of(2026, 10)) }
                var picked by remember { mutableStateOf<LocalDate?>(LocalDate.of(2026, 10, 16)) }
                var rangeMonth by remember { mutableStateOf(YearMonth.of(2026, 10)) }
                var start by remember { mutableStateOf<LocalDate?>(LocalDate.of(2026, 10, 20)) }
                var end by remember { mutableStateOf<LocalDate?>(LocalDate.of(2026, 10, 24)) }
                var hour by remember { mutableStateOf(7) }
                var minute by remember { mutableStateOf(30) }
                var pm by remember { mutableStateOf(false) }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Label("Date · single")
                    GlassDatePicker(month, { month = it }, DateSelection.Single(picked), { picked = it }, today, onCancel = {}, onDone = {})
                    Label("Date · range")
                    GlassDatePicker(
                        rangeMonth, { rangeMonth = it }, DateSelection.Range(start, end),
                        { date ->
                            val s = start
                            if (s == null || end != null) { start = date; end = null }
                            else if (date.isBefore(s)) start = date
                            else end = date
                        },
                        today, onCancel = {}, onDone = {},
                    )
                    Label("Time wheel")
                    GlassTimeWheel(hour, minute, pm, { h, m, p -> hour = h; minute = m; pm = p })
                    Label("Compact pickers open the pane")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassPickerChip("event", "Fri, Oct 16", {})
                        CompositionLocalProvider(LocalForcedGlassState provides GlassState.Focus) {
                            GlassPickerChip("schedule", "%d:%02d %s".format(hour, minute, if (pm) "PM" else "AM"), {})
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
}
