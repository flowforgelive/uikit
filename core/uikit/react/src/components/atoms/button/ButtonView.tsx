"use client";

import React, { useMemo, useCallback } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { useSurfaceContext } from "../../../theme/SurfaceContext";
import { toRem } from "../../../utils/units";
import {
	ButtonStyleResolver,
	Visibility,
	type ButtonConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./ButtonView.module.css";

interface ButtonViewProps {
	config: ButtonConfig;
	onAction?: (route: string) => void;
	onClick?: () => void;
	tokens?: DesignTokens;
	className?: string;
}

export const ButtonView: React.FC<ButtonViewProps> = React.memo(
	({ config, onAction, onClick, tokens: tokensProp, className }) => {
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
						"--btn-text": style.colors.text,					"--btn-text-hover": style.colors.textHover,						"--btn-border": style.colors.border,
						"--btn-border-hover": style.colors.borderHover,
						"--btn-height": toRem(style.sizes.height),
						"--btn-padding-h": toRem(style.sizes.paddingH),
						"--btn-font-size": toRem(style.sizes.fontSize),					"--btn-font-weight": style.sizes.fontWeight,
					"--btn-icon-size": toRem(style.sizes.iconSize),						"--btn-radius": toRem(style.radius),
						"--btn-letter-spacing": toRem(style.sizes.letterSpacing),
						"--btn-duration": `${tokens.motion.durationFast}ms`,
						"--btn-easing": tokens.motion.easingStandard,
						"--btn-focus-ring": tokens.color.focusRing,
						"--btn-border-width": `${tokens.borderWidth}px`,
						"--btn-focus-ring-width": `${tokens.focusRingWidth}px`,
						"--btn-spinner-duration": `${tokens.motion.durationSlower * 1.5}ms`,
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				{config.loading ? (
					<span className={css.spinner} />
				) : (
					config.text
				)}
			</button>
		);
	},
);

ButtonView.displayName = "ButtonView";
