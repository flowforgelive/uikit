import React from "react";
import { Divider, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

export function DividerShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="divider" title="Разделитель (Divider)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xl) }}>

				{/* Horizontal default */}
				<div>
					<SubSectionTitle tokens={tokens}>Horizontal</SubSectionTitle>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
						<Text text="Элемент выше" variant="body-medium" />
						<Divider />
						<Text text="Элемент ниже" variant="body-medium" />
					</div>
				</div>

				{/* With insets */}
				<div>
					<SubSectionTitle tokens={tokens}>С отступами (inset)</SubSectionTitle>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
						<Text text="Элемент выше" variant="body-medium" />
						<Divider insetStart={tokens.spacing.xl} insetEnd={tokens.spacing.xl} />
						<Text text="Элемент ниже" variant="body-medium" />
					</div>
				</div>

				{/* Vertical */}
				<div>
					<SubSectionTitle tokens={tokens}>Vertical</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
						<Text text="Слева" variant="body-medium" />
						<Divider orientation="vertical" />
						<Text text="Справа" variant="body-medium" />
					</div>
				</div>

				{/* Custom thickness */}
				<div>
					<SubSectionTitle tokens={tokens}>Толщина 3dp</SubSectionTitle>
					<Divider thickness={3} />
				</div>
			</div>
		</Section>
	);
}
