package com.uikit.compose.components.primitives.skeleton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.skeleton.SkeletonConfig
import com.uikit.components.primitives.skeleton.SkeletonShape

@Composable
fun Skeleton(
	shape: SkeletonShape = SkeletonShape.Rectangle,
	width: Double = 0.0,
	height: Double = 0.0,
	cornerRadius: Double? = null,
	animate: Boolean = true,
	modifier: Modifier = Modifier,
) {
	SkeletonView(
		config = SkeletonConfig(
			shape = shape,
			width = width,
			height = height,
			cornerRadius = cornerRadius,
			animate = animate,
		),
		modifier = modifier,
	)
}
