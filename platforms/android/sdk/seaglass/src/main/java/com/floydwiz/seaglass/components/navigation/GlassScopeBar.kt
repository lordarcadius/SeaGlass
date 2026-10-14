package com.floydwiz.seaglass.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.white
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

public class ScopeItem(public val label: String, public val count: Int? = null)

/** Scope bar for filtering one list: a clear capsule 44 dp tall, a frost lens on the active scope, beads for counts. */
@Composable
public fun GlassScopeBar(
    scopes: List<ScopeItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    role: TonalTones = LocalGlassRole.current,
) {
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    val seaPink = LocalRolePalettes.current[GlassRole.SeaPink]
    GlassSurface(
        finish = GlassFinish.Clear,
        modifier = modifier.height(44.dp),
        shape = SeaGlassShape.Capsule,
        role = role,
        reactsTo = emptySet(),
    ) {
        Row(Modifier.padding(4.dp)) {
            scopes.forEachIndexed { index, scope ->
                val selected = index == selectedIndex
                GlassSurface(
                    finish = if (selected) GlassFinish.Frost else GlassFinish.Plain,
                    modifier = Modifier.height(36.dp),
                    shape = SeaGlassShape.Capsule,
                    role = role,
                    onClick = { onSelect(index) },
                    semanticsRole = Role.Tab,
                    contentColor = if (selected) glassOnRole(role) else ink,
                ) {
                    Row(Modifier.padding(horizontal = 16.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                        Text(scope.label, color = LocalContentColor.current, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        if (scope.count != null) {
                            GlassSurface(
                                finish = GlassFinish.Bead,
                                modifier = Modifier.size(18.dp),
                                shape = SeaGlassShape.Round,
                                role = seaPink,
                                reactsTo = emptySet(),
                            ) { Text(scope.count.toString(), color = LocalContentColor.current, fontSize = 10.5.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
    }
}

/** Page dots in a small well; the current page is a stretched 20×8 pearl. */
@Composable
public fun GlassPageDots(count: Int, current: Int, modifier: Modifier = Modifier, role: TonalTones = LocalGlassRole.current) {
    val dark = LocalSeaGlassDark.current
    GlassSurface(
        finish = GlassFinish.Well,
        modifier = modifier.size(width = ((count - 1) * 14 + 20 + 24).dp, height = 22.dp),
        shape = SeaGlassShape.Pane(11.dp),
        role = role,
        reactsTo = emptySet(),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(count) { index ->
                if (index == current) {
                    GlassSurface(GlassFinish.Pearl, Modifier.size(20.dp, 8.dp), SeaGlassShape.Pane(4.dp), role, reactsTo = emptySet())
                } else {
                    Box(
                        Modifier
                            .size(7.dp)
                            .shadow(1.dp, CircleShape)
                            .background(white(if (dark) 0.4f else 0.85f), CircleShape),
                    )
                }
            }
        }
    }
}
