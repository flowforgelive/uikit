"use client";

import React, { useMemo } from "react";
import { DividerView } from "./DividerView";
import { DividerConfig, DividerOrientation } from "uikit-common";

const ORIENTATION_MAP = {
	horizontal: DividerOrientation.Horizontal,
	vertical: DividerOrientation.Vertical,
} as const;

interface DividerProps {
	orientation?: keyof typeof ORIENTATION_MAP;
	thickness?: number;
	color?: string;
	insetStart?: number;
	insetEnd?: number;
	className?: string;
}

export const Divider: React.FC<DividerProps> = React.memo(
	({
		orientation = "horizontal",
		thickness,
		color,
		insetStart,
		insetEnd,
		className,
	}) => {
		const config = useMemo(
			() =>
				new DividerConfig(
					ORIENTATION_MAP[orientation],
					thickness ?? 0,
					color ?? null,
					insetStart ?? 0,
					insetEnd ?? 0,
				),
			[orientation, thickness, color, insetStart, insetEnd],
		);

		return <DividerView config={config} className={className} />;
	},
);

Divider.displayName = "Divider";
