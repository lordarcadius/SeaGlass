package com.floydwiz.seaglass.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.buttons.GlassButton
import com.floydwiz.seaglass.components.buttons.GlassButtonSize
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.glassContentColor
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import kotlinx.coroutines.delay
import com.floydwiz.seaglass.tokens.SeaGlassSizeTokens

public class ToastAction(public val label: String, public val onClick: () -> Unit)

/**
 * Toast: an ink pane, dark on light themes and pale on dark, so it reads as passing above everything.
 * - One line is 52 dp tall; with [detail] it is 68 dp with 26 dp corners.
 * - The symbol and action use the role colour tuned for ink.
 */
@Composable
public fun GlassToast(
    message: String,
    modifier: Modifier = Modifier,
    icon: String = "check_circle",
    iconRole: GlassRole = GlassRole.Aqua,
    detail: String? = null,
    action: ToastAction? = null,
) {
    val dark = LocalSeaGlassDark.current
    val palette = LocalRolePalettes.current[iconRole]
    val ink = glassContentColor(GlassFinish.Ink, palette, dark)
    val accent = if (dark) palette[40] else palette[80]
    GlassSurface(
        finish = GlassFinish.Ink,
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = if (detail != null) 68.dp else SeaGlassSizeTokens.Toast).semantics { liveRegion = LiveRegionMode.Polite },
        shape = if (detail != null) SeaGlassShape.Pane(26.dp) else SeaGlassShape.Capsule,
        reactsTo = emptySet(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(start = 16.dp, end = if (action != null) 6.dp else 18.dp), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
            SymbolIcon(icon, size = 22.dp, fill = icon == "check_circle", tint = accent)
            Column(Modifier.weight(1f)) {
                Text(message, color = ink, fontSize = 15.sp, fontWeight = FontWeight(if (detail != null) 600 else 550), maxLines = 2)
                if (detail != null) Text(detail, color = ink.copy(alpha = 0.75f), fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            }
            if (action != null) {
                GlassSurface(
                    finish = GlassFinish.Plain,
                    modifier = Modifier.height(40.dp),
                    shape = SeaGlassShape.Capsule,
                    onClick = action.onClick,
                    contentColor = accent,
                ) { Text(action.label, Modifier.padding(horizontal = 14.dp), color = accent, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

/** Toast over media: clear glass with an avatar, a message and a glass action. */
@Composable
public fun GlassMediaToast(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: Painter? = null,
    role: TonalTones = LocalGlassRole.current,
) {
    GlassSurface(
        finish = GlassFinish.Clear,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = SeaGlassShape.Capsule,
        role = role,
        reactsTo = emptySet(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(start = 10.dp, end = 9.dp), Arrangement.spacedBy(10.dp), Alignment.CenterVertically) {
            if (avatar != null) Image(avatar, null, Modifier.size(36.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Text(message, Modifier.weight(1f), color = LocalContentColor.current, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            GlassButton(actionLabel, onAction, size = GlassButtonSize.S, role = role)
        }
    }
}

public class ToastData(
    public val message: String,
    public val icon: String,
    public val role: GlassRole,
    public val detail: String?,
    public val action: ToastAction?,
)

/** Holds the toast on screen; each [show] replaces the current one and clears it after [durationMillis]. */
@Stable
public class GlassToastState {
    public var current: ToastData? by mutableStateOf<ToastData?>(null)
        private set

    public suspend fun show(
        message: String,
        icon: String = "check_circle",
        role: GlassRole = GlassRole.Aqua,
        detail: String? = null,
        action: ToastAction? = null,
        durationMillis: Long = 2600,
    ) {
        current = ToastData(message, icon, role, detail, action)
        delay(durationMillis)
        current = null
    }

    public fun dismiss() {
        current = null
    }
}

/** Shows the toast from [state] at the bottom centre with a rise-and-fade. */
@Composable
public fun GlassToastHost(state: GlassToastState, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = state.current != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            val data = state.current
            if (data != null) GlassToast(data.message, icon = data.icon, iconRole = data.role, detail = data.detail, action = data.action)
        }
    }
}
