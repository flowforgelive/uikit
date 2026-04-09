"use client";

import React, { useState } from "react";
import { Chip, Text, SegmentedControl, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { starIcon, searchIcon } from "../../components/icons";

const CHIP_VARIANTS = ["soft", "outline", "ghost"] as const;

const MODE_OPTIONS = [
	{ id: "static", label: "Static" },
	{ id: "interactive", label: "Interactive" },
	{ id: "selected", label: "Selected" },
	{ id: "disabled", label: "Disabled" },
];

const INTENT_OPTIONS = [
	{ id: "primary", label: "Primary" },
	{ id: "neutral", label: "Neutral" },
	{ id: "danger", label: "Danger" },
];

export function ChipShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const selectedSize = globalSize;
	const [selectedMode, setSelectedMode] = useState("static");
	const [selectedIntent, setSelectedIntent] = useState("primary");

	const isStatic = selectedMode === "static";
	const isDisabled = selectedMode === "disabled";
	const isSelected = selectedMode === "selected";

	return (
		<Section id="chip" title="Чип (Chip)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md), marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Режим:" variant="label-medium" />
					<SegmentedControl options={MODE_OPTIONS} selectedId={selectedMode} onSelectionChange={setSelectedMode} />
				</div>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Intent:" variant="label-medium" />
					<SegmentedControl options={INTENT_OPTIONS} selectedId={selectedIntent} onSelectionChange={setSelectedIntent} />
				</div>
			</div>
			{CHIP_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), flexWrap: "wrap" }}>
						<Chip
							text="Chip"
							variant={variant}
							intent={selectedIntent as any}
							size={selectedSize as any}
							onClick={isStatic ? undefined : () => {}}
							disabled={isDisabled}
							selected={isSelected}
						/>
						<Chip
							text="С иконкой"
							variant={variant}
							intent={selectedIntent as any}
							size={selectedSize as any}
							leadingIcon={searchIcon}
							onClick={isStatic ? undefined : () => {}}
							disabled={isDisabled}
							selected={isSelected}
						/>
						<Chip
							text="Dismissible"
							variant={variant}
							intent={selectedIntent as any}
							size={selectedSize as any}
							dismissible
							onDismiss={() => {}}
							onClick={isStatic ? undefined : () => {}}
							disabled={isDisabled}
							selected={isSelected}
						/>
						<Chip
							text="Icon + Dismiss"
							variant={variant}
							intent={selectedIntent as any}
							size={selectedSize as any}
							leadingIcon={starIcon}
							dismissible
							onDismiss={() => {}}
							onClick={isStatic ? undefined : () => {}}
							disabled={isDisabled}
							selected={isSelected}
						/>
					</div>
				</div>
			))}
		</Section>
	);
}
