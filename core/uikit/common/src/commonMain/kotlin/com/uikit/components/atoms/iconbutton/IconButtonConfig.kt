package com.uikit.components.atoms.iconbutton

import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.Visibility
import com.uikit.foundation.VisualVariant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class IconButtonConfig(
	val variant: VisualVariant = VisualVariant.Solid,
	val intent: ColorIntent = ColorIntent.Primary,
	val size: ComponentSize = ComponentSize.Md,
	val disabled: Boolean = false,
	val loading: Boolean = false,
	val id: String = "",
	val actionRoute: String? = null,
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
	val ariaLabel: String? = null,
) {
	val isInteractive: Boolean get() = !disabled && !loading
}
