package com.floydwiz.seaglasssample.perf

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.metrics.performance.FrameData
import androidx.metrics.performance.FrameDataApi31
import androidx.metrics.performance.JankStats

class FrameSummary(
    val frames: Int,
    val totalP50Ms: Float,
    val totalP95Ms: Float,
    val totalP99Ms: Float,
    val totalMaxMs: Float,
    val uiP95Ms: Float,
    val missedPercent: Float,
    val jankPercent: Float,
) {
    override fun toString() =
        "frames=$frames total(p50=%.1f p95=%.1f p99=%.1f max=%.1f) uiP95=%.1f missed=%.1f%% jank=%.1f%%"
            .format(totalP50Ms, totalP95Ms, totalP99Ms, totalMaxMs, uiP95Ms, missedPercent, jankPercent)
}

/**
 * Collects per-frame metrics from JankStats (API 31+ [FrameDataApi31]).
 * - total: vsync to frame completion, which includes RenderThread and GPU work; this is the gate metric.
 * - ui: UI-thread work only (input, composition, layout, draw recording).
 * - missed: frames whose overrun past the deadline is positive.
 */
class FrameStats {
    private val lock = Any()
    private val totalNanos = ArrayList<Long>()
    private val uiNanos = ArrayList<Long>()
    private val missed = ArrayList<Boolean>()
    private val jank = ArrayList<Boolean>()

    fun reset() = synchronized(lock) {
        totalNanos.clear()
        uiNanos.clear()
        missed.clear()
        jank.clear()
    }

    internal fun onFrame(frame: FrameData) = synchronized(lock) {
        val api31 = frame as FrameDataApi31
        totalNanos.add(api31.frameDurationTotalNanos)
        uiNanos.add(frame.frameDurationUiNanos)
        missed.add(api31.frameOverrunNanos > 0)
        jank.add(frame.isJank)
    }

    /** Summary of the most recent [lastFrames] frames, or all frames since [reset]. */
    fun summary(lastFrames: Int = Int.MAX_VALUE): FrameSummary = synchronized(lock) {
        val total = totalNanos.takeLast(lastFrames)
        val ui = uiNanos.takeLast(lastFrames)
        FrameSummary(
            frames = total.size,
            totalP50Ms = percentile(total, 0.50),
            totalP95Ms = percentile(total, 0.95),
            totalP99Ms = percentile(total, 0.99),
            totalMaxMs = total.maxOrNull()?.let { it / 1e6f } ?: 0f,
            uiP95Ms = percentile(ui, 0.95),
            missedPercent = share(missed.takeLast(lastFrames)),
            jankPercent = share(jank.takeLast(lastFrames)),
        )
    }

    private fun share(flags: List<Boolean>) = if (flags.isEmpty()) 0f else 100f * flags.count { it } / flags.size

    private fun percentile(values: List<Long>, p: Double): Float {
        if (values.isEmpty()) return 0f
        val sorted = values.sorted()
        return sorted[((sorted.size - 1) * p).toInt()] / 1e6f
    }
}

@Composable
fun rememberFrameStats(): FrameStats {
    val stats = remember { FrameStats() }
    val activity = LocalContext.current as? Activity
    DisposableEffect(activity) {
        val window = activity?.window
        if (window == null) {
            onDispose { }
        } else {
            val jankStats = JankStats.createAndTrack(window) { frame -> stats.onFrame(frame) }
            onDispose { jankStats.isTrackingEnabled = false }
        }
    }
    return stats
}
