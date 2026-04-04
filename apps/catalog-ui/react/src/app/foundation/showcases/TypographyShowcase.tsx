import React from "react";
import { toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

const TYPOGRAPHY_STYLES = [
	["displayLarge", "Display Large"],
	["displayMedium", "Display Medium"],
	["displaySmall", "Display Small"],
	["headlineLarge", "Headline Large"],
	["headlineMedium", "Headline Medium"],
	["headlineSmall", "Headline Small"],
	["titleLarge", "Title Large"],
	["titleMedium", "Title Medium"],
	["titleSmall", "Title Small"],
	["bodyLarge", "Body Large"],
	["bodyMedium", "Body Medium"],
	["bodySmall", "Body Small"],
	["labelLarge", "Label Large"],
	["labelMedium", "Label Medium"],
	["labelSmall", "Label Small"],
] as const;

export function TypographyShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="typography" title="Typography" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
				{TYPOGRAPHY_STYLES.map(([key, label]) => {
					const style = tokens.typography[key];
					return (
						<div key={key} style={{ display: "flex", alignItems: "baseline", gap: toRem(tokens.spacing.lg), flexWrap: "wrap" }}>
							<span
								style={{
									...textStyle(style, tokens),
									color: tokens.color.textPrimary,
								}}
							>
								{label}
							</span>
							<span
								style={{
									...textStyle(tokens.typography.labelMedium, tokens),
									color: tokens.color.textMuted,
								}}
							>
								{style.fontSize}dp / {style.fontWeight} / {style.lineHeight}dp
							</span>
						</div>
					);
				})}
			</div>
		</Section>
	);
}
