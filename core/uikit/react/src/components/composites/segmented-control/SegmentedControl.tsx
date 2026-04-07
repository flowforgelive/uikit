"use client";

import React, { useMemo } from "react";
import { SegmentedControlView } from "./SegmentedControlView";
import {
	SegmentedControlConfig,
	SegmentedControlOption,
	IconPosition,
} from "uikit-common";
import { VARIANT_MAP, SIZE_MAP, ICON_POSITION_MAP } from "../../../utils/enumMaps";

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
	size?: keyof typeof SIZE_MAP;
	iconPosition?: keyof typeof ICON_POSITION_MAP;
	className?: string;
}

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
