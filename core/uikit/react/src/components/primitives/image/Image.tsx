"use client";

import React, { useMemo } from "react";
import { ImageView } from "./ImageView";
import { ImageConfig, ImageFit, ImageLoading } from "uikit-common";

const FIT_MAP = {
	cover: ImageFit.Cover,
	contain: ImageFit.Contain,
	fill: ImageFit.Fill,
	none: ImageFit.None,
	"scale-down": ImageFit.ScaleDown,
} as const;

const LOADING_MAP = {
	eager: ImageLoading.Eager,
	lazy: ImageLoading.Lazy,
} as const;

interface ImageProps {
	src?: string;
	alt?: string;
	width?: number;
	height?: number;
	objectFit?: keyof typeof FIT_MAP;
	cornerRadius?: number;
	showBorder?: boolean;
	placeholder?: string;
	fallback?: string;
	loading?: keyof typeof LOADING_MAP;
	className?: string;
}

export const Image: React.FC<ImageProps> = React.memo(
	({
		src = "",
		alt = "",
		width,
		height,
		objectFit = "cover",
		cornerRadius,
		showBorder,
		placeholder,
		fallback,
		loading = "eager",
		className,
	}) => {
		const config = useMemo(
			() =>
				new ImageConfig(
					src,
					alt,
					width ?? 0,
					height ?? 0,
					FIT_MAP[objectFit],
					cornerRadius ?? null,
					showBorder ?? false,
					placeholder ?? null,
					fallback ?? null,
					LOADING_MAP[loading],
				),
			[src, alt, width, height, objectFit, cornerRadius, showBorder, placeholder, fallback, loading],
		);

		return <ImageView config={config} className={className} />;
	},
);

Image.displayName = "Image";
