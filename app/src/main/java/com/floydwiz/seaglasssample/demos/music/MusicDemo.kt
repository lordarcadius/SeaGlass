package com.floydwiz.seaglasssample.demos.music

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.containers.GlassInsetList
import com.floydwiz.seaglass.components.fab.FabSize
import com.floydwiz.seaglass.components.fab.GlassFab
import com.floydwiz.seaglass.components.feedback.GlassToastHost
import com.floydwiz.seaglass.components.feedback.GlassToastState
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.input.GlassSearchField
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.components.navigation.GlassTabBar
import com.floydwiz.seaglass.components.navigation.TabItem
import com.floydwiz.seaglass.components.navigation.rememberTabBarState
import com.floydwiz.seaglass.components.selection.GlassPillSlider
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.SegmentedOption
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalAdaptiveTint
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.demos.AboutDemoSheet
import com.floydwiz.seaglasssample.demos.DemoChrome
import com.floydwiz.seaglasssample.demos.DemoInfo
import com.floydwiz.seaglasssample.demos.EmptyState
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoTile
import com.floydwiz.seaglasssample.showcase.screens.Picture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private class Song(val title: String, val artist: String, val album: String, val seconds: Int, val cover: Int)

private val Songs = listOf(
    Song("Low tide at six", "The Salt Sisters", "Shoreline", 214, 9), Song("Kelp forest", "Jun Park Trio", "Understory", 187, 7),
    Song("Harbour bells", "Maya & the Moorings", "Slack water", 241, 5), Song("Amber jar", "The Salt Sisters", "Shoreline", 198, 4),
    Song("North rocks", "Jun Park Trio", "Understory", 226, 3), Song("Blue hour", "Maya & the Moorings", "Slack water", 203, 10),
    Song("Frosted on both sides", "Ines & Theo", "Winter swell", 175, 1), Song("Second groyne", "Ines & Theo", "Winter swell", 252, 6),
)

private val Tabs = listOf(TabItem("library_music", "Library"), TabItem("search", "Search"), TabItem("radio", "Radio"))
private val Modes = listOf("Songs", "Albums", "Artists").map { SegmentedOption(it) }

private val Info = DemoInfo(
    "Music player",
    "A library behind a floating tab bar, a mini player that is part of the bar, and a now-playing screen over blurred cover art.",
    listOf(
        "Library list on milk" to "R4: content sits on milk.",
        "Pill progress slider and volume pill" to "R2 and the fill finish; the pearl thumb becomes a lens while dragged.",
        "Clear transport lenses over the art" to "Floating chrome over imagery is clear glass with adaptive tint.",
        "Glass play button (80 dp pebble)" to "R1: the single primary action. The pebble shape belongs to the FAB, so play is a FAB.",
        "Mini player in the tab bar" to "R5: it is part of the bar's clear glass, not a second sheet; it sits above the bar and between the lenses when the bar is minimized.",
        "Press on the transport lenses" to "R6: scale 0.96, silhouette kept.",
        "Ink toast" to "R4: queue changes are passing messages.",
    ),
)

private fun time(seconds: Int) = "%d:%02d".format(seconds / 60, seconds % 60)

/** Music player demo. */
@Composable
fun MusicDemo(onBack: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var mode by remember { mutableIntStateOf(0) }
    var current by remember { mutableIntStateOf(0) }
    var position by remember { mutableFloatStateOf(0.18f) }
    var playing by remember { mutableStateOf(false) }
    var showNow by remember { mutableStateOf(false) }
    var about by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val toast = remember { GlassToastState() }
    val scope = rememberCoroutineScope()
    val tabState = rememberTabBarState()
    val song = Songs[current]
    BackHandler(showNow) { showNow = false }

    LaunchedEffect(playing, current) {
        while (playing) {
            delay(1000)
            position += 1f / song.seconds
            if (position >= 1f) { current = (current + 1) % Songs.size; position = 0f }
        }
    }
    fun skip(delta: Int) { current = (current + delta + Songs.size) % Songs.size; position = 0f }
    fun play(index: Int) { current = index; position = 0f; playing = true }
    fun queue(s: Song) = scope.launch { toast.show("Added “${s.title}” to the queue", icon = "playlist_add") }

    Box(Modifier.fillMaxSize()) {
        GlassBackdrop(
            Modifier.fillMaxSize(),
            backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) },
            layer = {
                when (tab) {
                    0 -> Library(mode, { mode = it }, current, playing, tabState.connection, ::play, ::queue)
                    1 -> SearchTab(query, { query = it }, ::play, ::queue)
                    else -> RadioTab()
                }
            },
        ) {
            CompositionLocalProvider(LocalAdaptiveTint provides true) {
                Column(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!tabState.minimized) MiniPlayer(song, playing, { playing = !playing }, { showNow = true }, Modifier.padding(horizontal = 16.dp))
                    GlassTabBar(
                        Tabs, tab, { tab = it }, minimized = tabState.minimized, tabWidth = 92.dp, onSearch = null, onExpand = { tabState.minimized = false },
                        accessory = { MiniPlayerContent(song, playing, { playing = !playing }, { showNow = true }) },
                    )
                }
            }
            DemoChrome(onBack, { about = true }, Modifier.align(Alignment.TopStart))
            GlassToastHost(toast, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 170.dp))
        }
        if (showNow) NowPlaying(song, playing, position, { position = it }, { playing = !playing }, { skip(1) }, { skip(-1) }, { showNow = false }, toast)
    }
    if (about) AboutDemoSheet(Info) { about = false }
}

@Composable
private fun MiniPlayer(song: Song, playing: Boolean, onToggle: () -> Unit, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    GlassSurface(GlassFinish.Clear, modifier.fillMaxWidth().height(60.dp), SeaGlassShape.Capsule, onClick = onOpen, contentAlignment = Alignment.CenterStart) {
        MiniPlayerContent(song, playing, onToggle, onOpen)
    }
}

@Composable
private fun MiniPlayerContent(song: Song, playing: Boolean, onToggle: () -> Unit, onOpen: () -> Unit) {
    Row(Modifier.padding(start = 8.dp, end = 6.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onOpen), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
        PhotoTile(song.cover, Modifier.size(44.dp), RoundedCornerShape(16.dp), zoom = 3f)
        Column(Modifier.weight(1f, fill = false).width(150.dp)) {
            Text(song.title, fontSize = 14.5.sp, fontWeight = FontWeight(650), maxLines = 1)
            Text(song.artist, color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(500), maxLines = 1)
        }
        GlassIconButton(if (playing) "pause" else "play_arrow", onToggle, size = 44.dp, contentDescription = if (playing) "Pause" else "Play")
    }
}

@Composable
private fun Library(
    mode: Int, onMode: (Int) -> Unit, current: Int, playing: Boolean, scroll: androidx.compose.ui.input.nestedscroll.NestedScrollConnection,
    onPlay: (Int) -> Unit, onQueue: (Song) -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize().nestedScroll(scroll), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 230.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(Modifier.statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Library", fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
                GlassSegmentedControl(Modes, mode, onMode, segmentWidth = 100.dp)
            }
        }
        item {
            when (mode) {
                0 -> GlassInsetList(Modifier.fillMaxWidth()) {
                    Songs.forEachIndexed { index, song -> SongRow(song, index == current, playing && index == current, index < Songs.lastIndex, { onPlay(index) }, { onQueue(song) }) }
                }
                1 -> GlassInsetList(Modifier.fillMaxWidth()) {
                    val albums = Songs.groupBy { it.album }.entries.toList()
                    albums.forEachIndexed { index, (album, songs) -> GroupRow(album, "${songs.first().artist} · ${songs.size} songs", songs.first().cover, index < albums.lastIndex) { onPlay(Songs.indexOf(songs.first())) } }
                }
                else -> GlassInsetList(Modifier.fillMaxWidth()) {
                    val artists = Songs.groupBy { it.artist }.entries.toList()
                    artists.forEachIndexed { index, (artist, songs) -> GroupRow(artist, "${songs.size} songs", songs.first().cover, index < artists.lastIndex) { onPlay(Songs.indexOf(songs.first())) } }
                }
            }
        }
    }
}

@Composable
private fun SongRow(song: Song, active: Boolean, playing: Boolean, divider: Boolean, onClick: () -> Unit, onQueue: () -> Unit) {
    Column(Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
            PhotoTile(song.cover, Modifier.size(52.dp), RoundedCornerShape(14.dp), zoom = 3f)
            Column(Modifier.weight(1f)) {
                Text(song.title, fontSize = 15.5.sp, fontWeight = FontWeight(if (active) 700 else 600), color = if (active) glassAccent() else MaterialTheme.colorScheme.onSurface, maxLines = 1)
                Text("${song.artist} · ${time(song.seconds)}", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(500), maxLines = 1)
            }
            if (playing) SymbolIcon("graphic_eq", size = 22.dp, tint = glassAccent())
            GlassIconButton("playlist_add", onQueue, style = com.floydwiz.seaglass.components.buttons.GlassButtonStyle.Plain, size = 40.dp, contentDescription = "Add to queue")
        }
        if (divider) androidx.compose.material3.HorizontalDivider(Modifier.padding(start = 78.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    }
}

@Composable
private fun GroupRow(title: String, subtitle: String, cover: Int, divider: Boolean, onClick: () -> Unit) {
    Column(Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
            PhotoTile(cover, Modifier.size(52.dp), RoundedCornerShape(14.dp), zoom = 3f)
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 15.5.sp, fontWeight = FontWeight(600), maxLines = 1)
                Text(subtitle, color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(500), maxLines = 1)
            }
            SymbolIcon("chevron_right", size = 22.dp, tint = glassOnSurfaceVariant())
        }
        if (divider) androidx.compose.material3.HorizontalDivider(Modifier.padding(start = 78.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    }
}

@Composable
private fun SearchTab(query: String, onQuery: (String) -> Unit, onPlay: (Int) -> Unit, onQueue: (Song) -> Unit) {
    val results = Songs.withIndex().filter { query.isBlank() || it.value.title.contains(query, true) || it.value.artist.contains(query, true) || it.value.album.contains(query, true) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 230.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(Modifier.statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Search", fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
                GlassSearchField(query, onQuery, Modifier.fillMaxWidth(), placeholder = "Songs, artists, albums")
            }
        }
        item {
            if (results.isEmpty()) EmptyState("music_off", "Nothing washed up", "No song matches “$query”.")
            else GlassInsetList(Modifier.fillMaxWidth()) {
                results.forEachIndexed { i, (index, song) -> SongRow(song, false, false, i < results.lastIndex, { onPlay(index) }, { onQueue(song) }) }
            }
        }
    }
}

private val Stations = listOf("Low Tide FM" to "Slow songs for the shoreline", "Harbour Radio" to "Bells, gulls and a little jazz", "Kelp Club" to "Deep cuts, played quietly")

@Composable
private fun RadioTab() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 230.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Radio", fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp, modifier = Modifier.statusBarsPadding().padding(start = 4.dp, top = 12.dp)) }
        items(Stations.size) { index ->
            Box(Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(28.dp))) {
                Picture(-(index + 1), Modifier.fillMaxSize())
                CompositionLocalProvider(LocalAdaptiveTint provides true) {
                    com.floydwiz.seaglass.components.containers.GlassCaption(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                            Text(Stations[index].first, fontSize = 16.sp, fontWeight = FontWeight(700))
                            Text(Stations[index].second, color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(550))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NowPlaying(
    song: Song, playing: Boolean, position: Float, onPosition: (Float) -> Unit, onToggle: () -> Unit, onNext: () -> Unit, onPrevious: () -> Unit,
    onClose: () -> Unit, toast: GlassToastState,
) {
    val dark = LocalSeaGlassDark.current
    var volume by remember { mutableFloatStateOf(0.6f) }
    var shuffle by remember { mutableStateOf(false) }
    var repeat by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    GlassBackdrop(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        backdrop = {
            Box(Modifier.fillMaxSize().clipToBounds()) {
                Box(Modifier.fillMaxSize().blur(36.dp)) { PhotoTile(song.cover, Modifier.fillMaxSize(), zoom = 2.2f, dim = dark) }
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(alpha = if (dark) 0.35f else 0.25f)))
            }
        },
    ) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
            Box(Modifier.statusBarsPadding().padding(start = 16.dp, top = 8.dp)) { GlassBarLens("expand_more", onClose, description = "Close") }
            Column(Modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(96.dp))
                PhotoTile(song.cover, Modifier.size(300.dp).clip(RoundedCornerShape(40.dp)), RoundedCornerShape(40.dp), zoom = 3f)
                Spacer(Modifier.height(26.dp))
                Text(song.title, fontSize = 26.sp, fontWeight = FontWeight(740), textAlign = TextAlign.Center)
                Text("${song.artist} · ${song.album}", color = glassOnSurfaceVariant(), fontSize = 15.sp, fontWeight = FontWeight(550), textAlign = TextAlign.Center)
                Spacer(Modifier.height(22.dp))
                GlassPillSlider(position, onPosition, "graphic_eq", width = 330.dp, height = 44.dp, endIcon = "timer")
                Row(Modifier.width(330.dp).padding(top = 6.dp), Arrangement.SpaceBetween) {
                    Text(time((position * song.seconds).toInt()), color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(600))
                    Text("-" + time(song.seconds - (position * song.seconds).toInt()), color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(600))
                }
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
                    GlassIconButton("shuffle", { shuffle = !shuffle; scope.launch { toast.show(if (shuffle) "Shuffle on" else "Shuffle off", icon = "shuffle") } }, size = 56.dp, filled = shuffle, contentDescription = "Shuffle")
                    GlassIconButton("skip_previous", onPrevious, size = 56.dp, contentDescription = "Previous")
                    GlassFab(if (playing) "pause" else "play_arrow", onToggle, size = FabSize.Large, contentDescription = if (playing) "Pause" else "Play")
                    GlassIconButton("skip_next", onNext, size = 56.dp, contentDescription = "Next")
                    GlassIconButton("repeat", { repeat = !repeat; scope.launch { toast.show(if (repeat) "Repeat on" else "Repeat off", icon = "repeat") } }, size = 56.dp, filled = repeat, contentDescription = "Repeat")
                }
                Spacer(Modifier.height(22.dp))
                GlassPillSlider(volume, { volume = it }, "volume_up", width = 300.dp, height = 40.dp)
            }
            GlassToastHost(toast, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 8.dp))
        }
    }
}
