package com.floydwiz.seaglasssample.benchmark

import android.content.Intent
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val Target = "com.floydwiz.seaglasssample"

private val Routes = listOf(
    "home", "catalogue/c1", "catalogue/c5", "screens/library", "screens/inbox", "screens/incall",
    "demos/todo", "demos/music", "demos/gallery", "motion", "materials",
)

/** Records the classes and methods used to start the showcase and scroll through its main screens. */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(packageName = Target, includeInStartupProfile = true) {
        for (route in Routes) {
            pressHome()
            startActivityAndWait(Intent().setClassName(Target, "$Target.MainActivity").putExtra("route", route).putExtra("theme", "Light").putExtra("contrast", "Standard"))
            device.waitForIdle()
            device.wait(Until.findObject(By.scrollable(true)), 1_500)?.let {
                it.setGestureMargin(device.displayWidth / 5)
                it.fling(Direction.DOWN)
                device.waitForIdle()
                it.fling(Direction.UP)
                device.waitForIdle()
            }
        }
    }
}
