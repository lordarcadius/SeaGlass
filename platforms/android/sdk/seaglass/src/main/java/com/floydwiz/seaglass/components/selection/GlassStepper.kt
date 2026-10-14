package com.floydwiz.seaglass.components.selection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/** Clear capsule 136×44 with two plain lenses; the pressed side condenses a lens that grows 10%. */
@Composable
fun GlassStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 0..99,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
) {
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    Box(modifier.size(136.dp, 44.dp)) {
        GlassSurface(GlassFinish.Clear, Modifier.size(136.dp, 44.dp), SeaGlassShape.Capsule, role, enabled = enabled, reactsTo = emptySet())
        Row(verticalAlignment = Alignment.CenterVertically) {
            StepperButton("remove", "Decrease", enabled && value > range.first, role, ink) { onValueChange(value - 1) }
            Text(
                value.toString(),
                Modifier.width(48.dp),
                color = if (enabled) ink else LocalContentColor.current.copy(alpha = SeaGlassTokens.State.Disabled.ContentAlpha),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            StepperButton("add", "Increase", enabled && value < range.last, role, ink) { onValueChange(value + 1) }
        }
    }
}

@Composable
private fun StepperButton(icon: String, description: String, enabled: Boolean, role: TonalTones, ink: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    GlassSurface(
        finish = GlassFinish.Plain,
        modifier = Modifier.size(44.dp),
        shape = SeaGlassShape.Lens,
        role = role,
        enabled = enabled,
        onClick = onClick,
        pressScale = 1.1f,
        contentColor = ink,
    ) {
        SymbolIcon(icon, size = 22.dp, tint = LocalContentColor.current, contentDescription = description)
    }
}
