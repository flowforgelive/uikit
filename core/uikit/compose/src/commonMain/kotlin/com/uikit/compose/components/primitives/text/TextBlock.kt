package com.uikit.compose.components.primitives.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.text.TextBlockConfig
import com.uikit.components.primitives.text.TextBlockVariant
import com.uikit.foundation.TextEmphasis

@Composable
fun TextBlock(
	text: String,
	variant: TextBlockVariant = TextBlockVariant.BodyLarge,
	emphasis: TextEmphasis = TextEmphasis.Auto,
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	TextBlockView(
		config =
			TextBlockConfig(
				text = text,
				variant = variant,
				emphasis = emphasis,
				testTag = testTag,
			),
		modifier = modifier,
	)
}
