package com.floydwiz.seaglass.components.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.material.white
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.tokens.SeaGlassSizeTokens

private val ChipHeight = SeaGlassSizeTokens.Chip

/** Filter chip: a sunken well when off, tinted glass with a heavy check when on (selection lifts). */
@Composable
public fun GlassFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    GlassSurface(
        finish = if (selected) GlassFinish.Glass else GlassFinish.Well,
        modifier = modifier.minimumInteractiveComponentSize().height(ChipHeight),
        shape = SeaGlassShape.Capsule,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        semanticsRole = Role.Checkbox,
        contentColor = if (selected) null else glassOnSurfaceVariant(),
    ) {
        Row(
            Modifier.padding(start = if (selected) 12.dp else 14.dp, end = 14.dp),
            Arrangement.spacedBy(6.dp),
            Alignment.CenterVertically,
        ) {
            if (selected) SymbolIcon("check", size = 18.dp, weight = 700, tint = LocalContentColor.current)
            Text(label, color = LocalContentColor.current, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

/** Input chip: a frost capsule with an avatar bead and a small remove lens. */
@Composable
public fun GlassInputChip(
    label: String,
    initial: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val dark = LocalSeaGlassDark.current
    GlassSurface(
        finish = GlassFinish.Frost,
        modifier = modifier.minimumInteractiveComponentSize().height(ChipHeight),
        shape = SeaGlassShape.Capsule,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(start = 4.dp, end = 6.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            GlassSurface(
                finish = GlassFinish.Bead,
                modifier = Modifier.size(26.dp),
                shape = SeaGlassShape.Round,
                role = LocalRolePalettes.current[GlassRole.Lavender],
                enabled = enabled,
                reactsTo = emptySet(),
            ) {
                Text(initial, color = LocalContentColor.current, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(label, color = LocalContentColor.current, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(white(if (dark) 0.14f else 0.6f))
                    .clickable(enabled = enabled, role = Role.Button, onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                SymbolIcon("close", size = 15.dp, tint = LocalContentColor.current, contentDescription = "Remove $label")
            }
        }
    }
}

/** Suggestion chip: clear glass with an accent symbol. */
@Composable
public fun GlassSuggestionChip(
    label: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    GlassSurface(
        finish = GlassFinish.Clear,
        modifier = modifier.minimumInteractiveComponentSize().height(ChipHeight),
        shape = SeaGlassShape.Capsule,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
    ) {
        Row(Modifier.padding(start = 12.dp, end = 14.dp), Arrangement.spacedBy(6.dp), Alignment.CenterVertically) {
            SymbolIcon(icon, size = 18.dp, fill = true, tint = if (enabled) glassAccent(role) else LocalContentColor.current)
            Text(label, color = LocalContentColor.current, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}
