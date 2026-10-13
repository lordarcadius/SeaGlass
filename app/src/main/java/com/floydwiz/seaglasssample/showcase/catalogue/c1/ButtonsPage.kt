package com.floydwiz.seaglasssample.showcase.catalogue.c1

import androidx.compose.runtime.setValue
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.ClusterItem
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassCluster
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.buttons.GlassSplitRow
import com.floydwiz.seaglass.components.buttons.GlassToggleButton
import com.floydwiz.seaglass.components.containers.GlassMenu
import com.floydwiz.seaglass.components.containers.GlassMenuItem
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoStrip
import com.floydwiz.seaglasssample.showcase.catalogue.SpecimenTable
import com.floydwiz.seaglasssample.showcase.catalogue.TableRow
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels
import com.floydwiz.seaglasssample.showcase.catalogue.stateRows

private val StateNotes = mapOf(
    GlassState.Rest to "enabled",
    GlassState.Hover to "lift 2dp · light +",
    GlassState.Focus to "halo, no outline",
    GlassState.Press to "scale 0.96 · bloom",
    GlassState.Disabled to "dry glass · 38%",
)

private class StyleSpec(val head: String, val style: GlassButtonStyle, val label: String, val icon: String)

private val Styles = listOf(
    StyleSpec("Glass", GlassButtonStyle.Glass, "Send", "send"),
    StyleSpec("Frost", GlassButtonStyle.Frost, "Save", "bookmark"),
    StyleSpec("Clear", GlassButtonStyle.Clear, "Share", "ios_share"),
    StyleSpec("Plain", GlassButtonStyle.Plain, "Later", "schedule"),
    StyleSpec("Destructive", GlassButtonStyle.Destructive, "Delete", "delete"),
)

@Composable
fun ButtonsPage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Styles and states", "Glass · Frost · Clear · Plain · Destructive",
            "Glass is the one primary action in a view. Frost is secondary: same tint, less light. Clear is neutral glass with an accent label, for actions over imagery and inside floating bars. Plain has no material at rest; when pressed a clear lens condenses under the finger. Destructive is Glass in the sea pink role.",
        ) {
            ThemePanels {
                SpecimenTable(
                    heads = Styles.map { it.head },
                    rows = stateRows(subFor = { StateNotes[it] }, height = 60.dp, cells = Styles.map { spec ->
                        { _: GlassState -> GlassButton(spec.label, {}, style = spec.style, leadingIcon = spec.icon) }
                    }),
                    labelWidth = 96.dp, cellWidth = 140.dp,
                )
            }
        }

        CatalogueSection(
            "Sizes and silhouettes", "Sea Glass",
            "Capsules for labelled buttons, lenses for icon-only buttons, pebbles for the single most expressive action. Every size keeps a 48 dp touch target. There is no square button; the tightest corner is the 12 dp seam of a split button.",
        ) {
            ThemePanels {
                val seafoam = LocalRolePalettes.current[GlassRole.Seafoam]
                val heights = mapOf(GlassButtonSize.S to 48.dp, GlassButtonSize.M to 56.dp, GlassButtonSize.L to 68.dp, GlassButtonSize.XL to 86.dp)
                SpecimenTable(
                    heads = listOf("Capsule", "Lens", "Pebble"),
                    rows = GlassButtonSize.entries.map { size ->
                        TableRow(size.name, "h ${size.height.value.toInt()} · ${size.fontSize.toInt()} sp", heights.getValue(size), listOf(
                            { GlassButton("Send", {}, size = size, leadingIcon = "send") },
                            { GlassIconButton("send", {}, style = GlassButtonStyle.Glass, size = size.height) },
                            { GlassIconButton("add", {}, style = GlassButtonStyle.Glass, size = size.height, shape = SeaGlassShape.Pebble, iconScale = 0.52f, role = seafoam) },
                        ))
                    },
                    labelWidth = 96.dp, cellWidth = 130.dp,
                )
            }
        }

        CatalogueSection(
            "Icon buttons and toggles", "Lens",
            "Icon buttons are 48 dp lenses. A toggle starts as clear glass with an outlined symbol and becomes tinted glass with a filled symbol when on, so selection reads through both colour and form.",
        ) {
            ThemePanels {
                val seaPink = LocalRolePalettes.current[GlassRole.SeaPink]
                SpecimenTable(
                    heads = listOf("Glass", "Frost", "Clear", "Plain", "Off", "On"),
                    rows = stateRows(height = 62.dp, cells = listOf(
                        { _: GlassState -> GlassIconButton("favorite", {}, style = GlassButtonStyle.Glass) },
                        { _: GlassState -> GlassIconButton("bookmark", {}, style = GlassButtonStyle.Frost) },
                        { _: GlassState -> GlassIconButton("more_horiz", {}, style = GlassButtonStyle.Clear) },
                        { _: GlassState -> GlassIconButton("close", {}, style = GlassButtonStyle.Plain) },
                        { _: GlassState -> GlassToggleButton(false, {}, "favorite", role = seaPink) },
                        { _: GlassState -> GlassToggleButton(true, {}, "favorite", role = seaPink) },
                    )),
                    labelWidth = 88.dp, cellWidth = 80.dp,
                )
            }
        }

        CatalogueSection(
            "Clusters, split buttons and pairs", "Clear holder · Glass action",
            "Related actions share one clear capsule; the active one is a frost lens inside it, and pressing another condenses a clear lens that bulges past the edge. Split buttons are two pieces of the same glass with a 3 dp seam and 12 dp inner corners; opening rounds the trailing piece into a lens and drops a milk menu.",
        ) {
            ThemePanels { dark ->
                var format by remember { mutableStateOf(setOf(0)) }
                var history by remember { mutableStateOf(setOf<Int>()) }
                var open by remember { mutableStateOf(true) }
                val horizontal = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 22.dp)
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Label("Cluster")
                    PhotoStrip(dark, 360.dp, 92.dp, crop = 1) {
                        Row(Modifier.offset(16.dp, 22.dp), Arrangement.spacedBy(16.dp)) {
                            GlassCluster(
                                listOf(ClusterItem("format_bold", "Bold"), ClusterItem("format_italic", "Italic"), ClusterItem("format_underlined", "Underline")),
                                selected = format,
                                onItemClick = { format = if (it in format) format - it else format + it },
                            )
                            GlassCluster(
                                listOf(ClusterItem("undo", "Undo"), ClusterItem("redo", "Redo")),
                                selected = history,
                                onItemClick = {},
                            )
                            GlassIconButton("more_horiz", {}, style = GlassButtonStyle.Clear, iconScale = 0.5f)
                        }
                    }
                    Label("Split · glass · frost · clear")
                    PhotoStrip(dark, 360.dp, 92.dp, crop = 3) {
                        Row(Modifier.then(horizontal).offset(16.dp, 24.dp), Arrangement.spacedBy(16.dp)) {
                            GlassSplitRow("Reply", "reply", {}, false, {}, GlassButtonStyle.Glass)
                            GlassSplitRow("Reply", "reply", {}, false, {}, GlassButtonStyle.Frost)
                            GlassSplitRow("Reply", "reply", {}, false, {}, GlassButtonStyle.Clear)
                        }
                    }
                    Label("Split · open")
                    PhotoStrip(dark, 360.dp, 232.dp, crop = 5) {
                        Box(Modifier.offset(16.dp, 20.dp)) {
                            GlassSplitRow("Reply", "reply", {}, open, { open = it }, GlassButtonStyle.Glass)
                            if (open) {
                                GlassMenu(Modifier.offset(0.dp, 56.dp), width = 214.dp) {
                                    GlassMenuItem("Reply all", {}, icon = "reply_all")
                                    GlassMenuItem("Forward", {}, icon = "forward", highlighted = true)
                                    GlassMenuItem("Reply later", {}, icon = "schedule")
                                }
                            }
                        }
                    }
                    Label("Pairs · dismiss and confirm")
                    Row(Modifier.then(horizontal), Arrangement.spacedBy(8.dp), androidx.compose.ui.Alignment.CenterVertically) {
                        GlassButton("Not now", {}, style = GlassButtonStyle.Plain)
                        GlassButton("Allow", {})
                        Spacer(Modifier.width(24.dp))
                        GlassButton("Cancel", {}, style = GlassButtonStyle.Clear)
                        GlassButton("Delete", {}, style = GlassButtonStyle.Destructive, leadingIcon = "delete")
                    }
                    Label("Stacked · sheet and dialog footer")
                    Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassButton("Continue", {}, Modifier.fillMaxWidth(), size = GlassButtonSize.L)
                        GlassButton("Maybe later", {}, Modifier.fillMaxWidth().height(50.dp), style = GlassButtonStyle.Clear, size = GlassButtonSize.L)
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
}
