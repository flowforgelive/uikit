"use client";

import React, { useMemo } from "react";
import { toRem, toEm, toLineHeightRatio, buildFontStack } from "../../../utils/units";
import {
	TextBlockStyleResolver,
	TextBlockVariant,
	Visibility,
	type TextBlockConfig,
	type DesignTokens,
} from "uikit-common";

interface TextBlockViewProps {
	config: TextBlockConfig;
	tokens: DesignTokens;
	className?: string;
}

export const TextBlockView: React.FC<TextBlockViewProps> = React.memo(
	({ config, tokens, className }) => {
		if (config.visibility === Visibility.Gone) return null;

		const style = useMemo(
			() => TextBlockStyleResolver.getInstance().resolve(config, tokens),
			[config, tokens],
		);

		const variant = config.variant;
		const Tag =
			variant === TextBlockVariant.DisplayLarge || variant === TextBlockVariant.DisplayMedium || variant === TextBlockVariant.DisplaySmall
				? "h1"
				: variant === TextBlockVariant.HeadlineLarge || variant === TextBlockVariant.HeadlineMedium || variant === TextBlockVariant.HeadlineSmall
					? "h2"
					: variant === TextBlockVariant.TitleLarge || variant === TextBlockVariant.TitleMedium || variant === TextBlockVariant.TitleSmall
						? "h3"
						: variant === TextBlockVariant.LabelLarge || variant === TextBlockVariant.LabelMedium || variant === TextBlockVariant.LabelSmall
							? "span"
							: "p";

		return (
			<Tag
				data-testid={config.testTag ?? config.id}
				className={`uikit-text-block ${className ?? ""}`}
				style={{
					color: style.color,
					fontFamily: buildFontStack(tokens.fontFamilyName),
					fontSize: toRem(style.fontSize),
					fontWeight: style.fontWeight,
					lineHeight: toLineHeightRatio(style.lineHeight, style.fontSize),
					letterSpacing: toEm(style.letterSpacing, style.fontSize),
					fontVariationSettings: tokens.fontVariationSettings,
					margin: 0,
					visibility:
						config.visibility === Visibility.Invisible ? "hidden" : undefined,
				}}
			>
				{config.text}
			</Tag>
		);
	},
);

TextBlockView.displayName = "TextBlockView";
