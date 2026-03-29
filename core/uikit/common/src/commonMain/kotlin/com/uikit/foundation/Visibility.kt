package com.uikit.foundation

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
enum class Visibility {
    Visible,
    Gone,
    Invisible,
}
