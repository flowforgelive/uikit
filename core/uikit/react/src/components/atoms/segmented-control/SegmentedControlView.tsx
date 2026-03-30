"use client";

import React, { useMemo, useCallback } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { toRem } from "../../../utils/units";
import {
	SegmentedControlStyleResolver,
	Visibility,
	type SegmentedControlConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./SegmentedControlView.module.css";

interface SegmentedControlViewProps {
	config: SegmentedControlConfig;
	onSelectionChange?: (id: string) => void;
	tokens?: DesignTokens;
	className?: string;
}

export const SegmentedControlView: React.FC<SegmentedControlViewProps> =
	React.memo(({ config, onSelectionChange, tokens: tokensProp, className }) => {
		if (config.visibility === Visibility.Gone) return null;

		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const style = useMemo(
			() => SegmentedControlStyleResolver.getInstance().resolve(tokens),
			[tokens],
		);

		const options = config.options;
		const selectedIndex = Math.max(
			0,
			options.findIndex((o: any) => o.id === config.selectedId),
		);
		const segmentPercent = options.length > 0 ? 100 / options.length : 0;

		return (
			<div
				data-testid={config.testTag ?? config.id}
				className={`${css.track} ${className ?? ""}`}
				style={
					{
						"--sc-track-bg": style.colors.trackBg,
						"--sc-thumb-bg": style.colors.thumbBg,
						"--sc-text-active": style.colors.textActive,
						"--sc-text-inactive": style.colors.textInactive,
						"--sc-height": toRem(style.sizes.height),
						"--sc-font-size": toRem(style.sizes.fontSize),
						"--sc-radius": toRem(style.sizes.radius),
						"--sc-thumb-radius": toRem(style.sizes.thumbRadius),
						"--sc-track-padding": toRem(style.sizes.trackPadding),
						"--sc-thumb-offset": `${selectedIndex * 100}%`,
						"--sc-thumb-width": `${segmentPercent}%`,
						"--sc-duration": `${tokens.motion.durationNormal}ms`,
						"--sc-easing": tokens.motion.easingStandard,
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				<div className={css.thumb} />
				{Array.from(options).map((option: any) => (
					<button
						key={option.id}
						type="button"
						className={`${css.option} ${
							option.id === config.selectedId
								? css.optionActive
								: ""
						}`}
						onClick={() => onSelectionChange?.(option.id)}
					>
						{option.label}
					</button>
				))}
			</div>
		);
	});

SegmentedControlView.displayName = "SegmentedControlView";
