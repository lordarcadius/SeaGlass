package com.floydwiz.seaglass.material

import com.floydwiz.seaglass.color.SeaGlassPalettes
import com.floydwiz.seaglass.theme.SeaGlassContrast

/** Values of a finish's recipe at standard contrast, for controls that start where the recipe is. */
object GlassRecipeInfo {
    private fun context(dark: Boolean) = GlassContext(dark, SeaGlassContrast.Standard, false, SeaGlassPalettes.Aqua, 0f, false)

    /** The backdrop blur radius in dp, or null for a finish that does not blur its backdrop. */
    fun blurDp(finish: GlassFinish, dark: Boolean): Float? = (GlassRecipes.body(finish, context(dark)) as? Body.Backdrop)?.blur

    /** The grain opacity. */
    fun grain(finish: GlassFinish, dark: Boolean): Float = GlassRecipes.layers(finish, context(dark), 120f, 56f).grain
}
