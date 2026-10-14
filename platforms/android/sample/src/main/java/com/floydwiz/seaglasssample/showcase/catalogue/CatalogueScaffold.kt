package com.floydwiz.seaglasssample.showcase.catalogue

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.floydwiz.seaglasssample.showcase.glassBoardBackground
import com.floydwiz.seaglasssample.showcase.LabTheme
import com.floydwiz.seaglasssample.control.LocalControls

/** State chosen in the picker; applied to every specimen that does not set its own. */
val LocalCatalogueState = compositionLocalOf { GlassState.Rest }

private val WideBreakpoint = 600.dp

/** Scrolling catalogue page with the state picker pinned at the top of the content unless [statePicker] is false. */
@Composable
fun CatalogueScreen(contentPadding: PaddingValues, statePicker: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
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
        if (statePicker) {
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
    LabTheme(
        dark = dark,
        contrast = controls.effectiveContrast,
        reduceTransparency = controls.effectiveReduceTransparency,
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

class TableRow(val label: String, val sub: String? = null, val height: Dp = 56.dp, val cells: List<@Composable () -> Unit>)

/**
 * Specimen grid for [ThemePanels]: a label column and one column per head.
 * Scrolls horizontally when the columns do not fit.
 */
@Composable
fun SpecimenTable(
    heads: List<String>,
    rows: List<TableRow>,
    labelWidth: Dp = 88.dp,
    cellWidth: Dp = 120.dp,
    labelsAbove: Boolean = false,
) {
    Column(Modifier.horizontalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(if (labelsAbove) 16.dp else 10.dp)) {
        if (heads.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.width(labelWidth))
                heads.forEach {
                    Text(
                        it, Modifier.width(cellWidth), fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        rows.forEach { row ->
            val label: @Composable (Modifier) -> Unit = { modifier ->
                Column(modifier) {
                    Text(row.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    if (row.sub != null) Text(row.sub, fontSize = 10.sp, lineHeight = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            val cells: @Composable () -> Unit = {
                row.cells.forEach { cell ->
                    Box(Modifier.width(cellWidth).height(row.height), contentAlignment = if (labelsAbove) Alignment.CenterStart else Alignment.Center) {
                        Box(Modifier.wrapContentSize(unbounded = true)) { cell() }
                    }
                }
            }
            if (labelsAbove) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    label(Modifier)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { cells() }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    label(Modifier.width(labelWidth))
                    cells()
                }
            }
        }
    }
}

/** One table row per state; each cell is shown in that state. */
fun stateRows(
    labelFor: (GlassState) -> String = { it.name },
    subFor: (GlassState) -> String? = { null },
    height: Dp = 56.dp,
    states: List<GlassState> = GlassState.entries,
    cells: List<@Composable (GlassState) -> Unit>,
): List<TableRow> = states.map { state ->
    TableRow(labelFor(state), subFor(state), height, cells.map { cell ->
        {
            CompositionLocalProvider(LocalForcedGlassState provides state.takeUnless { it == GlassState.Rest }) { cell(state) }
        }
    })
}

/** Photo crop used as the backdrop of glass-over-imagery specimens; dark themes dim it as the boards do. */
@Composable
fun PhotoStrip(dark: Boolean, width: Dp, height: Dp, crop: Int, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val (cx, cy) = PhotoCrops[crop % PhotoCrops.size]
    GlassBackdrop(
        modifier = modifier.size(width, height).clip(RoundedCornerShape(24.dp)),
        backdrop = {
            Image(
                painter = painterResource(com.floydwiz.seaglasssample.R.drawable.seaglass_photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = if (dark) ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                    0.42f * (0.213f + 0.787f * 1.25f), 0.42f * (0.715f - 0.715f * 1.25f), 0.42f * (0.072f - 0.072f * 1.25f), 0f, 0f,
                    0.42f * (0.213f - 0.213f * 1.25f), 0.42f * (0.715f + 0.285f * 1.25f), 0.42f * (0.072f - 0.072f * 1.25f), 0f, 0f,
                    0.42f * (0.213f - 0.213f * 1.25f), 0.42f * (0.715f - 0.715f * 1.25f), 0.42f * (0.072f + 0.928f * 1.25f), 0f, 0f,
                    0f, 0f, 0f, 1f, 0f,
                ))) else null,
                modifier = Modifier.fillMaxSize().graphicsLayer {
                    scaleX = 2.6f
                    scaleY = 2.6f
                    transformOrigin = TransformOrigin(cx / 1200f, cy / 1200f)
                },
            )
        },
        content = content,
    )
}

val PhotoCrops = listOf(
    130 to 100, 520 to 130, 780 to 270, 250 to 620, 950 to 430, 1030 to 770, 330 to 350,
    250 to 920, 670 to 930, 1050 to 110, 1000 to 1150, 430 to 1130, 720 to 50, 630 to 600,
)

/** A crop of the sample photo as a plain content tile (no glass), zoomed on one of [PhotoCrops]. */
@Composable
fun PhotoTile(index: Int, modifier: Modifier = Modifier, shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(0.dp), zoom: Float = 2.6f, dim: Boolean = false) {
    val (cx, cy) = PhotoCrops[index % PhotoCrops.size]
    Box(modifier.clip(shape)) {
        Image(
            painter = painterResource(com.floydwiz.seaglasssample.R.drawable.seaglass_photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = if (dim) ColorFilter.colorMatrix(ColorMatrix().apply { setToScale(0.55f, 0.55f, 0.55f, 1f) }) else null,
            modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = zoom
                scaleY = zoom
                transformOrigin = TransformOrigin(cx / 1200f, cy / 1200f)
            },
        )
    }
}
