package com.uikit.components.atoms.surface

import com.uikit.foundation.Visibility
import com.uikit.foundation.VisualVariant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class SurfaceLevel {
	Level0,
	Level1,
	Level2,
	Level3,
	Level4,
	Level5,
}

@JsExport
@Serializable
enum class SurfaceShape {
	None,
	Sm,
	Md,
	Lg,
	Xl,
	Full,
}

@JsExport
@Serializable
data class SurfaceConfig(
	val variant: VisualVariant = VisualVariant.Solid,
	val level: SurfaceLevel = SurfaceLevel.Level2,
	val shape: SurfaceShape = SurfaceShape.Md,
	val elevated: Boolean = false,
	val clickable: Boolean = false,
	val hoverable: Boolean = false,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
)
