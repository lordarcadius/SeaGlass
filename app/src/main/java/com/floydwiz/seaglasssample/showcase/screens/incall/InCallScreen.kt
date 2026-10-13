package com.floydwiz.seaglasssample.showcase.screens.incall

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalAdaptiveTint
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.R
import com.floydwiz.seaglasssample.showcase.screens.Avatar
import com.floydwiz.seaglasssample.showcase.screens.belowStatusBar
import com.floydwiz.seaglasssample.showcase.screens.saturateBrightness
import kotlinx.coroutines.delay

private class CallControl(val icon: String, val label: String)

private val Controls = listOf(
    CallControl("mic_off", "Mute"), CallControl("dialpad", "Keypad"), CallControl("volume_up", "Speaker"),
    CallControl("person_add", "Add call"), CallControl("videocam", "Video"), CallControl("pause", "Hold"),
)

/** Active call: a blurred, saturated caller photo behind clear control lenses; the active control is glass. */
@Composable
fun InCallScreen(onEnd: () -> Unit) {
    val dark = LocalSeaGlassDark.current
    val surface = MaterialTheme.colorScheme.surface
    val ink = MaterialTheme.colorScheme.onSurface
    val inkVariant = ink.copy(alpha = if (dark) 0.8f else 0.9f)
    val roles = LocalRolePalettes.current
    var seconds by remember { mutableIntStateOf(267) }
    var active by remember { mutableStateOf(setOf("Speaker")) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
        }
    }

    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = {
            Box(Modifier.fillMaxSize().clipToBounds()) {
                Image(
                    painterResource(R.drawable.seaglass_photo), null,
                    Modifier.fillMaxSize().graphicsLayer {
                        scaleX = 1.12f
                        scaleY = 1.12f
                    }.blur(34.dp),
                    alignment = BiasAlignment(-0.47f, 0f),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(saturateBrightness(1.5f, if (dark) 0.4f else 1.08f)),
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            0f to surface.copy(alpha = if (dark) 0.72f else 0.62f),
                            0.45f to surface.copy(alpha = if (dark) 0.42f else 0.28f),
                            1f to surface.copy(alpha = if (dark) 0.7f else 0.66f),
                        ),
                    ),
                )
            }
        },
    ) {
        CompositionLocalProvider(LocalAdaptiveTint provides true) {
        Column(Modifier.fillMaxSize().navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            GlassSurface(
                GlassFinish.Clear, Modifier.belowStatusBar(60.dp).size(164.dp, 34.dp), SeaGlassShape.Capsule,
                reactsTo = emptySet(),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    SymbolIcon("graphic_eq", size = 18.dp, tint = glassAccent(roles[GlassRole.Kelly]))
                    Text("HD voice · %02d:%02d".format(seconds / 60, seconds % 60), color = ink, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(34.dp))
            GlassSurface(GlassFinish.Clear, Modifier.size(152.dp), SeaGlassShape.Round, reactsTo = emptySet()) {
                Avatar(0, 132.dp)
            }
            Spacer(Modifier.height(20.dp))
            Text("Maya Ortiz", color = ink, fontSize = 32.sp, fontWeight = FontWeight(650), letterSpacing = (-0.3).sp)
            Spacer(Modifier.height(6.dp))
            Text("Mobile · Shore walk group", color = inkVariant, fontSize = 15.sp, fontWeight = FontWeight(500))
            Spacer(Modifier.height(100.dp))
            Controls.chunked(3).forEachIndexed { row, items ->
                if (row > 0) Spacer(Modifier.height(28.dp))
                Row {
                    items.forEach { control ->
                        val on = control.label in active
                        Column(Modifier.width(110.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            GlassIconButton(
                                control.icon,
                                { active = if (on) active - control.label else active + control.label },
                                style = if (on) GlassButtonStyle.Glass else GlassButtonStyle.Clear,
                                size = 78.dp, filled = on, contentDescription = control.label,
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(control.label, color = ink, fontSize = 13.5.sp, fontWeight = FontWeight(600), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().padding(bottom = 24.dp), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
                GlassIconButton("chat", {}, size = 58.dp, contentDescription = "Message")
                GlassSurface(
                    GlassFinish.Glass, Modifier.size(180.dp, 72.dp), SeaGlassShape.Capsule, roles[GlassRole.SeaPink],
                    onClick = onEnd,
                ) { SymbolIcon("call_end", size = 34.dp, fill = true, tint = LocalContentColor.current, contentDescription = "End call") }
                GlassIconButton("more_horiz", {}, size = 58.dp, contentDescription = "More")
            }
        }
        }
    }
}
