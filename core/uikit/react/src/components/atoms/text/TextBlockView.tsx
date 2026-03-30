"use client";

import React, { useMemo } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import {
	TextBlockStyleResolver,
	TextBlockVariant,
	Visibility,
	type TextBlockConfig,
	type DesignTokens,
} from "uikit-common";

interface TextBlockViewProps {
	config: TextBlockConfig;
	tokens?: DesignTokens;
	className?: string;
}

export const TextBlockView: React.FC<TextBlockViewProps> = React.memo(
	({ config, tokens: tokensProp, className }) => {
		if (config.visibility === Visibility.Gone) return null;

		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const style = useMemo(
			() => TextBlockStyleResolver.getInstance().resolve(config, tokens),
			[config, tokens],
		);

		const Tag =
			config.variant === TextBlockVariant.H1
				? "h1"
				: config.variant === TextBlockVariant.H2
					? "h2"
					: config.variant === TextBlockVariant.H3
						? "h3"
						: "p";

		return (
			<Tag
				data-testid={config.testTag ?? config.id}
				className={`uikit-text-block ${className ?? ""}`}
				style={{
					color: style.color,
					fontSize: `${style.fontSize}px`,
					fontWeight: style.fontWeight,
					lineHeight: `${style.lineHeight}px`,
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
