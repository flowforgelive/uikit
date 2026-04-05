package com.uikit.compose.components.composites.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.composites.button.ButtonConfig
import com.uikit.components.composites.button.ButtonSize
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant

@Composable
fun Button(
	text: String,
	onClick: () -> Unit = {},
	variant: VisualVariant = VisualVariant.Solid,
	intent: ColorIntent = ColorIntent.Primary,
	size: ButtonSize = ButtonSize.Md,
	iconPosition: IconPosition = IconPosition.None,
	iconStart: (@Composable () -> Unit)? = null,
	iconEnd: (@Composable () -> Unit)? = null,
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
				iconPosition = iconPosition,
				hasIconStart = iconStart != null,
				hasIconEnd = iconEnd != null,
				disabled = disabled,
				loading = loading,
				testTag = testTag,
			),
		onClick = onClick,
		iconStart = iconStart,
		iconEnd = iconEnd,
		modifier = modifier,
	)
}
