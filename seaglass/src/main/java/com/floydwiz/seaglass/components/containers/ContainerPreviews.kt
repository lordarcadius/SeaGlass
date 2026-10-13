package com.floydwiz.seaglass.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.preview.GlassPreview
import com.floydwiz.seaglass.preview.LightDarkPreviews
import com.floydwiz.seaglass.preview.TallLightDarkPreviews
import com.floydwiz.seaglass.theme.LocalRolePalettes

@LightDarkPreviews
@Composable
private fun GlassCardPreview() = GlassPreview {
    val seafoam = LocalRolePalettes.current[GlassRole.Seafoam]
    GlassCard(Modifier.fillMaxWidth().height(84.dp)) { Text("Shore walk", Modifier.padding(20.dp)) }
    GlassCard(Modifier.fillMaxWidth().height(84.dp), emphasized = true, role = seafoam) { Text("Blue hour", Modifier.padding(20.dp)) }
    GlassCard(Modifier.fillMaxWidth().height(84.dp), selected = true) { Text("Tide pools", Modifier.padding(20.dp)) }
}

@LightDarkPreviews
@Composable
private fun GlassInsetListPreview() = GlassPreview {
    val roles = LocalRolePalettes.current
    GlassInsetList(Modifier.fillMaxWidth()) {
        GlassListRow("Wi-Fi", onClick = {}, leadingIcon = "wifi", leadingRole = roles[GlassRole.Aqua], supporting = "Shoreline")
        GlassListRow("Bluetooth", onClick = {}, leadingIcon = "bluetooth", leadingRole = roles[GlassRole.Cobalt], selected = true)
        GlassListRow("Battery saver", leadingIcon = "battery_saver", leadingRole = roles[GlassRole.Kelly], enabled = false, divider = false)
    }
}

@TallLightDarkPreviews
@Composable
private fun GlassAlertPanePreview() = GlassPreview {
    GlassAlertPane("delete", "Delete 3 photos?", "They move to Trash and are removed for good after 30 days.", "Delete", {}, "Cancel", {})
}

@TallLightDarkPreviews
@Composable
private fun GlassSheetPanePreview() = GlassPreview {
    GlassSheetPane(Modifier.fillMaxWidth()) {
        Text("Share", Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp))
        GlassInsetList(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            GlassListRow("Copy link", leadingSymbol = "link", divider = false, onClick = {})
            GlassListRow("Add to album", leadingSymbol = "photo_album", divider = false, onClick = {})
        }
    }
}

@LightDarkPreviews
@Composable
private fun GlassMenuPreview() = GlassPreview {
    GlassMenu {
        GlassMenuItem("Copy", {}, icon = "content_copy")
        GlassMenuItem("Favorite", {}, icon = "favorite", highlighted = true)
        GlassMenuDivider()
        GlassMenuItem("Delete", {}, icon = "delete", danger = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@LightDarkPreviews
@Composable
private fun GlassCarouselPreview() = GlassPreview {
    val roles = LocalRolePalettes.current
    val tones = listOf(GlassRole.Aqua, GlassRole.Seafoam, GlassRole.Honey, GlassRole.SeaPink)
    GlassCarousel(tones.size, Modifier.fillMaxWidth().height(180.dp), preferredItemWidth = 220.dp) { index ->
        Box(Modifier.fillMaxSize().maskClip(RoundedCornerShape(28.dp)).background(roles[tones[index]][80]))
    }
}
