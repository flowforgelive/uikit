package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SizingTokens(
	val buttonXs: Double,
	val buttonSm: Double,
	val buttonMd: Double,
	val buttonLg: Double,
	val buttonXl: Double,
	val iconXs: Double,
	val iconSm: Double,
	val iconMd: Double,
	val iconLg: Double,
	val iconXl: Double,
	val minTouchTarget: Double,
)
