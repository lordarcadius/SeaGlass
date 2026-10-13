package com.floydwiz.seaglasssample.showcase.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.navigation.GlassFloatingToolbar
import com.floydwiz.seaglass.components.navigation.GlassTabBar
import com.floydwiz.seaglass.components.navigation.TabItem
import com.floydwiz.seaglass.components.navigation.ToolItem
import com.floydwiz.seaglass.components.navigation.rememberTabBarState
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.SegmentedOption
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassEdgeDiffusion
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalAdaptiveTint
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.showcase.screens.Avatar
import com.floydwiz.seaglasssample.showcase.screens.Picture

private val Tabs = listOf(TabItem("photo_library", "Library"), TabItem("photo_album", "Albums"), TabItem("auto_awesome", "Memories"))
private val Modes = listOf("Years", "Months", "Days", "All").map { SegmentedOption(it) }
private val Tools = listOf(ToolItem("favorite", "Favourite"), ToolItem("library_add", "Add to album"), ToolItem("download", "Download"), ToolItem("delete", "Delete", danger = true))
private val TileImages = listOf(0, -2, 1, 5, 3, -1, 2, 8, -4, 9, 7, 13, -6, 4, 11, 10, -3, 12)

private class Section(val title: String, val caption: String, val count: Int, val start: Int)

private fun sectionsFor(mode: Int): List<Section> {
    val rows = when (mode) {
        0 -> listOf("2026" to ("1,904 photos" to 9), "2025" to ("1,877 photos" to 9), "2024" to ("1,602 photos" to 6))
        1 -> listOf("October 2026" to ("312 items" to 9), "September 2026" to ("428 items" to 9), "August 2026" to ("391 items" to 6))
        2 -> listOf("Saturday, 10 Oct" to ("Shore walk · 24 items" to 9), "Wednesday, 7 Oct" to ("Blue hour · 12 items" to 6), "Sunday, 4 Oct" to ("North rocks · 18 items" to 9), "Friday, 2 Oct" to ("Tidepools · 8 items" to 6))
        else -> listOf("All photos" to ("2,481 photos · 38 videos" to 27))
    }
    var start = 0
    return rows.map { (title, rest) ->
        Section(title, rest.first, rest.second, start).also { start += rest.second }
    }
}

/** Photo library: browse by period, then select photos with a clear toolbar and a glass share lens. */
@Composable
fun LibraryScreen() {
    var mode by remember { mutableIntStateOf(2) }
    var tab by remember { mutableIntStateOf(0) }
    var selecting by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    val tabState = rememberTabBarState()
    val listState = rememberLazyListState()
    val topBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    val sections = remember(mode) { sectionsFor(mode) }
    val ink = MaterialTheme.colorScheme.onSurface
    val total = sections.sumOf { it.count }

    fun toggle(id: Int) { selected = if (id in selected) selected - id else selected + id }

    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                LazyColumn(
                    Modifier.fillMaxSize().nestedScroll(tabState.connection), listState,
                    contentPadding = PaddingValues(top = if (selecting) topBarHeight else 0.dp, bottom = 140.dp),
                ) {
                    if (!selecting) {
                        item {
                            Row(Modifier.statusBarsPadding().fillMaxWidth().padding(end = 16.dp, top = 8.dp), Arrangement.End, Alignment.CenterVertically) {
                                GlassButton("Select", { selecting = true; tabState.minimized = false }, style = GlassButtonStyle.Clear, size = GlassButtonSize.S)
                                Spacer(Modifier.size(10.dp))
                                GlassSurface(GlassFinish.Clear, Modifier.size(40.dp), SeaGlassShape.Round, onClick = {}) { Avatar(2, 32.dp) }
                            }
                        }
                        item {
                            Text("Library", Modifier.padding(start = 20.dp, top = 12.dp), color = ink, fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
                            Text("2,481 photos · 38 videos", Modifier.padding(start = 21.dp, top = 2.dp), color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(500))
                            GlassSegmentedControl(Modes, mode, { mode = it }, Modifier.padding(start = 18.dp, top = 18.dp), segmentWidth = 92.dp)
                        }
                    }
                    sections.forEach { section ->
                        item(key = "h-${section.title}") {
                            SectionHeader(section, if (selecting) "${section.caption.substringBefore(" ·")} · ${selected.count { it in section.start until section.start + section.count }} of ${section.count} selected" else section.caption)
                        }
                        val rows = (0 until section.count).chunked(3)
                        rows.forEachIndexed { rowIndex, ids ->
                            item(key = "r-${section.title}-$rowIndex") {
                                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 4.dp), Arrangement.spacedBy(4.dp)) {
                                    ids.forEach { local ->
                                        val id = section.start + local
                                        Box(Modifier.weight(1f)) {
                                            Tile(id, selecting, id in selected, onClick = { if (selecting) toggle(id) }, onLongClick = { selecting = true; tabState.minimized = false; selected = selected + id })
                                        }
                                    }
                                    repeat(3 - ids.size) { Spacer(Modifier.weight(1f)) }
                                }
                            }
                        }
                    }
                }
            }
        },
    ) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
        if (selecting) {
            Box(Modifier.align(Alignment.TopStart)) {
                GlassEdgeDiffusion(Modifier.fillMaxWidth(), height = 118.dp + 24.dp)
            }
            Box(Modifier.statusBarsPadding().fillMaxWidth().height(56.dp).padding(horizontal = 6.dp)) {
                GlassButton("Cancel", { selecting = false; selected = emptySet() }, Modifier.align(Alignment.CenterStart), style = GlassButtonStyle.Plain, size = GlassButtonSize.S)
                Text(
                    "${selected.size} selected", Modifier.align(Alignment.Center), color = ink, fontSize = 17.sp, fontWeight = FontWeight(690),
                )
                GlassButton(
                    "Select all", { selected = (0 until total).toSet() }, Modifier.align(Alignment.CenterEnd).padding(end = 10.dp),
                    style = GlassButtonStyle.Clear, size = GlassButtonSize.S,
                )
            }
            GlassFloatingToolbar(
                Tools, -1, { if (it == 3) selected = emptySet() }, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 26.dp),
                onConfirm = {}, confirmIcon = "ios_share",
            )
        } else {
            GlassTabBar(
                Tabs, tab, { tab = it }, Modifier.align(Alignment.BottomStart).navigationBarsPadding().padding(start = 28.dp, bottom = 26.dp),
                minimized = tabState.minimized, tabWidth = 92.dp, onSearch = {}, onExpand = { tabState.minimized = false },
                accessory = { Text("2,481 photos", Modifier.padding(horizontal = 20.dp), color = LocalContentColor.current, fontSize = 14.sp, fontWeight = FontWeight(600)) },
            )
        }
        }
    }
}

@Composable
private fun SectionHeader(section: Section, caption: String) {
    Box(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 6.dp)) {
        Column {
            Text(section.title, color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(caption, color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(500))
        }
        Text(
            "See all", Modifier.align(Alignment.TopEnd).padding(top = 3.dp), color = glassAccent(), fontSize = 14.sp, fontWeight = FontWeight(650),
            textAlign = TextAlign.End,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Tile(id: Int, selecting: Boolean, selected: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    val dark = LocalSeaGlassDark.current
    val roles = LocalRolePalettes.current
    val shape = RoundedCornerShape(10.dp)
    val image = TileImages[id % TileImages.size]
    val hasGlass = selecting || id == 1 || id == 4
    val frame = Modifier.fillMaxWidth().aspectRatio(1f).clip(shape).combinedClickable(
        interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick, onLongClick = onLongClick,
    )
    if (!hasGlass) {
        Picture(image, frame, shape)
        return
    }
    GlassBackdrop(frame, backdrop = { Picture(image, Modifier.fillMaxSize(), shape) }) {
        if (id == 1) {
            GlassSurface(GlassFinish.Ink, Modifier.align(Alignment.TopEnd).offset(x = (-6).dp, y = 6.dp).size(46.dp, 22.dp), SeaGlassShape.Capsule, reactsTo = emptySet()) {
                Text("0:14", color = LocalContentColor.current, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        if (id == 4 && !selecting) {
            GlassSurface(GlassFinish.Ink, Modifier.align(Alignment.BottomStart).offset(x = 6.dp, y = (-6).dp).size(28.dp), SeaGlassShape.Round, reactsTo = emptySet()) {
                SymbolIcon("favorite", size = 16.dp, fill = true, tint = roles[GlassRole.SeaPink][if (dark) 40 else 80])
            }
        }
        if (selected) {
            Box(
                Modifier.fillMaxSize().background(if (dark) Color.Black.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.16f))
                    .border(3.dp, glassAccent(), shape),
            )
            GlassSurface(GlassFinish.Glass, Modifier.align(Alignment.BottomEnd).offset(x = (-6).dp, y = (-6).dp).size(26.dp), SeaGlassShape.Round, reactsTo = emptySet()) {
                SymbolIcon("check", size = 18.dp, weight = 700, tint = LocalContentColor.current)
            }
        } else if (selecting) {
            GlassSurface(GlassFinish.Clear, Modifier.align(Alignment.BottomEnd).offset(x = (-6).dp, y = (-6).dp).size(24.dp), SeaGlassShape.Round, reactsTo = emptySet())
        }
    }
}
