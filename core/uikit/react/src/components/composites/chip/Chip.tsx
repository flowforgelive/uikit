"use client";

import React, { useMemo } from "react";
import { ChipView } from "./ChipView";
import { ChipConfig } from "uikit-common";
import { VARIANT_MAP, INTENT_MAP, SIZE_MAP } from "../../../utils/enumMaps";

interface ChipProps {
	text: string;
	onClick?: () => void;
	onDismiss?: () => void;
	variant?: "solid" | "soft" | "outline" | "ghost" | "glass";
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
