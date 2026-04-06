package com.uikit.compose.theme

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

/**
 * CompositionLocal for anti-stacking hover behavior.
 *
 * When a child interactive component is hovered, it sets this to `true`,
 * so the parent can suppress its own hover visual.
 */
val LocalChildHoverState = compositionLocalOf<MutableState<Boolean>> { mutableStateOf(false) }
