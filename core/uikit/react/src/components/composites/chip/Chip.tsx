"use client";

import React, { useMemo } from "react";
import { ChipView } from "./ChipView";
import {
	ChipConfig,
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

interface ChipProps {
	text: string;
	onClick?: () => void;
	onDismiss?: () => void;
	variant?: keyof typeof VARIANT_MAP;
	intent?: keyof typeof INTENT_MAP;
	size?: keyof typeof SIZE_MAP;
	leadingIcon?: React.ReactNode;
	dismissible?: boolean;
	selected?: boolean;
	disabled?: boolean;
	loading?: boolean;
	className?: string;
}

export const Chip: React.FC<ChipProps> = React.memo(
	({
		text,
		onClick,
		onDismiss,
		variant = "soft",
		intent = "neutral",
		size = "md",
		leadingIcon,
		dismissible = false,
		selected = false,
		disabled = false,
		loading = false,
		className,
	}) => {
		const config = useMemo(
			() =>
				new ChipConfig(
					text,
					VARIANT_MAP[variant],
					INTENT_MAP[intent],
					SIZE_MAP[size],
					!!leadingIcon,
					dismissible,
					selected,
					disabled,
					loading,
				),
			[text, variant, intent, size, !!leadingIcon, dismissible, selected, disabled, loading],
		);

		return (
			<ChipView
				config={config}
				leadingIcon={leadingIcon}
				onClick={onClick}
				onDismiss={onDismiss}
				className={className}
			/>
		);
	},
);

Chip.displayName = "Chip";
