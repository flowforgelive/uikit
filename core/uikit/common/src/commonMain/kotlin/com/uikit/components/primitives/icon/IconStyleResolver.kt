package com.uikit.components.primitives.icon

import com.uikit.foundation.resolveSize
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedIconStyle(
	val size: Double,
	val color: String?,
)

/**
 * SSR SAFETY: stateless singleton, pure functions.
 */
@JsExport
object IconStyleResolver {
	fun resolve(
		config: IconConfig,
		tokens: DesignTokens,
	): ResolvedIconStyle {
		val size = if (config.hasCustomSize) {
			config.customSize
		} else {
			tokens.resolveSize(config.size).iconSize
		}
		return ResolvedIconStyle(
			size = size,
			color = config.color,
		)
	}
}
