"use client";

import React, { useMemo } from "react";
import { SegmentedControlView } from "./SegmentedControlView";
import {
	SegmentedControlConfig,
	SegmentedControlOption,
	ComponentSize,
} from "uikit-common";

interface SegmentedControlProps {
	options: { id: string; label: string }[];
	selectedId: string;
	onSelectionChange?: (id: string) => void;
	size?: "xs" | "sm" | "md" | "lg" | "xl";
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
	({ options, selectedId, onSelectionChange, size = "sm", className }) => {
		const config = useMemo(
			() =>
				new SegmentedControlConfig(
					options.map((o) => new SegmentedControlOption(o.id, o.label)),
					selectedId,
					SIZE_MAP[size],
				),
			[options, selectedId, size],
		);

		return (
			<SegmentedControlView
				config={config}
				onSelectionChange={onSelectionChange}
				className={className}
			/>
		);
	},
);

SegmentedControl.displayName = "SegmentedControl";
