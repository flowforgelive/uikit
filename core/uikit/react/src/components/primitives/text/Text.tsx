"use client";

import React, { useMemo } from "react";
import { TextBlockView } from "./TextBlockView";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import { TextBlockConfig, TextBlockVariant } from "uikit-common";

const VARIANT_MAP = {
	"display-large": TextBlockVariant.DisplayLarge,
	"display-medium": TextBlockVariant.DisplayMedium,
	"display-small": TextBlockVariant.DisplaySmall,
	"headline-large": TextBlockVariant.HeadlineLarge,
	"headline-medium": TextBlockVariant.HeadlineMedium,
	"headline-small": TextBlockVariant.HeadlineSmall,
	"title-large": TextBlockVariant.TitleLarge,
	"title-medium": TextBlockVariant.TitleMedium,
	"title-small": TextBlockVariant.TitleSmall,
	"body-large": TextBlockVariant.BodyLarge,
	"body-medium": TextBlockVariant.BodyMedium,
	"body-small": TextBlockVariant.BodySmall,
	"label-large": TextBlockVariant.LabelLarge,
	"label-medium": TextBlockVariant.LabelMedium,
	"label-small": TextBlockVariant.LabelSmall,
} as const;

interface TextProps {
	text: string;
	variant?: keyof typeof VARIANT_MAP;
	className?: string;
}

export const Text: React.FC<TextProps> = React.memo(
	({ text, variant = "body-large", className }) => {
		const tokens = useDesignTokens();
		const config = useMemo(
			() => new TextBlockConfig(text, VARIANT_MAP[variant]),
			[text, variant],
		);

		return <TextBlockView config={config} tokens={tokens} className={className} />;
	},
);

Text.displayName = "Text";
