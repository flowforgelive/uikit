"use client";

import React from "react";
import { SegmentedControl, useUIKitTheme } from "@uikit/react";
import { CatalogOptions } from "catalog-shared";

const THEME_OPTIONS = CatalogOptions.getInstance().themeOptions.map((o: any) => ({ id: o.id, label: o.label }));

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
