"use client";

import React, { useMemo } from "react";
import { TextBlockView } from "./TextBlockView";
import { TextBlockConfig, TextBlockVariant, Visibility } from "uikit-common";

const VARIANT_MAP = {
	h1: TextBlockVariant.H1,
	h2: TextBlockVariant.H2,
	h3: TextBlockVariant.H3,
	body: TextBlockVariant.Body,
	caption: TextBlockVariant.Caption,
} as const;

interface TextProps {
	text: string;
	variant?: keyof typeof VARIANT_MAP;
	className?: string;
}

export const Text: React.FC<TextProps> = React.memo(
	({ text, variant = "body", className }) => {
		const config = useMemo(
			() =>
				new TextBlockConfig(
					"",
					text,
					VARIANT_MAP[variant],
					null,
					Visibility.Visible,
				),
			[text, variant],
		);

		return <TextBlockView config={config} className={className} />;
	},
);

Text.displayName = "Text";
