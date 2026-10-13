package com.floydwiz.seaglass.material

import androidx.compose.ui.graphics.Color
import com.floydwiz.seaglass.color.TonalTones

enum class GlassFinish { Glass, Frost, Clear, Well, Milk }

/** Foreground colour for content drawn on [finish] (`FG()` in recipes.js). */
fun glassContentColor(finish: GlassFinish, role: TonalTones, dark: Boolean): Color = when (finish) {
    GlassFinish.Glass, GlassFinish.Frost -> if (dark) role[95] else role[10]
    GlassFinish.Clear, GlassFinish.Well, GlassFinish.Milk ->
        if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
}
