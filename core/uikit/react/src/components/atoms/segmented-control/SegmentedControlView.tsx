"use client";

import React, { useMemo, useCallback, useRef } from "react";
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

		const optionRefs = useRef<(HTMLButtonElement | null)[]>([]);

		const handleKeyDown = useCallback(
			(e: React.KeyboardEvent, index: number) => {
				const len = options.length;
				let next = -1;
				switch (e.key) {
					case "ArrowRight":
					case "ArrowDown":
						next = (index + 1) % len;
						break;
					case "ArrowLeft":
					case "ArrowUp":
						next = (index - 1 + len) % len;
						break;
					case "Home":
						next = 0;
						break;
					case "End":
						next = len - 1;
						break;
					case "Enter":
					case " ":
						e.preventDefault();
						onSelectionChange?.((options as any)[index].id);
						return;
					default:
						return;
				}
				e.preventDefault();
				const opt = (options as any)[next];
				onSelectionChange?.(opt.id);
				optionRefs.current[next]?.focus();
			},
			[options, onSelectionChange],
		);

		return (
			<div
				data-testid={config.testTag ?? config.id}
				role="radiogroup"
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
					"--sc-option-count": options.length,
						"--sc-duration": `${tokens.motion.durationNormal}ms`,
						"--sc-easing": tokens.motion.easingStandard,
						"--sc-focus-ring": tokens.color.focusRing,
						"--sc-focus-ring-width": `${tokens.focusRingWidth}px`,
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				<div className={css.thumb} />
				{Array.from(options).map((option: any, index: number) => {
					const isSelected = option.id === config.selectedId;
					return (
						<button
							key={option.id}
							ref={(el) => { optionRefs.current[index] = el; }}
							type="button"
							role="radio"
							aria-checked={isSelected}
							tabIndex={0}
							className={`${css.option} ${isSelected ? css.optionActive : ""}`}
							onClick={() => onSelectionChange?.(option.id)}
							onKeyDown={(e) => handleKeyDown(e, index)}
						>
							{option.label}
						</button>
					);
				})}
			</div>
		);
	});

SegmentedControlView.displayName = "SegmentedControlView";
