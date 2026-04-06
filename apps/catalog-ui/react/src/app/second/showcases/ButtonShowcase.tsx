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

const BUTTON_VARIANTS = ["solid", "soft", "surface", "outline", "ghost"] as const;
const BUTTON_INTENTS = ["primary", "neutral", "danger"] as const;

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
	const [selectedPosition, setSelectedPosition] = useState("none");
	const isStartEnd = selectedPosition === "startend";
	const hasIcons = selectedPosition !== "none";

	return (
		<Section id="buttons" title="Кнопка (Button)" tokens={tokens}>
			<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<Text text="Позиция иконки:" variant="label-medium" />
				<SegmentedControl
					options={ICON_POSITION_OPTIONS}
					selectedId={selectedPosition}
					onSelectionChange={setSelectedPosition}
				/>
			</div>
			{BUTTON_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					{BUTTON_INTENTS.map((intent) => (
						<div key={intent} style={{ marginBlockEnd: toRem(tokens.spacing.md) }}>
							<div style={{ marginInlineEnd: toRem(tokens.spacing.sm), marginBlockEnd: toRem(tokens.spacing.xs) }}>
								<Text text={`${intent.charAt(0).toUpperCase() + intent.slice(1)}:`} variant="label-medium" />
							</div>
							<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), overflowX: "auto" }}>
								{hasIcons ? (
									<>
										{isStartEnd ? (
											<Button
												text="Start+End"
												variant={variant}
												intent={intent}
												size={selectedSize as any}
												iconPosition="start"
												iconStart={arrowLeftIcon}
												iconEnd={arrowRightIcon}
											/>
										) : (
											<Button
												text="Кнопка"
												variant={variant}
												intent={intent}
												size={selectedSize as any}
												iconPosition={selectedPosition as any}
												iconStart={selectedPosition !== "end" ? searchIcon : undefined}
												iconEnd={selectedPosition === "end" ? chevronRightIcon : undefined}
											/>
										)}
										<Button
											text="Disabled"
											variant={variant}
											intent={intent}
											size={selectedSize as any}
											iconPosition={isStartEnd ? "start" : selectedPosition as any}
											iconStart={starIcon}
											disabled
										/>
										<Button
											text="Loading"
											variant={variant}
											intent={intent}
											size={selectedSize as any}
											iconPosition={selectedPosition as any}
											iconStart={checkIcon}
											loading
										/>
									</>
								) : (
									<>
										<Button text={`${variant.charAt(0).toUpperCase() + variant.slice(1)} ${selectedSize.charAt(0).toUpperCase() + selectedSize.slice(1)}`} variant={variant} intent={intent} size={selectedSize as any} />
										<Button text="Disabled" variant={variant} intent={intent} size={selectedSize as any} disabled />
										<Button text="Loading" variant={variant} intent={intent} size={selectedSize as any} loading />
									</>
								)}
							</div>
						</div>
					))}
				</div>
			))}
		</Section>
	);
}
