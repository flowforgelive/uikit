package com.uikit.components.primitives.divider

import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedDividerStyle(
	val color: String,
	val thickness: Double,
	val insetStart: Double,
	val insetEnd: Double,
)

/**
 * SSR SAFETY: stateless singleton, pure functions.
 */
@JsExport
object DividerStyleResolver {
	fun resolve(
		config: DividerConfig,
		tokens: DesignTokens,
	): ResolvedDividerStyle {
		return ResolvedDividerStyle(
			color = config.color ?: tokens.color.outlineVariant,
			thickness = if (config.thickness > 0.0) config.thickness else tokens.borderWidth,
			insetStart = config.insetStart,
			insetEnd = config.insetEnd,
		)
	}
}
