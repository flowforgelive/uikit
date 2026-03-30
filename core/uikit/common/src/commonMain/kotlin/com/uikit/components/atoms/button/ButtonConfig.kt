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
