package com.uikit.compose.components.primitives.image

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.uikit.components.primitives.image.ImageConfig
import com.uikit.components.primitives.image.ImageStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun ImageView(
	config: ImageConfig,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit = {},
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) {
		ImageStyleResolver.resolve(config, tokens)
	}

	val shape = remember(style.cornerRadius) {
		RoundedCornerShape(style.cornerRadius.dp)
	}

	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.then(if (style.width > 0) Modifier.width(style.width.dp) else Modifier)
			.then(if (style.height > 0) Modifier.height(style.height.dp) else Modifier)
			.then(
				if (style.cornerRadius > 0) Modifier.clip(shape)
				else Modifier,
			)
			.then(
				if (style.borderColor != null) Modifier.border(
					width = style.borderWidth.dp,
					color = parseColor(style.borderColor!!),
					shape = shape,
				) else Modifier,
			)
			.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
			.testTag(config.testTag ?: config.id),
	) {
		if (config.src.isNotEmpty()) {
			AsyncImageContent(
				src = config.src,
				fallback = style.fallback,
				contentDescription = config.alt,
				contentScale = config.objectFit.toContentScale(),
				modifier = Modifier.fillMaxSize(),
			)
		}
		content()
	}
}
