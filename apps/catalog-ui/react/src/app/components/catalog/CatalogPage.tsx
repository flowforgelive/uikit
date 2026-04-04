"use client";

import React, { useMemo } from "react";
import { useRouter } from "next/navigation";
import { Button, Text, useUIKitTheme, toRem, textStyle } from "@uikit/react";
import { CatalogLayoutResolver } from "catalog-shared";

interface CatalogPageProps {
	title: string;
	subtitle?: string;
	topBarEnd?: React.ReactNode;
	children: React.ReactNode;
}

export function CatalogPage({ title, subtitle, topBarEnd, children }: CatalogPageProps) {
	const router = useRouter();
	const { tokens } = useUIKitTheme();
	const layout = useMemo(() => CatalogLayoutResolver.getInstance().resolve(tokens), [tokens]);

	return (
		<div
			style={{
				minHeight: "100vh",
				backgroundColor: tokens.color.surface,
				color: tokens.color.textPrimary,
				transition: `background-color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}, color ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}`,
			}}
		>
			{/* Top bar */}
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
				<div style={{ flex: 1 }} />
				{topBarEnd}
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
						<p
							style={{
								...textStyle(tokens.typography.bodyLarge, tokens),
								color: tokens.color.textSecondary,
								marginBlockStart: toRem(layout.titleSubtitleGap),
							}}
						>
							{subtitle}
						</p>
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
	);
}
