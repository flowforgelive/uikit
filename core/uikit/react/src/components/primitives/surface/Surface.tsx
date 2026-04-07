"use client";

import React, { useMemo } from "react";
import { SurfaceView } from "./SurfaceView";
import { SurfaceConfig, SurfaceLevel, SurfaceShape } from "uikit-common";
import { VARIANT_MAP } from "../../../utils/enumMaps";

const LEVEL_MAP = {
	0: SurfaceLevel.Level0,
	1: SurfaceLevel.Level1,
	2: SurfaceLevel.Level2,
	3: SurfaceLevel.Level3,
	4: SurfaceLevel.Level4,
	5: SurfaceLevel.Level5,
} as const;

const SHAPE_MAP = {
	none: SurfaceShape.None,
	sm: SurfaceShape.Sm,
	md: SurfaceShape.Md,
	lg: SurfaceShape.Lg,
	xl: SurfaceShape.Xl,
	full: SurfaceShape.Full,
} as const;

interface SurfaceProps {
	variant?: keyof typeof VARIANT_MAP;
	level?: keyof typeof LEVEL_MAP;
	shape?: keyof typeof SHAPE_MAP;
	elevated?: boolean;
	hoverable?: boolean;
	clickable?: boolean;
	onClick?: () => void;
	className?: string;
	children?: React.ReactNode;
}

export const Surface: React.FC<SurfaceProps> = React.memo(
	({
		variant = "solid",
		level = 2,
		shape = "md",
		elevated = false,
		hoverable = false,
		clickable = false,
		onClick,
		className,
		children,
	}) => {
		const config = useMemo(
			() =>
				new SurfaceConfig(
					VARIANT_MAP[variant],
					LEVEL_MAP[level],
					SHAPE_MAP[shape],
					elevated,
					clickable,
					hoverable,
				),
			[variant, level, shape, elevated, clickable, hoverable],
		);

		return (
			<SurfaceView config={config} onClick={onClick} className={className}>
				{children}
			</SurfaceView>
		);
	},
);

Surface.displayName = "Surface";
