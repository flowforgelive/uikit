"use client";

import { useRouter } from "next/navigation";
import { Button, Text, SegmentedControl, useUIKitTheme } from "@uikit/react";
import { ThemeSwitcher } from "./components/theme-switcher/ThemeSwitcher";

const DIR_OPTIONS = [
	{ id: "ltr", label: "LTR" },
	{ id: "rtl", label: "RTL" },
];

export default function FirstPage() {
	const router = useRouter();
	const { tokens, dir, setDir } = useUIKitTheme();

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
			<div
				style={{
					position: "absolute",
					top: "16px",
					insetInlineEnd: "16px",
					display: "flex",
					gap: "8px",
					alignItems: "center",
				}}
			>
				<div style={{ width: "7.5rem" }}>
					<SegmentedControl
						options={DIR_OPTIONS}
						selectedId={dir}
						onSelectionChange={(id) => setDir(id as "ltr" | "rtl")}
					/>
				</div>
				<div style={{ width: "15rem" }}>
					<ThemeSwitcher />
				</div>
			</div>
			<Text text="UIKit Catalog" variant="h1" />
			<Button
				text="Foundation Tokens"
				onClick={() => router.push("/foundation")}
			/>
			<Button
				text="Components"
				variant="soft"
				onClick={() => router.push("/second")}
			/>
		</main>
	);
}
