"use client";

import React, { useState } from "react";
import { Button, Text, SegmentedControl, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import {
	searchIcon,
	starIcon,
	checkIcon,
	arrowLeftIcon,
	arrowRightIcon,
	chevronRightIcon,
} from "../../components/icons";

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

const ICON_POSITION_OPTIONS = [
	{ id: "none", label: "None" },
	{ id: "start", label: "Start" },
	{ id: "end", label: "End" },
	{ id: "top", label: "Top" },
	{ id: "bottom", label: "Bottom" },
	{ id: "startend", label: "Start+End" },
];

export function ButtonShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const selectedSize = globalSize;
	const [selectedState, setSelectedState] = useState("active");
	const [selectedIntent, setSelectedIntent] = useState("primary");
	const [selectedPosition, setSelectedPosition] = useState("none");
	const isStartEnd = selectedPosition === "startend";
	const hasIcons = selectedPosition !== "none";

	const isDisabled = selectedState === "disabled";
	const isLoading = selectedState === "loading";

	return (
		<Section id="buttons" title="Кнопка (Button)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md), marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Состояние:" variant="label-medium" />
					<SegmentedControl options={STATE_OPTIONS} selectedId={selectedState} onSelectionChange={setSelectedState} />
				</div>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Intent:" variant="label-medium" />
					<SegmentedControl options={INTENT_OPTIONS} selectedId={selectedIntent} onSelectionChange={setSelectedIntent} />
				</div>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Позиция иконки:" variant="label-medium" />
					<SegmentedControl options={ICON_POSITION_OPTIONS} selectedId={selectedPosition} onSelectionChange={setSelectedPosition} />
				</div>
			</div>
			{BUTTON_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					<div style={{
						display: "flex",
						alignItems: "center",
						gap: toRem(tokens.spacing.md),
						overflowX: "auto",
					}}>
						{hasIcons ? (
							isStartEnd ? (
								<Button
									text="Start+End"
									variant={variant}
									intent={selectedIntent as any}
									size={selectedSize as any}
									iconPosition="start"
									iconStart={arrowLeftIcon}
									iconEnd={arrowRightIcon}
									disabled={isDisabled}
									loading={isLoading}
								/>
							) : (
								<Button
									text="Кнопка"
									variant={variant}
									intent={selectedIntent as any}
									size={selectedSize as any}
									iconPosition={selectedPosition as any}
									iconStart={selectedPosition !== "end" ? searchIcon : undefined}
									iconEnd={selectedPosition === "end" ? chevronRightIcon : undefined}
									disabled={isDisabled}
									loading={isLoading}
								/>
							)
						) : (
							<Button
								text="Кнопка"
								variant={variant}
								intent={selectedIntent as any}
								size={selectedSize as any}
								disabled={isDisabled}
								loading={isLoading}
							/>
						)}
					</div>
				</div>
			))}
		</Section>
	);
}

