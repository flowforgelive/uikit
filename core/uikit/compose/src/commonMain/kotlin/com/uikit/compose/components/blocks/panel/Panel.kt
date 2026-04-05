package com.uikit.compose.components.blocks.panel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.blocks.panel.PanelCollapsible
import com.uikit.components.blocks.panel.PanelConfig
import com.uikit.components.blocks.panel.PanelSide
import com.uikit.components.blocks.panel.PanelVariant
import com.uikit.components.primitives.surface.SurfaceLevel

@Composable
fun Panel(
	variant: PanelVariant = PanelVariant.Pinned,
	side: PanelSide = PanelSide.Left,
	collapsible: PanelCollapsible = PanelCollapsible.Offcanvas,
	isOpen: Boolean = true,
	width: Double = 256.0,
	height: Double = 200.0,
	collapsedWidth: Double = 48.0,
	surfaceLevel: SurfaceLevel = SurfaceLevel.Level1,
	elevated: Boolean = false,
	showBorder: Boolean = true,
	onToggle: (() -> Unit)? = null,
	testTag: String? = null,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	PanelView(
		config = PanelConfig(
			variant = variant,
			side = side,
			collapsible = collapsible,
			isOpen = isOpen,
			width = width,
			height = height,
			collapsedWidth = collapsedWidth,
			surfaceLevel = surfaceLevel,
			elevated = elevated,
			showBorder = showBorder,
			testTag = testTag,
		),
		onToggle = onToggle,
		modifier = modifier,
		content = content,
	)
}
