package com.uikit.components.primitives.divider

import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class DividerOrientation {
	Horizontal,
	Vertical,
}

/**
 * Configuration for the Divider primitive.
 *
 * Divider renders a thin line separating content. Supports horizontal and vertical orientations,
 * optional insets (start/end padding), and customizable color/thickness.
 *
 * @param orientation Horizontal or Vertical line direction.
 * @param thickness Line thickness in dp. When 0.0, defaults to [DesignTokens.borderWidth].
 * @param color Explicit color hex. When null, defaults to [ColorTokens.outlineVariant].
 * @param insetStart Start inset in dp (left for horizontal, top for vertical in LTR).
 * @param insetEnd End inset in dp.
 */
@JsExport
@Serializable
data class DividerConfig(
	val orientation: DividerOrientation = DividerOrientation.Horizontal,
	val thickness: Double = 0.0,
	val color: String? = null,
	val insetStart: Double = 0.0,
	val insetEnd: Double = 0.0,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
)
