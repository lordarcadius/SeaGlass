package com.floydwiz.seaglass.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.extendedColors
import com.floydwiz.seaglass.theme.SeaGlassTheme

/** Light and dark previews of one composable. */
@Preview(name = "Light", widthDp = 380, heightDp = 360)
@Preview(name = "Dark", widthDp = 380, heightDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
internal annotation class LightDarkPreviews

/** Light and dark previews of a tall composable. */
@Preview(name = "Light", widthDp = 380, heightDp = 640)
@Preview(name = "Dark", widthDp = 380, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
internal annotation class TallLightDarkPreviews

/** Theme plus a flat backdrop for glass to blur; dark follows the preview's uiMode. */
@Composable
internal fun GlassPreview(content: @Composable ColumnScope.() -> Unit) {
    SeaGlassTheme {
        val dark = com.floydwiz.seaglass.theme.LocalSeaGlassDark.current
        GlassBackdrop(
            backdrop = { Box(Modifier.fillMaxSize().background(extendedColors(dark).backdrop)) },
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), content = content)
        }
    }
}
