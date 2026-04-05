"use client";

import React, { useMemo, useCallback } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { SurfaceContextProvider } from "../../../theme/SurfaceContext";
import { toRem } from "../../../utils/units";
import {
	SurfaceContext,
	PanelStyleResolver,
	Visibility,
	type PanelConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./PanelView.module.css";

interface PanelViewProps {
	config: PanelConfig;
	onToggle?: () => void;
	tokens?: DesignTokens;
	className?: string;
	children?: React.ReactNode;
}

export const PanelView: React.FC<PanelViewProps> = React.memo(
	({ config, onToggle, tokens: tokensProp, className, children }) => {
		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const style = useMemo(
			() => PanelStyleResolver.getInstance().resolve(config, tokens),
			[config, tokens],
		);

		const surfaceContext = useMemo(
			() => new SurfaceContext(config.surfaceLevel.ordinal, style.bg),
			[config.surfaceLevel, style.bg],
		);

		const handleToggle = useCallback(() => {
			onToggle?.();
		}, [onToggle]);

		if (config.visibility === Visibility.Gone) return null;

		const isInset = config.variant.name === "Inset";
		const isOpen = config.isOpen;
		const sideName = config.side.name.toLowerCase();
		const collapsibleName = config.collapsible.name.toLowerCase();

		return (
			<SurfaceContextProvider value={surfaceContext}>
				<aside
					role="complementary"
					aria-label="Panel"
					data-state={isOpen ? "open" : "closed"}
					data-side={sideName}
					data-collapsible={collapsibleName}
					data-testid={config.testTag ?? config.id}
					className={`${css.panel} ${isInset ? css.inset : ""} ${className ?? ""}`}
					style={
						{
							"--panel-bg": style.bg,
							"--panel-border": style.border,
							"--panel-radius": toRem(style.radius),
							"--panel-width": toRem(style.width),						"--panel-height": toRem(style.height),							"--panel-shadow": style.shadow,
							"--panel-inset": toRem(style.insetPadding),
							"--panel-border-width": `${style.borderWidth}px`,
							"--panel-duration": `${style.durationMs}ms`,
							"--panel-easing": style.easing,						"--panel-toggle-duration": `${tokens.motion.durationFast}ms`,							visibility:
								config.visibility === Visibility.Invisible ? "hidden" : undefined,
						} as React.CSSProperties
					}
				>
					<div className={css.content}>
						{children}
					</div>
					{config.collapsible.name !== "None" && (
						<button
							type="button"
							className={css.toggle}
							onClick={handleToggle}
							aria-label={isOpen ? "Collapse panel" : "Expand panel"}
							aria-expanded={isOpen}
						>
							<svg
								width="16"
								height="16"
								viewBox="0 0 16 16"
								fill="none"
								className={`${css.toggleIcon} ${!isOpen ? css.toggleIconCollapsed : ""}`}
								style={{ transform: sideName === "right" ? "scaleX(-1)" : sideName === "bottom" ? "rotate(90deg)" : sideName === "top" ? "rotate(-90deg)" : undefined }}
							>
								<path
									d="M10.5 3L5.5 8L10.5 13"
									stroke="currentColor"
									strokeWidth="1.5"
									strokeLinecap="round"
									strokeLinejoin="round"
								/>
							</svg>
						</button>
					)}
				</aside>
			</SurfaceContextProvider>
		);
	},
);

PanelView.displayName = "PanelView";
