"use client";

import React, { useMemo, useState, useCallback, useEffect } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { toRem } from "../../../utils/units";
import {
	ImageStyleResolver,
	ImageLoading,
	Visibility,
	type ImageConfig,
	type DesignTokens,
} from "uikit-common";
import css from "./ImageView.module.css";

interface ImageViewProps {
	config: ImageConfig;
	tokens?: DesignTokens;
	className?: string;
}

export const ImageView: React.FC<ImageViewProps> = React.memo(
	({ config, tokens: tokensProp, className }) => {
		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const style = useMemo(
			() => ImageStyleResolver.getInstance().resolve(config, tokens),
			[config, tokens],
		);

		const [isLoading, setIsLoading] = useState(true);
		const [hasError, setHasError] = useState(false);
		const [isFailed, setIsFailed] = useState(false);

		useEffect(() => {
			setIsLoading(true);
			setHasError(false);
			setIsFailed(false);
		}, [config.src]);

		const handleLoad = useCallback(() => {
			setIsLoading(false);
			setIsFailed(false);
		}, []);

		const handleError = useCallback(() => {
			setIsLoading(false);
			if (style.fallback && !hasError) {
				setHasError(true);
				setIsLoading(true);
			} else {
				setIsFailed(true);
			}
		}, [style.fallback, hasError]);

		if (config.visibility === Visibility.Gone) return null;

		const activeSrc = hasError && style.fallback ? style.fallback : config.src;

		const cssVars = {
			"--img-width": style.width > 0 ? toRem(style.width) : undefined,
			"--img-height": style.height > 0 ? toRem(style.height) : undefined,
			"--img-fit": style.objectFit,
			"--img-radius": style.cornerRadius > 0 ? toRem(style.cornerRadius) : undefined,
			"--img-border": style.borderColor
				? `inset 0 0 0 1px ${style.borderColor}`
				: undefined,
			"--img-skeleton-base": tokens.color.surfaceContainerHigh,
			"--img-skeleton-pulse": tokens.color.surfaceContainerHighest,
			visibility:
				config.visibility === Visibility.Invisible ? ("hidden" as const) : undefined,
		} as React.CSSProperties;

		return (
			<div
				className={`${css.wrapper} ${className ?? ""}`}
				style={cssVars}
				data-testid={config.testTag ?? (config.id || undefined)}
			>
				{(isLoading || isFailed) && (
					<div
						className={`${css.skeleton} ${isFailed ? css.skeletonStatic : ""}`}
						aria-hidden="true"
					/>
				)}
				<img
					src={activeSrc}
					alt={config.alt}
					loading={config.loading === ImageLoading.Lazy ? "lazy" : undefined}
					onLoad={handleLoad}
					onError={handleError}
					className={`${css.image} ${isLoading ? css.imageLoading : ""}`}
				/>
			</div>
		);
	},
);

ImageView.displayName = "ImageView";
