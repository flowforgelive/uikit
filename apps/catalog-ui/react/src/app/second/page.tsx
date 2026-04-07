"use client";

import React, { useState, useRef, useEffect } from "react";
import dynamic from "next/dynamic";
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
import { TextShowcase } from "./showcases/TextShowcase";
import { ButtonShowcase } from "./showcases/ButtonShowcase";

const IconButtonShowcase = dynamic(() => import("./showcases/IconButtonShowcase").then(m => ({ default: m.IconButtonShowcase })), { ssr: false });
const ChipShowcase = dynamic(() => import("./showcases/ChipShowcase").then(m => ({ default: m.ChipShowcase })), { ssr: false });
const SurfaceShowcase = dynamic(() => import("./showcases/SurfaceShowcase").then(m => ({ default: m.SurfaceShowcase })), { ssr: false });
const SegmentedControlShowcase = dynamic(() => import("./showcases/SegmentedControlShowcase").then(m => ({ default: m.SegmentedControlShowcase })), { ssr: false });
const HeightAlignmentShowcase = dynamic(() => import("./showcases/HeightAlignmentShowcase").then(m => ({ default: m.HeightAlignmentShowcase })), { ssr: false });
const IconShowcase = dynamic(() => import("./showcases/IconShowcase").then(m => ({ default: m.IconShowcase })), { ssr: false });
const DividerShowcase = dynamic(() => import("./showcases/DividerShowcase").then(m => ({ default: m.DividerShowcase })), { ssr: false });
const ImageShowcase = dynamic(() => import("./showcases/ImageShowcase").then(m => ({ default: m.ImageShowcase })), { ssr: false });
const SkeletonShowcase = dynamic(() => import("./showcases/SkeletonShowcase").then(m => ({ default: m.SkeletonShowcase })), { ssr: false });

export default function ComponentsPage() {
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
			title="Компоненты (Components)"
			subtitle="Кнопки, иконки, поверхности, текст, контролы"
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
				<HeightAlignmentShowcase tokens={tokens} globalSize={globalSize} />
				{/* Primitives */}
				<TextShowcase tokens={tokens} />
				<IconShowcase tokens={tokens} globalSize={globalSize} />
				<DividerShowcase tokens={tokens} />
				<ImageShowcase tokens={tokens} globalSize={globalSize} />
				<SkeletonShowcase tokens={tokens} globalSize={globalSize} />
				<SurfaceShowcase tokens={tokens} />
				{/* Composites */}
				<ButtonShowcase tokens={tokens} globalSize={globalSize} />
				<IconButtonShowcase tokens={tokens} globalSize={globalSize} />
				<ChipShowcase tokens={tokens} globalSize={globalSize} />
				<SegmentedControlShowcase tokens={tokens} globalSize={globalSize} />
			</DesignTokensProvider>
		</CatalogPage>
	);
}
