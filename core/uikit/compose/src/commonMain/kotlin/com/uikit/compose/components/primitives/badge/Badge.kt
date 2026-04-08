package com.uikit.compose.components.primitives.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.primitives.badge.BadgeConfig
import com.uikit.components.primitives.badge.BadgeVariant
import com.uikit.foundation.ColorIntent

@Composable
fun Badge(
	variant: BadgeVariant = BadgeVariant.Dot,
	value: Int = 0,
	maxValue: Int = 99,
	intent: ColorIntent = ColorIntent.Danger,
	modifier: Modifier = Modifier,
) {
	BadgeView(
		config = BadgeConfig(
			variant = variant,
			value = value,
			maxValue = maxValue,
			intent = intent,
		),
		modifier = modifier,
	)
}
