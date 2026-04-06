package com.uikit.components.primitives.text

import com.uikit.foundation.TextEmphasis
import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class TextBlockVariant {
	DisplayLarge,
	DisplayMedium,
	DisplaySmall,
	HeadlineLarge,
	HeadlineMedium,
	HeadlineSmall,
	TitleLarge,
	TitleMedium,
	TitleSmall,
	BodyLarge,
	BodyMedium,
	BodySmall,
	LabelLarge,
	LabelMedium,
	LabelSmall,
}

@JsExport
@Serializable
data class TextBlockConfig(
	val text: String,
	val variant: TextBlockVariant = TextBlockVariant.BodyLarge,
	val emphasis: TextEmphasis = TextEmphasis.Auto,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
)
