package com.uikit.compose.components.composites.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
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
import com.uikit.compose.theme.LocalKeyboardNavigationMode
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.parseColor
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

	val interactionSource = remember { MutableInteractionSource() }
	val isHovered by interactionSource.collectIsHoveredAsState()
	val isFocused by interactionSource.collectIsFocusedAsState()
	val keyboardMode = LocalKeyboardNavigationMode.current

	// Anti-stacking hover
	val parentChildHover = LocalChildHoverState.current
	LaunchedEffect(isHovered) {
		parentChildHover.value = isHovered
	}
	val childHoverState = remember { mutableStateOf(false) }

	val active = isHovered && !childHoverState.value && config.isInteractive
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
				.height(style.sizes.height.dp)
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
							.alpha(tokens.state.disabledOpacity.toFloat())
					} else {
						Modifier
					},
				)
				.padding(start = style.sizes.paddingStart.dp, end = style.sizes.paddingEnd.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
	) {
		CompositionLocalProvider(
			LocalContentColor provides textColor,
			LocalChildHoverState provides childHoverState,
		) {
			if (config.loading) {
				Spinner(
					size = style.sizes.iconSize.dp,
					color = textColor,
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
					BasicText(
						text = config.text,
						style = TextStyle(
							fontSize = style.sizes.fontSize.sp,
							fontWeight = FontWeight(style.sizes.fontWeight),
							letterSpacing = style.sizes.letterSpacing.sp,
							lineHeight = style.sizes.lineHeight.sp,
							color = textColor,
							fontFamily = fontFamily,
						),
					)
					if (config.dismissible && onDismiss != null) {
						Spacer(Modifier.width(style.sizes.iconGap.dp))
						val dismissInteraction = remember { MutableInteractionSource() }
						val dismissHovered by dismissInteraction.collectIsHoveredAsState()
						val dismissBg = if (dismissHovered) textColor.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent
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
									color = textColor,
									start = Offset(pad, pad),
									end = Offset(size.width - pad, size.height - pad),
									strokeWidth = strokePx,
									cap = StrokeCap.Round,
								)
								drawLine(
									color = textColor,
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
