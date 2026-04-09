"use client";

import { useMemo } from "react";
import { useRouter } from "next/navigation";
import { Button, Text, useUIKitTheme, toRem } from "@uikit/react";
import { CatalogLayoutResolver } from "catalog-shared";
import { ThemeSwitcher } from "./components/theme-switcher/ThemeSwitcher";
import { DirSwitcher } from "./components/dir-switcher/DirSwitcher";

export default function FirstPage() {
	const router = useRouter();
	const { tokens } = useUIKitTheme();
	const layout = useMemo(() => CatalogLayoutResolver.getInstance().resolve(tokens), [tokens]);

	return (
		<main
			style={{
				position: "relative",
				display: "flex",
				flexDirection: "column",
				alignItems: "center",
				justifyContent: "center",
				minHeight: "100vh",
				gap: toRem(layout.firstScreenGap),
				backgroundColor: tokens.color.surface,
				color: tokens.color.textPrimary,
				transition: `background-color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}, color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}`,
			}}
		>
			<div
				style={{
					position: "absolute",
					top: toRem(layout.firstScreenControlsPadding),
					insetInlineEnd: toRem(layout.firstScreenControlsPadding),
					display: "flex",
					gap: toRem(layout.firstScreenControlsGap),
					alignItems: "center",
				}}
			>
				<DirSwitcher />
				<ThemeSwitcher />
			</div>
			<Text text="UIKit Catalog" variant="headline-large" />
			<Button
				text="Foundation Tokens"
				onClick={() => router.push("/foundation")}
			/>
			<Button
				text="Primitives"
				variant="soft"
				onClick={() => router.push("/primitives")}
			/>
			<Button
				text="Composites"
				variant="outline"
				onClick={() => router.push("/composites")}
			/>
		</main>
	);
}
