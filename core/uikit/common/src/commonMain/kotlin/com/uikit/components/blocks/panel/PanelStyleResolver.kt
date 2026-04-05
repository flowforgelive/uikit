package com.uikit.components.blocks.panel

import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.foundation.ColorConstants
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedPanelStyle(
	val bg: String,
	val border: String,
	val shadow: String,
	val radius: Double,
	val width: Double,
	val height: Double,
	val isHorizontal: Boolean,
	val insetPadding: Double,
	val borderWidth: Double,
	val durationMs: Int,
	val easing: String,
)

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
@JsExport
object PanelStyleResolver {
	private const val DEFAULT_RADIUS_FRACTION = 0.2

	fun resolve(
		config: PanelConfig,
		tokens: DesignTokens,
	): ResolvedPanelStyle {
		val bg = resolveBg(config.surfaceLevel, tokens)

		val border = if (config.showBorder) {
			tokens.color.borderSubtle
		} else {
			ColorConstants.TRANSPARENT
		}

		val shadow = if (config.elevated) {
			tokens.shadows.md
		} else {
			ColorConstants.SHADOW_NONE
		}

		val radiusScale = tokens.controls.proportions.radiusFraction / DEFAULT_RADIUS_FRACTION
		val radius = when (config.variant) {
			PanelVariant.Pinned -> 0.0
			PanelVariant.Inset -> tokens.radius.lg * radiusScale
		}

		val insetPadding = when (config.variant) {
			PanelVariant.Pinned -> 0.0
			PanelVariant.Inset -> tokens.spacing.md
		}

		return ResolvedPanelStyle(
			bg = bg,
			border = border,
			shadow = shadow,
			radius = radius,
			width = config.currentWidth,
			height = config.currentHeight,
			isHorizontal = config.isHorizontal,
			insetPadding = insetPadding,
			borderWidth = tokens.borderWidth,
			durationMs = tokens.motion.durationSlow,
			easing = tokens.motion.easingEmphasizedDecelerate,
		)
	}

	private fun resolveBg(level: SurfaceLevel, tokens: DesignTokens): String =
		when (level) {
			SurfaceLevel.Level0 -> tokens.color.surface
			SurfaceLevel.Level1 -> tokens.color.surfaceContainerLowest
			SurfaceLevel.Level2 -> tokens.color.surfaceContainerLow
			SurfaceLevel.Level3 -> tokens.color.surfaceContainer
			SurfaceLevel.Level4 -> tokens.color.surfaceContainerHigh
			SurfaceLevel.Level5 -> tokens.color.surfaceContainerHighest
		}
}
