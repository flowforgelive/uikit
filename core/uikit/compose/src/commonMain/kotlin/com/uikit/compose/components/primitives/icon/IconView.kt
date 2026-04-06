package com.uikit.compose.components.primitives.icon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.uikit.components.primitives.icon.IconConfig
import com.uikit.components.primitives.icon.IconStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun IconView(
	config: IconConfig,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) {
		IconStyleResolver.resolve(config, tokens)
	}

	val iconColor = if (style.color != null) {
		parseColor(style.color!!)
	} else {
		LocalContentColor.current
	}

	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.size(style.size.dp)
			.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
			.testTag(config.testTag ?: config.id),
	) {
		CompositionLocalProvider(LocalContentColor provides iconColor) {
			content()
		}
	}
}
