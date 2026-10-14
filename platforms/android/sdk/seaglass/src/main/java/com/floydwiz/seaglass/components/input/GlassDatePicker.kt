package com.floydwiz.seaglass.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import com.floydwiz.seaglass.tokens.SeaGlassRadiusTokens

public sealed interface DateSelection {
    public data class Single(val date: LocalDate?) : DateSelection
    public data class Range(val start: LocalDate?, val end: LocalDate?) : DateSelection
}

private val PickerWidth = 340.dp
private val CellSize = 44.dp

/**
 * Month picker on a milk pane (340 dp wide, radius 32).
 * - The chosen date is a 40 dp glass lens; today is an accent bold numeral with a 5 dp bead.
 * - A range is a tinted well joining two lenses.
 * - [onDateClick] receives taps; the caller owns the selection logic.
 */
@Composable
public fun GlassDatePicker(
    month: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    selection: DateSelection,
    onDateClick: (LocalDate) -> Unit,
    today: LocalDate,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    role: TonalTones = LocalGlassRole.current,
) {
    val ink = if (LocalSeaGlassDark.current) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    val variant = glassOnSurfaceVariant()
    val accent = glassAccent(role)
    val firstColumn = month.atDay(1).dayOfWeek.value % 7
    val days = month.lengthOfMonth()
    val rows = (firstColumn + days + 6) / 7
    val gridTop = 88.dp
    val buttonsTop = gridTop + CellSize * rows + 18.dp
    val height = buttonsTop + 40.dp + 18.dp

    fun inRange(date: LocalDate): Boolean {
        val r = selection as? DateSelection.Range ?: return false
        val s = r.start ?: return false
        val e = r.end ?: return false
        return !date.isBefore(s) && !date.isAfter(e)
    }
    fun isSelected(date: LocalDate) = when (selection) {
        is DateSelection.Single -> selection.date == date
        is DateSelection.Range -> selection.start == date || selection.end == date
    }

    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier.size(PickerWidth, height),
        shape = SeaGlassShape.Pane(SeaGlassRadiusTokens.Pane32),
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.TopStart,
    ) {
        Row(Modifier.offset(24.dp, 24.dp), Arrangement.spacedBy(6.dp), Alignment.CenterVertically) {
            Text(
                "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                color = ink, fontSize = 17.sp, fontWeight = FontWeight(650),
            )
            SymbolIcon("expand_more", size = 20.dp, tint = variant)
        }
        Row(Modifier.offset(PickerWidth - 100.dp, 18.dp)) {
            GlassIconButton("chevron_left", { onMonthChange(month.minusMonths(1)) }, style = GlassButtonStyle.Plain, size = 36.dp, iconScale = 0.61f, contentDescription = "Previous month")
            Box(Modifier.width(4.dp))
            GlassIconButton("chevron_right", { onMonthChange(month.plusMonths(1)) }, style = GlassButtonStyle.Plain, size = 36.dp, iconScale = 0.61f, contentDescription = "Next month")
        }
        Row(Modifier.offset(16.dp, 66.dp)) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(it, Modifier.width(CellSize), color = variant.copy(alpha = 0.8f), fontSize = 12.5.sp, fontWeight = FontWeight(650), textAlign = TextAlign.Center)
            }
        }
        val range = selection as? DateSelection.Range
        if (range?.start != null && range.end != null) {
            val firstIndex = firstColumn + maxOf(range.start.dayOfMonth, 1) - 1
            val lastIndex = firstColumn + minOf(range.end.dayOfMonth, days) - 1
            if (range.start.month == month.month || range.end.month == month.month) {
                val from = if (range.start.month == month.month && range.start.year == month.year) firstIndex else firstColumn
                val to = if (range.end.month == month.month && range.end.year == month.year) lastIndex else firstColumn + days - 1
                for (row in (from / 7)..(to / 7)) {
                    val startColumn = if (row == from / 7) from % 7 else 0
                    val endColumn = if (row == to / 7) to % 7 else 6
                    GlassSurface(
                        finish = GlassFinish.Well,
                        modifier = Modifier
                            .offset(16.dp + CellSize * startColumn + 2.dp, gridTop + CellSize * row + 2.dp)
                            .size(CellSize * (endColumn - startColumn) + 40.dp, 40.dp),
                        shape = SeaGlassShape.Pane(20.dp),
                        role = role,
                        tinted = true,
                        reactsTo = emptySet(),
                    )
                }
            }
        }
        for (day in 1..days) {
            val date = month.atDay(day)
            val index = firstColumn + day - 1
            val x = 16.dp + CellSize * (index % 7)
            val y = gridTop + CellSize * (index / 7)
            val selected = isSelected(date)
            val isToday = date == today
            val inBand = inRange(date)
            Box(
                Modifier
                    .offset(x, y)
                    .size(CellSize)
                    .clickable(role = Role.Button) { onDateClick(date) },
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    GlassSurface(
                        finish = GlassFinish.Glass,
                        modifier = Modifier.size(40.dp),
                        shape = SeaGlassShape.Lens,
                        role = role,
                        reactsTo = emptySet(),
                    ) {
                        Text(day.toString(), color = LocalContentColor.current, fontSize = 15.sp, fontWeight = FontWeight(650))
                    }
                } else {
                    Text(
                        day.toString(),
                        color = if (isToday) accent else ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight(if (isToday) 750 else if (inBand) 600 else 450),
                    )
                    if (isToday) Box(Modifier.offset(0.dp, 16.dp).size(5.dp).background(accent, CircleShape))
                }
            }
        }
        Row(Modifier.offset(PickerWidth - 196.dp, buttonsTop), Arrangement.spacedBy(8.dp)) {
            GlassButton("Cancel", onCancel, style = GlassButtonStyle.Plain, size = GlassButtonSize.M, role = role)
            GlassButton("Done", onDone, size = GlassButtonSize.M, role = role)
        }
    }
}
