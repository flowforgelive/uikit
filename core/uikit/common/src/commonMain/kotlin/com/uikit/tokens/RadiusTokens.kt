package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class RadiusTokens(
	val sm: Int,
	val md: Int,
	val lg: Int,
)
