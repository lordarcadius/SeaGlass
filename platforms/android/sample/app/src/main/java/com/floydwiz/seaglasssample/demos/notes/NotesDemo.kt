package com.floydwiz.seaglasssample.demos.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.ClusterItem
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassCluster
import com.floydwiz.seaglass.components.containers.GlassBottomSheet
import com.floydwiz.seaglass.components.containers.GlassInsetList
import com.floydwiz.seaglass.components.fab.GlassExtendedFab
import com.floydwiz.seaglass.components.feedback.GlassToastHost
import com.floydwiz.seaglass.components.feedback.GlassToastState
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.input.GlassSearchField
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.components.selection.GlassCheckbox
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglasssample.demos.AboutDemoSheet
import com.floydwiz.seaglasssample.demos.DemoChrome
import com.floydwiz.seaglasssample.demos.DemoInfo
import com.floydwiz.seaglasssample.demos.EmptyState
import kotlinx.coroutines.launch

private class Note(val id: Int, var title: String, var body: String, val date: String, val checks: MutableList<Pair<String, Boolean>> = mutableListOf())

private val Info = DemoInfo(
    "Notes",
    "A list of notes, an editor on the plain surface with a floating clear format cluster and checklist items, and a share sheet of glass tiles.",
    listOf(
        "Note rows on an inset milk list" to "R4: content sits on milk.",
        "Search well" to "R2: fields are wells.",
        "Format cluster: one clear capsule of 40 dp items" to "R3: several items can be active; R5: lenses, not second sheets.",
        "Checklist checkboxes" to "R3: unchecked rests in a well, checked lifts to glass.",
        "Share sheet on milk with glass tiles" to "Glass on milk (R4).",
        "Extended FAB" to "The single glass primary action.",
    ),
)

private val Formats = listOf(ClusterItem("format_bold", "Bold"), ClusterItem("format_italic", "Italic"), ClusterItem("format_list_bulleted", "List"), ClusterItem("checklist", "Checklist"))
private val Targets = listOf(Triple("Message", "chat", GlassRole.Aqua), Triple("Mail", "mail", GlassRole.Cobalt), Triple("Copy link", "link", GlassRole.Seafoam), Triple("Print", "print", GlassRole.Honey), Triple("Save to file", "save", GlassRole.Lavender))

/** Notes demo: list, editor and share sheet. */
@Composable
fun NotesDemo(onBack: () -> Unit) {
    val notes = remember {
        mutableStateListOf(
            Note(1, "Tide tables for Saturday", "Low at 6:40, high at 12:52. Meet by the north rocks and bring the thermos.", "Today"),
            Note(2, "Jar inventory", "Sorted by colour last night.", "Yesterday", mutableListOf("Amber (31)" to true, "Blue (12)" to false, "Green (22)" to false, "Cobalt (4)" to true)),
            Note(3, "Meetup plan", "Parking fills early, so share rides.", "Mon", mutableListOf("Book the car park" to false, "Tell Maya the new time" to true)),
            Note(4, "Ideas", "A shelf lit from behind. A map of the best beaches.", "Sun"),
        )
    }
    var openId by remember { mutableStateOf<Int?>(null) }
    var query by remember { mutableStateOf("") }
    var about by remember { mutableStateOf(false) }
    var nextId by remember { mutableStateOf(10) }
    val toast = remember { GlassToastState() }
    BackHandler(openId != null) { openId = null }

    Box(Modifier.fillMaxSize()) {
        val open = notes.firstOrNull { it.id == openId }
        if (open == null) {
            NoteList(notes, query, { query = it }, { openId = it }, { notes.add(0, Note(nextId, "", "", "Now")); openId = nextId++ }, onBack, { about = true })
        } else {
            Editor(open, toast, { if (open.title.isBlank() && open.body.isBlank() && open.checks.isEmpty()) notes.remove(open); openId = null })
        }
    }
    if (about) AboutDemoSheet(Info) { about = false }
}

@Composable
private fun NoteList(notes: List<Note>, query: String, onQuery: (String) -> Unit, onOpen: (Int) -> Unit, onNew: () -> Unit, onBack: () -> Unit, onAbout: () -> Unit) {
    val shown = notes.filter { query.isBlank() || it.title.contains(query, true) || it.body.contains(query, true) }
    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) },
        layer = {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 140.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Column(Modifier.statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Notes", fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
                        GlassSearchField(query, onQuery, Modifier.fillMaxWidth(), placeholder = "Search notes", trailingIcon = null)
                    }
                }
                item {
                    if (shown.isEmpty()) EmptyState("note_stack", "Nothing washed up", "No note matches “$query”.")
                    else GlassInsetList(Modifier.fillMaxWidth()) {
                        shown.forEachIndexed { index, note ->
                            Column(Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onOpen(note.id) }) {
                                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text(note.title.ifBlank { "New note" }, Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight(650), maxLines = 1)
                                        Text(note.date, color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(550))
                                    }
                                    Text(note.body.ifBlank { note.checks.firstOrNull()?.first ?: "No additional text" }, color = glassOnSurfaceVariant(), fontSize = 14.sp, maxLines = 2, lineHeight = 19.sp)
                                }
                                if (index < shown.lastIndex) HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            }
                        }
                    }
                }
            }
        },
    ) {
        DemoChrome(onBack, onAbout, Modifier.align(Alignment.TopStart))
        GlassExtendedFab("New note", "edit_square", onNew, Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(end = 16.dp, bottom = 24.dp))
    }
}

@Composable
private fun Editor(note: Note, toast: GlassToastState, onClose: () -> Unit) {
    var title by remember(note.id) { mutableStateOf(note.title) }
    var body by remember(note.id) { mutableStateOf(note.body) }
    val checks = remember(note.id) { mutableStateListOf<Pair<String, Boolean>>().apply { addAll(note.checks) } }
    var active by remember { mutableStateOf(setOf<Int>()) }
    var sharing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val ink = MaterialTheme.colorScheme.onSurface
    val bold = 0 in active
    val italic = 1 in active
    val bodyStyle = TextStyle(
        color = ink, fontSize = 17.sp, lineHeight = 26.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight(450), fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
    )
    fun save() { note.title = title; note.body = body; note.checks.clear(); note.checks.addAll(checks) }

    GlassBackdrop(Modifier.fillMaxSize(), backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) }) {
        Column(Modifier.fillMaxSize().statusBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 64.dp, bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BasicTextField(
                title, { title = it; save() }, Modifier.fillMaxWidth(), textStyle = TextStyle(color = ink, fontSize = 30.sp, fontWeight = FontWeight(760)),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner -> Box { if (title.isEmpty()) Text("Title", color = glassOnSurfaceVariant(), fontSize = 30.sp, fontWeight = FontWeight(760)); inner() } },
            )
            BasicTextField(
                body, { body = it; save() }, Modifier.fillMaxWidth(), textStyle = bodyStyle, cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner -> Box { if (body.isEmpty()) Text("Start writing", color = glassOnSurfaceVariant(), fontSize = 17.sp); inner() } },
            )
            checks.forEachIndexed { index, (text, done) ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    GlassCheckbox(done, { checks[index] = text to it; save() })
                    Text(text, color = if (done) glassOnSurfaceVariant() else ink, fontSize = 17.sp, textDecoration = if (done) TextDecoration.LineThrough else null)
                }
            }
        }
        Row(Modifier.statusBarsPadding().padding(start = 16.dp, top = 8.dp), Arrangement.spacedBy(10.dp)) {
            GlassBarLens("arrow_back", { save(); onClose() }, description = "Back")
            GlassBarLens("ios_share", { sharing = true }, description = "Share")
        }
        Box(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().imePadding().padding(bottom = 16.dp)) {
            GlassCluster(
                Formats, active,
                { index ->
                    if (index == 3) { checks.add("New item" to false); save() } else if (index == 2) { body = body + (if (body.isEmpty() || body.endsWith("\n")) "• " else "\n• "); save() }
                    else active = if (index in active) active - index else active + index
                },
            )
        }
        GlassToastHost(toast, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 90.dp))
    }
    if (sharing) {
        GlassBottomSheet(onDismissRequest = { sharing = false }) {
            val roles = LocalRolePalettes.current
            Column(Modifier.padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("Share note", fontSize = 22.sp, fontWeight = FontWeight(740))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Targets.take(5).forEach { (name, icon, role) ->
                        Column(Modifier.width(62.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { sharing = false; scope.launch { toast.show("$name: ${title.ifBlank { "Untitled" }}", icon = icon) } }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            GlassSurface(GlassFinish.Glass, Modifier.size(52.dp), SeaGlassShape.Pane(18.dp), roles[role], reactsTo = emptySet()) { SymbolIcon(icon, size = 26.dp, fill = true, tint = LocalContentColor.current) }
                            Text(name, fontSize = 11.5.sp, fontWeight = FontWeight(600), maxLines = 1)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), Arrangement.End) { GlassButton("Cancel", { sharing = false }, style = GlassButtonStyle.Plain) }
            }
        }
    }
}
