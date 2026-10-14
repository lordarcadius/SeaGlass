package com.floydwiz.seaglass.components.selection

import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange

/**
 * Handles one gesture that starts at [down], claiming only drags along one axis so a parent scroller keeps the other.
 * - [onPress] fires immediately for press feedback.
 * - A release before the touch slop is a tap ([onTap]); a drag along the axis calls [onMove] and then [onEnd].
 * - If a parent takes the gesture over (for example a vertical scroll) [onCancel] fires and nothing is committed.
 */
internal suspend fun AwaitPointerEventScope.trackAxisDrag(
    down: PointerInputChange,
    horizontal: Boolean,
    onPress: (Offset) -> Unit,
    onMove: (Offset) -> Unit,
    onTap: (Offset) -> Unit,
    onCancel: () -> Unit,
    onEnd: () -> Unit,
) {
    onPress(down.position)
    val slop = if (horizontal) {
        awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ -> change.consume() }
    } else {
        awaitVerticalTouchSlopOrCancellation(down.id) { change, _ -> change.consume() }
    }
    if (slop != null) {
        onMove(slop.position)
        val finished = if (horizontal) {
            horizontalDrag(slop.id) { change -> onMove(change.position); change.consume() }
        } else {
            verticalDrag(slop.id) { change -> onMove(change.position); change.consume() }
        }
        if (finished) onEnd() else onCancel()
    } else {
        val released = currentEvent.changes.firstOrNull { it.id == down.id }?.let { !it.pressed } ?: true
        if (released) onTap(down.position) else onCancel()
    }
}
