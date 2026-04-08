package com.uikit.components.primitives.spacer

import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedSpacerStyle(
	val size: Double,
	val isFlexible: Boolean,
)

/**
 * SSR SAFETY: stateless singleton, pure functions.
 */
@JsExport
object SpacerStyleResolver {
	fun resolve(
		config: SpacerConfig,
		tokens: DesignTokens,
	): ResolvedSpacerStyle {
		return ResolvedSpacerStyle(
			size = if (config.isFlexible) 0.0 else config.size,
			isFlexible = config.isFlexible,
		)
	}
}
