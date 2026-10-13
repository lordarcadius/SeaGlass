package com.floydwiz.seaglasssample.perf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Median and p95 frame duration and jank share over the last 120 frames, from JankStats; compose it only while shown. */
@Composable
fun FpsOverlay(modifier: Modifier = Modifier) {
    val stats = rememberFrameStats()
    var summary by remember { mutableStateOf<FrameSummary?>(null) }
    LaunchedEffect(stats) {
        while (true) {
            delay(500)
            summary = stats.summary(120)
        }
    }
    val current = summary ?: return
    Text(
        "%.1f ms median · %.1f p95 · %.0f%% jank".format(current.totalP50Ms, current.totalP95Ms, current.jankPercent),
        modifier.background(MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.inverseOnSurface, fontSize = 11.sp, fontFamily = FontFamily.Monospace,
    )
}
