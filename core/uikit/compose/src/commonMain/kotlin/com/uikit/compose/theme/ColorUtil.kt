package com.uikit.compose.theme

import androidx.compose.ui.graphics.Color

fun parseColor(hex: String): Color {
    if (hex == "transparent") return Color.Transparent
    val colorLong = hex.removePrefix("#").toLong(16)
    return Color(0xFF000000 or colorLong)
}
