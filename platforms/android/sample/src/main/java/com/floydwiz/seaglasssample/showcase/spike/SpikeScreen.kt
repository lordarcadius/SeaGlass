package com.floydwiz.seaglasssample.showcase.spike

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassLayers
import com.floydwiz.seaglass.material.glassContentColor
import com.floydwiz.seaglass.material.seaGlass
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.control.ControlState
import com.floydwiz.seaglasssample.perf.FrameSummary
import com.floydwiz.seaglasssample.perf.rememberFrameStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.math.abs

private const val Tag = "SGPerf"
private const val ScrollSpeedDpPerSecond = 900f

private val Crops = listOf(
    130 to 100, 520 to 130, 780 to 270, 250 to 620, 950 to 430, 1030 to 770, 330 to 350,
    250 to 920, 670 to 930, 1050 to 110, 1000 to 1150, 430 to 1130, 720 to 50, 630 to 600,
)

private class SweepResult(val name: String, val summary: FrameSummary)

private val LayerConfigs: List<Pair<String, GlassLayers>> = buildList {
    add("all layers" to GlassLayers.All)
    add("no shadow" to GlassLayers.All.copy(shadow = false))
    add("no caustic" to GlassLayers.All.copy(caustic = false))
    add("no contact" to GlassLayers.All.copy(contact = false))
    add("no body (blur+tint)" to GlassLayers.All.copy(body = false))
    add("no light" to GlassLayers.All.copy(light = false))
    add("no grain" to GlassLayers.All.copy(grain = false))
    add("no volume" to GlassLayers.All.copy(volume = false))
    add("no rim" to GlassLayers.All.copy(rim = false))
    add("all layers off" to GlassLayers.None)
}

/**
 * Performance spike: glass over a scrolling photo grid with live frame statistics.
 * - Auto-scroll drives the grid at a fixed speed so runs are comparable.
 * - The sweep runs [LayerConfigs] one after another, 1.5 s warm-up then 4 s measured, and logs each to `SGPerf`.
 */
@Composable
fun SpikeScreen(controls: ControlState, contentPadding: PaddingValues) {
    val stats = rememberFrameStats()
    val gridState = rememberLazyGridState()
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()

    var autoScroll by remember { mutableStateOf(false) }
    var live by remember { mutableStateOf(stats.summary(120)) }
    var running by remember { mutableStateOf<String?>(null) }
    var results by remember { mutableStateOf<List<SweepResult>>(emptyList()) }
    val refreshRate = context.display.refreshRate

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            live = stats.summary(120)
        }
    }

    LaunchedEffect(autoScroll) {
        if (!autoScroll) return@LaunchedEffect
        var last = 0L
        var direction = 1f
        while (true) {
            androidx.compose.runtime.withFrameNanos { now ->
                if (last != 0L) {
                    val delta = direction * ScrollSpeedDpPerSecond * density * (now - last) / 1e9f
                    if (abs(gridState.dispatchRawDelta(delta)) < abs(delta) / 2f) direction = -direction
                }
                last = now
            }
        }
    }

    GlassBackdrop(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        backdrop = { PhotoGrid(gridState) },
        content = {
            FloatingControls()
            StatsPanel(
                live = live,
                refreshRate = refreshRate,
                running = running,
                results = results,
                autoScroll = autoScroll,
                onToggleScroll = { autoScroll = !autoScroll },
                onSweep = {
                    if (running == null) {
                        scope.launch {
                            val original = controls.layers
                            autoScroll = true
                            val collected = mutableListOf<SweepResult>()
                            for ((name, layers) in LayerConfigs) {
                                controls.layers = layers
                                running = name
                                delay(1500)
                                stats.reset()
                                delay(4000)
                                val summary = stats.summary()
                                Log.i(Tag, "config=$name $summary")
                                collected += SweepResult(name, summary)
                            }
                            controls.layers = original
                            autoScroll = false
                            running = null
                            results = collected
                            writeResults(context.filesDir, refreshRate, collected)
                        }
                    }
                },
            )
            FloatingTabBar()
        },
    )
}

@Composable
private fun PhotoGrid(state: LazyGridState) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        state = state,
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(240) { index ->
            val (cx, cy) = Crops[index % Crops.size]
            Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(10.dp))) {
                Image(
                    painter = painterResource(R.drawable.seaglass_photo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        val zoom = 2.2f + (index % 3) * 0.6f
                        scaleX = zoom
                        scaleY = zoom
                        transformOrigin = TransformOrigin(cx / 1200f, cy / 1200f)
                    },
                )
            }
        }
    }
}

@Composable
private fun BoxScope.FloatingControls() {
    Column(
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SpikeButton(GlassFinish.Glass, "Send", "send", fill = true)
            SpikeButton(GlassFinish.Frost, "Save", "bookmark", fill = true)
            SpikeButton(GlassFinish.Clear, "Share", "ios_share")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            SpikePane(GlassFinish.Milk, 168.dp, 64.dp, SeaGlassShape.Pane(24.dp)) { content ->
                Text("Tide tables", color = content, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            SpikePane(GlassFinish.Well, 170.dp, 46.dp, SeaGlassShape.Capsule) { content ->
                SymbolIcon("search", size = 20.dp, tint = content)
                Spacer(Modifier.width(8.dp))
                Text("Search", color = content, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun BoxScope.FloatingTabBar() {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 14.dp)
            .size(width = 316.dp, height = 62.dp)
            .seaGlass(GlassFinish.Clear, adaptive = true)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val dark = LocalSeaGlassDark.current
        val role = LocalGlassRole.current
        val tabs = listOf("photo_library" to "Photos", "photo_album" to "Albums", "auto_awesome_motion" to "Memories", "search" to "Search")
        tabs.forEachIndexed { index, (icon, label) ->
            val current = index == 0
            val finish = if (current) GlassFinish.Frost else GlassFinish.Clear
            val color = glassContentColor(finish, role, dark)
            val tabModifier = Modifier.size(width = 76.dp, height = 54.dp)
            Column(
                modifier = if (current) tabModifier.seaGlass(GlassFinish.Frost, SeaGlassShape.Lens) else tabModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                SymbolIcon(icon, size = 24.dp, fill = current, tint = color)
                Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SpikeButton(finish: GlassFinish, label: String, icon: String, fill: Boolean = false) {
    val color = glassContentColor(finish, LocalGlassRole.current, LocalSeaGlassDark.current)
    Row(
        modifier = Modifier.size(width = 104.dp, height = 44.dp).seaGlass(finish, adaptive = true).padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SymbolIcon(icon, size = 20.dp, fill = fill, tint = color)
        Spacer(Modifier.width(8.dp))
        Text(label, color = color, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SpikePane(
    finish: GlassFinish,
    width: Dp,
    height: Dp,
    shape: SeaGlassShape,
    content: @Composable androidx.compose.foundation.layout.RowScope.(Color) -> Unit,
) {
    val color = glassContentColor(finish, LocalGlassRole.current, LocalSeaGlassDark.current)
    Row(
        modifier = Modifier.size(width, height).seaGlass(finish, shape).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) { content(color) }
}

@Composable
private fun BoxScope.StatsPanel(
    live: FrameSummary,
    refreshRate: Float,
    running: String?,
    results: List<SweepResult>,
    autoScroll: Boolean,
    onToggleScroll: () -> Unit,
    onSweep: () -> Unit,
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 8.dp, bottom = 92.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.72f))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val mono = androidx.compose.ui.text.TextStyle(fontSize = 10.5.sp, color = Color.White, lineHeight = 13.sp)
        Text(
            "%.0f Hz · total p50 %.1f · p95 %.1f · p99 %.1f ms · ui p95 %.1f · missed %.0f%%".format(
                refreshRate, live.totalP50Ms, live.totalP95Ms, live.totalP99Ms, live.uiP95Ms, live.missedPercent,
            ),
            style = mono,
        )
        if (running != null) Text("sweep: $running", style = mono)
        results.forEach { Text("${it.name}: p50 %.1f p95 %.1f ui %.1f miss %.0f%%".format(it.summary.totalP50Ms, it.summary.totalP95Ms, it.summary.uiP95Ms, it.summary.missedPercent), style = mono) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onToggleScroll, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                Text(if (autoScroll) "Stop scroll" else "Auto-scroll", fontSize = 11.sp)
            }
            Button(onClick = onSweep, enabled = running == null, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                Text("Layer sweep", fontSize = 11.sp)
            }
        }
    }
}

private fun writeResults(dir: File, refreshRate: Float, results: List<SweepResult>) {
    val json = JSONObject().apply {
        put("device", "${Build.MANUFACTURER} ${Build.MODEL}")
        put("sdk", Build.VERSION.SDK_INT)
        put("emulator", Build.FINGERPRINT.contains("generic") || Build.MODEL.contains("sdk", ignoreCase = true))
        put("refreshRateHz", refreshRate.toDouble())
        put(
            "results",
            JSONArray(
                results.map {
                    JSONObject().apply {
                        put("config", it.name)
                        put("frames", it.summary.frames)
                        put("totalP50Ms", it.summary.totalP50Ms.toDouble())
                        put("totalP95Ms", it.summary.totalP95Ms.toDouble())
                        put("totalP99Ms", it.summary.totalP99Ms.toDouble())
                        put("totalMaxMs", it.summary.totalMaxMs.toDouble())
                        put("uiP95Ms", it.summary.uiP95Ms.toDouble())
                        put("missedPercent", it.summary.missedPercent.toDouble())
                        put("jankPercent", it.summary.jankPercent.toDouble())
                    }
                },
            ),
        )
    }
    File(dir, "perf-m1.json").writeText(json.toString(2))
    Log.i(Tag, "wrote perf-m1.json")
}
