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
	text: String = "",
	onClick: () -> Unit = {},
	variant: VisualVariant = VisualVariant.Solid,
	intent: ColorIntent = ColorIntent.Primary,
	size: ButtonSize = ButtonSize.Md,
	icon: (@Composable () -> Unit)? = null,
	iconPosition: IconPosition = IconPosition.None,
	iconStart: (@Composable () -> Unit)? = null,
	iconEnd: (@Composable () -> Unit)? = null,
	disabled: Boolean = false,
	loading: Boolean = false,
	ariaLabel: String? = null,
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	val effectivePosition = if (icon != null && iconPosition == IconPosition.None) IconPosition.Start else iconPosition
	val effectiveIconStart = if (icon != null && effectivePosition != IconPosition.End) icon else iconStart
	val effectiveIconEnd = if (icon != null && effectivePosition == IconPosition.End) icon else iconEnd

	ButtonView(
		config =
			ButtonConfig(
				text = text,
				variant = variant,
				intent = intent,
				size = size,
				iconPosition = effectivePosition,
				hasIconStart = effectiveIconStart != null,
				hasIconEnd = effectiveIconEnd != null,
				disabled = disabled,
				loading = loading,
				testTag = testTag,
				ariaLabel = ariaLabel,
			),
		onClick = onClick,
		iconStart = effectiveIconStart,
		iconEnd = effectiveIconEnd,
		modifier = modifier,
	)
}
