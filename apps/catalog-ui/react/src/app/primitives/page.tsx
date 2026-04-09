"use client";

import React, { useState } from "react";
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
import { SIZE_OPTIONS, RADIUS_OPTIONS, RADIUS_FRACTION_MAP, MAX_CONTAINER_RADIUS_MAP, SCALE_FACTOR_MAP } from "../components/catalog/CatalogConstants";
import { TextShowcase } from "./showcases/TextShowcase";

const IconShowcase = dynamic(() => import("./showcases/IconShowcase").then(m => ({ default: m.IconShowcase })), { ssr: false });
const DividerShowcase = dynamic(() => import("./showcases/DividerShowcase").then(m => ({ default: m.DividerShowcase })), { ssr: false });
const ImageShowcase = dynamic(() => import("./showcases/ImageShowcase").then(m => ({ default: m.ImageShowcase })), { ssr: false });
const SkeletonShowcase = dynamic(() => import("./showcases/SkeletonShowcase").then(m => ({ default: m.SkeletonShowcase })), { ssr: false });
const SpacerShowcase = dynamic(() => import("./showcases/SpacerShowcase").then(m => ({ default: m.SpacerShowcase })), { ssr: false });
const BadgeShowcase = dynamic(() => import("./showcases/BadgeShowcase").then(m => ({ default: m.BadgeShowcase })), { ssr: false });
const SurfaceShowcase = dynamic(() => import("./showcases/SurfaceShowcase").then(m => ({ default: m.SurfaceShowcase })), { ssr: false });

export default function PrimitivesPage() {
	const { tokens: baseTokens } = useUIKitTheme();
	const [globalSize, setGlobalSize] = useState("md");
	const [globalRadius, setGlobalRadius] = useState("md");

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
			title="Примитивы (Primitives)"
			subtitle="Text, Icon, Divider, Image, Skeleton, Spacer, Badge, Surface"
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
				</>
			}
		>
			<DesignTokensProvider tokens={tokens}>
				<TextShowcase tokens={tokens} />
				<IconShowcase tokens={tokens} globalSize={globalSize} />
				<DividerShowcase tokens={tokens} />
				<ImageShowcase tokens={tokens} globalSize={globalSize} />
				<SkeletonShowcase tokens={tokens} globalSize={globalSize} />
				<SpacerShowcase tokens={tokens} />
				<BadgeShowcase tokens={tokens} />
				<SurfaceShowcase tokens={tokens} />
			</DesignTokensProvider>
		</CatalogPage>
	);
}
