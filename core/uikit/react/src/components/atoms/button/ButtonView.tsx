"use client";

import React, { useMemo, useCallback } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { useSurfaceContext } from "../../../theme/SurfaceContext";
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
		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const surface = useSurfaceContext();
		const style = useMemo(
			() => ButtonStyleResolver.getInstance().resolve(config, tokens, surface),
			[config, tokens, surface],
		);

		const handleClick = useCallback(() => {
			if (config.isInteractive) {
				onClick?.();
				if (config.actionRoute) {
					onAction?.(config.actionRoute!);
				}
			}
		}, [config.isInteractive, config.actionRoute, onAction, onClick]);

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
				aria-disabled={!config.isInteractive || undefined}
				data-testid={config.testTag ?? config.id}
				className={`${css.button} ${className ?? ""}`}
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
						"--btn-font-weight": style.sizes.fontWeight,
						"--btn-icon-size": toRem(style.sizes.iconSize),
						"--btn-icon-gap": toRem(style.sizes.iconGap),
						"--btn-radius": toRem(style.radius),
						"--btn-letter-spacing": toEm(style.sizes.letterSpacing, style.sizes.fontSize),
						"--btn-line-height": toLineHeightRatio(style.sizes.lineHeight, style.sizes.fontSize),
						"--btn-font-variation": tokens.fontVariationSettings,
						"--btn-duration": `${tokens.motion.durationFast}ms`,
						"--btn-easing": tokens.motion.easingStandard,
						"--btn-focus-ring": tokens.color.focusRing,
						"--btn-border-width": `${tokens.borderWidth}px`,
						"--btn-focus-ring-width": `${tokens.focusRingWidth}px`,
						"--btn-spinner-duration": `${tokens.motion.durationSlower * 1.5}ms`,
						"--btn-spinner-stroke": `${tokens.spinnerStrokeWidth}px`,
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
