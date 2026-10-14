package com.floydwiz.seaglasssample.showcase.catalogue.c0

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassContrast
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.theme.SeaGlassContrast
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueScreen
import com.floydwiz.seaglasssample.showcase.catalogue.CatalogueSection
import com.floydwiz.seaglasssample.showcase.catalogue.ThemePanels
import com.floydwiz.seaglasssample.showcase.catalogue.ThemeStripPair

private class Rule(val number: String, val title: String, val body: String)

private val Rules = listOf(
    Rule("01", "Wet acts, dry rests", "Enabled glass is glossy and lit. Disabled glass dries to a matte, desaturated frost with content at 38%. Nothing else signals disabled."),
    Rule("02", "Raised acts, sunken takes", "Glass, frost, clear and pearl stand off the surface and are pressed. Wells sit below it and are typed or dragged in."),
    Rule("03", "Selection lifts", "Unchosen options rest in wells; choosing one raises it into glass. The current tab or tool is a frost lens."),
    Rule("04", "Milk holds, ink passes", "Content lives on milk panes and passing messages on ink. Glass may sit on milk, but never on other glass."),
    Rule("05", "A lens is not a second sheet", "A lens inside a clear bar is the same glass, thickened. It adds light, never a second shadow or rim stack."),
    Rule("06", "Press keeps the outline", "Press scales to 0.96 and blooms light from inside. Silhouettes only morph for real state changes such as open and close."),
    Rule("07", "Focus glows", "A 1.5 dp tint rim hugs the shape with no gap, inside a 14 dp glow and a 34 dp bloom. High contrast thickens the rim to 3 dp."),
    Rule("08", "No sharp corners", "Capsules, lenses and pebbles for controls, 24–36 dp panes for content, concentric corners when inset. Nothing 36 dp or taller goes below 12 dp."),
)

@Composable
fun LanguagePage(contentPadding: PaddingValues) {
    CatalogueScreen(contentPadding) {
        CatalogueSection(
            "Composition rules", "Applies to every component",
            "Eight rules that replace the M3E component anatomy. Where a component on the following boards looks unusual, one of these is the reason.",
        ) { RuleGrid() }

        CatalogueSection(
            "Ten finishes of one glass, plus the lens", "seaGlass.material.*",
            "Each finish is a fixed recipe of the layer stack, so components never invent their own effects. Wet finishes (glass, frost, clear, pearl, fill, bead) are lit and glossy; well and milk are surfaces; ink is the inverse surface; dry replaces any of them when disabled.",
        ) { FinishTable() }

        CatalogueSection(
            "One state model", "seaGlass.state.*",
            "Hover lifts raised glass 2 dp and brightens its light pool; surfaces get a 7% layer instead. Focus is the halo: a tint rim, glow and bloom hugging the shape. Press scales to 0.96 and blooms light from the centre, keeping the silhouette. A plain button condenses clear glass only while pressed. Disabled is dry glass.",
        ) { StateMatrix() }

        CatalogueSection(
            "Shape", "seaGlass.shape.*",
            "Controls are capsules (radius = height / 2 with a ±4% hand-tumbled irregularity), lenses or, for the FAB only, pebbles. Containers are panes from 24 to 36 dp. Anything inset keeps concentric corners. Nothing 36 dp or taller goes below 12 dp; small marks such as 26 dp checkboxes use a third of their height.",
        ) { ShapeGallery() }
    }
}

@Composable
private fun RuleGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Rules.forEach { rule ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    rule.number,
                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(rule.title, style = MaterialTheme.typography.titleMedium)
                Text(rule.body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private class FinishRow(
    val name: String,
    val token: String,
    val usedFor: String,
    val implementation: String,
    val specimens: @Composable RowScope.(dark: Boolean) -> Unit,
)

@Composable
private fun FinishTable() {
    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        finishRows().forEach { row ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
                    Text(row.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        row.token,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(row.usedFor, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ThemeStripPair { dark -> row.specimens(this, dark) }
                Text(row.implementation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun accent(role: TonalTones = LocalGlassRole.current): Color {
    val high = LocalGlassContrast.current == SeaGlassContrast.High
    return if (LocalSeaGlassDark.current) (if (high) role[90] else role[80]) else (if (high) role[30] else role[40])
}

@Composable
private fun role(role: GlassRole): TonalTones = LocalRolePalettes.current[role]

/** A glass specimen with an optional leading symbol and label, as built by `W()` in the boards. */
@Composable
private fun Spec(
    finish: GlassFinish,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    shape: SeaGlassShape = SeaGlassShape.Capsule,
    role: TonalTones = LocalGlassRole.current,
    icon: String? = null,
    filled: Boolean = false,
    iconSize: Dp = 20.dp,
    label: String? = null,
    fontSize: TextUnit = 14.sp,
    weight: FontWeight = FontWeight.SemiBold,
    gap: Dp = 8.dp,
    padding: Dp = 0.dp,
    alignStart: Boolean = false,
    foreground: Color? = null,
    tinted: Boolean = false,
    interactive: Boolean = true,
) {
    GlassSurface(
        finish = finish,
        modifier = modifier.size(width, height),
        shape = shape,
        role = role,
        tinted = tinted,
        onClick = if (interactive) ({}) else null,
        contentAlignment = if (alignStart) Alignment.CenterStart else Alignment.Center,
    ) {
        val disabled = LocalForcedGlassState.current == GlassState.Disabled
        val color = if (disabled || foreground == null) LocalContentColor.current else foreground
        if (icon != null || label != null) {
            Row(Modifier.padding(horizontal = padding), Arrangement.spacedBy(gap), Alignment.CenterVertically) {
                if (icon != null) SymbolIcon(icon, size = iconSize, fill = filled, tint = color)
                if (label != null) Text(label, color = color, fontSize = fontSize, fontWeight = weight, letterSpacing = 0.1.sp)
            }
        }
    }
}

@Composable
private fun finishRows(): List<FinishRow> = listOf(
    FinishRow("Glass", "seaGlass.material.glass", "Primary action, FAB, checked and on states, send.",
        "Backdrop blur 18 dp + saturation 1.7, tint verticalGradient, three innerShadow layers, gradient Stroke rim, tinted dropShadow.") {
        Spec(GlassFinish.Glass, 104.dp, 44.dp, icon = "send", filled = true, label = "Send", fontSize = 15.sp)
        Spec(GlassFinish.Glass, 44.dp, 44.dp, shape = SeaGlassShape.Lens, icon = "favorite", filled = true, iconSize = 22.dp)
    },
    FinishRow("Frost", "seaGlass.material.frost", "Secondary action, active tab and tool lens, banners, emphasis cards.",
        "Blur 14 dp with a lighter tint, top-only rim, soft tinted dropShadow.") {
        Spec(GlassFinish.Frost, 104.dp, 44.dp, icon = "bookmark", filled = true, label = "Save", fontSize = 15.sp)
        Spec(GlassFinish.Frost, 44.dp, 44.dp, shape = SeaGlassShape.Lens, icon = "tune", iconSize = 22.dp)
    },
    FinishRow("Clear", "seaGlass.material.clear", "Floating bars, lenses over imagery, tertiary buttons.",
        "Blur 16 dp, white gradient 60 → 32%, specular radialGradient, gradient Stroke rim.") {
        Spec(GlassFinish.Clear, 104.dp, 44.dp, icon = "ios_share", label = "Share", fontSize = 15.sp, foreground = accent())
        Spec(GlassFinish.Clear, 44.dp, 44.dp, shape = SeaGlassShape.Lens, icon = "more_horiz", iconSize = 22.dp)
    },
    FinishRow("Pearl", "seaGlass.material.pearl", "Knobs, thumbs and page markers at rest.",
        "Opaque white verticalGradient, innerShadow base tint, small dropShadow.") {
        Spec(GlassFinish.Pearl, 28.dp, 28.dp, shape = SeaGlassShape.Round)
        Spec(GlassFinish.Pearl, 34.dp, 22.dp, shape = SeaGlassShape.Pane(11.dp))
        Spec(GlassFinish.Pearl, 20.dp, 8.dp, shape = SeaGlassShape.Pane(4.dp))
    },
    FinishRow("Fill", "seaGlass.material.fill", "Liquid inside switches, sliders and progress.",
        "Tinted gradient, 1 dp top highlight, glow dropShadow with blur 0.4 × height.") {
        Spec(GlassFinish.Well, 58.dp, 34.dp, shape = SeaGlassShape.Pane(17.dp), interactive = false)
        Spec(GlassFinish.Fill, 120.dp, 12.dp, shape = SeaGlassShape.Pane(6.dp), interactive = false)
    },
    FinishRow("Well", "seaGlass.material.well", "Fields, tracks, unchosen options, pressed rows.",
        "Inset shadows from the top (Compose 1.9+), 1 dp bottom lip highlight, light blur.") {
        Spec(
            GlassFinish.Well, 150.dp, 40.dp, shape = SeaGlassShape.Pane(18.dp), icon = "search", iconSize = 18.dp,
            label = "Search", fontSize = 14.sp, weight = FontWeight.Medium, alignStart = true, padding = 12.dp,
            foreground = MaterialTheme.colorScheme.onSurfaceVariant, interactive = false,
        )
        Spec(GlassFinish.Well, 26.dp, 26.dp, shape = SeaGlassShape.Pane(9.dp), interactive = false)
    },
    FinishRow("Milk", "seaGlass.material.milk", "Cards, lists, sheets, alerts, menus, pickers.",
        "Surface at 86–90% over a 30 dp backdrop blur, gradient top rim, large soft shadow.") {
        Spec(
            GlassFinish.Milk, 168.dp, 52.dp, shape = SeaGlassShape.Pane(20.dp), label = "Tide tables", fontSize = 14.sp,
            alignStart = true, padding = 16.dp, interactive = false,
        )
    },
    FinishRow("Ink", "seaGlass.material.ink", "Toasts and plain tooltips, inverse of the theme.",
        "Inverse milk: dark at 90% on light themes, pale at 93% on dark themes.") {
        Spec(GlassFinish.Ink, 132.dp, 40.dp, icon = "check_circle", filled = true, iconSize = 18.dp, label = "Saved", fontSize = 14.sp, interactive = false)
    },
    FinishRow("Bead", "seaGlass.material.bead", "Badges, counts, avatar initials, status dots.",
        "radialGradient fill with innerShadow gloss and a 3 dp shadow.") {
        Spec(GlassFinish.Bead, 20.dp, 20.dp, shape = SeaGlassShape.Round, role = role(GlassRole.SeaPink), label = "3", fontSize = 11.sp, weight = FontWeight.Bold, interactive = false)
        Spec(GlassFinish.Bead, 36.dp, 20.dp, role = role(GlassRole.SeaPink), label = "99+", fontSize = 11.sp, weight = FontWeight.Bold, interactive = false)
        Spec(GlassFinish.Bead, 36.dp, 36.dp, shape = SeaGlassShape.Round, role = role(GlassRole.Lavender), label = "M", fontSize = 14.sp, weight = FontWeight.Bold, interactive = false)
    },
    FinishRow("Dry", "seaGlass.material.dry", "Every disabled control, whatever its wet material.",
        "Blur 8 dp, saturation 0.3, grain at 30%, no shadow or light.") {
        CompositionLocalProvider(LocalForcedGlassState provides GlassState.Disabled) {
            Spec(GlassFinish.Glass, 104.dp, 44.dp, icon = "send", label = "Send", fontSize = 15.sp, interactive = false)
            Spec(GlassFinish.Glass, 44.dp, 44.dp, shape = SeaGlassShape.Lens, icon = "favorite", iconSize = 22.dp, interactive = false)
        }
    },
    FinishRow("Lens", "seaGlass.effect.lens", "Thumb, knob or tab marker while dragged; magnifies what is beneath.",
        "AGSL RuntimeShader, 1.12× magnification with edge bend (API 33+); API 31–32 scale the backdrop layer 1.1×.") {
        Box(Modifier.size(150.dp, 40.dp)) {
            Spec(GlassFinish.Well, 150.dp, 10.dp, shape = SeaGlassShape.Pane(5.dp), modifier = Modifier.offset(0.dp, 15.dp), interactive = false)
            Spec(GlassFinish.Fill, 96.dp, 10.dp, shape = SeaGlassShape.Pane(5.dp), modifier = Modifier.offset(0.dp, 15.dp), interactive = false)
            Spec(GlassFinish.Lens, 50.dp, 36.dp, shape = SeaGlassShape.Pane(18.dp), modifier = Modifier.offset(71.dp, 2.dp), interactive = false)
        }
    },
)

private val StateColumns = listOf(GlassState.Rest, GlassState.Hover, GlassState.Focus, GlassState.Press, GlassState.Disabled)

@Composable
private fun StateMatrix() {
    ThemePanels { dark ->
        Column(Modifier.horizontalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(56.dp, 16.dp))
                StateColumns.forEach {
                    Text(it.name, Modifier.size(104.dp, 16.dp), fontSize = 11.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            MatrixRow("Glass") { Spec(GlassFinish.Glass, 92.dp, 42.dp, icon = "send", filled = true, iconSize = 18.dp, label = "Send", gap = 6.dp) }
            MatrixRow("Clear") { Spec(GlassFinish.Clear, 92.dp, 42.dp, label = "Share", foreground = accent()) }
            MatrixRow("Plain") { Spec(GlassFinish.Plain, 92.dp, 42.dp, label = "Later", foreground = accent()) }
            MatrixRow("Well") { state ->
                Spec(
                    GlassFinish.Well, 104.dp, 40.dp, shape = SeaGlassShape.Pane(16.dp), icon = "search", iconSize = 18.dp,
                    label = "Search", fontSize = 13.5.sp, weight = FontWeight.Medium, alignStart = true, padding = 10.dp, gap = 6.dp,
                    foreground = MaterialTheme.colorScheme.onSurfaceVariant, tinted = state == GlassState.Focus,
                )
            }
            MatrixRow("Pearl") { Spec(GlassFinish.Pearl, 30.dp, 30.dp, shape = SeaGlassShape.Round) }
        }
    }
}

@Composable
private fun MatrixRow(name: String, specimen: @Composable (GlassState) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(name, Modifier.size(56.dp, 20.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        StateColumns.forEach { state ->
            Box(Modifier.size(104.dp, 54.dp), contentAlignment = Alignment.Center) {
                CompositionLocalProvider(LocalForcedGlassState provides state.takeUnless { it == GlassState.Rest }) { specimen(state) }
            }
        }
    }
}

@Composable
private fun ShapeGallery() {
    ThemePanels { _ ->
        FlowRow(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ShapeCell("Capsule\nlabelled controls") {
                Spec(GlassFinish.Glass, 120.dp, 44.dp, label = "Capsule", fontSize = 14.sp)
            }
            ShapeCell("Lens\nicon-only, knobs") {
                Spec(GlassFinish.Clear, 60.dp, 60.dp, shape = SeaGlassShape.Lens, icon = "more_horiz", iconSize = 26.dp)
            }
            ShapeCell("Pebble\nFAB only") {
                Spec(GlassFinish.Glass, 76.dp, 76.dp, shape = SeaGlassShape.Pebble, role = role(GlassRole.Seafoam), icon = "edit", filled = true, iconSize = 32.dp)
            }
            ShapeCell("Pane\n24 · 28 · 32 · 36 dp") {
                Spec(GlassFinish.Milk, 120.dp, 84.dp, shape = SeaGlassShape.Pane(28.dp), interactive = false)
            }
            ShapeCell("Concentric\ninner = 28 − 8 inset") {
                Box {
                    Spec(GlassFinish.Milk, 120.dp, 84.dp, shape = SeaGlassShape.Pane(28.dp), interactive = false)
                    Spec(
                        GlassFinish.Frost, 104.dp, 44.dp, shape = SeaGlassShape.Pane(20.dp), label = "r 20", fontSize = 13.sp,
                        modifier = Modifier.offset(8.dp, 20.dp), interactive = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShapeCell(caption: String, specimen: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(width = 130.dp, height = 90.dp), contentAlignment = Alignment.Center) { specimen() }
        Text(
            caption,
            Modifier.size(width = 130.dp, height = 40.dp),
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
