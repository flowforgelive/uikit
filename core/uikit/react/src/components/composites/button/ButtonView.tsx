"use client";

import React, { useMemo, useCallback } from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { useInteractiveHandler } from "../../../hooks/useInteractiveHandler";
import { buildInteractiveStyleVars, buildSpinnerStyleVars } from "../../../utils/interactiveStyleVars";
import { toRem, toEm, toLineHeightRatio } from "../../../utils/units";
import {
	ButtonStyleResolver,
	Visibility,
	IconPosition,
	type ButtonConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./ButtonView.module.css";

interface ButtonViewProps {
	config: ButtonConfig;
	iconStart?: React.ReactNode;
	iconEnd?: React.ReactNode;
	onAction?: (route: string) => void;
	onClick?: () => void;
	tokens?: DesignTokens;
	className?: string;
}

export const ButtonView: React.FC<ButtonViewProps> = React.memo(
	({ config, iconStart, iconEnd, onAction, onClick, tokens: tokensProp, className }) => {
		const { style, tokens } = useResolvedStyle(
			config,
			(c, t, s) => ButtonStyleResolver.getInstance().resolve(c, t, s),
			tokensProp,
		);

		const handleClick = useInteractiveHandler(config.isInteractive, config.actionRoute, onClick, onAction);

		if (config.visibility === Visibility.Gone) return null;

		const isVertical =
			config.iconPosition === IconPosition.Top ||
			config.iconPosition === IconPosition.Bottom;

		const hasIcon = config.hasIcon;

		const renderIcon = (node: React.ReactNode) => (
			<span className={css.icon}>{node}</span>
		);

		const renderContent = () => {
			if (config.loading) {
				return <span className={css.spinner} />;
			}

			if (config.isIconOnly) {
				const icon = iconStart ?? iconEnd;
				return icon ? renderIcon(icon) : null;
			}

			if (!hasIcon) {
				return config.text;
			}

			const textEl = <span className={css.label}>{config.text}</span>;

			if (isVertical) {
				const icon = iconStart ?? iconEnd;
				if (!icon) return textEl;
				return (
					<span className={css.contentVertical}>
						{config.iconPosition === IconPosition.Top && renderIcon(icon)}
						{textEl}
						{config.iconPosition === IconPosition.Bottom && renderIcon(icon)}
					</span>
				);
			}

			return (
				<span className={css.content}>
					{iconStart && renderIcon(iconStart)}
					{textEl}
					{iconEnd && renderIcon(iconEnd)}
				</span>
			);
		};

		return (
			<button
				onClick={handleClick}
				aria-disabled={!config.isInteractive ? "true" : undefined}
				aria-label={config.ariaLabel ?? (config.loading ? (config.text || undefined) : undefined)}
				aria-busy={config.loading || undefined}
				data-interactive={config.isInteractive || undefined}
				data-testid={config.testTag ?? config.id}
				className={`${css.button} ${config.isIconOnly ? css.iconOnly : ""} ${className ?? ""}`}
				style={
					{
						"--btn-bg": style.colors.bg,
						"--btn-bg-hover": style.colors.bgHover,
						"--btn-text": style.colors.text,
						"--btn-text-hover": style.colors.textHover,
						"--btn-border": style.colors.border,
						"--btn-border-hover": style.colors.borderHover,
						"--btn-height": toRem(style.sizes.height),
						"--btn-padding-h": toRem(style.sizes.paddingH),
						"--btn-padding-v": toRem(style.sizes.paddingV),
						"--btn-font-size": toRem(style.sizes.fontSize),
						"--btn-font-weight": `${style.sizes.fontWeight}`,
						"--btn-icon-size": toRem(style.sizes.iconSize),
						"--btn-icon-gap": toRem(style.sizes.iconGap),
						"--btn-radius": toRem(style.radius),
						"--btn-letter-spacing": toEm(style.sizes.letterSpacing, style.sizes.fontSize),
						"--btn-line-height": `${toLineHeightRatio(style.sizes.lineHeight, style.sizes.fontSize)}`,
						"--btn-font-variation": tokens.fontVariationSettings,
						...buildInteractiveStyleVars(tokens, "btn"),
						...buildSpinnerStyleVars(tokens, "btn"),
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				{renderContent()}
			</button>
		);
	},
);

ButtonView.displayName = "ButtonView";
