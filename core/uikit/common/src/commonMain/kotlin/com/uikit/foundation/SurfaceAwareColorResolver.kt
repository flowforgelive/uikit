package com.uikit.foundation

import com.uikit.tokens.ColorTokens
import kotlin.js.JsExport

/**
 * Resolves surface-aware colors for Soft and Ghost variants.
 * Chooses a bg color that provides sufficient contrast against the current surface.
 */
@JsExport
object SurfaceAwareColorResolver {

	/**
	 * Returns an adjusted Soft background color that contrasts with the current surface.
	 * Picks from the surface container scale, choosing one that differs from [surfaceBg].
	 */
	fun resolveSoftBg(
		surfaceBg: String,
		colors: ColorTokens,
		isPrimary: Boolean,
	): String {
		// Surface container scale from lightest to darkest (light theme)
		// or darkest to lightest (dark theme) — both go "up" in visual weight.
		val scale = listOf(
			colors.surfaceContainerLowest,
			colors.surfaceContainerLow,
			colors.surfaceContainer,
			colors.surfaceContainerHigh,
			colors.surfaceContainerHighest,
		)

		val currentIndex = scale.indexOf(surfaceBg)

		return if (isPrimary) {
			// Primary soft: always use containerHigh unless that IS the surface
			if (colors.surfaceContainerHigh == surfaceBg) {
				colors.surfaceContainerHighest
			} else {
				colors.surfaceContainerHigh
			}
		} else {
			// Neutral soft: pick 2 steps up from current surface, clamped
			when {
				currentIndex < 0 -> colors.surfaceContainerLow // unknown surface → default
				currentIndex + 2 < scale.size -> scale[currentIndex + 2]
				else -> scale.last()
			}
		}
	}

	/**
	 * Returns an adjusted Soft hover bg — one step darker than the soft bg.
	 */
	fun resolveSoftBgHover(
		softBg: String,
		colors: ColorTokens,
	): String {
		val scale = listOf(
			colors.surfaceContainerLowest,
			colors.surfaceContainerLow,
			colors.surfaceContainer,
			colors.surfaceContainerHigh,
			colors.surfaceContainerHighest,
			colors.surfaceHover,
		)

		val index = scale.indexOf(softBg)
		return if (index >= 0 && index + 1 < scale.size) {
			scale[index + 1]
		} else {
			colors.surfaceHover
		}
	}
}
