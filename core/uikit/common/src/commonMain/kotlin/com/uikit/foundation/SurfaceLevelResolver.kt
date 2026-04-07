package com.uikit.foundation

import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.tokens.DesignTokens
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Centralized resolution of surface-level → background color.
 * Eliminates duplication across SurfaceStyleResolver, PanelStyleResolver,
 * and SegmentedControlStyleResolver.
 *
 * SSR SAFETY: stateless singleton, pure functions.
 */
@JsExport
object SurfaceLevelResolver {

	fun resolveColor(level: SurfaceLevel, tokens: DesignTokens): String =
		resolveColorByIndex(level.ordinal, tokens)

	@JsName("resolveColorByIndex")
	fun resolveColor(level: Int, tokens: DesignTokens): String =
		resolveColorByIndex(level, tokens)

	fun resolveSoftColor(level: SurfaceLevel, tokens: DesignTokens): String =
		when (level) {
			SurfaceLevel.Level0 -> tokens.color.surface
			SurfaceLevel.Level1 -> tokens.color.surface
			SurfaceLevel.Level2 -> tokens.color.surfaceContainerLowest
			SurfaceLevel.Level3 -> tokens.color.surfaceContainerLow
			SurfaceLevel.Level4 -> tokens.color.surfaceContainer
			SurfaceLevel.Level5 -> tokens.color.surfaceContainerHigh
		}

	private fun resolveColorByIndex(level: Int, tokens: DesignTokens): String = when (level) {
		0 -> tokens.color.surface
		1 -> tokens.color.surfaceContainerLowest
		2 -> tokens.color.surfaceContainerLow
		3 -> tokens.color.surfaceContainer
		4 -> tokens.color.surfaceContainerHigh
		5 -> tokens.color.surfaceContainerHighest
		else -> tokens.color.surfaceContainerHighest
	}
}
