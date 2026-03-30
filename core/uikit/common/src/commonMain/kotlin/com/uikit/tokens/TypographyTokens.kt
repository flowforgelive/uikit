package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class TypographyTokens(
	val largeTitle: TextStyle,
	val title1: TextStyle,
	val title2: TextStyle,
	val title3: TextStyle,
	val headline: TextStyle,
	val body: TextStyle,
	val callout: TextStyle,
	val subhead: TextStyle,
	val footnote: TextStyle,
	val caption1: TextStyle,
	val caption2: TextStyle,
)
