import React, { useMemo } from "react";
import { Divider, Text, toRem } from "@uikit/react";
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
				<Text text={title} variant="headline-medium" className="uikit-section-title" />
				<div style={{ flex: 1 }}>
					<Divider />
				</div>
			</div>
			{children}
		</section>
	);
}
