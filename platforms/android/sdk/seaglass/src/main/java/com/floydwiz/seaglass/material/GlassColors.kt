package com.floydwiz.seaglass.material

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.theme.LocalGlassContrast
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.theme.SeaGlassContrast

internal fun rgba(r: Int, g: Int, b: Int, a: Float) = Color(r / 255f, g / 255f, b / 255f, a.coerceIn(0f, 1f))

internal fun white(a: Float) = Color(1f, 1f, 1f, a.coerceIn(0f, 1f))

internal fun Color.alpha(a: Float) = copy(alpha = a.coerceIn(0f, 1f))

internal val Color.transparent get() = copy(alpha = 0f)

/** Linear mix in sRGB, matching `mix()` in recipes.js. */
internal fun mix(a: Color, b: Color, t: Float) = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f,
)

/** Accent for labels and symbols on neutral glass (`acc()` in the boards): tone 40 / 80, or 30 / 90 in high contrast. */
@Composable
fun glassAccent(role: TonalTones = LocalGlassRole.current): Color {
    val high = LocalGlassContrast.current == SeaGlassContrast.High
    return if (LocalSeaGlassDark.current) (if (high) role[90] else role[80]) else (if (high) role[30] else role[40])
}

/** Content colour on an accent-tinted pane (`onc()` in the boards). */
@Composable
fun glassOnRole(role: TonalTones = LocalGlassRole.current): Color =
    if (LocalSeaGlassDark.current) role[95] else role[10]

/** Secondary content colour; high contrast promotes it to the primary content colour. */
@Composable
fun glassOnSurfaceVariant(): Color {
    val high = LocalGlassContrast.current == SeaGlassContrast.High
    val dark = LocalSeaGlassDark.current
    return when {
        high && dark -> SeaGlassTokens.Content.OnSurfaceDark
        high -> SeaGlassTokens.Content.OnSurfaceLight
        dark -> SeaGlassTokens.Content.OnSurfaceVariantDark
        else -> SeaGlassTokens.Content.OnSurfaceVariantLight
    }
}
