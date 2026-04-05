"use client";

import React, { useMemo, useCallback, useRef } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { useSurfaceContext } from "../../../theme/SurfaceContext";
import { toRem, toEm, toLineHeightRatio } from "../../../utils/units";
import {
	SegmentedControlStyleResolver,
	Visibility,
	IconPosition,
	type SegmentedControlConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./SegmentedControlView.module.css";

interface SegmentedControlViewProps {
	config: SegmentedControlConfig;
	onSelectionChange?: (id: string) => void;
	renderIcon?: (iconId: string) => React.ReactNode;
	tokens?: DesignTokens;
	className?: string;
}

export const SegmentedControlView: React.FC<SegmentedControlViewProps> =
	React.memo(({ config, onSelectionChange, renderIcon, tokens: tokensProp, className }) => {
		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const surface = useSurfaceContext();
		const style = useMemo(
			() => SegmentedControlStyleResolver.getInstance().resolve(config, tokens, surface),
			[config, tokens, surface],
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

		if (config.visibility === Visibility.Gone) return null;

		const hasIcons = renderIcon && config.iconPosition !== IconPosition.None;
		const isVertical = config.iconPosition === IconPosition.Top || config.iconPosition === IconPosition.Bottom;
		const isReversed = config.iconPosition === IconPosition.End || config.iconPosition === IconPosition.Bottom;

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
						"--sc-border": style.colors.border,
						"--sc-border-width": `${tokens.borderWidth}px`,
						"--sc-height": toRem(style.sizes.height),
						"--sc-padding-v": toRem(style.sizes.paddingV),
						"--sc-font-size": toRem(style.sizes.fontSize),
						"--sc-font-weight": style.sizes.fontWeight,
						"--sc-letter-spacing": toEm(style.sizes.letterSpacing, style.sizes.fontSize),
						"--sc-line-height": toLineHeightRatio(style.sizes.lineHeight, style.sizes.fontSize),
						"--sc-font-variation": tokens.fontVariationSettings,
						"--sc-padding-h": toRem(style.sizes.paddingH),
						"--sc-radius": toRem(style.sizes.radius),
						"--sc-thumb-radius": toRem(style.sizes.thumbRadius),
						"--sc-track-padding": toRem(style.sizes.trackPadding),
						"--sc-thumb-offset": `${selectedIndex * 100}%`,
					"--sc-option-count": options.length,
						"--sc-duration": `${tokens.motion.durationNormal}ms`,
						"--sc-easing": tokens.motion.easingStandard,
						"--sc-focus-ring": tokens.color.focusRing,
						"--sc-focus-ring-width": `${tokens.focusRingWidth}px`,
						"--sc-icon-size": toRem(style.sizes.iconSize),
						"--sc-icon-gap": toRem(style.sizes.iconGap),
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				<div className={css.thumb} />
				{Array.from(options).map((option: any, index: number) => {
					const isSelected = option.id === config.selectedId;
					const iconNode = hasIcons && option.iconId ? renderIcon(option.iconId) : null;
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
							{iconNode ? (
								<span className={`${css.optionContent} ${isVertical ? css.optionContentVertical : ""} ${isReversed ? css.optionContentReversed : ""}`}>
									<span className={css.optionIcon}>{iconNode}</span>
									<span>{option.label}</span>
								</span>
							) : (
								option.label
							)}
						</button>
					);
				})}
			</div>
		);
	});

SegmentedControlView.displayName = "SegmentedControlView";
