package com.floydwiz.seaglass.material

import androidx.compose.runtime.compositionLocalOf

/**
 * Makes glass below take part in adaptive tint: each surface samples the luminance of the [GlassBackdrop] behind it and
 * fades in the protective tint above 0.4. Surfaces outside a backdrop, or with no bright content behind, are unchanged.
 */
val LocalAdaptiveTint = compositionLocalOf { false }
