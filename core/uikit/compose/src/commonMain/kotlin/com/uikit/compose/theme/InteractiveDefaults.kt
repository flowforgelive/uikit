package com.uikit.compose.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.uikit.foundation.ColorSet
import com.uikit.tokens.DesignTokens

/**
 * Resolved interactive visual state for Compose interactive components.
 */
data class InteractiveVisualState(
	val interactionSource: MutableInteractionSource,
	val childHoverState: MutableState<Boolean>,
	val active: Boolean,
	val pressed: Boolean,
	val focused: Boolean,
	val currentBg: Color,
	val currentBorder: Color,
	val currentText: Color,
)

/**
 * Sets up interactive state with anti-stacking hover, keyboard focus detection,
 * and color resolution based on hover/focus state.
 *
 * Replaces ~20 lines of duplicated state management in ButtonView, ChipView, etc.
 */
@Composable
fun rememberInteractiveState(
	colors: ColorSet,
	tokens: DesignTokens,
	isInteractive: Boolean,
	glassAlpha: Float = 1f,
	glassBorderAlpha: Float = 1f,
): InteractiveVisualState {
	val interactionSource = remember { MutableInteractionSource() }
	val isHovered by interactionSource.collectIsHoveredAsState()
	val isPressed by interactionSource.collectIsPressedAsState()
	val isFocused by interactionSource.collectIsFocusedAsState()
	val keyboardMode = LocalKeyboardNavigationMode.current

	val parentChildHover = LocalChildHoverState.current
	LaunchedEffect(isHovered) {
		parentChildHover.value = isHovered
	}

	val childHoverState = remember { mutableStateOf(false) }

	val active = isHovered && !childHoverState.value && isInteractive
	val pressed = isPressed && isInteractive
	val focused = isFocused && keyboardMode.value

	val currentBg = parseColor(
		when {
			pressed -> colors.bgActive
			active -> colors.bgHover
			else -> colors.bg
		},
	).let { if (glassAlpha < 1f) it.copy(alpha = it.alpha * glassAlpha) else it }
	val currentBorder = parseColor(
		when {
			focused -> tokens.color.focusRing
			pressed -> colors.borderActive
			active -> colors.borderHover
			else -> colors.border
		},
	).let { if (glassBorderAlpha < 1f && !focused) it.copy(alpha = glassBorderAlpha) else it }
	val currentText = parseColor(
		when {
			pressed -> colors.textActive
			active -> colors.textHover
			else -> colors.text
		},
	)

	return InteractiveVisualState(
		interactionSource = interactionSource,
		childHoverState = childHoverState,
		active = active,
		pressed = pressed,
		focused = focused,
		currentBg = currentBg,
		currentBorder = currentBorder,
		currentText = currentText,
	)
}

/**
 * Applies interactive modifier chain: clip, background, border, hoverable,
 * clickable with ripple, disabled semantics + opacity.
 *
 * Replaces ~25 lines of duplicated modifier chaining in ButtonView, ChipView.
 */
fun Modifier.interactiveModifier(
	state: InteractiveVisualState,
	shape: RoundedCornerShape,
	tokens: DesignTokens,
	isInteractive: Boolean,
	clickable: Boolean = true,
	onClick: () -> Unit,
): Modifier = this
	.clip(shape)
	.background(state.currentBg)
	.border(
		width = if (state.focused) tokens.focusRingWidth.dp else tokens.borderWidth.dp,
		color = state.currentBorder,
		shape = shape,
	)
	.then(
		if (clickable) {
			Modifier
				.hoverable(state.interactionSource)
				.clickable(
					interactionSource = state.interactionSource,
					indication = if (isInteractive) ripple() else null,
					enabled = true,
					role = androidx.compose.ui.semantics.Role.Button,
				) { onClick() }
		} else {
			Modifier
		},
	)
	.then(
		if (!isInteractive && clickable) {
			Modifier
				.semantics { disabled() }
		} else {
			Modifier
		},
	)
