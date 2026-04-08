"use client";

import React, { useMemo, useCallback } from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { useInteractiveHandler } from "../../../hooks/useInteractiveHandler";
import { buildInteractiveStyleVars, buildSpinnerStyleVars } from "../../../utils/interactiveStyleVars";
import { toRem, toEm, toLineHeightRatio } from "../../../utils/units";
import {
	ChipStyleResolver,
	Visibility,
	type ChipConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./ChipView.module.css";

interface ChipViewProps {
	config: ChipConfig;
	leadingIcon?: React.ReactNode;
	onAction?: (route: string) => void;
	onClick?: () => void;
	onDismiss?: () => void;
	tokens?: DesignTokens;
	className?: string;
}

export const ChipView: React.FC<ChipViewProps> = React.memo(
	({ config, leadingIcon, onAction, onClick, onDismiss, tokens: tokensProp, className }) => {
		const { style, tokens } = useResolvedStyle(
			config,
			(c, t, s) => ChipStyleResolver.getInstance().resolve(c, t, s),
			tokensProp,
		);

		const handleClick = useInteractiveHandler(config.isInteractive, config.actionRoute, onClick, onAction);

		const handleDismiss = useCallback(
			(e: React.MouseEvent | React.KeyboardEvent) => {
				e.stopPropagation();
				if (config.isInteractive) {
					onDismiss?.();
				}
			},
			[config.isInteractive, onDismiss],
		);

		const handleDismissKeyDown = useCallback(
			(e: React.KeyboardEvent) => {
				if (e.key === "Enter" || e.key === " ") {
					e.preventDefault();
					handleDismiss(e);
				}
			},
			[handleDismiss],
		);

		if (config.visibility === Visibility.Gone) return null;

		return (
			<button
				onClick={handleClick}
				aria-disabled={!config.isInteractive ? "true" : undefined}
				aria-pressed={config.selected || undefined}
				aria-label={config.loading ? config.text : undefined}
				aria-busy={config.loading || undefined}
				data-interactive={config.isInteractive || undefined}
				data-testid={config.testTag ?? config.id}
				className={`${css.chip} ${className ?? ""}`}
				style={
					{
						"--chip-bg": style.colors.bg,
						"--chip-bg-hover": style.colors.bgHover,
						"--chip-text": style.colors.text,
						"--chip-text-hover": style.colors.textHover,
						"--chip-border": style.colors.border,
						"--chip-border-hover": style.colors.borderHover,
						"--chip-height": toRem(style.sizes.height),
						"--chip-padding-start": toRem(style.sizes.paddingStart),
						"--chip-padding-end": toRem(style.sizes.paddingEnd),
						"--chip-font-size": toRem(style.sizes.fontSize),
						"--chip-font-weight": `${style.sizes.fontWeight}`,
						"--chip-icon-size": toRem(style.sizes.iconSize),
						"--chip-icon-gap": toRem(style.sizes.iconGap),
						"--chip-close-size": toRem(style.sizes.closeButtonSize),
					"--chip-close-icon-size": toRem(style.sizes.closeIconSize),
						"--chip-radius": toRem(style.radius),
						"--chip-letter-spacing": toEm(style.sizes.letterSpacing, style.sizes.fontSize),
						"--chip-line-height": `${toLineHeightRatio(style.sizes.lineHeight, style.sizes.fontSize)}`,
						"--chip-font-variation": tokens.fontVariationSettings,
						...buildInteractiveStyleVars(tokens, "chip"),
						...buildSpinnerStyleVars(tokens, "chip"),
						"--chip-hover-content-opacity": `${tokens.state.hoverContentOpacity}`,
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				{config.loading ? (
					<span className={css.spinner} />
				) : (
					<>
						{config.hasLeadingIcon && leadingIcon && (
							<span className={css.leadingIcon}>{leadingIcon}</span>
						)}
						<span className={css.label}>{config.text}</span>
						{config.dismissible && (
							<span
								role="button"
								className={css.closeButton}
								onClick={handleDismiss}
								onKeyDown={handleDismissKeyDown}
								data-interactive
								aria-label="Dismiss"
								tabIndex={-1}
							>
								<svg
									className={css.closeIcon}
									viewBox="0 0 16 16"
									fill="none"
									stroke="currentColor"
									strokeWidth="2.5"
									strokeLinecap="round"
								>
									<line x1="4" y1="4" x2="12" y2="12" />
									<line x1="12" y1="4" x2="4" y2="12" />
								</svg>
							</span>
						)}
					</>
				)}
			</button>
		);
	},
);

ChipView.displayName = "ChipView";
