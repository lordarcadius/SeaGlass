package com.floydwiz.seaglasssample.showcase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.components.icon.SymbolIcon

/** A list of destinations in titled sections; rows are disabled while [enabled] says the destination is not built yet. */
@Composable
fun DestinationList(
    contentPadding: PaddingValues,
    sections: List<Pair<String, List<Destination>>>,
    onOpen: (Destination) -> Unit,
    enabled: (Destination) -> Boolean = { true },
) {
    LazyColumn(contentPadding = contentPadding) {
        sections.forEach { (title, destinations) ->
            item { SectionHeader(title) }
            items(destinations) { DestinationRow(it, enabled(it), onOpen) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun DestinationRow(destination: Destination, enabled: Boolean, onOpen: (Destination) -> Unit) {
    ListItem(
        modifier = Modifier.alpha(if (enabled) 1f else 0.5f).clickable(enabled = enabled) { onOpen(destination) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = destination.code,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        headlineContent = { Text(destination.title) },
        supportingContent = { Text(if (enabled) destination.summary else "${destination.summary} · built in milestone M${destination.milestone}") },
        trailingContent = { SymbolIcon("chevron_right", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
    )
}
