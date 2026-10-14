package com.floydwiz.seaglass.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole

class ClusterItem(val icon: String, val description: String)

/**
 * Related actions in one clear capsule.
 * - Each item is a 40 dp lens; the active ones are frost lenses with a filled symbol.
 * - Pressing an inactive item condenses a clear lens that bulges 4 dp past the capsule (press scale 1.2).
 */
@Composable
fun GlassCluster(
    items: List<ClusterItem>,
    selected: Set<Int>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    role: TonalTones = LocalGlassRole.current,
) {
    val itemSize = 40.dp
    Box(modifier.size((44 * items.size + 4).dp, 48.dp)) {
        GlassSurface(GlassFinish.Clear, Modifier.matchParentSize(), SeaGlassShape.Capsule, role)
        Row(Modifier.padding(4.dp), Arrangement.spacedBy(4.dp)) {
            items.forEachIndexed { index, item ->
                val active = index in selected
                GlassSurface(
                    finish = if (active) GlassFinish.Frost else GlassFinish.Plain,
                    modifier = Modifier.size(itemSize),
                    shape = SeaGlassShape.Lens,
                    role = role,
                    onClick = { onItemClick(index) },
                    semanticsRole = Role.Checkbox,
                    pressScale = if (active) 0.96f else 1.2f,
                    contentColor = if (active) glassOnRole(role) else null,
                ) {
                    SymbolIcon(item.icon, size = 22.dp, fill = active, tint = LocalContentColor.current, contentDescription = item.description)
                }
            }
        }
    }
}
