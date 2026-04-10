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
			VisualVariant.Glass -> glassColors(intent, tokens)
		}

	fun resolveDisabled(tokens: DesignTokens): ColorSet =
		ColorSet(
			bg = tokens.color.surfaceDisabled,
			bgHover = tokens.color.surfaceDisabled,
			bgActive = tokens.color.surfaceDisabled,
			text = tokens.color.textDisabled,
			textHover = tokens.color.textDisabled,
			textActive = tokens.color.textDisabled,
			border = tokens.color.borderDisabled,
			borderHover = tokens.color.borderDisabled,
			borderActive = tokens.color.borderDisabled,
		)

	private fun solidColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = tokens.color.primary,
				bgHover = tokens.color.primaryHover,
				bgActive = tokens.color.primaryActive,
				text = tokens.color.textOnPrimary,
				textHover = tokens.color.textOnPrimary,
				textActive = tokens.color.textOnPrimary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
				borderActive = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = tokens.color.surfaceContainerHighest,
				bgHover = tokens.color.surfaceHover,
				bgActive = tokens.color.surfaceActive,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				textActive = tokens.color.textPrimary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
				borderActive = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Danger -> ColorSet(
				bg = tokens.color.danger,
				bgHover = tokens.color.dangerHover,
				bgActive = tokens.color.dangerActive,
				text = tokens.color.textOnDanger,
				textHover = tokens.color.textOnDanger,
				textActive = tokens.color.textOnDanger,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
				borderActive = ColorConstants.TRANSPARENT,
			)
		}

	private fun softColors(intent: ColorIntent, tokens: DesignTokens, surfaceContext: SurfaceContext?): ColorSet {
		val depth = surfaceContext?.nestingDepth ?: 0
		val pair = NestingColorStrategy.resolveSoftBg(intent, depth, tokens)
		val textColors = softTextColors(intent, tokens)
		return ColorSet(
			bg = pair.bg,
			bgHover = pair.bgHover,
			bgActive = pair.bgActive,
			text = textColors.first,
			textHover = textColors.second,
			textActive = textColors.second,
			border = ColorConstants.TRANSPARENT,
			borderHover = ColorConstants.TRANSPARENT,
			borderActive = ColorConstants.TRANSPARENT,
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
				bgActive = tokens.color.primarySoftActive,
				text = tokens.color.textPrimary,
				textHover = tokens.color.textPrimary,
				textActive = tokens.color.textPrimary,
				border = tokens.color.primaryBorder,
				borderHover = tokens.color.primaryBorderHover,
				borderActive = tokens.color.primaryBorderActive,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = tokens.color.neutralSoft,
				bgHover = tokens.color.neutralSoftHover,
				bgActive = tokens.color.neutralSoftActive,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				textActive = tokens.color.textPrimary,
				border = tokens.color.borderSubtle,
				borderHover = tokens.color.outline,
				borderActive = tokens.color.outlineActive,
			)

			ColorIntent.Danger -> ColorSet(
				bg = tokens.color.dangerSoft,
				bgHover = tokens.color.dangerSoftHover,
				bgActive = tokens.color.dangerSoftActive,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				textActive = tokens.color.danger,
				border = tokens.color.danger,
				borderHover = tokens.color.dangerHover,
				borderActive = tokens.color.dangerActive,
			)
		}

	private fun outlineColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLowest,
				bgActive = tokens.color.surfaceContainerLow,
				text = tokens.color.primary,
				textHover = tokens.color.primary,
				textActive = tokens.color.primary,
				border = tokens.color.outline,
				borderHover = tokens.color.primary,
				borderActive = tokens.color.primary,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLowest,
				bgActive = tokens.color.surfaceContainerLow,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				textActive = tokens.color.textPrimary,
				border = tokens.color.outlineVariant,
				borderHover = tokens.color.outline,
				borderActive = tokens.color.outlineActive,
			)

			ColorIntent.Danger -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.dangerSoft,
				bgActive = tokens.color.dangerSoftHover,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				textActive = tokens.color.danger,
				border = tokens.color.danger,
				borderHover = tokens.color.dangerHover,
				borderActive = tokens.color.dangerActive,
			)
		}

	private fun ghostColors(intent: ColorIntent, tokens: DesignTokens): ColorSet =
		when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLow,
				bgActive = tokens.color.surfaceContainer,
				text = tokens.color.primary,
				textHover = tokens.color.primary,
				textActive = tokens.color.primary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
				borderActive = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.surfaceContainerLow,
				bgActive = tokens.color.surfaceContainer,
				text = tokens.color.textSecondary,
				textHover = tokens.color.textPrimary,
				textActive = tokens.color.textPrimary,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
				borderActive = ColorConstants.TRANSPARENT,
			)

			ColorIntent.Danger -> ColorSet(
				bg = ColorConstants.TRANSPARENT,
				bgHover = tokens.color.dangerSoft,
				bgActive = tokens.color.dangerSoftHover,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				textActive = tokens.color.danger,
				border = ColorConstants.TRANSPARENT,
				borderHover = ColorConstants.TRANSPARENT,
				borderActive = ColorConstants.TRANSPARENT,
			)
		}

	/**
	 * Glass variant: frosted glass with neutral surface-based background.
	 * Uses backdrop-filter in React (real blur), alpha fallback in Compose.
	 * Background is always surface-derived (not intent-colored) — intent only affects text/border.
	 * Actual opacity is applied in the View layer via GlassTokens.
	 */
	private fun glassColors(intent: ColorIntent, tokens: DesignTokens): ColorSet {
		// Apple liquid glass fill: surface (white in light, near-black in dark) at 45% opacity.
		// This ensures visibility on any background — light glass on light bg, dark glass on dark bg.
		val glassBg = tokens.color.surface
		val glassBgHover = tokens.color.surfaceContainer
		val glassBgActive = tokens.color.surfaceContainerHigh
		// Adaptive border: dark in light mode, white in dark mode (Apple liquid glass spec).
		// Actual opacity (15%) is applied in the View layer via glassBorderAlpha.
		val glassBorder = tokens.color.onSurface

		return when (intent) {
			ColorIntent.Primary -> ColorSet(
				bg = glassBg,
				bgHover = glassBgHover,
				bgActive = glassBgActive,
				text = tokens.color.onSurface,
				textHover = tokens.color.onSurface,
				textActive = tokens.color.onSurface,
				border = glassBorder,
				borderHover = glassBorder,
				borderActive = glassBorder,
			)

			ColorIntent.Neutral -> ColorSet(
				bg = glassBg,
				bgHover = glassBgHover,
				bgActive = glassBgActive,
				text = tokens.color.onSurfaceVariant,
				textHover = tokens.color.onSurface,
				textActive = tokens.color.onSurface,
				border = glassBorder,
				borderHover = glassBorder,
				borderActive = glassBorder,
			)

			ColorIntent.Danger -> ColorSet(
				bg = glassBg,
				bgHover = glassBgHover,
				bgActive = glassBgActive,
				text = tokens.color.danger,
				textHover = tokens.color.danger,
				textActive = tokens.color.danger,
				border = glassBorder,
				borderHover = tokens.color.danger,
				borderActive = tokens.color.dangerActive,
			)
		}
	}
}
