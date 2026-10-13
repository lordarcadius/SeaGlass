package com.floydwiz.seaglass.components.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.selection.trackAxisDrag
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.SeaGlassTokens
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
private val BarHeight = 62.dp

/**
 * Floating tab bar: a clear capsule with the current tab as a frost lens and search in its own lens beside it.
 * - Pressing or dragging turns the lens clear, 100×74, bulging past the bar, and enlarges the tab beneath it.
 * - [minimized] condenses it to a current-tab lens, an [accessory] capsule and the search lens.
 */
@Composable
fun GlassTabBar(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minimized: Boolean = false,
    accessory: (@Composable () -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onExpand: () -> Unit = {},
    previewStates: Map<Int, GlassState> = emptyMap(),
    role: TonalTones = LocalGlassRole.current,
) {
    AnimatedContent(
        targetState = minimized,
        modifier = modifier,
        transitionSpec = { fadeIn(SeaGlassTokens.Motion.effectsDefault()) togetherWith fadeOut(SeaGlassTokens.Motion.effectsDefault()) },
        label = "tabBarMinimize",
    ) { isMinimized ->
        if (isMinimized) {
            MinimizedBar(tabs, selectedIndex, accessory, onSearch, onExpand, role)
        } else {
            FullBar(tabs, selectedIndex, onSelect, onSearch, previewStates, role)
        }
    }
}

@Composable
private fun FullBar(tabs: List<TabItem>, selectedIndex: Int, onSelect: (Int) -> Unit, onSearch: (() -> Unit)?, previewStates: Map<Int, GlassState>, role: TonalTones) {
    val density = LocalDensity.current.density
    val tabPx = TabWidth.value * density
    val insetPx = 4f * density
    val count = tabs.size
    val scope = rememberCoroutineScope()
    val forcedPress = LocalForcedGlassState.current == GlassState.Press
    val lensLeft = remember { Animatable(insetPx + selectedIndex * tabPx) }
    var interacting by remember { mutableStateOf(false) }
    LaunchedEffect(selectedIndex, tabPx, interacting) {
        if (!interacting) lensLeft.animateTo(insetPx + selectedIndex * tabPx, SeaGlassTokens.Motion.spatialDefault())
    }
    val pressing = interacting || forcedPress
    val pressAmount by animateFloatAsState(if (pressing) 1f else 0f, SeaGlassTokens.Motion.spatialFast(), label = "tabLens")
    val hot = ((lensLeft.value - insetPx) / tabPx).roundToInt().coerceIn(0, count - 1)
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) Content.OnSurfaceDark else Content.OnSurfaceLight

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(TabWidth * count + 8.dp, BarHeight)
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
                            onPress = { interacting = true; scope.launch { lensLeft.snapTo(target(it.x)) } },
                            onMove = { lastX = it.x; scope.launch { lensLeft.snapTo(target(it.x)) } },
                            onTap = { interacting = false; onSelect(indexAt(it.x)) },
                            onCancel = { interacting = false },
                            onEnd = { interacting = false; onSelect(indexAt(lastX)) },
                        )
                    }
                },
        ) {
            GlassSurface(GlassFinish.Clear, Modifier.size(TabWidth * count + 8.dp, BarHeight), SeaGlassShape.Capsule, role, reactsTo = emptySet())
            CompositionLocalProvider(LocalForcedGlassState provides null) {
                GlassSurface(
                    finish = GlassFinish.Frost,
                    modifier = Modifier
                        .offset { IntOffset(lensLeft.value.roundToInt(), 4.dp.roundToPx()) }
                        .size(TabWidth, BarHeight - 8.dp)
                        .graphicsLayer { alpha = 1f - pressAmount },
                    shape = SeaGlassShape.Pane((BarHeight - 8.dp) / 2),
                    role = role,
                    reactsTo = emptySet(),
                )
                if (pressAmount > 0.01f) {
                    GlassSurface(
                        finish = GlassFinish.Lens,
                        modifier = Modifier
                            .offset { IntOffset((lensLeft.value - 12.dp.toPx()).roundToInt(), (-6).dp.roundToPx()) }
                            .size(100.dp, 74.dp)
                            .graphicsLayer { alpha = pressAmount },
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
                    Box(Modifier.size(TabWidth, BarHeight - 8.dp).hoverable(itemSource).semantics { this.selected = index == selectedIndex }) {
                        CompositionLocalProvider(LocalForcedGlassState provides previewStates[index]) {
                        GlassSurface(
                            finish = GlassFinish.Plain,
                            modifier = Modifier.size(TabWidth, BarHeight - 8.dp),
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
