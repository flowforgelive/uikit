/**
 * Shared string → KMP enum mappings for convenience React components.
 * Each component imports the relevant maps instead of defining local copies.
 */
import {
	ComponentSize,
	ColorIntent,
	VisualVariant,
	IconPosition,
} from "uikit-common";

export const VARIANT_MAP = {
	solid: VisualVariant.Solid,
	soft: VisualVariant.Soft,
	surface: VisualVariant.Surface,
	outline: VisualVariant.Outline,
	ghost: VisualVariant.Ghost,
	glass: VisualVariant.Glass,
} as const;

export const INTENT_MAP = {
	primary: ColorIntent.Primary,
	neutral: ColorIntent.Neutral,
	danger: ColorIntent.Danger,
} as const;

export const SIZE_MAP = {
	xs: ComponentSize.Xs,
	sm: ComponentSize.Sm,
	md: ComponentSize.Md,
	lg: ComponentSize.Lg,
	xl: ComponentSize.Xl,
} as const;

export const ICON_POSITION_MAP = {
	none: IconPosition.None,
	start: IconPosition.Start,
	end: IconPosition.End,
	top: IconPosition.Top,
	bottom: IconPosition.Bottom,
} as const;
