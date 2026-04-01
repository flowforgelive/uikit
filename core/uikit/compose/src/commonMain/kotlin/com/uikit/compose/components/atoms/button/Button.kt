package com.uikit.compose.components.atoms.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.atoms.button.ButtonConfig
import com.uikit.components.atoms.button.ButtonSize
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.VisualVariant

@Composable
fun Button(
	text: String,
	onClick: () -> Unit = {},
	variant: VisualVariant = VisualVariant.Solid,
	intent: ColorIntent = ColorIntent.Primary,
	size: ButtonSize = ButtonSize.Md,
	disabled: Boolean = false,
	loading: Boolean = false,
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	ButtonView(
		config =
			ButtonConfig(
				text = text,
				variant = variant,
				intent = intent,
				size = size,
				disabled = disabled,
				loading = loading,
				testTag = testTag,
			),
		onClick = onClick,
		modifier = modifier,
	)
}
