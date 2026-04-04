"use client";

import React, { useState } from "react";
import {
	SegmentedControl,
	useUIKitTheme,
	DesignTokensProvider,
	textStyle,
} from "@uikit/react";
import { ThemeSwitcher } from "../components/theme-switcher/ThemeSwitcher";
import { DirSwitcher } from "../components/dir-switcher/DirSwitcher";
import { CatalogPage } from "../components/catalog/CatalogPage";
import { SIZE_OPTIONS, RADIUS_OPTIONS, RADIUS_FRACTION_MAP } from "../components/catalog/CatalogConstants";
import { TextShowcase } from "./showcases/TextShowcase";
import { ButtonShowcase } from "./showcases/ButtonShowcase";
import { IconButtonShowcase } from "./showcases/IconButtonShowcase";
import { SurfaceShowcase } from "./showcases/SurfaceShowcase";
import { SegmentedControlShowcase } from "./showcases/SegmentedControlShowcase";
import { HeightAlignmentShowcase } from "./showcases/HeightAlignmentShowcase";

export default function ComponentsPage() {
	const { tokens: baseTokens } = useUIKitTheme();
	const [globalSize, setGlobalSize] = useState("md");
	const [globalRadius, setGlobalRadius] = useState("md");

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
			topBarEnd={
				<>
					<DirSwitcher />
					<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, whiteSpace: "nowrap" }}>Размер:</span>
					<SegmentedControl
						options={SIZE_OPTIONS}
						selectedId={globalSize}
						onSelectionChange={setGlobalSize}
					/>
					<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, whiteSpace: "nowrap" }}>Скругление:</span>
					<SegmentedControl
						options={RADIUS_OPTIONS}
						selectedId={globalRadius}
						onSelectionChange={setGlobalRadius}
					/>
					<ThemeSwitcher />
				</>
			}
		>
			<DesignTokensProvider tokens={tokens}>
				<TextShowcase tokens={tokens} />
				<ButtonShowcase tokens={tokens} globalSize={globalSize} />
				<IconButtonShowcase tokens={tokens} globalSize={globalSize} />
				<SurfaceShowcase tokens={tokens} />
				<SegmentedControlShowcase tokens={tokens} globalSize={globalSize} />
				<HeightAlignmentShowcase tokens={tokens} globalSize={globalSize} />
			</DesignTokensProvider>
		</CatalogPage>
	);
}
