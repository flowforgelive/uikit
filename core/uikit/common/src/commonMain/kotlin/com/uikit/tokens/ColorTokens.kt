package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ColorTokens(
	val primary: String,
	val primaryHover: String,
	val secondary: String,
	val danger: String,
	val dangerHover: String,
	val background: String,
	val surface: String,
	val surfaceContainerLowest: String,
	val surfaceContainerLow: String,
	val surfaceContainer: String,
	val surfaceContainerHigh: String,
	val surfaceContainerHighest: String,
	val surfaceHover: String,
	val onSurface: String,
	val outline: String,
	val outlineVariant: String,
	val textPrimary: String,
	val textSecondary: String,
	val textMuted: String,
	val textOnPrimary: String,
	val textOnDanger: String,
	val textDisabled: String,
	val surfaceDisabled: String,
	val borderDisabled: String,
	val border: String,
	val focusRing: String,
	val dangerSoft: String,
	val dangerSoftHover: String,
	val primarySoft: String,
	val primarySoftHover: String,
	val neutralSoft: String,
	val neutralSoftHover: String,
	val borderSubtle: String,
	val primaryBorder: String,
	val primaryBorderHover: String,
)
