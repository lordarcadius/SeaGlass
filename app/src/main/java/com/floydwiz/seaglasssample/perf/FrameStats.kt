package com.floydwiz.seaglasssample.perf

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.FrameMetrics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.metrics.performance.JankStats

class FrameSummary(
    val frames: Int,
    val p50Ms: Float,
    val p95Ms: Float,
    val p99Ms: Float,
    val maxMs: Float,
    val jankPercent: Float,
    val gpuP95Ms: Float,
) {
    override fun toString() =
        "frames=$frames p50=%.1f p95=%.1f p99=%.1f max=%.1f jank=%.1f%% gpuP95=%.1f".format(p50Ms, p95Ms, p99Ms, maxMs, jankPercent, gpuP95Ms)
}

/** Collects UI frame durations and jank flags from JankStats, and GPU durations from FrameMetrics. */
class FrameStats {
    private val lock = Any()
    private val uiNanos = ArrayList<Long>()
    private val jankFlags = ArrayList<Boolean>()
    private val gpuNanos = ArrayList<Long>()

    fun reset() = synchronized(lock) {
        uiNanos.clear()
        jankFlags.clear()
        gpuNanos.clear()
    }

    internal fun onUiFrame(durationNanos: Long, jank: Boolean) = synchronized(lock) {
        uiNanos.add(durationNanos)
        jankFlags.add(jank)
    }

    internal fun onGpuFrame(durationNanos: Long) = synchronized(lock) { gpuNanos.add(durationNanos) }

    /** Summary of the most recent [lastFrames] frames, or all frames since [reset]. */
    fun summary(lastFrames: Int = Int.MAX_VALUE): FrameSummary = synchronized(lock) {
        val ui = uiNanos.takeLast(lastFrames)
        val jank = jankFlags.takeLast(lastFrames)
        val gpu = gpuNanos.takeLast(lastFrames)
        FrameSummary(
            frames = ui.size,
            p50Ms = percentile(ui, 0.50),
            p95Ms = percentile(ui, 0.95),
            p99Ms = percentile(ui, 0.99),
            maxMs = ui.maxOrNull()?.let { it / 1e6f } ?: 0f,
            jankPercent = if (jank.isEmpty()) 0f else 100f * jank.count { it } / jank.size,
            gpuP95Ms = percentile(gpu, 0.95),
        )
    }

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
            val jankStats = JankStats.createAndTrack(window) { frame -> stats.onUiFrame(frame.frameDurationUiNanos, frame.isJank) }
            val gpuListener = frameMetricsListener { metrics -> stats.onGpuFrame(metrics.getMetric(FrameMetrics.GPU_DURATION)) }
            window.addOnFrameMetricsAvailableListener(gpuListener, Handler(Looper.getMainLooper()))
            onDispose {
                jankStats.isTrackingEnabled = false
                window.removeOnFrameMetricsAvailableListener(gpuListener)
            }
        }
    }
    return stats
}

private fun frameMetricsListener(block: (FrameMetrics) -> Unit) =
    android.view.Window.OnFrameMetricsAvailableListener { _, metrics, _ -> block(metrics) }
