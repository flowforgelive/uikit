import React from "react";
import { Button, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { starIcon, ICON_BUTTON_SAMPLES } from "../../components/icons";

const BUTTON_VARIANTS = ["solid", "soft", "surface", "outline", "ghost"] as const;
const BUTTON_INTENTS = ["primary", "neutral", "danger"] as const;

export function IconButtonShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const selectedSize = globalSize;

	return (
		<Section id="icon-only-button" title="Кнопка-иконка (Button icon-only)" tokens={tokens}>
			{BUTTON_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<SubSectionTitle tokens={tokens}>{variant}</SubSectionTitle>
					{BUTTON_INTENTS.map((intent) => (
						<div key={intent} style={{ marginBlockEnd: toRem(tokens.spacing.md) }}>
							<div style={{ marginInlineEnd: toRem(tokens.spacing.sm), marginBlockEnd: toRem(tokens.spacing.xs) }}>
								<Text text={`${intent.charAt(0).toUpperCase() + intent.slice(1)}:`} variant="label-medium" />
							</div>
							<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), overflowX: "auto" }}>
								{ICON_BUTTON_SAMPLES.map((icon, i) => (
									<Button
										key={i}
										icon={icon}
										variant={variant}
										intent={intent}
										size={selectedSize as any}
										ariaLabel="Icon button"
									/>
								))}
								<Button
									icon={starIcon}
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									ariaLabel="Disabled"
									disabled
								/>
								<Button
									icon={starIcon}
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									ariaLabel="Loading"
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
