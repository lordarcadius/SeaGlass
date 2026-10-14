package com.floydwiz.seaglass.components.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.tokens.SeaGlassSizeTokens

public enum class GlassButtonStyle { Glass, Frost, Clear, Plain, Destructive }

public enum class GlassButtonSize(public val height: Dp, public val padding: Dp, public val fontSize: Float, public val iconSize: Dp, public val gap: Dp) {
    S(SeaGlassSizeTokens.ButtonS, 14.dp, 14f, 18.dp, 6.dp),
    M(SeaGlassSizeTokens.ButtonM, 18.dp, 15f, 20.dp, 8.dp),
    L(SeaGlassSizeTokens.ButtonL, 24.dp, 17f, 22.dp, 10.dp),
    XL(SeaGlassSizeTokens.ButtonXL, 30.dp, 20f, 26.dp, 12.dp),
}

private fun GlassButtonStyle.finish() = when (this) {
    GlassButtonStyle.Glass, GlassButtonStyle.Destructive -> GlassFinish.Glass
    GlassButtonStyle.Frost -> GlassFinish.Frost
    GlassButtonStyle.Clear -> GlassFinish.Clear
    GlassButtonStyle.Plain -> GlassFinish.Plain
}

/**
 * Labelled capsule button.
 * - Glass is the one primary action in a view, Frost is secondary, Clear is neutral glass with an accent label,
 *   Plain has no material until pressed, Destructive is Glass in the sea pink role.
 * - Every size keeps a 48 dp touch target.
 */
@Composable
public fun GlassButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: GlassButtonStyle = GlassButtonStyle.Glass,
    size: GlassButtonSize = GlassButtonSize.M,
    leadingIcon: String? = null,
    enabled: Boolean = true,
    role: TonalTones = if (style == GlassButtonStyle.Destructive) LocalRolePalettes.current[GlassRole.SeaPink] else LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val accent = glassAccent(role)
    val fillIcon = style == GlassButtonStyle.Glass || style == GlassButtonStyle.Frost || style == GlassButtonStyle.Destructive
    val foreground = if (style == GlassButtonStyle.Clear || style == GlassButtonStyle.Plain) accent else null
    GlassSurface(
        finish = style.finish(),
        modifier = modifier.minimumInteractiveComponentSize().height(size.height).defaultMinSize(minWidth = size.height),
        shape = SeaGlassShape.Capsule,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        contentColor = foreground,
    ) {
        Row(
            Modifier.padding(horizontal = size.padding),
            Arrangement.spacedBy(size.gap),
            Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) SymbolIcon(leadingIcon, size = size.iconSize, fill = fillIcon, tint = LocalContentColor.current)
            Text(label, color = LocalContentColor.current, fontSize = size.fontSize.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp, maxLines = 1)
        }
    }
}

/** Icon-only lens button, 48 dp by default; the symbol is 48% of [size]. */
@Composable
public fun GlassIconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: GlassButtonStyle = GlassButtonStyle.Clear,
    size: Dp = SeaGlassSizeTokens.IconButton,
    filled: Boolean = style == GlassButtonStyle.Glass,
    shape: SeaGlassShape = SeaGlassShape.Lens,
    iconScale: Float = 0.48f,
    enabled: Boolean = true,
    role: TonalTones = if (style == GlassButtonStyle.Destructive) LocalRolePalettes.current[GlassRole.SeaPink] else LocalGlassRole.current,
    contentDescription: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val foreground = when (style) {
        GlassButtonStyle.Plain -> glassOnSurfaceVariant()
        else -> null
    }
    GlassSurface(
        finish = style.finish(),
        modifier = modifier.minimumInteractiveComponentSize().defaultMinSize(size, size).height(size),
        shape = shape,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        contentColor = foreground,
    ) {
        SymbolIcon(icon, size = size * iconScale, fill = filled, tint = LocalContentColor.current, contentDescription = contentDescription)
    }
}

/** Toggle lens: clear with an outlined symbol when off, tinted glass with a filled symbol when on. */
@Composable
public fun GlassToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: String,
    modifier: Modifier = Modifier,
    size: Dp = SeaGlassSizeTokens.IconButton,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    contentDescription: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    GlassSurface(
        finish = if (checked) GlassFinish.Glass else GlassFinish.Clear,
        modifier = modifier.minimumInteractiveComponentSize().defaultMinSize(size, size).height(size),
        shape = SeaGlassShape.Lens,
        role = role,
        enabled = enabled,
        interactionSource = source,
        onClick = { onCheckedChange(!checked) },
        semanticsRole = Role.Switch,
    ) {
        SymbolIcon(icon, size = size * 0.48f, fill = checked, tint = LocalContentColor.current, contentDescription = contentDescription)
    }
}
