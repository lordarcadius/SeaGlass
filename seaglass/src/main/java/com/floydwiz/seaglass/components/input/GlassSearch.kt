package com.floydwiz.seaglass.components.input

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonStyle
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.LocalMotionEnabled
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnRole
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.theme.RobotoFlex

@Composable
private fun inkColor() = if (LocalSeaGlassDark.current) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight

@Composable
private fun SearchText(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier,
    source: MutableInteractionSource,
    role: TonalTones,
    placeholderWeight: Int = 450,
    valueColor: androidx.compose.ui.graphics.Color = inkColor(),
    placeholderColor: androidx.compose.ui.graphics.Color = glassOnSurfaceVariant(),
) {
    Box(modifier, contentAlignment = Alignment.CenterStart) {
        if (value.isEmpty()) Text(placeholder, color = placeholderColor, fontSize = 16.sp, fontWeight = FontWeight(placeholderWeight), maxLines = 1)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = valueColor, fontSize = 16.sp, fontWeight = FontWeight(450), fontFamily = RobotoFlex),
            cursorBrush = SolidColor(glassAccent(role)),
            interactionSource = source,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Search inside content: a 46 dp well. Focus tints it and reveals a clear lens and a Cancel button.
 */
@Composable
fun GlassSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    trailingIcon: String? = "mic",
    onCancel: (() -> Unit)? = null,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focusedReal by source.collectIsFocusedAsState()
    val focused = enabled && (focusedReal || LocalForcedGlassState.current == GlassState.Focus)
    val variant = glassOnSurfaceVariant()
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassSurface(
            finish = GlassFinish.Well,
            modifier = Modifier.weight(1f).height(46.dp),
            shape = SeaGlassShape.Pane(23.dp),
            role = role,
            tinted = focused,
            enabled = enabled,
            interactionSource = source,
            reactsTo = setOf(GlassState.Hover, GlassState.Focus),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(Modifier.padding(start = 14.dp, end = 10.dp), Arrangement.spacedBy(10.dp), Alignment.CenterVertically) {
                SymbolIcon("search", size = 22.dp, tint = variant)
                SearchText(value, onValueChange, placeholder, Modifier.weight(1f), source, role)
                when {
                    value.isNotEmpty() && enabled -> ClearLens(variant) { onValueChange("") }
                    trailingIcon != null -> SymbolIcon(trailingIcon, size = 22.dp, tint = variant, modifier = Modifier.padding(end = 4.dp))
                }
            }
        }
        if (onCancel != null && focused) {
            GlassButton("Cancel", onCancel, style = GlassButtonStyle.Plain, role = role)
        }
    }
}

/** Search that floats over content: a clear capsule with its own clear lens beside it. */
@Composable
fun GlassFloatingSearch(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    lensIcon: String = "tune",
    onLensClick: () -> Unit = {},
    role: TonalTones = LocalGlassRole.current,
) {
    val source = remember { MutableInteractionSource() }
    val ink = inkColor()
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassSurface(
            finish = GlassFinish.Clear,
            modifier = Modifier.weight(1f).height(52.dp),
            shape = SeaGlassShape.Capsule,
            role = role,
            reactsTo = setOf(GlassState.Hover, GlassState.Focus),
            interactionSource = source,
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(Modifier.padding(start = 18.dp, end = 16.dp), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                SymbolIcon("search", size = 22.dp, tint = ink)
                SearchText(value, onValueChange, placeholder, Modifier.weight(1f), source, role, placeholderWeight = 500)
                SymbolIcon("mic", size = 22.dp, tint = ink)
            }
        }
        GlassSurface(
            finish = GlassFinish.Clear,
            modifier = Modifier.size(52.dp),
            shape = SeaGlassShape.Lens,
            role = role,
            onClick = onLensClick,
        ) {
            SymbolIcon(lensIcon, size = 24.dp, tint = LocalContentColor.current, contentDescription = "Filters")
        }
    }
}

class PromptAttachment(val name: String, val thumbnail: Painter? = null)

private val Waveform = listOf(8, 14, 22, 30, 18, 26, 36, 24, 14, 20, 32, 22, 12, 18, 26, 16, 10, 6)

/**
 * Prompt bar: a clear capsule with a plain add and mic, a glass send lens and an attachment capsule above it.
 * While [listening] the whole bar turns to tinted glass with a live waveform and a pearl stop lens.
 * - With [onVoice] set, an empty field shows a lavender glass voice lens in place of send.
 */
@Composable
fun GlassPromptBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Ask anything",
    attachment: PromptAttachment? = null,
    onRemoveAttachment: () -> Unit = {},
    onAdd: () -> Unit = {},
    listening: Boolean = false,
    onListeningChange: (Boolean) -> Unit = {},
    onVoice: (() -> Unit)? = null,
    listeningRole: TonalTones = LocalRolePalettes.current[GlassRole.Lavender],
    role: TonalTones = LocalGlassRole.current,
) {
    val source = remember { MutableInteractionSource() }
    val ink = inkColor()
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (attachment != null && !listening) {
            GlassSurface(
                finish = GlassFinish.Clear,
                modifier = Modifier.height(40.dp),
                shape = SeaGlassShape.Capsule,
                role = role,
                reactsTo = emptySet(),
            ) {
                Row(Modifier.padding(start = 6.dp, end = 12.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                    if (attachment.thumbnail != null) {
                        Image(attachment.thumbnail, null, Modifier.size(28.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    }
                    Text(attachment.name, color = ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    SymbolIcon(
                        "close", size = 16.dp, tint = glassOnSurfaceVariant(), contentDescription = "Remove attachment",
                        modifier = Modifier.clickable(role = Role.Button, onClick = onRemoveAttachment),
                    )
                }
            }
        }
        if (listening) {
            ListeningBar(listeningRole, onStop = { onListeningChange(false) })
        } else {
            GlassSurface(
                finish = GlassFinish.Clear,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = SeaGlassShape.Capsule,
                role = role,
                interactionSource = source,
                reactsTo = setOf(GlassState.Focus),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(Modifier.padding(start = 14.dp, end = 8.dp), Arrangement.spacedBy(10.dp), Alignment.CenterVertically) {
                    GlassSurface(
                        finish = GlassFinish.Plain,
                        modifier = Modifier.size(40.dp),
                        shape = SeaGlassShape.Lens,
                        onClick = onAdd,
                    ) { SymbolIcon("add", size = 24.dp, tint = ink, contentDescription = "Add") }
                    SearchText(value, onValueChange, placeholder, Modifier.weight(1f), source, role)
                    GlassSurface(
                        finish = GlassFinish.Plain,
                        modifier = Modifier.size(40.dp),
                        shape = SeaGlassShape.Lens,
                        onClick = { onListeningChange(true) },
                    ) { SymbolIcon("mic", size = 22.dp, tint = ink, contentDescription = "Dictate") }
                    val voice = onVoice != null && value.isBlank()
                    GlassSurface(
                        finish = GlassFinish.Glass,
                        modifier = Modifier.size(44.dp),
                        shape = SeaGlassShape.Lens,
                        role = if (voice) listeningRole else role,
                        onClick = if (voice) onVoice!! else onSend,
                    ) {
                        SymbolIcon(
                            if (voice) "graphic_eq" else "arrow_upward", size = 24.dp, weight = 600, tint = LocalContentColor.current,
                            contentDescription = if (voice) "Voice mode" else "Send",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListeningBar(role: TonalTones, onStop: () -> Unit) {
    val foreground = glassOnRole(role)
    val motion = LocalMotionEnabled.current
    val transition = rememberInfiniteTransition(label = "waveform")
    val phase by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(SeaGlassTokens.Motion.millis(900)), RepeatMode.Reverse), label = "wave",
    )
    GlassSurface(
        finish = GlassFinish.Glass,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = SeaGlassShape.Capsule,
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(start = 18.dp, end = 8.dp), Arrangement.spacedBy(14.dp), Alignment.CenterVertically) {
            SymbolIcon("close", size = 22.dp, tint = foreground, contentDescription = "Cancel")
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Waveform.forEachIndexed { index, height ->
                    val wobble = if (motion) 0.65f + 0.35f * ((index % 3) * 0.5f + phase * (if (index % 2 == 0) 1f else -1f)).coerceIn(0f, 1f) else 1f
                    Box(
                        Modifier
                            .size(width = 4.dp, height = height.dp)
                            .graphicsLayer { scaleY = wobble }
                            .clip(CircleShape)
                            .background(foreground.copy(alpha = 0.85f)),
                    )
                }
            }
            Text("Listening", Modifier.weight(1f), color = foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            GlassSurface(
                finish = GlassFinish.Pearl,
                modifier = Modifier.size(44.dp),
                shape = SeaGlassShape.Lens,
                role = role,
                onClick = onStop,
                contentColor = role[40],
            ) { SymbolIcon("stop", size = 22.dp, fill = true, tint = role[40], contentDescription = "Stop") }
        }
    }
}

/** Compact picker: a clear capsule with an accent symbol that opens a picker pane. */
@Composable
fun GlassPickerChip(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val accent = glassAccent(role)
    GlassSurface(
        finish = GlassFinish.Clear,
        modifier = modifier.height(40.dp),
        shape = SeaGlassShape.Capsule,
        role = role,
        interactionSource = interactionSource,
        onClick = onClick,
    ) {
        Row(Modifier.padding(horizontal = 14.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            SymbolIcon(icon, size = 20.dp, tint = accent)
            Text(label, color = LocalContentColor.current, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}
