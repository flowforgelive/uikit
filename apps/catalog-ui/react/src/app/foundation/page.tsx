"use client";

import { useUIKitTheme, Text, toRem } from "@uikit/react";
import { ThemeSwitcher } from "../components/theme-switcher/ThemeSwitcher";
import { DirSwitcher } from "../components/dir-switcher/DirSwitcher";
import { CatalogPage } from "../components/catalog/CatalogPage";
import { TypographyShowcase } from "./showcases/TypographyShowcase";
import { ColorsShowcase } from "./showcases/ColorsShowcase";
import { SpacingShowcase } from "./showcases/SpacingShowcase";
import { SizingShowcase } from "./showcases/SizingShowcase";
import { RadiusShowcase } from "./showcases/RadiusShowcase";
import { MotionShowcase } from "./showcases/MotionShowcase";
import { BreakpointsShowcase } from "./showcases/BreakpointsShowcase";

export default function FoundationPage() {
	const { tokens } = useUIKitTheme();

	return (
		<CatalogPage
			title="Foundation Tokens"
			subtitle="Типография, цвета, отступы, размеры, радиусы, анимации, брейкпоинты"
			panelContent={
				<>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Направление" variant="label-small" />
						<DirSwitcher />
					</div>
					<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
						<Text text="Тема" variant="label-small" />
						<ThemeSwitcher />
					</div>
				</>
			}
		>
			<TypographyShowcase tokens={tokens} />
			<ColorsShowcase tokens={tokens} />
			<SpacingShowcase tokens={tokens} />
			<SizingShowcase tokens={tokens} />
			<RadiusShowcase tokens={tokens} />
			<MotionShowcase tokens={tokens} />
			<BreakpointsShowcase tokens={tokens} />
		</CatalogPage>
	);
}
