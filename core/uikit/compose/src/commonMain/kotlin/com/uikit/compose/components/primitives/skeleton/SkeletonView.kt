package com.uikit.compose.components.primitives.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.uikit.components.primitives.skeleton.SkeletonConfig
import com.uikit.components.primitives.skeleton.SkeletonShape
import com.uikit.components.primitives.skeleton.SkeletonStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun SkeletonView(
	config: SkeletonConfig,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) {
		SkeletonStyleResolver.resolve(config, tokens)
	}

	val shape = RoundedCornerShape(style.cornerRadius.dp)
	val baseColor = parseColor(style.baseColor)
	val highlightColor = parseColor(style.highlightColor)

	val sizeModifier = when {
		config.shape == SkeletonShape.Circle && style.width > 0.0 ->
			Modifier.size(style.width.dp)
		style.width > 0.0 ->
			Modifier.width(style.width.dp).height(style.height.dp)
		style.height > 0.0 ->
			Modifier.fillMaxWidth().height(style.height.dp)
		else ->
			Modifier.fillMaxWidth().defaultMinSize(minHeight = 16.dp)
	}

	if (style.animate) {
		val transition = rememberInfiniteTransition()
		val progress by transition.animateFloat(
			initialValue = -1f,
			targetValue = 2f,
			animationSpec = infiniteRepeatable(
				animation = tween(
					durationMillis = style.durationMs,
					easing = LinearEasing,
				),
				repeatMode = RepeatMode.Restart,
			),
		)

		Box(
			modifier = modifier
				.then(sizeModifier)
				.clip(shape)
				.background(baseColor)
				.drawWithContent {
					drawContent()
					val shimmerWidth = size.width * 0.4f
					val start = progress * size.width
					val brush = Brush.linearGradient(
						colors = listOf(
							Color.Transparent,
							highlightColor,
							Color.Transparent,
						),
						start = Offset(start - shimmerWidth, 0f),
						end = Offset(start + shimmerWidth, 0f),
					)
					drawRect(brush)
				}
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
		)
	} else {
		Box(
			modifier = modifier
				.then(sizeModifier)
				.clip(shape)
				.background(baseColor)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
		)
	}
}
