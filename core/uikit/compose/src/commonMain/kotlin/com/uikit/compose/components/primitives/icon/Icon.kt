package com.uikit.compose.components.primitives.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.icon.IconConfig
import com.uikit.foundation.ComponentSize

@Composable
fun Icon(
	size: ComponentSize = ComponentSize.Md,
	customSize: Double = 0.0,
	color: String? = null,
	name: String = "",
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	IconView(
		config = IconConfig(
			name = name,
			size = size,
			customSize = customSize,
			color = color,
		),
		modifier = modifier,
		content = content,
	)
}
