package com.floydwiz.seaglass.components.selection

import androidx.compose.runtime.getValue
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.color.GlassRole
import com.floydwiz.seaglass.color.TonalTones
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.material.GlassFinish
import com.floydwiz.seaglass.material.GlassState
import com.floydwiz.seaglass.material.GlassSurface
import com.floydwiz.seaglass.material.LocalForcedGlassState
import com.floydwiz.seaglass.shape.SeaGlassShape
import com.floydwiz.seaglass.theme.LocalGlassRole
import com.floydwiz.seaglass.theme.LocalRolePalettes
import com.floydwiz.seaglass.theme.LocalSeaGlassDark

private val BoxSize = 26.dp

/**
 * 26 dp check with a 9 dp radius: a well when off, glass with a heavy check or dash when on or mixed.
 * An error check is a sea pink tinted well with a 1.5 dp ring.
 */
@Composable
fun GlassTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val seaPink = LocalRolePalettes.current[GlassRole.SeaPink]
    val dark = LocalSeaGlassDark.current
    val focused by source.collectIsFocusedAsState()
    val forced = LocalForcedGlassState.current
    val effectiveRole = if (error) seaPink else role
    val shape = SeaGlassShape.Pane(9.dp)
    val on = state != ToggleableState.Off
    val showRing = error && !on && enabled && !focused && forced != GlassState.Focus && forced != GlassState.Disabled

    val host = if (onClick != null) {
        Modifier.triStateToggleable(state, source, null, enabled, Role.Checkbox, onClick)
    } else {
        Modifier
    }
    Box(modifier.then(host).minimumInteractiveComponentSize(), contentAlignment = Alignment.Center) {
        GlassSurface(
            finish = if (on) GlassFinish.Glass else GlassFinish.Well,
            modifier = Modifier
                .size(BoxSize)
                .then(if (showRing) Modifier.border(1.5.dp, if (dark) seaPink[80] else seaPink[60], shape) else Modifier),
            shape = shape,
            role = effectiveRole,
            tinted = error,
            enabled = enabled,
            interactionSource = source,
        ) {
            if (on) {
                SymbolIcon(
                    if (state == ToggleableState.On) "check" else "remove",
                    size = 20.dp, weight = 700, tint = LocalContentColor.current,
                )
            }
        }
    }
}

@Composable
fun GlassCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: Boolean = false,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    GlassTriStateCheckbox(
        state = ToggleableState(checked),
        onClick = onCheckedChange?.let { { it(!checked) } },
        modifier = modifier,
        enabled = enabled,
        error = error,
        role = role,
        interactionSource = interactionSource,
    )
}

/** 26 dp radio: a well when off; when selected a glass lens holding a 10 dp pearl. */
@Composable
fun GlassRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: TonalTones = LocalGlassRole.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val host = if (onClick != null) Modifier.selectable(selected, source, null, enabled, Role.RadioButton, onClick) else Modifier
    Box(modifier.then(host).minimumInteractiveComponentSize(), contentAlignment = Alignment.Center) {
        Box(Modifier.size(BoxSize)) {
            GlassSurface(
                finish = if (selected) GlassFinish.Glass else GlassFinish.Well,
                modifier = Modifier.size(BoxSize),
                shape = SeaGlassShape.Round,
                role = role,
                enabled = enabled,
                interactionSource = source,
            )
            if (selected) {
                GlassSurface(
                    finish = GlassFinish.Pearl,
                    modifier = Modifier.offset(8.dp, 8.dp).size(10.dp),
                    shape = SeaGlassShape.Round,
                    role = role,
                    enabled = enabled,
                    interactionSource = source,
                    reactsTo = setOf(GlassState.Hover),
                )
            }
        }
    }
}
