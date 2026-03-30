"use client";

import React, { useMemo } from "react";
import { SegmentedControlView } from "./SegmentedControlView";
import {
	SegmentedControlConfig,
	SegmentedControlOption,
} from "uikit-common";

interface SegmentedControlProps {
	options: { id: string; label: string }[];
	selectedId: string;
	onSelectionChange?: (id: string) => void;
	className?: string;
}

export const SegmentedControl: React.FC<SegmentedControlProps> = React.memo(
	({ options, selectedId, onSelectionChange, className }) => {
		const config = useMemo(
			() =>
				new SegmentedControlConfig(
					options.map((o) => new SegmentedControlOption(o.id, o.label)),
					selectedId,
				),
			[options, selectedId],
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
