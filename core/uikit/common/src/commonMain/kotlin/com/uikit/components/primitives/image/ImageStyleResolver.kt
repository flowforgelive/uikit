package com.uikit.components.primitives.image

import com.uikit.foundation.AdaptiveRadiusResolver
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedImageStyle(
	val width: Double,
	val height: Double,
	val objectFit: String,
	val cornerRadius: Double,
	val borderColor: String?,
	val borderWidth: Double,
	val placeholder: String?,
	val fallback: String?,
)

/**
 * SSR SAFETY: stateless singleton, pure functions.
 */
@JsExport
object ImageStyleResolver {
	fun resolve(
		config: ImageConfig,
		tokens: DesignTokens,
	): ResolvedImageStyle {
		val fitValue = when (config.objectFit) {
			ImageFit.Cover -> "cover"
			ImageFit.Contain -> "contain"
			ImageFit.Fill -> "fill"
			ImageFit.None -> "none"
			ImageFit.ScaleDown -> "scale-down"
		}

		val cornerRadius = AdaptiveRadiusResolver.resolve(
			explicitRadius = config.cornerRadius,
			containerDimension = minOf(config.width, config.height),
			proportions = tokens.controls.proportions,
		)

		return ResolvedImageStyle(
			width = config.width,
			height = config.height,
			objectFit = fitValue,
			cornerRadius = cornerRadius,
			borderColor = if (config.showBorder) tokens.color.outlineVariant else null,
			borderWidth = if (config.showBorder) 1.0 else 0.0,
			placeholder = config.placeholder,
			fallback = config.fallback,
		)
	}
}
