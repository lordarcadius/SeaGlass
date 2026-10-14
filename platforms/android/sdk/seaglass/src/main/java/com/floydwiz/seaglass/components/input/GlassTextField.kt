package com.floydwiz.seaglass.components.input

import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.tokens.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.material.disabledContentColor
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark
import com.floydwiz.seaglass.theme.RobotoFlex
import com.floydwiz.seaglass.material.SeaGlassTokens
import com.floydwiz.seaglass.tokens.SeaGlassRadiusTokens
import com.floydwiz.seaglass.tokens.SeaGlassSizeTokens

internal data class FieldColors(val label: androidx.compose.ui.graphics.Color, val symbol: androidx.compose.ui.graphics.Color, val value: androidx.compose.ui.graphics.Color, val placeholder: androidx.compose.ui.graphics.Color, val supporting: androidx.compose.ui.graphics.Color)

@Composable
internal fun fieldColors(enabled: Boolean, error: Boolean, focused: Boolean, role: TonalTones): FieldColors {
    val dark = LocalSeaGlassDark.current
    val ink = if (dark) SeaGlassTokens.Content.OnSurfaceDark else SeaGlassTokens.Content.OnSurfaceLight
    val variant = glassOnSurfaceVariant()
    val pink = glassAccent(LocalRolePalettes.current[GlassRole.SeaPink])
    val off = disabledContentColor(dark)
    return FieldColors(
        label = when { !enabled -> off; error -> pink; focused -> glassAccent(role); else -> variant },
        symbol = when { !enabled -> off; error -> pink; else -> variant },
        value = if (enabled) ink else off,
        placeholder = if (enabled) variant else off,
        supporting = when { !enabled -> off; error -> pink; else -> variant.copy(alpha = 0.85f) },
    )
}

/**
 * Text field: an 18 dp well 52 dp tall with its label outside and above, so nothing floats.
 * - Focus tints the well with the accent and adds the halo; error tints it sea pink with a 1.5 dp ring and
 *   swaps in the error text; disabled is dry glass.
 * - A 26 dp clear lens clears a non-empty value.
 */
@Composable
public fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: String? = null,
    supportingText: String? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focusedReal by source.collectIsFocusedAsState()
    val forced = LocalForcedGlassState.current
    val focused = enabled && (focusedReal || forced == GlassState.Focus)
    val seaPink = LocalRolePalettes.current[GlassRole.SeaPink]
    val colors = fieldColors(enabled, error, focused, role)
    val dark = LocalSeaGlassDark.current
    val shape = SeaGlassShape.Pane(SeaGlassRadiusTokens.Field)
    val ring = if (error && enabled && !focused) Modifier.border(1.5.dp, if (dark) seaPink[80] else seaPink[60], shape) else Modifier

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, Modifier.padding(start = 6.dp), color = colors.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        GlassSurface(
            finish = GlassFinish.Well,
            modifier = Modifier.fillMaxWidth().height(SeaGlassSizeTokens.Field).then(ring),
            shape = shape,
            role = if (error) seaPink else role,
            tinted = focused || error,
            enabled = enabled,
            interactionSource = source,
            reactsTo = setOf(GlassState.Hover, GlassState.Focus),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(Modifier.padding(start = 16.dp, end = 13.dp), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                if (leadingIcon != null) SymbolIcon(leadingIcon, size = 20.dp, tint = colors.symbol)
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text(placeholder, color = colors.placeholder, fontSize = 16.sp, fontWeight = FontWeight(450), maxLines = 1)
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        singleLine = singleLine,
                        textStyle = TextStyle(color = colors.value, fontSize = 16.sp, fontWeight = FontWeight(450), fontFamily = RobotoFlex),
                        cursorBrush = SolidColor(glassAccent(role)),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        visualTransformation = visualTransformation,
                        interactionSource = source,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (value.isNotEmpty() && enabled) ClearLens(colors.symbol) { onValueChange("") }
                if (error) SymbolIcon("error", size = 20.dp, fill = true, tint = colors.symbol)
            }
        }
        if (supportingText != null) {
            Text(supportingText, Modifier.padding(start = 6.dp), color = colors.supporting, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/** Multi-line text field: a 22 dp well with a character counter. */
@Composable
public fun GlassTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLength: Int = 280,
    supportingText: String? = null,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focusedReal by source.collectIsFocusedAsState()
    val focused = enabled && (focusedReal || LocalForcedGlassState.current == GlassState.Focus)
    val colors = fieldColors(enabled, false, focused, role)
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, Modifier.padding(start = 6.dp), color = colors.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        GlassSurface(
            finish = GlassFinish.Well,
            modifier = Modifier.fillMaxWidth().height(128.dp),
            shape = SeaGlassShape.Pane(SeaGlassRadiusTokens.TextArea),
            role = role,
            tinted = focused,
            enabled = enabled,
            interactionSource = source,
            reactsTo = setOf(GlassState.Hover, GlassState.Focus),
            contentAlignment = Alignment.TopStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = { if (it.length <= maxLength) onValueChange(it) },
                enabled = enabled,
                textStyle = TextStyle(color = colors.value, fontSize = 16.sp, fontWeight = FontWeight(450), fontFamily = RobotoFlex, lineHeight = 23.sp),
                cursorBrush = SolidColor(glassAccent(role)),
                interactionSource = source,
                modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
            )
            Text(
                "${value.length} / $maxLength",
                Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 8.dp),
                color = colors.supporting, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            )
        }
        if (supportingText != null) {
            Text(supportingText, Modifier.padding(start = 6.dp), color = colors.supporting, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/** 26 dp clear lens with a small close symbol. */
@Composable
internal fun ClearLens(color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    GlassSurface(
        finish = GlassFinish.Clear,
        modifier = Modifier.size(26.dp),
        shape = SeaGlassShape.Round,
        onClick = onClick,
        contentColor = color,
    ) {
        SymbolIcon("close", size = 16.dp, tint = color, contentDescription = "Clear")
    }
}
