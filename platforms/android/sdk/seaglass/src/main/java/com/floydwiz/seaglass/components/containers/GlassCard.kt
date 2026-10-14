package com.floydwiz.seaglass.components.containers

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/**
 * Content card: a milk pane with 28 dp corners and a lit top edge.
 * - [emphasized] swaps milk for tinted frost in [role].
 * - [selected] adds a 2 dp accent ring and a 30 dp glass check lens instead of a heavier container.
 * - Anything inset should use concentric corners: an 8 dp inset photo gets 20 dp.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    emphasized: Boolean = false,
    selected: Boolean = false,
    role: TonalTones = LocalGlassRole.current,
    shape: SeaGlassShape = SeaGlassShape.Pane(28.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val ring = if (selected) Modifier.border(2.dp, glassAccent(role), shape) else Modifier
    GlassSurface(
        finish = if (emphasized) GlassFinish.Frost else GlassFinish.Milk,
        modifier = modifier.then(ring),
        shape = shape,
        role = role,
        onClick = onClick,
        reactsTo = if (onClick != null) setOf(GlassState.Hover, GlassState.Focus, GlassState.Press) else emptySet(),
        contentAlignment = Alignment.TopStart,
    ) {
        content()
        if (selected) {
            GlassSurface(
                finish = GlassFinish.Glass,
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).size(30.dp),
                shape = SeaGlassShape.Round,
                role = role,
                reactsTo = emptySet(),
            ) { SymbolIcon("check", size = 20.dp, weight = 700, tint = LocalContentColor.current, contentDescription = "Selected") }
        }
    }
}

/** Clear caption pane laid over imagery (radius 20), for overlay cards and carousels. */
@Composable
fun GlassCaption(modifier: Modifier = Modifier, role: TonalTones = LocalGlassRole.current, content: @Composable BoxScope.() -> Unit) {
    GlassSurface(
        finish = GlassFinish.Clear,
        modifier = modifier,
        shape = SeaGlassShape.Pane(20.dp),
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.CenterStart,
        content = content,
    )
}

/** Inset list: a milk pane with 26 dp corners; rows react without lifting and hairlines start at the text. */
@Composable
fun GlassInsetList(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier,
        shape = SeaGlassShape.Pane(26.dp),
        reactsTo = emptySet(),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(Modifier.padding(6.dp), content = content)
    }
}

/**
 * One row of a [GlassInsetList].
 * - Hover adds a 7% layer, press dips the row into a well and selection fills it with a frost capsule.
 * - [leadingIcon] sits in a 32 dp glass tile with 11 dp corners, tinted by [leadingRole].
 */
@Composable
fun GlassListRow(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    supporting: String? = null,
    leadingIcon: String? = null,
    leadingRole: TonalTones = LocalGlassRole.current,
    leadingSymbol: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    divider: Boolean = true,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val source = remember { MutableInteractionSource() }
    val pressedReal by source.collectIsPressedAsState()
    val pressed = enabled && (pressedReal || LocalForcedGlassState.current == GlassState.Press)
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    Column(modifier.fillMaxWidth()) {
        GlassSurface(
            finish = when {
                selected -> GlassFinish.Frost
                pressed -> GlassFinish.Well
                else -> GlassFinish.Plain
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = SeaGlassShape.Capsule,
            enabled = enabled,
            interactionSource = source,
            onClick = onClick,
            reactsTo = setOf(GlassState.Hover, GlassState.Focus),
            semanticsRole = Role.Button,
            contentColor = if (selected) glassOnRole() else ink,
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(Modifier.padding(start = 10.dp, end = 16.dp, top = 4.dp, bottom = 4.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    GlassSurface(
                        finish = GlassFinish.Glass,
                        modifier = Modifier.size(32.dp),
                        shape = SeaGlassShape.Pane(11.dp),
                        role = leadingRole,
                        enabled = enabled,
                        reactsTo = emptySet(),
                    ) { SymbolIcon(leadingIcon, size = 19.dp, fill = true, tint = LocalContentColor.current) }
                }
                if (leadingSymbol != null) {
                    SymbolIcon(
                        leadingSymbol, size = 22.dp, fill = selected,
                        tint = if (selected) LocalContentColor.current else glassOnSurfaceVariant(),
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(title, color = LocalContentColor.current, fontSize = 15.5.sp, fontWeight = FontWeight(if (selected) 650 else 550), maxLines = 1)
                    if (supporting != null) {
                        Text(supporting, color = if (selected) LocalContentColor.current.copy(alpha = 0.8f) else glassOnSurfaceVariant(), fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                    }
                }
                if (trailing != null) trailing()
            }
        }
        if (divider && !selected && !pressed) {
            HorizontalDivider(
                Modifier.padding(start = if (leadingIcon != null) 56.dp else if (leadingSymbol != null) 54.dp else 16.dp, end = 10.dp),
                thickness = 1.dp,
                color = ink.copy(alpha = if (dark) 0.10f else 0.08f),
            )
        }
    }
}
