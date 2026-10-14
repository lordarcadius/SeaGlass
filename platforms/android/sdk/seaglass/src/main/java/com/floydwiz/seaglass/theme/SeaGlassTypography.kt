package com.floydwiz.seaglass.theme

import androidx.compose.material3.Typography
import com.floydwiz.seaglass.tokens.SeaGlassTextStyles

public val SeaGlassTypography: Typography = Typography().let { base ->
    val tokens = SeaGlassTextStyles(RobotoFlex)
    base.copy(
        displayLarge = base.displayLarge.merge(tokens.displayLarge),
        displayMedium = base.displayMedium.merge(tokens.displayMedium),
        displaySmall = base.displaySmall.merge(tokens.displaySmall),
        headlineLarge = base.headlineLarge.merge(tokens.headlineLarge),
        headlineMedium = base.headlineMedium.merge(tokens.headlineMedium),
        headlineSmall = base.headlineSmall.merge(tokens.headlineSmall),
        titleLarge = base.titleLarge.merge(tokens.titleLarge),
        titleMedium = base.titleMedium.merge(tokens.titleMedium),
        titleSmall = base.titleSmall.merge(tokens.titleSmall),
        bodyLarge = base.bodyLarge.merge(tokens.bodyLarge),
        bodyMedium = base.bodyMedium.merge(tokens.bodyMedium),
        bodySmall = base.bodySmall.merge(tokens.bodySmall),
        labelLarge = base.labelLarge.merge(tokens.labelLarge),
        labelMedium = base.labelMedium.merge(tokens.labelMedium),
        labelSmall = base.labelSmall.merge(tokens.labelSmall),
    )
}
