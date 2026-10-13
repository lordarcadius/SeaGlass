package com.floydwiz.seaglass.color

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Static (non-dynamic) role mapping from the F1 board. Tones are noted per role.
val SeaGlassLightScheme = lightColorScheme(
    primary = Color(0xFF006783), // aqua · T40
    onPrimary = Color(0xFFFFFFFF), // T100
    primaryContainer = Color(0xFFB7EAFF), // T90
    onPrimaryContainer = Color(0xFF001F2A), // T10
    secondary = Color(0xFF006C4C), // seafoam · T40
    onSecondary = Color(0xFFFFFFFF), // T100
    secondaryContainer = Color(0xFF94F5CB), // T90
    onSecondaryContainer = Color(0xFF002115), // T10
    tertiary = Color(0xFF734B9E), // lavender · T40
    onTertiary = Color(0xFFFFFFFF), // T100
    tertiaryContainer = Color(0xFFF1DBFF), // T90
    onTertiaryContainer = Color(0xFF2B0053), // T10
    error = Color(0xFFAB323E), // seapink · T40
    onError = Color(0xFFFFFFFF), // T100
    errorContainer = Color(0xFFFFDADB), // T90
    onErrorContainer = Color(0xFF410009), // T10
    surface = Color(0xFFF6FAFD), // N T98
    surfaceContainerLowest = Color(0xFFFFFFFF), // N T100
    surfaceContainerLow = Color(0xFFF0F4F8), // N T96
    surfaceContainer = Color(0xFFE9EEF1), // N T94
    surfaceContainerHigh = Color(0xFFE4E9EC), // N T92
    surfaceContainerHighest = Color(0xFFDFE3E6), // N T90
    onSurface = Color(0xFF171C1F), // N T10
    onSurfaceVariant = Color(0xFF40484C), // NV T30
    outline = Color(0xFF70787D), // NV T50
    outlineVariant = Color(0xFFC0C8CC), // NV T80
)

val SeaGlassDarkScheme = darkColorScheme(
    primary = Color(0xFF55D5FF), // aqua · T80
    onPrimary = Color(0xFF003545), // T20
    primaryContainer = Color(0xFF004D63), // T30
    onPrimaryContainer = Color(0xFFB7EAFF), // T90
    secondary = Color(0xFF78D9B0), // seafoam · T80
    onSecondary = Color(0xFF003826), // T20
    secondaryContainer = Color(0xFF005138), // T30
    onSecondaryContainer = Color(0xFF94F5CB), // T90
    tertiary = Color(0xFFDDB7FF), // lavender · T80
    onTertiary = Color(0xFF431A6C), // T20
    tertiaryContainer = Color(0xFF5A3384), // T30
    onTertiaryContainer = Color(0xFFF1DBFF), // T90
    error = Color(0xFFFFB3B6), // seapink · T80
    onError = Color(0xFF680015), // T20
    errorContainer = Color(0xFF8A1829), // T30
    onErrorContainer = Color(0xFFFFDADB), // T90
    surface = Color(0xFF0F1417), // N T6
    surfaceContainerLowest = Color(0xFF0A0F11), // N T4
    surfaceContainerLow = Color(0xFF171C1F), // N T10
    surfaceContainer = Color(0xFF1B2023), // N T12
    surfaceContainerHigh = Color(0xFF262B2D), // N T17
    surfaceContainerHighest = Color(0xFF313537), // N T22
    onSurface = Color(0xFFDFE3E6), // N T90
    onSurfaceVariant = Color(0xFFC0C8CC), // NV T80
    outline = Color(0xFF8A9296), // NV T60
    outlineVariant = Color(0xFF40484C), // NV T30
)
