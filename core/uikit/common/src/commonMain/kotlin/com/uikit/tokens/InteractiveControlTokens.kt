package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Semantic token layer for interactive controls (Button, SegmentedControl, Input, Select, etc.).
 * Maps abstract sizes (Xs..Xl) to concrete dimensions, bridging raw tokens and component resolvers.
 */
@JsExport
@Serializable
data class ControlSizeScale(
	val height: Double,
	val paddingH: Double,
	val fontSize: Double,
	val fontWeight: Int,
	val iconSize: Double,
	val letterSpacing: Double,
	val radius: Double,
)

@JsExport
@Serializable
data class InteractiveControlTokens(
	val xs: ControlSizeScale,
	val sm: ControlSizeScale,
	val md: ControlSizeScale,
	val lg: ControlSizeScale,
	val xl: ControlSizeScale,
)
