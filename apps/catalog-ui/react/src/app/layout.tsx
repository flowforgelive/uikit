import { UIKitThemeProvider, UIKitThemeScript } from "@uikit/react";
import type { Metadata } from "next";
import { cookies } from "next/headers";
import "./globals.css";

export const metadata: Metadata = {
	title: "UIKit Catalog",
	description: "UIKit MVP — Compose Multiplatform + React",
};

export default async function RootLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	const cookieStore = await cookies();
	const themeCookie = cookieStore.get("uikit-theme")?.value;
	const initialTheme = (
		themeCookie === "light" || themeCookie === "dark" ? themeCookie : "system"
	) as "light" | "dark" | "system";

	return (
		<html lang="ru" suppressHydrationWarning>
			<head>
				<UIKitThemeScript />
			</head>
			<body>
				<UIKitThemeProvider initialTheme={initialTheme}>
					{children}
				</UIKitThemeProvider>
			</body>
		</html>
	);
}
