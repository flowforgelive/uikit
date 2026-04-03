package com.uikit.components.atoms.segmentedcontrol

import com.uikit.foundation.ComponentSize
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant
import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class SegmentedControlOption(
	val id: String,
	val label: String,
	val iconId: String? = null,
)

@JsExport
@Serializable
data class SegmentedControlConfig(
	val options: Array<SegmentedControlOption>,
	val selectedId: String,
	val size: ComponentSize = ComponentSize.Sm,
	val variant: VisualVariant = VisualVariant.Surface,
	val iconPosition: IconPosition = IconPosition.None,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is SegmentedControlConfig) return false
		return id == other.id &&
			options.contentEquals(other.options) &&
			selectedId == other.selectedId &&
			size == other.size &&
			variant == other.variant &&
			iconPosition == other.iconPosition &&
			testTag == other.testTag &&
			visibility == other.visibility
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + options.contentDeepHashCode()
		result = 31 * result + selectedId.hashCode()
		result = 31 * result + size.hashCode()
		result = 31 * result + variant.hashCode()
		result = 31 * result + iconPosition.hashCode()
		result = 31 * result + (testTag?.hashCode() ?: 0)
		result = 31 * result + visibility.hashCode()
		return result
	}
}
