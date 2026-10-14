package com.floydwiz.seaglasssample.showcase.catalogue.c7

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.fab.FabMenuItem
import com.floydwiz.seaglass.components.fab.FabSize
import com.floydwiz.seaglass.components.fab.GlassExtendedFab
import com.floydwiz.seaglass.components.fab.GlassFab
import com.floydwiz.seaglass.components.fab.GlassFabMenu
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.SpecimenTable
import com.floydwiz.seaglasssample.showcase.catalogue.TableRow
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels
import com.floydwiz.seaglasssample.showcase.catalogue.stateRows

private val StateNotes = mapOf(
    GlassState.Rest to "enabled",
    GlassState.Hover to "lift 2dp · light +",
    GlassState.Focus to "halo",
    GlassState.Press to "scale 0.96 · bloom",
    GlassState.Disabled to "dry glass",
)

private class Mail(val initial: String, val name: String, val subject: String, val time: String, val role: GlassRole)

private val Mails = listOf(
    Mail("M", "Maya Ortiz", "Shore walk photos", "9:41", GlassRole.Lavender),
    Mail("J", "Jun Park", "Tide tables for Saturday", "8:12", GlassRole.Aqua),
    Mail("I", "Ines Silva", "Re: the blue jar pieces", "Yesterday", GlassRole.Honey),
    Mail("T", "Theo Grant", "Album invite: Blue hour", "Yesterday", GlassRole.Seafoam),
    Mail("N", "North Rocks Club", "Meetup moved to 7:00", "Mon", GlassRole.Kelly),
    Mail("S", "Sam Lee", "Found a cobalt piece!", "Sun", GlassRole.Cobalt),
)

@Composable
fun FabsPage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Floating action button", "Sea Glass pebble",
            "The FAB is the only pebble-shaped glass in a layout: an organic silhouette with the full light, volume and caustic stack. 56, 80 and 96 dp sizes; the extended FAB is a 56 dp capsule. It shares the button state model, so press keeps the pebble outline instead of morphing to a sharper shape.",
        ) {
            ThemePanels {
                val roles = LocalRolePalettes.current
                SpecimenTable(
                    heads = listOf("56", "80", "96"),
                    rows = stateRows(subFor = { StateNotes[it] }, height = 108.dp, cells = listOf(
                        { _: GlassState -> GlassFab("edit", {}, size = FabSize.Medium) },
                        { _: GlassState -> GlassFab("edit", {}, size = FabSize.Large) },
                        { _: GlassState -> GlassFab("edit", {}, size = FabSize.ExtraLarge) },
                    )),
                    labelWidth = 72.dp, cellWidth = 92.dp,
                )
                SpecimenTable(
                    heads = listOf("Extended", "Frost"),
                    rows = stateRows(height = 72.dp, cells = listOf(
                        { _: GlassState -> GlassExtendedFab("Compose", "edit", {}) },
                        { _: GlassState -> GlassExtendedFab("Compose", "edit", {}, frost = true) },
                    )),
                    labelWidth = 72.dp, cellWidth = 148.dp,
                )
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(TableRow("Roles", "seed colour", 80.dp, listOf({
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(GlassRole.Seafoam, GlassRole.Lavender, GlassRole.Honey, GlassRole.SeaPink).forEach { GlassFab("edit", {}, role = roles[it]) }
                        }
                    }))),
                    labelWidth = 72.dp, cellWidth = 264.dp,
                )
            }
        }

        CatalogueSection(
            "Floating action menu", "Pebble → lens · Frost cascade",
            "Opening melts the pebble into a round glass close lens while frost capsules rise from it in a cascade, each 40 ms behind the last on spatial.fast. The content behind is veiled with a light blur rather than a dark scrim, so the menu still reads as glass over the screen. Tap the buttons below.",
        ) {
            ThemePanels {
                var closed by remember { mutableStateOf(false) }
                var openOne by remember { mutableStateOf(true) }
                val items = listOf(FabMenuItem("reply", "Reply") {}, FabMenuItem("forward", "Forward") {}, FabMenuItem("archive", "Archive") {}, FabMenuItem("event", "New event") {}, FabMenuItem("sticky_note_2", "Note") {})
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Closed → open", "tap Compose", 620.dp, listOf({ InboxMock(closed, { closed = it }, items) })),
                        TableRow("Open", "five items · close lens", 620.dp, listOf({ InboxMock(openOne, { openOne = it }, items) })),
                    ),
                    cellWidth = 384.dp, labelsAbove = true,
                )
            }
        }
    }
}

@Composable
private fun InboxMock(expanded: Boolean, onExpandedChange: (Boolean) -> Unit, items: List<FabMenuItem>) {
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    val surface = MaterialTheme.colorScheme.surface
    GlassBackdrop(
        modifier = Modifier.size(384.dp, 600.dp).clip(RoundedCornerShape(32.dp)),
        backdrop = {
            Box(Modifier.fillMaxSize().padding(0.dp)) {
                Column(Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 22.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text("Inbox", color = ink, fontSize = 30.sp, fontWeight = FontWeight(760), letterSpacing = (-0.3).sp)
                        GlassIconButton("search", {}, style = GlassButtonStyle.Clear, size = 44.dp, iconScale = 0.5f)
                    }
                    Column(Modifier.padding(top = 20.dp)) {
                        Mails.forEachIndexed { index, mail ->
                            Row(Modifier.fillMaxWidth().height(76.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
                                GlassSurface(GlassFinish.Bead, Modifier.size(40.dp), SeaGlassShape.Round, LocalRolePalettes.current[mail.role], reactsTo = emptySet()) {
                                    Text(mail.initial, color = androidx.compose.material3.LocalContentColor.current, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(mail.name, color = ink, fontSize = 15.sp, fontWeight = FontWeight(if (index < 2) 700 else 550))
                                    Text(mail.subject, color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(450))
                                }
                                Text(mail.time, color = if (index < 2) glassAccent() else glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(if (index < 2) 700 else 500))
                            }
                            if (index < Mails.lastIndex) HorizontalDivider(Modifier.padding(start = 54.dp), color = ink.copy(alpha = 0.07f))
                        }
                    }
                }
            }
        },
    ) {
        GlassFabMenu(expanded, onExpandedChange, items, label = "Compose")
    }
}
