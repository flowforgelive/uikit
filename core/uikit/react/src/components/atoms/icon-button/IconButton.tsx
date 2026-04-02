"use client";

import React, { useMemo } from "react";
import { IconButtonView } from "./IconButtonView";
import {
	IconButtonConfig,
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

interface IconButtonProps {
	icon: React.ReactNode;
	onClick?: () => void;
	variant?: keyof typeof VARIANT_MAP;
	intent?: keyof typeof INTENT_MAP;
	size?: keyof typeof SIZE_MAP;
	disabled?: boolean;
	loading?: boolean;
	ariaLabel?: string;
	className?: string;
}

export const IconButton: React.FC<IconButtonProps> = React.memo(
	({
		icon,
		onClick,
		variant = "solid",
		intent = "primary",
		size = "md",
		disabled = false,
		loading = false,
		ariaLabel,
		className,
	}) => {
		const config = useMemo(
			() =>
				new IconButtonConfig(
					VARIANT_MAP[variant],
					INTENT_MAP[intent],
					SIZE_MAP[size],
					disabled,
					loading,
					undefined,
					undefined,
					undefined,
					undefined,
					ariaLabel,
				),
			[variant, intent, size, disabled, loading, ariaLabel],
		);

		return (
			<IconButtonView
				config={config}
				icon={icon}
				onClick={onClick}
				className={className}
			/>
		);
	},
);

IconButton.displayName = "IconButton";
