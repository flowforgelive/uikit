import React from "react";
import { Skeleton, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

export function SkeletonShowcase({ tokens }: { tokens: any }) {
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
						<Skeleton width={80} height={60} />
						<Skeleton width={200} height={120} />
						<Skeleton width={300} height={200} />
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
							<Skeleton width={200} height={120} cornerRadius={30} />
							<Text text="30dp" variant="label-small" />
						</div>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Skeleton width={200} height={120} cornerRadius={60} />
							<Text text="60dp (pill)" variant="label-small" />
						</div>
					</div>
				</div>

				{/* Rectangle — full width */}
				<div>
					<SubSectionTitle tokens={tokens}>Rectangle (full width)</SubSectionTitle>
					<Skeleton height={120} />
				</div>

				{/* Rectangle — fixed size */}
				<div>
					<SubSectionTitle tokens={tokens}>Rectangle (Fixed size)</SubSectionTitle>
					<Skeleton width={200} height={80} cornerRadius={12} />
				</div>

				{/* Circle */}
				<div>
					<SubSectionTitle tokens={tokens}>Circle</SubSectionTitle>
					<div style={{ display: "flex", gap: toRem(tokens.spacing.md) }}>
						<Skeleton shape="circle" width={40} />
						<Skeleton shape="circle" width={56} />
						<Skeleton shape="circle" width={72} />
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
						<Skeleton height={160} cornerRadius={12} />
						<div style={{ display: "flex", gap: toRem(tokens.spacing.sm), alignItems: "center" }}>
							<Skeleton shape="circle" width={40} />
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
					<Skeleton height={48} animate={false} />
				</div>
			</div>
		</Section>
	);
}
