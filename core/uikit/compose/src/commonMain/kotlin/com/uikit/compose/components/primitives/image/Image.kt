package com.uikit.compose.components.primitives.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.image.ImageConfig
import com.uikit.components.primitives.image.ImageFit
import com.uikit.components.primitives.image.ImageLoading

@Composable
fun Image(
	src: String = "",
	alt: String = "",
	width: Double = 0.0,
	height: Double = 0.0,
	objectFit: ImageFit = ImageFit.Cover,
	cornerRadius: Double? = null,
	showBorder: Boolean = false,
	placeholder: String? = null,
	fallback: String? = null,
	loading: ImageLoading = ImageLoading.Eager,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit = {},
) {
	ImageView(
		config = ImageConfig(
			src = src,
			alt = alt,
			width = width,
			height = height,
			objectFit = objectFit,
			cornerRadius = cornerRadius,
			showBorder = showBorder,
			placeholder = placeholder,
			fallback = fallback,
			loading = loading,
		),
		modifier = modifier,
		content = content,
	)
}
