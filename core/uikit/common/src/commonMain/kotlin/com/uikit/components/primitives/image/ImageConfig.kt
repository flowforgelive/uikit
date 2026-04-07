package com.uikit.components.primitives.image

import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class ImageFit {
	Cover,
	Contain,
	Fill,
	None,
	ScaleDown,
}

@JsExport
@Serializable
enum class ImageLoading {
	Eager,
	Lazy,
}

/**
 * Configuration for the Image primitive.
 *
 * Image renders a visual asset with optional sizing, fit mode, corner radius,
 * placeholder/fallback support, and lazy loading.
 *
 * @param src Image URL or data URI.
 * @param alt Accessibility description.
 * @param width Explicit width in dp. When 0.0, the image is intrinsically sized.
 * @param height Explicit height in dp. When 0.0, the image is intrinsically sized.
 * @param objectFit How the image fills its container.
 * @param cornerRadius Corner radius in dp. When null (default), adapts to global radiusFraction: min(width, height) × radiusFraction.
 *   Set explicitly to override (e.g. 0.0 for sharp corners, 40.0 for circle).
 * @param showBorder When true, renders a subtle inset border to separate the image from the background.
 * @param placeholder URL/data URI shown while loading.
 * @param fallback URL/data URI shown on load error.
 * @param loading Eager or Lazy loading strategy.
 */
@JsExport
@Serializable
data class ImageConfig(
	val src: String = "",
	val alt: String = "",
	val width: Double = 0.0,
	val height: Double = 0.0,
	val objectFit: ImageFit = ImageFit.Cover,
	val cornerRadius: Double? = null,
	val showBorder: Boolean = false,
	val placeholder: String? = null,
	val fallback: String? = null,
	val loading: ImageLoading = ImageLoading.Eager,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val hasExplicitWidth: Boolean get() = width > 0.0
	val hasExplicitHeight: Boolean get() = height > 0.0
	val hasPlaceholder: Boolean get() = !placeholder.isNullOrEmpty()
	val hasFallback: Boolean get() = !fallback.isNullOrEmpty()
}
