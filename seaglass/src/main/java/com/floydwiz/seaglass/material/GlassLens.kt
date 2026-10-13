package com.floydwiz.seaglass.material

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer

private const val Magnification = 1.12f
private const val FallbackMagnification = 1.1f
private const val EdgeBend = 0.08f

private const val LensShader = """
uniform shader content;
uniform float2 size;
uniform float magnify;
uniform float bend;
half4 main(float2 coord) {
    float2 c = size * 0.5;
    float2 d = (coord - c) / c;
    float rim = smoothstep(0.55, 1.0, clamp(length(d), 0.0, 1.0));
    float2 p = (coord - c) / magnify;
    p *= 1.0 - bend * rim;
    return content.eval(p + c);
}
"""

/**
 * Magnifies the layer it is applied to.
 * - API 33+: AGSL shader, 1.12x about the centre with a slight bend toward the rim.
 * - API 31-32: the layer is scaled 1.1x instead.
 */
@Composable
internal fun rememberLensMagnifier(): Modifier =
    if (Build.VERSION.SDK_INT >= 33) {
        val holder = remember { LensEffectHolder() }
        Modifier.graphicsLayer { renderEffect = holder.effect(size.width, size.height) }
    } else {
        Modifier.graphicsLayer {
            scaleX = FallbackMagnification
            scaleY = FallbackMagnification
        }
    }

@RequiresApi(33)
private class LensEffectHolder {
    private val shader = RuntimeShader(LensShader)
    private var width = -1f
    private var height = -1f
    private var effect: androidx.compose.ui.graphics.RenderEffect? = null

    fun effect(w: Float, h: Float): androidx.compose.ui.graphics.RenderEffect {
        val current = effect
        if (current != null && w == width && h == height) return current
        shader.setFloatUniform("size", w, h)
        shader.setFloatUniform("magnify", Magnification)
        shader.setFloatUniform("bend", EdgeBend)
        width = w
        height = h
        return RenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect().also { effect = it }
    }
}
