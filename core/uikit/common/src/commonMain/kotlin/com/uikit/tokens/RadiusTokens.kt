package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class RadiusTokens(
	val xs: Double,
	val sm: Double,
	val md: Double,
	val lg: Double,
	val xl: Double,
	val full: Double,
)
