package com.uikit.components.primitives.text

import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.TextEmphasis
import com.uikit.tokens.DesignTokens
import com.uikit.tokens.TextStyle
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName

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

	/**
	 * Surface-aware emphasis color resolution.
	 * When surfaceContext provides foreground colors, uses them instead of global text tokens.
	 * Disabled always uses global tokens (independent of surface).
	 */
	private fun resolveSurfaceAwareEmphasisColor(
		emphasis: TextEmphasis,
		variant: TextBlockVariant,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext,
	): String {
		val fg = surfaceContext.foregroundColor
		if (fg.isEmpty()) return resolveEmphasisColor(emphasis, variant, tokens)
		val fgSecondary = surfaceContext.foregroundSecondary
		val fgMuted = surfaceContext.foregroundMuted
		return when (emphasis) {
			TextEmphasis.Auto -> resolveSurfaceAwareAutoColor(variant, fg, fgSecondary, fgMuted)
			TextEmphasis.Primary -> fg
			TextEmphasis.Secondary -> fgSecondary.ifEmpty { fg }
			TextEmphasis.Muted -> fgMuted.ifEmpty { fgSecondary.ifEmpty { fg } }
			TextEmphasis.Disabled -> tokens.color.textDisabled
		}
	}

	private fun resolveSurfaceAwareAutoColor(
		variant: TextBlockVariant,
		fg: String,
		fgSecondary: String,
		fgMuted: String,
	): String =
		when (variant) {
			TextBlockVariant.BodySmall, TextBlockVariant.LabelLarge -> fgSecondary.ifEmpty { fg }
			TextBlockVariant.LabelMedium, TextBlockVariant.LabelSmall -> fgMuted.ifEmpty { fgSecondary.ifEmpty { fg } }
			else -> fg
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

	@JsName("resolveWithSurface")
	fun resolve(
		config: TextBlockConfig,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext?,
	): ResolvedTextBlockStyle {
		val typography = resolveTypography(config.variant, tokens)
		val color = if (surfaceContext != null) {
			resolveSurfaceAwareEmphasisColor(config.emphasis, config.variant, tokens, surfaceContext)
		} else {
			resolveEmphasisColor(config.emphasis, config.variant, tokens)
		}
		return fromTextStyle(typography, color)
	}
}
