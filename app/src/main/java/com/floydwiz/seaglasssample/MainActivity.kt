package com.floydwiz.seaglasssample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.theme.SeaGlassContrast
import com.floydwiz.seaglasssample.control.ControlState
import com.floydwiz.seaglasssample.control.ThemeMode
import com.floydwiz.seaglasssample.showcase.ShowcaseApp

/**
 * Optional launch extras for repeatable captures and benchmarks, for example
 * `am start -n .../.MainActivity --es route screens/library --es theme Dark --es contrast High --ez rt true`:
 * - `route`: a destination route; `theme`: System, Light or Dark; `contrast`: System, Standard, Medium or High;
 * - `rt`: reduce transparency; `rtAuto`: reduce transparency when system blur is off; `dynamic`: dynamic colour;
 * - `seed`: a glass role name.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ShowcaseApp(intent.toControlState(), intent.getStringExtra("route")) }
    }
}

private fun Intent.toControlState() = ControlState(
    themeMode = getStringExtra("theme")?.let { ThemeMode.valueOf(it) } ?: ThemeMode.System,
    contrast = getStringExtra("contrast")?.takeUnless { it == "System" }?.let { SeaGlassContrast.valueOf(it) },
    reduceTransparency = getBooleanExtra("rt", false),
    dynamicColor = getBooleanExtra("dynamic", false),
    seedRole = getStringExtra("seed")?.let { GlassRole.valueOf(it) } ?: GlassRole.Aqua,
    autoReduceTransparency = getBooleanExtra("rtAuto", true),
)
