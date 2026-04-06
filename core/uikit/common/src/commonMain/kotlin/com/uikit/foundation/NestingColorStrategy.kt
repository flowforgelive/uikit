package com.uikit.foundation

import com.uikit.tokens.DesignTokens
import kotlin.js.JsExport

/**
 * Tonal Staircase strategy for nested interactive components.
 *
 * For Soft variant, each nesting level picks the next step in a pre-defined
 * color scale so that parent bg ≠ child bg, guaranteeing visual separation.
 *
 * Scale per intent (light theme example):
 * ```
 * depth 0: primarySoft        → primarySoftHover         (normal → hover)
 * depth 1: primarySoftHover   → surfaceContainerHigh     (normal → hover)
 * depth 2: surfaceContainerHigh → surfaceContainerHighest (normal → hover)
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
		val idx = nestingDepth.coerceIn(0, scale.size - 2)
		return ColorPair(bg = scale[idx], bgHover = scale[idx + 1])
	}

	private fun getSoftScale(intent: ColorIntent, tokens: DesignTokens): Array<String> =
		when (intent) {
			ColorIntent.Primary -> arrayOf(
				tokens.color.primarySoft,
				tokens.color.primarySoftHover,
				tokens.color.surfaceContainerHigh,
				tokens.color.surfaceContainerHighest,
			)

			ColorIntent.Neutral -> arrayOf(
				tokens.color.neutralSoft,
				tokens.color.neutralSoftHover,
				tokens.color.surfaceContainerHigh,
				tokens.color.surfaceContainerHighest,
			)

			ColorIntent.Danger -> arrayOf(
				tokens.color.dangerSoft,
				tokens.color.dangerSoftHover,
				tokens.color.surfaceContainer,
				tokens.color.surfaceContainerHigh,
			)
		}
}
