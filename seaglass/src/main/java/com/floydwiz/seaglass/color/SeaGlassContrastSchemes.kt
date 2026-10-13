package com.floydwiz.seaglass.color

import androidx.compose.material3.ColorScheme
import com.floydwiz.seaglass.theme.SeaGlassContrast

/**
 * Adjusts the scheme for a contrast level.
 * - Medium strengthens outlines.
 * - High moves the accent to tone 30 (light) or 90 (dark), promotes secondary text to the primary text colour and uses
 *   strong outlines.
 */
internal fun ColorScheme.withContrast(contrast: SeaGlassContrast, dark: Boolean, accent: TonalTones): ColorScheme {
    val variant = SeaGlassPalettes.NeutralVariant
    return when (contrast) {
        SeaGlassContrast.Standard -> this
        SeaGlassContrast.Medium -> copy(outline = variant[if (dark) 70 else 40])
        SeaGlassContrast.High -> copy(
            primary = accent[if (dark) 90 else 30],
            onSurfaceVariant = onSurface,
            outline = variant[if (dark) 80 else 30],
            outlineVariant = variant[if (dark) 60 else 50],
        )
    }
}
