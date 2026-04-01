package com.uikit.foundation

import com.uikit.tokens.ControlSizeScale
import com.uikit.tokens.InteractiveControlTokens
import kotlin.js.JsExport

/**
 * Resolves a [ComponentSize] to a [ControlSizeScale] from the interactive control tokens,
 * optionally applying a density [scaleFactor].
 */
@JsExport
object ComponentSizeResolver {
	fun resolve(
		size: ComponentSize,
		controls: InteractiveControlTokens,
		scaleFactor: Double = 1.0,
	): ControlSizeScale {
		val base = when (size) {
			ComponentSize.Xs -> controls.xs
			ComponentSize.Sm -> controls.sm
			ComponentSize.Md -> controls.md
			ComponentSize.Lg -> controls.lg
			ComponentSize.Xl -> controls.xl
		}
		if (scaleFactor == 1.0) return base
		return ControlSizeScale(
			height = base.height * scaleFactor,
			paddingH = base.paddingH * scaleFactor,
			fontSize = base.fontSize,
			fontWeight = base.fontWeight,
			iconSize = base.iconSize * scaleFactor,
			letterSpacing = base.letterSpacing,
			radius = base.radius * scaleFactor,
		)
	}
}
