package com.floydwiz.seaglass.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.containers.GlassMenu
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole

/**
 * Two pieces of the same glass with a 3 dp seam and 12 dp inner corners.
 * - Opening rounds the trailing piece into a lens (with the focus halo) and drops a milk menu below.
 * - [menu] is shown in a popup 12 dp under the button while [expanded].
 */
@Composable
public fun GlassSplitButton(
    label: String,
    leadingIcon: String,
    onClick: () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    style: GlassButtonStyle = GlassButtonStyle.Glass,
    role: TonalTones = LocalGlassRole.current,
    menu: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier) {
        GlassSplitRow(label, leadingIcon, onClick, expanded, onExpandedChange, style, role)
        if (expanded) {
            val offsetY = with(LocalDensity.current) { 56.dp.roundToPx() }
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, offsetY),
                onDismissRequest = { onExpandedChange(false) },
                properties = PopupProperties(focusable = true),
            ) { GlassMenu(content = menu) }
        }
    }
}

/** The two split pieces without the menu; the catalogue draws the open state with a static menu. */
@Composable
public fun GlassSplitRow(
    label: String,
    leadingIcon: String,
    onClick: () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    style: GlassButtonStyle = GlassButtonStyle.Glass,
    role: TonalTones = LocalGlassRole.current,
) {
    val finish = if (style == GlassButtonStyle.Frost) GlassFinish.Frost else if (style == GlassButtonStyle.Clear) GlassFinish.Clear else GlassFinish.Glass
    val foreground = if (finish == GlassFinish.Clear) glassAccent(role) else null
    val fill = finish != GlassFinish.Clear
    Row(Modifier.height(44.dp), Arrangement.spacedBy(3.dp)) {
        GlassSurface(
            finish = finish,
            modifier = Modifier.height(44.dp).defaultMinSize(minWidth = 106.dp),
            shape = SeaGlassShape.Corners(22.dp, 12.dp, 12.dp, 22.dp),
            role = role,
            onClick = onClick,
            contentColor = foreground,
        ) {
            Row(Modifier.padding(start = 16.dp, end = 14.dp), Arrangement.spacedBy(6.dp), Alignment.CenterVertically) {
                SymbolIcon(leadingIcon, size = 20.dp, fill = fill, tint = LocalContentColor.current)
                Text(label, color = LocalContentColor.current, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp, maxLines = 1)
            }
        }
        CompositionLocalProvider(LocalForcedGlassState provides (if (expanded) GlassState.Focus else LocalForcedGlassState.current)) {
            GlassSurface(
                finish = finish,
                modifier = Modifier.size(width = if (expanded) 44.dp else 40.dp, height = 44.dp),
                shape = if (expanded) SeaGlassShape.Pane(22.dp) else SeaGlassShape.Corners(12.dp, 22.dp, 22.dp, 12.dp),
                role = role,
                onClick = { onExpandedChange(!expanded) },
                semanticsRole = Role.Button,
                contentColor = foreground,
            ) {
                SymbolIcon(if (expanded) "expand_less" else "expand_more", size = 22.dp, tint = LocalContentColor.current, contentDescription = "More options")
            }
        }
    }
}
