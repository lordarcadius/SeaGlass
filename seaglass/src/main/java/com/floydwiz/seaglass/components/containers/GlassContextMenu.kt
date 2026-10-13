package com.floydwiz.seaglass.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/**
 * Context menu: the page is veiled, the pressed item is lifted as a [preview] and a [GlassMenu] sits 14 dp below it.
 * The veil is a dim tint because a dialog window cannot blur the window beneath it.
 */
@Composable
fun GlassContextMenu(
    onDismissRequest: () -> Unit,
    preview: @Composable () -> Unit,
    menu: @Composable ColumnScope.() -> Unit,
) {
    val dark = LocalSeaGlassDark.current
    Dialog(onDismissRequest, DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(
            Modifier
                .fillMaxSize()
                .background(if (dark) SeaGlassTokens.Veil.ContextMenuDark else SeaGlassTokens.Veil.ContextMenuLight)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismissRequest),
            contentAlignment = Alignment.Center,
        ) {
            Column(Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}, Arrangement.spacedBy(14.dp), Alignment.Start) {
                Box(Modifier.shadow(24.dp, SeaGlassShape.Pane(24.dp), clip = false)) { preview() }
                GlassMenu(content = menu)
            }
        }
    }
}
