package com.uikit.compose.components.composites.chip

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.composites.chip.ChipConfig
import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.VisualVariant

@Composable
fun Chip(
	text: String,
	onClick: () -> Unit = {},
	onDismiss: (() -> Unit)? = null,
	variant: VisualVariant = VisualVariant.Soft,
	intent: ColorIntent = ColorIntent.Neutral,
	size: ComponentSize = ComponentSize.Md,
	leadingIcon: (@Composable () -> Unit)? = null,
	dismissible: Boolean = false,
	selected: Boolean = false,
	disabled: Boolean = false,
	loading: Boolean = false,
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	ChipView(
		config = ChipConfig(
			text = text,
			variant = variant,
			intent = intent,
			size = size,
			hasLeadingIcon = leadingIcon != null,
			dismissible = dismissible,
			selected = selected,
			disabled = disabled,
			loading = loading,
			testTag = testTag,
		),
		onClick = onClick,
		onDismiss = onDismiss,
		leadingIcon = leadingIcon,
		modifier = modifier,
	)
}
