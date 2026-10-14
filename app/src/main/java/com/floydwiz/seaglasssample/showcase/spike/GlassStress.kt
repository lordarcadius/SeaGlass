package com.floydwiz.seaglasssample.showcase.spike

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglasssample.R

/** A grid of [Intent] extra `n` identical glass surfaces of finish `finish` over the photo, for counting render passes. */
@Composable
fun GlassStress(intent: Intent?) {
    val n = intent?.getIntExtra("n", 8) ?: 8
    val finish = intent?.getStringExtra("finish")?.let { GlassFinish.valueOf(it) } ?: GlassFinish.Glass
    val moving = intent?.getBooleanExtra("anim", false) ?: false
    val shift = rememberInfiniteTransition(label = "stress").animateFloat(0f, 120f, infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "shift")
    GlassBackdrop(Modifier.fillMaxSize(), backdrop = {
        Image(painterResource(R.drawable.seaglass_photo), null, Modifier.fillMaxSize().graphicsLayer { if (moving) translationX = shift.value }, contentScale = ContentScale.Crop)
    }) {
        FlowRow(Modifier.statusBarsPadding().padding(16.dp), Arrangement.spacedBy(12.dp), Arrangement.spacedBy(12.dp)) {
            repeat(n) { GlassSurface(finish, Modifier.size(150.dp, 56.dp), SeaGlassShape.Capsule, reactsTo = emptySet()) { Text("Glass $it") } }
        }
    }
}
