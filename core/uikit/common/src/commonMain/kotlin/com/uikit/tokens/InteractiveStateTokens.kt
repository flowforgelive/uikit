package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class InteractiveStateTokens(
	val hoverOpacity: Double,
	val pressOpacity: Double,
	val disabledOpacity: Double,
)
