"use client";

import React, { useMemo } from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { buildInteractiveStyleVars } from "../../../utils/interactiveStyleVars";
import { SurfaceContextProvider, useSurfaceContext } from "../../../theme/SurfaceContext";
import { toRem } from "../../../utils/units";
import {
	SurfaceContext,
	GlassSurfaceStyleResolver,
	Visibility,
	type GlassSurfaceConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./GlassSurfaceView.module.css";

interface GlassSurfaceViewProps {
	config: GlassSurfaceConfig;
	tokens?: DesignTokens;
	className?: string;
	children?: React.ReactNode;
}

export const GlassSurfaceView: React.FC<GlassSurfaceViewProps> = React.memo(
	({ config, tokens: tokensProp, className, children }) => {
		const { style, tokens } = useResolvedStyle(
			config,
			(c, t) => GlassSurfaceStyleResolver.getInstance().resolve(c, t),
			tokensProp,
		);
		const parentSurface = useSurfaceContext();

		const surfaceContext = useMemo(
			() =>
				new SurfaceContext(
					parentSurface.level,
					style.bg,
					parentSurface.nestingDepth,
					style.foregroundColor,
					style.foregroundSecondary,
					style.foregroundMuted,
				),
			[
				parentSurface.level,
				style.bg,
				parentSurface.nestingDepth,
				style.foregroundColor,
				style.foregroundSecondary,
				style.foregroundMuted,
			],
		);

		if (config.visibility === Visibility.Gone) return null;

		return (
			<SurfaceContextProvider value={surfaceContext}>
				<div
					data-testid={config.testTag ?? config.id}
					className={`${css.glass} ${className ?? ""}`}
					style={
						{
							"--glass-bg": `color-mix(in oklch, ${style.bg} ${Math.round(style.bgOpacity * 100)}%, transparent)`,
							"--glass-bg-fallback": `color-mix(in oklch, ${style.bg} ${Math.min(Math.round(style.bgOpacity * 100) + 20, 95)}%, transparent)`,
							"--glass-border": `color-mix(in oklch, ${style.border} ${Math.round(style.borderOpacity * 100)}%, transparent)`,
							"--glass-radius": toRem(style.radius),
							"--glass-blur": `${style.blur}px`,
							"--glass-saturate": String(style.saturate),
							"--glass-shadow": style.shadow,
							...buildInteractiveStyleVars(tokens, "glass"),
							visibility:
								config.visibility === Visibility.Invisible ? "hidden" : undefined,
						} as React.CSSProperties
					}
				>
					{children}
				</div>
			</SurfaceContextProvider>
		);
	},
);

GlassSurfaceView.displayName = "GlassSurfaceView";
