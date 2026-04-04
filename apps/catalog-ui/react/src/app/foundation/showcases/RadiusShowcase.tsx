import React from "react";
import { toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

const RADIUS_KEYS = [
	["xs", "xs"],
	["sm", "sm"],
	["md", "md"],
	["lg", "lg"],
	["xl", "xl"],
	["full", "full"],
] as const;

export function RadiusShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="radius" title="Radius" tokens={tokens}>
			<div style={{ display: "flex", gap: toRem(tokens.spacing.lg), flexWrap: "wrap" }}>
				{RADIUS_KEYS.map(([key, label]) => {
					const val = tokens.radius[key];
					return (
						<div key={key} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: toRem(tokens.spacing.xs) }}>
							<div
								style={{
									width: toRem(56),
									height: toRem(56),
									backgroundColor: tokens.color.primary,
									borderRadius: toRem(val),
								}}
							/>
							<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted }}>
								{label} ({val}dp)
							</span>
						</div>
					);
				})}
			</div>
		</Section>
	);
}
