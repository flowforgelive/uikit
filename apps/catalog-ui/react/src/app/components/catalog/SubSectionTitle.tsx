import React from "react";
import { Text, toRem } from "@uikit/react";

export function SubSectionTitle({
	children,
	tokens,
}: {
	children: React.ReactNode;
	tokens: any;
}) {
	const text = typeof children === 'string' ? children.charAt(0).toUpperCase() + children.slice(1) : String(children);
	return (
		<div style={{ marginBlockEnd: toRem(tokens.spacing.sm) }}>
			<Text text={text} variant="title-large" />
		</div>
	);
}
