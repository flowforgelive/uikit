"use client";

import React from "react";
import { SegmentedControl, useUIKitTheme } from "@uikit/react";

const THEME_OPTIONS = [
	{ id: "dark", label: "Тёмная" },
	{ id: "light", label: "Светлая" },
	{ id: "system", label: "Система" },
];

export function ThemeSwitcher({ className }: { className?: string }) {
	const { mode, setMode } = useUIKitTheme();

	return (
		<SegmentedControl
			options={THEME_OPTIONS}
			selectedId={mode}
			onSelectionChange={(id) => setMode(id as "light" | "dark" | "system")}
			className={className}
		/>
	);
}
