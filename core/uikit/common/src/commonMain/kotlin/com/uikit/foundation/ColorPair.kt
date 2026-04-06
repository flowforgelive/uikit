package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Simple pair of background + hover background colors.
 * Used by [NestingColorStrategy] to return tonal staircase results.
 */
@JsExport
@Serializable
data class ColorPair(
	val bg: String,
	val bgHover: String,
)
