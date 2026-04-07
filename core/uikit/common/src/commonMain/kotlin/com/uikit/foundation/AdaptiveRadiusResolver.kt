package com.uikit.foundation

import com.uikit.tokens.ControlProportions
import kotlin.js.JsExport

/**
 * Centralized adaptive radius computation for container components
 * (Image, Skeleton, Card, Avatar, Badge, etc.).
 *
 * SSR SAFETY: stateless singleton, pure function.
 */
@JsExport
object AdaptiveRadiusResolver {
	/**
	 * Resolves corner radius for a container component.
	 *
	 * @param explicitRadius explicit override from config (null = adaptive)
	 * @param containerDimension the dimension to base radius on (min(w,h) for Image, height for Skeleton)
	 * @param proportions token proportions with radiusFraction and maxContainerRadius
	 * @return resolved radius value in dp
	 */
	fun resolve(
		explicitRadius: Double?,
		containerDimension: Double,
		proportions: ControlProportions,
	): Double {
		if (explicitRadius != null) return explicitRadius
		if (containerDimension <= 0.0) return 0.0
		val adaptive = containerDimension * proportions.radiusFraction
		return minOf(adaptive, proportions.maxContainerRadius)
	}
}
