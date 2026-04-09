package com.uikit.compose.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

private val OKLCH_REGEX = Regex("""oklch\(\s*([\d.]+)(%?)\s+([\d.]+)\s+([\d.]+)\s*(?:/\s*([\d.]+%?))?\s*\)""")

fun parseColor(value: String): Color {
	if (value == "transparent") return Color.Transparent
	if (value.startsWith("oklch(")) return parseOklch(value)
	val clean = value.removePrefix("#")
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

/**
 * Parses OKLCH color string to Compose [Color].
 * Format: `oklch(L% C H)` or `oklch(L% C H / alpha)`.
 * L: 0–100 (lightness %), C: 0–0.4 (chroma), H: 0–360 (hue degrees).
 */
private fun parseOklch(value: String): Color {
	val match = OKLCH_REGEX.find(value) ?: return Color.Magenta // fallback for malformed
	val lRaw = match.groupValues[1].toDouble()
	val lIsPercent = match.groupValues[2] == "%"
	val l = if (lIsPercent) lRaw / 100.0 else lRaw
	val c = match.groupValues[3].toDouble()
	val h = match.groupValues[4].toDouble()
	val alpha = match.groupValues[5].let { raw ->
		when {
			raw.isEmpty() -> 1.0
			raw.endsWith("%") -> raw.removeSuffix("%").toDouble() / 100.0
			else -> raw.toDouble()
		}
	}

	// OKLCH → OKLab
	val hRad = h * (Math.PI / 180.0)
	val labL = l
	val labA = c * cos(hRad)
	val labB = c * sin(hRad)

	// OKLab → linear sRGB
	val l_ = labL + 0.3963377774 * labA + 0.2158037573 * labB
	val m_ = labL - 0.1055613458 * labA - 0.0638541728 * labB
	val s_ = labL - 0.0894841775 * labA - 1.2914855480 * labB

	val lCubed = l_ * l_ * l_
	val mCubed = m_ * m_ * m_
	val sCubed = s_ * s_ * s_

	val r = +4.0767416621 * lCubed - 3.3077115913 * mCubed + 0.2309699292 * sCubed
	val g = -1.2684380046 * lCubed + 2.6097574011 * mCubed - 0.3413193965 * sCubed
	val b = -0.0041960863 * lCubed - 0.7034186147 * mCubed + 1.7076147010 * sCubed

	// Linear sRGB → sRGB (gamma)
	fun linearToSrgb(x: Double): Double =
		if (x <= 0.0031308) 12.92 * x
		else 1.055 * x.pow(1.0 / 2.4) - 0.055

	val sR = linearToSrgb(r).coerceIn(0.0, 1.0).toFloat()
	val sG = linearToSrgb(g).coerceIn(0.0, 1.0).toFloat()
	val sB = linearToSrgb(b).coerceIn(0.0, 1.0).toFloat()

	return Color(sR, sG, sB, alpha.toFloat())
}
