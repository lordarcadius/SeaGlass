package com.floydwiz.seaglasssample.showcase.screens.assistant

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.buttons.GlassIconButton
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.feedback.GlassLoadingIndicator
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.input.GlassPromptBar
import com.floydwiz.seaglass.components.selection.GlassSuggestionChip
import com.floydwiz.seaglass.material.GlassBackdrop
import com.floydwiz.seaglass.material.GlassEdgeDiffusion
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalMotionEnabled
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglasssample.showcase.catalogue.PhotoTile
import com.floydwiz.seaglasssample.showcase.screens.ScreenTopBar
import com.floydwiz.seaglasssample.showcase.screens.ellipse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private sealed interface Message {
    class User(val text: String) : Message
    class Reply(val text: String, val photos: List<Int> = emptyList(), val suggestions: Boolean = false, val card: Boolean = false) : Message
}

private val Opening = listOf(
    Message.User("Which pieces from Saturday are cobalt?"),
    Message.Reply("I found 4 cobalt pieces in Shore walk. The deepest blue one was taken at 6:52 near the north rocks.", photos = listOf(9, 10, 3, 1), suggestions = true),
    Message.User("Make a cobalt album and share it with Maya"),
    Message.Reply("Done. Cobalt finds has 4 photos and Maya can view it.", card = true),
)

/** Assistant: frost bubbles, plain answers with a lavender pebble mark, a clear prompt bar and a voice mode. */
@Composable
fun AssistantScreen() {
    var voice by remember { mutableStateOf(false) }
    Crossfade(voice, label = "assistantMode") { inVoice ->
        if (inVoice) VoiceMode { voice = false } else Chat { voice = true }
    }
}

@Composable
private fun Aurora(strong: Boolean) {
    val dark = LocalSeaGlassDark.current
    val roles = LocalRolePalettes.current
    val lavender = roles[GlassRole.Lavender]
    val aqua = roles[GlassRole.Aqua]
    Box(
        Modifier.fillMaxSize().drawBehind {
            fun tone(color: Color, alpha: Float) = color.copy(alpha = alpha) to color.copy(alpha = 0f)
            val (l0, l1) = tone(if (dark) lavender[40] else lavender[80], if (dark) 0.5f else 0.6f)
            val (a0, a1) = tone(if (dark) aqua[30] else aqua[80], if (dark) 0.6f else 0.5f)
            ellipse(0.18f, 1f, 0.7f, if (strong) 0.55f else 0.38f, 0f to l0, 0.72f to l1)
            ellipse(0.88f, 0.96f, 0.7f, if (strong) 0.5f else 0.34f, 0f to a0, 0.72f to a1)
            if (strong) {
                val (c0, c1) = tone(if (dark) lavender[30] else lavender[90], if (dark) 0.6f else 0.8f)
                ellipse(0.5f, 0.5f, 0.5f, 0.3f, 0f to c0, 0.7f to c1)
            }
        },
    )
}

@Composable
private fun Chat(onVoice: () -> Unit) {
    val messages = remember { mutableStateListOf<Message>().apply { addAll(Opening) } }
    var draft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val ink = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(messages.size) { listState.animateScrollToItem(messages.size) }

    GlassBackdrop(
        Modifier.fillMaxSize(),
        backdrop = {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                Aurora(strong = false)
                LazyColumn(
                    Modifier.fillMaxSize().imePadding(), listState,
                    contentPadding = PaddingValues(top = topInset + 64.dp, bottom = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    item { Text("Today 9:41", Modifier.fillMaxWidth(), color = glassOnSurfaceVariant(), fontSize = 12.5.sp, fontWeight = FontWeight(600), textAlign = TextAlign.Center) }
                    items(messages.size) { index ->
                        when (val message = messages[index]) {
                            is Message.User -> UserBubble(message.text)
                            is Message.Reply -> ReplyBlock(message)
                        }
                    }
                }
            }
        },
    ) {
        GlassEdgeDiffusion(Modifier.fillMaxWidth(), height = topInset + 84.dp, visible = listState.canScrollBackward)
        ScreenTopBar("menu", {}, "edit_square", { messages.clear(); messages.addAll(Opening) }, title = "Assistant")
        Box(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().imePadding()) {
            GlassEdgeDiffusion(Modifier.fillMaxWidth(), height = 140.dp, fromBottom = true)
            GlassPromptBar(
                draft, { draft = it },
                onSend = {
                    val text = draft.trim()
                    if (text.isNotEmpty()) {
                        messages += Message.User(text)
                        draft = ""
                        scope.launch {
                            delay(700)
                            messages += Message.Reply("I looked through your library for “$text”. This is a demo, so there is nothing to show yet.")
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 52.dp, bottom = 10.dp),
                onVoice = onVoice,
            )
        }
    }
}

@Composable
private fun UserBubble(text: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), Arrangement.End) {
        GlassSurface(
            GlassFinish.Frost, Modifier.widthIn(max = 268.dp), SeaGlassShape.Corners(22.dp, 22.dp, 10.dp, 22.dp), reactsTo = emptySet(),
        ) { Text(text, Modifier.padding(horizontal = 18.dp, vertical = 12.dp), color = LocalContentColor.current, fontSize = 15.5.sp, lineHeight = 22.sp, fontWeight = FontWeight(500)) }
    }
}

@Composable
private fun ReplyBlock(reply: Message.Reply) {
    val ink = MaterialTheme.colorScheme.onSurface
    val variant = glassOnSurfaceVariant()
    val lavender = LocalRolePalettes.current[GlassRole.Lavender]
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            GlassSurface(GlassFinish.Glass, Modifier.size(30.dp), SeaGlassShape.Pebble, lavender, reactsTo = emptySet()) {
                SymbolIcon("auto_awesome", size = 17.dp, fill = true, tint = LocalContentColor.current)
            }
            Text("Assistant", color = ink, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
        }
        Text(reply.text, color = ink, fontSize = 15.5.sp, lineHeight = 23.sp, fontWeight = FontWeight(450))
        if (reply.photos.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                reply.photos.forEach { PhotoTile(it, Modifier.size(88.dp), RoundedCornerShape(16.dp)) }
            }
        }
        if (reply.suggestions) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassSuggestionChip("Make an album", "photo_album", {})
                GlassSuggestionChip("Show on map", "map", {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(17.dp), modifier = Modifier.padding(start = 4.dp)) {
                listOf("content_copy", "thumb_up", "thumb_down", "refresh").forEach { SymbolIcon(it, size = 19.dp, tint = variant.copy(alpha = 0.85f)) }
            }
        }
        if (reply.card) {
            GlassCard(Modifier.width(320.dp).height(84.dp), onClick = {}, shape = SeaGlassShape.Pane(24.dp)) {
                Row(Modifier.fillMaxSize().padding(12.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
                    PhotoTile(9, Modifier.size(60.dp), RoundedCornerShape(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Cobalt finds", color = ink, fontSize = 15.5.sp, fontWeight = FontWeight(690))
                        Text("4 photos · Shared with Maya", color = variant, fontSize = 13.sp, fontWeight = FontWeight(500))
                    }
                    SymbolIcon("chevron_right", size = 22.dp, tint = variant)
                }
            }
        }
    }
}

private val OrbBars = listOf(16, 30, 46, 30, 16)

@Composable
private fun VoiceMode(onExit: () -> Unit) {
    val dark = LocalSeaGlassDark.current
    val roles = LocalRolePalettes.current
    val lavender = roles[GlassRole.Lavender]
    val ink = MaterialTheme.colorScheme.onSurface
    val variant = glassOnSurfaceVariant()
    var listening by remember { mutableStateOf(true) }
    val motion = LocalMotionEnabled.current
    val wave by rememberInfiniteTransition(label = "voiceWave").animateFloat(
        0.55f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "bars",
    )

    GlassBackdrop(Modifier.fillMaxSize(), backdrop = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) { Aurora(strong = true) } }) {
        ScreenTopBar("expand_more", onExit, "subtitles", {}, title = "Voice")
        Column(Modifier.fillMaxSize().navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 152.dp))
            Text("Which of these pieces would", color = ink, fontSize = 25.sp, fontWeight = FontWeight(620), letterSpacing = (-0.2).sp)
            Text("make the best pendant…", color = ink.copy(alpha = 0.58f), fontSize = 25.sp, fontWeight = FontWeight(620), letterSpacing = (-0.2).sp)
            Spacer(Modifier.height(96.dp))
            Box(Modifier.size(300.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { listening = !listening }, contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    listOf(300.dp to 0f, 236.dp to 0.05f).forEach { (diameter, fade) ->
                        val radius = diameter.toPx() / 2f
                        drawCircle(Brush.radialGradient(0.83f to Color.Transparent, 0.84f to lavender[60].copy(alpha = (if (dark) 0.22f else 0.18f) - fade), 1f to Color.Transparent, center = center, radius = radius * 1.2f), radius * 1.2f, center)
                        drawCircle(Color.White.copy(alpha = if (dark) 0.14f else 0.7f), radius, center, style = Stroke(1.dp.toPx()))
                    }
                }
                GlassLoadingIndicator(size = 180.dp, role = lavender)
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    OrbBars.forEachIndexed { index, height ->
                        val scale = if (listening && motion) 0.55f + 0.45f * (if (index % 2 == 0) wave else 1.55f - wave).coerceIn(0f, 1f) else 1f
                        Box(
                            Modifier.size(6.dp, height.dp).graphicsLayer { scaleY = scale }
                                .background(glassOnRole(lavender).copy(alpha = 0.85f), RoundedCornerShape(3.dp)),
                        )
                    }
                }
            }
            Text(if (listening) "Listening" else "Paused", Modifier.offset(y = (-14).dp), color = glassAccent(lavender), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Tap the orb to ${if (listening) "interrupt" else "resume"}", Modifier.offset(y = (-10).dp), color = variant, fontSize = 13.sp, fontWeight = FontWeight(500))
            Spacer(Modifier.weight(1f))
            Row(Modifier.padding(bottom = 36.dp), Arrangement.spacedBy(24.dp)) {
                listOf(Triple("mic_off", "Mute", false), Triple("videocam", "Camera", false), Triple("keyboard", "Type", false), Triple("close", "End", true)).forEach { (icon, label, end) ->
                    Column(Modifier.width(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        GlassIconButton(
                            icon, { if (label == "Type" || end) onExit() }, style = if (end) GlassButtonStyle.Glass else GlassButtonStyle.Clear,
                            size = 64.dp, contentDescription = label, filled = end, role = if (end) roles[GlassRole.SeaPink] else LocalGlassRole.current,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(label, color = ink, fontSize = 12.5.sp, fontWeight = FontWeight(600), maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
