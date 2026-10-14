package com.floydwiz.seaglasssample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.runBlocking
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.material.GlassLayers
import com.floydwiz.seaglass.theme.SeaGlassContrast
import com.floydwiz.seaglasssample.control.ControlState
import com.floydwiz.seaglasssample.control.ThemeMode
import com.floydwiz.seaglasssample.control.readControls
import com.floydwiz.seaglasssample.showcase.ShowcaseApp

/**
 * Optional launch extras for repeatable captures and benchmarks, for example
 * `am start -n .../.MainActivity --es route screens/library --es theme Dark --es contrast High --ez rt true`:
 * - `route`: a destination route; `theme`: System, Light or Dark; `contrast`: System, Standard, Medium or High;
 * - `rt`: reduce transparency; `rtAuto`: reduce transparency when system blur is off; `dynamic`: dynamic colour;
 * - `seed`: a glass role name; `off`: comma-separated glass layers to switch off (shadow, caustic, contact, body, light, grain, volume, rim); `drift`: animate the light drift.
 * Without any control extra the controls are read from, and saved to, DataStore; with them they are neither.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestHighestRefreshRate()
        val hasExtras = ControlExtras.any { intent.hasExtra(it) }
        val initial = if (hasExtras) intent.toControlState() else runBlocking { readControls(this@MainActivity) }
        setContent { ShowcaseApp(initial, intent.getStringExtra("route"), persist = !hasExtras) }
    }
}

/** Asks for the fastest display mode at the current resolution; the system may still lower it, for example in battery saver. */
private fun ComponentActivity.requestHighestRefreshRate() {
    val display = display ?: return
    val current = display.mode
    val fastest = display.supportedModes
        .filter { it.physicalWidth == current.physicalWidth && it.physicalHeight == current.physicalHeight }
        .maxByOrNull { it.refreshRate } ?: return
    window.attributes = window.attributes.apply { preferredDisplayModeId = fastest.modeId }
}

private val ControlExtras = listOf("theme", "contrast", "rt", "rtAuto", "dynamic", "seed", "off", "drift")

private fun Intent.toControlState() = ControlState(
    themeMode = getStringExtra("theme")?.let { ThemeMode.valueOf(it) } ?: ThemeMode.System,
    contrast = getStringExtra("contrast")?.takeUnless { it == "System" }?.let { SeaGlassContrast.valueOf(it) },
    reduceTransparency = getBooleanExtra("rt", false),
    dynamicColor = getBooleanExtra("dynamic", false),
    seedRole = getStringExtra("seed")?.let { GlassRole.valueOf(it) } ?: GlassRole.Aqua,
    layers = getStringExtra("off")?.split(",")?.map { it.trim() }?.toSet().orEmpty().let { off ->
        GlassLayers(
            shadow = "shadow" !in off, caustic = "caustic" !in off, contact = "contact" !in off, body = "body" !in off,
            light = "light" !in off, grain = "grain" !in off, volume = "volume" !in off, rim = "rim" !in off,
        )
    },
    autoReduceTransparency = getBooleanExtra("rtAuto", true),
    lightDrift = getBooleanExtra("drift", false),
)
