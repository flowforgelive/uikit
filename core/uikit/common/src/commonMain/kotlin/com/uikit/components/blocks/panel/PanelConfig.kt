package com.uikit.components.blocks.panel

import com.uikit.components.primitives.surface.SurfaceLevel
import com.uikit.foundation.Visibility
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Visual variant of the Panel.
 * - Pinned: flush against the edge, no rounding, full height (macOS-style sidebar)
 * - Inset: Apple-style with padding from edges, rounded corners (iPadOS-style)
 */
@JsExport
@Serializable
enum class PanelVariant {
	Pinned,
	Inset,
}

/**
 * Which edge the Panel is attached to.
 */
@JsExport
@Serializable
enum class PanelSide {
	Left,
	Right,
	Top,
	Bottom,
}

/**
 * How the Panel collapses when closed.
 * - Offcanvas: fully hidden (width → 0), content takes all space
 * - Icon: collapses to a narrow rail (collapsedWidth), shows icons only
 * - None: always expanded, cannot be collapsed
 */
@JsExport
@Serializable
enum class PanelCollapsible {
	Offcanvas,
	Icon,
	None,
}

@JsExport
@Serializable
data class PanelConfig(
	val variant: PanelVariant = PanelVariant.Pinned,
	val side: PanelSide = PanelSide.Left,
	val collapsible: PanelCollapsible = PanelCollapsible.Offcanvas,
	val isOpen: Boolean = true,
	val width: Double = 256.0,
	val height: Double = 200.0,
	val collapsedWidth: Double = 48.0,
	val surfaceLevel: SurfaceLevel = SurfaceLevel.Level1,
	val elevated: Boolean = false,
	val showBorder: Boolean = true,
	val id: String = "",
	val testTag: String? = null,
	val visibility: Visibility = Visibility.Visible,
) {
	val isHorizontal: Boolean
		get() = side == PanelSide.Top || side == PanelSide.Bottom

	val currentWidth: Double
		get() = if (isOpen) width else when (collapsible) {
			PanelCollapsible.Offcanvas -> 0.0
			PanelCollapsible.Icon -> collapsedWidth
			PanelCollapsible.None -> width
		}

	val currentHeight: Double
		get() = if (isOpen) height else when (collapsible) {
			PanelCollapsible.Offcanvas -> 0.0
			PanelCollapsible.Icon -> collapsedWidth
			PanelCollapsible.None -> height
		}
}
