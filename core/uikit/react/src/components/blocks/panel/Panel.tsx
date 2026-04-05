"use client";

import React, { useMemo } from "react";
import { PanelView } from "./PanelView";
import {
	PanelConfig,
	PanelVariant,
	PanelSide,
	PanelCollapsible,
	SurfaceLevel,
} from "uikit-common";

const VARIANT_MAP = {
	pinned: PanelVariant.Pinned,
	inset: PanelVariant.Inset,
} as const;

const SIDE_MAP = {
	left: PanelSide.Left,
	right: PanelSide.Right,
	top: PanelSide.Top,
	bottom: PanelSide.Bottom,
} as const;

const COLLAPSIBLE_MAP = {
	offcanvas: PanelCollapsible.Offcanvas,
	icon: PanelCollapsible.Icon,
	none: PanelCollapsible.None,
} as const;

const LEVEL_MAP = {
	0: SurfaceLevel.Level0,
	1: SurfaceLevel.Level1,
	2: SurfaceLevel.Level2,
	3: SurfaceLevel.Level3,
	4: SurfaceLevel.Level4,
	5: SurfaceLevel.Level5,
} as const;

interface PanelProps {
	variant?: keyof typeof VARIANT_MAP;
	side?: keyof typeof SIDE_MAP;
	collapsible?: keyof typeof COLLAPSIBLE_MAP;
	isOpen?: boolean;
	width?: number;
	height?: number;
	collapsedWidth?: number;
	surfaceLevel?: keyof typeof LEVEL_MAP;
	elevated?: boolean;
	showBorder?: boolean;
	onToggle?: () => void;
	className?: string;
	children?: React.ReactNode;
}

export const Panel: React.FC<PanelProps> = React.memo(
	({
		variant = "pinned",
		side = "left",
		collapsible = "offcanvas",
		isOpen = true,
		width = 256,
		height = 200,
		collapsedWidth = 48,
		surfaceLevel = 1,
		elevated = false,
		showBorder = true,
		onToggle,
		className,
		children,
	}) => {
		const config = useMemo(
			() =>
				new PanelConfig(
					VARIANT_MAP[variant],
					SIDE_MAP[side],
					COLLAPSIBLE_MAP[collapsible],
					isOpen,
					width,
					height,
					collapsedWidth,
					LEVEL_MAP[surfaceLevel],
					elevated,
					showBorder,
				),
			[variant, side, collapsible, isOpen, width, height, collapsedWidth, surfaceLevel, elevated, showBorder],
		);

		return (
			<PanelView config={config} onToggle={onToggle} className={className}>
				{children}
			</PanelView>
		);
	},
);

Panel.displayName = "Panel";
