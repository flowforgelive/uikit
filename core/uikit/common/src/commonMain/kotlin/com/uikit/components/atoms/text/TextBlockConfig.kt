package com.uikit.components.atoms.text

import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class TextBlockVariant {
	H1,
	H2,
	H3,
	Body,
	Caption,
}

@JsExport
@Serializable
data class TextBlockConfig(
	val id: String,
	val text: String,
	val variant: TextBlockVariant,
	val testTag: String?,
	val visibility: Visibility,
)
