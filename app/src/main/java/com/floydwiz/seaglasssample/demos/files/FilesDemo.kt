package com.floydwiz.seaglasssample.demos.files

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.containers.GlassContextMenu
import com.floydwiz.seaglass.components.containers.GlassInsetList
import com.floydwiz.seaglass.components.containers.GlassListRow
import com.floydwiz.seaglass.components.containers.GlassMenuDivider
import com.floydwiz.seaglass.components.containers.GlassMenuItem
import com.floydwiz.seaglass.components.feedback.GlassLiquidProgress
import com.floydwiz.seaglass.components.feedback.GlassRingProgress
import com.floydwiz.seaglass.components.feedback.GlassToastHost
import com.floydwiz.seaglass.components.feedback.GlassToastState
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.input.GlassSearchField
import com.floydwiz.seaglass.components.navigation.GlassFloatingToolbar
import com.floydwiz.seaglass.components.navigation.GlassScopeBar
import com.floydwiz.seaglass.components.navigation.GlassTabBar
import com.floydwiz.seaglass.components.navigation.ScopeItem
import com.floydwiz.seaglass.components.navigation.TabItem
import com.floydwiz.seaglass.components.navigation.ToolItem
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.SegmentedOption
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
import com.floydwiz.seaglasssample.demos.LoadingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class Kind(val label: String, val role: GlassRole, val icon: String) {
    Folder("Folders", GlassRole.Honey, "folder"), Image("Images", GlassRole.Lavender, "image"), Video("Video", GlassRole.Cornflower, "videocam"),
    Audio("Audio", GlassRole.Seafoam, "music_note"), Document("Documents", GlassRole.Aqua, "description"),
    Archive("Archives", GlassRole.Kelly, "folder_zip"), App("Apps", GlassRole.Cobalt, "apps"),
}

private class Entry(val name: String, val kind: Kind, val sizeMb: Float, val modified: String, val children: List<Entry> = emptyList()) {
    val isFolder get() = kind == Kind.Folder
}

private fun f(name: String, kind: Kind, mb: Float, modified: String) = Entry(name, kind, mb, modified)
private fun d(name: String, vararg children: Entry) = Entry(name, Kind.Folder, children.sumOf { it.sizeMb.toDouble() }.toFloat(), "Today", children.toList())

private val Root = d(
    "Storage",
    d(
        "Pictures",
        d("Shore walk", f("IMG_0412.jpg", Kind.Image, 4.2f, "Sat"), f("IMG_0413.jpg", Kind.Image, 3.9f, "Sat"), f("IMG_0420.jpg", Kind.Image, 4.4f, "Sat"), f("Low tide.mp4", Kind.Video, 148f, "Sat")),
        d("Blue hour", f("Pier.jpg", Kind.Image, 5.1f, "Wed"), f("Jetty.jpg", Kind.Image, 4.8f, "Wed")),
        f("Cobalt find.jpg", Kind.Image, 3.6f, "Mon"),
    ),
    d("Music", f("Tide song.mp3", Kind.Audio, 7.2f, "Oct 2"), f("Kelp.flac", Kind.Audio, 31f, "Sep 28"), f("Harbour bells.mp3", Kind.Audio, 6.1f, "Sep 20")),
    d("Documents", f("Tide tables.pdf", Kind.Document, 0.4f, "Today"), f("Meetup notes.txt", Kind.Document, 0.02f, "Tue"), f("Jar inventory.xlsx", Kind.Document, 0.3f, "Sun")),
    d("Backups", f("Library 2025.zip", Kind.Archive, 2480f, "Jan 3"), f("Notes.zip", Kind.Archive, 14f, "Sep 1")),
    f("Beachcomb.apk", Kind.App, 41f, "Oct 1"),
)

private val Tabs = listOf(TabItem("storage", "Storage"), TabItem("folder_open", "Browse"), TabItem("search", "Search"))
private val Views = listOf("List", "Grid").map { SegmentedOption(it) }
private val SelectTools = listOf(ToolItem("content_copy", "Copy"), ToolItem("drive_file_move", "Move"), ToolItem("ios_share", "Share"), ToolItem("delete", "Delete", danger = true))
private val SearchFilters = listOf("Images", "Documents", "Large", "Recent")

private val Info = DemoInfo(
    "File manager",
    "Storage at a glance, a folder browser with a context menu and multi-select, and search. Colour carries the file type; sea pink is reserved for delete.",
    listOf(
        "Rows on an inset milk list, tiles as milk cards" to "R4: content sits on milk.",
        "Glass type tiles on milk" to "Glass may sit on milk; colour carries meaning: folders honey, images lavender, video cornflower, audio seafoam, documents aqua, archives kelly, apps cobalt.",
        "Scope bar (clear capsule, frost lens, count beads)" to "R3: the current folder is the frost lens; beads count items.",
        "List / Grid segmented control" to "R3.",
        "Context menu (milk, lifted preview, delete in sea pink)" to "R4, R2.",
        "Multi-select toolbar" to "Floating clear chrome with a glass confirm lens (R5).",
        "Storage ring and liquid bars" to "Fill finish on well tracks; the ring is the progress ring scaled up.",
        "Search field and chips" to "R2 (well) and R3 (a chip lifts when on).",
    ),
)

/** File manager demo. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesDemo(onBack: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var path by remember { mutableStateOf(listOf("Storage")) }
    var grid by remember { mutableIntStateOf(0) }
    var selecting by remember { mutableStateOf(false) }
    val selected = remember { mutableStateListOf<String>() }
    val removed = remember { mutableStateListOf<String>() }
    var menuFor by remember { mutableStateOf<Entry?>(null) }
    var about by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(setOf<String>()) }
    val toast = remember { GlassToastState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { delay(900); loading = false }

    fun folder(at: List<String>): Entry? {
        var current: Entry? = Root
        for (name in at.drop(1)) current = current?.children?.firstOrNull { it.name == name && it.name !in removed }
        return current
    }
    fun entries(at: List<String>) = folder(at)?.children.orEmpty().filter { it.name !in removed }
    fun open(entry: Entry) { if (entry.isFolder) path = path + entry.name }
    fun exitSelect() { selecting = false; selected.clear() }

    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) },
        layer = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = 720.dp).fillMaxSize()) {
                    when {
                        loading -> LoadingState()
                        tab == 0 -> StorageTab()
                        tab == 1 -> BrowseTab(
                            path, { path = it }, entries(path), grid, { grid = it }, selecting, selected,
                            onOpen = { entry -> if (selecting) { if (entry.name in selected) selected.remove(entry.name) else selected.add(entry.name) } else open(entry) },
                            onMenu = { menuFor = it },
                        )
                        else -> SearchTab(query, { query = it }, filters, { filters = it }) { entry ->
                            tab = 1
                            path = listOf("Storage") + (if (entry.isFolder) listOf(entry.name) else emptyList())
                        }
                    }
                }
            }
        },
    ) {
        DemoChrome(onBack, { about = true }, Modifier.align(Alignment.TopStart))
        GlassToastHost(toast, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 110.dp))
        if (selecting && tab == 1) {
            Box(Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 16.dp)) {
                Text("${selected.size} selected", fontSize = 17.sp, fontWeight = FontWeight(690))
            }
            GlassFloatingToolbar(
                SelectTools, -1,
                { index ->
                    when (index) {
                        3 -> {
                            val count = selected.size
                            removed.addAll(selected); exitSelect()
                            scope.launch { toast.show("Deleted $count items", icon = "delete", role = GlassRole.SeaPink) }
                        }
                        else -> scope.launch { toast.show("${SelectTools[index].description} ${selected.size} items", icon = SelectTools[index].icon) }
                    }
                },
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 26.dp),
                onConfirm = { exitSelect() }, confirmIcon = "check",
            )
        } else if (!loading) {
            GlassTabBar(Tabs, tab, { tab = it }, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 26.dp), tabWidth = 92.dp)
        }
    }

    menuFor?.let { entry ->
        GlassContextMenu(
            onDismissRequest = { menuFor = null },
            preview = { EntryPreview(entry) },
            menu = {
                GlassMenuItem("Open", { open(entry); menuFor = null }, icon = "open_in_new", highlighted = true)
                GlassMenuItem("Select", { selecting = true; selected.add(entry.name); menuFor = null }, icon = "check_circle")
                GlassMenuItem("Share", { menuFor = null; scope.launch { toast.show("Shared ${entry.name}", icon = "ios_share") } }, icon = "ios_share")
                GlassMenuDivider()
                GlassMenuItem("Delete", { removed.add(entry.name); menuFor = null; scope.launch { toast.show("Deleted ${entry.name}", icon = "delete", role = GlassRole.SeaPink) } }, icon = "delete", danger = true)
            },
        )
    }
    if (about) AboutDemoSheet(Info) { about = false }
}

@Composable
private fun StorageTab() {
    val roles = LocalRolePalettes.current
    val categories = listOf(Kind.Image to 38f, Kind.Video to 31f, Kind.Audio to 12f, Kind.Document to 9f, Kind.Archive to 14f, Kind.App to 20f)
    val used = categories.sumOf { it.second.toDouble() }.toFloat()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp).padding(top = 72.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text("Storage", fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
        GlassCard(Modifier.fillMaxWidth(), shape = SeaGlassShape.Pane(28.dp)) {
            Row(Modifier.fillMaxWidth().padding(22.dp), Arrangement.spacedBy(22.dp), Alignment.CenterVertically) {
                GlassRingProgress(used / 200f, size = 132.dp, stroke = 14.dp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${used.toInt()} GB", fontSize = 20.sp, fontWeight = FontWeight(760))
                        Text("of 200 GB", color = glassOnSurfaceVariant(), fontSize = 12.sp, fontWeight = FontWeight(500))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { (kind, gb) ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).background(com.floydwiz.seaglass.material.glassAccent(roles[kind.role]), SeaGlassShape.Round))
                            Text("${kind.label} ${gb.toInt()} GB", fontSize = 13.sp, fontWeight = FontWeight(550))
                        }
                    }
                }
            }
        }
        GlassCard(Modifier.fillMaxWidth(), shape = SeaGlassShape.Pane(28.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                categories.forEach { (kind, gb) -> GlassLiquidProgress(gb / 200f, kind.label, "${gb.toInt()} GB", Modifier.fillMaxWidth(), role = roles[kind.role]) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BrowseTab(
    path: List<String>, onPath: (List<String>) -> Unit, entries: List<Entry>, view: Int, onView: (Int) -> Unit,
    selecting: Boolean, selected: List<String>, onOpen: (Entry) -> Unit, onMenu: (Entry) -> Unit,
) {
    val roles = LocalRolePalettes.current
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(top = 68.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(path.last(), Modifier.padding(horizontal = 20.dp), fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            GlassScopeBar(path.map { ScopeItem(it) }, path.lastIndex, { onPath(path.take(it + 1)) })
        }
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            GlassSegmentedControl(Views, view, onView, segmentWidth = 84.dp)
            Text("${entries.size} items", Modifier.padding(start = 14.dp), color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(550))
        }
        if (entries.isEmpty()) {
            EmptyState("folder_off", "Nothing here", "This folder is empty.", Modifier.padding(horizontal = 16.dp))
        } else if (view == 0) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 140.dp)) {
                GlassInsetList(Modifier.fillMaxWidth()) {
                    entries.forEachIndexed { index, entry ->
                        Box(
                            Modifier.combinedClickable(
                                interactionSource = remember { MutableInteractionSource() }, indication = null,
                                onClick = { onOpen(entry) }, onLongClick = { onMenu(entry) },
                            ),
                        ) {
                            GlassListRow(
                                entry.name, leadingIcon = entry.kind.icon, leadingRole = roles[entry.kind.role],
                                supporting = "${size(entry)} · ${entry.modified}", selected = entry.name in selected, divider = index < entries.lastIndex,
                                trailing = if (entry.isFolder && !selecting) ({ SymbolIcon("chevron_right", size = 20.dp, tint = glassOnSurfaceVariant()) }) else null,
                            )
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                GridCells.Fixed(2), Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, bottom = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(entries) { entry ->
                    GlassCard(
                        Modifier.fillMaxWidth().combinedClickable(
                            interactionSource = remember { MutableInteractionSource() }, indication = null,
                            onClick = { onOpen(entry) }, onLongClick = { onMenu(entry) },
                        ),
                        selected = entry.name in selected, shape = SeaGlassShape.Pane(22.dp),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            GlassSurface(GlassFinish.Glass, Modifier.size(48.dp), SeaGlassShape.Pane(16.dp), roles[entry.kind.role], reactsTo = emptySet()) {
                                SymbolIcon(entry.kind.icon, size = 26.dp, fill = true, tint = LocalContentColor.current)
                            }
                            Column {
                                Text(entry.name, fontSize = 14.5.sp, fontWeight = FontWeight(650), maxLines = 1)
                                Text("${size(entry)} · ${entry.modified}", color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(500))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun size(entry: Entry): String = when {
    entry.isFolder -> "${entry.children.size} items"
    entry.sizeMb >= 1000f -> "%.1f GB".format(entry.sizeMb / 1000f)
    entry.sizeMb >= 1f -> "%.1f MB".format(entry.sizeMb)
    else -> "${(entry.sizeMb * 1000).toInt()} KB"
}

@Composable
private fun EntryPreview(entry: Entry) {
    val roles = LocalRolePalettes.current
    GlassCard(Modifier.width(300.dp), shape = SeaGlassShape.Pane(24.dp)) {
        Row(Modifier.padding(16.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
            GlassSurface(GlassFinish.Glass, Modifier.size(48.dp), SeaGlassShape.Pane(16.dp), roles[entry.kind.role], reactsTo = emptySet()) {
                SymbolIcon(entry.kind.icon, size = 26.dp, fill = true, tint = LocalContentColor.current)
            }
            Column {
                Text(entry.name, fontSize = 16.sp, fontWeight = FontWeight(680), maxLines = 1)
                Text("${size(entry)} · ${entry.modified}", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(500))
            }
        }
    }
}

@Composable
private fun SearchTab(query: String, onQuery: (String) -> Unit, filters: Set<String>, onFilters: (Set<String>) -> Unit, onOpen: (Entry) -> Unit) {
    val roles = LocalRolePalettes.current
    val all = remember { flatten(Root) }
    val results = all.filter { entry ->
        (query.isBlank() || entry.name.contains(query, ignoreCase = true)) &&
            ("Images" !in filters || entry.kind == Kind.Image) && ("Documents" !in filters || entry.kind == Kind.Document) &&
            ("Large" !in filters || entry.sizeMb >= 20f) && ("Recent" !in filters || entry.modified in listOf("Today", "Sat", "Tue", "Wed", "Mon"))
    }
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(top = 68.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Search", Modifier.padding(horizontal = 20.dp), fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
        GlassSearchField(query, onQuery, Modifier.padding(horizontal = 16.dp).fillMaxWidth(), placeholder = "Search files")
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), Arrangement.spacedBy(8.dp)) {
            SearchFilters.forEach { name -> GlassFilterChip(name in filters, { onFilters(if (name in filters) filters - name else filters + name) }, name) }
        }
        if (results.isEmpty()) {
            EmptyState("search_off", "Nothing washed up", "No file matches “$query” with these filters. Try fewer filters or a shorter word.", Modifier.padding(horizontal = 16.dp))
        } else {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 140.dp)) {
                GlassInsetList(Modifier.fillMaxWidth()) {
                    results.forEachIndexed { index, entry ->
                        GlassListRow(
                            entry.name, onClick = { onOpen(entry) }, leadingIcon = entry.kind.icon, leadingRole = roles[entry.kind.role],
                            supporting = "${size(entry)} · ${entry.modified}", divider = index < results.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

private fun flatten(entry: Entry): List<Entry> = entry.children.flatMap { listOf(it) + flatten(it) }
