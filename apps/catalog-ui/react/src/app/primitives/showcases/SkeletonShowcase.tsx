import React from "react";
import { Skeleton, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { SCALE_FACTOR_MAP } from "../../components/catalog/CatalogConstants";

export function SkeletonShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const s = SCALE_FACTOR_MAP[globalSize] ?? 1.0;

	return (
		<Section id="skeleton" title="Скелетон (Skeleton)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xl) }}>

				{/* Adaptive radius — Rectangle (capped by maxContainerRadius) */}
				<div>
					<SubSectionTitle tokens={tokens}>Адаптивное скругление Rectangle (с cap)</SubSectionTitle>
					<Text
						text="Без явного cornerRadius → min(height × radiusFraction, maxContainerRadius). Переключите слайдер скругления."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						<Skeleton width={80 * s} height={60 * s} />
						<Skeleton width={200 * s} height={120 * s} />
						<Skeleton width={300 * s} height={200 * s} />
					</div>
				</div>

				{/* Explicit radius — Rectangle (no cap) */}
				<div>
					<SubSectionTitle tokens={tokens}>Принудительное скругление (explicit, без cap)</SubSectionTitle>
					<Text
						text="Явный cornerRadius игнорирует глобальный cap."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Skeleton width={200 * s} height={120 * s} cornerRadius={30 * s} />
							<Text text="30dp" variant="label-small" />
						</div>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Skeleton width={200 * s} height={120 * s} cornerRadius={60 * s} />
							<Text text="60dp (pill)" variant="label-small" />
						</div>
					</div>
				</div>

				{/* Rectangle — full width */}
				<div>
					<SubSectionTitle tokens={tokens}>Rectangle (full width)</SubSectionTitle>
					<Skeleton height={120 * s} />
				</div>

				{/* Rectangle — fixed size */}
				<div>
					<SubSectionTitle tokens={tokens}>Rectangle (Fixed size)</SubSectionTitle>
					<Skeleton width={200 * s} height={80 * s} />
				</div>

				{/* Circle */}
				<div>
					<SubSectionTitle tokens={tokens}>Circle</SubSectionTitle>
					<div style={{ display: "flex", gap: toRem(tokens.spacing.md) }}>
						<Skeleton shape="circle" width={40 * s} />
						<Skeleton shape="circle" width={56 * s} />
						<Skeleton shape="circle" width={72 * s} />
					</div>
				</div>

				{/* TextLine */}
				<div>
					<SubSectionTitle tokens={tokens}>TextLine</SubSectionTitle>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
						<Skeleton shape="text-line" />
						<div style={{ width: "75%" }}>
							<Skeleton shape="text-line" />
						</div>
						<div style={{ width: "50%" }}>
							<Skeleton shape="text-line" />
						</div>
					</div>
				</div>

				{/* Card-like composition */}
				<div>
					<SubSectionTitle tokens={tokens}>Card placeholder</SubSectionTitle>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
						<Skeleton height={160 * s} />
						<div style={{ display: "flex", gap: toRem(tokens.spacing.sm), alignItems: "center" }}>
							<Skeleton shape="circle" width={40 * s} />
							<div style={{ flex: 1, display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
								<Skeleton shape="text-line" />
								<div style={{ width: "60%" }}>
									<Skeleton shape="text-line" />
								</div>
							</div>
						</div>
					</div>
				</div>

				{/* Static — no animation */}
				<div>
					<SubSectionTitle tokens={tokens}>Без анимации</SubSectionTitle>
					<Skeleton height={48 * s} animate={false} />
				</div>
			</div>
		</Section>
	);
}

