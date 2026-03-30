package com.uikit.tokens

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Motion/animation tokens based on Material Design 3 + Apple HIG standards.
 *
 * Durations (Int, milliseconds):
 * - instant: micro-interactions (ripples, highlights)
 * - fast: button state changes, toggles, small feedback
 * - normal: expanding content, tab switching, segmented controls
 * - slow: sheets, modals, panels entering/exiting
 * - slower: page transitions, complex multi-element animations
 *
 * Easings (String, CSS cubic-bezier format):
 * - standard: general-purpose (Material 3 standard)
 * - decelerate: elements entering the screen
 * - accelerate: elements leaving the screen
 * - emphasizedDecelerate: hero transitions, focal elements entering
 * - emphasizedAccelerate: focal elements exiting
 * - linear: continuous animations (spinners, progress)
 *
 * In React: use easing strings directly in CSS transitions.
 * In Compose: parse via CubicBezierEasing or map to built-in easings.
 */
@JsExport
@Serializable
data class MotionTokens(
	val durationInstant: Int,
	val durationFast: Int,
	val durationNormal: Int,
	val durationSlow: Int,
	val durationSlower: Int,
	val easingStandard: String,
	val easingDecelerate: String,
	val easingAccelerate: String,
	val easingEmphasizedDecelerate: String,
	val easingEmphasizedAccelerate: String,
	val easingLinear: String,
)
