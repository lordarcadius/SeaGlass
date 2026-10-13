package com.floydwiz.seaglass.components.fab

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalHazeState
import com.floydwiz.seaglass.material.LocalMotionEnabled
import com.floydwiz.seaglass.material.LocalMotionScale
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class FabSize(val dp: Dp) { Medium(56.dp), Large(80.dp), ExtraLarge(96.dp) }

/** The one pebble-shaped glass in a layout: 56, 80 or 96 dp, symbol at 42% of the size. */
@Composable
fun GlassFab(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: FabSize = FabSize.Medium,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    contentDescription: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    GlassSurface(
        finish = GlassFinish.Glass,
        modifier = modifier.size(size.dp),
        shape = SeaGlassShape.Pebble,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
    ) {
        SymbolIcon(icon, size = size.dp * 0.42f, fill = true, tint = LocalContentColor.current, contentDescription = contentDescription)
    }
}

/** Extended FAB: a 56 dp capsule with a symbol and a label. */
@Composable
fun GlassExtendedFab(
    label: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    frost: Boolean = false,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    GlassSurface(
        finish = if (frost) GlassFinish.Frost else GlassFinish.Glass,
        modifier = modifier.height(56.dp),
        shape = SeaGlassShape.Capsule,
        role = role,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
    ) {
        Row(Modifier.padding(horizontal = 20.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            SymbolIcon(icon, size = 24.dp, fill = true, tint = LocalContentColor.current)
            Text(label, color = LocalContentColor.current, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp, maxLines = 1)
        }
    }
}

class FabMenuItem(val icon: String, val label: String, val onClick: () -> Unit)

/**
 * FAB with a menu. Fills the area it is placed in so the veil can cover the content behind it.
 * - Opening veils the content with a light blur, melts the pebble through an egg into a round glass close lens, and
 *   raises frost capsules 62 dp apart, each 40 ms after the previous, on spatial.fast.
 * - [label] makes the closed button an extended FAB that narrows to the close lens.
 * - [contentPadding] insets the button and items, for example by the navigation bar, while the veil still fills the area.
 */
@Composable
fun GlassFabMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<FabMenuItem>,
    modifier: Modifier = Modifier,
    icon: String = "edit",
    label: String? = null,
    contentPadding: PaddingValues = PaddingValues(),
    role: TonalTones = LocalGlassRole.current,
) {
    val dark = LocalSeaGlassDark.current
    val motion = LocalMotionEnabled.current
    val motionScale = LocalMotionScale.current
    val scope = rememberCoroutineScope()
    val melt by animateFloatAsState(if (expanded) 1f else 0f, SeaGlassTokens.Motion.spatialFast(), label = "fabMelt")
    val meltShape = melt.coerceIn(0f, 1f)
    val rise = remember(items.size) { List(items.size) { Animatable(0f) } }
    LaunchedEffect(expanded, items.size, motion, motionScale) {
        rise.forEachIndexed { index, animatable ->
            scope.launch {
                if (expanded) {
                    if (motion) delay((index * 40L * motionScale * SeaGlassTokens.Motion.slowdown).toLong())
                    animatable.animateTo(1f, SeaGlassTokens.Motion.spatialFast())
                } else {
                    animatable.animateTo(0f, SeaGlassTokens.Motion.spatialFast())
                }
            }
        }
    }
    val hazeState = LocalHazeState.current
    val tint = if (dark) SeaGlassTokens.Veil.FabMenuDark else SeaGlassTokens.Veil.FabMenuLight
    val veilStyle = remember(dark) {
        HazeBlurStyle {
            blurRadius(6.dp)
            noiseFactor(0f)
            colorEffects(listOf(HazeColorEffect.tint(tint)))
        }
    }

    Box(modifier.fillMaxSize()) {
        if (melt > 0.01f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .alpha(meltShape)
                    .then(if (hazeState != null) Modifier.hazeBlur(HazeInput.Sources(hazeState), veilStyle) else Modifier.background(tint))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onExpandedChange(false) },
            )
            Column(
                Modifier.align(Alignment.BottomEnd).padding(contentPadding).padding(end = 16.dp, bottom = 16.dp + 56.dp + 14.dp),
                Arrangement.spacedBy(10.dp, Alignment.Bottom),
                Alignment.End,
            ) {
                items.forEachIndexed { index, item ->
                    val progress = rise[index].value
                    Box(
                        Modifier.graphicsLayer {
                            alpha = progress.coerceIn(0f, 1f)
                            translationY = (1f - progress) * (index + 1) * 62.dp.toPx()
                            scaleX = 0.7f + 0.3f * progress
                            scaleY = scaleX
                            transformOrigin = TransformOrigin(1f, 1f)
                        },
                    ) {
                        GlassSurface(
                            finish = GlassFinish.Frost,
                            modifier = Modifier.height(52.dp),
                            shape = SeaGlassShape.Capsule,
                            role = role,
                            onClick = { item.onClick(); onExpandedChange(false) },
                        ) {
                            Row(Modifier.padding(start = 20.dp, end = 22.dp), Arrangement.spacedBy(10.dp), Alignment.CenterVertically) {
                                SymbolIcon(item.icon, size = 22.dp, fill = true, tint = LocalContentColor.current)
                                Text(item.label, color = LocalContentColor.current, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
        val closedWidth = if (label != null) 136.dp else 56.dp
        val shape = if (meltShape < 0.5f) {
            SeaGlassShape.lerp(if (label != null) SeaGlassShape.Capsule else SeaGlassShape.Pebble, SeaGlassShape.Egg, meltShape * 2f)
        } else {
            SeaGlassShape.lerp(SeaGlassShape.Egg, SeaGlassShape.Lens, (meltShape - 0.5f) * 2f)
        }
        GlassSurface(
            finish = GlassFinish.Glass,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(contentPadding)
                .padding(16.dp)
                .size(lerp(closedWidth, 56.dp, melt), 56.dp)
                .semantics { contentDescription = if (expanded) "Close menu" else (label ?: "Open menu") },
            shape = shape,
            role = role,
            onClick = { onExpandedChange(!expanded) },
            semanticsRole = Role.Button,
        ) {
            if (melt > 0.5f) {
                SymbolIcon("close", size = 26.dp, weight = 600, tint = LocalContentColor.current)
            } else {
                Row(Modifier.alpha(1f - melt * 2f), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                    SymbolIcon(icon, size = 24.dp, fill = true, tint = LocalContentColor.current)
                    if (label != null) Text(label, color = LocalContentColor.current, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                }
            }
        }
    }
}
