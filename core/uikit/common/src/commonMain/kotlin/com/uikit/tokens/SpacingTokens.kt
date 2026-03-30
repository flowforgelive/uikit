package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SpacingTokens(
	val xxs: Double,
	val xs: Double,
	val sm: Double,
	val md: Double,
	val lg: Double,
	val xl: Double,
	val xxl: Double,
	val xxxl: Double,
	val xxxxl: Double,
)
