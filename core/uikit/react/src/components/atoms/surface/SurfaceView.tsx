"use client";

import React, { useMemo, useCallback } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { toRem } from "../../../utils/units";
import {
	SurfaceStyleResolver,
	Visibility,
	type SurfaceConfig,
	type DesignTokens,
} from "uikit-common";
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
		if (config.visibility === Visibility.Gone) return null;

		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const style = useMemo(
			() => SurfaceStyleResolver.getInstance().resolve(config, tokens),
			[config, tokens],
		);

		const handleClick = useCallback(() => {
			if (config.clickable) {
				onClick?.();
			}
		}, [config.clickable, onClick]);

		const handlePointerDown = useCallback(
			(e: React.PointerEvent<HTMLElement>) => {
				if (!config.clickable) return;
				const rect = e.currentTarget.getBoundingClientRect();
				const x = ((e.clientX - rect.left) / rect.width) * 100;
				const y = ((e.clientY - rect.top) / rect.height) * 100;
				e.currentTarget.style.setProperty("--ripple-x", `${x}%`);
				e.currentTarget.style.setProperty("--ripple-y", `${y}%`);
			},
			[config.clickable],
		);

		const Tag = config.clickable ? "button" : "div";

		return (
			<Tag
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
						"--surface-duration": `${tokens.motion.durationFast}ms`,
						"--surface-easing": tokens.motion.easingStandard,
						"--surface-focus-ring": tokens.color.focusRing,
						"--surface-border-width": `${tokens.borderWidth}px`,
						"--surface-focus-ring-width": `${tokens.focusRingWidth}px`,
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				{children}
			</Tag>
		);
	},
);

SurfaceView.displayName = "SurfaceView";
