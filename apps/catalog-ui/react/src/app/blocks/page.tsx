"use client";

import React, { useState } from "react";
import {
	SegmentedControl,
	Text,
	useUIKitTheme,
	DesignTokensProvider,
	toRem,
	scaleDesignTokens,
	withControlProportions,
} from "@uikit/react";
import { ThemeSwitcher } from "../components/theme-switcher/ThemeSwitcher";
import { DirSwitcher } from "../components/dir-switcher/DirSwitcher";
import { CatalogPage } from "../components/catalog/CatalogPage";
import { SIZE_OPTIONS, RADIUS_OPTIONS, RADIUS_FRACTION_MAP, MAX_CONTAINER_RADIUS_MAP, SCALE_FACTOR_MAP, PANEL_VARIANT_OPTIONS, PANEL_SIDE_OPTIONS } from "../components/catalog/CatalogConstants";
import { PanelShowcase } from "./showcases/PanelShowcase";

export default function BlocksPage() {
	const { tokens: baseTokens } = useUIKitTheme();
	const [globalSize, setGlobalSize] = useState("md");
	const [globalRadius, setGlobalRadius] = useState("md");
	const [panelVariant, setPanelVariant] = useState<"pinned" | "inset">("inset");
	const [panelSide, setPanelSide] = useState<"left" | "right" | "top" | "bottom">("left");

	const tokens: any = React.useMemo(() => {
		let t: any = baseTokens;
		const fraction = RADIUS_FRACTION_MAP[globalRadius];
		const maxCR = MAX_CONTAINER_RADIUS_MAP[globalRadius];
		if (fraction !== t.controls.proportions.radiusFraction ||
			maxCR !== t.controls.proportions.maxContainerRadius) {
			t = withControlProportions(t, fraction, maxCR);
		}
		const sf = SCALE_FACTOR_MAP[globalSize] ?? 1.0;
		if (sf !== 1.0) {
			t = scaleDesignTokens(t, sf);
		}
		return t;
	}, [baseTokens, globalRadius, globalSize]);

	return (
		<CatalogPage
			title="Блоки (Blocks)"
			subtitle="Panel и другие самодостаточные компоненты"
			panelVariant={panelVariant}
			panelSide={panelSide}
			panelTokens={tokens}
			panelContent={
				<>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Направление" variant="label-small" emphasis="secondary" />
						<DirSwitcher />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Размер" variant="label-small" emphasis="secondary" />
						<SegmentedControl options={SIZE_OPTIONS} selectedId={globalSize} onSelectionChange={setGlobalSize} size="sm" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Скругление" variant="label-small" emphasis="secondary" />
						<SegmentedControl options={RADIUS_OPTIONS} selectedId={globalRadius} onSelectionChange={setGlobalRadius} size="sm" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Тема" variant="label-small" emphasis="secondary" />
						<ThemeSwitcher />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Панель: вариант" variant="label-small" emphasis="secondary" />
						<SegmentedControl options={PANEL_VARIANT_OPTIONS} selectedId={panelVariant} onSelectionChange={(id) => setPanelVariant(id as any)} size="sm" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Панель: сторона" variant="label-small" emphasis="secondary" />
						<SegmentedControl options={PANEL_SIDE_OPTIONS} selectedId={panelSide} onSelectionChange={(id) => setPanelSide(id as any)} size="sm" />
					</div>
				</>
			}
		>
			<DesignTokensProvider tokens={tokens}>
				<PanelShowcase tokens={tokens} />
			</DesignTokensProvider>
		</CatalogPage>
	);
}
