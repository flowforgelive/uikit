package com.uikit.compose.components.primitives.glass

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.glass.GlassSurfaceConfig
import com.uikit.components.primitives.surface.SurfaceShape
import com.uikit.foundation.GlassVariant

@Composable
fun GlassSurface(
	variant: GlassVariant = GlassVariant.Regular,
	tintColor: String = "",
	shape: SurfaceShape = SurfaceShape.Md,
	elevated: Boolean = false,
	testTag: String? = null,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	GlassSurfaceView(
		config = GlassSurfaceConfig(
			variant = variant,
			tintColor = tintColor,
			shape = shape,
			elevated = elevated,
			testTag = testTag,
		),
		modifier = modifier,
		content = content,
	)
}
