package com.floydwiz.seaglass.components.input

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

private val RowHeight = 40.dp

/**
 * Time wheel on a milk pane (340×232, radius 32) with a frost band at the centre row.
 * Values shrink and fade as they turn away from the band: 22 sp, 18 sp at 75%, 15 sp at 38%.
 */
@Composable
fun GlassTimeWheel(
    hour: Int,
    minute: Int,
    pm: Boolean,
    onChange: (hour: Int, minute: Int, pm: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    role: TonalTones = LocalGlassRole.current,
) {
    val hours = remember { (1..12).map { it.toString() } }
    val minutes = remember { (0..55 step 5).map { "%02d".format(it) } }
    val meridiem = remember { listOf("AM", "PM") }
    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier.size(340.dp, 232.dp),
        shape = SeaGlassShape.Pane(32.dp),
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.TopStart,
    ) {
        GlassSurface(
            finish = GlassFinish.Frost,
            modifier = Modifier.offset(20.dp, 96.dp).size(300.dp, 40.dp),
            shape = SeaGlassShape.Pane(20.dp),
            role = role,
            reactsTo = emptySet(),
        )
        Wheel(hours, hour - 1, Modifier.offset(58.dp, 16.dp), role) { onChange(it + 1, minute, pm) }
        Wheel(minutes, minute / 5, Modifier.offset(138.dp, 16.dp), role) { onChange(hour, it * 5, pm) }
        Wheel(meridiem, if (pm) 1 else 0, Modifier.offset(218.dp, 16.dp), role) { onChange(hour, minute, it == 1) }
        Text(":", Modifier.offset(127.dp, 104.dp), color = glassOnRole(role), fontSize = 22.sp, fontWeight = FontWeight(650), lineHeight = 22.sp)
    }
}

@Composable
private fun Wheel(values: List<String>, selectedIndex: Int, modifier: Modifier, role: TonalTones, onSelect: (Int) -> Unit) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceIn(0, values.lastIndex))
    val rowPx = with(LocalDensity.current) { RowHeight.toPx() }
    val onBand = glassOnRole(role)
    val variant = glassOnSurfaceVariant()
    var settled by remember { mutableIntStateOf(selectedIndex) }

    LaunchedEffect(state) {
        snapshotFlow { Triple(state.isScrollInProgress, state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset) }
            .filter { !it.first }
            .distinctUntilChanged()
            .collect { (_, index, offset) ->
                val center = (index + if (offset > rowPx / 2f) 1 else 0).coerceIn(0, values.lastIndex)
                if (center != settled) {
                    settled = center
                    onSelect(center)
                }
            }
    }

    LazyColumn(
        state = state,
        flingBehavior = rememberSnapFlingBehavior(state),
        contentPadding = PaddingValues(vertical = RowHeight * 2),
        modifier = modifier.width(64.dp).height(RowHeight * 5),
    ) {
        itemsIndexed(values) { index, value ->
            val position by remember { derivedPosition(state, rowPx) }
            val distance = abs(index - position)
            val size = if (distance <= 1f) lerp(22f, 18f, distance) else lerp(18f, 15f, (distance - 1f).coerceAtMost(1f))
            val alpha = if (distance <= 1f) lerp(1f, 0.75f, distance) else lerp(0.75f, 0.38f, (distance - 1f).coerceAtMost(1f))
            Box(Modifier.fillMaxWidth().height(RowHeight), contentAlignment = Alignment.Center) {
                Text(
                    value,
                    color = (if (distance < 0.5f) onBand else variant).copy(alpha = if (distance < 0.5f) 1f else alpha),
                    fontSize = size.sp,
                    fontWeight = FontWeight(if (distance < 0.5f) 650 else 500),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun derivedPosition(state: androidx.compose.foundation.lazy.LazyListState, rowPx: Float) =
    androidx.compose.runtime.derivedStateOf { state.firstVisibleItemIndex + state.firstVisibleItemScrollOffset / rowPx }
