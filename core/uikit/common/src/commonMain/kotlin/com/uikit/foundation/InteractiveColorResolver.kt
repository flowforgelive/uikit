package com.uikit.foundation

import com.uikit.tokens.DesignTokens
import kotlin.js.JsExport

/**
 * Shared color resolver for all interactive components (Button, IconButton, Chip, Toggle, etc.).
 *
 * Resolves [VisualVariant] × [ColorIntent] matrix to a [ColorSet].
 * Surface-aware: Soft variant adapts to parent surface background via [SurfaceContext].
 *
 * SSR SAFETY: stateless singleton, pure functions, no platform dependencies.
 */
@JsExport
object InteractiveColorResolver {

	fun resolve(
		variant: VisualVariant,
		intent: ColorIntent,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext? = null,
	): ColorSet =
		when (variant) {
			VisualVariant.Solid -> solidColors(intent, tokens)
			VisualVariant.Soft -> softColors(intent, tokens, surfaceContext)
			VisualVariant.Surface -> surfaceColors(intent, tokens)
			VisualVariant.Outline -> outlineColors(intent, tokens)
			VisualVariant.Ghost -> ghostColors(intent, tokens)
		}

	fun resolveDisabled(tokens: DesignTokens): ColorSet =
		ColorSet(
			bg = tokens.color.surfaceDisabled,
			bgHover = tokens.color.surfaceDisabled,
			text = tokens.color.textDisabled,
			textHover = tokens.color.textDisabled,
			border = tokens.color.borderDisabled,
			borderHover = tokens.color.borderDisabled,
		)

	private fun solidColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = tokens.color.primary,
				bgHover = tokens.color.primaryHover,
				text = tokens.color.textOnPrimary,
				textHover = tokens.color.textOnPrimary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = tokens.color.surfaceContainerHighest,
				bgHover = tokens.color.surfaceHover,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Danger -> ColorSet(
				bg = tokens.color.danger,
				bgHover = tokens.color.dangerHover,
				text = tokens.color.textOnDanger,
				textHover = tokens.color.textOnDanger,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
			)
		}

	private fun softColors(intent: ColorIntent, tokens: DesignTokens, surfaceContext: SurfaceContext?): ColorSet {
		val depth = surfaceContext?.nestingDepth ?: 0
		val pair = NestingColorStrategy.resolveSoftBg(intent, depth, tokens)
		val textColors = softTextColors(intent, tokens)
		return ColorSet(
			bg = pair.bg,
			bgHover = pair.bgHover,
			text = textColors.first,
			textHover = textColors.second,
			border = ColorConstants.TRANSPARENT,
			borderHover = ColorConstants.TRANSPARENT,
		)
	}

	private fun softTextColors(intent: ColorIntent, tokens: DesignTokens): Pair<String, String> =
		when (intent) {
			ColorIntent.Primary -> tokens.color.textPrimary to tokens.color.textPrimary
			ColorIntent.Neutral -> tokens.color.textSecondary to tokens.color.textPrimary
			ColorIntent.Danger -> tokens.color.danger to tokens.color.danger
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
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLow,
				text = tokens.color.textPrimary,
				textHover = tokens.color.primary,
				border = tokens.color.outline,
				borderHover = tokens.color.primary,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLowest,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				border = tokens.color.outlineVariant,
				borderHover = tokens.color.outline,
			)

			ColorIntent.Danger -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
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
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLow,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLow,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Danger -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.dangerSoft,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
			)
		}
}
