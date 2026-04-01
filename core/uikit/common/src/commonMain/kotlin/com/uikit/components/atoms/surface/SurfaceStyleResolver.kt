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

@JsExport
object SurfaceStyleResolver {
	fun resolve(
		config: SurfaceConfig,
		tokens: DesignTokens,
	): ResolvedSurfaceStyle {
		val bg =
			when (config.variant) {
				VisualVariant.Ghost -> "transparent"
				else -> resolveBg(config.level, tokens)
			}

		val bgHover =
			if (config.clickable || config.hoverable) tokens.color.surfaceHover else bg

		val border =
			if (config.variant == VisualVariant.Outline) tokens.color.outlineVariant else "transparent"

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
}
