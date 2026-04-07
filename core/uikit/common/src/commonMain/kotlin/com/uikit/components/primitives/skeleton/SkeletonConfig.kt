package com.uikit.components.primitives.skeleton

import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Shape variants for the Skeleton placeholder.
 *
 * - Rectangle: general-purpose block placeholder (cards, images, containers).
 * - Circle: avatar / icon placeholder.
 * - TextLine: single line of text placeholder (narrow height, small radius).
 */
@JsExport
@Serializable
enum class SkeletonShape {
	Rectangle,
	Circle,
	TextLine,
}

/**
 * Configuration for the Skeleton primitive.
 *
 * Skeleton renders an animated placeholder that indicates loading content.
 * Supports three shapes, customizable dimensions, and optional shimmer animation.
 *
 * @param shape Visual form: Rectangle, Circle, or TextLine.
 * @param width Explicit width in dp. 0.0 means fill available width.
 * @param height Explicit height in dp. 0.0 defaults per shape (Circle → width, TextLine → bodyMedium lineHeight).
 * @param cornerRadius Explicit corner radius in dp. When null, derived from shape (Circle → full round, TextLine → radius.xs, Rectangle → radius.sm).
 * @param animate Whether to play the shimmer animation. Disable for static placeholders.
 */
@JsExport
@Serializable
data class SkeletonConfig(
	val shape: SkeletonShape = SkeletonShape.Rectangle,
	val width: Double = 0.0,
	val height: Double = 0.0,
	val cornerRadius: Double? = null,
	val animate: Boolean = true,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
)
