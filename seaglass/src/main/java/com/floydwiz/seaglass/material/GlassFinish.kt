package com.floydwiz.seaglass.material

import androidx.compose.ui.graphics.Color
import com.floydwiz.seaglass.color.TonalTones

enum class GlassFinish { Glass, Frost, Clear, Lens, Pearl, Fill, Well, Milk, Ink, Dry, Bead, Plain }

/** Finishes that stand off the surface: they lift on hover and bloom light on press. */
internal val GlassFinish.isRaised
    get() = when (this) {
        GlassFinish.Glass, GlassFinish.Frost, GlassFinish.Clear, GlassFinish.Lens,
        GlassFinish.Pearl, GlassFinish.Fill, GlassFinish.Bead -> true
        else -> false
    }

/** Foreground colour for content drawn on [finish] (`FG()` in recipes.js). */
fun glassContentColor(finish: GlassFinish, role: TonalTones, dark: Boolean): Color = when (finish) {
    GlassFinish.Glass, GlassFinish.Frost -> if (dark) role[95] else role[10]
    GlassFinish.Fill, GlassFinish.Bead -> if (dark) role[10] else Color.White
    GlassFinish.Pearl -> Color(0xFF171C1F)
    GlassFinish.Ink -> if (dark) Color(0xFF171C1F) else Color(0xFFEEF3F5)
    GlassFinish.Dry -> disabledContentColor(dark)
    GlassFinish.Clear, GlassFinish.Lens, GlassFinish.Well, GlassFinish.Milk, GlassFinish.Plain ->
        if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
}

fun disabledContentColor(dark: Boolean): Color =
    (if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight)
        .copy(alpha = SeaGlassTokens.State.Disabled.ContentAlpha)
