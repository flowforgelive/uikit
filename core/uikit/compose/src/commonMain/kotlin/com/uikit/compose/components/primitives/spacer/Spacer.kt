package com.uikit.compose.components.primitives.spacer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.spacer.SpacerAxis
import com.uikit.components.primitives.spacer.SpacerConfig

@Composable
fun Spacer(
	size: Double = 0.0,
	axis: SpacerAxis = SpacerAxis.Vertical,
	modifier: Modifier = Modifier,
) {
	SpacerView(
		config = SpacerConfig(
			size = size,
			axis = axis,
		),
		modifier = modifier,
	)
}
