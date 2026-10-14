package com.floydwiz.seaglass.material

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.ProvidableCompositionLocal

/** System animator duration scale: 1 is normal, above 1 slows motion and 0 turns animation off. */
public val LocalMotionScale: ProvidableCompositionLocal<Float> = staticCompositionLocalOf { 1f }

/** False when animations are off (scale 0): loops and morphs stop and transitions become instant. */
public val LocalMotionEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { true }

private fun Context.readAnimatorScale(): Float =
    Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)

/** Tracks `ANIMATOR_DURATION_SCALE` live, so toggling developer options takes effect without a restart. */
@Composable
internal fun rememberAnimatorScale(): Float {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(context.readAnimatorScale()) }
    DisposableEffect(context) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                scale = context.readAnimatorScale()
            }
        }
        context.contentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        onDispose { context.contentResolver.unregisterContentObserver(observer) }
    }
    return scale
}
