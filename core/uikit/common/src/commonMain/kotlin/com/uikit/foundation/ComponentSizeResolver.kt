package com.uikit.foundation

import com.uikit.tokens.ControlSizeScale
import com.uikit.tokens.DesignTokens
import com.uikit.tokens.InteractiveControlTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class VerticalLayout(
	val height: Double,
	val paddingV: Double,
)

/**
 * Resolves a [ComponentSize] to a [ControlSizeScale] by computing proportional dimensions
 * from [ControlSizeInput] × [ControlProportions], optionally applying a density [scaleFactor].
 */
@JsExport
object ComponentSizeResolver {
	fun resolve(
		size: ComponentSize,
		controls: InteractiveControlTokens,
		scaleFactor: Double = 1.0,
	): ControlSizeScale {
		val input = when (size) {
			ComponentSize.Xs -> controls.xs
			ComponentSize.Sm -> controls.sm
			ComponentSize.Md -> controls.md
			ComponentSize.Lg -> controls.lg
			ComponentSize.Xl -> controls.xl
		}
		val p = controls.proportions
		val fs = input.fontSize
		val height = fs * p.heightRatio
		val paddingH = fs * p.paddingHRatio
		val iconSize = fs * p.iconSizeRatio
		val iconGap = fs * p.iconGapRatio
		val radius = height * p.radiusFraction
		val lineHeight = fs * p.lineHeightRatio

		return ControlSizeScale(
			height = height * scaleFactor,
			paddingH = paddingH * scaleFactor,
			fontSize = fs,
			fontWeight = input.fontWeight,
			iconSize = iconSize * scaleFactor,
			iconGap = iconGap * scaleFactor,
			letterSpacing = input.letterSpacing,
			radius = radius * scaleFactor,
			lineHeight = lineHeight,
		)
	}

	fun resolveVerticalLayout(scale: ControlSizeScale, isVertical: Boolean): VerticalLayout =
		if (isVertical) {
			val paddingV = (scale.height - scale.lineHeight) / 2.0
			val height = scale.iconSize + scale.iconGap + scale.lineHeight + 2.0 * paddingV
			VerticalLayout(height = height, paddingV = paddingV)
		} else {
			VerticalLayout(height = scale.height, paddingV = 0.0)
		}
}

/**
 * Convenience extension: resolves [ComponentSize] using tokens' controls and scaleFactor.
 * Internal to Kotlin StyleResolvers — NOT exported to JS (extension functions are invisible to @JsExport).
 */
fun DesignTokens.resolveSize(size: ComponentSize): ControlSizeScale =
	ComponentSizeResolver.resolve(size, controls, scaleFactor)
