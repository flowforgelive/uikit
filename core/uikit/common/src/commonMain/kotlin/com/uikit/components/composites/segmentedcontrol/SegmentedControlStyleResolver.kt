package com.uikit.components.composites.segmentedcontrol

import com.uikit.foundation.ColorConstants
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.foundation.IconPosition
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.SurfaceLevelResolver
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
	val glassTrackTint: String = ColorConstants.TRANSPARENT,
	val glassTrackOpacity: Double = 0.45,
	/** Thumb opacity for glass variant: <1.0 keeps backdrop blur visible through the thumb. */
	val glassThumbOpacity: Double = 1.0,
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
					iconGap = layout.iconGap,
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
		val thumbBg = resolveThumbBg(tokens)

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
				trackBg = resolveSolidTrackBg(tokens, level),
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

			VisualVariant.Glass -> SegmentedControlColors(
				trackBg = ColorConstants.TRANSPARENT,
				thumbBg = thumbBg,
				textActive = tokens.color.textPrimary,
				textInactive = tokens.color.textSecondary,
				border = tokens.color.borderSubtle,
				// Track tint uses the same token as Surface track for visual parity.
				glassTrackTint = resolveSurfaceTrackBg(tokens, level),
				// Light: high opacity so the frosted track is as visible as the Surface variant.
				// Dark: standard bgOpacity — frosted effect reads well on dark backgrounds.
				glassTrackOpacity = if (isDarkScheme(tokens)) tokens.glass.bgOpacity else 0.85,
				// Semi-transparent thumb: blur remains visible through the selected segment,
				// creating a layered frosted appearance (track + elevated frosted thumb).
				glassThumbOpacity = 0.75,
			)
		}
	}

	/**
	 * Resolves the thumb (selected segment) background.
	 *
	 * Strategy: use the tokens that guarantee maximum contrast against the track,
	 * regardless of the surface nesting level.
	 *
	 * Light: [surface] is always white (100% L) — lightest possible, always above any track.
	 * Dark:  [surfaceContainerHighest] is the lightest dark token (35.1% L) — always above
	 *        the dark tracks produced by [resolveSurfaceTrackBg].
	 */
	private fun resolveThumbBg(tokens: DesignTokens): String =
		if (isDarkScheme(tokens)) tokens.color.surfaceContainerHighest
		else tokens.color.surface

	/**
	 * Resolves the track background for the Surface (and Glass) variant.
	 *
	 * Design rule: track must always contrast visually with the parent surface at every
	 * nesting level (0-5). Two strategies per mode:
	 *
	 * **Light mode** — parent surfaces range from 100% (level 0-1) to 90.7% (level 5).
	 * We step through three fixed tokens that are darker than any parent level:
	 *   • Levels 0-1 (parent ≈ 100%): neutralSoft  97.0% → −3 pp
	 *   • Levels 2-3 (parent ≈ 95-97%): borderSubtle 92.2% → −3..−5 pp
	 *   • Levels 4-5 (parent ≈ 90-93%): surfaceHover 87.6% → −3..−6 pp
	 *
	 * **Dark mode** — parent surfaces range from 15.6% (level 0) to 35.1% (level 5).
	 * We alternate between "elevated" and "recessed" tracks to keep the thumb (fixed at
	 * surfaceContainerHighest 35.1%) always contrasting with the track:
	 *   • Levels 0-1 (parent 15-18%): neutralSoft    23.5% → elevated  (+7-8 pp) ✓
	 *   • Levels 2-3 (parent 21-25%): neutralSoftHover 28.7% → elevated (+3-7 pp) ✓
	 *   • Levels 4-5 (parent 30-35%): neutralSoft    23.5% → recessed  (−7-11 pp) ✓
	 *     Recessed track on a light-dark parent keeps thumb (35.1%) at +11 pp above track.
	 */
	private fun resolveSurfaceTrackBg(tokens: DesignTokens, level: Int): String =
		if (isDarkScheme(tokens)) {
			when (level) {
				0, 1 -> tokens.color.neutralSoft        // elevated on very dark surface
				2, 3 -> tokens.color.neutralSoftHover   // elevated on medium-dark surface
				else -> tokens.color.neutralSoft        // recessed on light-dark surface (4-5)
			}
		} else {
			when {
				level <= 1 -> tokens.color.neutralSoft    // 97.0% — subtle on white
				level <= 3 -> tokens.color.borderSubtle   // 92.2% — medium on near-white
				else       -> tokens.color.surfaceHover   // 87.6% — visible on light-gray
			}
		}

	/**
	 * Resolves the track background for the Solid variant.
	 *
	 * Solid uses a more opaque/filled look than Surface. The track is visually heavier.
	 *
	 * Light: surfaceContainerHigh (93.7%) for levels 0-3; surfaceHover (87.6%) for 4-5
	 *        so the track remains darker than the parent at all levels.
	 * Dark:  resolveColor(level+2) for levels 0-2 (elevated fill); neutralSoft (23.5%)
	 *        for levels 3+ so the thumb (35.1%) always has +11 pp contrast with the track.
	 */
	private fun resolveSolidTrackBg(tokens: DesignTokens, level: Int): String =
		if (isDarkScheme(tokens)) {
			when {
				level <= 2 -> SurfaceLevelResolver.resolveColor((level + 2).coerceAtMost(5), tokens)
				else       -> tokens.color.neutralSoft   // recessed: prevents thumb collision at level 3+
			}
		} else {
			when {
				level <= 3 -> tokens.color.surfaceContainerHigh   // 93.7%
				else       -> tokens.color.surfaceHover            // 87.6% — darker than level 4-5 parent
			}
		}

	/**
	 * Detects dark scheme by comparing surface brightness to neutralSoft.
	 * In light schemes surface is brighter than neutralSoft; in dark schemes it's darker.
	 */
	private fun isDarkScheme(tokens: DesignTokens): Boolean =
		colorBrightness(tokens.color.surface) < colorBrightness(tokens.color.neutralSoft)

	private fun colorBrightness(color: String): Double {
		if (color.startsWith("oklch(")) {
			val inner = color.removePrefix("oklch(").removeSuffix(")")
			val parts = inner.trim().split(" ")
			val lStr = parts.firstOrNull() ?: return 0.0
			return lStr.removeSuffix("%").toDoubleOrNull() ?: 0.0
		}
		val h = color.removePrefix("#")
		if (h.length < 6) return 0.0
		return (h.substring(0, 2).toInt(16) +
			h.substring(2, 4).toInt(16) +
			h.substring(4, 6).toInt(16)).toDouble()
	}
}
