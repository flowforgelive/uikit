package com.uikit.components.composites.chip

import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.Visibility
import com.uikit.foundation.VisualVariant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ChipConfig(
	val text: String,
	val variant: VisualVariant = VisualVariant.Soft,
	val intent: ColorIntent = ColorIntent.Neutral,
	val size: ComponentSize = ComponentSize.Md,
	val hasLeadingIcon: Boolean = false,
	val dismissible: Boolean = false,
	val selected: Boolean = false,
	val disabled: Boolean = false,
	val loading: Boolean = false,
	val id: String = "",
	val actionRoute: String? = null,
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val isInteractive: Boolean get() = !disabled && !loading
}
