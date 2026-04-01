package com.uikit.compose.theme

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout

/**
 * Tracks whether the last user interaction was keyboard-based (Tab navigation).
 * Compose Desktop analogue of CSS :focus-visible heuristic.
 *
 * - Tab key press → true (show focus rings)
 * - Any pointer event → false (hide focus rings)
 */
val LocalKeyboardNavigationMode = compositionLocalOf<MutableState<Boolean>> { mutableStateOf(false) }

@Composable
fun KeyboardNavigationHandler(content: @Composable () -> Unit) {
	val keyboardMode = remember { mutableStateOf(false) }

	CompositionLocalProvider(LocalKeyboardNavigationMode provides keyboardMode) {
		Layout(
			content = content,
			modifier = Modifier
				.onPreviewKeyEvent { event ->
					if (event.key == Key.Tab) {
						keyboardMode.value = true
					}
					false
				}
				.pointerInput(Unit) {
					awaitEachGesture {
						awaitFirstDown(pass = PointerEventPass.Initial)
						keyboardMode.value = false
					}
				},
		) { measurables, constraints ->
			val placeable = measurables.first().measure(constraints)
			layout(placeable.width, placeable.height) {
				placeable.place(0, 0)
			}
		}
	}
}
