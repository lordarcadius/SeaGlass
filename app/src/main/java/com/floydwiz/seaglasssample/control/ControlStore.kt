package com.floydwiz.seaglasssample.control

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.material.GlassLayers
import com.floydwiz.seaglass.theme.SeaGlassContrast
import kotlinx.coroutines.flow.first

private val Context.controlStore by preferencesDataStore("showcase_controls")

private val Theme = stringPreferencesKey("theme")
private val Contrast = stringPreferencesKey("contrast")
private val ReduceTransparency = booleanPreferencesKey("reduceTransparency")
private val AutoReduceTransparency = booleanPreferencesKey("autoReduceTransparency")
private val DynamicColor = booleanPreferencesKey("dynamicColor")
private val Seed = stringPreferencesKey("seed")
private val FpsOverlay = booleanPreferencesKey("fpsOverlay")
private val LayerKeys = listOf("shadow", "caustic", "contact", "body", "light", "grain", "volume", "rim").associateWith { booleanPreferencesKey("layer.$it") }

private fun GlassLayers.flags() = mapOf(
    "shadow" to shadow, "caustic" to caustic, "contact" to contact, "body" to body,
    "light" to light, "grain" to grain, "volume" to volume, "rim" to rim,
)

/** The controls saved the last time the app ran, or the defaults. */
suspend fun readControls(context: Context): ControlState {
    val prefs = context.controlStore.data.first()
    fun layer(name: String) = prefs[LayerKeys.getValue(name)] ?: true
    return ControlState(
        themeMode = prefs[Theme]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.System,
        contrast = prefs[Contrast]?.let { runCatching { SeaGlassContrast.valueOf(it) }.getOrNull() },
        reduceTransparency = prefs[ReduceTransparency] ?: false,
        dynamicColor = prefs[DynamicColor] ?: false,
        seedRole = prefs[Seed]?.let { runCatching { GlassRole.valueOf(it) }.getOrNull() } ?: GlassRole.Aqua,
        layers = GlassLayers(
            shadow = layer("shadow"), caustic = layer("caustic"), contact = layer("contact"), body = layer("body"),
            light = layer("light"), grain = layer("grain"), volume = layer("volume"), rim = layer("rim"),
        ),
        autoReduceTransparency = prefs[AutoReduceTransparency] ?: true,
        fpsOverlay = prefs[FpsOverlay] ?: false,
    )
}

suspend fun writeControls(context: Context, state: ControlState) {
    context.controlStore.edit { prefs: androidx.datastore.preferences.core.MutablePreferences ->
        prefs[Theme] = state.themeMode.name
        val contrast = state.contrast
        if (contrast == null) prefs.remove(Contrast) else prefs[Contrast] = contrast.name
        prefs[ReduceTransparency] = state.reduceTransparency
        prefs[AutoReduceTransparency] = state.autoReduceTransparency
        prefs[DynamicColor] = state.dynamicColor
        prefs[Seed] = state.seedRole.name
        prefs[FpsOverlay] = state.fpsOverlay
        state.layers.flags().forEach { (name, on) -> prefs[LayerKeys.getValue(name)] = on }
    }
}
