package com.uikit.components.atoms.text

import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedTextBlockStyle(
	val color: String,
	val fontSize: Int,
	val fontWeight: Int,
	val lineHeight: Int,
)

@JsExport
object TextBlockStyleResolver {
	fun resolve(
		config: TextBlockConfig,
		tokens: DesignTokens,
	): ResolvedTextBlockStyle =
		when (config.variant) {
			TextBlockVariant.H1 -> {
				ResolvedTextBlockStyle(
					color = tokens.color.textPrimary,
					fontSize = tokens.typography.h1Size,
					fontWeight = tokens.typography.h1Weight,
					lineHeight = (tokens.typography.h1Size * 1.2).toInt(),
				)
			}

			TextBlockVariant.H2 -> {
				ResolvedTextBlockStyle(
					color = tokens.color.textPrimary,
					fontSize = tokens.typography.h2Size,
					fontWeight = tokens.typography.h2Weight,
					lineHeight = (tokens.typography.h2Size * 1.2).toInt(),
				)
			}

			TextBlockVariant.H3 -> {
				ResolvedTextBlockStyle(
					color = tokens.color.textPrimary,
					fontSize = tokens.typography.h3Size,
					fontWeight = tokens.typography.h3Weight,
					lineHeight = (tokens.typography.h3Size * 1.2).toInt(),
				)
			}

			TextBlockVariant.Body -> {
				ResolvedTextBlockStyle(
					color = tokens.color.textPrimary,
					fontSize = tokens.typography.bodySize,
					fontWeight = tokens.typography.bodyWeight,
					lineHeight = (tokens.typography.bodySize * 1.5).toInt(),
				)
			}

			TextBlockVariant.Caption -> {
				ResolvedTextBlockStyle(
					color = tokens.color.secondary,
					fontSize = tokens.typography.captionSize,
					fontWeight = tokens.typography.captionWeight,
					lineHeight = (tokens.typography.captionSize * 1.5).toInt(),
				)
			}
		}
}
