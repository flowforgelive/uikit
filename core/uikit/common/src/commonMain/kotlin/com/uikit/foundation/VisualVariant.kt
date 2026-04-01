package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Shared visual variant type for all atomic components (Button, Surface, Badge, etc.).
 * Follows Radix UI Themes pattern: consistent vocabulary, component-specific styling.
 */
@JsExport
@Serializable
enum class VisualVariant {
	Solid,
	Soft,
	Outline,
	Ghost,
}
