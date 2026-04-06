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
	val lineHeightRatio: Double = 1.0,
	/** Dismiss button size as fraction of control height (used in Chip, Tag, etc.). */
	val dismissButtonRatio: Double = 0.70,
	/** Dismiss icon (×) size as fraction of dismiss button size. */
	val dismissIconRatio: Double = 0.50,
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
	val lineHeight: Double,
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
