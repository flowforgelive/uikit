import React from "react";
import { Text, toRem } from "@uikit/react";
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
						<div style={{ minWidth: "6rem", textAlign: "end" }}>
							<Text text={label} variant="label-medium" />
						</div>
						<Text text={`${val}dp`} variant="body-large" emphasis="primary" className="monospace-text" />
					</div>
				))}
			</div>
		</Section>
	);
}
