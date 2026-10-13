package com.floydwiz.seaglass.material

import androidx.compose.ui.graphics.Color

internal fun rgba(r: Int, g: Int, b: Int, a: Float) = Color(r / 255f, g / 255f, b / 255f, a.coerceIn(0f, 1f))

internal fun white(a: Float) = Color(1f, 1f, 1f, a.coerceIn(0f, 1f))

internal fun Color.alpha(a: Float) = copy(alpha = a.coerceIn(0f, 1f))

internal val Color.transparent get() = copy(alpha = 0f)

/** Linear mix in sRGB, matching `mix()` in recipes.js. */
internal fun mix(a: Color, b: Color, t: Float) = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f,
)
