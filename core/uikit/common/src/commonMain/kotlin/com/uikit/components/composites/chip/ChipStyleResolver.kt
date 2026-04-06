package com.uikit.components.composites.chip

import com.uikit.foundation.ColorSet
import com.uikit.foundation.InteractiveColorResolver
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.VisualVariant
import com.uikit.foundation.resolveSize
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ChipSizes(
	val height: Double,
	val paddingStart: Double,
	val paddingEnd: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val iconSize: Double,
	val iconGap: Double,
	val closeButtonSize: Double,
	val closeIconSize: Double,
	val letterSpacing: Double,
	val lineHeight: Double,
)

@JsExport
@Serializable
data class ResolvedChipStyle(
	val colors: ColorSet,
	val sizes: ChipSizes,
	val radius: Double,
)

/**
 * SSR SAFETY: Stateless singleton, pure functions only.
 */
@JsExport
object ChipStyleResolver {
	fun resolve(
		config: ChipConfig,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext? = null,
	): ResolvedChipStyle {
		val scale = tokens.resolveSize(config.size)

		val colors = when {
			config.disabled -> InteractiveColorResolver.resolveDisabled(tokens)
			config.selected -> {
				// Selected chip uses Solid variant regardless of config variant
				InteractiveColorResolver.resolve(VisualVariant.Solid, config.intent, tokens, surfaceContext)
			}
			else -> InteractiveColorResolver.resolve(config.variant, config.intent, tokens, surfaceContext)
		}

		val height = scale.height
		val paddingH = scale.paddingH
		val iconSize = scale.iconSize
		val iconGap = scale.iconGap
		val closeButtonSize = height * tokens.controls.proportions.dismissButtonRatio
		val closeIconSize = closeButtonSize * tokens.controls.proportions.dismissIconRatio
		val radius = height * tokens.controls.proportions.radiusFraction

		// When dismissible, reduce end padding so close button sits near edge
		val paddingEnd = if (config.dismissible) (height - closeButtonSize) / 2.0 else paddingH

		val sizes = ChipSizes(
			height = height,
			paddingStart = paddingH,
			paddingEnd = paddingEnd,
			fontSize = scale.fontSize,
			fontWeight = scale.fontWeight,
			iconSize = iconSize,
			iconGap = iconGap,
			closeButtonSize = closeButtonSize,
			closeIconSize = closeIconSize,
			letterSpacing = scale.letterSpacing,
			lineHeight = scale.lineHeight,
		)

		return ResolvedChipStyle(
			colors = colors,
			sizes = sizes,
			radius = radius,
		)
	}
}
