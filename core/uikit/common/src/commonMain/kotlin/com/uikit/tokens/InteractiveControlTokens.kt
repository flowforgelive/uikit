package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Semantic token layer for interactive controls (Button, SegmentedControl, Input, Select, etc.).
 * fontSize is the sole anchor; all other dimensions are derived via [ControlProportions].
 */

@JsExport
@Serializable
data class ControlSizeInput(
	val fontSize: Double,
	val fontWeight: Int,
	val letterSpacing: Double,
)

@JsExport
@Serializable
data class ControlProportions(
	val heightRatio: Double,
	val paddingHRatio: Double,
	val iconSizeRatio: Double,
	val iconGapRatio: Double,
	val radiusFraction: Double,
)

@JsExport
@Serializable
data class ControlSizeScale(
	val height: Double,
	val paddingH: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val iconSize: Double,
	val iconGap: Double,
	val letterSpacing: Double,
	val radius: Double,
)

@JsExport
@Serializable
data class InteractiveControlTokens(
	val proportions: ControlProportions,
	val xs: ControlSizeInput,
	val sm: ControlSizeInput,
	val md: ControlSizeInput,
	val lg: ControlSizeInput,
	val xl: ControlSizeInput,
	val segmentedControlTrackPadding: Double = 2.0,
)
