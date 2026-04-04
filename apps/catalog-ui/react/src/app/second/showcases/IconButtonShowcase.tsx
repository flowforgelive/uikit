import React from "react";
import { IconButton, toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { starIcon, ICON_BUTTON_SAMPLES } from "../../components/icons";

const BUTTON_VARIANTS = ["solid", "soft", "surface", "outline", "ghost"] as const;
const BUTTON_INTENTS = ["primary", "neutral", "danger"] as const;

export function IconButtonShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const selectedSize = globalSize;

	return (
		<Section id="icon-button" title="Кнопка-иконка (Icon Button)" tokens={tokens}>
			{BUTTON_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					{BUTTON_INTENTS.map((intent) => (
						<div key={intent} style={{ marginBlockEnd: toRem(tokens.spacing.md) }}>
							<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, marginInlineEnd: toRem(tokens.spacing.sm) }}>
								{intent.charAt(0).toUpperCase() + intent.slice(1)}:
							</span>
							<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), overflowX: "auto" }}>
								{ICON_BUTTON_SAMPLES.map((icon, i) => (
									<IconButton
										key={i}
										icon={icon}
										variant={variant}
										intent={intent}
										size={selectedSize as any}
									/>
								))}
								<IconButton
									icon={starIcon}
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									disabled
								/>
								<IconButton
									icon={starIcon}
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
