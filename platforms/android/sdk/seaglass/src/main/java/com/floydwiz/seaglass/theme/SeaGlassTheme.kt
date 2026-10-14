package com.floydwiz.seaglass.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.tokens.SeaGlassDarkScheme
import com.floydwiz.seaglass.tokens.SeaGlassLightScheme
import com.floydwiz.seaglass.tokens.SeaGlassPalettes
import com.floydwiz.seaglass.color.SeaGlassRolePalettes
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.color.dynamicRolePalettes
import com.floydwiz.seaglass.color.withContrast
import com.floydwiz.seaglass.material.LocalGlassClock
import com.floydwiz.seaglass.material.LocalGlassLayers
import com.floydwiz.seaglass.material.LocalClockOwned
import com.floydwiz.seaglass.material.LocalMotionEnabled
import com.floydwiz.seaglass.material.LocalMotionScale
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.rememberAnimatorScale
import com.floydwiz.seaglass.material.rememberGlassClock
import androidx.compose.runtime.ProvidableCompositionLocal

public enum class SeaGlassContrast { Standard, Medium, High }

public val LocalSeaGlassDark: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }
public val LocalGlassContrast: ProvidableCompositionLocal<SeaGlassContrast> = staticCompositionLocalOf { SeaGlassContrast.Standard }
public val LocalReduceTransparency: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }
public val LocalRolePalettes: ProvidableCompositionLocal<SeaGlassRolePalettes> = staticCompositionLocalOf { SeaGlassRolePalettes.Static }
public val LocalGlassRole: ProvidableCompositionLocal<TonalTones> = compositionLocalOf<TonalTones> { SeaGlassPalettes.Aqua }

@Composable
public fun SeaGlassTheme(
    dark: Boolean = isSystemInDarkTheme(),
    contrast: SeaGlassContrast = SeaGlassContrast.Standard,
    reduceTransparency: Boolean = false,
    dynamicColor: Boolean = false,
    seedRole: GlassRole = GlassRole.Aqua,
    lightDrift: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && dark -> dynamicDarkColorScheme(context)
        dynamicColor -> dynamicLightColorScheme(context)
        dark -> SeaGlassDarkScheme
        else -> SeaGlassLightScheme
    }
    val palettes = remember(dynamicColor, context) {
        if (dynamicColor) dynamicRolePalettes(context) else SeaGlassRolePalettes.Static
    }
    val contrastScheme = remember(colorScheme, contrast, dark, palettes, seedRole) { colorScheme.withContrast(contrast, dark, palettes[seedRole]) }

    val layers = LocalGlassLayers.current
    val clockOwned = LocalClockOwned.current
    val motionScale = if (clockOwned) LocalMotionScale.current else rememberAnimatorScale()
    val motionEnabled = motionScale > 0f
    val clock = if (clockOwned) LocalGlassClock.current else rememberGlassClock(running = motionEnabled && layers.light && lightDrift, motionScale = motionScale * SeaGlassTokens.Motion.slowdown)

    CompositionLocalProvider(
        LocalGlassClock provides clock,
        LocalClockOwned provides true,
        LocalMotionScale provides motionScale,
        LocalMotionEnabled provides motionEnabled,
        LocalSeaGlassDark provides dark,
        LocalGlassContrast provides contrast,
        LocalReduceTransparency provides reduceTransparency,
        LocalRolePalettes provides palettes,
        LocalGlassRole provides palettes[seedRole],
    ) {
        MaterialTheme(colorScheme = contrastScheme, typography = SeaGlassTypography, content = content)
    }
}
