"use client";

import React from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { toRem } from "../../../utils/units";
import {
	BadgeStyleResolver,
	BadgeVariant,
	Visibility,
	type BadgeConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./BadgeView.module.css";

interface BadgeViewProps {
	config: BadgeConfig;
	tokens?: DesignTokens;
	className?: string;
}

export const BadgeView: React.FC<BadgeViewProps> = React.memo(
	({ config, tokens: tokensProp, className }) => {
		const { style, tokens } = useResolvedStyle(
			config,
			(c, t) => BadgeStyleResolver.getInstance().resolve(c, t),
			tokensProp,
		);

		if (config.visibility === Visibility.Gone) return null;

		const isDot = config.variant === BadgeVariant.Dot;

		const cssVars = {
			"--badge-dot-size": toRem(style.dotSize),
			"--badge-min-width": toRem(style.minWidth),
			"--badge-height": toRem(style.height),
			"--badge-padding-h": toRem(style.paddingH),
			"--badge-font-size": toRem(style.fontSize),
			"--badge-font-weight": `${style.fontWeight}`,
			"--badge-bg": style.bgColor,
			"--badge-text": style.textColor,
			visibility:
				config.visibility === Visibility.Invisible
					? ("hidden" as const)
					: undefined,
		} as React.CSSProperties;

		return (
			<span
				role="status"
				data-testid={config.testTag ?? (config.id || undefined)}
				className={`${isDot ? css.dot : css.numeric} ${className ?? ""}`}
				style={cssVars}
			>
				{!isDot && config.showValue ? config.displayValue : null}
			</span>
		);
	},
);

BadgeView.displayName = "BadgeView";
