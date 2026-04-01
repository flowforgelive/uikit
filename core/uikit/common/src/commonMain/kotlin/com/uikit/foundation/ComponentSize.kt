package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Unified size enum for all interactive controls (Button, SegmentedControl, Input, etc.)
 */
@JsExport
@Serializable
enum class ComponentSize {
	Xs,
	Sm,
	Md,
	Lg,
	Xl,
}
