package com.floydwiz.seaglass.components.containers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/**
 * Alert pane: milk, 320 dp wide, radius 34, a 52 dp glass icon lens, centred copy and full-width capsule buttons
 * (50 dp confirm in glass, 46 dp dismiss as plain).
 */
@Composable
fun GlassAlertPane(
    icon: String,
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    role: TonalTones = LocalRolePalettes.current[GlassRole.SeaPink],
) {
    val ink = if (LocalSeaGlassDark.current) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier.width(320.dp),
        shape = SeaGlassShape.Pane(34.dp),
        reactsTo = emptySet(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            GlassSurface(
                finish = GlassFinish.Glass,
                modifier = Modifier.size(52.dp),
                shape = SeaGlassShape.Lens,
                role = role,
                reactsTo = emptySet(),
            ) { SymbolIcon(icon, size = 26.dp, fill = true, tint = LocalContentColor.current) }
            Text(
                title, Modifier.padding(top = 16.dp), color = ink, fontSize = 20.sp, fontWeight = FontWeight(720), textAlign = TextAlign.Center,
            )
            Text(
                message, Modifier.padding(top = 8.dp, start = 6.dp, end = 6.dp), color = glassOnSurfaceVariant(),
                fontSize = 14.5.sp, fontWeight = FontWeight(450), lineHeight = 21.sp, textAlign = TextAlign.Center,
            )
            GlassButton(
                confirmLabel, onConfirm, Modifier.padding(top = 20.dp).fillMaxWidth().height(50.dp),
                size = GlassButtonSize.L, role = role,
            )
            GlassButton(
                dismissLabel, onDismiss, Modifier.padding(top = 6.dp).fillMaxWidth().height(46.dp),
                style = GlassButtonStyle.Plain, size = GlassButtonSize.L, role = LocalGlassRole.current,
            )
        }
    }
}

/** Modal alert in a dialog window over a scrim. */
@Composable
fun GlassAlert(
    onDismissRequest: () -> Unit,
    icon: String,
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String = "Cancel",
    modifier: Modifier = Modifier,
    role: TonalTones = LocalRolePalettes.current[GlassRole.SeaPink],
) {
    Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false)) {
        GlassAlertPane(icon, title, message, confirmLabel, onConfirm, dismissLabel, onDismissRequest, modifier, role)
    }
}
