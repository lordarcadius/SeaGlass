package com.floydwiz.seaglass.components.fab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.preview.GlassPreview
import com.floydwiz.seaglass.preview.LightDarkPreviews
import com.floydwiz.seaglass.preview.TallLightDarkPreviews

@LightDarkPreviews
@Composable
private fun GlassFabPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassFab("edit", {})
        GlassFab("edit", {}, size = FabSize.Large)
        GlassFab("edit", {}, size = FabSize.ExtraLarge)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassExtendedFab("Compose", "edit", {})
        GlassExtendedFab("Compose", "edit", {}, frost = true)
    }
}

@TallLightDarkPreviews
@Composable
private fun GlassFabMenuPreview() = GlassPreview {
    val items = listOf(FabMenuItem("reply", "Reply") {}, FabMenuItem("forward", "Forward") {}, FabMenuItem("archive", "Archive") {})
    Box(Modifier.fillMaxWidth().height(520.dp)) { GlassFabMenu(true, {}, items, label = "Compose") }
}
