package com.uikit.compose.components.atoms.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.uikit.foundation.Visibility
import com.uikit.components.atoms.text.TextBlockConfig
import com.uikit.components.atoms.text.TextBlockStyleResolver
import com.uikit.components.atoms.text.TextBlockVariant
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor

@Composable
fun TextBlockView(
    config: TextBlockConfig,
    modifier: Modifier = Modifier,
) {
    if (config.visibility == Visibility.Gone) return

    val tokens = LocalDesignTokens.current
    val style = remember(config, tokens) { TextBlockStyleResolver.resolve(config, tokens) }

    Text(
        text = config.text,
        color = parseColor(style.color),
        fontSize = style.fontSize.sp,
        fontWeight = FontWeight(style.fontWeight),
        lineHeight = style.lineHeight.sp,
        modifier = modifier
            .then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
            .testTag(config.testTag ?: config.id),
    )
}

@Composable
fun TextBlockView(
    text: String,
    variant: TextBlockVariant = TextBlockVariant.Body,
    modifier: Modifier = Modifier,
) {
    TextBlockView(
        config = TextBlockConfig(
            id = "",
            text = text,
            variant = variant,
            testTag = null,
            visibility = Visibility.Visible,
        ),
        modifier = modifier,
    )
}
