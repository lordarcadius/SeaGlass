package com.floydwiz.seaglasssample.showcase.catalogue.c6

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.feedback.BannerKind
import com.floydwiz.seaglass.components.feedback.GlassBadge
import com.floydwiz.seaglass.components.feedback.GlassBanner
import com.floydwiz.seaglass.components.feedback.GlassBeadsLoading
import com.floydwiz.seaglass.components.feedback.GlassLinearProgress
import com.floydwiz.seaglass.components.feedback.GlassLiquidProgress
import com.floydwiz.seaglass.components.feedback.GlassLoadingIndicator
import com.floydwiz.seaglass.components.feedback.GlassMediaToast
import com.floydwiz.seaglass.components.feedback.GlassRichTooltip
import com.floydwiz.seaglass.components.feedback.GlassRingProgress
import com.floydwiz.seaglass.components.feedback.GlassToast
import com.floydwiz.seaglass.components.feedback.GlassToastHost
import com.floydwiz.seaglass.components.feedback.GlassTooltip
import com.floydwiz.seaglass.components.feedback.GlassToastState
import com.floydwiz.seaglass.components.feedback.ToastAction
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoStrip
import com.floydwiz.seaglasssample.showcase.catalogue.SpecimenTable
import com.floydwiz.seaglasssample.showcase.catalogue.TableRow
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels
import kotlinx.coroutines.launch

@Composable
private fun ink() = if (LocalSeaGlassDark.current) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight

@Composable
fun FeedbackPage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Loading", "Glass pebble · Bead",
            "The loading indicator is a glass pebble tumbling through lens, shard, egg and tumble silhouettes on a 2.4 s alternate loop, its light drifting as it turns. Inside busy surfaces it sits in a clear lens; for inline waits three beads bob in sequence. With animations off the pebble holds the lens shape.",
        ) {
            ThemePanels {
                val shapes = listOf("lens" to SeaGlassShape.Lens, "shard" to SeaGlassShape.Shard, "egg" to SeaGlassShape.Egg, "tumble" to SeaGlassShape.Tumble)
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Live", "alone · contained · beads", 84.dp, listOf({
                            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                GlassLoadingIndicator()
                                GlassLoadingIndicator(contained = true)
                                GlassBeadsLoading()
                                Text("Finding matches", color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        })),
                        TableRow("Keyframes", "0 · 33 · 66 · 100%", 84.dp, listOf({
                            Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
                                shapes.forEach { (name, shape) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        GlassSurface(GlassFinish.Glass, Modifier.size(48.dp), shape, reactsTo = emptySet())
                                        Text(name, color = glassOnSurfaceVariant(), fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        })),
                    ),
                    cellWidth = 420.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Progress", "Well + Fill",
            "Progress pours lit liquid into a well. Linear tracks are 8 dp; the liquid variant is 30 dp and carries its label. Rings are wells with rounded caps and a lighter leading edge; the indeterminate ring is a comet that fades to nothing at its tail.",
        ) {
            ThemePanels {
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Linear", null, 56.dp, listOf({
                            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("Uploading 12 of 20", color = ink(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("62%", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(650))
                                }
                                GlassLinearProgress(0.62f)
                            }
                        })),
                        TableRow("Indeterminate", null, 56.dp, listOf({
                            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Preparing album", color = ink(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                GlassLinearProgress(null)
                            }
                        })),
                        TableRow("Liquid", "30 dp · labelled", 40.dp, listOf({
                            Box(Modifier.width(360.dp)) { GlassLiquidProgress(0.62f, "Uploading", "12 of 20") }
                        })),
                        TableRow("Ring", "determinate · comet\ncontained · inline", 76.dp, listOf({
                            Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
                                GlassRingProgress(0.62f) { Text("62", color = ink(), fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                                GlassRingProgress(null)
                                Box(contentAlignment = Alignment.Center) {
                                    GlassSurface(GlassFinish.Clear, Modifier.size(64.dp), SeaGlassShape.Lens, reactsTo = emptySet())
                                    GlassRingProgress(0.4f, size = 42.dp, stroke = 6.dp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    GlassRingProgress(0.75f, size = 22.dp, stroke = 4.dp)
                                    Text("Syncing", color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        })),
                    ),
                    cellWidth = 380.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Toasts and banners", "Ink · Clear over media · Tinted frost",
            "Toasts are ink panes: dark on light themes, pale on dark, so they read as passing above everything. Over photos a toast becomes clear glass. Persistent banners are tinted frost in the role colour of their meaning, with the action as plain text on the same tint.",
        ) {
            ThemePanels { dark ->
                val toastState = remember { GlassToastState() }
                val scope = rememberCoroutineScope()
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Toast", "ink", 60.dp, listOf({ Box(Modifier.width(380.dp)) { GlassToast("Saved to Shore walk", action = ToastAction("Undo") {}) } })),
                        TableRow("Two-line", null, 76.dp, listOf({
                            Box(Modifier.width(380.dp)) {
                                GlassToast("Couldn’t upload 2 photos", icon = "cloud_off", iconRole = GlassRole.SeaPink, detail = "Waiting for a connection", action = ToastAction("Retry") {})
                            }
                        })),
                        TableRow("Over media", "clear", 110.dp, listOf({
                            PhotoStrip(dark, 384.dp, 96.dp, crop = 6) {
                                GlassMediaToast("Maya shared 3 photos", "View", {}, Modifier.offset(12.dp, 20.dp).width(360.dp), avatar = painterResource(R.drawable.seaglass_photo))
                            }
                        })),
                    ) + listOf(
                        Triple("Info", BannerKind.Info, "Backup paused on mobile data" to "Resume"),
                        Triple("Warning", BannerKind.Warning, "Storage is 92% full" to "Manage"),
                        Triple("Error", BannerKind.Error, "Two albums failed to sync" to "Retry"),
                        Triple("Success", BannerKind.Success, "All 2,481 photos backed up" to "View"),
                    ).map { (name, kind, copy) ->
                        TableRow(name, "${kind.role.name.lowercase()} frost", 60.dp, listOf({ Box(Modifier.width(380.dp)) { GlassBanner(copy.first, kind, copy.second, {}) } }))
                    } + TableRow("Live toast", "tap to show", 130.dp, listOf({
                        Column(Modifier.width(380.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            GlassButton("Show toast", { scope.launch { toastState.show("Saved to Shore walk", action = ToastAction("Undo") {}) } }, style = GlassButtonStyle.Frost)
                            Box(Modifier.size(380.dp, 60.dp)) { GlassToastHost(toastState) }
                        }
                    })),
                    cellWidth = 460.dp, labelsAbove = true,
                )
            }
        }

        CatalogueSection(
            "Beads and tooltips", "Bead · Ink · Milk",
            "Counts and status marks are beads: small solid glass drops with a gloss highlight, never larger than 20 dp tall. Plain tooltips are ink capsules; rich tooltips are milk panes with a plain action.",
        ) {
            ThemePanels {
                val roles = LocalRolePalettes.current
                SpecimenTable(
                    heads = emptyList(),
                    rows = listOf(
                        TableRow("Beads", "dot 9–11 · count 20", 72.dp, listOf({
                            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box { GlassIconButton("notifications", {}, size = 48.dp); GlassBadge(Modifier.offset(33.dp, 2.dp), dotSize = 11.dp) }
                                Box { GlassIconButton("mail", {}, size = 48.dp); GlassBadge(Modifier.offset(32.dp, -2.dp), "3") }
                                GlassBadge(dotSize = 9.dp)
                                GlassBadge(text = "8")
                                GlassBadge(text = "99+")
                                GlassBadge(text = "New", role = roles[GlassRole.Kelly])
                                GlassBadge(text = "Beta", role = roles[GlassRole.Aqua])
                            }
                        })),
                        TableRow("Tooltips", "ink · rich milk", 170.dp, listOf({
                            Row(Modifier.width(384.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    GlassTooltip("Add photos")
                                    CompositionLocalProvider(LocalForcedGlassState provides GlassState.Hover) { GlassIconButton("add_photo_alternate", {}, size = 48.dp) }
                                }
                                GlassRichTooltip(
                                    "Smart albums",
                                    "Photos from the same beach and hour are grouped for you. Rename or merge them any time.",
                                    "Learn more", {},
                                    Modifier.width(260.dp),
                                )
                            }
                        })),
                    ),
                    cellWidth = 384.dp, labelsAbove = true,
                )
            }
        }
    }
}
