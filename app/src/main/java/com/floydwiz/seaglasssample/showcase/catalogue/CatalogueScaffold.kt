package com.floydwiz.seaglasssample.showcase.catalogue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.glassBoardBackground
import com.floydwiz.seaglass.theme.SeaGlassTheme
import com.floydwiz.seaglasssample.control.LocalControls

/** State chosen in the picker; applied to every specimen that does not set its own. */
val LocalCatalogueState = compositionLocalOf { GlassState.Rest }

private val WideBreakpoint = 600.dp

/** Scrolling catalogue page with the state picker pinned at the top of the content. */
@Composable
fun CatalogueScreen(contentPadding: PaddingValues, content: @Composable ColumnScope.() -> Unit) {
    var picked by rememberSaveable { mutableStateOf(GlassState.Rest) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            Text("State", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                GlassState.entries.forEachIndexed { index, state ->
                    SegmentedButton(
                        selected = state == picked,
                        onClick = { picked = state },
                        shape = SegmentedButtonDefaults.itemShape(index, GlassState.entries.size),
                    ) { Text(state.name, maxLines = 1, fontSize = 11.sp) }
                }
            }
        }
        CompositionLocalProvider(LocalCatalogueState provides picked) { content() }
    }
}

@Composable
fun CatalogueSection(title: String, grade: String, note: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text(
                grade,
                style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        content()
    }
}

/** Theme scope for one panel or strip: same controls as the app, with [dark] chosen locally. */
@Composable
fun BoardTheme(dark: Boolean, content: @Composable () -> Unit) {
    val controls = LocalControls.current
    SeaGlassTheme(
        dark = dark,
        contrast = controls.contrast,
        reduceTransparency = controls.reduceTransparency,
        dynamicColor = controls.dynamicColor,
        seedRole = controls.seedRole,
        layers = controls.layers,
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface, content = content)
    }
}

/** Light and dark panel side by side from 600 dp wide, stacked below. */
@Composable
fun ThemePanels(content: @Composable ColumnScope.(dark: Boolean) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth >= WideBreakpoint) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ThemePanel(false, Modifier.weight(1f), content)
                ThemePanel(true, Modifier.weight(1f), content)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ThemePanel(false, Modifier.fillMaxWidth(), content)
                ThemePanel(true, Modifier.fillMaxWidth(), content)
            }
        }
    }
}

@Composable
private fun ThemePanel(dark: Boolean, modifier: Modifier, content: @Composable ColumnScope.(Boolean) -> Unit) {
    val picked = LocalCatalogueState.current
    BoardTheme(dark) {
        CompositionLocalProvider(LocalForcedGlassState provides picked.takeUnless { it == GlassState.Rest }) {
            GlassBackdrop(
                modifier = modifier.clip(RoundedCornerShape(24.dp)),
                backdrop = { androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().glassBoardBackground(dark)) },
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        if (dark) "DARK" else "LIGHT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    content(dark)
                }
            }
        }
    }
}

/** One specimen strip on its own board backdrop, themed light or dark independently of the app. */
@Composable
fun ThemeStrip(dark: Boolean, modifier: Modifier = Modifier, height: Dp = 68.dp, content: @Composable RowScope.() -> Unit) {
    BoardTheme(dark) {
        GlassBackdrop(
            modifier = modifier.height(height).clip(RoundedCornerShape(18.dp)),
            backdrop = { androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().glassBoardBackground(dark)) },
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp).fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

/** Light and dark [ThemeStrip]s side by side from 600 dp wide, stacked below. */
@Composable
fun ThemeStripPair(height: Dp = 68.dp, content: @Composable RowScope.(dark: Boolean) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth >= WideBreakpoint) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ThemeStrip(false, Modifier.weight(1f), height) { content(false) }
                ThemeStrip(true, Modifier.weight(1f), height) { content(true) }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeStrip(false, Modifier.fillMaxWidth(), height) { content(false) }
                ThemeStrip(true, Modifier.fillMaxWidth(), height) { content(true) }
            }
        }
    }
}
