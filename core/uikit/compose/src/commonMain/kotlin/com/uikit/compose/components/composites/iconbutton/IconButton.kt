package com.uikit.compose.components.composites.iconbutton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.composites.iconbutton.IconButtonConfig
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.VisualVariant

@Composable
fun IconButton(
	icon: @Composable () -> Unit,
	onClick: () -> Unit = {},
	variant: VisualVariant = VisualVariant.Solid,
	intent: ColorIntent = ColorIntent.Primary,
	size: ComponentSize = ComponentSize.Md,
	disabled: Boolean = false,
	loading: Boolean = false,
	ariaLabel: String? = null,
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	IconButtonView(
		config =
			IconButtonConfig(
				variant = variant,
				intent = intent,
				size = size,
				disabled = disabled,
				loading = loading,
				ariaLabel = ariaLabel,
				testTag = testTag,
			),
		icon = icon,
		onClick = onClick,
		modifier = modifier,
	)
}
