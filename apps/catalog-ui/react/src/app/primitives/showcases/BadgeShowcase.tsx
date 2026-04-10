import React from "react";
import { Badge, Button, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { starIcon, settingsIcon, searchIcon } from "../../components/icons";

const INTENTS = ["primary", "neutral", "danger"] as const;
const NUMERIC_VALUES = [1, 5, 42, 99, 100] as const;

export function BadgeShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="badge" title="Значок (Badge)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xl) }}>

				{/* Dot × intents */}
				<div>
					<SubSectionTitle tokens={tokens}>Dot</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.lg), marginTop: toRem(tokens.spacing.sm) }}>
						{INTENTS.map((intent) => (
							<div key={intent} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: toRem(tokens.spacing.xs) }}>
								<Badge variant="dot" intent={intent} />
								<Text text={intent} variant="label-small" emphasis="muted" />
							</div>
						))}
					</div>
				</div>

				{/* Numeric values */}
				<div>
					<SubSectionTitle tokens={tokens}>Numeric</SubSectionTitle>
					<Text
						text="Значения больше maxValue (99) отображаются как «99+»."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						{NUMERIC_VALUES.map((val) => (
							<div key={val} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: toRem(tokens.spacing.xs) }}>
								<Badge variant="numeric" value={val} />
								<Text text={`value=${val}`} variant="label-small" emphasis="muted" />
							</div>
						))}
					</div>
				</div>

				{/* All intents × numeric */}
				<div>
					<SubSectionTitle tokens={tokens}>Все intent × numeric</SubSectionTitle>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						{INTENTS.map((intent) => (
							<div key={intent} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
								<div style={{ width: toRem(60) }}>
									<Text text={intent} variant="label-medium" />
								</div>
								<Badge variant="numeric" value={3} intent={intent} />
								<Badge variant="numeric" value={42} intent={intent} />
								<Badge variant="numeric" value={100} intent={intent} />
							</div>
						))}
					</div>
				</div>

				{/* Use case: badge on icon button */}
				<div>
					<SubSectionTitle tokens={tokens}>Позиционирование на кнопке</SubSectionTitle>
					<Text
						text="Badge позиционируется рядом с кнопкой-иконкой через обёртку с position: relative."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.xl), marginTop: toRem(tokens.spacing.sm) }}>
						{/* Dot on icon button */}
						<div style={{ position: "relative", display: "inline-flex" }}>
							<Button icon={starIcon} variant="ghost" intent="neutral" ariaLabel="Favorites" />
							<div style={{ position: "absolute", top: toRem(2), insetInlineEnd: toRem(2) }}>
								<Badge variant="dot" />
							</div>
						</div>

						{/* Numeric on icon button */}
						<div style={{ position: "relative", display: "inline-flex" }}>
							<Button icon={settingsIcon} variant="ghost" intent="neutral" ariaLabel="Settings" />
							<div style={{ position: "absolute", top: toRem(0), insetInlineEnd: toRem(-4) }}>
								<Badge variant="numeric" value={5} />
							</div>
						</div>

						{/* Large numeric on icon button */}
						<div style={{ position: "relative", display: "inline-flex" }}>
							<Button icon={searchIcon} variant="ghost" intent="neutral" ariaLabel="Search" />
							<div style={{ position: "absolute", top: toRem(0), insetInlineEnd: toRem(-10) }}>
								<Badge variant="numeric" value={128} />
							</div>
						</div>
					</div>
				</div>
			</div>
		</Section>
	);
}

