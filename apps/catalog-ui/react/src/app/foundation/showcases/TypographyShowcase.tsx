import React from "react";
import { Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

const TYPOGRAPHY_STYLES = [
	["displayLarge", "Display Large", "display-large"],
	["displayMedium", "Display Medium", "display-medium"],
	["displaySmall", "Display Small", "display-small"],
	["headlineLarge", "Headline Large", "headline-large"],
	["headlineMedium", "Headline Medium", "headline-medium"],
	["headlineSmall", "Headline Small", "headline-small"],
	["titleLarge", "Title Large", "title-large"],
	["titleMedium", "Title Medium", "title-medium"],
	["titleSmall", "Title Small", "title-small"],
	["bodyLarge", "Body Large", "body-large"],
	["bodyMedium", "Body Medium", "body-medium"],
	["bodySmall", "Body Small", "body-small"],
	["labelLarge", "Label Large", "label-large"],
	["labelMedium", "Label Medium", "label-medium"],
	["labelSmall", "Label Small", "label-small"],
] as const;

export function TypographyShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="typography" title="Typography" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
				{TYPOGRAPHY_STYLES.map(([key, label, variant]) => {
					const style = tokens.typography[key];
					return (
						<div key={key} style={{ display: "flex", alignItems: "baseline", gap: toRem(tokens.spacing.lg), flexWrap: "wrap" }}>
							<Text text={label} variant={variant as any} emphasis="primary" />
							<Text text={`${style.fontSize}dp / ${style.fontWeight} / ${style.lineHeight}dp`} variant="label-medium" />
						</div>
					);
				})}
			</div>
		</Section>
	);
}
