package com.uikit.components.atoms.button

import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.foundation.IconPosition
import com.uikit.foundation.SurfaceAwareColorResolver
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.VisualVariant
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Resolved foreground/background color contract for interactive components.
 *
 * PLATFORM CONTRACT:
 * - [text] / [textHover] define the color for ALL foreground content inside the component:
 *   text labels, icons, spinners, and any other visual elements.
 * - React: achieved via CSS `color` + `color: inherit` cascade.
 * - Compose: achieved via `CompositionLocalProvider(LocalContentColor provides ...)`.
 * - Platform implementations MUST propagate [text]/[textHover] to both text and icon slots.
 *
 * SSR SAFETY: Pure data class, no platform dependencies.
 */
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
	val paddingV: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val iconSize: Double,
	val iconGap: Double,
	val letterSpacing: Double,
	val lineHeight: Double,
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

		val isVerticalWithIcon = (config.iconPosition == IconPosition.Top || config.iconPosition == IconPosition.Bottom) && config.hasIcon
		val paddingV: Double
		val height: Double
		if (isVerticalWithIcon) {
			// Vertical: icon stacks above/below text — need extra height.
			// paddingV = same implicit vertical padding as horizontal mode:
			// (normalHeight - lineHeight) / 2
			paddingV = (scale.height - scale.lineHeight) / 2.0
			height = scale.iconSize + scale.iconGap + scale.lineHeight + 2.0 * paddingV
		} else {
			paddingV = 0.0
			height = scale.height
		}

		val sizes = SizeSet(
			height = height,
			paddingH = scale.paddingH,
			paddingV = paddingV,
			fontSize = scale.fontSize,
			fontWeight = scale.fontWeight,
			iconSize = scale.iconSize,
			iconGap = scale.iconGap,
			letterSpacing = scale.letterSpacing,
			lineHeight = scale.lineHeight,
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
			VisualVariant.Surface -> surfaceColors(intent, tokens)
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
				if (surfaceBg != null && surfaceBg == tokens.color.primarySoft) {
					// Edge case: soft bg matches surface bg → fall back to hover shade
					ColorSet(
						bg = tokens.color.primarySoftHover,
						bgHover = tokens.color.surfaceHover,
						text = tokens.color.textPrimary,
						textHover = tokens.color.textPrimary,
						border = "transparent",
						borderHover = "transparent",
					)
				} else {
					ColorSet(
						bg = tokens.color.primarySoft,
						bgHover = tokens.color.primarySoftHover,
						text = tokens.color.textPrimary,
						textHover = tokens.color.textPrimary,
						border = "transparent",
						borderHover = "transparent",
					)
				}
			}

			ColorIntent.Neutral -> {
				val surfaceBg = surfaceContext?.backgroundColor
				if (surfaceBg != null && surfaceBg == tokens.color.neutralSoft) {
					ColorSet(
						bg = tokens.color.neutralSoftHover,
						bgHover = tokens.color.surfaceHover,
						text = tokens.color.textSecondary,
						textHover = tokens.color.textPrimary,
						border = "transparent",
						borderHover = "transparent",
					)
				} else {
					ColorSet(
						bg = tokens.color.neutralSoft,
						bgHover = tokens.color.neutralSoftHover,
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

	private fun surfaceColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = tokens.color.primarySoft,
				bgHover = tokens.color.primarySoftHover,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				border = tokens.color.primaryBorder,
				borderHover = tokens.color.primaryBorderHover,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = tokens.color.neutralSoft,
				bgHover = tokens.color.neutralSoftHover,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				border = tokens.color.borderSubtle,
				borderHover = tokens.color.outline,
			)

			ColorIntent.Danger -> ColorSet(
				bg = tokens.color.dangerSoft,
				bgHover = tokens.color.dangerSoftHover,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				border = tokens.color.danger,
				borderHover = tokens.color.dangerHover,
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
