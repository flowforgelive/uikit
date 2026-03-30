package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class ThemeMode {
	Light,
	Dark,
	System,
}
