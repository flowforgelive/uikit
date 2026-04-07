package com.uikit.compose.components.composites.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import com.uikit.compose.components.composites.Spinner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.uikit.components.composites.button.ButtonConfig
import com.uikit.components.composites.button.ButtonStyleResolver
import com.uikit.compose.theme.LocalChildHoverState
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalFontFamily
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.StyledText
import com.uikit.compose.theme.IconSlot
import com.uikit.compose.theme.IconTextLayout
import com.uikit.compose.theme.interactiveModifier
import com.uikit.compose.theme.rememberInteractiveState
import com.uikit.foundation.IconPosition
import com.uikit.foundation.Visibility

@Composable
fun ButtonView(
	config: ButtonConfig,
	onAction: (String) -> Unit = {},
	onClick: (() -> Unit)? = null,
	iconStart: (@Composable () -> Unit)? = null,
	iconEnd: (@Composable () -> Unit)? = null,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val surfaceContext = LocalSurfaceContext.current
	val fontFamily = LocalFontFamily.current
	val style = remember(config, tokens, surfaceContext) {
		ButtonStyleResolver.resolve(config, tokens, surfaceContext)
	}
	val shape = RoundedCornerShape(style.radius.dp)

	val state = rememberInteractiveState(style.colors, tokens, config.isInteractive)

	val sizeModifier = if (config.isIconOnly) {
		Modifier.size(style.sizes.height.dp)
	} else {
		Modifier.defaultMinSize(minHeight = style.sizes.height.dp)
	}

	Box(
		contentAlignment = Alignment.Center,
		modifier =
			modifier
				.then(sizeModifier)
				.interactiveModifier(
					state = state,
					shape = shape,
					tokens = tokens,
					isInteractive = config.isInteractive,
				) {
					if (config.isInteractive) {
						onClick?.invoke()
						config.actionRoute?.let(onAction)
					}
				}
				.padding(horizontal = style.sizes.paddingH.dp, vertical = style.sizes.paddingV.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
	) {
		CompositionLocalProvider(
			LocalContentColor provides state.currentText,
			LocalChildHoverState provides state.childHoverState,
		) {
			if (config.loading) {
				Spinner(
					size = style.sizes.iconSize.dp,
					color = state.currentText,
					strokeWidth = tokens.spinnerStrokeWidth.dp,
					durationMs = tokens.motion.durationSpinner,
				)
			} else {
				ButtonContent(
					config = config,
					iconSize = style.sizes.iconSize.dp,
					iconGap = style.sizes.iconGap.dp,
					fontSize = style.sizes.fontSize,
					fontWeight = style.sizes.fontWeight,
					letterSpacing = style.sizes.letterSpacing,
					lineHeight = style.sizes.lineHeight,
					fontFamily = fontFamily,
					textColor = state.currentText,
					iconStart = iconStart,
					iconEnd = iconEnd,
				)
			}
		}
	}
}

@Composable
private fun ButtonContent(
	config: ButtonConfig,
	iconSize: androidx.compose.ui.unit.Dp,
	iconGap: androidx.compose.ui.unit.Dp,
	fontSize: Double,
	fontWeight: Int,
	letterSpacing: Double,
	lineHeight: Double,
	fontFamily: androidx.compose.ui.text.font.FontFamily,
	textColor: androidx.compose.ui.graphics.Color,
	iconStart: (@Composable () -> Unit)?,
	iconEnd: (@Composable () -> Unit)?,
) {
	val textContent: @Composable () -> Unit = {
		StyledText(
			text = config.text,
			fontSize = fontSize,
			fontWeight = fontWeight,
			letterSpacing = letterSpacing,
			lineHeight = lineHeight,
			color = textColor,
			fontFamily = fontFamily,
		)
	}

	if (config.isIconOnly) {
		val icon = iconStart ?: iconEnd
		if (icon != null) {
			IconSlot(iconSize) { icon() }
		}
		return
	}

	if (!config.hasIcon) {
		textContent()
		return
	}

	val isVertical = config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom

	if (isVertical) {
		val icon = iconStart ?: iconEnd
		if (icon != null) {
			IconTextLayout(
				iconPosition = config.iconPosition,
				iconGap = iconGap,
				icon = { IconSlot(iconSize) { icon() } },
				text = textContent,
			)
		}
	} else {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
		) {
			iconStart?.let { slot ->
				IconSlot(iconSize) { slot() }
				Spacer(Modifier.width(iconGap))
			}
			textContent()
			iconEnd?.let { slot ->
				Spacer(Modifier.width(iconGap))
				IconSlot(iconSize) { slot() }
			}
		}
	}
}
