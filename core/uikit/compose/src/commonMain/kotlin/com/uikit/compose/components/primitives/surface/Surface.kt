package com.uikit.compose.components.primitives.surface

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.surface.SurfaceConfig
import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.components.primitives.surface.SurfaceShape
import com.uikit.foundation.VisualVariant

@Composable
fun Surface(
	variant: VisualVariant = VisualVariant.Solid,
	level: SurfaceLevel = SurfaceLevel.Level2,
	shape: SurfaceShape = SurfaceShape.Md,
	elevated: Boolean = false,
	clickable: Boolean = false,
	hoverable: Boolean = false,
	onClick: (() -> Unit)? = null,
	testTag: String? = null,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	SurfaceView(
		config =
			SurfaceConfig(
				variant = variant,
				level = level,
				shape = shape,
				elevated = elevated,
				clickable = clickable,			hoverable = hoverable,				testTag = testTag,
			),
		onClick = onClick,
		modifier = modifier,
		content = content,
	)
}
