package com.uikit.compose.components.blocks.panel

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.uikit.components.blocks.panel.PanelConfig
import com.uikit.components.blocks.panel.PanelSide
import com.uikit.components.blocks.panel.PanelStyleResolver
import com.uikit.components.blocks.panel.PanelVariant
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.Visibility

@Composable
fun PanelView(
	config: PanelConfig,
	onToggle: (() -> Unit)? = null,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) { PanelStyleResolver.resolve(config, tokens) }
	val shape = RoundedCornerShape(style.radius.dp)

	val animatedWidth by animateDpAsState(
		targetValue = style.width.dp,
		animationSpec = tween(
			durationMillis = style.durationMs,
		),
		label = "panel-width",
	)

	val animatedHeight by animateDpAsState(
		targetValue = style.height.dp,
		animationSpec = tween(
			durationMillis = style.durationMs,
		),
		label = "panel-height",
	)

	val surfaceContext = remember(config.surfaceLevel, style.bg) {
		SurfaceContext(level = config.surfaceLevel.ordinal, backgroundColor = style.bg)
	}

	val bgColor = parseColor(style.bg)
	val borderColor = parseColor(style.border)
	val isInset = config.variant == PanelVariant.Inset

	Box(
		modifier = modifier
			.then(
				if (isInset) {
					Modifier.padding(style.insetPadding.dp)
				} else {
					Modifier
				},
			)
			.then(
				if (config.elevated) {
					Modifier.shadow(tokens.shadows.elevationDp.dp, shape)
				} else {
					Modifier
				},
			)
			.then(
				if (style.isHorizontal) {
					Modifier.fillMaxWidth().height(animatedHeight)
				} else {
					Modifier.width(animatedWidth)
				},
			)
			.then(
				if (isInset) Modifier
				else if (style.isHorizontal) Modifier
				else Modifier.fillMaxHeight()
			)
			.clip(shape)
			.background(bgColor)
			.then(
				if (isInset) {
					Modifier.border(
						width = style.borderWidth.dp,
						color = borderColor,
						shape = shape,
					)
				} else {
					Modifier.drawWithContent {
						drawContent()
						val bw = style.borderWidth.dp.toPx()
						when (config.side) {
							PanelSide.Left -> drawLine(
								color = borderColor,
								start = Offset(size.width - bw / 2, 0f),
								end = Offset(size.width - bw / 2, size.height),
								strokeWidth = bw,
							)
							PanelSide.Right -> drawLine(
								color = borderColor,
								start = Offset(bw / 2, 0f),
								end = Offset(bw / 2, size.height),
								strokeWidth = bw,
							)
							PanelSide.Top -> drawLine(
								color = borderColor,
								start = Offset(0f, size.height - bw / 2),
								end = Offset(size.width, size.height - bw / 2),
								strokeWidth = bw,
							)
							PanelSide.Bottom -> drawLine(
								color = borderColor,
								start = Offset(0f, bw / 2),
								end = Offset(size.width, bw / 2),
								strokeWidth = bw,
							)
						}
					}
				},
			)
			.then(
				if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier
			)
			.testTag(config.testTag ?: config.id),
	) {
		CompositionLocalProvider(LocalSurfaceContext provides surfaceContext) {
			Column(
				modifier = Modifier
					.then(if (style.isHorizontal) Modifier.fillMaxWidth() else Modifier.fillMaxHeight())
					.verticalScroll(rememberScrollState()),
			) {
				content()
			}
		}
	}
}
