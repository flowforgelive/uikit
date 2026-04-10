package com.uikit.compose.components.composites.segmentedcontrol

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.uikit.components.composites.segmentedcontrol.SegmentedControlConfig
import com.uikit.components.composites.segmentedcontrol.SegmentedControlStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalFontFamily
import com.uikit.compose.theme.LocalKeyboardNavigationMode
import com.uikit.compose.theme.LocalSurfaceContext
import com.uikit.compose.theme.LocalHazeState
import com.uikit.compose.theme.StyledText
import com.uikit.compose.theme.IconSlot
import com.uikit.compose.theme.IconTextLayout
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant
import com.uikit.foundation.Visibility
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun SegmentedControlView(
	config: SegmentedControlConfig,
	onSelectionChange: (String) -> Unit = {},
	renderIcon: (@Composable (String) -> Unit)? = null,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val fontFamily = LocalFontFamily.current
	val surfaceContext = LocalSurfaceContext.current
	val style = remember(config, tokens, surfaceContext) { SegmentedControlStyleResolver.resolve(config, tokens, surfaceContext) }
	val keyboardMode = LocalKeyboardNavigationMode.current

	val selectedIndex = config.options.indexOfFirst { it.id == config.selectedId }.coerceAtLeast(0)
	val optionCount = config.options.size

	val easing = remember(tokens.motion.easingStandard) {
		val values = tokens.motion.easingStandard
			.removePrefix("cubic-bezier(").removeSuffix(")")
			.split(",").map { it.trim().toFloat() }
		CubicBezierEasing(values[0], values[1], values[2], values[3])
	}

	val animatedFraction by animateFloatAsState(
		targetValue = if (optionCount > 0) selectedIndex.toFloat() / optionCount else 0f,
		animationSpec = tween(durationMillis = tokens.motion.durationNormal, easing = easing),
	)

	val focusRequesters = remember(optionCount) { List(optionCount) { FocusRequester() } }

	// Glass: frosted track (hazeEffect) + opaque thumb on top (Apple pattern).
	// The "glass" effect is in the frosted track; the thumb is a solid opaque pill that sits above it.
	val isGlass = config.variant == VisualVariant.Glass
	val hazeState = if (isGlass) LocalHazeState.current else null
	val useHaze = isGlass && hazeState != null
	val glassTrackAlpha = if (isGlass) style.colors.glassTrackOpacity.toFloat() else 1f
	val thumbColor = parseColor(style.colors.thumbBg)
	// Glass: semi-transparent thumb so backdrop blur remains visible through the selected segment.
	// Non-glass: alpha = 1.0 (from default glassThumbOpacity), fully opaque — no change.
	val glassThumbColor = thumbColor.copy(alpha = style.colors.glassThumbOpacity.toFloat())

	// Glass track tint: same color as Surface track, applied at bgOpacity for frosted appearance.
	val glassTintColor = if (isGlass) parseColor(style.colors.glassTrackTint).copy(alpha = glassTrackAlpha)
	else Color.Transparent
	// Border: borderSubtle for glass, matching Surface.
	val borderColor = parseColor(style.colors.border)

	val thumbRadiusPx = style.sizes.thumbRadius.toFloat()
	val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current
	val shape = RoundedCornerShape(style.sizes.radius.dp)
	val thumbShape = RoundedCornerShape(style.sizes.thumbRadius.dp)

	Box(
		modifier =
			modifier
				.width(IntrinsicSize.Max)
				.defaultMinSize(minHeight = (style.sizes.height + 2 * style.sizes.trackPadding).dp)
				.height(IntrinsicSize.Max)
				.clip(shape)
				.then(
					if (useHaze) {
						// Real backdrop blur for glass track.
						// Dark mode: white tint at ~22% (not surface/black) — Apple's liquid glass pattern.
						// Light mode: surface (white) at 45% — standard frosted glass.
						Modifier.hazeEffect(state = hazeState!!) {
							blurRadius = tokens.glass.blurRadius.dp
							noiseFactor = 0f
							tints = listOf(HazeTint(glassTintColor))
						}
					} else if (isGlass) {
						// Fallback: no blur available → full opacity (same as Surface variant).
						// Semi-transparent without blur = invisible on light backgrounds.
						Modifier.background(parseColor(style.colors.glassTrackTint))
					} else {
						Modifier.background(parseColor(style.colors.trackBg))
					},
				)
				.then(
					if (style.colors.border != "transparent") {
						Modifier.border(tokens.borderWidth.dp, borderColor, shape)
					} else {
						Modifier
					},
				)
				.padding(style.sizes.trackPadding.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id)
				// Non-glass: thumb drawn in draw phase via drawBehind (no recomposition per frame).
				// Glass: thumb rendered as child composable (above hazeEffect layer) — see inside Box below.
				.then(
					if (optionCount > 0 && !isGlass) {
						Modifier.drawBehind {
							val segmentWidth = size.width / optionCount
							val thumbX =
								if (layoutDirection == LayoutDirection.Rtl) {
									size.width - animatedFraction * size.width - segmentWidth
								} else {
									animatedFraction * size.width
								}
							drawRoundRect(
								color = thumbColor,
								topLeft = Offset(x = thumbX, y = 0f),
								size = Size(width = segmentWidth, height = size.height),
								cornerRadius = CornerRadius(thumbRadiusPx * density, thumbRadiusPx * density),
							)
						}
					} else {
						Modifier
					},
				),
	) {
		// For glass: thumb drawn via drawWithContent on the Row (child of hazeEffect Box).
		// drawWithContent on a child renders above hazeEffect, preserving the sliding animation.
		val rowModifier = if (isGlass && optionCount > 0) {
			Modifier.fillMaxSize().drawWithContent {
				val segmentWidth = size.width / optionCount
				val thumbX = if (layoutDirection == LayoutDirection.Rtl) {
					size.width - animatedFraction * size.width - segmentWidth
				} else {
					animatedFraction * size.width
				}
				// Use glassThumbColor (semi-transparent) so the backdrop blur is still partially
				// visible through the selected segment — creating a layered frosted effect.
				drawRoundRect(
					color = glassThumbColor,
					topLeft = Offset(x = thumbX, y = 0f),
					size = Size(width = segmentWidth, height = size.height),
					cornerRadius = CornerRadius(thumbRadiusPx * density, thumbRadiusPx * density),
				)
				drawContent()
			}
		} else {
			Modifier.fillMaxSize()
		}

		Row(modifier = rowModifier) {
			config.options.forEachIndexed { index, option ->
				val isActive = option.id == config.selectedId
				val optionInteractionSource = remember { MutableInteractionSource() }
				val isOptionFocused by optionInteractionSource.collectIsFocusedAsState()
				val isOptionHovered by optionInteractionSource.collectIsHoveredAsState()
				val showOptionFocusRing = isOptionFocused && keyboardMode.value

				Box(
					modifier =
						Modifier
							.weight(1f)
							.fillMaxHeight()
							.then(
								if (showOptionFocusRing) {
									Modifier.border(tokens.focusRingWidth.dp, parseColor(tokens.color.focusRing), thumbShape)
								} else {
									Modifier
								},
							)
							.focusRequester(focusRequesters[index])
							.onPreviewKeyEvent { event ->
								if (event.type == KeyEventType.KeyDown) {
									when (event.key) {
										Key.DirectionLeft -> {
											val prev = (index - 1).coerceAtLeast(0)
											if (prev != index) {
												onSelectionChange(config.options[prev].id)
												focusRequesters[prev].requestFocus()
											}
											true
										}
										Key.DirectionRight -> {
											val next = (index + 1).coerceAtMost(optionCount - 1)
											if (next != index) {
												onSelectionChange(config.options[next].id)
												focusRequesters[next].requestFocus()
											}
											true
										}
										Key.Enter, Key.Spacebar -> {
											onSelectionChange(config.options[index].id)
											true
										}
										else -> false
									}
								} else {
									false
								}
							}
							.hoverable(optionInteractionSource)
							.clickable(
								interactionSource = optionInteractionSource,
								indication = null,
							) { onSelectionChange(option.id) }
							.then(if (isOptionHovered) Modifier.alpha(tokens.state.hoverContentOpacity.toFloat()) else Modifier)
							.padding(horizontal = style.sizes.paddingH.dp, vertical = style.sizes.paddingV.dp),
					contentAlignment = Alignment.Center,
				) {
					val textColor = parseColor(
						if (isActive) style.colors.textActive else style.colors.textInactive,
					)
					val hasIcon = renderIcon != null && option.iconId != null && config.iconPosition != IconPosition.None
					if (hasIcon) {
						val iconId = option.iconId!!
						val iconContent: @Composable () -> Unit = {
							androidx.compose.runtime.CompositionLocalProvider(
								LocalContentColor provides textColor,
							) {
								IconSlot(size = style.sizes.iconSize.dp) {
									renderIcon(iconId)
								}
							}
						}
						val textContent: @Composable () -> Unit = {
							StyledText(
								text = option.label,
								fontSize = style.sizes.fontSize,
								fontWeight = style.sizes.fontWeight,
								letterSpacing = style.sizes.letterSpacing,
								lineHeight = style.sizes.lineHeight,
								color = textColor,
								fontFamily = fontFamily,
								textAlign = TextAlign.Center,
								maxLines = 1,
							)
						}
						IconTextLayout(
							iconPosition = config.iconPosition,
							iconGap = style.sizes.iconGap.dp,
							icon = iconContent,
							text = textContent,
							modifier = if (config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom) {
								Modifier.fillMaxHeight()
							} else {
								Modifier
							},
						)
					} else {
						StyledText(
							text = option.label,
							fontSize = style.sizes.fontSize,
							fontWeight = style.sizes.fontWeight,
							letterSpacing = style.sizes.letterSpacing,
							lineHeight = style.sizes.lineHeight,
							color = textColor,
							fontFamily = fontFamily,
							textAlign = TextAlign.Center,
							maxLines = 1,
						)
					}
				}
			}
		}
	}
}
