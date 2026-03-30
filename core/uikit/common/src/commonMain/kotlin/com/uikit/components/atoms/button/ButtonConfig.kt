package com.uikit.components.atoms.button

import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class ButtonVariant {
	Primary,
	Secondary,
	Ghost,
	Danger,
	Link,
}

@JsExport
@Serializable
enum class ButtonSize {
	Sm,
	Md,
	Lg,
}

@JsExport
@Serializable
data class ButtonConfig(
	val text: String,
	val variant: ButtonVariant = ButtonVariant.Primary,
	val size: ButtonSize = ButtonSize.Md,
	val disabled: Boolean = false,
	val loading: Boolean = false,
	val id: String = "",
	val actionRoute: String? = null,
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val isInteractive: Boolean get() = !disabled && !loading
}
