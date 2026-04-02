package com.uikit.components.atoms.iconbutton

import com.uikit.components.atoms.button.ButtonConfig
import com.uikit.components.atoms.button.ButtonStyleResolver
import com.uikit.components.atoms.button.ColorSet
import com.uikit.foundation.ComponentSizeResolver
import com.uikit.foundation.SurfaceContext
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
		// Reuse ButtonStyleResolver for color resolution
		val btnConfig = ButtonConfig(
			text = "",
			variant = config.variant,
			intent = config.intent,
			size = config.size,
			disabled = config.disabled,
			loading = config.loading,
		)
		val btnStyle = ButtonStyleResolver.resolve(btnConfig, tokens, surfaceContext)

		val scale = ComponentSizeResolver.resolve(config.size, tokens.controls, tokens.scaleFactor)

		return ResolvedIconButtonStyle(
			colors = btnStyle.colors,
			sizes = IconButtonSizeSet(
				size = scale.height,
				iconSize = scale.iconSize,
				radius = scale.radius,
			),
		)
	}
}
