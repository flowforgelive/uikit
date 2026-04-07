package com.uikit.compose.components.primitives.divider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.uikit.components.primitives.divider.DividerConfig
import com.uikit.components.primitives.divider.DividerOrientation
import com.uikit.components.primitives.divider.DividerStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun DividerView(
	config: DividerConfig,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) {
		DividerStyleResolver.resolve(config, tokens)
	}

	val isVertical = config.orientation == DividerOrientation.Vertical

	Box(
		modifier = modifier
			.then(
				if (isVertical) {
					Modifier
						.width(style.thickness.dp)
						.fillMaxHeight()
						.padding(top = style.insetStart.dp, bottom = style.insetEnd.dp)
				} else {
					Modifier
						.height(style.thickness.dp)
						.fillMaxWidth()
						.padding(start = style.insetStart.dp, end = style.insetEnd.dp)
				},
			)
			.background(parseColor(style.color))
			.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
			.testTag(config.testTag ?: config.id),
	)
}
