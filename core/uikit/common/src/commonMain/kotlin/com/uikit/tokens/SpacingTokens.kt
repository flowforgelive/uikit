package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SpacingTokens(
	val xxs: Double,
	val xs: Double,
	val sm: Double,
	val md: Double,
	val lg: Double,
	val xl: Double,
	val xxl: Double,
	val xxxl: Double,
	val xxxxl: Double,
)

fun SpacingTokens.scaled(factor: Double): SpacingTokens =
	if (factor == 1.0) this
	else copy(
		xxs = xxs * factor, xs = xs * factor, sm = sm * factor,
		md = md * factor, lg = lg * factor, xl = xl * factor,
		xxl = xxl * factor, xxxl = xxxl * factor, xxxxl = xxxxl * factor,
	)
