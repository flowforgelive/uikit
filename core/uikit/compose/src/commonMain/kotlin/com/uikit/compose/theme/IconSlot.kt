package com.uikit.compose.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.uikit.foundation.IconPosition

/**
 * Sized icon container with center alignment.
 * Replaces `Box(modifier = Modifier.size(iconSize), contentAlignment = Alignment.Center) { ... }`
 */
@Composable
fun IconSlot(
	size: Dp,
	content: @Composable () -> Unit,
) {
	Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
		content()
	}
}

/**
 * Layout composable that arranges icon + text based on [IconPosition].
 * Handles Row (Start/End) and Column (Top/Bottom) branching.
 *
 * Replaces ~25 lines of Row/Column branching in ButtonContent and SegmentedControlView.
 */
@Composable
fun IconTextLayout(
	iconPosition: IconPosition,
	iconGap: Dp,
	icon: @Composable () -> Unit,
	text: @Composable () -> Unit,
	modifier: Modifier = Modifier,
) {
	val isVertical = iconPosition == IconPosition.Top || iconPosition == IconPosition.Bottom
	val iconFirst = iconPosition == IconPosition.Top || iconPosition == IconPosition.Start

	if (isVertical) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
			modifier = modifier,
		) {
			if (iconFirst) {
				icon()
				Spacer(Modifier.height(iconGap))
				text()
			} else {
				text()
				Spacer(Modifier.height(iconGap))
				icon()
			}
		}
	} else {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
		) {
			if (iconFirst) {
				icon()
				Spacer(Modifier.width(iconGap))
				text()
			} else {
				text()
				Spacer(Modifier.width(iconGap))
				icon()
			}
		}
	}
}
