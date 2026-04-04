import React from "react";
import { toRem, textStyle } from "@uikit/react";

export function SubSectionTitle({
	children,
	tokens,
}: {
	children: React.ReactNode;
	tokens: any;
}) {
	return (
		<h3
			style={{
				...textStyle(tokens.typography.titleLarge, tokens),
				color: tokens.color.textPrimary,
				marginBlockEnd: toRem(tokens.spacing.sm),
			}}
		>
			{typeof children === 'string' ? children.charAt(0).toUpperCase() + children.slice(1) : children}
		</h3>
	);
}
