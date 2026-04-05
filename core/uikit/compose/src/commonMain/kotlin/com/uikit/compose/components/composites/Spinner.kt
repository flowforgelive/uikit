package com.uikit.compose.components.composites

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * CSS-compatible spinner: 270° arc rotating 360°.
 * Matches React's CSS border-spinner exactly:
 *   border: Npx solid currentColor;
 *   border-top-color: transparent;
 * This draws 3/4 of a circle (270°) and rotates it continuously.
 */
@Composable
fun Spinner(
	size: Dp,
	color: Color,
	strokeWidth: Dp,
	durationMs: Int,
	modifier: Modifier = Modifier,
) {
	val transition = rememberInfiniteTransition(label = "spinner")
	val rotation by transition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = durationMs, easing = LinearEasing),
		),
		label = "rotation",
	)

	Canvas(modifier = modifier.size(size)) {
		drawArc(
			color = color,
			startAngle = rotation - 90f,
			sweepAngle = 270f,
			useCenter = false,
			style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt),
		)
	}
}
