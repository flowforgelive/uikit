import { UIKitThemeProvider, UIKitThemeScript } from "@uikit/react";
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { cookies } from "next/headers";
import "./globals.css";

const inter = Inter({
	subsets: ["latin", "cyrillic"],
	display: "swap",
});

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
			<body className={inter.className}>
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
