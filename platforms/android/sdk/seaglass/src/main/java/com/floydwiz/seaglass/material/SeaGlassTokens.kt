package com.floydwiz.seaglass.material

import kotlin.math.roundToInt
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.tokens.SeaGlassDarkScheme
import com.floydwiz.seaglass.tokens.SeaGlassLightScheme
import com.floydwiz.seaglass.tokens.SeaGlassOpacityTokens
import com.floydwiz.seaglass.tokens.SeaGlassRadiusTokens
import com.floydwiz.seaglass.tokens.SeaGlassSizeTokens
import com.floydwiz.seaglass.tokens.SeaGlassStateTokens
import com.floydwiz.seaglass.SeaGlassLabApi

/** Token tree from spec section 10. Per-finish numbers live in [GlassRecipes]. */
public object SeaGlassTokens {
    internal object Shape {
        val Pane24 = SeaGlassRadiusTokens.Pane24
        val Pane28 = SeaGlassRadiusTokens.Pane28
        val Pane32 = SeaGlassRadiusTokens.Pane32
        val Pane36 = SeaGlassRadiusTokens.Pane36
        val Min = SeaGlassRadiusTokens.Min
    }

    internal object State {
        object Hover {
            val Lift = SeaGlassStateTokens.HoverLift
            val ShadowDrop = 3.dp
            val ShadowScale = SeaGlassStateTokens.HoverShadowScale
            const val LightAlphaLight = 0.32f
            const val LightAlphaDark = 0.14f
            val SurfaceAlpha = SeaGlassOpacityTokens.StateHover
        }

        object Press {
            val Scale = SeaGlassStateTokens.PressScale
            val ShadowRise = 3.dp
            val ShadowScale = SeaGlassStateTokens.PressShadowScale
            const val BloomAlphaLight = 0.42f
            const val BloomAlphaDark = 0.22f
            val SurfaceAlpha = SeaGlassOpacityTokens.StatePress
        }

        object Focus {
            val Ring = SeaGlassSizeTokens.FocusRingWidth
            val Glow = SeaGlassStateTokens.FocusGlow
            val Bloom = SeaGlassStateTokens.FocusBloom
            val RingHighContrast = SeaGlassSizeTokens.FocusRingWidthHigh
        }

        object Disabled {
            val ContentAlpha = SeaGlassOpacityTokens.ContentDisabled
        }
    }

    internal object Rim {
        val Width = SeaGlassSizeTokens.RimWidth
    }

    internal object Well {
        val Lip = SeaGlassSizeTokens.WellLip
    }

    internal object Grain {
        val Tile = 200.dp
    }

    internal object Light {
        const val DriftSeconds = 8f
        const val DriftX = 0.05f
        const val DriftY = 0.03f
    }

    internal object Contrast {
        val TintBoost = floatArrayOf(0f, 0.15f, 0.32f)
        val TransmissionScale = floatArrayOf(1f, 0.6f, 0.3f)
        val LightScale = floatArrayOf(1f, 0.7f, 0.4f)
    }

    internal object Adapt {
        val Threshold = SeaGlassOpacityTokens.AdaptThreshold
    }

    internal object Fill {
        const val ToneLight = 45
        const val ToneDarkTop = 60
        const val ToneDarkBottom = 40
    }

    public object Content {
        public val OnSurfaceLight: Color = SeaGlassLightScheme.onSurface
        public val OnSurfaceDark: Color = SeaGlassDarkScheme.onSurface
        public val OnSurfaceVariantLight: Color = SeaGlassLightScheme.onSurfaceVariant
        public val OnSurfaceVariantDark: Color = SeaGlassDarkScheme.onSurfaceVariant
        public val OutlineLight: Color = SeaGlassLightScheme.outline
        public val OutlineDark: Color = SeaGlassDarkScheme.outline
    }

    /** Tints laid over the page behind a modal surface, and the track of a progress ring. */
    internal object Veil {
        val ContextMenuLight = Color(0x8CECF4F7)
        val ContextMenuDark = Color(0x80000000)
        val FabMenuLight = Color(0x73ECF4F7)
        val FabMenuDark = Color(0x61000000)
        val RingTrackLight = Color(0x1F162630)
        val RingTrackDark = Color(0x6B000000)
    }

    /** Solid fills used when transparency is reduced (`RTS` in recipes.js). */
    internal object ReduceTransparency {
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

    public object Motion {
        /** Multiplier on every duration, for slow-motion demonstrations: springs get their stiffness divided by its square. */
        @SeaGlassLabApi
        public var slowdown: Float by mutableFloatStateOf(1f)

        public val SpatialFast: SpringSpec<Float> get() = spatialFast()
        public val SpatialDefault: SpringSpec<Float> get() = spatialDefault()
        public val SpatialSlow: SpringSpec<Float> get() = spatialSlow()
        public val EffectsFast: SpringSpec<Float> get() = effectsFast()
        public val EffectsDefault: SpringSpec<Float> get() = effectsDefault()
        public val EffectsSlow: SpringSpec<Float> get() = effectsSlow()

        private fun stiffness(base: Float) = base / (slowdown * slowdown)

        public fun <T> spatialFast(): SpringSpec<T> = SpringSpec<T>(dampingRatio = 0.6f, stiffness = stiffness(800f))
        public fun <T> spatialDefault(): SpringSpec<T> = SpringSpec<T>(dampingRatio = 0.8f, stiffness = stiffness(380f))
        public fun <T> spatialSlow(): SpringSpec<T> = SpringSpec<T>(dampingRatio = 0.8f, stiffness = stiffness(200f))
        public fun <T> effectsFast(): SpringSpec<T> = SpringSpec<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness(3800f))
        public fun <T> effectsDefault(): SpringSpec<T> = SpringSpec<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness(1600f))
        public fun <T> effectsSlow(): SpringSpec<T> = SpringSpec<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = stiffness(800f))

        /** A timed duration stretched by [slowdown]. */
        public fun millis(base: Int): Int = (base * slowdown).roundToInt()
    }
}
