package com.floydwiz.seaglass.theme

import android.app.UiModeManager
import android.os.Build
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.function.Consumer

private fun Float.toContrastLevel() = when {
    this < 0.25f -> SeaGlassContrast.Standard
    this < 0.75f -> SeaGlassContrast.Medium
    else -> SeaGlassContrast.High
}

/** The contrast level chosen in system settings, tracked live; Standard below Android 14. */
@Composable
fun rememberSystemContrast(): SeaGlassContrast {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return SeaGlassContrast.Standard
    val context = LocalContext.current
    val manager = remember(context) { context.getSystemService(UiModeManager::class.java) }
    var level by remember(manager) { mutableStateOf(manager.contrast.toContrastLevel()) }
    DisposableEffect(manager) {
        val listener = UiModeManager.ContrastChangeListener { level = it.toContrastLevel() }
        manager.addContrastChangeListener(context.mainExecutor, listener)
        onDispose { manager.removeContrastChangeListener(listener) }
    }
    return level
}

/** Whether the system allows cross-window blur, tracked live; it is off in power saving, for example. */
@Composable
fun rememberCrossWindowBlurEnabled(): Boolean {
    val context = LocalContext.current
    val manager = remember(context) { context.getSystemService(WindowManager::class.java) }
    var enabled by remember(manager) { mutableStateOf(manager.isCrossWindowBlurEnabled) }
    DisposableEffect(manager) {
        val listener = Consumer<Boolean> { enabled = it }
        manager.addCrossWindowBlurEnabledListener(context.mainExecutor, listener)
        onDispose { manager.removeCrossWindowBlurEnabledListener(listener) }
    }
    return enabled
}
