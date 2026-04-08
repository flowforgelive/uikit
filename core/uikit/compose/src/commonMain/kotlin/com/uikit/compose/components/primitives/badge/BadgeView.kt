package com.uikit.compose.components.primitives.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.primitives.badge.BadgeConfig
import com.uikit.components.primitives.badge.BadgeStyleResolver
import com.uikit.components.primitives.badge.BadgeVariant
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun BadgeView(
	config: BadgeConfig,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) {
		BadgeStyleResolver.resolve(config, tokens)
	}

	val bgColor = parseColor(style.bgColor)
	val visibilityModifier = if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier
	val tagModifier = Modifier.testTag(config.testTag ?: config.id)

	if (config.variant == BadgeVariant.Dot) {
		Box(
			modifier = modifier
				.size(style.dotSize.dp)
				.clip(CircleShape)
				.background(bgColor)
				.then(visibilityModifier)
				.then(tagModifier),
		)
	} else {
		val pillShape = RoundedCornerShape(50)

		Box(
			contentAlignment = Alignment.Center,
			modifier = modifier
				.defaultMinSize(minWidth = style.minWidth.dp)
				.height(style.height.dp)
				.clip(pillShape)
				.background(bgColor)
				.padding(horizontal = style.paddingH.dp)
				.then(visibilityModifier)
				.then(tagModifier),
		) {
			if (config.showValue) {
				BasicText(
					text = config.displayValue,
					style = TextStyle(
						color = parseColor(style.textColor),
						fontSize = style.fontSize.sp,
						fontWeight = FontWeight(style.fontWeight),
						textAlign = TextAlign.Center,
						lineHeight = style.fontSize.sp,
					),
				)
			}
		}
	}
}
