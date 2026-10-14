package com.floydwiz.seaglass.material

import com.floydwiz.seaglass.tokens.SeaGlassPalettes
import com.floydwiz.seaglass.theme.SeaGlassContrast
import com.floydwiz.seaglass.SeaGlassLabApi

/** Values of a finish's recipe at standard contrast, for controls that start where the recipe is. */
@SeaGlassLabApi
public object GlassRecipeInfo {
    private fun context(dark: Boolean) = GlassContext(dark, SeaGlassContrast.Standard, false, SeaGlassPalettes.Aqua, 0f, false)

    /** The backdrop blur radius in dp, or null for a finish that does not blur its backdrop. */
    public fun blurDp(finish: GlassFinish, dark: Boolean): Float? = (GlassRecipes.body(finish, context(dark)) as? Body.Backdrop)?.blur

    /** The grain opacity. */
    public fun grain(finish: GlassFinish, dark: Boolean): Float = GlassRecipes.layers(finish, context(dark), 120f, 56f).grain
}
