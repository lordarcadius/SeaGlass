package com.floydwiz.seaglasssample.showcase.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.containers.GlassCaption
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalAdaptiveTint
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.showcase.Area
import com.floydwiz.seaglasssample.showcase.Destinations
import com.floydwiz.seaglasssample.showcase.screens.saturateBrightness

private const val TwoColumnsFromDp = 600

/** Home: the sample photo fixed behind the screen and one clear glass card per area scrolling over it. */
@Composable
fun HomeScreen(onOpen: (Area) -> Unit) {
    val dark = LocalSeaGlassDark.current
    val columns = if (LocalConfiguration.current.screenWidthDp >= TwoColumnsFromDp) 2 else 1
    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = {
            Image(
                painterResource(R.drawable.seaglass_photo), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
                colorFilter = if (dark) ColorFilter.colorMatrix(saturateBrightness(1.25f, 0.3f)) else null,
            )
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(alpha = if (dark) 0.35f else 0.5f)))
        },
    ) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
            LazyVerticalGrid(
                GridCells.Fixed(columns),
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 40.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 32.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GlassCaption(Modifier.padding(bottom = 10.dp).fillMaxWidth()) {
                        Column(Modifier.padding(horizontal = 22.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Sea Glass", fontSize = 46.sp, fontWeight = FontWeight(760), letterSpacing = (-0.8).sp)
                            Text("A glass material for Compose: soft light, real blur and organic shapes.", fontSize = 16.sp, lineHeight = 23.sp, fontWeight = FontWeight(500))
                        }
                    }
                }
                items(Destinations.areas) { area -> AreaCard(area) { onOpen(area) } }
            }
        }
    }
}

@Composable
private fun AreaCard(area: Area, onClick: () -> Unit) {
    GlassSurface(
        GlassFinish.Clear, Modifier.fillMaxWidth().heightIn(min = 104.dp), SeaGlassShape.Pane(28.dp),
        onClick = onClick, contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(horizontal = 22.dp, vertical = 16.dp), Arrangement.spacedBy(18.dp), Alignment.CenterVertically) {
            SymbolIcon(area.icon, size = 36.dp, tint = glassAccent())
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(area.title, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight(700))
                Text(area.description, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, lineHeight = 19.sp, fontWeight = FontWeight(500))
            }
            SymbolIcon("chevron_right", size = 22.dp, tint = glassOnSurfaceVariant())
        }
    }
}
