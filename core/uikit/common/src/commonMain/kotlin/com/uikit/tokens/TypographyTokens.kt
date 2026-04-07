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

fun TypographyTokens.scaled(factor: Double): TypographyTokens =
	if (factor == 1.0) this
	else copy(
		displayLarge = displayLarge.scaled(factor),
		displayMedium = displayMedium.scaled(factor),
		displaySmall = displaySmall.scaled(factor),
		headlineLarge = headlineLarge.scaled(factor),
		headlineMedium = headlineMedium.scaled(factor),
		headlineSmall = headlineSmall.scaled(factor),
		titleLarge = titleLarge.scaled(factor),
		titleMedium = titleMedium.scaled(factor),
		titleSmall = titleSmall.scaled(factor),
		bodyLarge = bodyLarge.scaled(factor),
		bodyMedium = bodyMedium.scaled(factor),
		bodySmall = bodySmall.scaled(factor),
		labelLarge = labelLarge.scaled(factor),
		labelMedium = labelMedium.scaled(factor),
		labelSmall = labelSmall.scaled(factor),
	)
