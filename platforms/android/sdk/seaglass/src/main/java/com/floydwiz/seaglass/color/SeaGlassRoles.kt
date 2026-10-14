package com.floydwiz.seaglass.color

import androidx.compose.runtime.Immutable

enum class GlassRole(val label: String) {
    Aqua("Aqua"),
    Seafoam("Seafoam"),
    Lavender("Lavender"),
    SeaPink("Sea pink"),
    Cobalt("Cobalt"),
    Honey("Honey amber"),
    Kelly("Kelly green"),
    Cornflower("Cornflower"),
}

@Immutable
class SeaGlassRolePalettes(
    private val roles: Map<GlassRole, TonalTones>,
    val neutral: TonalTones,
    val neutralVariant: TonalTones,
) {
    operator fun get(role: GlassRole): TonalTones = roles.getValue(role)

    companion object {
        val Static = SeaGlassRolePalettes(
            roles = mapOf(
                GlassRole.Aqua to SeaGlassPalettes.Aqua,
                GlassRole.Seafoam to SeaGlassPalettes.Seafoam,
                GlassRole.Lavender to SeaGlassPalettes.Lavender,
                GlassRole.SeaPink to SeaGlassPalettes.SeaPink,
                GlassRole.Cobalt to SeaGlassPalettes.Cobalt,
                GlassRole.Honey to SeaGlassPalettes.Honey,
                GlassRole.Kelly to SeaGlassPalettes.Kelly,
                GlassRole.Cornflower to SeaGlassPalettes.Cornflower,
            ),
            neutral = SeaGlassPalettes.Neutral,
            neutralVariant = SeaGlassPalettes.NeutralVariant,
        )
    }
}
