package com.uikit.components.atoms.button

import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.foundation.SurfaceAwareColorResolver
import com.uikit.foundation.SurfaceContext
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

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
@JsExport
object ButtonStyleResolver {
	fun resolve(
		config: ButtonConfig,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext? = null,
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
				resolveColors(config.variant, config.intent, tokens, surfaceContext)
			}

		val scale = ComponentSizeResolver.resolve(config.size, tokens.controls, tokens.scaleFactor)

		val sizes = SizeSet(
			height = scale.height,
			paddingH = scale.paddingH,
			fontSize = scale.fontSize,
			fontWeight = scale.fontWeight,
			iconSize = scale.iconSize,
			letterSpacing = scale.letterSpacing,
		)

		return ResolvedButtonStyle(
			colors = colors,
			sizes = sizes,
			radius = scale.radius,
		)
	}

	private fun resolveColors(
		variant: VisualVariant,
		intent: ColorIntent,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext?,
	): ColorSet =
		when (variant) {
			VisualVariant.Solid -> solidColors(intent, tokens)
			VisualVariant.Soft -> softColors(intent, tokens, surfaceContext)
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

	private fun softColors(intent: ColorIntent, tokens: DesignTokens, surfaceContext: SurfaceContext?): ColorSet =
		when (intent) {
			ColorIntent.Primary -> {
				val surfaceBg = surfaceContext?.backgroundColor
				if (surfaceBg != null) {
					val bg = SurfaceAwareColorResolver.resolveSoftBg(surfaceBg, tokens.color, isPrimary = true)
					val bgHover = SurfaceAwareColorResolver.resolveSoftBgHover(bg, tokens.color)
					ColorSet(
						bg = bg,
						bgHover = bgHover,
						text = tokens.color.textPrimary,
						textHover = tokens.color.textPrimary,
						border = "transparent",
						borderHover = "transparent",
					)
				} else {
					ColorSet(
						bg = tokens.color.surfaceContainerHigh,
						bgHover = tokens.color.surfaceHover,
						text = tokens.color.textPrimary,
						textHover = tokens.color.textPrimary,
						border = "transparent",
						borderHover = "transparent",
					)
				}
			}

			ColorIntent.Neutral -> {
				val surfaceBg = surfaceContext?.backgroundColor
				if (surfaceBg != null) {
					val bg = SurfaceAwareColorResolver.resolveSoftBg(surfaceBg, tokens.color, isPrimary = false)
					val bgHover = SurfaceAwareColorResolver.resolveSoftBgHover(bg, tokens.color)
					ColorSet(
						bg = bg,
						bgHover = bgHover,
						text = tokens.color.textSecondary,
						textHover = tokens.color.textPrimary,
						border = "transparent",
						borderHover = "transparent",
					)
				} else {
					ColorSet(
						bg = tokens.color.surfaceContainerLow,
						bgHover = tokens.color.surfaceContainer,
						text = tokens.color.textSecondary,
						textHover = tokens.color.textPrimary,
						border = "transparent",
						borderHover = "transparent",
					)
				}
			}

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
