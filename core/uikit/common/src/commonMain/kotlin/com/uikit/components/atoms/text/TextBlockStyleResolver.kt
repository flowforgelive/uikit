package com.uikit.components.atoms.text

import com.uikit.tokens.DesignTokens
import com.uikit.tokens.TextStyle
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedTextBlockStyle(
	val color: String,
	val fontSize: Double,
	val fontWeight: Int,
	val lineHeight: Double,
	val letterSpacing: Double,
)

@JsExport
object TextBlockStyleResolver {
	private fun fromTextStyle(
		style: TextStyle,
		color: String,
	): ResolvedTextBlockStyle =
		ResolvedTextBlockStyle(
			color = color,
			fontSize = style.fontSize,
			fontWeight = style.fontWeight,
			lineHeight = style.lineHeight,
			letterSpacing = style.letterSpacing,
		)

	fun resolve(
		config: TextBlockConfig,
		tokens: DesignTokens,
	): ResolvedTextBlockStyle =
		when (config.variant) {
			TextBlockVariant.H1 -> fromTextStyle(tokens.typography.title1, tokens.color.textPrimary)
			TextBlockVariant.H2 -> fromTextStyle(tokens.typography.title2, tokens.color.textPrimary)
			TextBlockVariant.H3 -> fromTextStyle(tokens.typography.title3, tokens.color.textPrimary)
			TextBlockVariant.Body -> fromTextStyle(tokens.typography.body, tokens.color.textPrimary)
			TextBlockVariant.Caption -> fromTextStyle(tokens.typography.caption1, tokens.color.textMuted)
		}
}
