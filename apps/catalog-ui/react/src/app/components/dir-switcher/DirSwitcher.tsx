"use client";

import React from "react";
import { SegmentedControl, useUIKitTheme } from "@uikit/react";
import { DIR_OPTIONS } from "../catalog/CatalogConstants";

export function DirSwitcher({ className }: { className?: string }) {
	const { dir, setDir } = useUIKitTheme();

	return (
		<SegmentedControl
			options={DIR_OPTIONS}
			selectedId={dir}
			onSelectionChange={(id) => setDir(id as "ltr" | "rtl")}
			className={className}
		/>
	);
}
