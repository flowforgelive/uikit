"use client";

import React, { useState } from "react";
import { SegmentedControl, Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { searchIcon, starIcon, settingsIcon } from "../../components/icons";

export function SegmentedControlShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const [selected, setSelected] = useState("a");
	const selectedSize = globalSize;

	return (
		<Section id="segmented-control" title="Сегментированный переключатель (Segmented Control)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.lg) }}>
				<div>
					<div style={{ marginBlockEnd: toRem(tokens.spacing.xs) }}>
						<Text text="3 опции" variant="body-small" emphasis="muted" />
					</div>
					<SegmentedControl
						options={[
							{ id: "a", label: "First" },
							{ id: "b", label: "Second" },
							{ id: "c", label: "Third" },
						]}
						selectedId={selected}
						onSelectionChange={setSelected}
						size={selectedSize as any}
					/>
				</div>
				<div>
					<div style={{ marginBlockEnd: toRem(tokens.spacing.xs) }}>
						<Text text="2 опции" variant="body-small" emphasis="muted" />
					</div>
					<SegmentedControl
						options={[
							{ id: "on", label: "Вкл" },
							{ id: "off", label: "Выкл" },
						]}
						selectedId="on"
						onSelectionChange={() => {}}
						size={selectedSize as any}
					/>
				</div>
				{/* Variant showcase */}
				<div>
					<div style={{ marginBlockEnd: toRem(tokens.spacing.xs) }}>
						<Text text="Варианты (Variants)" variant="body-small" emphasis="muted" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
						{(["surface", "soft", "outline", "solid", "ghost"] as const).map((v) => (
							<div key={v} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
								<div style={{ minWidth: "4rem" }}>
									<Text text={v} variant="label-medium" />
								</div>
								<div style={{ flex: 1 }}>
									<SegmentedControl
										options={[
											{ id: "a", label: "First" },
											{ id: "b", label: "Second" },
											{ id: "c", label: "Third" },
										]}
										selectedId={selected}
										onSelectionChange={setSelected}
										variant={v}
										size={selectedSize as any}
									/>
								</div>
							</div>
						))}
					</div>
				</div>
				{/* Icon positions showcase */}
				<div>
					<div style={{ marginBlockEnd: toRem(tokens.spacing.xs) }}>
						<Text text="С иконками (Icons)" variant="body-small" emphasis="muted" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
						{(["start", "end", "top", "bottom"] as const).map((pos) => (
							<div key={pos} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
								<div style={{ minWidth: "4rem" }}>
									<Text text={pos} variant="label-medium" />
								</div>
								<div style={{ flex: 1 }}>
									<SegmentedControl
										options={[
											{ id: "search", label: "Search", icon: searchIcon },
											{ id: "star", label: "Star", icon: starIcon },
											{ id: "settings", label: "Settings", icon: settingsIcon },
										]}
										selectedId={selected}
										onSelectionChange={setSelected}
										iconPosition={pos}
										size={selectedSize as any}
									/>
								</div>
							</div>
						))}
					</div>
				</div>
			</div>
		</Section>
	);
}
