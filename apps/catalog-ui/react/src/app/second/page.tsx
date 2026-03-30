"use client";

import { useRouter } from "next/navigation";
import { Button, Text } from "@uikit/react";

export default function SecondPage() {
	const router = useRouter();

	return (
		<main
			style={{
				display: "flex",
				flexDirection: "column",
				alignItems: "center",
				justifyContent: "center",
				minHeight: "100vh",
				gap: "24px",
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
