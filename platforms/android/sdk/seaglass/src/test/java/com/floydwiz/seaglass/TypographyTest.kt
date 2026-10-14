package com.floydwiz.seaglass

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import com.floydwiz.seaglass.theme.SeaGlassTypography
import com.floydwiz.seaglass.tokens.SeaGlassTextStyles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TypographyTest {
    private val tokens = SeaGlassTextStyles(FontFamily.Default)

    @Test
    fun scaleFollowsTheTokens() {
        val pairs = listOf(
            SeaGlassTypography.displayLarge to tokens.displayLarge,
            SeaGlassTypography.titleMedium to tokens.titleMedium,
            SeaGlassTypography.bodyMedium to tokens.bodyMedium,
            SeaGlassTypography.labelSmall to tokens.labelSmall,
        )
        pairs.forEach { (style, token) ->
            assertEquals(token.fontSize, style.fontSize)
            assertEquals(token.lineHeight, style.lineHeight)
            assertEquals(token.letterSpacing, style.letterSpacing)
            assertEquals(token.fontWeight, style.fontWeight)
        }
    }

    @Test
    fun keepsMaterialLineMetrics() {
        val material = Typography()
        assertNotNull(SeaGlassTypography.bodyLarge.lineHeightStyle)
        assertEquals(material.bodyLarge.lineHeightStyle, SeaGlassTypography.bodyLarge.lineHeightStyle)
        assertEquals(material.bodyLarge.platformStyle, SeaGlassTypography.bodyLarge.platformStyle)
    }
}
