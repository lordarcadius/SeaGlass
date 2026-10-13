package com.floydwiz.seaglasssample.demos.gallery

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.containers.GlassBottomSheet
import com.floydwiz.seaglass.components.containers.GlassCaption
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.containers.GlassCarousel
import com.floydwiz.seaglass.components.feedback.GlassToastHost
import com.floydwiz.seaglass.components.feedback.GlassToastState
import com.floydwiz.seaglass.components.feedback.ToastAction
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.components.navigation.GlassFloatingToolbar
import com.floydwiz.seaglass.components.navigation.GlassTabBar
import com.floydwiz.seaglass.components.navigation.TabItem
import com.floydwiz.seaglass.components.navigation.ToolItem
import com.floydwiz.seaglass.components.navigation.rememberTabBarState
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.LocalAdaptiveTint
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglasssample.demos.AboutDemoSheet
import com.floydwiz.seaglasssample.demos.DemoChrome
import com.floydwiz.seaglasssample.demos.DemoInfo
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoTile
import com.floydwiz.seaglasssample.showcase.screens.Picture
import com.floydwiz.seaglasssample.showcase.screens.library.PhotoBrowser
import kotlinx.coroutines.launch

private val Tabs = listOf(TabItem("photo_library", "Photos"), TabItem("photo_album", "Albums"), TabItem("auto_awesome", "Memories"))

private class Album(val name: String, val count: Int, val cover: Int)

private val Albums = listOf(
    Album("Shore walk", 24, 9), Album("Blue hour", 12, 5), Album("North rocks", 18, 3), Album("Tidepools", 8, 7),
    Album("Cobalt finds", 4, 10), Album("Amber jar", 31, 4), Album("Winter swell", 16, 1), Album("Kelp forest", 22, 6),
)

private class Memory(val title: String, val caption: String, val image: Int)

private val Memories = listOf(
    Memory("A year ago", "Shore walk", 9), Memory("Blue hour", "Twelve photos from the pier", 5),
    Memory("Amber season", "Your jar, sorted by colour", 4), Memory("Low tide", "North rocks, 6:40", 3), Memory("Cobalt", "The deep blue ones", 10),
)

private val Info = DemoInfo(
    "Gallery",
    "Albums, a photo grid with a selection mode, a full-screen viewer and memories. Floating chrome over photos is clear glass with adaptive tint; content is on milk.",
    listOf(
        "Segmented control (well + frost lens)" to "R3: the chosen range lifts out of the well.",
        "Photo tiles with ring and glass check lens" to "R3: selection lifts. The tile radius is the board's 10 dp, below the 12 dp floor of R8, ported as drawn.",
        "Floating clear tab bar and toolbars" to "R4: floating chrome over photos is clear glass; adaptive tint over bright photos.",
        "Glass share lens in the select toolbar" to "R1, R2, R5: the one action that does something is the only glass in the clear toolbar.",
        "Album cards and the info sheet" to "R4: content sits on milk.",
        "Carousel with clear caption and arrow lens" to "R4: captions over imagery are clear; page dots sit in a well (R2).",
    ),
)

/** Gallery demo: Photos, Albums and Memories behind a floating tab bar, a viewer on top. */
@Composable
fun GalleryDemo(onBack: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var viewer by remember { mutableStateOf<Int?>(null) }
    var about by remember { mutableStateOf(false) }
    BackHandler(viewer != null) { viewer = null }

    Box(Modifier.fillMaxSize()) {
        val bar: @Composable androidx.compose.foundation.layout.BoxScope.(Boolean, () -> Unit) -> Unit = { minimized, expand ->
            GlassTabBar(
                Tabs, tab, { tab = it }, Modifier.align(Alignment.BottomStart).navigationBarsPadding().padding(start = 28.dp, bottom = 26.dp),
                minimized = minimized, tabWidth = 92.dp, onSearch = {}, onExpand = expand,
            )
        }
        when (tab) {
            0 -> PhotoBrowser(onOpenPhoto = { viewer = it }) { state -> bar(state.minimized) { state.minimized = false } }
            1 -> AlbumsTab(bar) { viewer = it }
            else -> MemoriesTab(bar) { viewer = it }
        }
        DemoChrome(onBack, { about = true }, Modifier.align(Alignment.TopStart))
        viewer?.let { Viewer(it) { viewer = null } }
    }
    if (about) AboutDemoSheet(Info) { about = false }
}

@Composable
private fun AlbumsTab(bar: @Composable androidx.compose.foundation.layout.BoxScope.(Boolean, () -> Unit) -> Unit, onOpen: (Int) -> Unit) {
    val state = rememberTabBarState()
    GlassBackdrop(Modifier.fillMaxSize(), backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) },
    layer = {
        LazyVerticalGrid(
            GridCells.Fixed(2), Modifier.fillMaxSize().nestedScrollForBar(state),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 68.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 130.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column(Modifier.padding(start = 4.dp, bottom = 4.dp)) {
                    Text("Albums", color = MaterialTheme.colorScheme.onSurface, fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
                    Text("${Albums.size} albums · ${Albums.sumOf { it.count }} photos", color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(500))
                }
            }
            items(Albums) { album -> AlbumCard(album) { onOpen(album.cover) } }
        }
    }) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) { bar(state.minimized) { state.minimized = false } }
    }
}

@Composable
private fun AlbumCard(album: Album, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlassCard(Modifier.fillMaxWidth().aspectRatio(1f), onClick = onClick, shape = SeaGlassShape.Pane(28.dp)) {
            Picture(album.cover, Modifier.fillMaxSize().padding(8.dp).clip(RoundedCornerShape(20.dp)), RoundedCornerShape(20.dp))
        }
        Column(Modifier.padding(start = 6.dp)) {
            Text(album.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.5.sp, fontWeight = FontWeight(650))
            Text("${album.count} photos", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(500))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoriesTab(bar: @Composable androidx.compose.foundation.layout.BoxScope.(Boolean, () -> Unit) -> Unit, onOpen: (Int) -> Unit) {
    val state = rememberTabBarState()
    GlassBackdrop(Modifier.fillMaxSize(), backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) },
    layer = {
        Column(Modifier.statusBarsPadding().padding(top = 68.dp)) {
            Column(Modifier.padding(start = 20.dp, bottom = 16.dp)) {
                Text("Memories", color = MaterialTheme.colorScheme.onSurface, fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
                Text("Moments from your library", color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(500))
            }
            GlassCarousel(Memories.size, Modifier.fillMaxWidth().height(460.dp), preferredItemWidth = 340.dp) { index -> MemoryCard(Memories[index], onOpen) }
        }
    }) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) { bar(state.minimized) { state.minimized = false } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarouselItemScope.MemoryCard(memory: Memory, onOpen: (Int) -> Unit) {
    Box(
        Modifier.fillMaxSize().maskClip(RoundedCornerShape(28.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onOpen(memory.image) },
    ) {
        Picture(memory.image, Modifier.fillMaxSize())
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
            GlassCaption(Modifier.align(Alignment.TopStart).padding(14.dp)) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(memory.title, fontSize = 16.sp, fontWeight = FontWeight(720), maxLines = 1)
                    Text(memory.caption, color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(550), maxLines = 1)
                }
            }
        }
    }
}

private val ViewerTools = listOf(ToolItem("ios_share", "Share"), ToolItem("favorite", "Favourite"), ToolItem("info", "Info"), ToolItem("delete", "Delete", danger = true))

/** Full-screen photo with a floating clear toolbar and an info sheet. */
@Composable
private fun Viewer(image: Int, onClose: () -> Unit) {
    var favourite by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf(false) }
    val toast = remember { GlassToastState() }
    val scope = rememberCoroutineScope()
    GlassBackdrop(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface), backdrop = { ViewerPhoto(image) }) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
            Box(Modifier.statusBarsPadding().padding(start = 16.dp, top = 8.dp)) { GlassBarLens("arrow_back", onClose, description = "Close") }
            GlassFloatingToolbar(
                ViewerTools, if (favourite) 1 else -1,
                { index ->
                    when (index) {
                        0 -> scope.launch { toast.show("Shared a link to Shore walk", icon = "ios_share") }
                        1 -> favourite = !favourite
                        2 -> info = true
                        else -> scope.launch { toast.show("Moved to Trash", icon = "delete", action = ToastAction("Undo") { toast.dismiss() }) }
                    }
                },
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 26.dp),
            )
        }
        GlassToastHost(toast, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 110.dp))
    }
    BackHandler(info) { info = false }
    if (info) {
        GlassBottomSheet(onDismissRequest = { info = false }) {
            Column(Modifier.padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Shore walk", fontSize = 22.sp, fontWeight = FontWeight(740))
                InfoRow("calendar_today", "Saturday, 10 Oct · 6:52")
                InfoRow("place", "North rocks")
                InfoRow("photo_size_select_large", "4032 × 3024 · 4.2 MB")
            }
        }
    }
}

@Composable
private fun ViewerPhoto(image: Int) {
    if (image < 0) Picture(image, Modifier.fillMaxSize()) else PhotoTile(image, Modifier.fillMaxSize(), zoom = 1.8f)
}

@Composable
private fun InfoRow(icon: String, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        SymbolIcon(icon, size = 22.dp, tint = glassOnSurfaceVariant())
        Text(text, fontSize = 15.sp, fontWeight = FontWeight(550))
    }
}

private fun Modifier.nestedScrollForBar(state: com.floydwiz.seaglass.components.navigation.TabBarState): Modifier =
    this.then(Modifier.nestedScroll(state.connection))
