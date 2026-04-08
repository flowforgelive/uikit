"use client";

import React from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { toRem } from "../../../utils/units";
import {
	SpacerStyleResolver,
	SpacerAxis,
	Visibility,
	type SpacerConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./SpacerView.module.css";

interface SpacerViewProps {
	config: SpacerConfig;
	tokens?: DesignTokens;
	className?: string;
}

export const SpacerView: React.FC<SpacerViewProps> = React.memo(
	({ config, tokens: tokensProp, className }) => {
		const { style } = useResolvedStyle(
			config,
			(c, t) => SpacerStyleResolver.getInstance().resolve(c, t),
			tokensProp,
		);

		if (config.visibility === Visibility.Gone) return null;

		const isVertical = config.axis === SpacerAxis.Vertical;
		const modeClass = style.isFlexible
			? css.flexible
			: isVertical
				? css.vertical
				: css.horizontal;

		return (
			<div
				aria-hidden="true"
				data-testid={config.testTag ?? (config.id || undefined)}
				className={`${css.spacer} ${modeClass} ${className ?? ""}`}
				style={
					{
						"--spacer-size": style.isFlexible
							? undefined
							: toRem(style.size),
						visibility:
							config.visibility === Visibility.Invisible
								? "hidden"
								: undefined,
					} as React.CSSProperties
				}
			/>
		);
	},
);

SpacerView.displayName = "SpacerView";
