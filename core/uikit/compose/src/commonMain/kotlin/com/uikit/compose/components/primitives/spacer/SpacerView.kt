package com.uikit.compose.components.primitives.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.uikit.components.primitives.spacer.SpacerAxis
import com.uikit.components.primitives.spacer.SpacerConfig
import com.uikit.components.primitives.spacer.SpacerStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.foundation.Visibility

@Composable
fun SpacerView(
	config: SpacerConfig,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) {
		SpacerStyleResolver.resolve(config, tokens)
	}

	val sizeModifier = when {
		style.isFlexible && config.axis == SpacerAxis.Horizontal -> Modifier.fillMaxWidth()
		style.isFlexible -> Modifier.fillMaxHeight()
		config.axis == SpacerAxis.Vertical -> Modifier.height(style.size.dp)
		else -> Modifier.width(style.size.dp)
	}

	Spacer(
		modifier = modifier
			.then(sizeModifier)
			.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
			.testTag(config.testTag ?: config.id),
	)
}
