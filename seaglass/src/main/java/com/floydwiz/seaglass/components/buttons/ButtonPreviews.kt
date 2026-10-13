package com.floydwiz.seaglass.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.components.containers.GlassMenu
import com.floydwiz.seaglass.components.containers.GlassMenuItem
import com.floydwiz.seaglass.preview.GlassPreview
import com.floydwiz.seaglass.preview.LightDarkPreviews
import com.floydwiz.seaglass.shape.SeaGlassShape

@LightDarkPreviews
@Composable
private fun GlassButtonPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassButton("Send", {}, leadingIcon = "send")
        GlassButton("Save", {}, style = GlassButtonStyle.Frost, leadingIcon = "bookmark")
        GlassButton("Share", {}, style = GlassButtonStyle.Clear)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassButton("Later", {}, style = GlassButtonStyle.Plain)
        GlassButton("Delete", {}, style = GlassButtonStyle.Destructive, leadingIcon = "delete")
        GlassButton("Disabled", {}, enabled = false)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassButton("S", {}, size = GlassButtonSize.S)
        GlassButton("L", {}, size = GlassButtonSize.L)
        GlassButton("XL", {}, size = GlassButtonSize.XL)
    }
}

@LightDarkPreviews
@Composable
private fun GlassIconButtonPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassIconButton("favorite", {}, style = GlassButtonStyle.Glass)
        GlassIconButton("bookmark", {}, style = GlassButtonStyle.Frost)
        GlassIconButton("more_horiz", {})
        GlassIconButton("close", {}, style = GlassButtonStyle.Plain)
        GlassIconButton("add", {}, style = GlassButtonStyle.Glass, shape = SeaGlassShape.Pebble, size = 64.dp)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassToggleButton(false, {}, "favorite")
        GlassToggleButton(true, {}, "favorite")
    }
}

@LightDarkPreviews
@Composable
private fun GlassClusterPreview() = GlassPreview {
    GlassCluster(
        listOf(ClusterItem("format_bold", "Bold"), ClusterItem("format_italic", "Italic"), ClusterItem("format_underlined", "Underline")),
        selected = setOf(0),
        onItemClick = {},
    )
}

@LightDarkPreviews
@Composable
private fun GlassSplitButtonPreview() = GlassPreview {
    GlassSplitRow("Reply", "reply", {}, expanded = false, onExpandedChange = {})
    GlassSplitRow("Reply", "reply", {}, expanded = true, onExpandedChange = {})
    GlassMenu {
        GlassMenuItem("Reply all", {}, icon = "reply_all")
        GlassMenuItem("Forward", {}, icon = "forward", highlighted = true)
    }
}
