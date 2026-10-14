package com.floydwiz.seaglass.color

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.floydwiz.seaglass.tokens.TonalTones

private val anchorTones = intArrayOf(100, 99, 95, 90, 80, 70, 60, 50, 40, 30, 20, 10, 0)
private val generatedTones = intArrayOf(
    0, 4, 6, 10, 12, 17, 20, 22, 24, 30, 34, 40, 50, 60, 70, 80, 85, 87, 90, 92, 94, 95, 96, 98, 99, 100,
)

private val accent1 = intArrayOf(
    android.R.color.system_accent1_0, android.R.color.system_accent1_10, android.R.color.system_accent1_50,
    android.R.color.system_accent1_100, android.R.color.system_accent1_200, android.R.color.system_accent1_300,
    android.R.color.system_accent1_400, android.R.color.system_accent1_500, android.R.color.system_accent1_600,
    android.R.color.system_accent1_700, android.R.color.system_accent1_800, android.R.color.system_accent1_900,
    android.R.color.system_accent1_1000,
)
private val accent2 = intArrayOf(
    android.R.color.system_accent2_0, android.R.color.system_accent2_10, android.R.color.system_accent2_50,
    android.R.color.system_accent2_100, android.R.color.system_accent2_200, android.R.color.system_accent2_300,
    android.R.color.system_accent2_400, android.R.color.system_accent2_500, android.R.color.system_accent2_600,
    android.R.color.system_accent2_700, android.R.color.system_accent2_800, android.R.color.system_accent2_900,
    android.R.color.system_accent2_1000,
)
private val accent3 = intArrayOf(
    android.R.color.system_accent3_0, android.R.color.system_accent3_10, android.R.color.system_accent3_50,
    android.R.color.system_accent3_100, android.R.color.system_accent3_200, android.R.color.system_accent3_300,
    android.R.color.system_accent3_400, android.R.color.system_accent3_500, android.R.color.system_accent3_600,
    android.R.color.system_accent3_700, android.R.color.system_accent3_800, android.R.color.system_accent3_900,
    android.R.color.system_accent3_1000,
)
private val neutral1 = intArrayOf(
    android.R.color.system_neutral1_0, android.R.color.system_neutral1_10, android.R.color.system_neutral1_50,
    android.R.color.system_neutral1_100, android.R.color.system_neutral1_200, android.R.color.system_neutral1_300,
    android.R.color.system_neutral1_400, android.R.color.system_neutral1_500, android.R.color.system_neutral1_600,
    android.R.color.system_neutral1_700, android.R.color.system_neutral1_800, android.R.color.system_neutral1_900,
    android.R.color.system_neutral1_1000,
)
private val neutral2 = intArrayOf(
    android.R.color.system_neutral2_0, android.R.color.system_neutral2_10, android.R.color.system_neutral2_50,
    android.R.color.system_neutral2_100, android.R.color.system_neutral2_200, android.R.color.system_neutral2_300,
    android.R.color.system_neutral2_400, android.R.color.system_neutral2_500, android.R.color.system_neutral2_600,
    android.R.color.system_neutral2_700, android.R.color.system_neutral2_800, android.R.color.system_neutral2_900,
    android.R.color.system_neutral2_1000,
)

/**
 * Role palettes for dynamic colour (API 31+).
 * - Aqua, seafoam and lavender follow the system accent 1, 2 and 3 palettes; neutrals follow neutral 1 and 2.
 * - Tones the system does not publish (4, 6, 12, 85, 87, …) are interpolated between neighbouring anchors.
 * - Sea pink and the four accents stay static; harmonizing them toward the dynamic primary is not done yet.
 */
internal fun dynamicRolePalettes(context: Context): SeaGlassRolePalettes {
    val static = SeaGlassRolePalettes.Static
    return SeaGlassRolePalettes(
        roles = GlassRole.entries.associateWith { static[it] } + mapOf(
            GlassRole.Aqua to systemPalette(context, accent1),
            GlassRole.Seafoam to systemPalette(context, accent2),
            GlassRole.Lavender to systemPalette(context, accent3),
        ),
        neutral = systemPalette(context, neutral1),
        neutralVariant = systemPalette(context, neutral2),
    )
}

private fun systemPalette(context: Context, resIds: IntArray): TonalTones {
    val anchors = anchorTones.indices.associate { anchorTones[it] to Color(context.getColor(resIds[it])) }
    val sorted = anchors.keys.sorted()
    val tones = generatedTones.associateWith { tone ->
        val low = sorted.last { it <= tone }
        val high = sorted.first { it >= tone }
        if (low == high) {
            anchors.getValue(low)
        } else {
            lerp(anchors.getValue(low), anchors.getValue(high), (tone - low).toFloat() / (high - low))
        }
    }
    return TonalTones(0f, 0f, tones)
}
