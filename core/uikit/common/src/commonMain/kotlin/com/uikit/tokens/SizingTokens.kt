package com.uikit.tokens

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

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
