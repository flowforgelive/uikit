package com.uikit.compose.components.primitives.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.ripple
import androidx.compose.ui.unit.dp
import com.uikit.components.primitives.surface.SurfaceConfig
import com.uikit.components.primitives.surface.SurfaceStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalKeyboardNavigationMode
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.parseColor
import com.uikit.compose.theme.LocalHazeState
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.SurfaceLevelResolver
import com.uikit.foundation.VisualVariant
import com.uikit.foundation.Visibility
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun SurfaceView(
	config: SurfaceConfig,
	onClick: (() -> Unit)? = null,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) { SurfaceStyleResolver.resolve(config, tokens) }
	val shape = RoundedCornerShape(style.radius.dp)

	val isClickable = config.clickable && onClick != null
	val isHoverable = isClickable || config.hoverable
	val interactionSource = remember { MutableInteractionSource() }
	val isHovered by interactionSource.collectIsHoveredAsState()
	val isPressed by interactionSource.collectIsPressedAsState()
	val isFocused by interactionSource.collectIsFocusedAsState()
	val keyboardMode = LocalKeyboardNavigationMode.current

	val isGlass = config.variant == VisualVariant.Glass
	val hazeState = if (isGlass) LocalHazeState.current else null

	val currentBg = when {
		isPressed && isHoverable -> style.bgActive
		isHovered && isHoverable -> style.bgHover
		else -> style.bg
	}
	val showFocusRing = isFocused && keyboardMode.value && isClickable
	val focusBorder = if (showFocusRing) tokens.color.focusRing else style.border
	val borderWidth = if (showFocusRing) tokens.focusRingWidth.dp else tokens.borderWidth.dp

	val parentSurfaceContext = LocalSurfaceContext.current
	val effectiveBg = if (isGlass) tokens.color.surface else style.bg
	val surfaceContext = remember(config.level, effectiveBg, parentSurfaceContext.nestingDepth) {
		SurfaceContext(
			level = config.level.ordinal,
			backgroundColor = effectiveBg,
			nestingDepth = parentSurfaceContext.nestingDepth,
			foregroundColor = style.foregroundColor,
			foregroundSecondary = style.foregroundSecondary,
			foregroundMuted = style.foregroundMuted,
		)
	}

	val glassBgColor = if (isGlass) {
		parseColor(SurfaceLevelResolver.resolveColor(config.level, tokens))
			.copy(alpha = tokens.glass.surfaceBgOpacity.toFloat())
	} else null

	val glassHazeModifier = if (isGlass && hazeState != null) {
		val tintColor = glassBgColor ?: parseColor(currentBg)
		Modifier.hazeEffect(state = hazeState) {
			blurRadius = tokens.glass.surfaceBlurRadius.dp
			noiseFactor = 0f
			tints = listOf(HazeTint(tintColor))
		}
	} else {
		Modifier.background(glassBgColor ?: parseColor(currentBg))
	}

	val glassBorderColor = if (isGlass) parseColor(tokens.color.onSurface).copy(alpha = tokens.glass.surfaceBorderOpacity.toFloat()) else null

	Box(
		modifier =
			modifier
				.then(
					if (config.elevated && !isGlass) {
						Modifier.shadow(style.elevationDp.dp, shape)
					} else {
						Modifier
					},
				)
				.clip(shape)
				.then(glassHazeModifier)
				.border(
					width = borderWidth,
					color = glassBorderColor ?: parseColor(focusBorder),
					shape = shape,
				)
				.then(
					if (isClickable) {
						val handler = onClick
						Modifier
							.hoverable(interactionSource)

							.clickable(
								interactionSource = interactionSource,
							indication = ripple(),
							) { handler() }
					} else if (config.hoverable) {
						Modifier.hoverable(interactionSource)
					} else {
						Modifier
					},
				)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id),
	) {
		CompositionLocalProvider(LocalSurfaceContext provides surfaceContext) {
			content()
		}
	}
}
