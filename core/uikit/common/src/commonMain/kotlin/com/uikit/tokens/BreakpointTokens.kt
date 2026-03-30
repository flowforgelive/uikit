package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class BreakpointTokens(
	val compact: Double,
	val medium: Double,
	val expanded: Double,
	val large: Double,
	val extraLarge: Double,
)
