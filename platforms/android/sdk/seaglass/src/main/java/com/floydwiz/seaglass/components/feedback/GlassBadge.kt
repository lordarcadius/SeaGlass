package com.floydwiz.seaglass.components.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/**
 * Bead badge: a small solid glass drop, never taller than 20 dp.
 * - No [text] draws a status dot of [dotSize]; a short count is a 20 dp circle and longer text widens it.
 */
@Composable
fun GlassBadge(
    modifier: Modifier = Modifier,
    text: String? = null,
    role: TonalTones = LocalRolePalettes.current[GlassRole.SeaPink],
    dotSize: Dp = 11.dp,
) {
    if (text == null) {
        GlassSurface(GlassFinish.Bead, modifier.size(dotSize), SeaGlassShape.Round, role, reactsTo = emptySet())
    } else {
        GlassSurface(
            finish = GlassFinish.Bead,
            modifier = modifier.height(20.dp).defaultMinSize(minWidth = 20.dp),
            shape = SeaGlassShape.Capsule,
            role = role,
            reactsTo = emptySet(),
        ) {
            Text(text, Modifier.padding(horizontal = 6.dp), color = LocalContentColor.current, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

/** Plain tooltip: an ink capsule 32 dp tall. */
@Composable
fun GlassTooltip(text: String, modifier: Modifier = Modifier) {
    GlassSurface(
        finish = GlassFinish.Ink,
        modifier = modifier.height(32.dp),
        shape = SeaGlassShape.Capsule,
        reactsTo = emptySet(),
    ) {
        Text(text, Modifier.padding(horizontal = 16.dp), color = LocalContentColor.current, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

/** Rich tooltip: a milk pane (radius 24) with a title, body copy and a plain action. */
@Composable
fun GlassRichTooltip(
    title: String,
    text: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    role: TonalTones = LocalGlassRole.current,
) {
    val ink = if (LocalSeaGlassDark.current) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier.width(300.dp),
        shape = SeaGlassShape.Pane(24.dp),
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)) {
            Text(title, color = ink, fontSize = 15.5.sp, fontWeight = FontWeight.Bold)
            Text(text, Modifier.padding(top = 6.dp), color = glassOnSurfaceVariant(), fontSize = 13.5.sp, fontWeight = FontWeight(450), lineHeight = 19.5.sp)
            Box(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                GlassButton(actionLabel, onAction, style = GlassButtonStyle.Plain, size = GlassButtonSize.S, role = role)
            }
        }
    }
}

/** Anchors a [GlassTooltip] to [content]; long-press or hover shows it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTooltipBox(tooltip: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { GlassTooltip(tooltip) },
        state = rememberTooltipState(),
        modifier = modifier,
        content = content,
    )
}
