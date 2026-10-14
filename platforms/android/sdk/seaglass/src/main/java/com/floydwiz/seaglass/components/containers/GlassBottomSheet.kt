package com.floydwiz.seaglass.components.containers

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.tokens.SeaGlassRadiusTokens

/** Sheet pane: milk over a solid surface (a modal window has no backdrop to blur), inset 8 dp, radius 36, with a 40×6 well grabber. */
@Composable
public fun GlassSheetPane(
    modifier: Modifier = Modifier,
    role: TonalTones = LocalGlassRole.current,
    content: @Composable ColumnScope.() -> Unit,
) {
    GlassSurface(
        finish = GlassFinish.Milk,
        modifier = modifier.background(MaterialTheme.colorScheme.surface, SeaGlassShape.Pane(SeaGlassRadiusTokens.Pane36)),
        shape = SeaGlassShape.Pane(SeaGlassRadiusTokens.Pane36),
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.TopStart,
    ) {
        Column {
            Box(Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)) {
                GlassSurface(GlassFinish.Well, Modifier.size(40.dp, 6.dp), SeaGlassShape.Pane(3.dp), role, reactsTo = emptySet())
            }
            content()
        }
    }
}

/** Modal bottom sheet built on the Material scrim and drag handling, drawn as a [GlassSheetPane]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun GlassBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        dragHandle = null,
    ) {
        GlassSheetPane(Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(bottom = 8.dp).navigationBarsPadding(), content = content)
    }
}
