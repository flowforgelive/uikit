import React from "react";
import { Chip, toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { starIcon, searchIcon, checkIcon } from "../../components/icons";

const CHIP_VARIANTS = ["soft", "outline", "ghost"] as const;
const CHIP_INTENTS = ["primary", "neutral", "danger"] as const;

export function ChipShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const selectedSize = globalSize;

	return (
		<Section id="chip" title="Чип (Chip)" tokens={tokens}>
			{/* Basic chips by variant × intent */}
			{CHIP_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					{CHIP_INTENTS.map((intent) => (
						<div key={intent} style={{ marginBlockEnd: toRem(tokens.spacing.md) }}>
							<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, marginInlineEnd: toRem(tokens.spacing.sm) }}>
								{intent.charAt(0).toUpperCase() + intent.slice(1)}:
							</span>
							<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.sm), flexWrap: "wrap" }}>
								<Chip
									text="Chip"
									variant={variant}
									intent={intent}
									size={selectedSize as any}
								/>
								<Chip
									text="С иконкой"
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									leadingIcon={searchIcon}
								/>
								<Chip
									text="Dismissible"
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									dismissible
									onDismiss={() => {}}
								/>
								<Chip
									text="Icon + Dismiss"
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									leadingIcon={starIcon}
									dismissible
									onDismiss={() => {}}
								/>
								<Chip
									text="Selected"
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									selected
								/>
								<Chip
									text="Disabled"
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									disabled
								/>
								<Chip
									text="Loading"
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									loading
								/>
							</div>
						</div>
					))}
				</div>
			))}
		</Section>
	);
}
