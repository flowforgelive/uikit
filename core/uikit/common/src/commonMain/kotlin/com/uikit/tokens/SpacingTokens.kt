package com.uikit.tokens

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class SpacingTokens(
    val xs: Int,
    val sm: Int,
    val md: Int,
    val lg: Int,
    val xl: Int,
)
