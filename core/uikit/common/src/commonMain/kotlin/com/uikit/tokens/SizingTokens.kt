package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SizingTokens(
	val controlXs: Double,
	val controlSm: Double,
	val controlMd: Double,
	val controlLg: Double,
	val controlXl: Double,
	val iconXs: Double,
	val iconSm: Double,
	val iconMd: Double,
	val iconLg: Double,
	val iconXl: Double,
	val minTouchTarget: Double,
)

fun SizingTokens.scaled(factor: Double): SizingTokens =
	if (factor == 1.0) this
	else copy(
		controlXs = controlXs * factor, controlSm = controlSm * factor,
		controlMd = controlMd * factor, controlLg = controlLg * factor,
		controlXl = controlXl * factor,
		iconXs = iconXs * factor, iconSm = iconSm * factor,
		iconMd = iconMd * factor, iconLg = iconLg * factor,
		iconXl = iconXl * factor,
		minTouchTarget = minTouchTarget * factor,
	)
