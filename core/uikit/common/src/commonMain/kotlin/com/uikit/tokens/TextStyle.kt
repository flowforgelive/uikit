package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class TextStyle(
	val fontSize: Double,
	val fontWeight: Int,
	val lineHeight: Double,
	val letterSpacing: Double,
)

fun TextStyle.scaled(factor: Double): TextStyle =
	if (factor == 1.0) this
	else copy(
		fontSize = fontSize * factor,
		lineHeight = lineHeight * factor,
	)
