package com.uikit.components.atoms.iconbutton

import com.uikit.foundation.ColorSet
import com.uikit.foundation.InteractiveColorResolver
import com.uikit.foundation.SurfaceContext
import com.uikit.foundation.resolveSize
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class IconButtonSizeSet(
	val size: Double,
	val iconSize: Double,
	val radius: Double,
)

@JsExport
@Serializable
data class ResolvedIconButtonStyle(
	val colors: ColorSet,
	val sizes: IconButtonSizeSet,
)

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 */
@JsExport
object IconButtonStyleResolver {
	fun resolve(
		config: IconButtonConfig,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext? = null,
	): ResolvedIconButtonStyle {
		val colors =
			if (config.disabled) {
				InteractiveColorResolver.resolveDisabled(tokens)
			} else {
				InteractiveColorResolver.resolve(config.variant, config.intent, tokens, surfaceContext)
			}

		val scale = tokens.resolveSize(config.size)

		return ResolvedIconButtonStyle(
			colors = colors,
			sizes = IconButtonSizeSet(
				size = scale.height,
				iconSize = scale.iconSize,
				radius = scale.radius,
			),
		)
	}
}
