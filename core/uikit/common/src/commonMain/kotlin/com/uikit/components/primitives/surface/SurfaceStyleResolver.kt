package com.uikit.components.primitives.surface

import com.uikit.foundation.ColorConstants
import com.uikit.foundation.SurfaceLevelResolver
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedSurfaceStyle(
	val bg: String,
	val bgHover: String,
	val bgActive: String,
	val border: String,
	val radius: Double,
	val shadow: String,
	val elevationDp: Double,
	val foregroundColor: String,
	val foregroundSecondary: String,
	val foregroundMuted: String,
)

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
@JsExport
object SurfaceStyleResolver {
	fun resolve(
		config: SurfaceConfig,
		tokens: DesignTokens,
	): ResolvedSurfaceStyle {
		val bg =
			when (config.variant) {
				VisualVariant.Ghost, VisualVariant.Outline, VisualVariant.Glass -> ColorConstants.TRANSPARENT
				VisualVariant.Soft -> SurfaceLevelResolver.resolveSoftColor(config.level, tokens)
				else -> SurfaceLevelResolver.resolveColor(config.level, tokens) // Solid, Surface
			}

		val isInteractive = config.clickable || config.hoverable
		val levelIndex = config.level.ordinal
		val bgHover =
			if (isInteractive) SurfaceLevelResolver.resolveHoverColor(levelIndex, tokens) else bg
		val bgActive =
			if (isInteractive) SurfaceLevelResolver.resolveActiveColor(levelIndex, tokens) else bg

		val border =
			when (config.variant) {
				VisualVariant.Outline -> tokens.color.outline
				VisualVariant.Glass -> tokens.color.outlineVariant
				VisualVariant.Surface -> tokens.color.borderSubtle
				else -> ColorConstants.TRANSPARENT
			}

		val shadow =
			if (config.elevated) {
				tokens.shadows.md
			} else {
				ColorConstants.SHADOW_NONE
			}

		val radius =
			when (config.shape) {
				SurfaceShape.None -> 0.0
				SurfaceShape.Sm -> tokens.radius.sm
				SurfaceShape.Md -> tokens.radius.md
				SurfaceShape.Lg -> tokens.radius.lg
				SurfaceShape.Xl -> tokens.radius.xl
				SurfaceShape.Full -> tokens.radius.full
			}

		return ResolvedSurfaceStyle(
			bg = bg,
			bgHover = bgHover,
			bgActive = bgActive,
			border = border,
			radius = radius,
			shadow = shadow,
			elevationDp = if (config.elevated) tokens.shadows.elevationDp else 0.0,
			foregroundColor = tokens.color.onSurface,
			foregroundSecondary = tokens.color.onSurfaceVariant,
			foregroundMuted = tokens.color.onSurfaceMuted,
		)
	}

}
