package com.floydwiz.seaglass.material

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalReduceTransparency
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/**
 * One glass element: a [finish] drawn on [shape] with content above it.
 * - Provides [LocalContentColor] for the finish, at 38% when disabled.
 * - With [onClick] set it is hoverable, focusable and pressable, and drives the state model.
 * - A Lens draws its backdrop in a separate layer so only the backdrop is magnified.
 */
@Composable
fun GlassSurface(
    finish: GlassFinish,
    modifier: Modifier = Modifier,
    shape: SeaGlassShape = SeaGlassShape.Capsule,
    role: TonalTones = LocalGlassRole.current,
    enabled: Boolean = true,
    tinted: Boolean = false,
    adaptive: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = rememberGlassInteraction(finish, enabled, source)
    val dark = LocalSeaGlassDark.current
    val contentColor = if (interaction.disabled) disabledContentColor(dark) else glassContentColor(interaction.finish, role, dark)
    val interactive = if (onClick != null) {
        Modifier.hoverable(source).clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick)
    } else {
        Modifier
    }
    val magnify = interaction.finish == GlassFinish.Lens && !LocalReduceTransparency.current && LocalGlassLayers.current.body
    val magnifier = rememberLensMagnifier()

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        if (magnify) {
            Box(
                modifier.then(interactive).seaGlass(interaction, shape, role, tinted, adaptive, contentColor, Stages.Frame),
                contentAlignment,
            ) {
                Box(Modifier.matchParentSize().seaGlass(interaction, shape, role, tinted, adaptive, contentColor, Stages.Body, magnifier))
                Box(Modifier.matchParentSize().seaGlass(interaction, shape, role, tinted, adaptive, contentColor, Stages.Inner))
                content()
            }
        } else {
            Box(
                modifier.then(interactive).seaGlass(interaction, shape, role, tinted, adaptive, contentColor, Stages.All),
                contentAlignment,
                content = content,
            )
        }
    }
}
