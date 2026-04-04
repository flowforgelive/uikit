"use client";

import React, { useState } from "react";
import { SegmentedControl, toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { searchIcon, starIcon, settingsIcon } from "../../components/icons";

export function SegmentedControlShowcase({ tokens, globalSize }: { tokens: any; globalSize: string }) {
	const [selected, setSelected] = useState("a");
	const selectedSize = globalSize;

	return (
		<Section id="segmented-control" title="Сегментированный переключатель (Segmented Control)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.lg) }}>
				<div>
					<span style={{ ...textStyle(tokens.typography.bodySmall, tokens), color: tokens.color.textMuted, display: "block", marginBlockEnd: toRem(tokens.spacing.xs) }}>
						3 опции
					</span>
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
					<span style={{ ...textStyle(tokens.typography.bodySmall, tokens), color: tokens.color.textMuted, display: "block", marginBlockEnd: toRem(tokens.spacing.xs) }}>
						2 опции
					</span>
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
					<span style={{ ...textStyle(tokens.typography.bodySmall, tokens), color: tokens.color.textMuted, display: "block", marginBlockEnd: toRem(tokens.spacing.xs) }}>
						Варианты (Variants)
					</span>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
						{(["surface", "soft", "outline", "solid", "ghost"] as const).map((v) => (
							<div key={v} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
								<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, minWidth: "4rem" }}>
									{v}
								</span>
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
					<span style={{ ...textStyle(tokens.typography.bodySmall, tokens), color: tokens.color.textMuted, display: "block", marginBlockEnd: toRem(tokens.spacing.xs) }}>
						С иконками (Icons)
					</span>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
						{(["start", "end", "top", "bottom"] as const).map((pos) => (
							<div key={pos} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
								<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, minWidth: "4rem" }}>
									{pos}
								</span>
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
