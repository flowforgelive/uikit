"use client";

import React, { useMemo } from "react";
import { useResolvedStyle } from "../../../hooks/useResolvedStyle";
import { toRem } from "../../../utils/units";
import {
	SkeletonStyleResolver,
	Visibility,
	type SkeletonConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./SkeletonView.module.css";

interface SkeletonViewProps {
	config: SkeletonConfig;
	tokens?: DesignTokens;
	className?: string;
}

export const SkeletonView: React.FC<SkeletonViewProps> = React.memo(
	({ config, tokens: tokensProp, className }) => {
		const { style } = useResolvedStyle(
			config,
			(c, t) => SkeletonStyleResolver.getInstance().resolve(c, t),
			tokensProp,
		);

		if (config.visibility === Visibility.Gone) return null;

		const widthClass = style.width > 0 ? css.fixed : css.fill;
		const animClass = style.animate ? css.animated : "";

		return (
			<div
				role="status"
				aria-label="Loading"
				data-testid={config.testTag ?? (config.id || undefined)}
				className={`${css.skeleton} ${widthClass} ${animClass} ${className ?? ""}`}
				style={
					{
						"--sk-base": style.baseColor,
						"--sk-highlight": style.highlightColor,
						"--sk-radius": toRem(style.cornerRadius),
						"--sk-width": style.width > 0 ? toRem(style.width) : undefined,
						"--sk-duration": `${style.durationMs}ms`,
						"--sk-easing": style.easing,
						height: toRem(style.height),
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			/>
		);
	},
);

SkeletonView.displayName = "SkeletonView";
