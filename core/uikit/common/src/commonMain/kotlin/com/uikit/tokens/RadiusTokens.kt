package com.uikit.tokens

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class RadiusTokens(
    val sm: Int,
    val md: Int,
    val lg: Int,
)
