package com.uikit.compose.components.atoms.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.ripple
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.atoms.button.ButtonConfig
import com.uikit.components.atoms.button.ButtonStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalKeyboardNavigationMode
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.Visibility

@Composable
fun ButtonView(
	config: ButtonConfig,
	onAction: (String) -> Unit = {},
	onClick: (() -> Unit)? = null,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val surfaceContext = LocalSurfaceContext.current
	val style = remember(config, tokens, surfaceContext) {
		ButtonStyleResolver.resolve(config, tokens, surfaceContext)
	}
	val shape = RoundedCornerShape(style.radius.dp)

	val interactionSource = remember { MutableInteractionSource() }
	val isHovered by interactionSource.collectIsHoveredAsState()
	val isPressed by interactionSource.collectIsPressedAsState()
	val isFocused by interactionSource.collectIsFocusedAsState()
	val keyboardMode = LocalKeyboardNavigationMode.current

	val active = (isHovered || isPressed) && config.isInteractive
	val focused = isFocused && keyboardMode.value
	val currentBg = if (active) style.colors.bgHover else style.colors.bg
	val currentBorder = when {
		focused -> tokens.color.focusRing
		active -> style.colors.borderHover
		else -> style.colors.border
	}
	val currentText = if (active) style.colors.textHover else style.colors.text

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
				.padding(horizontal = style.sizes.paddingH.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
	) {
		if (config.loading) {
			CircularProgressIndicator(
				modifier = Modifier.size(style.sizes.iconSize.dp),
				color = parseColor(currentText),
				strokeWidth = 2.dp,
			)
		} else {
			Text(
				text = config.text,
				fontSize = style.sizes.fontSize.sp,
				fontWeight = FontWeight(style.sizes.fontWeight),
				letterSpacing = style.sizes.letterSpacing.sp,
				color = parseColor(currentText),
			)
		}
	}
}
