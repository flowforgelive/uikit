package com.uikit.compose.components.atoms.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.uikit.components.atoms.text.TextBlockConfig
import com.uikit.components.atoms.text.TextBlockStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalFontFamily
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun TextBlockView(
	config: TextBlockConfig,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val fontFamily = LocalFontFamily.current
	val style = remember(config, tokens) { TextBlockStyleResolver.resolve(config, tokens) }

	BasicText(
		text = config.text,
		style = TextStyle(
			color = parseColor(style.color),
			fontSize = style.fontSize.sp,
			fontWeight = FontWeight(style.fontWeight),
			lineHeight = style.lineHeight.sp,
			letterSpacing = style.letterSpacing.sp,
			fontFamily = fontFamily,
		),
		modifier =
			modifier
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
	)
}
