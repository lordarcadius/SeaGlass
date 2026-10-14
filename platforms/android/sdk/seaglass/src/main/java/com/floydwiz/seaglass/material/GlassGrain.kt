package com.floydwiz.seaglass.material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import com.floydwiz.seaglass.R

/** Noise tile repeated at 200 dp; the 200 px bitmap is scaled by the density. */
@Composable
internal fun rememberGrainBrush(): ShaderBrush {
    val density = LocalDensity.current.density
    val bitmap = ImageBitmap.imageResource(R.drawable.sg_grain)
    return remember(bitmap, density) {
        val shader = ImageShader(bitmap, TileMode.Repeated, TileMode.Repeated)
        shader.setLocalMatrix(android.graphics.Matrix().apply { setScale(density, density) })
        ShaderBrush(shader)
    }
}
