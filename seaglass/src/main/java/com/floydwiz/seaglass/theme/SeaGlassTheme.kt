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
import com.floydwiz.seaglass.color.SeaGlassDarkScheme
import com.floydwiz.seaglass.color.SeaGlassLightScheme
import com.floydwiz.seaglass.color.SeaGlassPalettes
import com.floydwiz.seaglass.color.SeaGlassRolePalettes
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.color.dynamicRolePalettes

enum class SeaGlassContrast { Standard, Medium, High }

val LocalSeaGlassDark = staticCompositionLocalOf { false }
val LocalGlassContrast = staticCompositionLocalOf { SeaGlassContrast.Standard }
val LocalReduceTransparency = staticCompositionLocalOf { false }
val LocalRolePalettes = staticCompositionLocalOf { SeaGlassRolePalettes.Static }
val LocalGlassRole = compositionLocalOf<TonalTones> { SeaGlassPalettes.Aqua }

@Composable
fun SeaGlassTheme(
    dark: Boolean = isSystemInDarkTheme(),
    contrast: SeaGlassContrast = SeaGlassContrast.Standard,
    reduceTransparency: Boolean = false,
    dynamicColor: Boolean = false,
    seedRole: GlassRole = GlassRole.Aqua,
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

    CompositionLocalProvider(
        LocalSeaGlassDark provides dark,
        LocalGlassContrast provides contrast,
        LocalReduceTransparency provides reduceTransparency,
        LocalRolePalettes provides palettes,
        LocalGlassRole provides palettes[seedRole],
    ) {
        MaterialTheme(colorScheme = colorScheme, typography = SeaGlassTypography, content = content)
    }
}
