package com.uikit.compose.components.atoms.segmentedcontrol

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlConfig
import com.uikit.components.atoms.segmentedcontrol.SegmentedControlOption
import com.uikit.foundation.ComponentSize

@Composable
fun SegmentedControl(
	options: List<Pair<String, String>>,
	selectedId: String,
	onSelectionChange: (String) -> Unit = {},
	size: ComponentSize = ComponentSize.Sm,
	testTag: String? = null,
	modifier: Modifier = Modifier,
) {
	SegmentedControlView(
		config =
			SegmentedControlConfig(
				options =
					options
						.map { SegmentedControlOption(it.first, it.second) }
						.toTypedArray(),
				selectedId = selectedId,
				size = size,
				testTag = testTag,
			),
		onSelectionChange = onSelectionChange,
		modifier = modifier,
	)
}
