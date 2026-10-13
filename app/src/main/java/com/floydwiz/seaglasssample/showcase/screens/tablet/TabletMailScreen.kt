package com.floydwiz.seaglasssample.showcase.screens.tablet

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.buttons.ClusterItem
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.input.GlassPromptBar
import com.floydwiz.seaglass.components.input.GlassSearchField
import com.floydwiz.seaglass.components.navigation.GlassBarActions
import com.floydwiz.seaglass.components.navigation.GlassNavigationRail
import com.floydwiz.seaglass.components.navigation.RailDestination
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.components.selection.GlassSuggestionChip
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoTile
import com.floydwiz.seaglasssample.showcase.screens.Avatar
import com.floydwiz.seaglasssample.showcase.screens.inbox.MailItems
import com.floydwiz.seaglasssample.showcase.screens.inbox.MailRow
import com.floydwiz.seaglasssample.showcase.screens.inbox.matches

private val Folders = listOf(
    RailDestination("inbox", "Inbox", "3"), RailDestination("star", "Starred"), RailDestination("send", "Sent"),
    RailDestination("draft", "Drafts"), RailDestination("archive", "Archive"), RailDestination("delete", "Trash"),
)
private val MessageActions = listOf(ClusterItem("archive", "Archive"), ClusterItem("delete", "Delete"), ClusterItem("more_vert", "More"))
private val Filters = listOf("Unread", "Flagged", "Attachments")

/** Mail on a tablet: clear rail, list with a frost selected row, and a milk reading pane with a clear reply bar. */
@Composable
fun TabletMailScreen() {
    var folder by remember { mutableIntStateOf(0) }
    var selectedId by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(setOf<String>()) }
    var readIds by remember { mutableStateOf(setOf<Int>()) }
    val visible = MailItems.filter { it.matches(filters, query, readIds) }
    val ink = MaterialTheme.colorScheme.onSurface

    GlassBackdrop(Modifier.fillMaxSize(), backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) }) {
        Row(Modifier.fillMaxSize().statusBarsPadding().padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 40.dp), Arrangement.spacedBy(16.dp)) {
            GlassNavigationRail(Folders, folder, { folder = it }, onFabClick = {})
            Column(Modifier.width(380.dp).fillMaxHeight()) {
                Text("Inbox", Modifier.padding(start = 8.dp), color = ink, fontSize = 28.sp, fontWeight = FontWeight(760), letterSpacing = (-0.3).sp)
                GlassSearchField(query, { query = it }, Modifier.padding(top = 14.dp).fillMaxWidth(), placeholder = "Search mail")
                Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 8.dp), Arrangement.spacedBy(8.dp)) {
                    Filters.forEach { name -> GlassFilterChip(name in filters, { filters = if (name in filters) filters - name else filters + name }, name) }
                }
                LazyColumn(Modifier.fillMaxSize()) {
                    items(visible, key = { it.id }) { mail ->
                        MailRow(mail, unread = mail.unread && mail.id !in readIds, last = mail === visible.last(), selected = mail.id == selectedId) {
                            selectedId = mail.id
                            readIds = readIds + mail.id
                        }
                    }
                }
            }
            ReadingPane(selectedId, Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Composable
private fun ReadingPane(mailId: Int, modifier: Modifier) {
    val mail = MailItems.first { it.id == mailId }
    val ink = MaterialTheme.colorScheme.onSurface
    val variant = glassOnSurfaceVariant()
    var reply by remember(mailId) { mutableStateOf("") }
    GlassCard(modifier, shape = SeaGlassShape.Pane(32.dp)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 32.dp, end = 32.dp, top = 28.dp, bottom = 112.dp), Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(mail.subject, Modifier.weight(1f), color = ink, fontSize = 24.sp, fontWeight = FontWeight(730), letterSpacing = (-0.2).sp)
                GlassBarActions(MessageActions, {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (mail.avatarCrop != null) Avatar(mail.avatarCrop, 44.dp)
                else Box(Modifier.size(44.dp).background(MaterialTheme.colorScheme.primaryContainer, SeaGlassShape.Round))
                Column {
                    Text(mail.sender, color = ink, fontSize = 15.5.sp, fontWeight = FontWeight.Bold)
                    Text(mail.recipients, color = variant, fontSize = 13.sp, fontWeight = FontWeight(500))
                }
            }
            Text(mail.body, color = ink, fontSize = 15.5.sp, lineHeight = 25.sp, fontWeight = FontWeight(450))
            if (mail.photos.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    mail.photos.forEach { PhotoTile(it, Modifier.size(160.dp, 120.dp), RoundedCornerShape(18.dp)) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GlassSuggestionChip("${mail.photos.size} photos", "photo_library", {})
                    GlassSuggestionChip("Save all", "download", {})
                }
            }
            if (mail.id == 0) {
                HorizontalDivider(color = ink.copy(alpha = 0.08f))
                Text("Jun Park replied · 9:52", color = variant, fontSize = 13.sp, fontWeight = FontWeight(600))
                Text("Yes! Let’s call it Cobalt finds. I can bring the blue jar on Saturday.", color = ink, fontSize = 15.sp, lineHeight = 23.sp, fontWeight = FontWeight(450))
            }
        }
        GlassPromptBar(
            reply, { reply = it }, { reply = "" },
            Modifier.align(Alignment.BottomCenter).padding(start = 32.dp, end = 32.dp, bottom = 28.dp).fillMaxWidth(),
            placeholder = "Reply to ${mail.sender}",
        )
    }
}
