package com.uikit.compose.components.composites.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.Canvas
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.composites.chip.ChipConfig
import com.uikit.components.composites.chip.ChipStyleResolver
import com.uikit.compose.components.composites.Spinner
import com.uikit.compose.theme.LocalChildHoverState
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalFontFamily
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.StyledText
import com.uikit.compose.theme.LocalHazeState
import com.uikit.compose.theme.parseColor
import com.uikit.compose.theme.interactiveModifier
import com.uikit.compose.theme.rememberInteractiveState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import com.uikit.foundation.VisualVariant
import com.uikit.foundation.Visibility

@Composable
fun ChipView(
	config: ChipConfig,
	onAction: (String) -> Unit = {},
	onClick: (() -> Unit)? = null,
	onDismiss: (() -> Unit)? = null,
	leadingIcon: (@Composable () -> Unit)? = null,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val surfaceContext = LocalSurfaceContext.current
	val fontFamily = LocalFontFamily.current
	val style = remember(config, tokens, surfaceContext) {
		ChipStyleResolver.resolve(config, tokens, surfaceContext)
	}
	val shape = RoundedCornerShape(style.radius.dp)

	val isClickable = (onClick != null || config.actionRoute != null) && config.isInteractive
	val isGlass = config.variant == VisualVariant.Glass
	val hazeState = if (isGlass) LocalHazeState.current else null
	val useHaze = isGlass && hazeState != null
	val glassAlpha = if (isGlass && !useHaze) tokens.glass.bgOpacity.toFloat() else if (useHaze) 0f else 1f
	val glassBorderAlpha = if (isGlass) tokens.glass.borderOpacity.toFloat() else 1f
	val state = rememberInteractiveState(style.colors, tokens, isClickable, glassAlpha, glassBorderAlpha)

	Box(
		contentAlignment = Alignment.Center,
		modifier =
			modifier
				.height(style.sizes.height.dp)
				.then(
					if (useHaze) {
						val glassTint = parseColor(style.colors.bg).copy(alpha = tokens.glass.bgOpacity.toFloat())
						Modifier
							.clip(shape)
							.hazeEffect(state = hazeState!!) {
								blurRadius = tokens.glass.blurRadius.dp
								noiseFactor = 0f
								tints = listOf(HazeTint(glassTint))
							}
					} else Modifier,
				)
				.interactiveModifier(
					state = state,
					shape = shape,
					tokens = tokens,
					isInteractive = isClickable,
					clickable = isClickable || !config.isInteractive,
				) {
					if (isClickable) {
						onClick?.invoke()
						config.actionRoute?.let(onAction)
					}
				}
				.padding(start = style.sizes.paddingStart.dp, end = style.sizes.paddingEnd.dp)
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
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center,
				) {
					if (config.hasLeadingIcon && leadingIcon != null) {
						Box(
							modifier = Modifier.size(style.sizes.iconSize.dp),
							contentAlignment = Alignment.Center,
						) {
							leadingIcon()
						}
						Spacer(Modifier.width(style.sizes.iconGap.dp))
					}
					StyledText(
						text = config.text,
						fontSize = style.sizes.fontSize,
						fontWeight = style.sizes.fontWeight,
						letterSpacing = style.sizes.letterSpacing,
						lineHeight = style.sizes.lineHeight,
						color = state.currentText,
						fontFamily = fontFamily,
				)
					if (config.dismissible && onDismiss != null) {
						Spacer(Modifier.width(style.sizes.iconGap.dp))
						val dismissInteraction = remember { MutableInteractionSource() }
						val dismissHovered by dismissInteraction.collectIsHoveredAsState()
						val dismissBg = if (dismissHovered) state.currentText.copy(alpha = tokens.state.pressOpacity.toFloat()) else androidx.compose.ui.graphics.Color.Transparent
						Box(
							modifier = Modifier
								.size(style.sizes.closeButtonSize.dp)
								.clip(CircleShape)
								.background(dismissBg, CircleShape)
								.hoverable(dismissInteraction)
								.clickable(
									interactionSource = dismissInteraction,
									indication = ripple(),
									enabled = config.isInteractive,
								) { onDismiss() },
							contentAlignment = Alignment.Center,
						) {
							val iconSizeDp = style.sizes.closeIconSize.dp
							Canvas(modifier = Modifier.size(iconSizeDp)) {
								val pad = size.width * 0.15f
								val strokePx = size.width * 0.16f
								drawLine(
									color = state.currentText,
									start = Offset(pad, pad),
									end = Offset(size.width - pad, size.height - pad),
									strokeWidth = strokePx,
									cap = StrokeCap.Round,
								)
								drawLine(
									color = state.currentText,
									start = Offset(size.width - pad, pad),
									end = Offset(pad, size.height - pad),
									strokeWidth = strokePx,
									cap = StrokeCap.Round,
								)
							}
						}
					}
				}
			}
		}
	}
}
