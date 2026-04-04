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

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
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
			TextBlockVariant.DisplayLarge -> fromTextStyle(tokens.typography.displayLarge, tokens.color.textPrimary)
			TextBlockVariant.DisplayMedium -> fromTextStyle(tokens.typography.displayMedium, tokens.color.textPrimary)
			TextBlockVariant.DisplaySmall -> fromTextStyle(tokens.typography.displaySmall, tokens.color.textPrimary)
			TextBlockVariant.HeadlineLarge -> fromTextStyle(tokens.typography.headlineLarge, tokens.color.textPrimary)
			TextBlockVariant.HeadlineMedium -> fromTextStyle(tokens.typography.headlineMedium, tokens.color.textPrimary)
			TextBlockVariant.HeadlineSmall -> fromTextStyle(tokens.typography.headlineSmall, tokens.color.textPrimary)
			TextBlockVariant.TitleLarge -> fromTextStyle(tokens.typography.titleLarge, tokens.color.textPrimary)
			TextBlockVariant.TitleMedium -> fromTextStyle(tokens.typography.titleMedium, tokens.color.textPrimary)
			TextBlockVariant.TitleSmall -> fromTextStyle(tokens.typography.titleSmall, tokens.color.textPrimary)
			TextBlockVariant.BodyLarge -> fromTextStyle(tokens.typography.bodyLarge, tokens.color.textPrimary)
			TextBlockVariant.BodyMedium -> fromTextStyle(tokens.typography.bodyMedium, tokens.color.textPrimary)
			TextBlockVariant.BodySmall -> fromTextStyle(tokens.typography.bodySmall, tokens.color.textSecondary)
			TextBlockVariant.LabelLarge -> fromTextStyle(tokens.typography.labelLarge, tokens.color.textSecondary)
			TextBlockVariant.LabelMedium -> fromTextStyle(tokens.typography.labelMedium, tokens.color.textMuted)
			TextBlockVariant.LabelSmall -> fromTextStyle(tokens.typography.labelSmall, tokens.color.textMuted)
		}
}
