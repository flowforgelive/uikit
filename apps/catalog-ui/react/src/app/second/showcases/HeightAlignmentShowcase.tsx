import React from "react";
import { Button, IconButton, SegmentedControl, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { searchIcon, starIcon } from "../../components/icons";

export function HeightAlignmentShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	return (
		<Section id="height-alignment" title="Выравнивание высот (Height Alignment)" tokens={tokens}>
			<p style={{ fontSize: toRem(tokens.typography.bodySmall.fontSize), color: tokens.color.textMuted, marginBlockEnd: toRem(tokens.spacing.md) }}>
				Все интерактивные элементы одного размера имеют одинаковую высоту
			</p>
			<div
				style={{
					display: "flex",
					alignItems: "center",
					gap: toRem(tokens.spacing.md),
					borderTop: "1px dashed silver",
					borderBottom: "1px dashed silver",
					paddingBlock: toRem(tokens.spacing.xs),
				}}
			>
				<Button text="Button" size={globalSize as any} iconPosition="start" iconStart={searchIcon} />
				<IconButton icon={starIcon} size={globalSize as any} />
				<SegmentedControl
					options={[
						{ id: "a", label: "Abc" },
						{ id: "b", label: "Def" },
					]}
					selectedId="a"
					onSelectionChange={() => {}}
					size={globalSize as any}
				/>
			</div>
		</Section>
	);
}
