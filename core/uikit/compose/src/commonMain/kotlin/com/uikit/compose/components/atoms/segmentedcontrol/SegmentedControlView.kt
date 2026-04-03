package com.uikit.compose.components.atoms.segmentedcontrol

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlConfig
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlStyleResolver
import com.uikit.compose.theme.LocalDesignTokens
import com.uikit.compose.theme.LocalKeyboardNavigationMode
import com.uikit.compose.theme.parseColor
import com.uikit.foundation.IconPosition
import com.uikit.foundation.Visibility

@Composable
fun SegmentedControlView(
	config: SegmentedControlConfig,
	onSelectionChange: (String) -> Unit = {},
	renderIcon: (@Composable (String) -> Unit)? = null,
	modifier: Modifier = Modifier,
) {
	if (config.visibility == Visibility.Gone) return

	val tokens = LocalDesignTokens.current
	val style = remember(config, tokens) { SegmentedControlStyleResolver.resolve(config, tokens) }
	val keyboardMode = LocalKeyboardNavigationMode.current

	val selectedIndex = config.options.indexOfFirst { it.id == config.selectedId }.coerceAtLeast(0)
	val optionCount = config.options.size

	val animatedFraction by animateFloatAsState(
		targetValue = if (optionCount > 0) selectedIndex.toFloat() / optionCount else 0f,
		animationSpec = tween(durationMillis = tokens.motion.durationNormal),
	)

	val focusRequesters = remember(optionCount) { List(optionCount) { FocusRequester() } }

	val thumbColor = parseColor(style.colors.thumbBg)
	val thumbRadiusPx = style.sizes.thumbRadius.toFloat()
	val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current
	val shape = RoundedCornerShape(style.sizes.radius.dp)
	val thumbShape = RoundedCornerShape(style.sizes.thumbRadius.dp)

	Box(
		modifier =
			modifier
				.fillMaxWidth()
				.defaultMinSize(minHeight = style.sizes.height.dp)
				.clip(shape)
				.background(parseColor(style.colors.trackBg))
				.then(
					if (style.colors.border != "transparent") {
						Modifier.border(tokens.borderWidth.dp, parseColor(style.colors.border), shape)
					} else {
						Modifier
					},
				)
				.padding(style.sizes.trackPadding.dp)
				.then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
				.testTag(config.testTag ?: config.id)
				.drawBehind {
					if (optionCount > 0) {
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
				},
	) {
		Row(modifier = Modifier.matchParentSize()) {
			config.options.forEachIndexed { index, option ->
				val isActive = option.id == config.selectedId
				val optionInteractionSource = remember { MutableInteractionSource() }
				val isOptionFocused by optionInteractionSource.collectIsFocusedAsState()
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
							.clickable(
								interactionSource = optionInteractionSource,
								indication = null,
							) { onSelectionChange(option.id) },
					contentAlignment = Alignment.Center,
				) {
					val textColor = parseColor(
						if (isActive) style.colors.textActive else style.colors.textInactive,
					)
					val hasIcon = renderIcon != null && option.iconId != null && config.iconPosition != IconPosition.None
					if (hasIcon) {
						val iconId = option.iconId!!
						val isVertical = config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom
						val iconContent: @Composable () -> Unit = {
							androidx.compose.runtime.CompositionLocalProvider(
								LocalContentColor provides textColor,
							) {
								Box(
									modifier = Modifier.size(style.sizes.iconSize.dp),
									contentAlignment = Alignment.Center,
								) {
									renderIcon(iconId)
								}
							}
						}
						val textContent: @Composable () -> Unit = {
							Text(
								text = option.label,
								fontSize = style.sizes.fontSize.sp,
								fontWeight = FontWeight(style.sizes.fontWeight),
								letterSpacing = style.sizes.letterSpacing.sp,
								color = textColor,
								textAlign = TextAlign.Center,
							)
						}
						if (isVertical) {
								Column(
									horizontalAlignment = Alignment.CenterHorizontally,
									verticalArrangement = Arrangement.spacedBy(
									style.sizes.iconGap.dp,
									Alignment.CenterVertically,
								),
								modifier = Modifier.fillMaxHeight(),
							) {
								if (config.iconPosition == IconPosition.Top) {
									iconContent()
									textContent()
								} else {
									textContent()
									iconContent()
								}
							}
						} else {
							Row(
								verticalAlignment = Alignment.CenterVertically,
									horizontalArrangement = Arrangement.spacedBy(
									style.sizes.iconGap.dp,
									Alignment.CenterHorizontally,
								),
							) {
								if (config.iconPosition == IconPosition.Start) {
									iconContent()
									textContent()
								} else {
									textContent()
									iconContent()
								}
							}
						}
					} else {
						Text(
							text = option.label,
							fontSize = style.sizes.fontSize.sp,
							fontWeight = FontWeight(style.sizes.fontWeight),
							letterSpacing = style.sizes.letterSpacing.sp,
							color = textColor,
							textAlign = TextAlign.Center,
						)
					}
				}
			}
		}
	}
}
