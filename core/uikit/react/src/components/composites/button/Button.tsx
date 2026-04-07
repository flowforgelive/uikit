"use client";

import React, { useMemo } from "react";
import { ButtonView } from "./ButtonView";
import { ButtonConfig } from "uikit-common";
import { VARIANT_MAP, INTENT_MAP, SIZE_MAP, ICON_POSITION_MAP } from "../../../utils/enumMaps";

interface ButtonProps {
	text?: string;
	onClick?: () => void;
	variant?: keyof typeof VARIANT_MAP;
	intent?: keyof typeof INTENT_MAP;
	size?: keyof typeof SIZE_MAP;
	icon?: React.ReactNode;
	iconPosition?: keyof typeof ICON_POSITION_MAP;
	iconStart?: React.ReactNode;
	iconEnd?: React.ReactNode;
	disabled?: boolean;
	loading?: boolean;
	ariaLabel?: string;
	className?: string;
}

export const Button: React.FC<ButtonProps> = React.memo(
	({
		text = "",
		onClick,
		variant = "solid",
		intent = "primary",
		size = "md",
		icon,
		iconPosition = "none",
		iconStart,
		iconEnd,
		disabled = false,
		loading = false,
		ariaLabel,
		className,
	}) => {
		const effectivePosition = icon && iconPosition === "none" ? "start" : iconPosition;
		const effectiveIconStart = icon && effectivePosition !== "end" ? icon : iconStart;
		const effectiveIconEnd = icon && effectivePosition === "end" ? icon : iconEnd;

		const config = useMemo(
			() =>
				new ButtonConfig(
					text,
					VARIANT_MAP[variant],
					INTENT_MAP[intent],
					SIZE_MAP[size],
					ICON_POSITION_MAP[effectivePosition],
					!!effectiveIconStart,
					!!effectiveIconEnd,
					disabled,
					loading,
					undefined,
					undefined,
					undefined,
					undefined,
					ariaLabel,
				),
			[text, variant, intent, size, effectivePosition, !!effectiveIconStart, !!effectiveIconEnd, disabled, loading, ariaLabel],
		);

		return (
			<ButtonView config={config} iconStart={effectiveIconStart} iconEnd={effectiveIconEnd} onClick={onClick} className={className} />
		);
	},
);

Button.displayName = "Button";
