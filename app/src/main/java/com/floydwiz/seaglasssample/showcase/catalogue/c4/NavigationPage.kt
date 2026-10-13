package com.floydwiz.seaglasssample.showcase.catalogue.c4

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.floydwiz.seaglass.components.navigation.rememberTabBarState
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoCrops
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.ClusterItem
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.navigation.GlassBarActions
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.components.navigation.GlassFloatingToolbar
import com.floydwiz.seaglass.components.navigation.GlassNavigationRail
import com.floydwiz.seaglass.components.navigation.GlassPageDots
import com.floydwiz.seaglass.components.navigation.GlassScopeBar
import com.floydwiz.seaglass.components.navigation.GlassSidebar
import com.floydwiz.seaglass.components.navigation.GlassTabBar
import com.floydwiz.seaglass.components.navigation.GlassTopBar
import com.floydwiz.seaglass.components.navigation.RailDestination
import com.floydwiz.seaglass.components.navigation.ScopeItem
import com.floydwiz.seaglass.components.navigation.SidebarDestination
import com.floydwiz.seaglass.components.navigation.TabItem
import com.floydwiz.seaglass.components.navigation.ToolItem
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoStrip
import com.floydwiz.seaglasssample.showcase.catalogue.SpecimenTable
import com.floydwiz.seaglasssample.showcase.catalogue.TableRow
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels

private val Tabs = listOf(TabItem("home", "Home"), TabItem("photo_library", "Photos"), TabItem("explore", "Explore"), TabItem("person", "You"))
private val Tools = listOf("crop", "tune", "auto_fix_high", "filter_vintage", "text_fields").map { ToolItem(it, it) }

@Composable
fun NavigationPage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Tab bar", "Clear capsule · Frost lens",
            "The tab bar floats 14 dp above the bottom edge as a clear capsule, with search in its own lens beside it. The current tab is a frost lens. Dragging turns the lens clear and stretches it between tabs, magnifying whatever is under it; on scroll the bar condenses into two lenses around an accessory capsule.",
        ) {
            ThemePanels { dark ->
                var rest by remember { mutableIntStateOf(0) }
                var minimized by remember { mutableStateOf(true) }
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Rest", "clear + frost lens", 140.dp, listOf({
                            PhotoStrip(dark, 412.dp, 132.dp, crop = 7) { GlassTabBar(Tabs, rest, { rest = it }, Modifier.offset(14.dp, 56.dp), onSearch = {}) }
                        })),
                        TableRow("Dragging", "lens turns clear\nand magnifies", 140.dp, listOf({
                            PhotoStrip(dark, 412.dp, 132.dp, crop = 7) {
                                CompositionLocalProvider(com.floydwiz.seaglass.material.LocalForcedGlassState provides GlassState.Press) {
                                    GlassTabBar(Tabs, 1, {}, Modifier.offset(14.dp, 56.dp), onSearch = {})
                                }
                            }
                        })),
                        TableRow("Minimized", "on scroll down", 140.dp, listOf({
                            PhotoStrip(dark, 412.dp, 132.dp, crop = 7) {
                                GlassTabBar(
                                    Tabs, 0, {}, Modifier.offset(14.dp, 62.dp), minimized = minimized, onSearch = {}, onExpand = { minimized = !minimized },
                                    accessory = { UploadAccessory() },
                                )
                            }
                        })),
                        TableRow("Focus", "keyboard", 140.dp, listOf({
                            PhotoStrip(dark, 412.dp, 132.dp, crop = 7) {
                                GlassTabBar(Tabs, 0, {}, Modifier.offset(14.dp, 56.dp), onSearch = {}, previewStates = mapOf(2 to GlassState.Focus))
                            }
                        })),
                    ),
                    cellWidth = 412.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Minimize on scroll", "Tab bar · nested scroll",
            "Scrolling a list down condenses the bar into a current-tab lens, an accessory capsule and the search lens; scrolling back up expands it again. Tapping the current-tab lens also expands it.",
        ) {
            ThemePanels { dark ->
                val tabState = rememberTabBarState()
                var selected by remember { mutableIntStateOf(1) }
                GlassBackdrop(
                    modifier = Modifier.size(412.dp, 440.dp).clip(RoundedCornerShape(24.dp)),
                    backdrop = {
                        LazyColumn(
                            Modifier.fillMaxSize().nestedScroll(tabState.connection),
                            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(24) { index ->
                                Image(
                                    painterResource(R.drawable.seaglass_photo), null,
                                    Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(20.dp)).graphicsLayer {
                                        val (cx, cy) = PhotoCrops[index % PhotoCrops.size]
                                        scaleX = 2.4f; scaleY = 2.4f
                                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(cx / 1200f, cy / 1200f)
                                    },
                                    contentScale = ContentScale.Crop,
                                    colorFilter = if (dark) ColorFilter.colorMatrix(ColorMatrix().apply { setToScale(0.55f, 0.55f, 0.55f, 1f) }) else null,
                                )
                            }
                        }
                    },
                ) {
                    GlassTabBar(
                        Tabs, selected, { selected = it },
                        Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
                        minimized = tabState.minimized, onSearch = {}, onExpand = { tabState.minimized = false },
                        accessory = { UploadAccessory() },
                    )
                }
            }
        }

        CatalogueSection(
            "Top bars", "No fill · Clear lenses · Edge diffusion",
            "Top bars have no container. Actions are clear lenses or a clear cluster; the title is plain type. Once content scrolls under, a diffusion edge blurs and lightens it, fading to nothing over 84 dp, instead of a hard bar with a divider.",
        ) {
            ThemePanels { dark ->
                val actions = listOf(ClusterItem("search", "Search"), ClusterItem("more_horiz", "More"))
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Large title", "at top of page", 140.dp, listOf({
                            PhotoStrip(dark, 412.dp, 132.dp, crop = 8) {
                                GlassTopBar(
                                    "Library", large = true, subtitle = "2,481 photos · 38 albums",
                                    leading = { GlassBarLens("arrow_back", {}) },
                                    trailing = { GlassBarActions(actions, {}) },
                                )
                            }
                        })),
                        TableRow("Scrolled", "edge diffusion", 140.dp, listOf({
                            PhotoStrip(dark, 412.dp, 132.dp, crop = 9) {
                                GlassTopBar(
                                    "Library", scrolled = true,
                                    leading = { GlassBarLens("arrow_back", {}) },
                                    trailing = { GlassBarActions(actions, {}) },
                                )
                            }
                        })),
                        TableRow("Selection", "glass primary", 140.dp, listOf({
                            PhotoStrip(dark, 412.dp, 132.dp, crop = 10) {
                                GlassTopBar(
                                    "3 selected", scrolled = true,
                                    leading = { GlassBarLens("close", {}) },
                                    trailing = { GlassBarLens("delete", {}); GlassBarLens("ios_share", {}, primary = true) },
                                )
                            }
                        })),
                    ),
                    cellWidth = 412.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Floating toolbar", "Clear capsule · Frost tool · Glass confirm",
            "Tools share a clear capsule; the active tool is a frost lens and the confirm action is a separate glass lens. Pressing a tool condenses a clear lens that swells past the capsule edge.",
        ) {
            ThemePanels { dark ->
                var tool by remember { mutableIntStateOf(1) }
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Rest", null, 112.dp, listOf({
                            PhotoStrip(dark, 412.dp, 104.dp, crop = 11) {
                                GlassFloatingToolbar(Tools, tool, { tool = it }, Modifier.offset(47.dp, 24.dp), onConfirm = {})
                            }
                        })),
                        TableRow("Pressed", "Filters", 112.dp, listOf({
                            PhotoStrip(dark, 412.dp, 104.dp, crop = 11) {
                                GlassFloatingToolbar(Tools, 1, {}, Modifier.offset(47.dp, 24.dp), onConfirm = {}, previewStates = mapOf(3 to GlassState.Press))
                            }
                        })),
                    ),
                    cellWidth = 412.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Rail, sidebar and vertical toolbar", "Clear rail · Milk sidebar",
            "On tablets the rail is a clear capsule floating 16 dp from the edge with the pebble action on top; the current destination is a frost lens. The expanded sidebar is content, so it is a milk pane with a frost row. Vertical tool strips are clear capsules.",
        ) {
            ThemePanels { dark ->
                var rail by remember { mutableIntStateOf(0) }
                var side by remember { mutableIntStateOf(0) }
                var vtool by remember { mutableIntStateOf(0) }
                val honey = LocalRolePalettes.current[GlassRole.Honey]
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Tablet navigation", null, 450.dp, listOf({
                            PhotoStrip(dark, 696.dp, 436.dp, crop = 12) {
                                GlassNavigationRail(
                                    listOf(RailDestination("inbox", "Inbox", "12"), RailDestination("send", "Sent"), RailDestination("draft", "Drafts"), RailDestination("delete", "Trash")),
                                    rail, { rail = it }, Modifier.offset(16.dp, 16.dp), onFabClick = {},
                                )
                                GlassSidebar(
                                    "Mail",
                                    listOf(SidebarDestination("inbox", "Inbox", "12"), SidebarDestination("star", "Starred"), SidebarDestination("send", "Sent"), SidebarDestination("draft", "Drafts", "2"), SidebarDestination("delete", "Trash")),
                                    side, { side = it }, Modifier.offset(112.dp, 16.dp),
                                    primaryAction = { GlassButton("Compose", {}, leadingIcon = "edit", size = com.floydwiz.seaglass.components.buttons.GlassButtonSize.L) },
                                )
                                GlassFloatingToolbar(
                                    listOf("brush", "ink_eraser", "format_color_fill", "gesture").map { ToolItem(it, it) },
                                    vtool, { vtool = it }, Modifier.offset(624.dp, 16.dp), vertical = true, activeRole = honey,
                                )
                            }
                        })),
                    ),
                    cellWidth = 696.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Scope bar and page dots", "Clear · Frost · Well · Pearl",
            "Scope bars filter a single list: a clear capsule with a frost lens on the active scope and beads for counts. Page dots sit in a small well; the current page is a stretched pearl.",
        ) {
            ThemePanels { dark ->
                var scope by remember { mutableIntStateOf(0) }
                var page by remember { mutableIntStateOf(1) }
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Scope · dots", null, 128.dp, listOf({
                            PhotoStrip(dark, 412.dp, 120.dp, crop = 13) {
                                GlassScopeBar(listOf(ScopeItem("All"), ScopeItem("Unread"), ScopeItem("Flagged", 3), ScopeItem("Files")), scope, { scope = it }, Modifier.offset(14.dp, 14.dp))
                                Box(Modifier.offset(160.dp, 84.dp)) { GlassPageDots(5, page) }
                            }
                        })),
                    ),
                    cellWidth = 412.dp, labelsAbove = true,
                )
            }
        }
    }
}

@Composable
private fun UploadAccessory() {
    Row(Modifier.padding(horizontal = 10.dp), Arrangement.spacedBy(10.dp), Alignment.CenterVertically) {
        Image(painterResource(R.drawable.seaglass_photo), null, Modifier.size(36.dp).clip(androidx.compose.foundation.shape.CircleShape), contentScale = ContentScale.Crop)
        Column(Modifier.weight(1f)) {
            Text("Uploading 3 photos", fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight(650), maxLines = 1)
            Text("Shore walk · 2 left", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        androidx.compose.material3.CircularProgressIndicator(progress = { 0.66f }, modifier = Modifier.size(22.dp), strokeWidth = 3.dp)
    }
}
