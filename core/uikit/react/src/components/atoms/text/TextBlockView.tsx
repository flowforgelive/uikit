import React from "react";
import { toRem } from "../../../utils/units";
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

export function TextBlockView({ config, tokens, className }: TextBlockViewProps) {
	if (config.visibility === Visibility.Gone) return null;

	const style = TextBlockStyleResolver.getInstance().resolve(config, tokens);

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
				fontSize: toRem(style.fontSize),
				fontWeight: style.fontWeight,
				lineHeight: toRem(style.lineHeight),
				letterSpacing: toRem(style.letterSpacing),
				margin: 0,
				visibility:
					config.visibility === Visibility.Invisible ? "hidden" : undefined,
			}}
		>
			{config.text}
		</Tag>
	);
}
