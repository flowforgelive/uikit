"use client";

import React, { useMemo, useCallback } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { useSurfaceContext } from "../../../theme/SurfaceContext";
import { toRem } from "../../../utils/units";
import {
	IconButtonStyleResolver,
	Visibility,
	type IconButtonConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./IconButtonView.module.css";

interface IconButtonViewProps {
	config: IconButtonConfig;
	icon?: React.ReactNode;
	onAction?: (route: string) => void;
	onClick?: () => void;
	tokens?: DesignTokens;
	className?: string;
}

export const IconButtonView: React.FC<IconButtonViewProps> = React.memo(
	({ config, icon, onAction, onClick, tokens: tokensProp, className }) => {
		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const surface = useSurfaceContext();
		const style = useMemo(
			() =>
				IconButtonStyleResolver.getInstance().resolve(config, tokens, surface),
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
				aria-label={config.ariaLabel ?? undefined}
				data-testid={config.testTag ?? config.id}
				className={`${css.iconButton} ${className ?? ""}`}
				style={
					{
						"--ib-bg": style.colors.bg,
						"--ib-bg-hover": style.colors.bgHover,
						"--ib-text": style.colors.text,
						"--ib-text-hover": style.colors.textHover,
						"--ib-border": style.colors.border,
						"--ib-border-hover": style.colors.borderHover,
						"--ib-size": toRem(style.sizes.size),
						"--ib-icon-size": toRem(style.sizes.iconSize),
						"--ib-radius": toRem(style.sizes.radius),
						"--ib-duration": `${tokens.motion.durationFast}ms`,
						"--ib-easing": tokens.motion.easingStandard,
						"--ib-focus-ring": tokens.color.focusRing,
						"--ib-border-width": `${tokens.borderWidth}px`,
						"--ib-focus-ring-width": `${tokens.focusRingWidth}px`,
						"--ib-spinner-duration": `${tokens.motion.durationSlower * 1.5}ms`,
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				{config.loading ? (
					<span className={css.spinner} />
				) : icon ? (
					<span className={css.icon}>{icon}</span>
				) : null}
			</button>
		);
	},
);

IconButtonView.displayName = "IconButtonView";
