package com.floydwiz.seaglasssample.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.theme.SeaGlassContrast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlSheet(state: ControlState, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text("Controls", style = MaterialTheme.typography.titleLarge)

            Section("Theme") {
                Segmented(ThemeMode.entries, state.themeMode, { it.label }) { state.themeMode = it }
            }
            Section("Contrast") {
                Segmented(SeaGlassContrast.entries, state.contrast, { it.name }) { state.contrast = it }
            }
            SwitchRow("Reduce transparency", state.reduceTransparency) { state.reduceTransparency = it }
            SwitchRow("Dynamic colour", state.dynamicColor) { state.dynamicColor = it }
            Section("Seed role") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassRole.entries.chunked(4).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { role ->
                                FilterChip(
                                    selected = state.seedRole == role,
                                    onClick = { state.seedRole = role },
                                    label = { Text(role.label) },
                                )
                            }
                        }
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
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        content()
    }
}

@Composable
private fun <T> Segmented(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
            ) { Text(label(option)) }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
