"use client";

import React, { useMemo } from "react";
import { SpacerView } from "./SpacerView";
import { SpacerConfig, SpacerAxis } from "uikit-common";

const AXIS_MAP = {
	horizontal: SpacerAxis.Horizontal,
	vertical: SpacerAxis.Vertical,
} as const;

interface SpacerProps {
	size?: number;
	axis?: keyof typeof AXIS_MAP;
	className?: string;
}

export const Spacer: React.FC<SpacerProps> = React.memo(
	({ size, axis = "vertical", className }) => {
		const config = useMemo(
			() => new SpacerConfig(size ?? 0, AXIS_MAP[axis]),
			[size, axis],
		);

		return <SpacerView config={config} className={className} />;
	},
);

Spacer.displayName = "Spacer";
