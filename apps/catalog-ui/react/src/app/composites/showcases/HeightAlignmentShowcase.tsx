import React from "react";
import { Button, Chip, Text, SegmentedControl, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { searchIcon, starIcon } from "../../components/icons";

export function HeightAlignmentShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	return (
		<Section id="height-alignment" title="Выравнивание высот (Height Alignment)" tokens={tokens}>
			<div style={{ marginBlockEnd: toRem(tokens.spacing.md) }}>
				<Text text="Интерактивные элементы одного размера выровнены по высоте внутри своей группы" variant="body-small" emphasis="muted" />
			</div>
			<div
				style={{
					display: "flex",
					alignItems: "center",
					gap: toRem(tokens.spacing.md),
					flexWrap: "wrap",
					borderTop: `1px dashed ${tokens.color.outlineVariant}`,
					borderBottom: `1px dashed ${tokens.color.outlineVariant}`,
					paddingBlock: toRem(tokens.spacing.xs),
				}}
			>
				<Button text="Button" size={globalSize as any} icon={searchIcon} />
				<Button icon={starIcon} size={globalSize as any} ariaLabel="Star" />
				<SegmentedControl
					options={[
						{ id: "a", label: "Abc" },
						{ id: "b", label: "Def" },
					]}
					selectedId="a"
					onSelectionChange={() => {}}
					size={globalSize as any}
				/>
				<Chip text="Chip" size={globalSize as any} />
				<Chip text="С иконкой" size={globalSize as any} leadingIcon={searchIcon} />
				<Chip text="Dismiss" size={globalSize as any} dismissible onDismiss={() => {}} />
			</div>
		</Section>
	);
}

