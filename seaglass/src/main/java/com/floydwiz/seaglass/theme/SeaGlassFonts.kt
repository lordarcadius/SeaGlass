package com.floydwiz.seaglass.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.floydwiz.seaglass.R

val RobotoFlex = FontFamily(
    listOf(300, 400, 500, 600, 700, 800).map { Font(R.font.roboto_flex, FontWeight(it)) },
)

internal fun symbolsFamily(fill: Boolean, weight: Int, opticalSize: Float) = FontFamily(
    Font(
        resId = R.font.material_symbols_rounded,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.Setting("FILL", if (fill) 1f else 0f),
            FontVariation.Setting("GRAD", 0f),
            FontVariation.Setting("opsz", opticalSize),
        ),
    ),
)
