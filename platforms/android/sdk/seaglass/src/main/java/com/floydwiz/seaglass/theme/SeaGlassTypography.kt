package com.floydwiz.seaglass.theme

import androidx.compose.material3.Typography

val SeaGlassTypography: Typography = Typography().let { base ->
    base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = RobotoFlex),
        displayMedium = base.displayMedium.copy(fontFamily = RobotoFlex),
        displaySmall = base.displaySmall.copy(fontFamily = RobotoFlex),
        headlineLarge = base.headlineLarge.copy(fontFamily = RobotoFlex),
        headlineMedium = base.headlineMedium.copy(fontFamily = RobotoFlex),
        headlineSmall = base.headlineSmall.copy(fontFamily = RobotoFlex),
        titleLarge = base.titleLarge.copy(fontFamily = RobotoFlex),
        titleMedium = base.titleMedium.copy(fontFamily = RobotoFlex),
        titleSmall = base.titleSmall.copy(fontFamily = RobotoFlex),
        bodyLarge = base.bodyLarge.copy(fontFamily = RobotoFlex),
        bodyMedium = base.bodyMedium.copy(fontFamily = RobotoFlex),
        bodySmall = base.bodySmall.copy(fontFamily = RobotoFlex),
        labelLarge = base.labelLarge.copy(fontFamily = RobotoFlex),
        labelMedium = base.labelMedium.copy(fontFamily = RobotoFlex),
        labelSmall = base.labelSmall.copy(fontFamily = RobotoFlex),
    )
}
