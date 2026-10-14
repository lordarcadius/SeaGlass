package com.floydwiz.seaglass

import androidx.compose.ui.graphics.Color
import com.floydwiz.seaglass.material.Body
import com.floydwiz.seaglass.material.GlassContext
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassRecipes
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.extendedColors
import com.floydwiz.seaglass.material.glassContentColor
import com.floydwiz.seaglass.theme.SeaGlassContrast
import com.floydwiz.seaglass.tokens.SeaGlassBlurTokens
import com.floydwiz.seaglass.tokens.SeaGlassDarkScheme
import com.floydwiz.seaglass.tokens.SeaGlassLightScheme
import com.floydwiz.seaglass.tokens.SeaGlassOpacityTokens
import com.floydwiz.seaglass.tokens.SeaGlassPalettes
import com.floydwiz.seaglass.tokens.SeaGlassStateTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenAdoptionTest {
    private fun context(dark: Boolean, reduceTransparency: Boolean = false) =
        GlassContext(dark, SeaGlassContrast.Standard, reduceTransparency, SeaGlassPalettes.Aqua, 0f, false)

    private fun blur(finish: GlassFinish, dark: Boolean) = (GlassRecipes.body(finish, context(dark)) as Body.Backdrop).blur

    @Test
    fun finishesBlurByTheirTokenRadius() {
        val expected = mapOf(
            GlassFinish.Frost to SeaGlassBlurTokens.Frost,
            GlassFinish.Clear to SeaGlassBlurTokens.Clear,
            GlassFinish.Lens to SeaGlassBlurTokens.Clear,
            GlassFinish.Milk to SeaGlassBlurTokens.Milk,
            GlassFinish.Ink to SeaGlassBlurTokens.Ink,
            GlassFinish.Fill to SeaGlassBlurTokens.Fill,
            GlassFinish.Well to SeaGlassBlurTokens.Well,
            GlassFinish.Pearl to SeaGlassBlurTokens.Pearl,
            GlassFinish.Dry to SeaGlassBlurTokens.Dry,
        )
        expected.forEach { (finish, token) ->
            assertEquals("$finish light", token.value, blur(finish, dark = false), 0f)
            assertEquals("$finish dark", token.value, blur(finish, dark = true), 0f)
        }
        assertEquals(SeaGlassBlurTokens.Glass.value, blur(GlassFinish.Glass, dark = false), 0f)
        assertEquals(SeaGlassBlurTokens.GlassDark.value, blur(GlassFinish.Glass, dark = true), 0f)
    }

    @Test
    fun lightGrainFollowsTheOpacityTokens() {
        val expected = mapOf(
            GlassFinish.Glass to SeaGlassOpacityTokens.GrainGlass,
            GlassFinish.Frost to SeaGlassOpacityTokens.GrainFrost,
            GlassFinish.Clear to SeaGlassOpacityTokens.GrainClear,
            GlassFinish.Milk to SeaGlassOpacityTokens.GrainMilk,
            GlassFinish.Dry to SeaGlassOpacityTokens.GrainDry,
        )
        expected.forEach { (finish, grain) ->
            assertEquals("$finish", grain, GlassRecipes.layers(finish, context(dark = false), 120f, 56f).grain, 0f)
        }
    }

    @Test
    fun stateAndContentValuesComeFromTheTokens() {
        assertEquals(SeaGlassStateTokens.PressScale, SeaGlassTokens.State.Press.Scale, 0f)
        assertEquals(SeaGlassStateTokens.HoverLift, SeaGlassTokens.State.Hover.Lift)
        assertEquals(SeaGlassOpacityTokens.ContentDisabled, SeaGlassTokens.State.Disabled.ContentAlpha, 0f)
        assertEquals(SeaGlassLightScheme.onSurface, SeaGlassTokens.Content.OnSurfaceLight)
        assertEquals(SeaGlassDarkScheme.outline, SeaGlassTokens.Content.OutlineDark)
    }

    @Test
    fun pearlAndInkContentUseTheExtendedColours() {
        listOf(false, true).forEach { dark ->
            assertEquals(extendedColors(dark).onPearl, glassContentColor(GlassFinish.Pearl, SeaGlassPalettes.Aqua, dark))
            assertEquals(extendedColors(dark).onInk, glassContentColor(GlassFinish.Ink, SeaGlassPalettes.Aqua, dark))
        }
    }

    @Test
    fun reducedTransparencyDrawsSolidFills() {
        val milk = GlassRecipes.body(GlassFinish.Milk, context(dark = false, reduceTransparency = true))
        assertTrue(milk is Body.Solid)
        assertEquals(SeaGlassTokens.ReduceTransparency.Light.milk, (milk as Body.Solid).color)
        assertEquals(Color.White, SeaGlassTokens.ReduceTransparency.Light.milk)
    }
}
