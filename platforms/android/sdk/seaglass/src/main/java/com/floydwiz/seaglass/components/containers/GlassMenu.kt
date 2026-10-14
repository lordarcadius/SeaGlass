package com.floydwiz.seaglass.components.containers

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.tokens.SeaGlassRadiusTokens
import com.floydwiz.seaglass.tokens.SeaGlassSizeTokens

/** Milk pane holding menu rows: radius 24, 8 dp padding, rows 44 dp tall. */
@Composable
public fun GlassMenu(modifier: Modifier = Modifier, width: androidx.compose.ui.unit.Dp = 214.dp, content: @Composable ColumnScope.() -> Unit) {
    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier.width(width).background(MaterialTheme.colorScheme.surface, SeaGlassShape.Pane(SeaGlassRadiusTokens.Pane24)),
        shape = SeaGlassShape.Pane(SeaGlassRadiusTokens.Pane24),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(Modifier.padding(8.dp), content = content)
    }
}

/**
 * One menu row.
 * - [highlighted] draws the frost capsule; [danger] uses the sea pink role.
 * - A plain row condenses clear glass while pressed.
 */
@Composable
public fun GlassMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    highlighted: Boolean = false,
    danger: Boolean = false,
    trailing: String? = null,
) {
    val dark = LocalSeaGlassDark.current
    val role = LocalGlassRole.current
    val seaPink = LocalRolePalettes.current[GlassRole.SeaPink]
    val content = when {
        danger -> if (dark) seaPink[80] else seaPink[40]
        highlighted -> glassOnRole(role)
        else -> LocalContentColor.current
    }
    val symbol = if (danger || highlighted) content else glassOnSurfaceVariant()
    GlassSurface(
        finish = if (highlighted) GlassFinish.Frost else GlassFinish.Plain,
        modifier = modifier.fillMaxWidth().height(SeaGlassSizeTokens.MenuRow),
        shape = SeaGlassShape.Capsule,
        onClick = onClick,
        semanticsRole = Role.Button,
        contentColor = content,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
            if (icon != null) SymbolIcon(icon, size = 20.dp, fill = highlighted, tint = symbol)
            Text(label, Modifier.weight(1f), color = content, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            if (trailing != null) Text(trailing, color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
public fun GlassMenuDivider() {
    Box(Modifier.fillMaxWidth().height(9.dp).padding(horizontal = 16.dp, vertical = 4.dp)) {
        HorizontalDivider(color = LocalContentColor.current.copy(alpha = 0.1f))
    }
}
