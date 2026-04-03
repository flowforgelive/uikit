package com.uikit.components.atoms.surface

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
				VisualVariant.Ghost, VisualVariant.Outline -> "transparent"
				VisualVariant.Soft -> resolveSoftBg(config.level, tokens)
				else -> resolveBg(config.level, tokens) // Solid, Surface
			}

		val bgHover =
			if (config.clickable || config.hoverable) tokens.color.surfaceHover else bg

		val border =
			when (config.variant) {
				VisualVariant.Outline -> tokens.color.outlineVariant
				VisualVariant.Surface -> tokens.color.borderSubtle
				else -> "transparent"
			}

		val shadow =
			if (config.elevated) {
				tokens.shadows.md
			} else {
				"none"
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

	/**
	 * Soft variant uses one level lighter than the requested level,
	 * ensuring visual distinction from Solid at the same level.
	 */
	private fun resolveSoftBg(level: SurfaceLevel, tokens: DesignTokens): String =
		when (level) {
			SurfaceLevel.Level0 -> tokens.color.surface
			SurfaceLevel.Level1 -> tokens.color.surface
			SurfaceLevel.Level2 -> tokens.color.surfaceContainerLowest
			SurfaceLevel.Level3 -> tokens.color.surfaceContainerLow
			SurfaceLevel.Level4 -> tokens.color.surfaceContainer
			SurfaceLevel.Level5 -> tokens.color.surfaceContainerHigh
		}
}
