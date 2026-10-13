package com.floydwiz.seaglass.components.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.selection.trackAxisDrag
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.StretchLens
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.material.SeaGlassTokens.Content
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class TabItem(val icon: String, val label: String)

/** Minimized state driven by scroll direction; attach [connection] to the scrolling content with `nestedScroll`. */
@Stable
class TabBarState {
    var minimized by mutableStateOf(false)

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y < -12f) minimized = true else if (available.y > 12f) minimized = false
            return Offset.Zero
        }
    }
}

@Composable
fun rememberTabBarState() = remember { TabBarState() }

private val TabWidth = 76.dp
private const val FullId = "tabBarFull"
private const val MinimizedId = "tabBarMinimized"
private val BarHeight = 62.dp

/**
 * Floating tab bar: a clear capsule with the current tab as a frost lens and search in its own lens beside it.
 * - Pressing or dragging turns the lens clear, 100×74, bulging past the bar, and enlarges the tab beneath it.
 * - [minimized] condenses it to a current-tab lens, an [accessory] capsule and the search lens.
 * - [tabWidth] is the width of each tab, 76 dp for four or more tabs and 92 dp for three.
 */
@Composable
fun GlassTabBar(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minimized: Boolean = false,
    tabWidth: Dp = TabWidth,
    accessory: (@Composable () -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onExpand: () -> Unit = {},
    previewStates: Map<Int, GlassState> = emptyMap(),
    role: TonalTones = LocalGlassRole.current,
) {
    val geometry by animateFloatAsState(if (minimized) 1f else 0f, SeaGlassTokens.Motion.spatialDefault(), label = "tabBarMinimize")
    val fade by animateFloatAsState(if (minimized) 1f else 0f, SeaGlassTokens.Motion.effectsDefault(), label = "tabBarFade")
    val showFull by remember { derivedStateOf { fade < 0.99f } }
    val showMinimized by remember { derivedStateOf { fade > 0.01f } }
    Layout(
        content = {
            if (showFull) {
                Box(
                    Modifier.layoutId(FullId).graphicsLayer {
                        alpha = 1f - fade.coerceIn(0f, 1f)
                        scaleX = lerp(1f, 0.3f, geometry)
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    },
                ) { FullBar(tabs, selectedIndex, onSelect, onSearch, previewStates, tabWidth, role) }
            }
            if (showMinimized) {
                Box(
                    Modifier.layoutId(MinimizedId).graphicsLayer {
                        alpha = fade.coerceIn(0f, 1f)
                        scaleX = lerp(0.6f, 1f, geometry)
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    },
                ) { MinimizedBar(tabs, selectedIndex, accessory, onSearch, onExpand, role) }
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val full = measurables.firstOrNull { it.layoutId == FullId }?.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val minimizedBar = measurables.firstOrNull { it.layoutId == MinimizedId }?.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val from = full ?: minimizedBar!!
        val to = minimizedBar ?: full!!
        val width = lerp(from.width.toFloat(), to.width.toFloat(), if (full != null && minimizedBar != null) geometry else 0f).roundToInt()
        val height = lerp(from.height.toFloat(), to.height.toFloat(), if (full != null && minimizedBar != null) geometry else 0f).roundToInt()
        layout(width, height) {
            full?.place(0, (height - full.height) / 2)
            minimizedBar?.place(0, (height - minimizedBar.height) / 2)
        }
    }
}

@Composable
private fun FullBar(tabs: List<TabItem>, selectedIndex: Int, onSelect: (Int) -> Unit, onSearch: (() -> Unit)?, previewStates: Map<Int, GlassState>, tabWidth: Dp, role: TonalTones) {
    val density = LocalDensity.current.density
    val tabPx = tabWidth.value * density
    val insetPx = 4f * density
    val count = tabs.size
    val scope = rememberCoroutineScope()
    val forcedPress = LocalForcedGlassState.current == GlassState.Press
    val lens = remember { StretchLens(insetPx + selectedIndex * tabPx, tabPx) }
    var interacting by remember { mutableStateOf(false) }
    LaunchedEffect(selectedIndex, tabPx, interacting) {
        if (!interacting) lens.settle(insetPx + selectedIndex * tabPx, tabPx)
    }
    val pressing = interacting || forcedPress
    val pressAmount by animateFloatAsState(if (pressing) 1f else 0f, SeaGlassTokens.Motion.spatialFast(), label = "tabLens")
    val hot by remember(count, tabPx) {
        derivedStateOf { ((lens.centre() - tabPx / 2f - insetPx) / tabPx).roundToInt().coerceIn(0, count - 1) }
    }
    val showLens by remember { derivedStateOf { pressAmount > 0.01f } }
    val barPx = (tabWidth * count + 8.dp).value * density
    val bulgePx = 6f * density
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) Content.OnSurfaceDark else Content.OnSurfaceLight

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(tabWidth * count + 8.dp, BarHeight)
                .semantics { this.role = Role.Tab }
                .pointerInput(count, tabPx) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val maxLeft = insetPx + (count - 1) * tabPx
                        fun target(x: Float) = (x - tabPx / 2f).coerceIn(insetPx, maxLeft)
                        fun indexAt(x: Float) = ((target(x) - insetPx) / tabPx).roundToInt().coerceIn(0, count - 1)
                        var lastX = down.position.x
                        trackAxisDrag(
                            down, horizontal = true,
                            onPress = { interacting = true; scope.launch { lens.drag(target(it.x), tabPx) } },
                            onMove = { lastX = it.x; scope.launch { lens.drag(target(it.x), tabPx) } },
                            onTap = { interacting = false; onSelect(indexAt(it.x)) },
                            onCancel = { interacting = false },
                            onEnd = { interacting = false; onSelect(indexAt(lastX)) },
                        )
                    }
                },
        ) {
            GlassSurface(GlassFinish.Clear, Modifier.size(tabWidth * count + 8.dp, BarHeight), SeaGlassShape.Capsule, role, reactsTo = emptySet())
            CompositionLocalProvider(LocalForcedGlassState provides null) {
                GlassSurface(
                    finish = GlassFinish.Frost,
                    modifier = Modifier
                        .offset(y = 4.dp)
                        .size(tabWidth, BarHeight - 8.dp)
                        .graphicsLayer {
                            val left = lens.leftEdge(-bulgePx)
                            translationX = left
                            scaleX = (lens.rightEdge(barPx + bulgePx) - left) / tabPx
                            transformOrigin = TransformOrigin(0f, 0.5f)
                            alpha = 1f - pressAmount.coerceIn(0f, 1f)
                        },
                    shape = SeaGlassShape.Pane((BarHeight - 8.dp) / 2),
                    role = role,
                    reactsTo = emptySet(),
                )
                if (showLens) {
                    GlassSurface(
                        finish = GlassFinish.Lens,
                        modifier = Modifier
                            .offset(y = (-6).dp)
                            .size(100.dp, 74.dp)
                            .graphicsLayer {
                                val left = lens.leftEdge(-bulgePx) - 12.dp.toPx()
                                translationX = left
                                scaleX = (lens.rightEdge(barPx + bulgePx) + 12.dp.toPx() - left) / (tabPx + 24.dp.toPx())
                                transformOrigin = TransformOrigin(0f, 0.5f)
                                alpha = pressAmount.coerceIn(0f, 1f)
                            },
                        shape = SeaGlassShape.Pane(37.dp),
                        role = role,
                        reactsTo = emptySet(),
                    )
                }
            }
            Row(Modifier.padding(4.dp)) {
                tabs.forEachIndexed { index, tab ->
                    val selected = !pressing && index == selectedIndex
                    val magnified = pressing && index == hot
                    val color = if (selected) glassOnRole(role) else ink
                    val itemSource = remember { MutableInteractionSource() }
                    Box(Modifier.size(tabWidth, BarHeight - 8.dp).hoverable(itemSource).semantics { this.selected = index == selectedIndex }) {
                        CompositionLocalProvider(LocalForcedGlassState provides previewStates[index]) {
                        GlassSurface(
                            finish = GlassFinish.Plain,
                            modifier = Modifier.size(tabWidth, BarHeight - 8.dp),
                            shape = SeaGlassShape.Pane((BarHeight - 8.dp) / 2),
                            interactionSource = itemSource,
                            reactsTo = setOf(GlassState.Hover, GlassState.Focus),
                            contentColor = color,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(if (magnified) 3.dp else 2.dp)) {
                                SymbolIcon(
                                    tab.icon, size = if (magnified) 29.dp else 24.dp, fill = selected || magnified,
                                    tint = if (magnified) glassAccent(role) else color,
                                )
                                Text(tab.label, color = color, fontSize = if (magnified) 12.5.sp else 11.sp, fontWeight = FontWeight(650), maxLines = 1)
                            }
                        }
                        }
                    }
                }
            }
        }
        if (onSearch != null) SearchLens(62.dp, onSearch, role)
    }
}

@Composable
private fun SearchLens(size: androidx.compose.ui.unit.Dp, onClick: () -> Unit, role: TonalTones) {
    GlassSurface(GlassFinish.Clear, Modifier.size(size), SeaGlassShape.Lens, role, onClick = onClick) {
        SymbolIcon("search", size = 26.dp, tint = LocalContentColor.current, contentDescription = "Search")
    }
}

@Composable
private fun MinimizedBar(tabs: List<TabItem>, selectedIndex: Int, accessory: (@Composable () -> Unit)?, onSearch: (() -> Unit)?, onExpand: () -> Unit, role: TonalTones) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassSurface(
            finish = GlassFinish.Clear,
            modifier = Modifier.size(56.dp),
            shape = SeaGlassShape.Lens,
            role = role,
            onClick = onExpand,
            contentColor = glassAccent(role),
        ) {
            SymbolIcon(tabs[selectedIndex].icon, size = 26.dp, fill = true, tint = LocalContentColor.current, contentDescription = tabs[selectedIndex].label)
        }
        GlassSurface(
            finish = GlassFinish.Clear,
            modifier = Modifier.size(236.dp, 56.dp),
            shape = SeaGlassShape.Capsule,
            role = role,
            reactsTo = emptySet(),
            contentAlignment = Alignment.CenterStart,
        ) { accessory?.invoke() }
        if (onSearch != null) SearchLens(56.dp, onSearch, role)
    }
}
