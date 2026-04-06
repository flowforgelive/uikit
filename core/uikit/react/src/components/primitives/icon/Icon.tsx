"use client";

import React, { useMemo } from "react";
import { IconView } from "./IconView";
import { IconConfig, ComponentSize } from "uikit-common";

const SIZE_MAP = {
	xs: ComponentSize.Xs,
	sm: ComponentSize.Sm,
	md: ComponentSize.Md,
	lg: ComponentSize.Lg,
	xl: ComponentSize.Xl,
} as const;

interface IconProps {
	name?: string;
	size?: keyof typeof SIZE_MAP;
	customSize?: number;
	color?: string;
	ariaHidden?: boolean;
	className?: string;
	children?: React.ReactNode;
}

export const Icon: React.FC<IconProps> = React.memo(
	({
		name = "",
		size = "md",
		customSize,
		color,
		ariaHidden = true,
		className,
		children,
	}) => {
		const config = useMemo(
			() =>
				new IconConfig(
					name,
					SIZE_MAP[size],
					customSize ?? 0,
					color ?? null,
					ariaHidden,
				),
			[name, size, customSize, color, ariaHidden],
		);

		return (
			<IconView config={config} className={className}>
				{children}
			</IconView>
		);
	},
);

Icon.displayName = "Icon";
