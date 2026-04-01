package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SizingTokens(
	val controlXs: Double,
	val controlSm: Double,
	val controlMd: Double,
	val controlLg: Double,
	val controlXl: Double,
	val iconXs: Double,
	val iconSm: Double,
	val iconMd: Double,
	val iconLg: Double,
	val iconXl: Double,
	val minTouchTarget: Double,
)
