package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class TypographyTokens(
	val h1Size: Int,
	val h1Weight: Int,
	val h2Size: Int,
	val h2Weight: Int,
	val h3Size: Int,
	val h3Weight: Int,
	val bodySize: Int,
	val bodyWeight: Int,
	val captionSize: Int,
	val captionWeight: Int,
)
