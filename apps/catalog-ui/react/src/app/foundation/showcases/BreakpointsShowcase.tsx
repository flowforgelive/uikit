import React from "react";
import { toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

export function BreakpointsShowcase({ tokens }: { tokens: any }) {
	const bp = [
		["compact", tokens.breakpoints.compact],
		["medium", tokens.breakpoints.medium],
		["expanded", tokens.breakpoints.expanded],
		["large", tokens.breakpoints.large],
		["extraLarge", tokens.breakpoints.extraLarge],
	] as const;

	return (
		<Section id="breakpoints" title="Breakpoints (Material Design 3)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
				{bp.map(([label, val]) => (
					<div key={label} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, minWidth: "6rem", textAlign: "end" }}>
						{label}
					</span>
					<span style={{ ...textStyle(tokens.typography.bodyLarge, tokens), fontWeight: 600, color: tokens.color.textPrimary, fontFamily: "monospace" }}>
							{val}dp
						</span>
					</div>
				))}
			</div>
		</Section>
	);
}
