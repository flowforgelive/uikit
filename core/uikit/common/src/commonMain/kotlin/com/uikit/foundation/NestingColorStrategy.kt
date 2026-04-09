package com.uikit.foundation

import com.uikit.tokens.DesignTokens
import kotlin.js.JsExport

/**
 * Tonal Staircase strategy for nested interactive components.
 *
 * For Soft variant, each nesting level picks the next step in a pre-defined
 * color scale so that parent bg ≠ child bg, guaranteeing visual separation.
 *
 * Scale per intent (light theme example, 6 steps):
 * ```
 * depth 0: primarySoft          → primarySoftHover       → primarySoftActive       (normal → hover → active)
 * depth 1: primarySoftHover     → primarySoftActive      → surfaceContainerHigh    (normal → hover → active)
 * depth 2: primarySoftActive    → surfaceContainerHigh   → surfaceContainerHighest (normal → hover → active)
 * depth 3: surfaceContainerHigh → surfaceContainerHighest → surfaceHover           (normal → hover → active)
 * ```
 *
 * SSR SAFETY: stateless singleton, pure functions, no platform dependencies.
 */
@JsExport
object NestingColorStrategy {

	fun resolveSoftBg(
		intent: ColorIntent,
		nestingDepth: Int,
		tokens: DesignTokens,
	): ColorPair {
		val scale = getSoftScale(intent, tokens)
		val idx = nestingDepth.coerceIn(0, scale.size - 3)
		return ColorPair(bg = scale[idx], bgHover = scale[idx + 1], bgActive = scale[idx + 2])
	}

	private fun getSoftScale(intent: ColorIntent, tokens: DesignTokens): Array<String> =
		when (intent) {
			ColorIntent.Primary -> arrayOf(
				tokens.color.primarySoft,
				tokens.color.primarySoftHover,
				tokens.color.primarySoftActive,
				tokens.color.surfaceContainerHigh,
				tokens.color.surfaceContainerHighest,
				tokens.color.surfaceHover,
			)

			ColorIntent.Neutral -> arrayOf(
				tokens.color.neutralSoft,
				tokens.color.neutralSoftHover,
				tokens.color.neutralSoftActive,
				tokens.color.surfaceContainerHigh,
				tokens.color.surfaceContainerHighest,
				tokens.color.surfaceHover,
			)

			ColorIntent.Danger -> arrayOf(
				tokens.color.dangerSoft,
				tokens.color.dangerSoftHover,
				tokens.color.dangerSoftActive,
				tokens.color.surfaceContainer,
				tokens.color.surfaceContainerHigh,
				tokens.color.surfaceContainerHighest,
			)
		}
}
