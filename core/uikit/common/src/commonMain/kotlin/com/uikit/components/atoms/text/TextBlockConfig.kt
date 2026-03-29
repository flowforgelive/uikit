package com.uikit.components.atoms.text

import com.uikit.foundation.Visibility
import kotlin.js.JsExport

@JsExport
enum class TextBlockVariant {
    H1,
    H2,
    H3,
    Body,
    Caption,
}

@JsExport
data class TextBlockConfig(
    val id: String,
    val text: String,
    val variant: TextBlockVariant,
    val testTag: String?,
    val visibility: Visibility,
)
