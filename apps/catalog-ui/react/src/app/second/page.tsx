"use client";

import { useRouter } from "next/navigation";
import { Button, Text, useUIKitTheme } from "@uikit/react";

export default function SecondPage() {
	const router = useRouter();
	const { tokens } = useUIKitTheme();

	return (
		<main
			style={{
				display: "flex",
				flexDirection: "column",
				alignItems: "center",
				justifyContent: "center",
				minHeight: "100vh",
				gap: "24px",
				backgroundColor: tokens.color.surface,
				color: tokens.color.textPrimary,
				transition: "background-color 0.2s ease, color 0.2s ease",
			}}
		>
			<Text text="Вторая страница" variant="h1" />
			<Button
				text="Вернуться назад"
				variant="secondary"
				onClick={() => router.back()}
			/>
		</main>
	);
}
