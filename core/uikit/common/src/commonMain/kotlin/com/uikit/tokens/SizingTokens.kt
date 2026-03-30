package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SizingTokens(
	val buttonSm: Int,
	val buttonMd: Int,
	val buttonLg: Int,
	val iconSm: Int,
	val iconMd: Int,
	val iconLg: Int,
)
