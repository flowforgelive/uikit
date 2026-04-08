package com.uikit.components.primitives.badge

import com.uikit.foundation.ColorIntent
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedBadgeStyle(
	val dotSize: Double,
	val minWidth: Double,
	val height: Double,
	val paddingH: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val bgColor: String,
	val textColor: String,
)

/**
 * SSR SAFETY: stateless singleton, pure functions.
 */
@JsExport
object BadgeStyleResolver {
	private const val HEIGHT_RATIO = 1.6
	private const val PADDING_H_RATIO = 0.4

	fun resolve(
		config: BadgeConfig,
		tokens: DesignTokens,
	): ResolvedBadgeStyle {
		val bgColor = resolveBgColor(config.intent, tokens)
		val textColor = resolveTextColor(config.intent, tokens)

		val labelStyle = tokens.typography.labelSmall
		val fontSize = labelStyle.fontSize
		val height = fontSize * HEIGHT_RATIO
		val paddingH = fontSize * PADDING_H_RATIO

		return ResolvedBadgeStyle(
			dotSize = tokens.spacing.sm,
			minWidth = height,
			height = height,
			paddingH = paddingH,
			fontSize = fontSize,
			fontWeight = labelStyle.fontWeight,
			bgColor = bgColor,
			textColor = textColor,
		)
	}

	private fun resolveBgColor(intent: ColorIntent, tokens: DesignTokens): String {
		return when (intent) {
			ColorIntent.Danger -> tokens.color.danger
			ColorIntent.Primary -> tokens.color.primary
			ColorIntent.Neutral -> tokens.color.surfaceContainerHighest
		}
	}

	private fun resolveTextColor(intent: ColorIntent, tokens: DesignTokens): String {
		return when (intent) {
			ColorIntent.Danger -> tokens.color.textOnDanger
			ColorIntent.Primary -> tokens.color.textOnPrimary
			ColorIntent.Neutral -> tokens.color.textPrimary
		}
	}
}
