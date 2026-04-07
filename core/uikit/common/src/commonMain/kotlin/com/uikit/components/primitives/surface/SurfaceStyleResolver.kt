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
	val border: String,
	val radius: Double,
	val shadow: String,
	val elevationDp: Double,
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
				VisualVariant.Ghost, VisualVariant.Outline -> ColorConstants.TRANSPARENT
				VisualVariant.Soft -> SurfaceLevelResolver.resolveSoftColor(config.level, tokens)
				else -> SurfaceLevelResolver.resolveColor(config.level, tokens) // Solid, Surface
			}

		val bgHover =
			if (config.clickable || config.hoverable) tokens.color.surfaceHover else bg

		val border =
			when (config.variant) {
				VisualVariant.Outline -> tokens.color.outlineVariant
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
			border = border,
			radius = radius,
			shadow = shadow,
			elevationDp = if (config.elevated) tokens.shadows.elevationDp else 0.0,
		)
	}

}
