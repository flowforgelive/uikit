package com.uikit.tokens

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ColorTokens(
    val primary: String,
    val primaryHover: String,
    val secondary: String,
    val danger: String,
    val surface: String,
    val surfaceHover: String,
    val textPrimary: String,
    val textOnPrimary: String,
    val textOnDanger: String,
    val textDisabled: String,
    val surfaceDisabled: String,
    val borderDisabled: String,
    val border: String,
)
