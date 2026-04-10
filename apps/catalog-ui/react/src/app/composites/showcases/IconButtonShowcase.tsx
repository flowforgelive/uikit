"use client";

import React, { useState } from "react";
import { Button, Text, SegmentedControl, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { starIcon, ICON_BUTTON_SAMPLES } from "../../components/icons";

const BUTTON_VARIANTS = ["solid", "soft", "surface", "outline", "ghost", "glass"] as const;

const STATE_OPTIONS = [
	{ id: "active", label: "Active" },
	{ id: "disabled", label: "Disabled" },
	{ id: "loading", label: "Loading" },
];

const INTENT_OPTIONS = [
	{ id: "primary", label: "Primary" },
	{ id: "neutral", label: "Neutral" },
	{ id: "danger", label: "Danger" },
];

export function IconButtonShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const selectedSize = globalSize;
	const [selectedState, setSelectedState] = useState("active");
	const [selectedIntent, setSelectedIntent] = useState("primary");

	const isDisabled = selectedState === "disabled";
	const isLoading = selectedState === "loading";

	return (
		<Section id="icon-only-button" title="Кнопка-иконка (Button icon-only)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md), marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Состояние:" variant="label-medium" />
					<SegmentedControl options={STATE_OPTIONS} selectedId={selectedState} onSelectionChange={setSelectedState} />
				</div>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Intent:" variant="label-medium" />
					<SegmentedControl options={INTENT_OPTIONS} selectedId={selectedIntent} onSelectionChange={setSelectedIntent} />
				</div>
			</div>
			{BUTTON_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), overflowX: "auto" }}>
						{ICON_BUTTON_SAMPLES.map((icon, i) => (
							<Button
								key={i}
								icon={icon}
								variant={variant}
								intent={selectedIntent as any}
								size={selectedSize as any}
								ariaLabel="Icon button"
								disabled={isDisabled}
								loading={isLoading}
							/>
						))}
					</div>
				</div>
			))}
		</Section>
	);
}

