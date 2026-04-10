"use client";

import React, { useMemo } from "react";
import { GlassSurfaceView } from "./GlassSurfaceView";
import { GlassSurfaceConfig, GlassVariant, SurfaceShape } from "uikit-common";

const VARIANT_MAP = {
	regular: GlassVariant.Regular,
	clear: GlassVariant.Clear,
} as const;

const SHAPE_MAP = {
	none: SurfaceShape.None,
	sm: SurfaceShape.Sm,
	md: SurfaceShape.Md,
	lg: SurfaceShape.Lg,
	xl: SurfaceShape.Xl,
	full: SurfaceShape.Full,
} as const;

interface GlassSurfaceProps {
	variant?: keyof typeof VARIANT_MAP;
	tintColor?: string;
	shape?: keyof typeof SHAPE_MAP;
	elevated?: boolean;
	className?: string;
	children?: React.ReactNode;
}

export const GlassSurface: React.FC<GlassSurfaceProps> = React.memo(
	({
		variant = "regular",
		tintColor = "",
		shape = "md",
		elevated = false,
		className,
		children,
	}) => {
		const config = useMemo(
			() =>
				new GlassSurfaceConfig(
					VARIANT_MAP[variant],
					tintColor,
					SHAPE_MAP[shape],
					elevated,
				),
			[variant, tintColor, shape, elevated],
		);

		return (
			<GlassSurfaceView config={config} className={className}>
				{children}
			</GlassSurfaceView>
		);
	},
);

GlassSurface.displayName = "GlassSurface";
