package com.floydwiz.seaglasssample.showcase.catalogue.c5

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.containers.GlassAlert
import com.floydwiz.seaglass.components.containers.GlassAlertPane
import com.floydwiz.seaglass.components.containers.GlassBottomSheet
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.containers.GlassCarousel
import com.floydwiz.seaglass.components.containers.GlassCaption
import com.floydwiz.seaglass.components.containers.GlassContextMenu
import com.floydwiz.seaglass.components.containers.GlassInsetList
import com.floydwiz.seaglass.components.containers.GlassListRow
import com.floydwiz.seaglass.components.containers.GlassMenu
import com.floydwiz.seaglass.components.containers.GlassMenuDivider
import com.floydwiz.seaglass.components.containers.GlassMenuItem
import com.floydwiz.seaglass.components.containers.GlassSheetPane
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoStrip
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoTile
import com.floydwiz.seaglasssample.showcase.catalogue.SpecimenTable
import com.floydwiz.seaglasssample.showcase.catalogue.TableRow
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels

@Composable
private fun ink() = if (LocalSeaGlassDark.current) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight

@Composable
fun ContainersPage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Cards", "Milk pane · glass action",
            "Cards are milk panes with 28 dp corners and a lit top edge. Anything inset keeps concentric corners: an 8 dp inset photo gets 20 dp. Emphasis cards use tinted frost instead of milk. Selection adds a 2 dp accent ring and a glass check lens rather than a heavier container.",
        ) {
            ThemePanels { dark ->
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Milk", "text · controls", 206.dp, listOf({ TextCard() }, { SwitchCard() })),
                        TableRow("Media", "inset · overlay", 278.dp, listOf({ MediaCard() }, { OverlayCard(dark) })),
                        TableRow("Emphasis", "frost · selected", 160.dp, listOf({ StatsCard() }, { SelectedCard() })),
                    ),
                    cellWidth = 300.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Inset lists", "Milk pane · glass icon tiles · frost row",
            "Lists are inset milk panes with 26 dp corners and hairlines that start at the text. Leading tiles are small tinted glass with 11 dp corners. Rows react without lifting: hover adds a 7% layer, press dips the row into a well, and selection fills it with a frost capsule.",
        ) {
            ThemePanels {
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Settings", "tiles · values · switches", 312.dp, listOf({ SettingsList() })),
                        TableRow("Row states", "rest · hover · pressed · selected · disabled", 312.dp, listOf({ StateList() })),
                    ),
                    cellWidth = 340.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Alerts, sheets and menus", "Milk pane over scrim",
            "Modal surfaces are milk panes that float with a margin: alerts at 34 dp corners, sheets inset 8 dp with 36 dp corners and a well grabber, menus at 24 dp beside a lifted preview. Buttons inside are full-width capsules; the highlighted menu row is a frost capsule.",
        ) {
            ThemePanels { dark ->
                var alert by remember { mutableStateOf(false) }
                var sheet by remember { mutableStateOf(false) }
                var menu by remember { mutableStateOf(false) }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Open the real modals", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GlassButton("Alert", { alert = true }, style = GlassButtonStyle.Frost)
                        GlassButton("Sheet", { sheet = true }, style = GlassButtonStyle.Frost)
                        GlassButton("Menu", { menu = true }, style = GlassButtonStyle.Frost)
                    }
                    if (alert) GlassAlert({ alert = false }, "delete", "Delete 3 photos?", "They move to Trash and are removed for good after 30 days.", "Delete", { alert = false })
                    if (sheet) GlassBottomSheet({ sheet = false }) { ShareSheetContent() }
                    if (menu) GlassContextMenu({ menu = false }, preview = { MenuPreview() }) { MenuItems() }
                }
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Alert", "320 · radius 34", 470.dp, listOf({
                            PhotoStrip(dark, 412.dp, 460.dp, crop = 1) {
                                Scrim(dark)
                                GlassAlertPane("delete", "Delete 3 photos?", "They move to Trash and are removed for good after 30 days.", "Delete", {}, "Cancel", {}, Modifier.offset(46.dp, 80.dp))
                            }
                        })),
                        TableRow("Sheet", "inset 8 · radius 36", 470.dp, listOf({
                            PhotoStrip(dark, 412.dp, 460.dp, crop = 5) {
                                Scrim(dark)
                                GlassSheetPane(Modifier.offset(8.dp, 134.dp).width(396.dp)) { ShareSheetContent() }
                            }
                        })),
                        TableRow("Context menu", "lifted preview + menu", 470.dp, listOf({
                            PhotoStrip(dark, 412.dp, 460.dp, crop = 8) {
                                Scrim(dark)
                                Column(Modifier.offset(56.dp, 32.dp), Arrangement.spacedBy(14.dp)) {
                                    MenuPreview()
                                    GlassMenu(width = 236.dp) { MenuItems() }
                                }
                            }
                        })),
                    ),
                    cellWidth = 412.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Carousel", "Content · Clear controls",
            "Carousel items are content, so they stay unglazed with 28 dp corners that tighten to 20 dp as items shrink off-screen. Controls laid over them are clear: a caption capsule, a lens arrow and page dots in a small well.",
        ) {
            ThemePanels { dark -> CarouselSpecimen(dark) }
        }
    }
}

@Composable
private fun Scrim(dark: Boolean) {
    Box(Modifier.fillMaxSize().background(if (dark) androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.35f) else androidx.compose.ui.graphics.Color(0xFF0C1C24).copy(alpha = 0.22f)))
}

@Composable
private fun TextCard() {
    GlassCard(Modifier.size(290.dp, 196.dp)) {
        Column(Modifier.padding(20.dp)) {
            GlassSurface(GlassFinish.Frost, Modifier.size(40.dp), SeaGlassShape.Lens, reactsTo = emptySet()) { SymbolIcon("waves", size = 22.dp, tint = androidx.compose.material3.LocalContentColor.current) }
            Text("Tide tables", Modifier.padding(top = 12.dp), color = ink(), fontSize = 18.sp, fontWeight = FontWeight(690))
            Text("Low 6:40 · High 12:52. Swell 0.6 m from the south-west.", Modifier.padding(top = 6.dp), color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight(450), lineHeight = 20.sp)
        }
        Row(Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 14.dp), Arrangement.spacedBy(8.dp)) {
            GlassButton("Later", {}, style = GlassButtonStyle.Plain)
            GlassButton("Open", {})
        }
    }
}

@Composable
private fun SwitchCard() {
    var notify by remember { mutableStateOf(true) }
    GlassCard(Modifier.size(290.dp, 196.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                PhotoTile(3, Modifier.size(56.dp), RoundedCornerShape(18.dp))
                Column {
                    Text("Morning alerts", color = ink(), fontSize = 17.sp, fontWeight = FontWeight(690))
                    Text("When the tide drops below 0.4 m", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
            HorizontalDivider(Modifier.padding(top = 16.dp), color = ink().copy(alpha = 0.1f))
            Row(Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 4.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Notify me", color = ink(), fontSize = 15.sp, fontWeight = FontWeight(550))
                GlassSwitch(notify, { notify = it })
            }
            HorizontalDivider(color = ink().copy(alpha = 0.1f))
            Row(Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 4.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Sound", color = ink(), fontSize = 15.sp, fontWeight = FontWeight(550))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Shell", color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    SymbolIcon("chevron_right", size = 20.dp, tint = glassOnSurfaceVariant())
                }
            }
        }
    }
}

@Composable
private fun MediaCard() {
    GlassCard(Modifier.size(290.dp, 268.dp)) {
        PhotoTile(0, Modifier.offset(8.dp, 8.dp).size(274.dp, 170.dp), RoundedCornerShape(20.dp))
        GlassIconButton(
            "favorite", {}, Modifier.offset(232.dp, 18.dp), size = 40.dp, filled = true,
            role = LocalRolePalettes.current[GlassRole.SeaPink], style = GlassButtonStyle.Clear,
        )
        Column(Modifier.offset(20.dp, 190.dp)) {
            Text("Shore walk", color = ink(), fontSize = 17.sp, fontWeight = FontWeight(690))
            Text("24 photos · Saturday", color = glassOnSurfaceVariant(), fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
        }
        GlassIconButton("more_horiz", {}, Modifier.offset(236.dp, 196.dp), style = GlassButtonStyle.Plain, size = 40.dp)
    }
}

@Composable
private fun OverlayCard(dark: Boolean) {
    Box(Modifier.size(290.dp, 268.dp).clip(RoundedCornerShape(28.dp))) {
        com.floydwiz.seaglass.material.GlassBackdrop(Modifier.fillMaxSize(), backdrop = { PhotoTile(7, Modifier.fillMaxSize(), dim = dark) }) {
            GlassCaption(Modifier.offset(8.dp, 188.dp).size(274.dp, 72.dp)) {
                Column(Modifier.padding(start = 16.dp)) {
                    Text("Blue hour", color = ink(), fontSize = 16.sp, fontWeight = FontWeight(690))
                    Text("12 photos · 3 min", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
            GlassSurface(GlassFinish.Glass, Modifier.offset(222.dp, 200.dp).size(48.dp), SeaGlassShape.Lens, onClick = {}) {
                SymbolIcon("play_arrow", size = 28.dp, fill = true, tint = androidx.compose.material3.LocalContentColor.current, contentDescription = "Play")
            }
        }
    }
}

@Composable
private fun StatsCard() {
    val seafoam = LocalRolePalettes.current[GlassRole.Seafoam]
    val fg = glassOnRole(seafoam)
    GlassCard(Modifier.size(290.dp, 150.dp), emphasized = true, role = seafoam) {
        Column(Modifier.padding(20.dp)) {
            Text("This week", color = fg.copy(alpha = 0.8f), fontSize = 13.sp, fontWeight = FontWeight(650))
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("14", color = fg, fontSize = 44.sp, fontWeight = FontWeight(760), letterSpacing = (-1).sp, lineHeight = 48.sp)
                Text("pieces found", Modifier.padding(bottom = 8.dp), color = fg, fontSize = 14.sp, fontWeight = FontWeight(550))
            }
            Text("4 more than last week", Modifier.padding(top = 6.dp), color = fg.copy(alpha = 0.8f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Row(Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 24.dp), Arrangement.spacedBy(4.dp), Alignment.Bottom) {
            listOf(3, 1, 4, 2, 0, 3, 1).forEachIndexed { index, v ->
                Box(Modifier.size(10.dp, (14 + v * 12).dp).clip(RoundedCornerShape(5.dp)).background(fg.copy(alpha = if (index == 6) 0.85f else 0.3f)))
            }
        }
    }
}

@Composable
private fun SelectedCard() {
    GlassCard(Modifier.size(290.dp, 150.dp), selected = true) {
        Row(Modifier.padding(12.dp), Arrangement.spacedBy(4.dp)) {
            listOf(
                RoundedCornerShape(topStart = 16.dp, topEnd = 8.dp, bottomEnd = 8.dp, bottomStart = 16.dp),
                RoundedCornerShape(8.dp),
                RoundedCornerShape(topStart = 8.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 8.dp),
            ).forEachIndexed { index, shape -> PhotoTile(index + 9, Modifier.size(86.dp), shape, zoom = 3f) }
        }
        Row(Modifier.offset(16.dp, 108.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            Text("Low tide finds", color = ink(), fontSize = 15.sp, fontWeight = FontWeight(690))
            Text("3 photos", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SettingsList() {
    val roles = LocalRolePalettes.current
    var dark by remember { mutableStateOf(true) }
    var saver by remember { mutableStateOf(false) }
    GlassInsetList(Modifier.width(340.dp)) {
        GlassListRow("Wi-Fi", onClick = {}, leadingIcon = "wifi", leadingRole = roles[GlassRole.Aqua], trailing = { Value("Shoreline") })
        GlassListRow("Bluetooth", onClick = {}, leadingIcon = "bluetooth", leadingRole = roles[GlassRole.Cobalt], trailing = { Value("On") })
        GlassListRow("Notifications", onClick = {}, leadingIcon = "notifications", leadingRole = roles[GlassRole.SeaPink], trailing = { Chevron() })
        GlassListRow("Dark theme", leadingIcon = "dark_mode", leadingRole = roles[GlassRole.Lavender], trailing = { GlassSwitch(dark, { dark = it }) })
        GlassListRow("Battery saver", leadingIcon = "battery_saver", leadingRole = roles[GlassRole.Kelly], divider = false, trailing = { GlassSwitch(saver, { saver = it }) })
    }
}

@Composable
private fun Value(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = glassOnSurfaceVariant(), fontSize = 14.5.sp, fontWeight = FontWeight.Medium)
        Chevron()
    }
}

@Composable
private fun Chevron() = SymbolIcon("chevron_right", size = 20.dp, tint = glassOnSurfaceVariant())

@Composable
private fun StateList() {
    val mono = FontFamily.Monospace
    GlassInsetList(Modifier.width(340.dp)) {
        fun supporting(text: String) = text
        GlassListRow("Rest", onClick = {}, leadingSymbol = "inbox", supporting = supporting("state.rest"))
        CompositionLocalProvider(LocalForcedGlassState provides GlassState.Hover) { GlassListRow("Hover", onClick = {}, leadingSymbol = "label", supporting = "layer 7%") }
        CompositionLocalProvider(LocalForcedGlassState provides GlassState.Press) { GlassListRow("Pressed", onClick = {}, leadingSymbol = "schedule", supporting = "well dip") }
        GlassListRow("Selected", onClick = {}, leadingSymbol = "star", supporting = "frost row", selected = true, trailing = { SymbolIcon("check", size = 22.dp, tint = androidx.compose.material3.LocalContentColor.current) })
        GlassListRow("Disabled", leadingSymbol = "archive", supporting = "content 38%", enabled = false, divider = false)
    }
}

@Composable
private fun ShareSheetContent() {
    Column(Modifier.padding(horizontal = 22.dp).padding(top = 10.dp, bottom = 16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Share", color = ink(), fontSize = 18.sp, fontWeight = FontWeight(720))
            GlassIconButton("close", {}, style = GlassButtonStyle.Clear, size = 34.dp, iconScale = 0.59f)
        }
        Row(Modifier.padding(top = 12.dp).fillMaxWidth(), Arrangement.SpaceBetween) {
            listOf("Maya", "Jun", "Ines", "Theo").forEachIndexed { index, name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PhotoTile(index + 2, Modifier.size(52.dp), RoundedCornerShape(26.dp), zoom = 4f)
                    Text(name, color = ink(), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 14.dp), color = ink().copy(alpha = 0.1f))
        GlassListRow("Copy link", leadingSymbol = "link", divider = false, onClick = {})
        CompositionLocalProvider(LocalForcedGlassState provides GlassState.Press) { GlassListRow("Add to album", leadingSymbol = "photo_album", divider = false, onClick = {}) }
        GlassListRow("Save to device", leadingSymbol = "download", divider = false, onClick = {})
    }
}

@Composable
private fun MenuPreview() {
    PhotoTile(5, Modifier.size(300.dp, 200.dp).shadow(22.dp, RoundedCornerShape(24.dp), clip = false), RoundedCornerShape(24.dp), zoom = 3f)
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.MenuItems() {
    GlassMenuItem("Copy", {}, icon = "content_copy")
    GlassMenuItem("Share", {}, icon = "ios_share")
    GlassMenuItem("Favorite", {}, icon = "favorite", highlighted = true)
    GlassMenuDivider()
    GlassMenuItem("Delete", {}, icon = "delete", danger = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarouselSpecimen(dark: Boolean) {
    Box(Modifier.size(412.dp, 210.dp).clip(RoundedCornerShape(28.dp))) {
        GlassCarousel(
            itemCount = 6,
            modifier = Modifier.fillMaxSize(),
            overlay = {
                GlassCaption(Modifier.offset(14.dp, 14.dp).height(36.dp)) {
                    Row(Modifier.padding(horizontal = 14.dp), Arrangement.spacedBy(6.dp), Alignment.CenterVertically) {
                        SymbolIcon("photo_library", size = 18.dp, tint = ink())
                        Text("Shore walk", color = ink(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                GlassIconButton("chevron_right", {}, Modifier.align(Alignment.CenterStart).offset(242.dp, 0.dp), size = 44.dp, iconScale = 0.55f)
            },
        ) { index ->
            PhotoTile(index * 2, Modifier.fillMaxSize().maskClip(RoundedCornerShape(28.dp)), zoom = 2.6f, dim = dark)
        }
    }
}
