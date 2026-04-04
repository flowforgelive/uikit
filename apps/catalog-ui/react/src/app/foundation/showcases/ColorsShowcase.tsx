import React from "react";
import { toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

const COLOR_KEYS = [
	["primary", "Primary"],
	["primaryHover", "Primary Hover"],
	["secondary", "Secondary"],
	["danger", "Danger"],
	["dangerHover", "Danger Hover"],
	["dangerSoft", "Danger Soft"],
	["dangerSoftHover", "Danger Soft Hover"],
	["background", "Background"],
	["surface", "Surface"],
	["surfaceContainerLowest", "Container Lowest"],
	["surfaceContainerLow", "Container Low"],
	["surfaceContainer", "Surface Container"],
	["surfaceContainerHigh", "Container High"],
	["surfaceContainerHighest", "Container Highest"],
	["surfaceHover", "Surface Hover"],
	["onSurface", "On Surface"],
	["outline", "Outline"],
	["outlineVariant", "Outline Variant"],
	["textPrimary", "Text Primary"],
	["textSecondary", "Text Secondary"],
	["textMuted", "Text Muted"],
	["textOnPrimary", "Text on Primary"],
	["textOnDanger", "Text on Danger"],
	["textDisabled", "Text Disabled"],
	["surfaceDisabled", "Surface Disabled"],
	["borderDisabled", "Border Disabled"],
	["border", "Border"],
] as const;

export function ColorsShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="colors" title="Colors" tokens={tokens}>
			<div
				style={{
					display: "grid",
					gridTemplateColumns: "repeat(auto-fill, minmax(120px, 1fr))",
					gap: toRem(tokens.spacing.md),
				}}
			>
				{COLOR_KEYS.map(([key, label]) => {
					const hex = tokens.color[key];
					return (
						<div key={key} style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<div
								style={{
									width: "100%",
									aspectRatio: "1",
									backgroundColor: hex,
									borderRadius: toRem(tokens.radius.md),
									boxShadow: `inset 0 0 0 1px ${tokens.color.outlineVariant}`,
								}}
							/>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), fontWeight: 600, color: tokens.color.textPrimary }}>
							{label}
						</span>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), color: tokens.color.textMuted, fontFamily: "monospace" }}>
								{hex}
							</span>
						</div>
					);
				})}
			</div>
		</Section>
	);
}
