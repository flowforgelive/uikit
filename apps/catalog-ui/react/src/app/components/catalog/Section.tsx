import React, { useMemo } from "react";
import { toRem, textStyle } from "@uikit/react";
import { CatalogLayoutResolver } from "catalog-shared";

export function Section({
	id,
	title,
	children,
	tokens,
}: {
	id: string;
	title: string;
	children: React.ReactNode;
	tokens: any;
}) {
	const layout = useMemo(() => CatalogLayoutResolver.getInstance().resolve(tokens), [tokens]);

	return (
		<section id={id} style={{ width: "100%" }}>
			<div
				style={{
					display: "flex",
					alignItems: "center",
					gap: toRem(layout.sectionTitleGap),
					marginBlockEnd: toRem(layout.sectionTitleMarginBottom),
				}}
			>
				<h2
					style={{
						...textStyle(tokens.typography.headlineMedium, tokens),
						color: tokens.color.textPrimary,
						whiteSpace: "nowrap",
					}}
				>
					{title}
				</h2>
				<div
					style={{
						flex: 1,
						height: "1px",
						background: tokens.color.outlineVariant,
					}}
				/>
			</div>
			{children}
		</section>
	);
}
