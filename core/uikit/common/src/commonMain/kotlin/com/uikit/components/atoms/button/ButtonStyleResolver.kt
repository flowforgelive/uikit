package com.uikit.components.atoms.button

import com.uikit.foundation.ColorIntent
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ColorSet(
	val bg: String,
	val bgHover: String,
	val text: String,
	val textHover: String,
	val border: String,
	val borderHover: String,
)

@JsExport
@Serializable
data class SizeSet(
	val height: Double,
	val paddingH: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val iconSize: Double,
	val letterSpacing: Double,
)

@JsExport
@Serializable
data class ResolvedButtonStyle(
	val colors: ColorSet,
	val sizes: SizeSet,
	val radius: Double,
)

@JsExport
object ButtonStyleResolver {
	fun resolve(
		config: ButtonConfig,
		tokens: DesignTokens,
	): ResolvedButtonStyle {
		val colors =
			if (config.disabled) {
				ColorSet(
					bg = tokens.color.surfaceDisabled,
					bgHover = tokens.color.surfaceDisabled,
					text = tokens.color.textDisabled,
					textHover = tokens.color.textDisabled,
					border = tokens.color.borderDisabled,
					borderHover = tokens.color.borderDisabled,
				)
			} else {
				resolveColors(config.variant, config.intent, tokens)
			}

		val sizes =
			when (config.size) {
				ButtonSize.Sm -> {
					SizeSet(
						height = tokens.sizing.buttonSm,
						paddingH = tokens.spacing.sm,
						fontSize = tokens.typography.caption1.fontSize,
						fontWeight = tokens.typography.headline.fontWeight,
						iconSize = tokens.sizing.iconSm,
						letterSpacing = tokens.typography.caption1.letterSpacing,
					)
				}

				ButtonSize.Md -> {
					SizeSet(
						height = tokens.sizing.buttonMd,
						paddingH = tokens.spacing.lg,
						fontSize = tokens.typography.body.fontSize,
						fontWeight = tokens.typography.headline.fontWeight,
						iconSize = tokens.sizing.iconMd,
						letterSpacing = tokens.typography.body.letterSpacing,
					)
				}

				ButtonSize.Lg -> {
					SizeSet(
						height = tokens.sizing.buttonLg,
						paddingH = tokens.spacing.xl,
						fontSize = tokens.typography.body.fontSize,
						fontWeight = tokens.typography.headline.fontWeight,
						iconSize = tokens.sizing.iconLg,
						letterSpacing = tokens.typography.body.letterSpacing,
					)
				}
			}

		return ResolvedButtonStyle(
			colors = colors,
			sizes = sizes,
			radius = tokens.radius.md,
		)
	}

	private fun resolveColors(
		variant: VisualVariant,
		intent: ColorIntent,
		tokens: DesignTokens,
	): ColorSet =
		when (variant) {
			VisualVariant.Solid -> solidColors(intent, tokens)
			VisualVariant.Soft -> softColors(intent, tokens)
			VisualVariant.Outline -> outlineColors(intent, tokens)
			VisualVariant.Ghost -> ghostColors(intent, tokens)
		}

	private fun solidColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = tokens.color.primary,
				bgHover = tokens.color.primaryHover,
				text = tokens.color.textOnPrimary,
				textHover = tokens.color.textOnPrimary,
				border = "transparent",
				borderHover = "transparent",
			)

			ColorIntent.Neutral -> ColorSet(
				bg = tokens.color.surfaceContainerHighest,
				bgHover = tokens.color.surfaceHover,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				border = "transparent",
				borderHover = "transparent",
			)

			ColorIntent.Danger -> ColorSet(
				bg = tokens.color.danger,
				bgHover = tokens.color.dangerHover,
				text = tokens.color.textOnDanger,
				textHover = tokens.color.textOnDanger,
				border = "transparent",
				borderHover = "transparent",
			)
		}

	private fun softColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = tokens.color.surfaceContainerHigh,
				bgHover = tokens.color.surfaceHover,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				border = "transparent",
				borderHover = "transparent",
			)

			ColorIntent.Neutral -> ColorSet(
				bg = tokens.color.surfaceContainerLow,
				bgHover = tokens.color.surfaceContainer,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				border = "transparent",
				borderHover = "transparent",
			)

			ColorIntent.Danger -> ColorSet(
				bg = tokens.color.dangerSoft,
				bgHover = tokens.color.dangerSoftHover,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				border = "transparent",
				borderHover = "transparent",
			)
		}

	private fun outlineColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = "transparent",
				bgHover = tokens.color.surfaceContainerLow,
				text = tokens.color.textPrimary,
				textHover = tokens.color.primary,
				border = tokens.color.outline,
				borderHover = tokens.color.primary,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = "transparent",
				bgHover = tokens.color.surfaceContainerLowest,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				border = tokens.color.outlineVariant,
				borderHover = tokens.color.outline,
			)

			ColorIntent.Danger -> ColorSet(
				bg = "transparent",
				bgHover = tokens.color.dangerSoft,
				text = tokens.color.danger,
				textHover = tokens.color.dangerHover,
				border = tokens.color.danger,
				borderHover = tokens.color.dangerHover,
			)
		}

	private fun ghostColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = "transparent",
				bgHover = tokens.color.surfaceContainerLow,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				border = "transparent",
				borderHover = "transparent",
			)

			ColorIntent.Neutral -> ColorSet(
				bg = "transparent",
				bgHover = tokens.color.surfaceContainerLow,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				border = "transparent",
				borderHover = "transparent",
			)

			ColorIntent.Danger -> ColorSet(
				bg = "transparent",
				bgHover = tokens.color.dangerSoft,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				border = "transparent",
				borderHover = "transparent",
			)
		}
}
