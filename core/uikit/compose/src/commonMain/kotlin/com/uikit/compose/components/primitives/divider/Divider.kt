package com.uikit.compose.components.primitives.divider

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.divider.DividerConfig
import com.uikit.components.primitives.divider.DividerOrientation

@Composable
fun Divider(
	orientation: DividerOrientation = DividerOrientation.Horizontal,
	thickness: Double = 0.0,
	color: String? = null,
	insetStart: Double = 0.0,
	insetEnd: Double = 0.0,
	modifier: Modifier = Modifier,
) {
	DividerView(
		config = DividerConfig(
			orientation = orientation,
			thickness = thickness,
			color = color,
			insetStart = insetStart,
			insetEnd = insetEnd,
		),
		modifier = modifier,
	)
}
