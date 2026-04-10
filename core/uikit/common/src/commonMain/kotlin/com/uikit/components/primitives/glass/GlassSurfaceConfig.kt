package com.uikit.components.primitives.glass

import com.uikit.components.primitives.surface.SurfaceShape
import com.uikit.foundation.GlassVariant
import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class GlassSurfaceConfig(
	val variant: GlassVariant = GlassVariant.Regular,
	val tintColor: String = "",
	val shape: SurfaceShape = SurfaceShape.Md,
	val elevated: Boolean = false,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
)
