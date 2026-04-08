package com.uikit.components.primitives.badge

import com.uikit.foundation.ColorIntent
import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class BadgeVariant {
	Dot,
	Numeric,
}

/**
 * Configuration for the Badge primitive.
 *
 * Badge renders a small status indicator — either a colored dot or a numeric label
 * in a pill-shaped container. Commonly used for notification counts, unread markers,
 * and step numbers.
 *
 * @param variant Dot (simple circle) or Numeric (pill with number).
 * @param value Numeric value to display. Only used when variant is Numeric.
 * @param maxValue Maximum displayed value. Values above this show "{maxValue}+".
 * @param intent Semantic color: Danger (notifications), Primary (info), Neutral (subtle).
 */
@JsExport
@Serializable
data class BadgeConfig(
	val variant: BadgeVariant = BadgeVariant.Dot,
	val value: Int = 0,
	val maxValue: Int = 99,
	val intent: ColorIntent = ColorIntent.Danger,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val displayValue: String get() = if (value > maxValue) "$maxValue+" else value.toString()
	val showValue: Boolean get() = variant == BadgeVariant.Numeric && value > 0
}
