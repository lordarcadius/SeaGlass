package com.floydwiz.seaglass.shape

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min

/**
 * Rounded rectangle with a separate elliptical radius per corner.
 * - [radii] resolves to px as tl, tr, br, bl, each (x, y), for a given size.
 * - Radii that overlap are scaled down uniformly, as in CSS `border-radius`.
 */
@Immutable
class SeaGlassShape private constructor(
    private val key: Any,
    private val radii: (width: Float, height: Float, density: Float) -> FloatArray,
) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Rounded(roundRect(size, density.density))

    internal fun roundRect(size: Size, density: Float): RoundRect {
        val r = radii(size.width, size.height, density)
        return RoundRect(
            left = 0f, top = 0f, right = size.width, bottom = size.height,
            topLeftCornerRadius = CornerRadius(r[0], r[1]),
            topRightCornerRadius = CornerRadius(r[2], r[3]),
            bottomRightCornerRadius = CornerRadius(r[4], r[5]),
            bottomLeftCornerRadius = CornerRadius(r[6], r[7]),
        )
    }

    override fun equals(other: Any?) = other is SeaGlassShape && other.key == key

    override fun hashCode() = key.hashCode()

    companion object {
        private fun percent(
            name: String,
            tlx: Float, trx: Float, brx: Float, blx: Float,
            tly: Float, try_: Float, bry: Float, bly: Float,
        ) = SeaGlassShape(name) { w, h, _ ->
            floatArrayOf(
                w * tlx, h * tly, w * trx, h * try_, w * brx, h * bry, w * blx, h * bly,
            )
        }

        /** Radius = min side / 2 with the ±4% hand-tumbled jitter per corner. */
        val Capsule = SeaGlassShape("capsule") { w, h, _ ->
            val r = min(w, h) / 2f
            floatArrayOf(
                r, r * 0.98f,
                r * 1.04f, r * 1.03f,
                r * 0.98f, r,
                r * 1.02f, r * 1.04f,
            )
        }

        /** Perfect circle for equal sides; used for round marks. */
        val Round = SeaGlassShape("round") { w, h, _ ->
            val r = min(w, h) / 2f
            floatArrayOf(r, r, r, r, r, r, r, r)
        }

        val Lens = percent("lens", 0.50f, 0.48f, 0.52f, 0.50f, 0.48f, 0.52f, 0.49f, 0.51f)
        val Pebble = percent("pebble", 0.42f, 0.38f, 0.40f, 0.45f, 0.40f, 0.44f, 0.38f, 0.43f)
        val Tumble = percent("tumble", 0.60f, 0.40f, 0.55f, 0.45f, 0.48f, 0.56f, 0.44f, 0.52f)
        val Shard = percent("shard", 0.24f, 0.64f, 0.38f, 0.58f, 0.58f, 0.30f, 0.66f, 0.36f)
        val Egg = percent("egg", 0.50f, 0.50f, 0.47f, 0.53f, 0.62f, 0.60f, 0.40f, 0.38f)

        /** Uniform rounded pane; 24, 28, 32 and 36 dp are the standard sizes. */
        fun Pane(radius: Dp) = SeaGlassShape("pane-${radius.value}") { _, _, density ->
            val r = radius.value * density
            floatArrayOf(r, r, r, r, r, r, r, r)
        }
    }
}
