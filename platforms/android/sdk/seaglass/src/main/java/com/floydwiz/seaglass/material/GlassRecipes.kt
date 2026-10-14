package com.floydwiz.seaglass.material

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.theme.SeaGlassContrast
import kotlin.math.max
import kotlin.math.min
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt
import com.floydwiz.seaglass.tokens.SeaGlassBlurTokens
import com.floydwiz.seaglass.tokens.SeaGlassOpacityTokens

/** Inputs shared by every finish; mirrors the `o` argument of `mat()` in recipes.js. */
@Immutable
internal class GlassContext(
    val dark: Boolean,
    val contrast: SeaGlassContrast,
    val reduceTransparency: Boolean,
    val role: TonalTones,
    val adaptive: Float,
    val tinted: Boolean,
    val overrides: GlassOverrides = GlassOverrides.None,
) {
    val level get() = contrast.ordinal
    val tintBoost get() = SeaGlassTokens.Contrast.TintBoost[level] + overrides.tintDelta
    val transmission get() = SeaGlassTokens.Contrast.TransmissionScale[level]
    val lightScale get() = SeaGlassTokens.Contrast.LightScale[level] * overrides.lightScale

    /** Transmission glow colour at the bottom of lit glass. */
    val glow: Color
        get() = if (dark) mix(role[80], Color(0xFFDFFFFF), 0.25f) else mix(role[95], Color(0xFFEAFFFF), 0.55f)

    val outline get() = if (dark) SeaGlassTokens.Content.OutlineDark else SeaGlassTokens.Content.OutlineLight
    val surfaces get() = if (dark) SeaGlassTokens.ReduceTransparency.Dark else SeaGlassTokens.ReduceTransparency.Light
}

internal sealed interface Body {
    /** Backdrop blur, then saturation and brightness, then [fills] from bottom to top. */
    class Backdrop(val blur: Float, val saturation: Float, val brightness: Float, val fills: List<Brush>) : Body

    class Solid(val color: Color) : Body

    /** Elliptical radial gradient fill without blur; centre and radii are fractions of the size. */
    class Radial(val cx: Float, val cy: Float, val rx: Float, val ry: Float, val stops: List<Pair<Float, Color>>) : Body

    data object None : Body
}

/** CSS pixel values in recipes.js are rounded to 0.1 (`r1()`/`px()`); mirror that so numbers match. */
private fun round1(v: Float) = (v * 10f).roundToInt() / 10f

internal class ShadowSpec(blur: Float, offsetY: Float, val scaleX: Float, val scaleY: Float, val color: Color) {
    val blur = round1(blur)
    val offsetY = round1(offsetY)
}

/** Soft ellipse below the element; [insetFraction] is the horizontal inset as a fraction of the width. */
internal class OvalSpec(
    val insetFraction: Float,
    bottom: Float,
    height: Float,
    val color: Color,
    val blur: Float,
    val fades: Boolean,
) {
    val bottom = round1(bottom)
    val height = round1(height)
}

/** Elliptical radial gradient; centre and radii are fractions of the element size. */
internal class RadialSpec(
    val cx: Float,
    val cy: Float,
    val rx: Float,
    val ry: Float,
    val color: Color,
    val endStop: Float = 1f,
)

/** One CSS inset box-shadow: offset, blur radius, spread, colour, all in dp. */
internal class InsetSpec(x: Float, y: Float, blur: Float, spread: Float, val color: Color) {
    val x = round1(x)
    val y = round1(y)
    val blur = round1(blur)
    val spread = round1(spread)
}

internal class RimSpec(val angle: Float, val stops: List<Pair<Float, Color>>)

internal class LayerSpec(
    val shadow: ShadowSpec?,
    val caustic: OvalSpec?,
    val contact: OvalSpec?,
    val light: List<RadialSpec>,
    val grain: Float,
    val insets: List<InsetSpec>,
    val rim: RimSpec?,
    val driftOffsetSeconds: Float,
)

internal object GlassRecipes {

    private fun lg(a: Color, b: Color, c: Color): Brush =
        Brush.verticalGradient(0f to a, 0.52f to b, 1f to c)

    private fun solidRing(color: Color) = RimSpec(180f, listOf(0f to color, 1f to color))

    private fun r1(v: Float) = round1(v)

    fun body(finish: GlassFinish, c: GlassContext): Body {
        val body = baseBody(finish, c)
        val blur = c.overrides.blurDp
        return if (blur != null && body is Body.Backdrop) Body.Backdrop(blur, body.saturation, body.brightness, body.fills) else body
    }

    private fun baseBody(finish: GlassFinish, c: GlassContext): Body {
        if (c.reduceTransparency) return Body.Solid(reduceTransparencyFill(finish, c))
        val p = c.role
        val dark = c.dark
        var ta = c.tintBoost
        return when (finish) {
            GlassFinish.Glass -> {
                if (c.adaptive > 0f && dark) ta = maxOf(ta, 0.3f * c.adaptive)
                if (dark) {
                    Body.Backdrop(
                        SeaGlassBlurTokens.GlassDark.value, 1.8f, lerp(1.25f, 0.9f, c.adaptive),
                        listOf(lg(p[40].alpha(0.52f + ta), p[30].alpha(0.38f + ta), p[30].alpha(0.3f + ta))),
                    )
                } else {
                    Body.Backdrop(
                        SeaGlassBlurTokens.Glass.value, 1.7f, 1.04f,
                        listOf(
                            lg(
                                p[80].alpha(0.66f + ta),
                                mix(p[80], p[90], 0.5f).alpha(0.5f + ta),
                                p[90].alpha(0.4f + ta),
                            ),
                        ),
                    )
                }
            }

            GlassFinish.Frost -> {
                if (c.adaptive > 0f && dark) ta = maxOf(ta, 0.3f * c.adaptive)
                if (dark) {
                    Body.Backdrop(
                        SeaGlassBlurTokens.Frost.value, 1.6f, lerp(1.15f, 0.9f, c.adaptive),
                        listOf(lg(p[40].alpha(0.46f + ta), p[30].alpha(0.36f + ta), p[30].alpha(0.32f + ta))),
                    )
                } else {
                    Body.Backdrop(
                        SeaGlassBlurTokens.Frost.value, 1.5f, 1.03f,
                        listOf(
                            lg(
                                mix(p[80], p[90], 0.6f).alpha(0.6f + ta),
                                p[90].alpha(0.5f + ta),
                                p[90].alpha(0.44f + ta),
                            ),
                        ),
                    )
                }
            }

            GlassFinish.Clear, GlassFinish.Lens -> if (dark) {
                val bo = if (finish == GlassFinish.Lens) 1f else 0f
                var smoke = when (c.level) {
                    2 -> 0.86f
                    1 -> 0.6f
                    else -> 0f
                }
                smoke = maxOf(smoke, SeaGlassOpacityTokens.AdaptTintDark * c.adaptive)
                if (smoke > 0f) {
                    Body.Backdrop(
                        SeaGlassBlurTokens.Clear.value, 1.5f, 1f,
                        listOf(lg(rgba(44, 49, 52, smoke), rgba(36, 41, 44, smoke), rgba(32, 37, 40, smoke))),
                    )
                } else {
                    Body.Backdrop(
                        SeaGlassBlurTokens.Clear.value, 2f, 1.55f + bo * 0.1f,
                        listOf(lg(white(0.2f + bo * 0.1f), white(0.1f + bo * 0.08f), white(0.12f + bo * 0.08f))),
                    )
                }
            } else {
                val bo = if (finish == GlassFinish.Lens) 1f else 0f
                Body.Backdrop(
                    SeaGlassBlurTokens.Clear.value, 1.8f, 1.06f + bo * 0.04f,
                    listOf(
                        lg(
                            white(0.6f + ta + bo * 0.12f),
                            rgba(250, 253, 255, 0.4f + ta + bo * 0.1f),
                            rgba(236, 248, 255, 0.32f + ta + bo * 0.1f),
                        ),
                    ),
                )
            }

            GlassFinish.Pearl -> if (dark) {
                if (c.level == 2) {
                    Body.Solid(Color(0xFF2C3134))
                } else {
                    Body.Backdrop(
                        SeaGlassBlurTokens.Pearl.value, 1.4f, 1f,
                        listOf(lg(rgba(242, 246, 248, 0.97f), rgba(224, 232, 236, 0.96f), rgba(206, 216, 221, 0.96f))),
                    )
                }
            } else {
                Body.Backdrop(
                    SeaGlassBlurTokens.Pearl.value, 1.4f, 1.02f,
                    listOf(lg(Color.White, rgba(252, 254, 255, 0.98f), rgba(240, 247, 250, 0.97f))),
                )
            }

            GlassFinish.Fill -> when {
                c.level == 2 -> Body.Solid(if (dark) p[80] else p[40])
                dark -> Body.Backdrop(
                    SeaGlassBlurTokens.Fill.value, 1.7f, 1.15f,
                    listOf(lg(p[60].alpha(0.92f), mix(p[60], p[40], 0.4f).alpha(0.86f), p[40].alpha(0.82f))),
                )
                else -> Body.Backdrop(
                    SeaGlassBlurTokens.Fill.value, 1.7f, 1f,
                    listOf(
                        lg(
                            mix(p[40], p[60], 0.42f).alpha(0.96f),
                            mix(p[40], p[60], 0.3f).alpha(0.95f),
                            mix(p[40], p[60], 0.18f).alpha(0.95f),
                        ),
                    ),
                )
            }

            GlassFinish.Ink -> if (dark) {
                Body.Backdrop(
                    SeaGlassBlurTokens.Ink.value, 1.5f, 1.02f,
                    listOf(lg(rgba(240, 244, 246, 0.95f), rgba(229, 235, 238, 0.93f), rgba(221, 228, 231, 0.93f))),
                )
            } else {
                Body.Backdrop(
                    SeaGlassBlurTokens.Ink.value, 1.5f, 1f,
                    listOf(lg(rgba(42, 48, 52, 0.92f), rgba(33, 38, 41, 0.9f), rgba(29, 34, 37, 0.9f))),
                )
            }

            GlassFinish.Dry -> if (dark) {
                Body.Backdrop(
                    SeaGlassBlurTokens.Dry.value, 0.3f, 1f,
                    listOf(lg(rgba(84, 90, 94, 0.4f), rgba(74, 80, 84, 0.34f), rgba(68, 74, 78, 0.32f))),
                )
            } else {
                Body.Backdrop(
                    SeaGlassBlurTokens.Dry.value, 0.3f, 1.02f,
                    listOf(lg(rgba(226, 231, 234, 0.74f), rgba(219, 225, 228, 0.66f), rgba(213, 220, 223, 0.64f))),
                )
            }

            GlassFinish.Bead -> Body.Radial(
                0.3f, 0.1f, 1.2f, 1.2f,
                if (dark) {
                    listOf(0f to p[80], 1f to p[70])
                } else {
                    listOf(0f to mix(p[40], p[60], 0.25f), 0.6f to p[40], 1f to mix(p[40], p[30], 0.5f))
                },
            )

            GlassFinish.Plain -> Body.None

            GlassFinish.Well -> {
                val base = if (dark) {
                    Brush.verticalGradient(
                        0f to rgba(0, 0, 0, 0.4f + ta * 0.4f),
                        0.62f to rgba(0, 0, 0, 0.28f + ta * 0.4f),
                        1f to white(0.05f),
                    )
                } else {
                    Brush.verticalGradient(
                        0f to rgba(22, 38, 48, 0.12f + ta * 0.3f),
                        0.62f to rgba(22, 38, 48, 0.06f + ta * 0.3f),
                        1f to white(0.2f),
                    )
                }
                val tint = when {
                    !c.tinted -> null
                    dark -> Brush.verticalGradient(listOf(p[40].alpha(0.32f), p[40].alpha(0.2f)))
                    else -> Brush.verticalGradient(listOf(p[80].alpha(0.28f), p[90].alpha(0.18f)))
                }
                Body.Backdrop(
                    SeaGlassBlurTokens.Well.value, 1.3f, if (dark) 0.9f else 1.02f,
                    listOfNotNull(base, tint),
                )
            }

            GlassFinish.Milk -> if (dark) {
                Body.Backdrop(
                    SeaGlassBlurTokens.Milk.value, 1.5f, 1f,
                    listOf(lg(rgba(48, 54, 58, 0.9f), rgba(38, 43, 46, 0.88f), rgba(33, 38, 41, 0.88f))),
                )
            } else {
                Body.Backdrop(
                    SeaGlassBlurTokens.Milk.value, 1.6f, 1.03f,
                    listOf(lg(white(0.9f), rgba(250, 253, 254, 0.86f), rgba(244, 249, 251, 0.86f))),
                )
            }
        }
    }

    private fun reduceTransparencyFill(finish: GlassFinish, c: GlassContext): Color {
        val p = c.role
        val s = c.surfaces
        return when (finish) {
            GlassFinish.Glass -> if (c.dark) mix(p[30], p[40], 0.4f) else mix(p[80], p[90], 0.5f)
            GlassFinish.Frost -> if (c.dark) mix(p[20], p[30], 0.5f) else p[90]
            GlassFinish.Well ->
                if (c.tinted) {
                    if (c.dark) mix(s.well, p[30], 0.5f) else mix(s.well, p[90], 0.6f)
                } else {
                    s.well
                }
            GlassFinish.Clear, GlassFinish.Lens -> s.clear
            GlassFinish.Milk -> s.milk
            GlassFinish.Fill, GlassFinish.Bead -> if (c.dark) p[80] else p[40]
            GlassFinish.Pearl -> if (c.dark) Color(0xFF2C3134) else s.pearl
            GlassFinish.Ink -> s.ink
            GlassFinish.Dry -> s.dry
            GlassFinish.Plain -> Color.Transparent
        }
    }

    fun layers(finish: GlassFinish, c: GlassContext, w: Float, h: Float): LayerSpec {
        val spec = baseLayers(finish, c, w, h)
        val o = c.overrides
        if (o.grain == null && o.rimScale == 1f && o.shadowScale == 1f) return spec
        fun Color.scaled(k: Float) = copy(alpha = (alpha * k).coerceIn(0f, 1f))
        fun OvalSpec.scaled(k: Float) = OvalSpec(insetFraction, bottom, height, color.scaled(k), blur, fades)
        return LayerSpec(
            shadow = spec.shadow?.let { ShadowSpec(it.blur, it.offsetY, it.scaleX, it.scaleY, it.color.scaled(o.shadowScale)) },
            caustic = spec.caustic?.scaled(o.shadowScale),
            contact = spec.contact?.scaled(o.shadowScale),
            light = spec.light,
            grain = o.grain ?: spec.grain,
            insets = spec.insets,
            rim = spec.rim?.let { rim -> RimSpec(rim.angle, rim.stops.map { (stop, color) -> stop to color.scaled(o.rimScale) }) },
            driftOffsetSeconds = spec.driftOffsetSeconds,
        )
    }

    private fun baseLayers(finish: GlassFinish, c: GlassContext, w: Float, h: Float): LayerSpec {
        val drift = ((w * 3f + h) * 1.7f) % 8f
        return if (c.reduceTransparency) reduceTransparencyLayers(finish, c, drift) else litLayers(finish, c, w, h, drift)
    }

    private fun reduceTransparencyLayers(finish: GlassFinish, c: GlassContext, drift: Float): LayerSpec {
        val dark = c.dark
        val base = if (finish == GlassFinish.Lens) GlassFinish.Clear else finish
        if (base == GlassFinish.Plain) return LayerSpec(null, null, null, emptyList(), 0f, emptyList(), null, drift)
        val line: Color? = when (base) {
            GlassFinish.Clear -> c.outline.alpha(0.75f)
            GlassFinish.Pearl, GlassFinish.Well -> c.outline
            GlassFinish.Milk -> if (dark) white(0.08f) else rgba(23, 28, 31, 0.08f)
            else -> null
        }
        val insets = buildList {
            if (base != GlassFinish.Well && base != GlassFinish.Dry) add(InsetSpec(0f, 1f, 0f, 0f, white(if (dark) 0.08f else 0.5f)))
            if (line != null) add(InsetSpec(0f, 0f, 0f, 1f, line))
        }
        val milk = base == GlassFinish.Milk
        val shadow = if (base == GlassFinish.Well || base == GlassFinish.Dry || base == GlassFinish.Fill) {
            null
        } else {
            ShadowSpec(
                blur = if (milk) 18f else 5f,
                offsetY = if (milk) 8f else 2f,
                scaleX = 0.98f, scaleY = 0.98f,
                color = if (dark) rgba(0, 0, 0, 0.5f) else rgba(20, 30, 40, if (milk) 0.12f else 0.2f),
            )
        }
        return LayerSpec(shadow, null, null, emptyList(), 0f, insets, null, drift)
    }

    private fun litLayers(finish: GlassFinish, c: GlassContext, w: Float, h: Float, drift: Float): LayerSpec {
        val p = c.role
        val dark = c.dark
        val lvl = c.level
        val gs = c.transmission
        val ls = c.lightScale
        val g = c.glow
        val hs = min(h, 120f)
        val hz = min(h, 360f)
        fun grain(a: Float) = if (lvl == 0) a else 0f

        return when (finish) {
            GlassFinish.Glass -> LayerSpec(
                shadow = if (dark) {
                    ShadowSpec(hs * 0.24f + 5f, hs * 0.08f, 0.96f, 0.9f, p[60].alpha(0.5f))
                } else {
                    ShadowSpec(hs * 0.16f + 5f, hs * 0.24f, 0.9f, 0.86f, p[60].alpha(0.55f))
                },
                caustic = if (dark) {
                    OvalSpec(0.26f, hs * 0.14f, hs * 0.17f, p[80].alpha(0.32f * gs), 5f, true)
                } else {
                    OvalSpec(0.24f, hs * 0.14f, hs * 0.17f, mix(p[80], Color.White, 0.3f).alpha(0.55f * gs), 4f, true)
                },
                contact = if (dark) {
                    OvalSpec(0.14f, 2f, min(8f, (hs * 0.2f).roundToInt() + 2f), rgba(0, 0, 0, 0.5f), 3f, false)
                } else {
                    OvalSpec(0.12f, 2f, min(8f, (hs * 0.2f).roundToInt() + 2f), rgba(20, 24, 34, 0.22f), 3f, false)
                },
                light = if (dark) {
                    listOf(
                        RadialSpec(0.50f, 0.68f, 0.62f, 0.58f, p[70].alpha(0.4f * ls)),
                        RadialSpec(0.28f, 0.06f, 0.55f, 0.45f, white(0.2f * ls)),
                    )
                } else {
                    listOf(
                        RadialSpec(0.26f, 0.04f, 0.68f, 0.62f, white(0.62f * ls)),
                        RadialSpec(0.72f, 1.00f, 0.52f, 0.42f, g.alpha(0.55f * ls)),
                    )
                },
                grain = grain(if (dark) 0.12f else SeaGlassOpacityTokens.GrainGlass),
                insets = if (dark) {
                    listOf(
                        InsetSpec(0f, -hs * 0.2f, hs * 0.28f, -hs * 0.1f, g.alpha(0.42f * gs)),
                        InsetSpec(0f, hs * 0.12f, hs * 0.2f, -hs * 0.1f, white(0.14f)),
                        InsetSpec(0f, 0f, hs * 0.22f, 0f, rgba(0, 0, 0, 0.32f)),
                    )
                } else {
                    listOf(
                        InsetSpec(0f, -hs * 0.2f, hs * 0.28f, -hs * 0.1f, g.alpha(0.75f * gs)),
                        InsetSpec(0f, hs * 0.14f, hs * 0.22f, -hs * 0.12f, p[40].alpha(0.2f)),
                        InsetSpec(0f, 0f, hs * 0.18f, 0f, p[60].alpha(0.2f)),
                    )
                },
                rim = RimSpec(
                    180f,
                    listOf(
                        0f to white(if (dark) 0.6f else 0.95f),
                        0.34f to white(if (dark) 0.08f else 0.25f),
                        0.66f to (if (dark) p[70] else p[60]).alpha(if (dark) 0.22f else 0.16f),
                        1f to g.alpha((if (dark) 0.6f else 0.85f) * gs),
                    ),
                ),
                driftOffsetSeconds = drift,
            )

            GlassFinish.Frost -> LayerSpec(
                shadow = ShadowSpec(hs * 0.14f + 4f, hs * 0.14f, 0.92f, 0.88f, p[60].alpha(if (dark) 0.42f else 0.32f)),
                caustic = null,
                contact = null,
                light = listOf(RadialSpec(0.30f, 0f, 0.70f, 0.60f, white((if (dark) 0.16f else 0.5f) * ls))),
                grain = grain(if (dark) 0.1f else SeaGlassOpacityTokens.GrainFrost),
                insets = listOf(
                    InsetSpec(0f, -hs * 0.16f, hs * 0.24f, -hs * 0.1f, g.alpha((if (dark) 0.3f else 0.6f) * gs)),
                ),
                rim = RimSpec(
                    180f,
                    listOf(
                        0f to white(if (dark) 0.38f else 0.85f),
                        0.45f to white(if (dark) 0.04f else 0.15f),
                        1f to g.alpha(if (dark) 0.2f else 0.4f),
                    ),
                ),
                driftOffsetSeconds = drift,
            )

            GlassFinish.Clear, GlassFinish.Lens -> if (dark) {
                LayerSpec(
                    shadow = ShadowSpec(hs * 0.2f + 5f, hs * 0.12f, 0.94f, 0.88f, rgba(0, 0, 0, 0.5f)),
                    caustic = null,
                    contact = OvalSpec(0.14f, 2f, min(8f, (hs * 0.2f).roundToInt() + 2f), rgba(0, 0, 0, 0.5f), 3f, false),
                    light = listOf(
                        RadialSpec(0.50f, -0.20f, 1.20f, 0.70f, white(0.18f * ls), endStop = 0.6f),
                        RadialSpec(0.28f, 0.12f, 0.40f, 0.30f, white(0.22f * ls)),
                        RadialSpec(0.50f, 1.25f, 0.70f, 0.60f, rgba(140, 210, 255, r1(0.22f * ls * 100f) / 100f)),
                    ),
                    grain = grain(0.05f),
                    insets = listOf(
                        InsetSpec(0f, 1f, 0.5f, 0f, white(0.45f)),
                        InsetSpec(0f, -1f, 0.5f, 0f, rgba(170, 220, 255, r1(0.3f * gs * 100f) / 100f)),
                        InsetSpec(0f, 0f, hs * 0.28f, 0f, rgba(0, 0, 0, 0.22f)),
                        InsetSpec(0f, -hs * 0.18f, hs * 0.25f, -hs * 0.12f, rgba(150, 215, 255, r1(0.12f * gs * 100f) / 100f)),
                    ),
                    rim = if (lvl == 2) {
                        solidRing(c.outline)
                    } else {
                        RimSpec(
                            135f,
                            listOf(
                                0f to white(0.95f),
                                0.16f to white(0.3f),
                                0.38f to white(0f),
                                0.64f to white(0f),
                                0.82f to white(0.18f),
                                1f to rgba(190, 232, 255, r1(0.8f * gs * 100f) / 100f),
                            ),
                        )
                    },
                    driftOffsetSeconds = drift,
                )
            } else {
                LayerSpec(
                    shadow = ShadowSpec(hs * 0.16f + 5f, hs * 0.22f, 0.9f, 0.86f, rgba(40, 70, 90, 0.28f)),
                    caustic = OvalSpec(0.24f, hs * 0.14f, hs * 0.17f, white(0.7f * gs), 4f, true),
                    contact = OvalSpec(0.12f, 2f, min(8f, (hs * 0.2f).roundToInt() + 2f), rgba(20, 24, 34, 0.16f), 3f, false),
                    light = listOf(
                        RadialSpec(0.26f, 0f, 0.70f, 0.60f, white(0.85f * ls)),
                        RadialSpec(0.74f, 1f, 0.56f, 0.46f, rgba(236, 252, 255, r1(0.7f * ls * 100f) / 100f)),
                    ),
                    grain = grain(SeaGlassOpacityTokens.GrainClear),
                    insets = listOf(
                        InsetSpec(0f, -hs * 0.2f, hs * 0.28f, -hs * 0.1f, rgba(228, 250, 255, r1(0.9f * gs * 100f) / 100f)),
                        InsetSpec(0f, hs * 0.14f, hs * 0.22f, -hs * 0.12f, rgba(40, 70, 90, 0.1f)),
                        InsetSpec(0f, 0f, hs * 0.18f, 0f, rgba(40, 70, 90, 0.1f)),
                    ),
                    rim = if (lvl == 2) {
                        solidRing(c.outline)
                    } else {
                        RimSpec(
                            180f,
                            listOf(
                                0f to white(1f),
                                0.34f to white(0.35f),
                                0.66f to rgba(80, 105, 120, 0.16f),
                                1f to rgba(236, 252, 255, 0.95f),
                            ),
                        )
                    },
                    driftOffsetSeconds = drift,
                )
            }

            GlassFinish.Well -> LayerSpec(
                shadow = null,
                caustic = null,
                contact = null,
                light = emptyList(),
                grain = grain(if (dark) 0.08f else 0.12f),
                insets = if (dark) {
                    listOf(
                        InsetSpec(0f, hs * 0.05f + 1.5f, hs * 0.12f + 3f, 0f, rgba(0, 0, 0, 0.6f)),
                        InsetSpec(0f, -1.5f, 0f, 0f, white(0.46f)),
                        InsetSpec(0f, 0f, 0f, 1f, white(0.05f)),
                    )
                } else {
                    listOf(
                        InsetSpec(0f, 1.5f, 0f, 0f, rgba(16, 32, 42, 0.42f)),
                        InsetSpec(0f, hs * 0.05f + 1.5f, hs * 0.12f + 3f, 0f, rgba(16, 32, 42, 0.2f)),
                        InsetSpec(0f, -1f, 0f, 0f, white(0.9f)),
                        InsetSpec(0f, 0f, 0f, 1f, rgba(16, 32, 42, 0.06f)),
                    )
                },
                rim = if (lvl > 0) solidRing(c.outline.alpha(if (lvl == 2) 1f else 0.55f)) else null,
                driftOffsetSeconds = drift,
            )

            GlassFinish.Milk -> {
                val edge = max(4f, r1(1600f / h)) / 100f
                LayerSpec(
                    shadow = if (dark) {
                        ShadowSpec(hz * 0.08f + 12f, hz * 0.04f + 8f, 0.97f, 0.97f, rgba(0, 0, 0, 0.55f))
                    } else {
                        ShadowSpec(hz * 0.08f + 12f, hz * 0.04f + 8f, 0.97f, 0.97f, rgba(30, 55, 75, 0.2f))
                    },
                    caustic = null,
                    contact = null,
                    light = emptyList(),
                    grain = grain(if (dark) 0.05f else SeaGlassOpacityTokens.GrainMilk),
                    insets = listOf(
                        InsetSpec(
                            0f, 0f, 0f, 0.5f,
                            if (dark) white(0.05f) else rgba(30, 50, 60, 0.06f),
                        ),
                    ),
                    rim = if (lvl == 2) {
                        solidRing(c.outline.alpha(0.7f))
                    } else if (dark) {
                        RimSpec(
                            180f,
                            monotonic(
                                0f to white(0.3f), edge to white(0.06f), 0.4f to white(0f), 1f to white(0.04f),
                            ),
                        )
                    } else {
                        RimSpec(
                            180f,
                            monotonic(
                                0f to white(1f), edge to white(0.4f), 0.4f to white(0f),
                                0.8f to white(0f), 1f to rgba(200, 218, 226, 0.45f),
                            ),
                        )
                    },
                    driftOffsetSeconds = drift,
                )
            }
            GlassFinish.Pearl -> LayerSpec(
                shadow = if (dark) {
                    ShadowSpec(hs * 0.16f + 3f, hs * 0.12f + 1f, 0.92f, 0.9f, rgba(0, 0, 0, 0.6f))
                } else {
                    ShadowSpec(hs * 0.14f + 3f, hs * 0.12f + 1f, 0.92f, 0.9f, rgba(20, 40, 55, 0.3f))
                },
                caustic = null,
                contact = OvalSpec(
                    0.16f, 2f, min(8f, (hs * 0.2f).roundToInt() + 2f),
                    if (dark) rgba(0, 0, 0, 0.45f) else rgba(20, 24, 34, 0.18f), 3f, false,
                ),
                light = emptyList(),
                grain = 0f,
                insets = if (lvl == 2 && dark) {
                    listOf(InsetSpec(0f, 0f, 0f, 1.5f, c.outline))
                } else if (dark) {
                    listOf(
                        InsetSpec(0f, -hs * 0.18f, hs * 0.24f, -hs * 0.1f, rgba(160, 182, 192, 0.7f)),
                        InsetSpec(0f, 0f, 0f, 0.5f, rgba(0, 0, 0, 0.2f)),
                    )
                } else {
                    listOf(
                        InsetSpec(0f, -hs * 0.18f, hs * 0.24f, -hs * 0.1f, rgba(206, 230, 240, 0.9f)),
                        InsetSpec(0f, 0f, 0f, 0.5f, rgba(30, 50, 60, 0.08f)),
                    )
                },
                rim = when {
                    lvl == 2 && dark -> RimSpec(
                        180f,
                        listOf(0f to white(1f), 0.4f to white(0.3f), 1f to rgba(150, 170, 180, 0.4f)),
                    )
                    lvl > 0 -> solidRing(c.outline)
                    dark -> RimSpec(
                        180f,
                        listOf(0f to white(1f), 0.4f to white(0.3f), 1f to rgba(150, 170, 180, 0.4f)),
                    )
                    else -> RimSpec(
                        180f,
                        listOf(0f to white(1f), 0.4f to white(0.4f), 1f to rgba(190, 210, 220, 0.55f)),
                    )
                },
                driftOffsetSeconds = drift,
            )

            GlassFinish.Fill -> LayerSpec(
                shadow = if (dark) {
                    ShadowSpec(hs * 0.4f + 4f, hs * 0.1f, 0.96f, 0.85f, p[60].alpha(0.7f))
                } else {
                    ShadowSpec(hs * 0.4f + 4f, hs * 0.15f, 0.96f, 0.8f, p[60].alpha(0.5f))
                },
                caustic = null,
                contact = null,
                light = if (lvl == 2) {
                    emptyList()
                } else if (dark) {
                    listOf(
                        RadialSpec(0.30f, 0f, 0.90f, 0.80f, white(0.3f * ls)),
                        RadialSpec(0.70f, 1f, 0.70f, 0.60f, p[80].alpha(0.4f * ls)),
                    )
                } else {
                    listOf(RadialSpec(0.30f, 0f, 0.90f, 0.80f, white(0.26f * ls)))
                },
                grain = if (lvl == 2) 0f else grain(if (dark) 0.08f else 0.1f),
                insets = if (dark) {
                    listOf(
                        InsetSpec(0f, 1f, 0f, 0f, white(0.4f)),
                        InsetSpec(0f, -hs * 0.22f, hs * 0.3f, -hs * 0.1f, g.alpha(0.5f * gs)),
                    )
                } else {
                    listOf(
                        InsetSpec(0f, 1f, 0f, 0f, white(0.45f)),
                        InsetSpec(0f, -hs * 0.22f, hs * 0.3f, -hs * 0.1f, p[70].alpha(0.45f * gs)),
                    )
                },
                rim = RimSpec(180f, listOf(0f to white(if (dark) 0.45f else 0.6f), 0.45f to white(0f))),
                driftOffsetSeconds = drift,
            )

            GlassFinish.Ink -> LayerSpec(
                shadow = if (dark) {
                    ShadowSpec(hs * 0.2f + 8f, hs * 0.15f + 4f, 0.95f, 0.9f, rgba(0, 0, 0, 0.55f))
                } else {
                    ShadowSpec(hs * 0.2f + 8f, hs * 0.15f + 4f, 0.95f, 0.9f, rgba(20, 30, 40, 0.32f))
                },
                caustic = null,
                contact = null,
                light = emptyList(),
                grain = grain(0.05f),
                insets = emptyList(),
                rim = RimSpec(180f, listOf(0f to white(if (dark) 1f else 0.32f), 0.4f to white(0f))),
                driftOffsetSeconds = drift,
            )

            GlassFinish.Dry -> LayerSpec(
                shadow = null,
                caustic = null,
                contact = null,
                light = emptyList(),
                grain = grain(if (dark) 0.18f else SeaGlassOpacityTokens.GrainDry),
                insets = listOf(
                    InsetSpec(0f, 0f, 0f, 1f, if (dark) white(0.04f) else rgba(30, 45, 55, 0.06f)),
                ),
                rim = RimSpec(180f, listOf(0f to white(if (dark) 0.14f else 0.75f), 0.45f to white(0f))),
                driftOffsetSeconds = drift,
            )

            GlassFinish.Bead -> LayerSpec(
                shadow = ShadowSpec(3f, 1.5f, 0.9f, 0.9f, if (dark) rgba(0, 0, 0, 0.5f) else p[40].alpha(0.45f)),
                caustic = null,
                contact = null,
                light = emptyList(),
                grain = 0f,
                insets = listOf(
                    InsetSpec(0f, 1f, 1f, 0f, white(if (dark) 0.6f else 0.5f)),
                    InsetSpec(0f, -1.5f, 2f, 0f, p[10].alpha(if (dark) 0.2f else 0.3f)),
                ),
                rim = null,
                driftOffsetSeconds = drift,
            )

            GlassFinish.Plain -> LayerSpec(null, null, null, emptyList(), 0f, emptyList(), null, drift)
        }
    }

    /** CSS clamps gradient stops so they never decrease. */
    private fun monotonic(vararg stops: Pair<Float, Color>): List<Pair<Float, Color>> {
        var last = 0f
        return stops.map { (pos, color) ->
            last = max(last, pos)
            last to color
        }
    }
}
