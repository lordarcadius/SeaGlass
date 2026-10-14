package com.floydwiz.seaglass.material

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalReduceTransparency
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

/**
 * One glass element: a [finish] drawn on [shape] with content above it.
 * - Provides [LocalContentColor] for the finish, at 38% when disabled.
 * - With [onClick] set it is hoverable, focusable and pressable, and drives the state model.
 * - A Lens draws its backdrop in a separate layer so only the backdrop is magnified.
 * - [onAdaptiveSample] reports the luminance sampled for adaptive tint, for inspection.
 */
@Composable
public fun GlassSurface(
    finish: GlassFinish,
    modifier: Modifier = Modifier,
    shape: SeaGlassShape = SeaGlassShape.Capsule,
    role: TonalTones = LocalGlassRole.current,
    enabled: Boolean = true,
    tinted: Boolean = false,
    adaptive: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null,
    semanticsRole: Role? = Role.Button,
    reactsTo: Set<GlassState> = AllInteractiveStates,
    pressScale: Float = SeaGlassTokens.State.Press.Scale,
    contentColor: Color? = null,
    contentAlignment: Alignment = Alignment.Center,
    onAdaptiveSample: ((AdaptiveSample) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = rememberGlassInteraction(finish, enabled, source, reactsTo, pressScale)
    val dark = LocalSeaGlassDark.current
    val adaptiveTint = rememberAdaptive(adaptive, if (dark) darkBrightnessLift(finish) else 1f, if (dark) darkWhiteFill(finish) else 0f)
    if (onAdaptiveSample != null) {
        LaunchedEffect(adaptiveTint.sample?.luminance, adaptiveTint.amount) { adaptiveTint.sample?.let(onAdaptiveSample) }
    }
    val resolvedContent = when {
        interaction.disabled -> disabledContentColor(dark)
        contentColor != null -> contentColor
        else -> glassContentColor(interaction.finish, role, dark)
    }
    val interactive = if (onClick != null) {
        Modifier.hoverable(source).clickable(
            interactionSource = source, indication = null, enabled = enabled, role = semanticsRole, onClick = onClick,
        )
    } else {
        Modifier
    }
    val magnify = interaction.finish == GlassFinish.Lens && !LocalReduceTransparency.current && LocalGlassLayers.current.body
    val magnifier = rememberLensMagnifier()

    CompositionLocalProvider(LocalContentColor provides resolvedContent) {
        if (magnify) {
            Box(
                modifier.then(adaptiveTint.boundsModifier).then(interactive).seaGlass(interaction, shape, role, tinted, adaptiveTint.amount, resolvedContent, Stages.Frame),
                contentAlignment,
            ) {
                Box(Modifier.matchParentSize().seaGlass(interaction, shape, role, tinted, adaptiveTint.amount, resolvedContent, Stages.Body, magnifier))
                Box(Modifier.matchParentSize().seaGlass(interaction, shape, role, tinted, adaptiveTint.amount, resolvedContent, Stages.Inner))
                content()
            }
        } else {
            Box(
                modifier.then(adaptiveTint.boundsModifier).then(interactive).seaGlass(interaction, shape, role, tinted, adaptiveTint.amount, resolvedContent, Stages.All),
                contentAlignment,
                content = content,
            )
        }
    }
}

/** How much a finish brightens its backdrop on dark themes (the brightness of the recipe), for adaptive tint. */
private fun darkBrightnessLift(finish: GlassFinish): Float = when (finish) {
    GlassFinish.Glass -> 1.25f
    GlassFinish.Frost -> 1.15f
    GlassFinish.Clear -> 1.55f
    GlassFinish.Lens -> 1.65f
    else -> 1f
}

/** The luminance a finish adds on dark themes with its white fill, for adaptive tint. */
private fun darkWhiteFill(finish: GlassFinish): Float = when (finish) {
    GlassFinish.Clear, GlassFinish.Lens -> 0.2f
    else -> 0f
}
