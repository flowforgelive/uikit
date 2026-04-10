import React from "react";
import { Icon, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { searchIcon, starIcon, checkIcon, closeIcon, plusIcon, settingsIcon } from "../../components/icons";

const ICON_SAMPLES = [searchIcon, plusIcon, starIcon, settingsIcon, closeIcon, checkIcon];

export function IconShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	return (
		<Section id="icon" title="Иконки (Icon)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xl) }}>

				{/* Size variant (follows global toggle) */}
				<div>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
						{ICON_SAMPLES.map((icon, i) => (
							<Icon key={i} size={globalSize as any}>{icon}</Icon>
						))}
					</div>
				</div>

				{/* Custom color */}
				<div>
					<SubSectionTitle tokens={tokens}>Кастомный цвет</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
						<Icon color={tokens.color.primary}>{starIcon}</Icon>
						<Icon color={tokens.color.danger}>{closeIcon}</Icon>
						<Icon color={tokens.color.textSecondary}>{settingsIcon}</Icon>
					</div>
				</div>
			</div>
		</Section>
	);
}

