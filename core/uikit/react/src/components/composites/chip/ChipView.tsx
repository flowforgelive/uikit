"use client";

import React, { useMemo, useCallback } from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { useInteractiveHandler } from "../../../hooks/useInteractiveHandler";
import { buildInteractiveStyleVars, buildSpinnerStyleVars } from "../../../utils/interactiveStyleVars";
import { toRem, toEm, toLineHeightRatio } from "../../../utils/units";
import {
	ChipStyleResolver,
	Visibility,
	VisualVariant,
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

		const isClickable = config.isInteractive && !!(onClick || onAction || config.actionRoute);
		const handleClick = useInteractiveHandler(isClickable, config.actionRoute, onClick, onAction);

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

		const isGlass = config.variant === VisualVariant.Glass;
		const isStatic = !isClickable && !config.disabled && !config.loading;
		const Tag = isStatic ? "span" : "button";

		return (
			<Tag
				onClick={isClickable ? handleClick : undefined}
				aria-disabled={!config.isInteractive && !isStatic ? "true" : undefined}
				aria-pressed={config.selected || undefined}
				aria-label={config.loading ? config.text : undefined}
				aria-busy={config.loading || undefined}
				data-interactive={isClickable || undefined}
				data-static={isStatic || undefined}
				data-glass={isGlass || undefined}
				data-testid={config.testTag ?? config.id}
				className={`${css.chip} ${className ?? ""}`}
				style={
					{
						"--chip-bg": style.colors.bg,
						"--chip-bg-hover": style.colors.bgHover,
						"--chip-bg-active": style.colors.bgActive,
						"--chip-text": style.colors.text,
						"--chip-text-hover": style.colors.textHover,
						"--chip-text-active": style.colors.textActive,
						"--chip-border": style.colors.border,
						"--chip-border-hover": style.colors.borderHover,
						"--chip-border-active": style.colors.borderActive,
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
						...(isGlass ? {
							"--chip-glass-blur": toRem(tokens.glass.blurRadius),
							"--chip-glass-saturate": `${tokens.glass.saturate}`,
							"--chip-glass-opacity": `${tokens.glass.bgOpacity * 100}%`,
							"--chip-glass-opacity-hover": `${tokens.glass.bgOpacity * tokens.glass.hoverOpacityMultiplier * 100}%`,
							"--chip-glass-opacity-active": `${tokens.glass.bgOpacity * tokens.glass.activeOpacityMultiplier * 100}%`,
							"--chip-glass-border-opacity": `${tokens.glass.borderOpacity * 100}%`,
							"--chip-glass-shadow": tokens.glass.glassShadow,
						} : {}),
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
			</Tag>
		);
	},
);

ChipView.displayName = "ChipView";
