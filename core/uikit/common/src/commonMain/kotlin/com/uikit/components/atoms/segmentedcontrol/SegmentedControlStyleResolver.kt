package com.uikit.components.atoms.segmentedcontrol

import com.uikit.foundation.ColorConstants
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.foundation.IconPosition
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.VisualVariant
import com.uikit.foundation.resolveSize
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
 * All methods must be pure functions: (config, tokens, surfaceContext?) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
@JsExport
object SegmentedControlStyleResolver {

	fun resolve(
		config: SegmentedControlConfig,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext? = null,
	): ResolvedSegmentedControlStyle {
		val scale = tokens.resolveSize(config.size)
		val trackPadding = tokens.controls.segmentedControlTrackPadding

		val isVerticalLayout = config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom
		val layout = ComponentSizeResolver.resolveVerticalLayout(scale, isVerticalLayout)

		return ResolvedSegmentedControlStyle(
			colors = resolveColors(config.variant, tokens, surfaceContext),
			sizes =
				SegmentedControlSizes(
					height = layout.height,
					paddingH = scale.paddingH,
					paddingV = layout.paddingV,
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
	fun resolve(tokens: DesignTokens): ResolvedSegmentedControlStyle =
		resolve(
			SegmentedControlConfig(options = emptyArray(), selectedId = ""),
			tokens,
		)

	private fun resolveColors(
		variant: VisualVariant,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext?,
	): SegmentedControlColors {
		val level = surfaceContext?.level ?: 0
		val thumbBg = resolveThumbBg(tokens, level)

		return when (variant) {
			VisualVariant.Surface -> SegmentedControlColors(
				trackBg = resolveSurfaceTrackBg(tokens, level),
				thumbBg = thumbBg,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = tokens.color.borderSubtle,
			)

			VisualVariant.Soft -> SegmentedControlColors(
				trackBg = tokens.color.primarySoft,
				thumbBg = thumbBg,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = ColorConstants.TRANSPARENT,
			)

			VisualVariant.Outline -> SegmentedControlColors(
				trackBg = ColorConstants.TRANSPARENT,
				thumbBg = thumbBg,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = tokens.color.outlineVariant,
			)

			VisualVariant.Solid -> SegmentedControlColors(
				trackBg = tokens.color.surfaceContainerHigh,
				thumbBg = thumbBg,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = ColorConstants.TRANSPARENT,
			)

			VisualVariant.Ghost -> SegmentedControlColors(
				trackBg = ColorConstants.TRANSPARENT,
				thumbBg = tokens.color.surfaceHover,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = ColorConstants.TRANSPARENT,
			)
		}
	}

	/**
	 * Resolves the thumb (selected segment) background.
	 *
	 * In light schemes, [surface] is the brightest token (white) — already optimal
	 * as an "elevated card" above the darker track.
	 *
	 * In dark schemes, [surface] is the darkest token — using it makes the thumb
	 * appear recessed instead of elevated. We use elevated container levels instead,
	 * adapting to the current surface nesting level (Apple-like pattern).
	 */
	private fun resolveThumbBg(tokens: DesignTokens, level: Int): String =
		if (isDarkScheme(tokens)) {
			containerForLevel((level + 4).coerceAtMost(5), tokens)
		} else {
			tokens.color.surface
		}

	/**
	 * Resolves the track background for the Surface variant.
	 *
	 * At level 0: uses [neutralSoft] (existing behavior).
	 * At elevated levels: uses [containerForLevel(level+1)] so the track remains
	 * visually distinct from the parent surface.
	 */
	private fun resolveSurfaceTrackBg(tokens: DesignTokens, level: Int): String =
		if (level <= 0) {
			tokens.color.neutralSoft
		} else {
			containerForLevel((level + 1).coerceAtMost(5), tokens)
		}

	private fun containerForLevel(level: Int, tokens: DesignTokens): String = when (level) {
		0 -> tokens.color.surface
		1 -> tokens.color.surfaceContainerLowest
		2 -> tokens.color.surfaceContainerLow
		3 -> tokens.color.surfaceContainer
		4 -> tokens.color.surfaceContainerHigh
		5 -> tokens.color.surfaceContainerHighest
		else -> tokens.color.surfaceContainerHighest
	}

	/**
	 * Detects dark scheme by comparing surface brightness to neutralSoft.
	 * In light schemes surface is brighter than neutralSoft; in dark schemes it's darker.
	 */
	private fun isDarkScheme(tokens: DesignTokens): Boolean =
		hexBrightness(tokens.color.surface) < hexBrightness(tokens.color.neutralSoft)

	private fun hexBrightness(hex: String): Int {
		val h = hex.removePrefix("#")
		if (h.length < 6) return 0
		return h.substring(0, 2).toInt(16) +
			h.substring(2, 4).toInt(16) +
			h.substring(4, 6).toInt(16)
	}
}
