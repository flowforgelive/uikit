"use client";

import React, { useMemo } from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { toRem } from "../../../utils/units";
import {
	IconStyleResolver,
	Visibility,
	type IconConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./IconView.module.css";

interface IconViewProps {
	config: IconConfig;
	tokens?: DesignTokens;
	className?: string;
	children?: React.ReactNode;
}

export const IconView: React.FC<IconViewProps> = React.memo(
	({ config, tokens: tokensProp, className, children }) => {
		const { style } = useResolvedStyle(
			config,
			(c, t) => IconStyleResolver.getInstance().resolve(c, t),
			tokensProp,
		);

		if (config.visibility === Visibility.Gone) return null;

		return (
			<span
				role={config.ariaHidden ? "presentation" : "img"}
				aria-hidden={config.ariaHidden || undefined}
				aria-label={!config.ariaHidden ? (config.name || undefined) : undefined}
				data-testid={config.testTag ?? (config.id || undefined)}
				className={`${css.icon} ${className ?? ""}`}
				style={
					{
						"--icon-size": toRem(style.size),
						"--icon-color": style.color ?? "currentColor",
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				{children}
			</span>
		);
	},
);

IconView.displayName = "IconView";
