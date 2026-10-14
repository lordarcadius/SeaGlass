package com.floydwiz.seaglasssample.benchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val Target = "com.floydwiz.seaglasssample"

/** Scrolls the photo library in the dark theme, with glass on and with transparency reduced as the cost baseline. */
@RunWith(AndroidJUnit4::class)
class LibraryScrollBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun scrollWithGlass() = scroll(reduceTransparency = false)

    @Test
    fun scrollWithReducedTransparency() = scroll(reduceTransparency = true)

    private fun scroll(reduceTransparency: Boolean) = rule.measureRepeated(
        packageName = Target,
        metrics = listOf(FrameTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None(),
        setupBlock = {
            pressHome()
            startActivityAndWait(
                Intent().setClassName(Target, "$Target.MainActivity")
                    .putExtra("route", "screens/library")
                    .putExtra("theme", "Dark")
                    .putExtra("contrast", "Standard")
                    .putExtra("rt", reduceTransparency),
            )
        },
    ) {
        val list = device.wait(androidx.test.uiautomator.Until.findObject(By.scrollable(true)), 5_000)
        list.setGestureMargin(device.displayWidth / 5)
        repeat(2) {
            list.fling(Direction.DOWN)
            device.waitForIdle()
            list.fling(Direction.UP)
            device.waitForIdle()
        }
    }
}
