package com.uikit.components.atoms.segmentedcontrol

import com.uikit.foundation.ComponentSizeResolver
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
	val fontSize: Double,
	val radius: Double,
	val thumbRadius: Double,
	val trackPadding: Double,
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
	private const val TRACK_PADDING = 2.0

	fun resolve(config: SegmentedControlConfig, tokens: DesignTokens): ResolvedSegmentedControlStyle {
		val scale = ComponentSizeResolver.resolve(config.size, tokens.controls, tokens.scaleFactor)
		return ResolvedSegmentedControlStyle(
			colors =
				SegmentedControlColors(
					trackBg = tokens.color.surfaceHover,
					thumbBg = tokens.color.surface,
					textActive = tokens.color.textPrimary,
					textInactive = tokens.color.textSecondary,
					border = tokens.color.border,
				),
			sizes =
				SegmentedControlSizes(
					height = scale.height,
					paddingH = scale.paddingH,
					fontSize = scale.fontSize,
					radius = scale.radius,
					thumbRadius = scale.radius - TRACK_PADDING,
					trackPadding = TRACK_PADDING,
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
		return ResolvedSegmentedControlStyle(
			colors =
				SegmentedControlColors(
					trackBg = tokens.color.surfaceHover,
					thumbBg = tokens.color.surface,
					textActive = tokens.color.textPrimary,
					textInactive = tokens.color.textSecondary,
					border = tokens.color.border,
				),
			sizes =
				SegmentedControlSizes(
					height = scale.height,
					paddingH = scale.paddingH,
					fontSize = scale.fontSize,
					radius = scale.radius,
					thumbRadius = scale.radius - TRACK_PADDING,
					trackPadding = TRACK_PADDING,
				),
		)
	}
}
