"use client";

import React, { useState } from "react";
import { Panel, Text, SegmentedControl, Surface, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

const VARIANT_OPTIONS = [
	{ id: "pinned", label: "Pinned" },
	{ id: "inset", label: "Inset" },
];

const SIDE_OPTIONS = [
	{ id: "left", label: "Left" },
	{ id: "right", label: "Right" },
	{ id: "top", label: "Top" },
	{ id: "bottom", label: "Bottom" },
];

const COLLAPSIBLE_OPTIONS = [
	{ id: "offcanvas", label: "Offcanvas" },
	{ id: "icon", label: "Icon" },
	{ id: "none", label: "None" },
];

const LEVEL_OPTIONS = [
	{ id: "0", label: "0" },
	{ id: "1", label: "1" },
	{ id: "2", label: "2" },
	{ id: "3", label: "3" },
];

export function PanelShowcase({ tokens }: { tokens: any }) {
	const [variant, setVariant] = useState("pinned");
	const [side, setSide] = useState("left");
	const [collapsible, setCollapsible] = useState("offcanvas");
	const [level, setLevel] = useState("1");
	const [isOpen, setIsOpen] = useState(true);
	const [elevated, setElevated] = useState(false);

	const isHorizontal = side === "top" || side === "bottom";

	return (
		<Section id="panel" title="Панель (Panel)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md), marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Вариант:" variant="label-medium" />
					<SegmentedControl options={VARIANT_OPTIONS} selectedId={variant} onSelectionChange={setVariant} />
				</div>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Сторона:" variant="label-medium" />
					<SegmentedControl options={SIDE_OPTIONS} selectedId={side} onSelectionChange={setSide} />
				</div>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Сворачивание:" variant="label-medium" />
					<SegmentedControl options={COLLAPSIBLE_OPTIONS} selectedId={collapsible} onSelectionChange={setCollapsible} />
				</div>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<Text text="Surface level:" variant="label-medium" />
					<SegmentedControl options={LEVEL_OPTIONS} selectedId={level} onSelectionChange={setLevel} />
				</div>
			</div>

			<div style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<SubSectionTitle tokens={tokens}>Интерактивный пример</SubSectionTitle>
				<div
					style={{
						display: "flex",
						flexDirection: isHorizontal ? "column" : "row",
						border: `1px dashed ${tokens.color.outlineVariant}`,
						borderRadius: toRem(tokens.radius.md),
						overflow: "hidden",
						height: isHorizontal ? toRem(400) : toRem(300),
					}}
				>
					{(side === "left" || side === "top") && (
						<Panel
							variant={variant as any}
							side={side as any}
							collapsible={collapsible as any}
							isOpen={isOpen}
							onToggle={() => setIsOpen(o => !o)}
							width={220}
							height={160}
							surfaceLevel={Number(level) as any}
							elevated={elevated}
						>
							<div style={{ padding: toRem(tokens.spacing.md) }}>
								<Text text="Содержимое панели" variant="body-medium" />
								<div style={{ marginBlockStart: toRem(tokens.spacing.sm) }}>
									<Text text={`${variant} / ${side} / level ${level}`} variant="label-small" emphasis="muted" />
								</div>
							</div>
						</Panel>
					)}
					<div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center" }}>
						<Text text="Основной контент" variant="body-large" emphasis="secondary" />
					</div>
					{(side === "right" || side === "bottom") && (
						<Panel
							variant={variant as any}
							side={side as any}
							collapsible={collapsible as any}
							isOpen={isOpen}
							onToggle={() => setIsOpen(o => !o)}
							width={220}
							height={160}
							surfaceLevel={Number(level) as any}
							elevated={elevated}
						>
							<div style={{ padding: toRem(tokens.spacing.md) }}>
								<Text text="Содержимое панели" variant="body-medium" />
								<div style={{ marginBlockStart: toRem(tokens.spacing.sm) }}>
									<Text text={`${variant} / ${side} / level ${level}`} variant="label-small" emphasis="muted" />
								</div>
							</div>
						</Panel>
					)}
				</div>
			</div>

			<div>
				<SubSectionTitle tokens={tokens}>Все варианты × стороны</SubSectionTitle>
				<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.lg) }}>
					{(["pinned", "inset"] as const).map((v) => (
						<div key={v}>
							<Text text={v} variant="label-medium" />
							<div style={{ display: "flex", gap: toRem(tokens.spacing.md), marginBlockStart: toRem(tokens.spacing.sm) }}>
								{(["left", "right"] as const).map((s) => (
									<div
										key={s}
										style={{
											display: "flex",
											border: `1px dashed ${tokens.color.outlineVariant}`,
											borderRadius: toRem(tokens.radius.sm),
											overflow: "hidden",
											width: toRem(280),
											height: toRem(120),
										}}
									>
										{s === "left" && (
											<Panel variant={v} side={s} collapsible="none" isOpen width={100} surfaceLevel={1}>
												<div style={{ padding: toRem(tokens.spacing.xs) }}>
													<Text text={s} variant="label-small" />
												</div>
											</Panel>
										)}
										<div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center" }}>
											<Text text="Content" variant="label-small" emphasis="muted" />
										</div>
										{s === "right" && (
											<Panel variant={v} side={s} collapsible="none" isOpen width={100} surfaceLevel={1}>
												<div style={{ padding: toRem(tokens.spacing.xs) }}>
													<Text text={s} variant="label-small" />
												</div>
											</Panel>
										)}
									</div>
								))}
							</div>
						</div>
					))}
				</div>
			</div>
		</Section>
	);
}
