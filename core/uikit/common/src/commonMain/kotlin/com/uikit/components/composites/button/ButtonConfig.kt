package com.uikit.components.composites.button

import com.uikit.foundation.ColorIntent
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.IconPosition
import com.uikit.foundation.Visibility
import com.uikit.foundation.VisualVariant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Backward-compatible alias. Prefer [ComponentSize] for new code.
 */
typealias ButtonSize = ComponentSize

@JsExport
@Serializable
data class ButtonConfig(
	val text: String,
	val variant: VisualVariant = VisualVariant.Solid,
	val intent: ColorIntent = ColorIntent.Primary,
	val size: ComponentSize = ComponentSize.Md,
	val iconPosition: IconPosition = IconPosition.None,
	val hasIconStart: Boolean = false,
	val hasIconEnd: Boolean = false,
	val disabled: Boolean = false,
	val loading: Boolean = false,
	val id: String = "",
	val actionRoute: String? = null,
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val isInteractive: Boolean get() = !disabled && !loading

	/** True when at least one icon should be rendered. */
	val hasIcon: Boolean get() = iconPosition != IconPosition.None && (hasIconStart || hasIconEnd)
}
