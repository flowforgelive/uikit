package com.uikit.compose.theme

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * Shared styled text composable for interactive components.
 * Replaces ~10 lines of BasicText + TextStyle boilerplate per usage
 * in ButtonView, ChipView, SegmentedControlView.
 */
@Composable
fun StyledText(
	text: String,
	fontSize: Double,
	fontWeight: Int,
	letterSpacing: Double,
	lineHeight: Double,
	color: Color,
	fontFamily: FontFamily,
	textAlign: TextAlign = TextAlign.Start,
	maxLines: Int = Int.MAX_VALUE,
) {
	BasicText(
		text = text,
		style = TextStyle(
			fontSize = fontSize.sp,
			fontWeight = FontWeight(fontWeight),
			letterSpacing = letterSpacing.sp,
			lineHeight = lineHeight.sp,
			color = color,
			fontFamily = fontFamily,
			textAlign = textAlign,
		),
		maxLines = maxLines,
		softWrap = maxLines > 1,
	)
}
