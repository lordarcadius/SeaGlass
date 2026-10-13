package com.floydwiz.seaglass.components.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.components.buttons.ClusterItem
import com.floydwiz.seaglass.preview.GlassPreview
import com.floydwiz.seaglass.preview.LightDarkPreviews

private val Tabs = listOf(TabItem("home", "Home"), TabItem("photo_library", "Photos"), TabItem("explore", "Explore"), TabItem("person", "You"))

@LightDarkPreviews
@Composable
private fun GlassTabBarPreview() = GlassPreview {
    GlassTabBar(Tabs, 0, {}, onSearch = {})
    GlassTabBar(Tabs, 0, {}, minimized = true, onSearch = {})
}

@LightDarkPreviews
@Composable
private fun GlassTopBarPreview() = GlassPreview {
    GlassTopBar(
        "Library", large = true, subtitle = "2,481 photos",
        leading = { GlassBarLens("arrow_back", {}) },
        trailing = { GlassBarActions(listOf(ClusterItem("search", "Search"), ClusterItem("more_horiz", "More")), {}) },
    )
}

@LightDarkPreviews
@Composable
private fun GlassToolbarPreview() = GlassPreview {
    GlassFloatingToolbar(listOf("crop", "tune", "auto_fix_high").map { ToolItem(it, it) }, 1, {}, onConfirm = {})
    Row { GlassPageDots(5, 1) }
    GlassScopeBar(listOf(ScopeItem("All"), ScopeItem("Unread"), ScopeItem("Flagged", 3)), 0, {})
}

@LightDarkPreviews
@Composable
private fun GlassRailPreview() = GlassPreview {
    Row {
        GlassNavigationRail(listOf(RailDestination("inbox", "Inbox", "12"), RailDestination("send", "Sent")), 0, {}, onFabClick = {})
        GlassSidebar("Mail", listOf(SidebarDestination("inbox", "Inbox", "12"), SidebarDestination("send", "Sent")), 0, {}, Modifier.padding(start = 12.dp))
    }
}
