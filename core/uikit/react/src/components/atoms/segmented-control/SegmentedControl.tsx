"use client";

import React, { useMemo } from "react";
import { SegmentedControlView } from "./SegmentedControlView";
import {
	SegmentedControlConfig,
	SegmentedControlOption,
	ComponentSize,
	VisualVariant,
	IconPosition,
} from "uikit-common";

const VARIANT_MAP = {
	surface: VisualVariant.Surface,
	soft: VisualVariant.Soft,
	outline: VisualVariant.Outline,
	solid: VisualVariant.Solid,
	ghost: VisualVariant.Ghost,
} as const;

const ICON_POSITION_MAP = {
	none: IconPosition.None,
	start: IconPosition.Start,
	end: IconPosition.End,
	top: IconPosition.Top,
	bottom: IconPosition.Bottom,
} as const;

interface SegmentedControlOptionProp {
	id: string;
	label: string;
	icon?: React.ReactNode;
}

interface SegmentedControlProps {
	options: SegmentedControlOptionProp[];
	selectedId: string;
	onSelectionChange?: (id: string) => void;
	variant?: keyof typeof VARIANT_MAP;
	size?: "xs" | "sm" | "md" | "lg" | "xl";
	iconPosition?: keyof typeof ICON_POSITION_MAP;
	className?: string;
}

const SIZE_MAP: Record<string, any> = {
	xs: ComponentSize.Xs,
	sm: ComponentSize.Sm,
	md: ComponentSize.Md,
	lg: ComponentSize.Lg,
	xl: ComponentSize.Xl,
};

export const SegmentedControl: React.FC<SegmentedControlProps> = React.memo(
	({ options, selectedId, onSelectionChange, variant = "surface", size = "sm", iconPosition = "none", className }) => {
		const hasIcons = iconPosition !== "none" && options.some((o) => o.icon);

		const config = useMemo(
			() =>
				new SegmentedControlConfig(
					options.map((o) => new SegmentedControlOption(o.id, o.label, hasIcons ? o.id : undefined)),
					selectedId,
					SIZE_MAP[size],
					VARIANT_MAP[variant],
					hasIcons ? ICON_POSITION_MAP[iconPosition] : IconPosition.None,
				),
			[options, selectedId, size, variant, iconPosition, hasIcons],
		);

		const iconMap = useMemo(() => {
			if (!hasIcons) return undefined;
			const map: Record<string, React.ReactNode> = {};
			for (const o of options) {
				if (o.icon) map[o.id] = o.icon;
			}
			return map;
		}, [options, hasIcons]);

		const renderIcon = useMemo(() => {
			if (!iconMap) return undefined;
			return (iconId: string) => iconMap[iconId] ?? null;
		}, [iconMap]);

		return (
			<SegmentedControlView
				config={config}
				onSelectionChange={onSelectionChange}
				renderIcon={renderIcon}
				className={className}
			/>
		);
	},
);

SegmentedControl.displayName = "SegmentedControl";
