"use client";

import React, { useMemo } from "react";
import { ButtonView } from "./ButtonView";
import {
	ButtonConfig,
	ComponentSize,
	ColorIntent,
	VisualVariant,
} from "uikit-common";

const VARIANT_MAP = {
	solid: VisualVariant.Solid,
	soft: VisualVariant.Soft,
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

interface ButtonProps {
	text: string;
	onClick?: () => void;
	variant?: keyof typeof VARIANT_MAP;
	intent?: keyof typeof INTENT_MAP;
	size?: keyof typeof SIZE_MAP;
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
					disabled,
					loading,
				),
			[text, variant, intent, size, disabled, loading],
		);

		return (
			<ButtonView config={config} onClick={onClick} className={className} />
		);
	},
);

Button.displayName = "Button";
