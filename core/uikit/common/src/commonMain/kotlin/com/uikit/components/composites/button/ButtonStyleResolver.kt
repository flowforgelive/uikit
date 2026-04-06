package com.uikit.components.composites.button

import com.uikit.foundation.ColorSet
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.foundation.IconPosition
import com.uikit.foundation.InteractiveColorResolver
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.resolveSize
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SizeSet(
	val height: Double,
	val paddingH: Double,
	val paddingV: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val iconSize: Double,
	val iconGap: Double,
	val letterSpacing: Double,
	val lineHeight: Double,
	val isIconOnly: Boolean = false,
)

@JsExport
@Serializable
data class ResolvedButtonStyle(
	val colors: ColorSet,
	val sizes: SizeSet,
	val radius: Double,
)

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
@JsExport
object ButtonStyleResolver {
	fun resolve(
		config: ButtonConfig,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext? = null,
	): ResolvedButtonStyle {
		val colors =
			if (config.disabled) {
				InteractiveColorResolver.resolveDisabled(tokens)
			} else {
				InteractiveColorResolver.resolve(config.variant, config.intent, tokens, surfaceContext)
			}

		val scale = tokens.resolveSize(config.size)

		val isVerticalWithIcon = (config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom) && config.hasIcon
		val layout = ComponentSizeResolver.resolveVerticalLayout(scale, isVerticalWithIcon)

		val effectivePaddingH = if (config.isIconOnly) {
			(scale.height - scale.iconSize) / 2.0
		} else {
			scale.paddingH
		}

		val sizes = SizeSet(
			height = layout.height,
			paddingH = effectivePaddingH,
			paddingV = layout.paddingV,
			fontSize = scale.fontSize,
			fontWeight = scale.fontWeight,
			iconSize = scale.iconSize,
			iconGap = layout.iconGap,
			letterSpacing = scale.letterSpacing,
			lineHeight = scale.lineHeight,
			isIconOnly = config.isIconOnly,
		)

		return ResolvedButtonStyle(
			colors = colors,
			sizes = sizes,
			radius = scale.radius,
		)
	}
}
