package com.uikit.components.atoms.segmentedcontrol

import com.uikit.foundation.ComponentSizeResolver
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
data class SegmentedControlColors(
	val trackBg: String,
	val thumbBg: String,
	val textActive: String,
	val textInactive: String,
	val border: String,
)

@JsExport
@Serializable
data class SegmentedControlSizes(
	val height: Double,
	val paddingH: Double,
	val paddingV: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val letterSpacing: Double,
	val radius: Double,
	val thumbRadius: Double,
	val trackPadding: Double,
	val iconSize: Double,
	val iconGap: Double,
	val lineHeight: Double,
)

@JsExport
@Serializable
data class ResolvedSegmentedControlStyle(
	val colors: SegmentedControlColors,
	val sizes: SegmentedControlSizes,
)

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
@JsExport
object SegmentedControlStyleResolver {

	fun resolve(config: SegmentedControlConfig, tokens: DesignTokens): ResolvedSegmentedControlStyle {
		val scale = ComponentSizeResolver.resolve(config.size, tokens.controls, tokens.scaleFactor)
		val trackPadding = tokens.controls.segmentedControlTrackPadding

		val isVerticalLayout = config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom
		val paddingV: Double
		val height: Double
		if (isVerticalLayout) {
			paddingV = (scale.height - scale.lineHeight) / 2.0
			height = scale.iconSize + scale.iconGap + scale.lineHeight + 2.0 * paddingV
		} else {
			paddingV = 0.0
			height = scale.height
		}

		return ResolvedSegmentedControlStyle(
			colors = resolveColors(config.variant, tokens),
			sizes =
				SegmentedControlSizes(
					height = height,
					paddingH = scale.paddingH,
					paddingV = paddingV,
					fontSize = scale.fontSize,
					fontWeight = scale.fontWeight,
					letterSpacing = scale.letterSpacing,
					radius = scale.radius,
					thumbRadius = scale.radius - trackPadding,
					trackPadding = trackPadding,
					iconSize = scale.iconSize,
					iconGap = scale.iconGap,
					lineHeight = scale.lineHeight,
				),
		)
	}

	/**
	 * Backward-compatible overload: resolves with default size (Sm).
	 */
	@JsName("resolveDefault")
	fun resolve(tokens: DesignTokens): ResolvedSegmentedControlStyle {
		val scale = ComponentSizeResolver.resolve(
			com.uikit.foundation.ComponentSize.Sm,
			tokens.controls,
			tokens.scaleFactor,
		)
		val trackPadding = tokens.controls.segmentedControlTrackPadding
		return ResolvedSegmentedControlStyle(
			colors = resolveColors(VisualVariant.Surface, tokens),
			sizes =
				SegmentedControlSizes(
					height = scale.height,
					paddingH = scale.paddingH,
					paddingV = 0.0,
					fontSize = scale.fontSize,
					fontWeight = scale.fontWeight,
					letterSpacing = scale.letterSpacing,
					radius = scale.radius,
					thumbRadius = scale.radius - trackPadding,
					trackPadding = trackPadding,
					iconSize = scale.iconSize,
					iconGap = scale.iconGap,
					lineHeight = scale.lineHeight,
				),
		)
	}

	private fun resolveColors(variant: VisualVariant, tokens: DesignTokens): SegmentedControlColors =
		when (variant) {
			VisualVariant.Surface -> SegmentedControlColors(
				trackBg = tokens.color.neutralSoft,
				thumbBg = tokens.color.surface,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = tokens.color.borderSubtle,
			)

			VisualVariant.Soft -> SegmentedControlColors(
				trackBg = tokens.color.primarySoft,
				thumbBg = tokens.color.surface,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = "transparent",
			)

			VisualVariant.Outline -> SegmentedControlColors(
				trackBg = "transparent",
				thumbBg = tokens.color.surface,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = tokens.color.outlineVariant,
			)

			VisualVariant.Solid -> SegmentedControlColors(
				trackBg = tokens.color.surfaceContainerHigh,
				thumbBg = tokens.color.surface,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = "transparent",
			)

			VisualVariant.Ghost -> SegmentedControlColors(
				trackBg = "transparent",
				thumbBg = tokens.color.surfaceHover,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = "transparent",
			)
		}
}
