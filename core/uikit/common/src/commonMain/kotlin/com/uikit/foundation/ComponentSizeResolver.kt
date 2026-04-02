package com.uikit.foundation

import com.uikit.tokens.ControlSizeScale
import com.uikit.tokens.InteractiveControlTokens
import kotlin.js.JsExport

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

		return ControlSizeScale(
			height = height * scaleFactor,
			paddingH = paddingH * scaleFactor,
			fontSize = fs,
			fontWeight = input.fontWeight,
			iconSize = iconSize * scaleFactor,
			iconGap = iconGap * scaleFactor,
			letterSpacing = input.letterSpacing,
			radius = radius * scaleFactor,
		)
	}
}
