package com.uikit.components.primitives.text

import com.uikit.foundation.TextEmphasis
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

	private fun resolveAutoColor(
		variant: TextBlockVariant,
		tokens: DesignTokens,
	): String =
		when (variant) {
			TextBlockVariant.BodySmall, TextBlockVariant.LabelLarge -> tokens.color.textSecondary
			TextBlockVariant.LabelMedium, TextBlockVariant.LabelSmall -> tokens.color.textMuted
			else -> tokens.color.textPrimary
		}

	private fun resolveEmphasisColor(
		emphasis: TextEmphasis,
		variant: TextBlockVariant,
		tokens: DesignTokens,
	): String =
		when (emphasis) {
			TextEmphasis.Auto -> resolveAutoColor(variant, tokens)
			TextEmphasis.Primary -> tokens.color.textPrimary
			TextEmphasis.Secondary -> tokens.color.textSecondary
			TextEmphasis.Muted -> tokens.color.textMuted
			TextEmphasis.Disabled -> tokens.color.textDisabled
		}

	private fun resolveTypography(
		variant: TextBlockVariant,
		tokens: DesignTokens,
	): TextStyle =
		when (variant) {
			TextBlockVariant.DisplayLarge -> tokens.typography.displayLarge
			TextBlockVariant.DisplayMedium -> tokens.typography.displayMedium
			TextBlockVariant.DisplaySmall -> tokens.typography.displaySmall
			TextBlockVariant.HeadlineLarge -> tokens.typography.headlineLarge
			TextBlockVariant.HeadlineMedium -> tokens.typography.headlineMedium
			TextBlockVariant.HeadlineSmall -> tokens.typography.headlineSmall
			TextBlockVariant.TitleLarge -> tokens.typography.titleLarge
			TextBlockVariant.TitleMedium -> tokens.typography.titleMedium
			TextBlockVariant.TitleSmall -> tokens.typography.titleSmall
			TextBlockVariant.BodyLarge -> tokens.typography.bodyLarge
			TextBlockVariant.BodyMedium -> tokens.typography.bodyMedium
			TextBlockVariant.BodySmall -> tokens.typography.bodySmall
			TextBlockVariant.LabelLarge -> tokens.typography.labelLarge
			TextBlockVariant.LabelMedium -> tokens.typography.labelMedium
			TextBlockVariant.LabelSmall -> tokens.typography.labelSmall
		}

	fun resolve(
		config: TextBlockConfig,
		tokens: DesignTokens,
	): ResolvedTextBlockStyle {
		val typography = resolveTypography(config.variant, tokens)
		val color = resolveEmphasisColor(config.emphasis, config.variant, tokens)
		return fromTextStyle(typography, color)
	}
}
