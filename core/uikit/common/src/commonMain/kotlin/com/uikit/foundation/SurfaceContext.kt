package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Propagated context describing the current surface a component is rendered on.
 * Used by child components (Button, etc.) to adapt colors for contrast against the surface.
 *
 * [nestingDepth] tracks how many interactive components are nested above the current one.
 * Used by [InteractiveColorResolver] to shift Soft bg along a tonal staircase,
 * ensuring each nesting level has a distinct background color.
 */
@JsExport
@Serializable
data class SurfaceContext(
	val level: Int,
	val backgroundColor: String,
	val nestingDepth: Int = 0,
)

@JsExport
val DefaultSurfaceContext = SurfaceContext(level = 0, backgroundColor = "transparent", nestingDepth = 0)
