package com.uikit.compose.components.atoms.segmentedcontrol

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlConfig
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun SegmentedControlView(
	config: SegmentedControlConfig,
	onSelectionChange: (String) -> Unit = {},
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(tokens) { SegmentedControlStyleResolver.resolve(tokens) }

	val selectedIndex = config.options.indexOfFirst { it.id == config.selectedId }.coerceAtLeast(0)
	val optionCount = config.options.size

	val animatedFraction by animateFloatAsState(
		targetValue = if (optionCount > 0) selectedIndex.toFloat() / optionCount else 0f,
		animationSpec = tween(durationMillis = 200),
	)

	val thumbColor = parseColor(style.colors.thumbBg)
	val thumbRadiusPx = style.sizes.thumbRadius.toFloat()

	Box(
		modifier =
			modifier
				.height(style.sizes.height.dp)
				.clip(RoundedCornerShape(style.sizes.radius.dp))
				.background(parseColor(style.colors.trackBg))
				.padding(style.sizes.trackPadding.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id)
				.drawBehind {
					if (optionCount > 0) {
						val segmentWidth = size.width / optionCount
						drawRoundRect(
							color = thumbColor,
							topLeft = Offset(x = animatedFraction * size.width, y = 0f),
							size = Size(width = segmentWidth, height = size.height),
							cornerRadius = CornerRadius(thumbRadiusPx * density, thumbRadiusPx * density),
						)
					}
				},
	) {
		Row(modifier = Modifier.matchParentSize()) {
			config.options.forEach { option ->
				val isActive = option.id == config.selectedId
				Box(
					modifier =
						Modifier
							.weight(1f)
							.fillMaxHeight()
							.clickable(
								interactionSource = remember { MutableInteractionSource() },
								indication = null,
							) { onSelectionChange(option.id) },
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = option.label,
						fontSize = style.sizes.fontSize.sp,
						color =
							parseColor(
								if (isActive) style.colors.textActive else style.colors.textInactive,
							),
						textAlign = TextAlign.Center,
					)
				}
			}
		}
	}
}

@Composable
fun SegmentedControlView(
	options: List<Pair<String, String>>,
	selectedId: String,
	onSelectionChange: (String) -> Unit = {},
	modifier: Modifier = Modifier,
) {
	SegmentedControlView(
		config =
			SegmentedControlConfig(
				id = "",
				options =
					options
						.map {
							com.uikit.components.atoms.segmentedcontrol
								.SegmentedControlOption(it.first, it.second)
						}.toTypedArray(),
				selectedId = selectedId,
			),
		onSelectionChange = onSelectionChange,
		modifier = modifier,
	)
}
