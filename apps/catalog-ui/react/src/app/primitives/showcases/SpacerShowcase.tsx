import React from "react";
import { Spacer, Surface, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

const SPACING_DEMOS = [
	{ label: "4", size: 4 },
	{ label: "8", size: 8 },
	{ label: "12", size: 12 },
	{ label: "16", size: 16 },
	{ label: "24", size: 24 },
	{ label: "32", size: 32 },
] as const;

export function SpacerShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="spacer" title="Отступ (Spacer)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xl) }}>

				{/* Visual ruler — all sizes side by side */}
				<div>
					<SubSectionTitle tokens={tokens}>Размеры (визуально)</SubSectionTitle>
					<Text
						text="Цветные полоски показывают высоту каждого spacer-значения."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", alignItems: "flex-end", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						{SPACING_DEMOS.map(({ label, size }) => (
							<div key={label} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: toRem(tokens.spacing.xs) }}>
								<div style={{
									width: toRem(40),
									height: toRem(size),
									backgroundColor: tokens.color.primarySoft,
									borderRadius: toRem(tokens.radius.sm),
									border: `1px solid ${tokens.color.primary}`,
								}} />
								<Text text={`${label}dp`} variant="label-small" emphasis="muted" />
							</div>
						))}
					</div>
				</div>

				{/* Vertical between blocks */}
				<div>
					<SubSectionTitle tokens={tokens}>Vertical между блоками</SubSectionTitle>
					<Text
						text="Spacer вставляется между Surface-блоками. Цветная линейка слева визуализирует размер."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", flexDirection: "column", marginTop: toRem(tokens.spacing.sm) }}>
						{SPACING_DEMOS.filter(d => [8, 16, 32].includes(d.size)).map(({ label, size }) => (
							<React.Fragment key={label}>
								<Surface variant="soft" shape="sm" style={{ padding: toRem(tokens.spacing.sm) }}>
									<Text text="Block" variant="label-small" />
								</Surface>
								<div style={{ display: "flex", alignItems: "stretch" }}>
									<div style={{
										width: toRem(4),
										backgroundColor: tokens.color.primary,
										borderRadius: toRem(2),
										marginInlineEnd: toRem(tokens.spacing.xs),
									}} />
									<div style={{ flex: 1, position: "relative" }}>
										<Spacer size={size} axis="vertical" />
										<Text
											text={`${label}dp`}
											variant="label-small"
											emphasis="muted"
											style={{ position: "absolute", top: "50%", transform: "translateY(-50%)", insetInlineStart: toRem(tokens.spacing.xs) }}
										/>
									</div>
								</div>
							</React.Fragment>
						))}
						<Surface variant="soft" shape="sm" style={{ padding: toRem(tokens.spacing.sm) }}>
							<Text text="Block" variant="label-small" />
						</Surface>
					</div>
				</div>

				{/* Horizontal fixed */}
				<div>
					<SubSectionTitle tokens={tokens}>Horizontal (fixed)</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "center", marginTop: toRem(tokens.spacing.sm) }}>
						{SPACING_DEMOS.filter(d => [8, 24, 48].includes(d.size)).map(({ label, size }, i, arr) => (
							<React.Fragment key={label}>
								<Surface variant="soft" shape="sm" style={{ padding: toRem(tokens.spacing.sm) }}>
									<Text text={String.fromCharCode(65 + i)} variant="label-small" />
								</Surface>
								{i < arr.length && (
									<div style={{ position: "relative", display: "flex", alignItems: "center" }}>
										<div style={{
											position: "absolute",
											inset: 0,
											backgroundColor: tokens.color.primarySoft,
											borderRadius: toRem(2),
										}} />
										<Spacer size={size} axis="horizontal" />
									</div>
								)}
							</React.Fragment>
						))}
						<Surface variant="soft" shape="sm" style={{ padding: toRem(tokens.spacing.sm) }}>
							<Text text="D" variant="label-small" />
						</Surface>
					</div>
				</div>

				{/* Flexible */}
				<div>
					<SubSectionTitle tokens={tokens}>Flexible</SubSectionTitle>
					<Text
						text="Spacer без size заполняет доступное пространство (flex: 1)."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{
						display: "flex", alignItems: "center",
						border: `1px dashed ${tokens.color.outlineVariant}`,
						padding: toRem(tokens.spacing.sm),
						borderRadius: toRem(tokens.radius.md),
						marginTop: toRem(tokens.spacing.sm),
					}}>
						<Text text="Слева" variant="body-medium" />
						<Spacer axis="horizontal" />
						<Text text="Справа" variant="body-medium" />
					</div>
				</div>
			</div>
		</Section>
	);
}
