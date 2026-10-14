package com.floydwiz.seaglass.material

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.SeaGlassTheme

@Composable
private fun FinishGallery(dark: Boolean) {
    SeaGlassTheme(dark = dark) {
        val backdrop = if (dark) listOf(Color(0xFF0B3A5A), Color(0xFF2D52D1), Color(0xFF024D62)) else listOf(Color(0xFF6FD3F7), Color(0xFFFEB3B3), Color(0xFF89D6B5))
        Column(
            modifier = Modifier.background(Brush.linearGradient(backdrop)).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GlassFinish.entries.forEach { finish ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.size(width = 104.dp, height = 44.dp).seaGlass(finish)) {}
                    Column(Modifier.size(44.dp).seaGlass(finish, SeaGlassShape.Lens)) {}
                    Column(Modifier.size(width = 120.dp, height = 64.dp).seaGlass(finish, SeaGlassShape.Pane(24.dp))) {}
                }
            }
        }
    }
}

@Preview(name = "Finishes light")
@Composable
private fun FinishGalleryLight() = FinishGallery(dark = false)

@Preview(name = "Finishes dark")
@Composable
private fun FinishGalleryDark() = FinishGallery(dark = true)
