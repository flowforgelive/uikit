package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Resolved foreground/background color contract for interactive components.
 *
 * PLATFORM CONTRACT:
 * - [text] / [textHover] define the color for ALL foreground content inside the component:
 *   text labels, icons, spinners, and any other visual elements.
 * - React: achieved via CSS `color` + `color: inherit` cascade.
 * - Compose: achieved via `CompositionLocalProvider(LocalContentColor provides ...)`.
 * - Platform implementations MUST propagate [text]/[textHover] to both text and icon slots.
 *
 * SSR SAFETY: Pure data class, no platform dependencies.
 */
@JsExport
@Serializable
data class ColorSet(
	val bg: String,
	val bgHover: String,
	val text: String,
	val textHover: String,
	val border: String,
	val borderHover: String,
)
