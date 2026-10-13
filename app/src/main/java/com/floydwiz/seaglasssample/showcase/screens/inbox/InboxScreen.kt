package com.floydwiz.seaglasssample.showcase.screens.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.fab.FabMenuItem
import com.floydwiz.seaglass.components.fab.GlassFabMenu
import com.floydwiz.seaglass.components.feedback.GlassBadge
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.input.GlassSearchField
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglasssample.showcase.screens.Avatar

private val Filters = listOf("Unread", "Flagged", "Attachments", "People")

private val ComposeItems = listOf(
    FabMenuItem("edit", "New message") {}, FabMenuItem("event", "Meeting invite") {},
    FabMenuItem("sticky_note_2", "Note to self") {}, FabMenuItem("document_scanner", "Scan a document") {},
)

internal fun Mail.matches(filters: Set<String>, query: String, readIds: Set<Int>): Boolean {
    if ("Unread" in filters && !(unread && id !in readIds)) return false
    if ("Flagged" in filters && flag != MailFlag.Starred) return false
    if ("Attachments" in filters && flag != MailFlag.Attachment) return false
    if ("People" in filters && avatarCrop == null) return false
    return query.isBlank() || listOf(sender, subject, preview).any { it.contains(query, ignoreCase = true) }
}

/** Inbox: well search, filter chips, message rows and an extended glass Compose button that opens a frost cascade. */
@Composable
fun InboxScreen() {
    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(setOf<String>()) }
    var readIds by remember { mutableStateOf(setOf<Int>()) }
    var composing by remember { mutableStateOf(false) }
    val visible = MailItems.filter { it.matches(filters, query, readIds) }
    val unreadCount = MailItems.count { it.unread && it.id !in readIds }
    val ink = MaterialTheme.colorScheme.onSurface

    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {
                    item {
                        Column(Modifier.statusBarsPadding()) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                GlassBarLens("menu", {}, description = "Menu")
                                GlassSurface(GlassFinish.Clear, Modifier.size(40.dp), SeaGlassShape.Round, onClick = {}) { Avatar(6, 32.dp) }
                            }
                            Text("Inbox", Modifier.padding(start = 20.dp, top = 12.dp), color = ink, fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
                            Text(
                                "$unreadCount unread · updated just now", Modifier.padding(start = 21.dp, top = 2.dp),
                                color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(500),
                            )
                            GlassSearchField(query, { query = it }, Modifier.padding(horizontal = 16.dp).padding(top = 18.dp).fillMaxWidth(), placeholder = "Search mail")
                            Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(top = 14.dp, bottom = 6.dp), Arrangement.spacedBy(8.dp)) {
                                Filters.forEach { name ->
                                    GlassFilterChip(name in filters, { filters = if (name in filters) filters - name else filters + name }, name)
                                }
                            }
                        }
                    }
                    items(visible, key = { it.id }) { mail ->
                        MailRow(mail, unread = mail.unread && mail.id !in readIds, last = mail === visible.last()) { readIds = readIds + mail.id }
                    }
                }
            }
        },
    ) {
        GlassFabMenu(
            composing, { composing = it }, ComposeItems, label = "Compose",
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        )
    }
}

/** A message row; [selected] draws the frost lens behind it and switches the text to the on-lens colours. */
@Composable
internal fun MailRow(mail: Mail, unread: Boolean, last: Boolean, selected: Boolean = false, onClick: () -> Unit) {
    val onLens = glassOnRole(LocalGlassRole.current)
    val ink = if (selected) onLens else MaterialTheme.colorScheme.onSurface
    val variant = if (selected) onLens.copy(alpha = 0.78f) else glassOnSurfaceVariant()
    val roles = LocalRolePalettes.current
    Column(Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)) {
        Box(Modifier.fillMaxWidth().height(86.dp)) {
            if (selected) GlassSurface(GlassFinish.Frost, Modifier.matchParentSize().padding(horizontal = 4.dp, vertical = 3.dp), SeaGlassShape.Pane(24.dp), reactsTo = emptySet())
            if (unread && !selected) GlassBadge(Modifier.align(Alignment.CenterStart).padding(start = 5.dp), dotSize = 8.dp)
            if (mail.avatarCrop != null) Avatar(mail.avatarCrop, 44.dp, Modifier.padding(start = 18.dp, top = 14.dp))
            else GlassSurface(
                GlassFinish.Bead, Modifier.padding(start = 18.dp, top = 14.dp).size(44.dp), SeaGlassShape.Round, roles[mail.avatarRole ?: GlassRole.Aqua], reactsTo = emptySet(),
            ) { Text(mail.sender.take(1), color = LocalContentColor.current, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
            Column(Modifier.fillMaxWidth().padding(start = 74.dp, end = 16.dp, top = 12.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text(mail.sender, Modifier.weight(1f), color = ink, fontSize = 15.5.sp, lineHeight = 22.sp, fontWeight = FontWeight(if (unread) 700 else 550), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(mail.time, Modifier.padding(start = 8.dp, top = 2.dp), color = if (unread && !selected) glassAccent() else if (selected) ink else variant, fontSize = 12.5.sp, lineHeight = 18.sp, fontWeight = FontWeight(if (unread) 700 else 500))
                }
                Text(mail.subject, color = ink, fontSize = 14.5.sp, lineHeight = 22.sp, fontWeight = FontWeight(if (unread) 650 else 500), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(mail.preview, Modifier.weight(1f), color = variant, fontSize = 13.5.sp, lineHeight = 22.sp, fontWeight = FontWeight(450), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    when (mail.flag) {
                        MailFlag.Attachment -> SymbolIcon("attach_file", Modifier.padding(start = 6.dp), size = 17.dp, tint = variant)
                        MailFlag.Starred -> SymbolIcon("star", Modifier.padding(start = 6.dp), size = 18.dp, fill = true, tint = glassAccent(roles[GlassRole.Honey]))
                        MailFlag.None -> Unit
                    }
                }
            }
        }
        if (!last && !selected) HorizontalDivider(Modifier.padding(start = 74.dp, end = 16.dp), color = ink.copy(alpha = 0.07f))
    }
}
