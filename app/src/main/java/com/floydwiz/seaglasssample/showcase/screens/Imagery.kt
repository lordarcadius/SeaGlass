package com.floydwiz.seaglasssample.showcase.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoTile
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private fun cssLinear(angleDegrees: Float, size: Size, vararg stops: Pair<Float, Color>): Brush {
    val radians = Math.toRadians(angleDegrees.toDouble())
    val dx = sin(radians).toFloat()
    val dy = -cos(radians).toFloat()
    val half = (abs(size.width * dx) + abs(size.height * dy)) / 2f
    val centre = Offset(size.width / 2f, size.height / 2f)
    return Brush.linearGradient(
        colorStops = stops,
        start = Offset(centre.x - dx * half, centre.y - dy * half),
        end = Offset(centre.x + dx * half, centre.y + dy * half),
    )
}

internal fun DrawScope.ellipse(cx: Float, cy: Float, rx: Float, ry: Float, vararg stops: Pair<Float, Color>) {
    val centre = Offset(size.width * cx, size.height * cy)
    val radius = size.width * rx
    val squash = size.height * ry / radius
    scale(1f, squash, pivot = centre) {
        drawRect(
            Brush.radialGradient(colorStops = stops, center = centre, radius = radius),
            topLeft = Offset(0f, centre.y - centre.y / squash),
            size = Size(size.width, size.height / squash),
        )
    }
}

private fun hex(value: Long) = Color(0xFF000000 or value)

/** Generated stand-ins for photos: the six gradients of the reference boards. */
@Composable
fun GradientImage(index: Int, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        when (index.mod(6)) {
            0 -> drawRect(cssLinear(180f, size, 0f to hex(0xF9D9B4), 0.42f to hex(0xF1A980), 0.78f to hex(0x8E7FB0), 1f to hex(0x4B5C8F)))
            1 -> ellipse(0.5f, 1.15f, 1.3f, 0.8f, 0f to hex(0x006783), 0.45f to hex(0x28B9E4), 1f to hex(0xDDF4FF))
            2 -> drawRect(cssLinear(160f, size, 0f to hex(0xB9FFDF), 0.58f to hex(0x5BBD96), 1f to hex(0x005138)))
            3 -> ellipse(0.3f, 0.2f, 0.9f, 0.7f, 0f to hex(0xFFEED6), 0.5f to hex(0xFBBB4C), 1f to hex(0x805600))
            4 -> drawRect(cssLinear(200f, size, 0f to hex(0xF1DBFF), 0.55f to hex(0xA87ED5), 1f to hex(0x2B0053)))
            else -> drawRect(cssLinear(180f, size, 0f to hex(0xB7EAFF), 0.48f to hex(0x8EDFFF), 0.49f to hex(0xFBBB4C), 1f to hex(0xDDA032)))
        }
    }
}

/** Picture slot: a photo crop for a non-negative [index], the (-index - 1)th gradient for a negative one. */
@Composable
fun Picture(index: Int, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(0.dp), dim: Boolean = false) {
    if (index < 0) Box(modifier.clip(shape)) { GradientImage(-index - 1, Modifier.fillMaxSize()) }
    else PhotoTile(index, modifier, shape, dim = dim)
}
