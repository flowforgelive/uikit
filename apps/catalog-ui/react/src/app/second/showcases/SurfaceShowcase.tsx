"use client";

import React, { useState } from "react";
import { Surface, SegmentedControl, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

const SURFACE_VARIANTS = ["solid", "soft", "surface", "outline", "ghost"] as const;
const SURFACE_LEVELS = [0, 1, 2, 3, 4, 5] as const;

const SURFACE_MODES = [
	{ id: "default", label: "Обычный" },
	{ id: "hoverable", label: "По наведению" },
	{ id: "clickable", label: "По клику" },
];

export function SurfaceShowcase({ tokens }: { tokens: any }) {
	const [mode, setMode] = useState("default");

	return (
		<Section id="surface" title="Поверхность (Surface) — Вариант × Уровень" tokens={tokens}>
			<div style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<SegmentedControl
					options={SURFACE_MODES}
					selectedId={mode}
					onSelectionChange={setMode}
				/>
			</div>
			{SURFACE_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					<div
						style={{
							display: "flex",
							gap: toRem(tokens.spacing.md),
							overflowX: "auto",
						}}
					>
						{SURFACE_LEVELS.map((level) => (
							<Surface
								key={level}
								variant={variant}
								level={level}
								hoverable={mode === "hoverable"}
								clickable={mode === "clickable"}
								onClick={mode === "clickable" ? () => {} : undefined}
							>
								<div style={{ padding: toRem(tokens.spacing.md), width: toRem(120) }}>
									<span
										style={{
											fontSize: toRem(tokens.typography.labelMedium.fontSize),
											fontWeight: 600,
											color: tokens.color.textPrimary,
											display: "block",
										}}
									>
										Уровень {level}
									</span>
									<span
										style={{
											fontSize: toRem(tokens.typography.labelSmall.fontSize),
											color: tokens.color.text,
											marginBlockStart: toRem(tokens.spacing.xs),
											display: "block",
										}}
									>
										{variant}{variant === "ghost" ? " (hover)" : ""}
									</span>
								</div>
							</Surface>
						))}
					</div>
				</div>
			))}
		</Section>
	);
}
