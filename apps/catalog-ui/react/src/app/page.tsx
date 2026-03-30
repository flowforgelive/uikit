"use client";

import { useRouter } from "next/navigation";
import { Button, Text, useUIKitTheme } from "@uikit/react";
import { ThemeSwitcher } from "./components/theme-switcher/ThemeSwitcher";

export default function FirstPage() {
	const router = useRouter();
	const { tokens } = useUIKitTheme();

	return (
		<main
			style={{
				position: "relative",
				display: "flex",
				flexDirection: "column",
				alignItems: "center",
				justifyContent: "center",
				minHeight: "100vh",
				gap: "24px",
				backgroundColor: tokens.color.surface,
				color: tokens.color.textPrimary,
				transition: `background-color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}, color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}`,
			}}
		>
			<div style={{ position: "absolute", top: "16px", right: "16px" }}>
				<ThemeSwitcher />
			</div>
			<Text text="Первая страница" variant="h1" />
			<Button
				text="Перейти на вторую страницу"
				onClick={() => router.push("/second")}
			/>
		</main>
	);
}
