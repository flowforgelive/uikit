package com.uikit.compose.components.atoms.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.atoms.text.TextBlockConfig
import com.uikit.components.atoms.text.TextBlockVariant

@Composable
fun TextBlock(
	text: String,
	variant: TextBlockVariant = TextBlockVariant.Body,
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	TextBlockView(
		config =
			TextBlockConfig(
				text = text,
				variant = variant,
				testTag = testTag,
			),
		modifier = modifier,
	)
}
