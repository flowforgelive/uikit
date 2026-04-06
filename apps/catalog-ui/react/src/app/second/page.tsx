"use client";

import React, { useState, useRef, useEffect } from "react";
import dynamic from "next/dynamic";
import {
	SegmentedControl,
	useUIKitTheme,
	DesignTokensProvider,
	toRem,
	textStyle,
} from "@uikit/react";
import { ThemeSwitcher } from "../components/theme-switcher/ThemeSwitcher";
import { DirSwitcher } from "../components/dir-switcher/DirSwitcher";
import { CatalogPage } from "../components/catalog/CatalogPage";
import { SIZE_OPTIONS, RADIUS_OPTIONS, RADIUS_FRACTION_MAP, PANEL_VARIANT_OPTIONS, PANEL_SIDE_OPTIONS } from "../components/catalog/CatalogConstants";
import { TextShowcase } from "./showcases/TextShowcase";
import { ButtonShowcase } from "./showcases/ButtonShowcase";

const IconButtonShowcase = dynamic(() => import("./showcases/IconButtonShowcase").then(m => ({ default: m.IconButtonShowcase })), { ssr: false });
const ChipShowcase = dynamic(() => import("./showcases/ChipShowcase").then(m => ({ default: m.ChipShowcase })), { ssr: false });
const SurfaceShowcase = dynamic(() => import("./showcases/SurfaceShowcase").then(m => ({ default: m.SurfaceShowcase })), { ssr: false });
const SegmentedControlShowcase = dynamic(() => import("./showcases/SegmentedControlShowcase").then(m => ({ default: m.SegmentedControlShowcase })), { ssr: false });
const HeightAlignmentShowcase = dynamic(() => import("./showcases/HeightAlignmentShowcase").then(m => ({ default: m.HeightAlignmentShowcase })), { ssr: false });

export default function ComponentsPage() {
	const { tokens: baseTokens } = useUIKitTheme();
	const [globalSize, setGlobalSize] = useState("md");
	const [globalRadius, setGlobalRadius] = useState("md");
	const [panelVariant, setPanelVariant] = useState<"pinned" | "inset">("inset");
	const [panelSide, setPanelSide] = useState<"left" | "right" | "top" | "bottom">("left");

	const tokens: any = React.useMemo(() => {
		const fraction = RADIUS_FRACTION_MAP[globalRadius];
		if (fraction === baseTokens.controls.proportions.radiusFraction) return baseTokens;
		return {
			...baseTokens,
			controls: {
				...baseTokens.controls,
				proportions: {
					...baseTokens.controls.proportions,
					radiusFraction: fraction,
				},
			},
		};
	}, [baseTokens, globalRadius]);

	return (
		<CatalogPage
			title="Компоненты (Components)"
			subtitle="Кнопки, иконки, поверхности, текст, контролы"
			panelVariant={panelVariant}
			panelSide={panelSide}
			panelTokens={tokens}
			panelContent={
				<>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), color: tokens.color.textSecondary }}>Направление</span>
						<DirSwitcher />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), color: tokens.color.textSecondary }}>Размер</span>
						<SegmentedControl options={SIZE_OPTIONS} selectedId={globalSize} onSelectionChange={setGlobalSize} size="sm" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), color: tokens.color.textSecondary }}>Скругление</span>
						<SegmentedControl options={RADIUS_OPTIONS} selectedId={globalRadius} onSelectionChange={setGlobalRadius} size="sm" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), color: tokens.color.textSecondary }}>Тема</span>
						<ThemeSwitcher />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), color: tokens.color.textSecondary }}>Панель: вариант</span>
						<SegmentedControl options={PANEL_VARIANT_OPTIONS} selectedId={panelVariant} onSelectionChange={(id) => setPanelVariant(id as any)} size="sm" />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<span style={{ ...textStyle(tokens.typography.labelSmall, tokens), color: tokens.color.textSecondary }}>Панель: сторона</span>
						<SegmentedControl options={PANEL_SIDE_OPTIONS} selectedId={panelSide} onSelectionChange={(id) => setPanelSide(id as any)} size="sm" />
					</div>
				</>
			}
		>
			<DesignTokensProvider tokens={tokens}>
				<TextShowcase tokens={tokens} />
				<ButtonShowcase tokens={tokens} globalSize={globalSize} />
				<IconButtonShowcase tokens={tokens} globalSize={globalSize} />
				<ChipShowcase tokens={tokens} globalSize={globalSize} />
				<SurfaceShowcase tokens={tokens} />
				<SegmentedControlShowcase tokens={tokens} globalSize={globalSize} />
				<HeightAlignmentShowcase tokens={tokens} globalSize={globalSize} />
			</DesignTokensProvider>
		</CatalogPage>
	);
}
