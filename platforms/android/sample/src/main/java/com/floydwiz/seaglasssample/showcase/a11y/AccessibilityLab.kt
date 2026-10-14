package com.floydwiz.seaglasssample.showcase.a11y

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.input.GlassTextField
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.GlassSlider
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.components.selection.SegmentedOption
import com.floydwiz.seaglass.material.AdaptiveSample
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.theme.SeaGlassContrast
import com.floydwiz.seaglasssample.showcase.LabTheme
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.control.LocalControls
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoStrip
import com.floydwiz.seaglasssample.showcase.screens.inbox.InboxScreen
import kotlinx.coroutines.launch

private class Mode(val label: String, val contrast: SeaGlassContrast, val reduceTransparency: Boolean)

private val Modes = listOf(
    Mode("Standard", SeaGlassContrast.Standard, false), Mode("Medium", SeaGlassContrast.Medium, false),
    Mode("High", SeaGlassContrast.High, false), Mode("Reduced", SeaGlassContrast.Standard, true),
)

private const val TwoByTwoFromDp = 840

/** Accessibility lab: the Inbox in every mode at once, adaptive tint with its sampled luminance, and the audit rules. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccessibilityLab(contentPadding: PaddingValues) {
    val wide = LocalConfiguration.current.screenWidthDp >= TwoByTwoFromDp
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(contentPadding).padding(horizontal = 16.dp).padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Heading("Four modes", "The Inbox in Standard, Medium contrast, High contrast and Reduce transparency, each in its own theme scope so they show at the same time.")
        if (wide) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Modes.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { mode -> ModeFrame(mode, Modifier.weight(1f).height(520.dp)) }
                    }
                }
            }
        } else {
            val pager = rememberPagerState { Modes.size }
            val scope = rememberCoroutineScope()
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                GlassSegmentedControl(
                    Modes.map { SegmentedOption(it.label) }, pager.currentPage, { scope.launch { pager.animateScrollToPage(it) } },
                    segmentWidth = (maxWidth - 8.dp) / Modes.size,
                )
            }
            HorizontalPager(pager, Modifier.fillMaxWidth().height(640.dp), pageSpacing = 12.dp) { page -> ModeFrame(Modes[page], Modifier.fillMaxSize()) }
        }

        Heading(
            "Adaptive tint",
            "Floating dark clear glass samples the luminance behind it. Above 0.4 it fades in a protective tint; the readout shows the 75th percentile of the sampled luminance, that value after the finish's own brightness lift, and the tint chosen. Shown in the dark theme, where the recipes use it.",
        )
        BoardScope {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AdaptiveStrip("Bright backdrop · adaptive on", bright = true, adaptive = true)
                AdaptiveStrip("Bright backdrop · adaptive off", bright = true, adaptive = false)
                AdaptiveStrip("Dark backdrop · adaptive on", bright = false, adaptive = true)
                AdaptiveStrip("Dark backdrop · adaptive off", bright = false, adaptive = false)
            }
        }

        Heading("Rules from the audit", "Each rule with a specimen, in the current theme and mode.")
        Rules()
    }
}

@Composable
private fun Heading(title: String, note: String) {
    Column(Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ModeFrame(mode: Mode, modifier: Modifier) {
    val controls = LocalControls.current
    LabTheme(
        dark = LocalSeaGlassDark.current,
        contrast = mode.contrast,
        reduceTransparency = mode.reduceTransparency,
        dynamicColor = controls.dynamicColor,
        seedRole = controls.seedRole,
        layers = controls.layers,
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Box(modifier.clip(RoundedCornerShape(24.dp)).consumeWindowInsets(WindowInsets.systemBars)) {
                InboxScreen()
                Text(
                    mode.label, Modifier.align(Alignment.BottomStart).padding(12.dp).background(MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 3.dp),
                    color = MaterialTheme.colorScheme.inverseOnSurface, fontSize = 11.sp, fontWeight = FontWeight(650),
                )
            }
        }
    }
}

@Composable
private fun BoardScope(content: @Composable () -> Unit) {
    val controls = LocalControls.current
    LabTheme(
        dark = true, contrast = controls.effectiveContrast, reduceTransparency = controls.effectiveReduceTransparency,
        dynamicColor = controls.dynamicColor, seedRole = controls.seedRole, layers = controls.layers,
    ) { CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface, content = content) }
}

@Composable
private fun AdaptiveStrip(label: String, bright: Boolean, adaptive: Boolean) {
    var sample by remember { mutableStateOf<AdaptiveSample?>(null) }
    val dark = MaterialTheme.colorScheme.surface
    GlassBackdrop(
        Modifier.fillMaxWidth().height(156.dp).clip(RoundedCornerShape(24.dp)),
        backdrop = {
            if (bright) Image(painterResource(R.drawable.seaglass_photo), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Box(Modifier.fillMaxSize().background(dark))
        },
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassSurface(
                GlassFinish.Clear, Modifier.size(236.dp, 52.dp), SeaGlassShape.Capsule, adaptive = adaptive, reactsTo = emptySet(),
                onAdaptiveSample = { sample = it },
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf("photo_library", "photo_album", "auto_awesome", "search").forEach { SymbolIcon(it, size = 24.dp, tint = LocalContentColor.current) }
                }
            }
            val current = sample
            Text(
                "$label\n" + if (!adaptive) "adaptive tint off" else if (current == null) "sampling…" else
                    "luminance %.2f → %.2f · threshold 0.40 · tint %d%%".format(current.luminance, current.lifted, (current.amount * 100).toInt()),
                color = if (bright) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight(600),
                modifier = if (bright) Modifier.background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.85f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp) else Modifier,
            )
        }
    }
}

@Composable
private fun Rules() {
    val roles = LocalRolePalettes.current
    var on by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf(0.6f) }
    var text by remember { mutableStateOf("") }
    val dark = LocalSeaGlassDark.current
    Rule("Placeholders use the secondary text colour at full strength, never 60 to 75% opacity.") {
        GlassTextField(text, { text = it }, "Email", Modifier.fillMaxWidth(), placeholder = "name@example.com", leadingIcon = "mail")
    }
    Rule("Symbols on photos sit in a small ink bead.") {
        PhotoStrip(dark, 384.dp, 96.dp, crop = 3) {
            GlassSurface(GlassFinish.Ink, Modifier.offset(16.dp, 54.dp).size(28.dp), SeaGlassShape.Round, reactsTo = emptySet()) {
                SymbolIcon("favorite", size = 16.dp, fill = true, tint = roles[GlassRole.SeaPink][if (dark) 40 else 80])
            }
        }
    }
    Rule("In the light theme the liquid is about tone 45 with white content on it.") {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            GlassSwitch(on, { on = it })
            GlassSlider(value, { value = it }, Modifier.weight(1f))
        }
    }
    Rule("Wells always have the 1.5 dp lip.") {
        GlassSurface(GlassFinish.Well, Modifier.size(240.dp, 48.dp), SeaGlassShape.Capsule, reactsTo = emptySet())
    }
}

@Composable
private fun Rule(text: String, specimen: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, color = glassOnSurfaceVariant(), fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight(550))
        specimen()
    }
}
