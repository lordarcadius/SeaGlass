package com.floydwiz.seaglass.material

import androidx.compose.animation.core.Animatable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Selection lens positioned by its two edges so it stretches while it travels.
 * - Dragging snaps the leading edge to the finger and lets the trailing edge follow on spatial.fast.
 * - Settling sends the leading edge on spatial.fast and the trailing edge on spatial.default.
 * Edges are read in draw/layout lambdas only; the readers clamp the bulge past the container.
 */
internal class StretchLens(initialLeft: Float, width: Float) {
    private val left = Animatable(initialLeft)
    private val right = Animatable(initialLeft + width)

    fun leftEdge(min: Float): Float = left.value.coerceAtLeast(min)

    fun rightEdge(max: Float): Float = right.value.coerceAtMost(max)

    fun centre(): Float = (left.value + right.value) / 2f

    suspend fun settle(targetLeft: Float, width: Float) = move(targetLeft, width, snapLeading = false)

    suspend fun drag(targetLeft: Float, width: Float) = move(targetLeft, width, snapLeading = true)

    private suspend fun move(targetLeft: Float, width: Float, snapLeading: Boolean) = coroutineScope {
        val rightward = targetLeft >= left.value
        val leading = if (rightward) right else left
        val trailing = if (rightward) left else right
        val leadingTarget = if (rightward) targetLeft + width else targetLeft
        val trailingTarget = if (rightward) targetLeft else targetLeft + width
        if (snapLeading) {
            leading.snapTo(leadingTarget)
            trailing.animateTo(trailingTarget, SeaGlassTokens.Motion.spatialFast())
        } else {
            launch { leading.animateTo(leadingTarget, SeaGlassTokens.Motion.spatialFast()) }
            trailing.animateTo(trailingTarget, SeaGlassTokens.Motion.spatialDefault())
        }
    }
}
