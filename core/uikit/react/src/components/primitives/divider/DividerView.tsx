"use client";

import React, { useMemo } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { toRem } from "../../../utils/units";
import {
	DividerStyleResolver,
	DividerOrientation,
	Visibility,
	type DividerConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./DividerView.module.css";

interface DividerViewProps {
	config: DividerConfig;
	tokens?: DesignTokens;
	className?: string;
}

export const DividerView: React.FC<DividerViewProps> = React.memo(
	({ config, tokens: tokensProp, className }) => {
		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const style = useMemo(
			() => DividerStyleResolver.getInstance().resolve(config, tokens),
			[config, tokens],
		);

		if (config.visibility === Visibility.Gone) return null;

		const isVertical = config.orientation === DividerOrientation.Vertical;

		return (
			<hr
				role="separator"
				aria-orientation={isVertical ? "vertical" : "horizontal"}
				data-testid={config.testTag ?? (config.id || undefined)}
				className={`${isVertical ? css.vertical : css.horizontal} ${className ?? ""}`}
				style={
					{
						"--div-color": style.color,
						"--div-thickness": toRem(style.thickness),
						"--div-inset-start": toRem(style.insetStart),
						"--div-inset-end": toRem(style.insetEnd),
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			/>
		);
	},
);

DividerView.displayName = "DividerView";
