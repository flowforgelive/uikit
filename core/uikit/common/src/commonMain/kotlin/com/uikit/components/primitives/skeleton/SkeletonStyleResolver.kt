package com.uikit.components.primitives.skeleton

import com.uikit.foundation.AdaptiveRadiusResolver
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResolvedSkeletonStyle(
	val width: Double,
	val height: Double,
	val cornerRadius: Double,
	val baseColor: String,
	val highlightColor: String,
	val animate: Boolean,
	val durationMs: Int,
	val easing: String,
)

/**
 * SSR SAFETY: stateless singleton, pure functions.
 */
@JsExport
object SkeletonStyleResolver {
	fun resolve(
		config: SkeletonConfig,
		tokens: DesignTokens,
	): ResolvedSkeletonStyle {
		val height = when {
			config.height > 0.0 -> config.height
			config.shape == SkeletonShape.Circle -> config.width
			config.shape == SkeletonShape.TextLine -> tokens.typography.bodyMedium.lineHeight
			else -> 0.0
		}

		val cornerRadius = when {
			config.cornerRadius != null -> config.cornerRadius
			config.shape == SkeletonShape.Circle -> height / 2.0
			config.shape == SkeletonShape.TextLine -> tokens.radius.xs
			else -> if (height > 0.0) {
				AdaptiveRadiusResolver.resolve(
					explicitRadius = null,
					containerDimension = height,
					proportions = tokens.controls.proportions,
				)
			} else tokens.radius.sm
		}

		return ResolvedSkeletonStyle(
			width = config.width,
			height = height,
			cornerRadius = cornerRadius,
			baseColor = tokens.color.surfaceContainerHigh,
			highlightColor = tokens.color.surfaceContainerHighest,
			animate = config.animate,
			durationMs = 1500,
			easing = tokens.motion.easingLinear,
		)
	}
}
