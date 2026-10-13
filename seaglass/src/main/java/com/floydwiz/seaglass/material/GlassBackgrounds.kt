package com.floydwiz.seaglass.material

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private class BackgroundSpot(val cx: Float, val cy: Float, val rx: Float, val ry: Float, val color: Color)

private val LightSpots = listOf(
    BackgroundSpot(0.10f, 0.20f, 0.34f, 0.40f, rgba(111, 211, 247, 0.5f)),
    BackgroundSpot(0.90f, 0.24f, 0.30f, 0.40f, rgba(254, 179, 179, 0.45f)),
    BackgroundSpot(0.62f, 0.96f, 0.40f, 0.40f, rgba(241, 190, 109, 0.42f)),
    BackgroundSpot(0.34f, 0.78f, 0.36f, 0.40f, rgba(137, 214, 181, 0.45f)),
)
private val DarkSpots = listOf(
    BackgroundSpot(0.10f, 0.20f, 0.34f, 0.40f, rgba(45, 82, 209, 0.45f)),
    BackgroundSpot(0.90f, 0.24f, 0.30f, 0.40f, rgba(168, 126, 213, 0.36f)),
    BackgroundSpot(0.62f, 0.96f, 0.40f, 0.40f, rgba(2, 77, 98, 0.55f)),
    BackgroundSpot(0.34f, 0.78f, 0.36f, 0.40f, rgba(190, 134, 25, 0.2f)),
)

/**
 * The canvas backdrop behind every specimen (`BGL` / `BGD` in recipes.js): a base colour,
 * four soft colour pools and faint 2 dp horizontal lines every 16 dp.
 */
fun Modifier.glassBoardBackground(dark: Boolean): Modifier = drawWithCache {
    val spots = (if (dark) DarkSpots else LightSpots).map {
        radialLayer(it.cx, it.cy, it.rx, it.ry, listOf(0f to it.color, 0.7f to it.color.transparent), size.width, size.height)
    }
    val base = if (dark) Color(0xFF090C12) else Color(0xFFEEF2F8)
    val line = if (dark) rgba(227, 230, 238, 0.04f) else rgba(25, 28, 34, 0.05f)
    val lineHeight = 2.dp.toPx()
    val pitch = 16.dp.toPx()
    onDrawBehind {
        drawRect(base)
        spots.asReversed().forEach { drawRadial(it) }
        var y = size.height - lineHeight
        while (y > -lineHeight) {
            drawRect(line, topLeft = Offset(0f, y), size = Size(size.width, lineHeight))
            y -= pitch
        }
    }
}
