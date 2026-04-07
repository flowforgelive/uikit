import React from "react";
import { Icon, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";
import { searchIcon, starIcon, checkIcon, closeIcon, plusIcon, settingsIcon } from "../../components/icons";

const SIZES = ["xs", "sm", "md", "lg", "xl"] as const;
const ICON_SAMPLES = [searchIcon, plusIcon, starIcon, settingsIcon, closeIcon, checkIcon];

export function IconShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="icon" title="Иконки (Icon)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xl) }}>

				{/* Size variants */}
				{SIZES.map((size) => (
					<div key={size}>
						<SubSectionTitle tokens={tokens}>{size.toUpperCase()}</SubSectionTitle>
						<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
							{ICON_SAMPLES.map((icon, i) => (
								<Icon key={i} size={size}>{icon}</Icon>
							))}
						</div>
					</div>
				))}

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
