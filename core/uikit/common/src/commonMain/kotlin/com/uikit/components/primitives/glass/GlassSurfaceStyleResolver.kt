package com.uikit.components.primitives.glass

import com.uikit.components.primitives.surface.SurfaceShape
import com.uikit.foundation.ColorConstants
import com.uikit.foundation.GlassVariant
import com.uikit.foundation.SurfaceContext
import com.uikit.tokens.DesignTokens
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
data class ResolvedGlassSurfaceStyle(
	val bg: String,
	val bgOpacity: Double,
	val border: String,
	val borderOpacity: Double,
	val radius: Double,
	val blur: Double,
	val saturate: Double,
	val shadow: String,
	val foregroundColor: String,
	val foregroundSecondary: String,
	val foregroundMuted: String,
)

/**
 * SSR SAFETY: This object is a stateless singleton cached in Node.js.
 * All methods must be pure functions: (config, tokens) → style.
 * Do NOT add mutable state, side effects, or platform-specific code.
 *
 * GlassSurface uses backdrop-filter in React (real blur) and semi-transparent
 * tinted background in Compose (alpha fallback — CMP does not support backdrop-filter).
 */
@JsExport
object GlassSurfaceStyleResolver {
	fun resolve(
		config: GlassSurfaceConfig,
		tokens: DesignTokens,
	): ResolvedGlassSurfaceStyle = resolve(config, tokens, null)

	@JsName("resolveWithSurface")
	fun resolve(
		config: GlassSurfaceConfig,
		tokens: DesignTokens,
		surfaceContext: SurfaceContext?,
	): ResolvedGlassSurfaceStyle {
		val glass = tokens.glass
		val isRegular = config.variant == GlassVariant.Regular

		val bg = config.tintColor.ifEmpty {
			surfaceContext?.backgroundColor?.takeIf { it != "transparent" } ?: tokens.color.surface
		}
		val bgOpacity = if (isRegular) glass.surfaceBgOpacity else glass.clearBgOpacity
		val borderOpacity = if (isRegular) glass.surfaceBorderOpacity else glass.clearBorderOpacity
		val blur = if (isRegular) glass.surfaceBlurRadius else glass.clearBlurRadius
		val saturate = if (isRegular) glass.surfaceSaturate else glass.clearSaturate

		val border = tokens.color.onSurface

		val shadow = if (isRegular) {
			glass.glassSurfaceShadow
		} else if (config.elevated) {
			tokens.shadows.md
		} else {
			ColorConstants.SHADOW_NONE
		}

		val radius = when (config.shape) {
			SurfaceShape.None -> 0.0
			SurfaceShape.Sm -> tokens.radius.sm
			SurfaceShape.Md -> tokens.radius.md
			SurfaceShape.Lg -> tokens.radius.lg
			SurfaceShape.Xl -> tokens.radius.xl
			SurfaceShape.Full -> tokens.radius.full
		}

		return ResolvedGlassSurfaceStyle(
			bg = bg,
			bgOpacity = bgOpacity,
			border = border,
			borderOpacity = borderOpacity,
			radius = radius,
			blur = blur,
			saturate = saturate,
			shadow = shadow,
			foregroundColor = tokens.color.onSurface,
			foregroundSecondary = tokens.color.onSurfaceVariant,
			foregroundMuted = tokens.color.onSurfaceMuted,
		)
	}

	/**
	 * Fallback style for reduced-transparency accessibility mode.
	 * Returns solid background with no blur.
	 */
	fun resolveFallback(
		config: GlassSurfaceConfig,
		tokens: DesignTokens,
	): ResolvedGlassSurfaceStyle {
		val resolved = resolve(config, tokens)
		val fallbackOpacity = if (config.variant == GlassVariant.Regular) 0.92 else 0.85
		return resolved.copy(
			bgOpacity = fallbackOpacity,
			blur = 0.0,
			saturate = 1.0,
			borderOpacity = 0.4,
		)
	}
}
