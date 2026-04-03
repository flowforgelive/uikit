package com.uikit.compose.components.atoms.segmentedcontrol

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlConfig
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlOption
import com.uikit.foundation.ComponentSize
import com.uikit.foundation.IconPosition
import com.uikit.foundation.VisualVariant

@Composable
fun SegmentedControl(
	options: List<Pair<String, String>>,
	selectedId: String,
	onSelectionChange: (String) -> Unit = {},
	size: ComponentSize = ComponentSize.Sm,
	variant: VisualVariant = VisualVariant.Surface,
	iconPosition: IconPosition = IconPosition.None,
	icons: Map<String, @Composable () -> Unit> = emptyMap(),
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	val hasIcons = iconPosition != IconPosition.None && icons.isNotEmpty()
	SegmentedControlView(
		config =
			SegmentedControlConfig(
				options =
					options
						.map { SegmentedControlOption(it.first, it.second, if (hasIcons) it.first else null) }
						.toTypedArray(),
				selectedId = selectedId,
				size = size,
				variant = variant,
				iconPosition = if (hasIcons) iconPosition else IconPosition.None,
				testTag = testTag,
			),
		onSelectionChange = onSelectionChange,
		renderIcon = if (hasIcons) { iconId -> icons[iconId]?.invoke() } else null,
		modifier = modifier,
	)
}
