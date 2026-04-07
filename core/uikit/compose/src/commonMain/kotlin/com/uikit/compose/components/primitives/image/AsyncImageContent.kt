package com.uikit.compose.components.primitives.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.uikit.components.primitives.image.ImageFit

@Composable
internal expect fun AsyncImageContent(
	src: String,
	fallback: String?,
	contentDescription: String,
	contentScale: ContentScale,
	modifier: Modifier,
)

internal fun ImageFit.toContentScale(): ContentScale = when (this) {
	ImageFit.Cover -> ContentScale.Crop
	ImageFit.Contain -> ContentScale.Fit
	ImageFit.Fill -> ContentScale.FillBounds
	ImageFit.None -> ContentScale.None
	ImageFit.ScaleDown -> ContentScale.Inside
}
