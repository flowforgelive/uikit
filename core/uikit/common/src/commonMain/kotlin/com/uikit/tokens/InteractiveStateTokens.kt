package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class InteractiveStateTokens(
	/** Overlay opacity on hover (used in ripple ::after for surfaces). */
	val hoverOpacity: Double,
	/** Overlay opacity on press/active (used in ripple ::after for buttons). */
	val pressOpacity: Double,
	/** Overall component opacity when disabled. */
	val disabledOpacity: Double,
	/** CSS brightness filter on button/icon-button press (e.g. 0.88 = darken 12%). */
	val pressBrightness: Double = 0.88,
	/** CSS brightness filter on surface press (e.g. 0.92 = darken 8%). */
	val pressBrightnessSurface: Double = 0.92,
	/** Opacity of inactive options on hover (e.g. SegmentedControl). */
	val hoverContentOpacity: Double = 0.72,
	/** Radial gradient spread stop for press ripple overlay (%). */
	val rippleSpread: Double = 70.0,
	/** Fade-out duration for ripple overlay (ms). */
	val rippleFadeDurationMs: Int = 300,
)
