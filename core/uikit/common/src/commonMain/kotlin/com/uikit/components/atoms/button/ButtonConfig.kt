package com.uikit.components.atoms.button

import com.uikit.foundation.Visibility
import kotlin.js.JsExport

@JsExport
enum class ButtonVariant {
    Primary,
    Secondary,
    Ghost,
    Danger,
    Link,
}

@JsExport
enum class ButtonSize {
    Sm,
    Md,
    Lg,
}

@JsExport
data class ButtonConfig(
    val id: String,
    val text: String,
    val variant: ButtonVariant,
    val size: ButtonSize,
    val disabled: Boolean,
    val loading: Boolean,
    val actionRoute: String?,
    val testTag: String?,
    val visibility: Visibility,
) {
    val isInteractive: Boolean get() = !disabled && !loading
}
