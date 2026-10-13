package com.floydwiz.seaglasssample.demos.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.containers.GlassBottomSheet
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.containers.GlassInsetList
import com.floydwiz.seaglass.components.containers.GlassListRow
import com.floydwiz.seaglass.components.feedback.GlassToastHost
import com.floydwiz.seaglass.components.feedback.GlassToastState
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.navigation.GlassScopeBar
import com.floydwiz.seaglass.components.navigation.ScopeItem
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.components.selection.GlassPillSlider
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglasssample.demos.AboutDemoSheet
import com.floydwiz.seaglasssample.demos.DemoChrome
import com.floydwiz.seaglasssample.demos.DemoInfo
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class Kind(val icon: String, val unit: String, val role: GlassRole) {
    Light("lightbulb", "brightness", GlassRole.Honey), Fan("mode_fan", "speed", GlassRole.Aqua), Speaker("speaker", "volume", GlassRole.Lavender),
    Blind("blinds", "open", GlassRole.Cornflower), Plug("power", "", GlassRole.Seafoam),
}

private class Device(val id: Int, val name: String, val room: Int, val kind: Kind, val on: Boolean, val level: Float, val online: Boolean = true)

private val Rooms = listOf("Living", "Kitchen", "Bedroom", "Studio")
private val Scenes = listOf("Evening", "Away", "Movie", "Sleep")

private val Info = DemoInfo(
    "Smart home",
    "Rooms as scope tabs, device tiles with switches, a sheet with a vertical pill slider per device, and scenes. Every change shows an ink toast.",
    listOf(
        "Rooms as scope tabs (clear capsule, frost lens)" to "R3: the current room is the frost lens; the beads count devices that are on.",
        "Device tiles on milk, r 28" to "R4: content sits on milk.",
        "Switch on each tile" to "R2 and R3: off rests in a well, on lifts to liquid glass.",
        "Vertical pill slider (64 × 168, symbol inside the liquid)" to "R2 and the fill finish.",
        "Scene picker as filter chips" to "R3.",
        "Ink toast on every change" to "R4: passing messages are ink.",
        "Offline devices" to "R1: dry finish, content at 38%.",
    ),
)

private fun seed() = listOf(
    Device(1, "Ceiling light", 0, Kind.Light, true, 0.7f), Device(2, "Floor lamp", 0, Kind.Light, false, 0.4f), Device(3, "Shelf speaker", 0, Kind.Speaker, true, 0.35f),
    Device(4, "Window blind", 0, Kind.Blind, false, 0.2f), Device(5, "Counter lights", 1, Kind.Light, true, 0.9f), Device(6, "Extractor fan", 1, Kind.Fan, false, 0.5f),
    Device(7, "Coffee plug", 1, Kind.Plug, false, 0f), Device(8, "Bedside lamp", 2, Kind.Light, false, 0.3f), Device(9, "Ceiling fan", 2, Kind.Fan, true, 0.4f),
    Device(10, "Night blind", 2, Kind.Blind, false, 0f, online = false), Device(11, "Studio lights", 3, Kind.Light, true, 0.6f), Device(12, "Desk speaker", 3, Kind.Speaker, false, 0.5f),
    Device(13, "Kiln plug", 3, Kind.Plug, false, 0f, online = false),
)

/** Smart home demo. */
@Composable
fun SmartHomeDemo(onBack: () -> Unit) {
    val devices = remember { mutableStateListOf<Device>().apply { addAll(seed()) } }
    var room by remember { mutableIntStateOf(0) }
    var scene by remember { mutableIntStateOf(-1) }
    var detail by remember { mutableStateOf<Int?>(null) }
    var about by remember { mutableStateOf(false) }
    val toast = remember { GlassToastState() }
    val scope = rememberCoroutineScope()
    val roles = LocalRolePalettes.current

    fun update(id: Int, change: (Device) -> Device, message: (Device) -> String) {
        val at = devices.indexOfFirst { it.id == id }
        val next = change(devices[at])
        devices[at] = next
        scope.launch { toast.show(message(next), icon = next.kind.icon, role = next.kind.role) }
    }
    fun applyScene(index: Int) {
        scene = index
        val on: (Device) -> Boolean = when (index) {
            0 -> { d -> d.kind == Kind.Light && d.room in 0..1 || d.kind == Kind.Speaker && d.room == 0 }
            1 -> { _ -> false }
            2 -> { d -> d.kind == Kind.Speaker && d.room == 0 || d.kind == Kind.Blind && d.room == 0 }
            else -> { d -> d.kind == Kind.Fan && d.room == 2 }
        }
        devices.indices.forEach { i -> val d = devices[i]; if (d.online) devices[i] = Device(d.id, d.name, d.room, d.kind, on(d), d.level, true) }
        scope.launch { toast.show("${Scenes[index]}: ${devices.count { it.on }} devices on", icon = "auto_awesome", role = GlassRole.Lavender) }
    }

    val inRoom = devices.filter { it.room == room }
    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) },
        layer = {
            LazyVerticalGrid(
                GridCells.Fixed(2), Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column(Modifier.statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Home", fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            GlassScopeBar(Rooms.mapIndexed { i, name -> ScopeItem(name, devices.count { it.room == i && it.on }.takeIf { it > 0 }) }, room, { room = it })
                        }
                        Text("Scenes", fontSize = 13.sp, fontWeight = FontWeight(650), color = glassOnSurfaceVariant(), modifier = Modifier.padding(start = 4.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
                            Scenes.forEachIndexed { i, name -> GlassFilterChip(scene == i, { applyScene(i) }, name) }
                        }
                    }
                }
                items(inRoom, key = { it.id }) { device ->
                    DeviceTile(device, { on -> update(device.id, { d -> Device(d.id, d.name, d.room, d.kind, on, d.level, d.online) }) { d -> "${d.name} ${if (d.on) "on" else "off"}" } }, { detail = device.id })
                }
            }
        },
    ) {
        DemoChrome(onBack, { about = true }, Modifier.align(Alignment.TopStart))
        GlassToastHost(toast, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp))
    }

    detail?.let { id ->
        val device = devices.first { it.id == id }
        GlassBottomSheet(onDismissRequest = { detail = null }) {
            Row(Modifier.fillMaxWidth().padding(24.dp), Arrangement.spacedBy(24.dp), Alignment.Top) {
                GlassPillSlider(
                    device.level, { v -> update(id, { d -> Device(d.id, d.name, d.room, d.kind, d.on || v > 0f, v, d.online) }) { d -> "${d.name} ${d.kind.unit} ${(d.level * 100).roundToInt()}%" } },
                    device.kind.icon, vertical = true, enabled = device.online, role = roles[device.kind.role],
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(device.name, fontSize = 22.sp, fontWeight = FontWeight(740))
                    Text("${Rooms[device.room]} · ${if (device.online) (if (device.on) "On" else "Off") else "Offline"}", color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(550))
                    GlassInsetList(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        GlassListRow("Schedule", leadingSymbol = "schedule", supporting = "Sunset to 23:00", divider = true)
                        GlassListRow("Automation", leadingSymbol = "auto_awesome", supporting = "Follows scenes", divider = false)
                    }
                }
            }
        }
    }
    if (about) AboutDemoSheet(Info) { about = false }
}

@Composable
private fun DeviceTile(device: Device, onToggle: (Boolean) -> Unit, onOpen: () -> Unit) {
    val roles = LocalRolePalettes.current
    GlassCard(Modifier.fillMaxWidth().height(150.dp), onClick = if (device.online) onOpen else null, shape = SeaGlassShape.Pane(28.dp)) {
        Column(Modifier.fillMaxSize().padding(16.dp).alpha(if (device.online) 1f else 0.38f), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                SymbolIcon(device.kind.icon, size = 30.dp, fill = device.on, tint = if (device.on) glassAccent(roles[device.kind.role]) else glassOnSurfaceVariant())
                GlassSwitch(device.on, onToggle, enabled = device.online, role = roles[device.kind.role])
            }
            Column {
                Text(device.name, fontSize = 15.5.sp, fontWeight = FontWeight(650), maxLines = 1)
                Text(
                    when { !device.online -> "Offline"; !device.on -> "Off"; device.kind.unit.isEmpty() -> "On"; else -> "${(device.level * 100).roundToInt()}% ${device.kind.unit}" },
                    color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(550),
                )
            }
        }
    }
}
