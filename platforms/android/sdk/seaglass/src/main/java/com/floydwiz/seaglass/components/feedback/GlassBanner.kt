package com.floydwiz.seaglass.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes

public enum class BannerKind(public val role: GlassRole, public val icon: String) {
    Info(GlassRole.Aqua, "info"),
    Warning(GlassRole.Honey, "warning"),
    Error(GlassRole.SeaPink, "error"),
    Success(GlassRole.Kelly, "check_circle"),
}

/** Persistent banner: tinted frost in the role colour of its meaning, 52 dp tall, with the action as plain text. */
@Composable
public fun GlassBanner(
    message: String,
    kind: BannerKind,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val role = LocalRolePalettes.current[kind.role]
    GlassSurface(
        finish = GlassFinish.Frost,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = SeaGlassShape.Capsule,
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(start = 16.dp, end = 6.dp), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
            SymbolIcon(kind.icon, size = 22.dp, fill = true, tint = LocalContentColor.current)
            Text(message, Modifier.weight(1f), color = LocalContentColor.current, fontSize = 15.sp, fontWeight = FontWeight(550), maxLines = 2)
            GlassSurface(
                finish = GlassFinish.Plain,
                modifier = Modifier.height(40.dp),
                shape = SeaGlassShape.Capsule,
                role = role,
                onClick = onAction,
            ) { Text(actionLabel, Modifier.padding(horizontal = 14.dp), color = LocalContentColor.current, fontSize = 14.5.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
