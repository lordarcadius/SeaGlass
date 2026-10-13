package com.floydwiz.seaglass.material

import androidx.compose.runtime.compositionLocalOf

/** Marks glass below as floating over content that can be bright, so dark clear glass gets the adaptive tint. */
val LocalAdaptiveTint = compositionLocalOf { false }
