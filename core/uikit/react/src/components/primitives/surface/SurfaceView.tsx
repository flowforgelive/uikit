"use client";

import React, { useMemo, useCallback, useRef } from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { buildInteractiveStyleVars } from "../../../utils/interactiveStyleVars";
import { SurfaceContextProvider } from "../../../theme/SurfaceContext";
import { toRem } from "../../../utils/units";
import {
	SurfaceContext,
	SurfaceStyleResolver,
	Visibility,
	type SurfaceConfig,
	type DesignTokens,
} from "uikit-common";
import { useSurfaceContext } from "../../../theme/SurfaceContext";
import css from "./SurfaceView.module.css";

interface SurfaceViewProps {
	config: SurfaceConfig;
	onClick?: () => void;
	tokens?: DesignTokens;
	className?: string;
	children?: React.ReactNode;
}

export const SurfaceView: React.FC<SurfaceViewProps> = React.memo(
	({ config, onClick, tokens: tokensProp, className, children }) => {
		const { style, tokens } = useResolvedStyle(
			config,
			(c, t) => SurfaceStyleResolver.getInstance().resolve(c, t),
			tokensProp,
		);
		const parentSurface = useSurfaceContext();

		const elementRef = useRef<HTMLElement>(null);

		const handleClick = useCallback(() => {
			if (config.clickable) {
				onClick?.();
			}
		}, [config.clickable, onClick]);

		const handlePointerDown = useCallback(
			(e: React.PointerEvent<HTMLElement>) => {
				if (!config.clickable || !elementRef.current) return;
				const rect = elementRef.current.getBoundingClientRect();
				const x = ((e.clientX - rect.left) / rect.width) * 100;
				const y = ((e.clientY - rect.top) / rect.height) * 100;
				elementRef.current.style.setProperty("--ripple-x", `${x}%`);
				elementRef.current.style.setProperty("--ripple-y", `${y}%`);
			},
			[config.clickable],
		);

		const surfaceContext = useMemo(
			() => new SurfaceContext(config.level.ordinal, style.bg, parentSurface.nestingDepth),
			[config.level, style.bg, parentSurface.nestingDepth],
		);

		if (config.visibility === Visibility.Gone) return null;

		const Tag = config.clickable ? "button" : "div";

		return (
			<SurfaceContextProvider value={surfaceContext}>
				<Tag
					ref={elementRef as React.RefObject<HTMLButtonElement & HTMLDivElement>}
					onClick={config.clickable ? handleClick : undefined}
					onPointerDown={config.clickable ? handlePointerDown : undefined}
					data-testid={config.testTag ?? config.id}
					className={`${css.surface} ${config.clickable ? css.clickable : ""} ${config.hoverable ? css.hoverable : ""} ${className ?? ""}`}
					style={
						{
							"--surface-bg": style.bg,
							"--surface-bg-hover": style.bgHover,
							"--surface-border": style.border,
							"--surface-radius": toRem(style.radius),
							"--surface-shadow": style.shadow,
							...buildInteractiveStyleVars(tokens, "surface"),
							"--surface-press-opacity": String(tokens.state.hoverOpacity),
							"--surface-press-brightness": String(tokens.state.pressBrightnessSurface),
							visibility:
								config.visibility === Visibility.Invisible ? "hidden" : undefined,
						} as React.CSSProperties
					}
				>
					{children}
				</Tag>
			</SurfaceContextProvider>
		);
	},
);

SurfaceView.displayName = "SurfaceView";
