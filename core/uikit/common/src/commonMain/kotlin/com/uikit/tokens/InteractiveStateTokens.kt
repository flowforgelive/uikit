package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class InteractiveStateTokens(
	/** Overlay opacity on press/active (used in ripple ::after for buttons). */
	val pressOpacity: Double,
	/** Opacity of inactive options on hover (e.g. SegmentedControl). */
	val hoverContentOpacity: Double = 0.72,
	/** Radial gradient spread stop for press ripple overlay (%). */
	val rippleSpread: Double = 70.0,
	/** Fade-out duration for ripple overlay (ms). */
	val rippleFadeDurationMs: Int = 300,
)
