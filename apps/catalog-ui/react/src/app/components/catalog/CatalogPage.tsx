"use client";

import React, { useMemo } from "react";
import { useRouter } from "next/navigation";
import { Button, Panel, Text, DesignTokensProvider, useUIKitTheme, toRem } from "@uikit/react";
import { CatalogLayoutResolver } from "catalog-shared";

interface CatalogPageProps {
	title: string;
	subtitle?: string;
	panelContent?: React.ReactNode;
	panelVariant?: "pinned" | "inset";
	panelSide?: "left" | "right" | "top" | "bottom";
	panelTokens?: any;
	children: React.ReactNode;
}

export function CatalogPage({ title, subtitle, panelContent, panelVariant = "inset", panelSide = "left", panelTokens, children }: CatalogPageProps) {
	const router = useRouter();
	const { tokens } = useUIKitTheme();
	const layout = useMemo(() => CatalogLayoutResolver.getInstance().resolve(tokens), [tokens]);
	const effectivePanelTokens = panelTokens || tokens;

	const isHorizontal = panelSide === "top" || panelSide === "bottom";
	const isBefore = panelSide === "left" || panelSide === "top";

	const panelBlock = panelContent ? (
		<DesignTokensProvider tokens={effectivePanelTokens}>
			<Panel variant={panelVariant} side={panelSide} collapsible="none" isOpen width={280} height={220} surfaceLevel={1}>
				<div style={{ padding: toRem(effectivePanelTokens.spacing.lg), display: "flex", flexDirection: "column", gap: toRem(effectivePanelTokens.spacing.xl) }}>
					{panelContent}
				</div>
			</Panel>
		</DesignTokensProvider>
	) : null;

	return (
		<div
			style={{
				display: "flex",
				flexDirection: isHorizontal ? "column" : "row",
				height: "100vh",
				backgroundColor: tokens.color.surface,
				color: tokens.color.textPrimary,
				transition: `background-color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}, color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}`,
			}}
		>
			{isBefore && panelBlock}

			{/* Main content (scrollable) */}
			<div style={{ flex: 1, overflow: "auto" }}>
				{/* Top bar (back button only) */}
				<div
					style={{
						display: "flex",
						alignItems: "center",
						padding: `${toRem(layout.topBarPaddingBlock)} ${toRem(layout.topBarPaddingInline)}`,
						backgroundColor: tokens.color.surface,
						gap: toRem(layout.topBarGap),
					}}
				>
					<Button text="← Назад" variant="ghost" onClick={() => router.push("/")} />
				</div>

				{/* Content */}
				<main
					style={{
						paddingBlockStart: toRem(layout.contentPaddingBlockStart),
						paddingBlockEnd: toRem(layout.contentPaddingBlockEnd),
						paddingInline: toRem(layout.contentPaddingInline),
					}}
				>
					{/* Title */}
					<div style={{ paddingBlock: toRem(layout.titlePaddingBlock), textAlign: "center" }}>
						<Text text={title} variant="headline-large" />
						{subtitle && (
							<div style={{ marginBlockStart: toRem(layout.titleSubtitleGap) }}>
								<Text text={subtitle} variant="body-large" emphasis="secondary" />
							</div>
						)}
					</div>

					{/* Sections */}
					<div
						style={{
							maxWidth: toRem(layout.contentMaxWidth),
							marginInline: "auto",
							display: "flex",
							flexDirection: "column",
							gap: toRem(layout.sectionsGap),
						}}
					>
						{children}
					</div>
				</main>
			</div>

			{!isBefore && panelBlock}
		</div>
	);
}
