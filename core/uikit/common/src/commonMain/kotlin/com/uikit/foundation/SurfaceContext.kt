package com.uikit.foundation

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Propagated context describing the current surface a component is rendered on.
 * Used by child components (Button, etc.) to adapt colors for contrast against the surface.
 */
@JsExport
@Serializable
data class SurfaceContext(
	val level: Int,
	val backgroundColor: String,
)

@JsExport
val DefaultSurfaceContext = SurfaceContext(level = 0, backgroundColor = "#FFFFFF")
