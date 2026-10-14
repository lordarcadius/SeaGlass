package com.floydwiz.seaglass.components.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.tokens.SeaGlassRadiusTokens
import com.floydwiz.seaglass.tokens.SeaGlassSizeTokens

public class RailDestination(public val icon: String, public val label: String, public val badge: String? = null)

/**
 * Tablet rail: a clear capsule 80 dp wide with the pebble action on top; the current destination is a frost lens.
 */
@Composable
public fun GlassNavigationRail(
    destinations: List<RailDestination>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    fabIcon: String = "edit",
    onFabClick: (() -> Unit)? = null,
    role: TonalTones = LocalGlassRole.current,
) {
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    val seaPink = LocalRolePalettes.current[GlassRole.SeaPink]
    val height = (104 + 72 * destinations.size + 12).dp
    Box(modifier.size(SeaGlassSizeTokens.Rail, height)) {
        GlassSurface(GlassFinish.Clear, Modifier.size(SeaGlassSizeTokens.Rail, height), SeaGlassShape.Pane(40.dp), role, reactsTo = emptySet())
        if (onFabClick != null) {
            GlassSurface(
                finish = GlassFinish.Glass,
                modifier = Modifier.offset(12.dp, 14.dp).size(56.dp),
                shape = SeaGlassShape.Pebble,
                role = role,
                onClick = onFabClick,
            ) { SymbolIcon(fabIcon, size = 26.dp, fill = true, tint = LocalContentColor.current, contentDescription = "Compose") }
        }
        destinations.forEachIndexed { index, destination ->
            val selected = index == selectedIndex
            Column(
                Modifier
                    .offset(0.dp, (104 + 72 * index).dp)
                    .width(SeaGlassSizeTokens.Rail)
                    .semantics { this.selected = selected }
                    .clickable(role = Role.Tab) { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                GlassSurface(
                    finish = if (selected) GlassFinish.Frost else GlassFinish.Plain,
                    modifier = Modifier.size(56.dp, 32.dp),
                    shape = SeaGlassShape.Pane(16.dp),
                    role = role,
                    contentColor = if (selected) glassOnRole(role) else ink,
                ) { SymbolIcon(destination.icon, size = 22.dp, fill = selected, tint = LocalContentColor.current) }
                Text(destination.label, color = ink, fontSize = 12.sp, fontWeight = FontWeight(if (selected) 700 else 550), textAlign = TextAlign.Center)
            }
            if (destination.badge != null) {
                GlassSurface(
                    finish = GlassFinish.Bead,
                    modifier = Modifier.offset(50.dp, (104 + 72 * index - 6).dp).size(24.dp, 16.dp),
                    shape = SeaGlassShape.Pane(8.dp),
                    role = seaPink,
                    reactsTo = emptySet(),
                ) { Text(destination.badge, color = LocalContentColor.current, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

public class SidebarDestination(public val icon: String, public val label: String, public val count: String? = null)

/** Expanded sidebar: content, so a milk pane (272 dp wide, radius 32) with a frost row for the current destination. */
@Composable
public fun GlassSidebar(
    title: String,
    destinations: List<SidebarDestination>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    primaryAction: (@Composable () -> Unit)? = null,
    role: TonalTones = LocalGlassRole.current,
) {
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    val variant = glassOnSurfaceVariant()
    val height = (128 + 50 * destinations.size + 26).dp
    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier.size(SeaGlassSizeTokens.Sidebar, height),
        shape = SeaGlassShape.Pane(SeaGlassRadiusTokens.Pane32),
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.TopStart,
    ) {
        Text(title, Modifier.offset(24.dp, 20.dp), color = ink, fontSize = 22.sp, fontWeight = FontWeight(720))
        Box(Modifier.offset(16.dp, 62.dp)) { primaryAction?.invoke() }
        destinations.forEachIndexed { index, destination ->
            val selected = index == selectedIndex
            val color = if (selected) glassOnRole(role) else ink
            GlassSurface(
                finish = if (selected) GlassFinish.Frost else GlassFinish.Plain,
                modifier = Modifier.offset(12.dp, (128 + 50 * index).dp).size(248.dp, 46.dp),
                shape = SeaGlassShape.Pane(23.dp),
                role = role,
                onClick = { onSelect(index) },
                semanticsRole = Role.Tab,
                contentColor = color,
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
                    SymbolIcon(destination.icon, size = 22.dp, fill = selected, tint = if (selected) color else variant)
                    Text(destination.label, Modifier.weight(1f), color = color, fontSize = 15.sp, fontWeight = FontWeight(if (selected) 650 else 500))
                    if (destination.count != null) Text(destination.count, color = if (selected) color else variant, fontSize = 13.sp, fontWeight = FontWeight(650))
                }
            }
        }
    }
}
