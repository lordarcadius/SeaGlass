package com.floydwiz.seaglass.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.preview.GlassPreview
import com.floydwiz.seaglass.preview.LightDarkPreviews
import com.floydwiz.seaglass.theme.LocalRolePalettes

@LightDarkPreviews
@Composable
private fun GlassLoadingPreview() = GlassPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassLoadingIndicator()
        GlassLoadingIndicator(contained = true)
        GlassBeadsLoading()
    }
}

@LightDarkPreviews
@Composable
private fun GlassProgressPreview() = GlassPreview {
    GlassLinearProgress(0.62f, Modifier.fillMaxWidth())
    GlassLinearProgress(null, Modifier.fillMaxWidth())
    GlassLiquidProgress(0.6f, "Uploading", "12 of 20", Modifier.fillMaxWidth())
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassRingProgress(0.62f) { Text("62") }
        GlassRingProgress(null)
    }
}

@LightDarkPreviews
@Composable
private fun GlassToastPreview() = GlassPreview {
    GlassToast("Saved to Shore walk", action = ToastAction("Undo") {})
    GlassToast("Couldn’t upload 2 photos", icon = "cloud_off", iconRole = GlassRole.SeaPink, detail = "Waiting for a connection", action = ToastAction("Retry") {})
    GlassMediaToast("Maya shared 3 photos", "View", {})
}

@LightDarkPreviews
@Composable
private fun GlassBannerPreview() = GlassPreview {
    GlassBanner("Backup paused on mobile data", BannerKind.Info, "Resume", {})
    GlassBanner("Storage is 92% full", BannerKind.Warning, "Manage", {})
    GlassBanner("Two albums failed to sync", BannerKind.Error, "Retry", {})
}

@LightDarkPreviews
@Composable
private fun GlassBadgeAndTooltipPreview() = GlassPreview {
    val roles = LocalRolePalettes.current
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassBadge()
        GlassBadge(text = "8")
        GlassBadge(text = "99+")
        GlassBadge(text = "New", role = roles[GlassRole.Kelly])
    }
    GlassTooltip("Add photos")
    GlassRichTooltip("Smart albums", "Photos from the same beach and hour are grouped for you.", "Learn more", {})
}
