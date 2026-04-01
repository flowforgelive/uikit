package com.uikit.components.atoms.segmentedcontrol

import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

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

@JsExport
object SegmentedControlStyleResolver {
	fun resolve(tokens: DesignTokens): ResolvedSegmentedControlStyle =
		ResolvedSegmentedControlStyle(
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
					height = tokens.sizing.buttonSm,
					paddingH = tokens.spacing.sm,
					fontSize = tokens.typography.caption1.fontSize,
					radius = tokens.radius.md,
					thumbRadius = tokens.radius.sm + 2.0,
					trackPadding = 2.0,
				),
		)
}
