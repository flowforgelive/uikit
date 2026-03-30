package com.uikit.components.atoms.button

import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ColorSet(
	val bg: String,
	val text: String,
	val border: String,
)

@JsExport
@Serializable
data class SizeSet(
	val height: Double,
	val paddingH: Double,
	val fontSize: Double,
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
					text = tokens.color.textDisabled,
					border = tokens.color.borderDisabled,
				)
			} else {
				when (config.variant) {
					ButtonVariant.Primary -> {
						ColorSet(
							bg = tokens.color.primary,
							text = tokens.color.textOnPrimary,
							border = "transparent",
						)
					}

					ButtonVariant.Secondary -> {
						ColorSet(
							bg = tokens.color.surface,
							text = tokens.color.primary,
							border = tokens.color.primary,
						)
					}

					ButtonVariant.Ghost -> {
						ColorSet(
							bg = "transparent",
							text = tokens.color.primary,
							border = "transparent",
						)
					}

					ButtonVariant.Danger -> {
						ColorSet(
							bg = tokens.color.danger,
							text = tokens.color.textOnDanger,
							border = "transparent",
						)
					}

					ButtonVariant.Link -> {
						ColorSet(
							bg = "transparent",
							text = tokens.color.primary,
							border = "transparent",
						)
					}
				}
			}

		val sizes =
			when (config.size) {
				ButtonSize.Sm -> {
					SizeSet(
						height = tokens.sizing.buttonSm,
						paddingH = tokens.spacing.sm,
						fontSize = tokens.typography.caption1.fontSize,
						iconSize = tokens.sizing.iconSm,
						letterSpacing = tokens.typography.caption1.letterSpacing,
					)
				}

				ButtonSize.Md -> {
					SizeSet(
						height = tokens.sizing.buttonMd,
						paddingH = tokens.spacing.lg,
						fontSize = tokens.typography.body.fontSize,
						iconSize = tokens.sizing.iconMd,
						letterSpacing = tokens.typography.body.letterSpacing,
					)
				}

				ButtonSize.Lg -> {
					SizeSet(
						height = tokens.sizing.buttonLg,
						paddingH = tokens.spacing.xl,
						fontSize = tokens.typography.body.fontSize,
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
}
