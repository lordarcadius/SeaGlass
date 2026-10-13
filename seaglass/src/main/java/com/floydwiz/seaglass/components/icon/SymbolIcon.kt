package com.floydwiz.seaglass.components.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.theme.symbolsFamily

/**
 * Material Symbols Rounded glyph drawn by ligature name.
 * - [fill] and [weight] map to the FILL and wght axes; opsz follows [size] clamped to 20–48.
 * - [size] is in dp and ignores the system font scale.
 */
@Composable
fun SymbolIcon(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    fill: Boolean = false,
    weight: Int = 400,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null,
) {
    val family = remember(fill, weight, size) { symbolsFamily(fill, weight, size.value.coerceIn(20f, 48f)) }
    val sizeSp = with(LocalDensity.current) { size.toSp() }
    val semantics = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier.semantics { hideFromAccessibility() }
    }
    Text(
        text = name,
        modifier = modifier.size(size).then(semantics),
        color = tint,
        fontSize = sizeSp,
        fontFamily = family,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        style = TextStyle(
            lineHeight = sizeSp,
            letterSpacing = 0.sp,
            fontFeatureSettings = "'liga'",
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both),
            platformStyle = PlatformTextStyle(includeFontPadding = false),
        ),
    )
}
