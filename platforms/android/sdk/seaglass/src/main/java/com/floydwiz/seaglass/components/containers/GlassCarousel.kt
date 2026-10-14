package com.floydwiz.seaglass.components.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.components.navigation.GlassPageDots

/**
 * Carousel of content items that shrink off-screen; the items stay unglazed with 28 dp corners.
 * - Controls laid over them are clear: [overlay] is drawn above the items (caption, arrow lens), with page dots at
 *   the bottom centre.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun GlassCarousel(
    itemCount: Int,
    modifier: Modifier = Modifier,
    state: CarouselState = rememberCarouselState { itemCount },
    preferredItemWidth: Dp = 300.dp,
    overlay: @Composable BoxScope.() -> Unit = {},
    itemContent: @Composable CarouselItemScope.(index: Int) -> Unit,
) {
    Box(modifier) {
        HorizontalMultiBrowseCarousel(
            state = state,
            preferredItemWidth = preferredItemWidth,
            itemSpacing = 8.dp,
        ) { index -> itemContent(index) }
        overlay()
        GlassPageDots(
            count = itemCount.coerceAtMost(5),
            current = state.currentItem.coerceIn(0, itemCount.coerceAtMost(5) - 1),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
        )
    }
}
