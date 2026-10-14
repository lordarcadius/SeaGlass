package com.floydwiz.seaglasssample.showcase.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoTile

private val BoardStatusBar = 44.dp

/** Places content where the boards put it: [y] dp from the top of a 412×915 frame whose status bar is 44 dp. */
fun Modifier.belowStatusBar(y: Dp): Modifier = statusBarsPadding().padding(top = (y - BoardStatusBar).coerceAtLeast(0.dp))

@Composable
fun Avatar(crop: Int, size: Dp, modifier: Modifier = Modifier) {
    PhotoTile(crop, modifier.size(size), CircleShape, zoom = 4.2f)
}

/** CSS `saturate(s) brightness(b)` as a colour matrix. */
fun saturateBrightness(saturation: Float, brightness: Float): ColorMatrix = ColorMatrix(
    floatArrayOf(
        brightness * (0.213f + 0.787f * saturation), brightness * (0.715f - 0.715f * saturation), brightness * (0.072f - 0.072f * saturation), 0f, 0f,
        brightness * (0.213f - 0.213f * saturation), brightness * (0.715f + 0.285f * saturation), brightness * (0.072f - 0.072f * saturation), 0f, 0f,
        brightness * (0.213f - 0.213f * saturation), brightness * (0.715f - 0.715f * saturation), brightness * (0.072f + 0.928f * saturation), 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    ),
)

/** Top bar with no container: a clear lens on each side and an optional centred title. */
@Composable
fun ScreenTopBar(
    leadingIcon: String,
    onLeading: () -> Unit,
    trailingIcon: String?,
    onTrailing: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Box(modifier.statusBarsPadding().fillMaxWidth().height(56.dp).padding(horizontal = 16.dp)) {
        GlassBarLens(leadingIcon, onLeading, Modifier.align(Alignment.CenterStart))
        if (title != null) Text(title, Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp, fontWeight = FontWeight(690))
        if (trailingIcon != null) GlassBarLens(trailingIcon, onTrailing, Modifier.align(Alignment.CenterEnd))
    }
}
