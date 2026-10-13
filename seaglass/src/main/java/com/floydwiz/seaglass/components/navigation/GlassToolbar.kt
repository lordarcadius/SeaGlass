package com.floydwiz.seaglass.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole

class ToolItem(val icon: String, val description: String)

/**
 * Floating toolbar: tools share a clear capsule, the active tool is a frost lens and the confirm action is a
 * separate glass lens. Pressing an inactive tool condenses a clear lens that swells 21% past the capsule.
 * - [vertical] draws the 56 dp wide strip, normally tinted with another [activeRole].
 */
@Composable
fun GlassFloatingToolbar(
    tools: List<ToolItem>,
    selectedIndex: Int,
    onToolClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    activeRole: TonalTones = LocalGlassRole.current,
    onConfirm: (() -> Unit)? = null,
    previewStates: Map<Int, GlassState> = emptyMap(),
    role: TonalTones = LocalGlassRole.current,
) {
    val length = (48 * tools.size + 8).dp
    @Composable
    fun Items() {
        tools.forEachIndexed { index, tool ->
            val active = index == selectedIndex
            CompositionLocalProvider(LocalForcedGlassState provides previewStates[index]) {
            GlassSurface(
                finish = if (active) GlassFinish.Frost else GlassFinish.Plain,
                modifier = Modifier.size(48.dp),
                shape = SeaGlassShape.Lens,
                role = if (active) activeRole else role,
                onClick = { onToolClick(index) },
                semanticsRole = Role.Button,
                pressScale = if (active) 0.96f else 58f / 48f,
                contentColor = if (active) glassOnRole(activeRole) else null,
            ) {
                SymbolIcon(tool.icon, size = 24.dp, fill = active, tint = LocalContentColor.current, contentDescription = tool.description)
            }
            }
        }
    }
    if (vertical) {
        Box(modifier.size(56.dp, length)) {
            GlassSurface(GlassFinish.Clear, Modifier.size(56.dp, length), SeaGlassShape.Pane(28.dp), role, reactsTo = emptySet())
            Column(Modifier.padding(4.dp)) { Items() }
        }
    } else {
        Row(modifier, Arrangement.spacedBy(10.dp), Alignment.CenterVertically) {
            Box(Modifier.size(length, 56.dp)) {
                GlassSurface(GlassFinish.Clear, Modifier.size(length, 56.dp), SeaGlassShape.Capsule, role, reactsTo = emptySet())
                Row(Modifier.padding(4.dp)) { Items() }
            }
            if (onConfirm != null) {
                GlassSurface(GlassFinish.Glass, Modifier.size(56.dp), SeaGlassShape.Lens, role, onClick = onConfirm) {
                    SymbolIcon("check", size = 28.dp, weight = 600, tint = LocalContentColor.current, contentDescription = "Confirm")
                }
            }
        }
    }
}
