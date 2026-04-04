import React from "react";
import { toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

export function SizingShowcase({ tokens }: { tokens: any }) {
	const btnSizes = [
		["controlXs", "XS", tokens.sizing.controlXs],
		["controlSm", "SM", tokens.sizing.controlSm],
		["controlMd", "MD", tokens.sizing.controlMd],
		["controlLg", "LG", tokens.sizing.controlLg],
		["controlXl", "XL", tokens.sizing.controlXl],
	] as const;

	const iconSizes = [
		["iconXs", "XS", tokens.sizing.iconXs],
		["iconSm", "SM", tokens.sizing.iconSm],
		["iconMd", "MD", tokens.sizing.iconMd],
		["iconLg", "LG", tokens.sizing.iconLg],
		["iconXl", "XL", tokens.sizing.iconXl],
	] as const;

	return (
		<Section id="sizing" title="Sizing" tokens={tokens}>
			<SubSectionTitle tokens={tokens}>Button Heights</SubSectionTitle>
			<div style={{ display: "flex", alignItems: "end", gap: toRem(tokens.spacing.md), flexWrap: "wrap", marginBlockEnd: toRem(tokens.spacing.xl) }}>
				{btnSizes.map(([key, label, val]) => (
					<div key={key} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: toRem(tokens.spacing.xs) }}>
						<div
							style={{
								width: toRem(val * 2),
								height: toRem(val),
								backgroundColor: tokens.color.primary,
								borderRadius: toRem(tokens.radius.md),
								display: "flex",
								alignItems: "center",
								justifyContent: "center",
								color: tokens.color.textOnPrimary,
								fontSize: toRem(tokens.typography.labelSmall.fontSize),
								fontWeight: 600,
							}}
						>
							{val}dp
						</div>
						<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted }}>{label}</span>
					</div>
				))}
			</div>

			<SubSectionTitle tokens={tokens}>Icon Sizes</SubSectionTitle>
			<div style={{ display: "flex", alignItems: "end", gap: toRem(tokens.spacing.lg), flexWrap: "wrap", marginBlockEnd: toRem(tokens.spacing.xl) }}>
				{iconSizes.map(([key, label, val]) => (
					<div key={key} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: toRem(tokens.spacing.xs) }}>
						<div
							style={{
								width: toRem(val),
								height: toRem(val),
								backgroundColor: tokens.color.primary,
								borderRadius: toRem(tokens.radius.sm),
							}}
						/>
						<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted }}>{label} ({val}dp)</span>
					</div>
				))}
			</div>

			<div
				style={{
					display: "flex",
					alignItems: "center",
					gap: toRem(tokens.spacing.md),
					padding: toRem(tokens.spacing.md),
					boxShadow: `inset 0 0 0 1px ${tokens.color.outlineVariant}`,
					borderRadius: toRem(tokens.radius.md),
				}}
			>
				<div
					style={{
						width: toRem(tokens.sizing.minTouchTarget),
						height: toRem(tokens.sizing.minTouchTarget),
						boxShadow: `inset 0 0 0 2px ${tokens.color.danger}`,
						borderRadius: toRem(tokens.radius.sm),
						display: "flex",
						alignItems: "center",
						justifyContent: "center",
						fontSize: toRem(tokens.typography.labelSmall.fontSize),
						color: tokens.color.danger,
						fontWeight: 600,
					}}
				>
					{tokens.sizing.minTouchTarget}dp
				</div>
				<span style={{ ...textStyle(tokens.typography.bodySmall, tokens), color: tokens.color.textSecondary }}>
					Min Touch Target (Apple HIG 44dp / Material 48dp)
				</span>
			</div>
		</Section>
	);
}
