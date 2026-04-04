import React from "react";
import { toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

const SPACING_KEYS = [
	["xxs", "xxs"],
	["xs", "xs"],
	["sm", "sm"],
	["md", "md"],
	["lg", "lg"],
	["xl", "xl"],
	["xxl", "xxl"],
	["xxxl", "xxxl"],
	["xxxxl", "xxxxl"],
] as const;

export function SpacingShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="spacing" title="Spacing" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
				{SPACING_KEYS.map(([key, label]) => {
					const val = tokens.spacing[key];
					return (
						<div key={key} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
							<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, minWidth: "4rem", textAlign: "end" }}>
								{label} ({val}dp)
							</span>
							<div
								style={{
									width: toRem(val),
									height: toRem(tokens.spacing.lg),
									backgroundColor: tokens.color.primary,
									borderRadius: toRem(tokens.radius.xs),
									transition: `width ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}`,
								}}
							/>
						</div>
					);
				})}
			</div>
		</Section>
	);
}
