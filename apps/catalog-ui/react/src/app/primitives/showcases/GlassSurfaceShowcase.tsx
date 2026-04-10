"use client";

import React from "react";
import { GlassSurface, Surface, Button, Chip, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

export function GlassSurfaceShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="glass-surface" title="Стеклянная поверхность (Glass)" tokens={tokens}>
			{/* Regular vs Clear */}
			<div style={{ display: "flex", flexWrap: "wrap", gap: toRem(tokens.spacing.lg) }}>
				<GlassSurface variant="regular" shape="xl" elevated>
					<div style={{ padding: toRem(tokens.spacing.lg), minWidth: toRem(220) }}>
						<Text text="Regular Glass" variant="title-medium" emphasis="primary" />
						<div style={{ marginBlockStart: toRem(tokens.spacing.xs) }}>
							<Text text="Blur 40 · Saturate 1.8×" variant="body-small" emphasis="secondary" />
						</div>
						<div style={{ marginBlockStart: toRem(tokens.spacing.md), display: "flex", gap: toRem(tokens.spacing.sm) }}>
							<Button text="Action" variant="glass" size="sm" />
							<Chip text="Tag" variant="glass" size="sm" />
						</div>
					</div>
				</GlassSurface>

				<GlassSurface variant="clear" shape="xl" elevated>
					<div style={{ padding: toRem(tokens.spacing.lg), minWidth: toRem(220) }}>
						<Text text="Clear Glass" variant="title-medium" emphasis="primary" />
						<div style={{ marginBlockStart: toRem(tokens.spacing.xs) }}>
							<Text text="Blur 4 · Saturate 1.2×" variant="body-small" emphasis="secondary" />
						</div>
						<div style={{ marginBlockStart: toRem(tokens.spacing.md), display: "flex", gap: toRem(tokens.spacing.sm) }}>
							<Button text="Action" variant="glass" intent="neutral" size="sm" />
							<Chip text="Tag" variant="glass" intent="neutral" size="sm" />
						</div>
					</div>
				</GlassSurface>
			</div>

			{/* Surface variant=glass levels */}
			<div style={{ marginBlockStart: toRem(tokens.spacing.xl) }}>
				<SubSectionTitle tokens={tokens}>Surface variant=glass</SubSectionTitle>
				<div style={{ display: "flex", flexWrap: "wrap", gap: toRem(tokens.spacing.md) }}>
					{([0, 2, 4] as const).map((level) => (
						<Surface key={level} variant="glass" level={level} shape="lg">
							<div style={{ padding: toRem(tokens.spacing.md), minWidth: toRem(160) }}>
								<Text text={`Glass · Level ${level}`} variant="title-small" emphasis="primary" />
								<div style={{ marginBlockStart: toRem(tokens.spacing.xs) }}>
									<Text text="backdrop-filter" variant="body-small" emphasis="secondary" />
								</div>
							</div>
						</Surface>
					))}
				</div>
			</div>

			{/* Elevated Glass */}
			<div style={{ marginBlockStart: toRem(tokens.spacing.xl) }}>
				<SubSectionTitle tokens={tokens}>Elevated Glass</SubSectionTitle>
				<GlassSurface variant="regular" shape="xl" elevated>
					<div style={{ padding: toRem(tokens.spacing.lg), display: "flex", alignItems: "center", justifyContent: "space-between", gap: toRem(tokens.spacing.xl), flexWrap: "wrap" }}>
						<div>
							<Text text="Elevated Glass Card" variant="title-medium" emphasis="primary" />
							<div style={{ marginBlockStart: toRem(tokens.spacing.xs) }}>
								<Text text="С тенью и backdrop-filter blur" variant="body-small" emphasis="secondary" />
							</div>
						</div>
						<Button text="Glass button" variant="glass" />
					</div>
				</GlassSurface>
			</div>
		</Section>
	);
}

