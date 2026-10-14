package com.floydwiz.seaglass.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.buttons.ClusterItem
import com.floydwiz.seaglass.components.buttons.GlassCluster
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassEdgeDiffusion
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/** 44 dp lens used for bar actions: clear by default, glass for the primary action. */
@Composable
public fun GlassBarLens(icon: String, onClick: () -> Unit, modifier: Modifier = Modifier, primary: Boolean = false, description: String? = null, role: TonalTones = LocalGlassRole.current) {
    GlassSurface(
        finish = if (primary) GlassFinish.Glass else GlassFinish.Clear,
        modifier = modifier.size(44.dp),
        shape = SeaGlassShape.Lens,
        role = role,
        onClick = onClick,
    ) {
        SymbolIcon(icon, size = 22.dp, tint = LocalContentColor.current, contentDescription = description ?: icon)
    }
}

/** Clear cluster of icon actions for a top bar. */
@Composable
public fun GlassBarActions(actions: List<ClusterItem>, onClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.height(44.dp)) {
        GlassCluster(actions, selected = emptySet(), onItemClick = onClick)
    }
}

/**
 * Top bar with no container.
 * - Actions are clear lenses or a clear cluster; the title is plain type, large (34 sp) or small and centred (17 sp).
 * - [scrolled] fades in the 84 dp edge diffusion under it instead of a hard bar.
 */
@Composable
public fun GlassTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    large: Boolean = false,
    scrolled: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    Box(modifier.fillMaxWidth().height(if (large) 132.dp else 64.dp)) {
        if (!large) GlassEdgeDiffusion(Modifier.fillMaxWidth(), visible = scrolled)
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp).padding(top = 10.dp),
            Arrangement.SpaceBetween,
            Alignment.Top,
        ) {
            Box(Modifier.size(width = 44.dp, height = 44.dp)) { leading?.invoke() }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { trailing?.invoke(this) }
        }
        if (large) {
            Text(title, Modifier.padding(start = 18.dp, top = 56.dp), color = ink, fontSize = 34.sp, fontWeight = FontWeight(760), letterSpacing = (-0.4).sp)
            if (subtitle != null) Text(subtitle, Modifier.padding(start = 20.dp, top = 102.dp), color = glassOnSurfaceVariant(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        } else {
            Text(
                title, Modifier.fillMaxWidth().padding(top = 21.dp), color = ink,
                fontSize = 17.sp, fontWeight = FontWeight(680), textAlign = TextAlign.Center,
            )
        }
    }
}
