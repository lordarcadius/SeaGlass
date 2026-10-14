package com.floydwiz.seaglasssample.showcase

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.tokens.SeaGlassDarkExtendedColors
import com.floydwiz.seaglass.tokens.SeaGlassLightExtendedColors

private class Spot(val cx: Float, val cy: Float, val rx: Float, val ry: Float, val color: Color)

private fun rgba(r: Int, g: Int, b: Int, a: Float) = Color(r / 255f, g / 255f, b / 255f, a.coerceIn(0f, 1f))

private val LightSpots = listOf(
    Spot(0.10f, 0.20f, 0.34f, 0.40f, rgba(111, 211, 247, 0.5f)),
    Spot(0.90f, 0.24f, 0.30f, 0.40f, rgba(254, 179, 179, 0.45f)),
    Spot(0.62f, 0.96f, 0.40f, 0.40f, rgba(241, 190, 109, 0.42f)),
    Spot(0.34f, 0.78f, 0.36f, 0.40f, rgba(137, 214, 181, 0.45f)),
)
private val DarkSpots = listOf(
    Spot(0.10f, 0.20f, 0.34f, 0.40f, rgba(45, 82, 209, 0.45f)),
    Spot(0.90f, 0.24f, 0.30f, 0.40f, rgba(168, 126, 213, 0.36f)),
    Spot(0.62f, 0.96f, 0.40f, 0.40f, rgba(2, 77, 98, 0.55f)),
    Spot(0.34f, 0.78f, 0.36f, 0.40f, rgba(190, 134, 25, 0.2f)),
)

private class Pool(val center: Offset, val radius: Float, val squash: Float, val brush: Brush)

private fun pool(spot: Spot, width: Float, height: Float): Pool {
    val radius = spot.rx * width
    return Pool(
        center = Offset(spot.cx * width, spot.cy * height),
        radius = radius,
        squash = (spot.ry * height) / radius,
        brush = Brush.radialGradient(
            colorStops = arrayOf(0f to spot.color, 0.7f to spot.color.copy(alpha = 0f)),
            center = Offset.Zero,
            radius = radius,
        ),
    )
}

private fun DrawScope.drawPool(pool: Pool) {
    withTransform({
        translate(pool.center.x, pool.center.y)
        scale(1f, pool.squash, pivot = Offset.Zero)
    }) {
        drawCircle(pool.brush, radius = pool.radius, center = Offset.Zero)
    }
}

/**
 * The canvas backdrop behind every specimen (`BGL` / `BGD` in recipes.js): a base colour,
 * four soft colour pools and faint 2 dp horizontal lines every 16 dp.
 */
fun Modifier.glassBoardBackground(dark: Boolean): Modifier = drawWithCache {
    val pools = (if (dark) DarkSpots else LightSpots).map { pool(it, size.width, size.height) }
    val base = (if (dark) SeaGlassDarkExtendedColors else SeaGlassLightExtendedColors).backdrop
    val line = if (dark) rgba(227, 230, 238, 0.04f) else rgba(25, 28, 34, 0.05f)
    val lineHeight = 2.dp.toPx()
    val pitch = 16.dp.toPx()
    onDrawBehind {
        drawRect(base)
        pools.asReversed().forEach { drawPool(it) }
        var y = size.height - lineHeight
        while (y > -lineHeight) {
            drawRect(line, topLeft = Offset(0f, y), size = Size(size.width, lineHeight))
            y -= pitch
        }
    }
}
