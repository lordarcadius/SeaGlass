package com.floydwiz.seaglasssample.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.containers.GlassBottomSheet
import com.floydwiz.seaglass.components.selection.GlassFilterChip
import com.floydwiz.seaglass.components.selection.GlassSegmentedControl
import com.floydwiz.seaglass.components.selection.GlassSwitch
import com.floydwiz.seaglass.components.selection.SegmentedOption
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.theme.SeaGlassContrast

/** The control sheet: a milk bottom sheet whose changes apply live to the page behind it. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ControlSheet(state: ControlState, onDismiss: () -> Unit) {
    GlassBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text("Controls", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight(700))

            Section("Theme") {
                Segmented(ThemeMode.entries, state.themeMode, { it.label }) { state.themeMode = it }
            }
            Section("Contrast") {
                Segmented(listOf<SeaGlassContrast?>(null) + SeaGlassContrast.entries, state.contrast, { it?.name ?: "System" }) { state.contrast = it }
            }
            SwitchRow("Reduce transparency", state.reduceTransparency) { state.reduceTransparency = it }
            SwitchRow("Reduce when system blur is off", state.autoReduceTransparency) { state.autoReduceTransparency = it }
            SwitchRow("Dynamic colour", state.dynamicColor) { state.dynamicColor = it }
            SwitchRow("Animate light drift", state.lightDrift) { state.lightDrift = it }
            Section("Seed role") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassRole.entries.forEach { role ->
                        GlassFilterChip(state.seedRole == role, { state.seedRole = role }, role.label)
                    }
                }
            }
            Section("Glass layers (debug)") {
                val layers = state.layers
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SwitchRow("Shadow", layers.shadow) { state.layers = layers.copy(shadow = it) }
                    SwitchRow("Caustic", layers.caustic) { state.layers = layers.copy(caustic = it) }
                    SwitchRow("Contact shadow", layers.contact) { state.layers = layers.copy(contact = it) }
                    SwitchRow("Body (blur + tint)", layers.body) { state.layers = layers.copy(body = it) }
                    SwitchRow("Light pool", layers.light) { state.layers = layers.copy(light = it) }
                    SwitchRow("Grain", layers.grain) { state.layers = layers.copy(grain = it) }
                    SwitchRow("Volume shadows", layers.volume) { state.layers = layers.copy(volume = it) }
                    SwitchRow("Rim", layers.rim) { state.layers = layers.copy(rim = it) }
                }
            }
            SwitchRow("FPS overlay (debug)", state.fpsOverlay) { state.fpsOverlay = it }
            Text(
                "Layers and the FPS overlay are debugging aids: the overlay reads JankStats frame durations, which include GPU time on API 31 and later.",
                color = glassOnSurfaceVariant(), fontSize = 12.5.sp, lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(650))
        content()
    }
}

@Composable
private fun <T> Segmented(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        GlassSegmentedControl(
            options.map { SegmentedOption(label(it)) }, options.indexOf(selected).coerceAtLeast(0), { onSelect(options[it]) },
            segmentWidth = (maxWidth - 8.dp) / options.size,
        )
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        GlassSwitch(checked, onChange)
    }
}
