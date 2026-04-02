package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Position of the icon relative to the button text.
 * Start/End are RTL-aware (Start = left in LTR, right in RTL).
 */
@JsExport
@Serializable
enum class IconPosition {
	None,
	Start,
	End,
	Top,
	Bottom,
}
