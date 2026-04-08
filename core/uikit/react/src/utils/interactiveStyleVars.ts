import type { DesignTokens } from "uikit-common";

/**
 * Builds interactive-state CSS custom properties from tokens.
 * These are token-derived vars that are identical across interactive components
 * (Button, Chip, Surface) and only differ by prefix.
 *
 * @param tokens - design tokens
 * @param prefix - CSS variable prefix (e.g., "btn", "chip", "surface")
 * @returns Record of CSS custom properties to spread into inline style
 */
export function buildInteractiveStyleVars(
	tokens: DesignTokens,
	prefix: string,
): Record<string, string> {
	return {
		[`--${prefix}-duration`]: `${tokens.motion.durationFast}ms`,
		[`--${prefix}-easing`]: tokens.motion.easingStandard,
		[`--${prefix}-focus-ring`]: tokens.color.focusRing,
		[`--${prefix}-border-width`]: `${tokens.borderWidth}px`,
		[`--${prefix}-focus-ring-width`]: `${tokens.focusRingWidth}px`,
		[`--${prefix}-press-opacity`]: `${tokens.state.pressOpacity}`,
		[`--${prefix}-press-brightness`]: `${tokens.state.pressBrightness}`,
		[`--${prefix}-ripple-spread`]: `${tokens.state.rippleSpread}%`,
		[`--${prefix}-ripple-fade-duration`]: `${tokens.state.rippleFadeDurationMs}ms`,
	};
}

/**
 * Extended interactive vars for components with spinner support (Button, Chip).
 */
export function buildSpinnerStyleVars(
	tokens: DesignTokens,
	prefix: string,
): Record<string, string> {
	return {
		[`--${prefix}-spinner-duration`]: `${tokens.motion.durationSpinner}ms`,
		[`--${prefix}-spinner-stroke`]: `${tokens.spinnerStrokeWidth}px`,
		[`--${prefix}-disabled-opacity`]: `${tokens.state.disabledOpacity}`,
	};
}
