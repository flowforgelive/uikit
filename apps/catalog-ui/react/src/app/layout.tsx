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
	const dirCookie = cookieStore.get("uikit-dir")?.value;
	const resolvedCookie = cookieStore.get("uikit-resolved-theme")?.value;
	const initialTheme = (
		themeCookie === "light" || themeCookie === "dark" ? themeCookie : "system"
	) as "light" | "dark" | "system";
	const initialDir = (dirCookie === "rtl" ? "rtl" : "ltr") as "ltr" | "rtl";
	const initialResolved = (
		resolvedCookie === "light" || resolvedCookie === "dark"
			? resolvedCookie
			: undefined
	) as "light" | "dark" | undefined;

	return (
		<html lang="ru" suppressHydrationWarning>
			<head>
				<UIKitThemeScript />
			</head>
			<body>
				<UIKitThemeProvider
					initialTheme={initialTheme}
					initialResolved={initialResolved}
					initialDir={initialDir}
				>
					{children}
				</UIKitThemeProvider>
			</body>
		</html>
	);
}
