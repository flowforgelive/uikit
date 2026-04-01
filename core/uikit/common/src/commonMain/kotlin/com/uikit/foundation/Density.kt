package com.uikit.foundation

import kotlin.js.JsExport

/**
 * Predefined density scaling factors for global UI sizing adjustment.
 * Applied via [DesignTokens.scaleFactor].
 */
@JsExport
object Density {
	val Comfortable: Double = 1.0
	val Cozy: Double = 0.9
	val Compact: Double = 0.8
}
