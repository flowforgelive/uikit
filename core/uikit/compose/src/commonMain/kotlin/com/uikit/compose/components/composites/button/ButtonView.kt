package com.uikit.compose.components.composites.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import com.uikit.compose.components.composites.Spinner
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.composites.button.ButtonConfig
import com.uikit.components.composites.button.ButtonStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalFontFamily
import com.uikit.compose.theme.LocalKeyboardNavigationMode
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.parseColor
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

	val interactionSource = remember { MutableInteractionSource() }
	val isHovered by interactionSource.collectIsHoveredAsState()
	val isFocused by interactionSource.collectIsFocusedAsState()
	val keyboardMode = LocalKeyboardNavigationMode.current

	val active = isHovered && config.isInteractive
	val focused = isFocused && keyboardMode.value
	val currentBg = if (active) style.colors.bgHover else style.colors.bg
	val currentBorder = when {
		focused -> tokens.color.focusRing
		active -> style.colors.borderHover
		else -> style.colors.border
	}
	val currentText = if (active) style.colors.textHover else style.colors.text
	val textColor = parseColor(currentText)

	Box(
		contentAlignment = Alignment.Center,
		modifier =
			modifier
				.defaultMinSize(minHeight = style.sizes.height.dp)
				.clip(shape)
				.background(parseColor(currentBg))
				.border(
					width = if (focused) tokens.focusRingWidth.dp else tokens.borderWidth.dp,
					color = parseColor(currentBorder),
					shape = shape,
				)
				.hoverable(interactionSource)
				.clickable(
					interactionSource = interactionSource,
					indication = if (config.isInteractive) ripple() else null,
					enabled = true,
					role = androidx.compose.ui.semantics.Role.Button,
				) {
					if (config.isInteractive) {
						onClick?.invoke()
						config.actionRoute?.let(onAction)
					}
				}
				.then(
					if (!config.isInteractive) {
						Modifier
							.semantics { disabled() }
							.alpha(0.6f)
					} else {
						Modifier
					},
				)
				.padding(horizontal = style.sizes.paddingH.dp, vertical = style.sizes.paddingV.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
	) {
		CompositionLocalProvider(LocalContentColor provides textColor) {
			if (config.loading) {
				Spinner(
					size = style.sizes.iconSize.dp,
					color = textColor,
					strokeWidth = tokens.spinnerStrokeWidth.dp,
					durationMs = (tokens.motion.durationSlower * 1.5).toInt(),
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
					textColor = textColor,
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
		BasicText(
			text = config.text,
			style = TextStyle(
				fontSize = fontSize.sp,
				fontWeight = FontWeight(fontWeight),
				letterSpacing = letterSpacing.sp,
				lineHeight = lineHeight.sp,
				color = textColor,
				fontFamily = fontFamily,
			),
		)
	}

	val iconSlot: @Composable ((@Composable () -> Unit)) -> Unit = { slot ->
		Box(modifier = Modifier.size(iconSize), contentAlignment = Alignment.Center) {
			slot()
		}
	}

	val isVertical = config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom

	if (!config.hasIcon) {
		textContent()
		return
	}

	if (isVertical) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			val icon = iconStart ?: iconEnd
			if (icon != null) {
				if (config.iconPosition == IconPosition.Top) {
					iconSlot(icon)
					Spacer(Modifier.height(iconGap))
					textContent()
				} else {
					textContent()
					Spacer(Modifier.height(iconGap))
					iconSlot(icon)
				}
			}
		}
	} else {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
		) {
			iconStart?.let { slot ->
				iconSlot(slot)
				Spacer(Modifier.width(iconGap))
			}
			textContent()
			iconEnd?.let { slot ->
				Spacer(Modifier.width(iconGap))
				iconSlot(slot)
			}
		}
	}
}
