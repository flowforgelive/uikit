"use client";

import React, { useMemo } from "react";
import { SkeletonView } from "./SkeletonView";
import { SkeletonConfig, SkeletonShape } from "uikit-common";

const SHAPE_MAP = {
	rectangle: SkeletonShape.Rectangle,
	circle: SkeletonShape.Circle,
	"text-line": SkeletonShape.TextLine,
} as const;

interface SkeletonProps {
	shape?: keyof typeof SHAPE_MAP;
	width?: number;
	height?: number;
	cornerRadius?: number;
	animate?: boolean;
	className?: string;
}

export const Skeleton: React.FC<SkeletonProps> = React.memo(
	({
		shape = "rectangle",
		width,
		height,
		cornerRadius,
		animate,
		className,
	}) => {
		const config = useMemo(
			() =>
				new SkeletonConfig(
					SHAPE_MAP[shape],
					width ?? 0,
					height ?? 0,
					cornerRadius ?? null,
					animate ?? true,
				),
			[shape, width, height, cornerRadius, animate],
		);

		return <SkeletonView config={config} className={className} />;
	},
);

Skeleton.displayName = "Skeleton";
