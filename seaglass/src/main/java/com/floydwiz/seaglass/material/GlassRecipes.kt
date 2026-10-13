package com.floydwiz.seaglass.material

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.theme.SeaGlassContrast
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Inputs shared by every finish; mirrors the `o` argument of `mat()` in recipes.js. */
@Immutable
internal class GlassContext(
    val dark: Boolean,
    val contrast: SeaGlassContrast,
    val reduceTransparency: Boolean,
    val role: TonalTones,
    val adaptive: Boolean,
    val tinted: Boolean,
) {
    val level get() = contrast.ordinal
    val tintBoost get() = SeaGlassTokens.Contrast.TintBoost[level]
    val transmission get() = SeaGlassTokens.Contrast.TransmissionScale[level]
    val lightScale get() = SeaGlassTokens.Contrast.LightScale[level]

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
        if (c.reduceTransparency) return Body.Solid(reduceTransparencyFill(finish, c))
        val p = c.role
        val dark = c.dark
        var ta = c.tintBoost
        return when (finish) {
            GlassFinish.Glass -> {
                if (c.adaptive && dark && ta < 0.3f) ta = 0.3f
                if (dark) {
                    Body.Backdrop(
                        20f, 1.8f, if (c.adaptive) 0.9f else 1.25f,
                        listOf(lg(p[40].alpha(0.52f + ta), p[30].alpha(0.38f + ta), p[30].alpha(0.3f + ta))),
                    )
                } else {
                    Body.Backdrop(
                        18f, 1.7f, 1.04f,
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
                if (c.adaptive && dark && ta < 0.3f) ta = 0.3f
                if (dark) {
                    Body.Backdrop(
                        14f, 1.6f, if (c.adaptive) 0.9f else 1.15f,
                        listOf(lg(p[40].alpha(0.46f + ta), p[30].alpha(0.36f + ta), p[30].alpha(0.32f + ta))),
                    )
                } else {
                    Body.Backdrop(
                        14f, 1.5f, 1.03f,
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

            GlassFinish.Clear -> if (dark) {
                var smoke = when (c.level) {
                    2 -> 0.86f
                    1 -> 0.6f
                    else -> 0f
                }
                if (c.adaptive && smoke == 0f) smoke = 0.6f
                if (smoke > 0f) {
                    Body.Backdrop(
                        16f, 1.5f, 1f,
                        listOf(lg(rgba(44, 49, 52, smoke), rgba(36, 41, 44, smoke), rgba(32, 37, 40, smoke))),
                    )
                } else {
                    Body.Backdrop(
                        16f, 2f, 1.55f,
                        listOf(lg(white(0.2f), white(0.1f), white(0.12f))),
                    )
                }
            } else {
                Body.Backdrop(
                    16f, 1.8f, 1.06f,
                    listOf(
                        lg(
                            white(0.6f + ta),
                            rgba(250, 253, 255, 0.4f + ta),
                            rgba(236, 248, 255, 0.32f + ta),
                        ),
                    ),
                )
            }

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
                    10f, 1.3f, if (dark) 0.9f else 1.02f,
                    listOfNotNull(base, tint),
                )
            }

            GlassFinish.Milk -> if (dark) {
                Body.Backdrop(
                    30f, 1.5f, 1f,
                    listOf(lg(rgba(48, 54, 58, 0.9f), rgba(38, 43, 46, 0.88f), rgba(33, 38, 41, 0.88f))),
                )
            } else {
                Body.Backdrop(
                    30f, 1.6f, 1.03f,
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
            GlassFinish.Clear -> s.clear
            GlassFinish.Milk -> s.milk
        }
    }

    fun layers(finish: GlassFinish, c: GlassContext, w: Float, h: Float): LayerSpec {
        val drift = ((w * 3f + h) * 1.7f) % 8f
        return if (c.reduceTransparency) reduceTransparencyLayers(finish, c, drift) else litLayers(finish, c, w, h, drift)
    }

    private fun reduceTransparencyLayers(finish: GlassFinish, c: GlassContext, drift: Float): LayerSpec {
        val dark = c.dark
        val line: Color? = when (finish) {
            GlassFinish.Clear -> c.outline.alpha(0.75f)
            GlassFinish.Well -> c.outline
            GlassFinish.Milk -> if (dark) white(0.08f) else rgba(23, 28, 31, 0.08f)
            else -> null
        }
        val insets = buildList {
            if (finish != GlassFinish.Well) add(InsetSpec(0f, 1f, 0f, 0f, white(if (dark) 0.08f else 0.5f)))
            if (line != null) add(InsetSpec(0f, 0f, 0f, 1f, line))
        }
        val milk = finish == GlassFinish.Milk
        val shadow = if (finish == GlassFinish.Well) {
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
                grain = grain(if (dark) 0.12f else 0.18f),
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
                grain = grain(if (dark) 0.1f else 0.15f),
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

            GlassFinish.Clear -> if (dark) {
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
                    grain = grain(0.12f),
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
                    grain = grain(if (dark) 0.05f else 0.07f),
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
