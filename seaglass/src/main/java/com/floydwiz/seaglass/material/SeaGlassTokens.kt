package com.floydwiz.seaglass.material

import kotlin.math.roundToInt
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Token tree from spec section 10. Per-finish numbers live in [GlassRecipes]. */
object SeaGlassTokens {
    object Shape {
        val Pane24 = 24.dp
        val Pane28 = 28.dp
        val Pane32 = 32.dp
        val Pane36 = 36.dp
        val Min = 12.dp
    }

    object State {
        object Hover {
            val Lift = 2.dp
            val ShadowDrop = 3.dp
            const val ShadowScale = 1.05f
            const val LightAlphaLight = 0.32f
            const val LightAlphaDark = 0.14f
            const val SurfaceAlpha = 0.07f
        }

        object Press {
            const val Scale = 0.96f
            val ShadowRise = 3.dp
            const val ShadowScale = 0.9f
            const val BloomAlphaLight = 0.42f
            const val BloomAlphaDark = 0.22f
            const val SurfaceAlpha = 0.12f
        }

        object Focus {
            val Ring = 1.5.dp
            val Glow = 14.dp
            val Bloom = 34.dp
            val RingHighContrast = 3.dp
        }

        object Disabled {
            const val ContentAlpha = 0.38f
        }
    }

    object Rim {
        val Width = 1.5.dp
    }

    object Well {
        val Lip = 1.5.dp
    }

    object Grain {
        val Tile = 200.dp
    }

    object Light {
        const val DriftSeconds = 8f
        const val DriftX = 0.05f
        const val DriftY = 0.03f
    }

    object Contrast {
        val TintBoost = floatArrayOf(0f, 0.15f, 0.32f)
        val TransmissionScale = floatArrayOf(1f, 0.6f, 0.3f)
        val LightScale = floatArrayOf(1f, 0.7f, 0.4f)
    }

    object Adapt {
        const val Threshold = 0.4f
    }

    object Fill {
        const val ToneLight = 45
        const val ToneDarkTop = 60
        const val ToneDarkBottom = 40
    }

    object Content {
        val OnSurfaceLight = Color(0xFF171C1F)
        val OnSurfaceDark = Color(0xFFDFE3E6)
        val OnSurfaceVariantLight = Color(0xFF40484C)
        val OnSurfaceVariantDark = Color(0xFFC0C8CC)
        val OutlineLight = Color(0xFF70787D)
        val OutlineDark = Color(0xFF8A9296)
    }

    /** Solid fills used when transparency is reduced (`RTS` in recipes.js). */
    object ReduceTransparency {
        class Surfaces(val milk: Color, val well: Color, val clear: Color, val pearl: Color, val ink: Color, val dry: Color)

        val Light = Surfaces(
            milk = Color(0xFFFFFFFF), well = Color(0xFFDFE3E6), clear = Color(0xFFFFFFFF),
            pearl = Color(0xFFFFFFFF), ink = Color(0xFF2C3134), dry = Color(0xFFE4E9EC),
        )
        val Dark = Surfaces(
            milk = Color(0xFF262B2D), well = Color(0xFF313537), clear = Color(0xFF353A3D),
            pearl = Color(0xFFDFE3E6), ink = Color(0xFFDFE3E6), dry = Color(0xFF1B2023),
        )
    }

    object Motion {
        /** Multiplier on every duration, for slow-motion demonstrations: springs get their stiffness divided by its square. */
        var slowdown by mutableFloatStateOf(1f)

        val SpatialFast: SpringSpec<Float> get() = spatialFast()
        val SpatialDefault: SpringSpec<Float> get() = spatialDefault()
        val SpatialSlow: SpringSpec<Float> get() = spatialSlow()
        val EffectsFast: SpringSpec<Float> get() = effectsFast()
        val EffectsDefault: SpringSpec<Float> get() = effectsDefault()
        val EffectsSlow: SpringSpec<Float> get() = effectsSlow()

        private fun stiffness(base: Float) = base / (slowdown * slowdown)

        fun <T> spatialFast() = SpringSpec<T>(dampingRatio = 0.6f, stiffness = stiffness(800f))
        fun <T> spatialDefault() = SpringSpec<T>(dampingRatio = 0.8f, stiffness = stiffness(380f))
        fun <T> spatialSlow() = SpringSpec<T>(dampingRatio = 0.8f, stiffness = stiffness(200f))
        fun <T> effectsFast() = SpringSpec<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness(3800f))
        fun <T> effectsDefault() = SpringSpec<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness(1600f))
        fun <T> effectsSlow() = SpringSpec<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness(800f))

        /** A timed duration stretched by [slowdown]. */
        fun millis(base: Int): Int = (base * slowdown).roundToInt()
    }
}
