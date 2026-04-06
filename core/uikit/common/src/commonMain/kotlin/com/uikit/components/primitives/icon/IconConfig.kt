package com.uikit.components.primitives.icon

import com.uikit.foundation.ComponentSize
import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Configuration for the Icon primitive.
 *
 * Icon renders a visual symbol at a system-defined size with an inherited or explicit color.
 * It has no interactivity — click/hover is the responsibility of the parent component.
 *
 * @param name Semantic icon identifier (e.g. "plus", "close"). Used for BDUI icon registry lookups.
 * @param size Resolved via [ComponentSizeResolver] → [ControlSizeScale.iconSize].
 * @param customSize Explicit size in dp. When set, overrides [size]-based resolution.
 * @param color Explicit color hex. When null, inherits from parent (CSS currentColor / LocalContentColor).
 * @param ariaHidden When true, the icon is decorative and hidden from screen readers.
 */
@JsExport
@Serializable
data class IconConfig(
	val name: String = "",
	val size: ComponentSize = ComponentSize.Md,
	val customSize: Double = 0.0,
	val color: String? = null,
	val ariaHidden: Boolean = true,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val hasCustomSize: Boolean get() = customSize > 0.0
}
