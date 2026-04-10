package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class GlassTokens(
	/** Controls: buttons, chips, segmented controls */
	val blurRadius: Double = 32.0,
	val saturate: Double = 1.8,
	val bgOpacity: Double = 0.45,
	val borderOpacity: Double = 0.15,
	/** Surfaces: glass panels, glass cards */
	val surfaceBlurRadius: Double = 40.0,
	val surfaceSaturate: Double = 1.8,
	val surfaceBgOpacity: Double = 0.40,
	val surfaceBorderOpacity: Double = 0.15,
	/** Clear variant: subtle glass */
	val clearBlurRadius: Double = 8.0,
	val clearSaturate: Double = 1.2,
	val clearBgOpacity: Double = 0.06,
	val clearBorderOpacity: Double = 0.12,
	val highContrastBorderOpacity: Double = 0.5,
	/** Shadow: soft glow for controls */
	val glassShadow: String = "0 0 15px 0 rgba(0,0,0,0.25), inset 0 0 0 1px rgba(255,255,255,0.06)",
	/** Shadow: wider glow for glass surfaces/panels */
	val glassSurfaceShadow: String = "0 0 30px 0 rgba(0,0,0,0.35), inset 0 0 0 1px rgba(255,255,255,0.06)",
	/** Active press scale for glass controls */
	val pressScale: Double = 0.95,
	/** Hover opacity multiplier relative to bgOpacity */
	val hoverOpacityMultiplier: Double = 1.3,
	/** Active/pressed opacity multiplier relative to bgOpacity */
	val activeOpacityMultiplier: Double = 1.33,
)
