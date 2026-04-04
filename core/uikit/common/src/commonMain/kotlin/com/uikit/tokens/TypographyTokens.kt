package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class TypographyTokens(
	val displayLarge: TextStyle,
	val displayMedium: TextStyle,
	val displaySmall: TextStyle,
	val headlineLarge: TextStyle,
	val headlineMedium: TextStyle,
	val headlineSmall: TextStyle,
	val titleLarge: TextStyle,
	val titleMedium: TextStyle,
	val titleSmall: TextStyle,
	val bodyLarge: TextStyle,
	val bodyMedium: TextStyle,
	val bodySmall: TextStyle,
	val labelLarge: TextStyle,
	val labelMedium: TextStyle,
	val labelSmall: TextStyle,
)
