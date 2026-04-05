package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ShadowTokens(
	val sm: String,
	val md: String,
	val lg: String,
	val xl: String,
	/** Compose elevation in dp for elevated surfaces/panels. */
	val elevationDp: Double = 4.0,
)
