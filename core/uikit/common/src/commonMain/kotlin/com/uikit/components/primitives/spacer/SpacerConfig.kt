package com.uikit.components.primitives.spacer

import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class SpacerAxis {
	Horizontal,
	Vertical,
}

/**
 * Configuration for the Spacer primitive.
 *
 * Spacer creates controlled empty space between elements.
 * Supports fixed size (explicit dp value) and flexible mode (fills available space).
 *
 * @param size Space size in dp. When 0.0 or negative, spacer becomes flexible (flex: 1).
 * @param axis Direction of the space: Vertical (height) or Horizontal (width).
 */
@JsExport
@Serializable
data class SpacerConfig(
	val size: Double = 0.0,
	val axis: SpacerAxis = SpacerAxis.Vertical,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val isFlexible: Boolean get() = size <= 0.0
}
