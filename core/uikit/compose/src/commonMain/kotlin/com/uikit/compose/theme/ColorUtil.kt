package com.uikit.compose.theme

import androidx.compose.ui.graphics.Color

fun parseColor(hex: String): Color {
	if (hex == "transparent") return Color.Transparent
	val clean = hex.removePrefix("#")
	return when (clean.length) {
		8 -> {
			// #RRGGBBAA format
			val v = clean.toLong(16)
			val r = ((v shr 24) and 0xFF).toInt()
			val g = ((v shr 16) and 0xFF).toInt()
			val b = ((v shr 8) and 0xFF).toInt()
			val a = (v and 0xFF).toInt()
			Color(r, g, b, a)
		}
		else -> Color(0xFF000000 or clean.toLong(16))
	}
}
