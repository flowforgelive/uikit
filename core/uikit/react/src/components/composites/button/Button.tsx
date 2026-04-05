"use client";

import React, { useMemo } from "react";
import { ButtonView } from "./ButtonView";
import {
	ButtonConfig,
	ComponentSize,
	ColorIntent,
	VisualVariant,
	IconPosition,
} from "uikit-common";

const VARIANT_MAP = {
	solid: VisualVariant.Solid,
	soft: VisualVariant.Soft,
	surface: VisualVariant.Surface,
	outline: VisualVariant.Outline,
	ghost: VisualVariant.Ghost,
} as const;

const INTENT_MAP = {
	primary: ColorIntent.Primary,
	neutral: ColorIntent.Neutral,
	danger: ColorIntent.Danger,
} as const;

const SIZE_MAP = {
	xs: ComponentSize.Xs,
	sm: ComponentSize.Sm,
	md: ComponentSize.Md,
	lg: ComponentSize.Lg,
	xl: ComponentSize.Xl,
} as const;

const ICON_POSITION_MAP = {
	none: IconPosition.None,
	start: IconPosition.Start,
	end: IconPosition.End,
	top: IconPosition.Top,
	bottom: IconPosition.Bottom,
} as const;

interface ButtonProps {
	text: string;
	onClick?: () => void;
	variant?: keyof typeof VARIANT_MAP;
	intent?: keyof typeof INTENT_MAP;
	size?: keyof typeof SIZE_MAP;
	iconPosition?: keyof typeof ICON_POSITION_MAP;
	iconStart?: React.ReactNode;
	iconEnd?: React.ReactNode;
	disabled?: boolean;
	loading?: boolean;
	className?: string;
}

export const Button: React.FC<ButtonProps> = React.memo(
	({
		text,
		onClick,
		variant = "solid",
		intent = "primary",
		size = "md",
		iconPosition = "none",
		iconStart,
		iconEnd,
		disabled = false,
		loading = false,
		className,
	}) => {
		const config = useMemo(
			() =>
				new ButtonConfig(
					text,
					VARIANT_MAP[variant],
					INTENT_MAP[intent],
					SIZE_MAP[size],
					ICON_POSITION_MAP[iconPosition],
					!!iconStart,
					!!iconEnd,
					disabled,
					loading,
				),
			[text, variant, intent, size, iconPosition, !!iconStart, !!iconEnd, disabled, loading],
		);

		return (
			<ButtonView config={config} iconStart={iconStart} iconEnd={iconEnd} onClick={onClick} className={className} />
		);
	},
);

Button.displayName = "Button";
