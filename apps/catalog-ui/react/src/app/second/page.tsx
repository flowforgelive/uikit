"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import {
	Button,
	IconButton,
	Text,
	SegmentedControl,
	Surface,
	useUIKitTheme,
	toRem,
} from "@uikit/react";
import { ThemeSwitcher } from "../components/theme-switcher/ThemeSwitcher";

const DIR_OPTIONS = [
	{ id: "ltr", label: "LTR" },
	{ id: "rtl", label: "RTL" },
];

/* ─── Local SVG icon helpers (catalog only) ────────────── */

const svgStyle: React.CSSProperties = {
	display: "block",
	fill: "none",
	stroke: "currentColor",
	strokeWidth: 2,
	strokeLinecap: "round",
	strokeLinejoin: "round",
};

const svgProps = {
	xmlns: "http://www.w3.org/2000/svg",
	viewBox: "0 0 24 24",
	width: "100%",
	height: "100%",
	style: svgStyle,
};

const searchIcon = (
	<svg {...svgProps}>
		<circle cx="11" cy="11" r="8" />
		<path d="m21 21-4.35-4.35" />
	</svg>
);
const starIcon = (
	<svg {...svgProps} style={{ ...svgStyle, fill: "currentColor", stroke: "none" }}>
		<path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
	</svg>
);
const checkIcon = (
	<svg {...svgProps}>
		<path d="M20 6L9 17l-5-5" />
	</svg>
);
const closeIcon = (
	<svg {...svgProps}>
		<path d="M18 6L6 18M6 6l12 12" />
	</svg>
);
const plusIcon = (
	<svg {...svgProps}>
		<path d="M12 5v14M5 12h14" />
	</svg>
);
const settingsIcon = (
	<svg {...svgProps}>
		<circle cx="12" cy="12" r="3" />
		<path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z" />
	</svg>
);
const arrowLeftIcon = (
	<svg {...svgProps}>
		<path d="M19 12H5M12 19l-7-7 7-7" />
	</svg>
);
const arrowRightIcon = (
	<svg {...svgProps}>
		<path d="M5 12h14M12 5l7 7-7 7" />
	</svg>
);
const chevronRightIcon = (
	<svg {...svgProps}>
		<path d="M9 18l6-6-6-6" />
	</svg>
);

const ICON_BUTTON_SAMPLES = [searchIcon, plusIcon, starIcon, settingsIcon, closeIcon, checkIcon];

/* ─── Section wrapper ──────────────────────────────────── */

function Section({
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
	return (
		<section id={id} style={{ width: "100%" }}>
			<div
				style={{
					display: "flex",
					alignItems: "center",
					gap: toRem(tokens.spacing.lg),
					marginBlockEnd: toRem(tokens.spacing.lg),
				}}
			>
				<h2
					style={{
						fontSize: toRem(tokens.typography.title3.fontSize),
						fontWeight: tokens.typography.title3.fontWeight,
						lineHeight: toRem(tokens.typography.title3.lineHeight),
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

/* ─── Size options for showcases ────────────────────────── */

const SIZE_OPTIONS = [
	{ id: "xs", label: "XS" },
	{ id: "sm", label: "SM" },
	{ id: "md", label: "MD" },
	{ id: "lg", label: "LG" },
	{ id: "xl", label: "XL" },
];

/* ─── Height Alignment Check ───────────────────────────── */

function HeightAlignmentShowcase({ tokens }: { tokens: any }) {
	const [selectedSize, setSelectedSize] = useState("md");

	return (
		<Section id="height-alignment" title="Height Alignment Check" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<span style={{ fontSize: toRem(tokens.typography.footnote.fontSize), fontWeight: 600, color: tokens.color.textMuted, whiteSpace: "nowrap" }}>
						Size:
					</span>
					<SegmentedControl
						options={SIZE_OPTIONS}
						selectedId={selectedSize}
						onSelectionChange={setSelectedSize}
					/>
				</div>
				{/* Rails container — dashed top/bottom borders to verify equal heights */}
				<div
					style={{
						display: "flex",
						alignItems: "center",
						gap: toRem(tokens.spacing.md),
						overflowX: "auto",
						borderBlockStart: `1px dashed ${tokens.color.outlineVariant}`,
						borderBlockEnd: `1px dashed ${tokens.color.outlineVariant}`,
					}}
				>
					<Button text="Solid" variant="solid" intent="primary" size={selectedSize as any} />
					<Button text="Outline" variant="outline" intent="neutral" size={selectedSize as any} />
					<Button text="Ghost" variant="ghost" intent="primary" size={selectedSize as any} />
					<Button
						text="With Icon"
						variant="solid"
						intent="primary"
						size={selectedSize as any}
						iconPosition="start"
						iconStart={searchIcon}
					/>
					<IconButton
						icon={starIcon}
						variant="solid"
						intent="primary"
						size={selectedSize as any}
					/>
					<div style={{ width: "fit-content", flexShrink: 0 }}>
						<SegmentedControl
							options={[
								{ id: "a", label: "On" },
								{ id: "b", label: "Off" },
							]}
							selectedId="a"
							onSelectionChange={() => {}}
							size={selectedSize as any}
						/>
					</div>
					<Text text="Text label" variant="body" />
				</div>
			</div>
		</Section>
	);
}

/* ─── Button showcase (merged: plain + with icons) ─────── */

const BUTTON_VARIANTS = ["solid", "soft", "outline", "ghost"] as const;
const BUTTON_INTENTS = ["primary", "neutral", "danger"] as const;

const ICON_POSITION_OPTIONS = [
	{ id: "none", label: "None" },
	{ id: "start", label: "Start" },
	{ id: "end", label: "End" },
	{ id: "top", label: "Top" },
	{ id: "bottom", label: "Bottom" },
	{ id: "startend", label: "Start+End" },
];

function ButtonShowcase({ tokens }: { tokens: any }) {
	const [selectedSize, setSelectedSize] = useState("md");
	const [selectedPosition, setSelectedPosition] = useState("none");
	const isStartEnd = selectedPosition === "startend";
	const hasIcons = selectedPosition !== "none";

	return (
		<Section id="buttons" title="Button" tokens={tokens}>
			<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), marginBlockEnd: toRem(tokens.spacing.md) }}>
				<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted }}>Icon position:</span>
				<SegmentedControl
					options={ICON_POSITION_OPTIONS}
					selectedId={selectedPosition}
					onSelectionChange={setSelectedPosition}
				/>
			</div>
			<div style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<SegmentedControl
					options={SIZE_OPTIONS}
					selectedId={selectedSize}
					onSelectionChange={setSelectedSize}
				/>
			</div>
			{BUTTON_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<h3 style={{ fontSize: toRem(tokens.typography.headline.fontSize), fontWeight: 600, color: tokens.color.textPrimary, marginBlockEnd: toRem(tokens.spacing.sm), textTransform: "capitalize" }}>
						{variant}
					</h3>
					{BUTTON_INTENTS.map((intent) => (
						<div key={intent} style={{ marginBlockEnd: toRem(tokens.spacing.md) }}>
							<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted, marginInlineEnd: toRem(tokens.spacing.sm), textTransform: "capitalize" }}>
								{intent}:
							</span>
							<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), overflowX: "auto" }}>
								{hasIcons ? (
									<>
										{isStartEnd ? (
											<Button
												text="Start+End"
												variant={variant}
												intent={intent}
												size={selectedSize as any}
												iconPosition="start"
												iconStart={arrowLeftIcon}
												iconEnd={arrowRightIcon}
											/>
										) : (
											<Button
												text="Кнопка"
												variant={variant}
												intent={intent}
												size={selectedSize as any}
												iconPosition={selectedPosition as any}
												iconStart={selectedPosition !== "end" ? searchIcon : undefined}
												iconEnd={selectedPosition === "end" ? chevronRightIcon : undefined}
											/>
										)}
										<Button
											text="Disabled"
											variant={variant}
											intent={intent}
											size={selectedSize as any}
											iconPosition={isStartEnd ? "start" : selectedPosition as any}
											iconStart={starIcon}
											disabled
										/>
										<Button
											text="Loading"
											variant={variant}
											intent={intent}
											size={selectedSize as any}
											iconPosition={selectedPosition as any}
											iconStart={checkIcon}
											loading
										/>
									</>
								) : (
									<>
										<Button text={`${variant} ${selectedSize}`} variant={variant} intent={intent} size={selectedSize as any} />
										<Button text="Disabled" variant={variant} intent={intent} size={selectedSize as any} disabled />
										<Button text="Loading" variant={variant} intent={intent} size={selectedSize as any} loading />
									</>
								)}
							</div>
						</div>
					))}
				</div>
			))}
		</Section>
	);
}

/* ─── Icon Button showcase ─────────────────────────────── */

function IconButtonShowcase({ tokens }: { tokens: any }) {
	const [selectedSize, setSelectedSize] = useState("md");

	return (
		<Section id="icon-button" title="Icon Button" tokens={tokens}>
			<div style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<SegmentedControl
					options={SIZE_OPTIONS}
					selectedId={selectedSize}
					onSelectionChange={setSelectedSize}
				/>
			</div>
			{BUTTON_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<h3 style={{ fontSize: toRem(tokens.typography.headline.fontSize), fontWeight: 600, color: tokens.color.textPrimary, marginBlockEnd: toRem(tokens.spacing.sm), textTransform: "capitalize" }}>
						{variant}
					</h3>
					{BUTTON_INTENTS.map((intent) => (
						<div key={intent} style={{ marginBlockEnd: toRem(tokens.spacing.md) }}>
							<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted, marginInlineEnd: toRem(tokens.spacing.sm), textTransform: "capitalize" }}>
								{intent}:
							</span>
							<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md), overflowX: "auto" }}>
								{ICON_BUTTON_SAMPLES.map((icon, i) => (
									<IconButton
										key={i}
										icon={icon}
										variant={variant}
										intent={intent}
										size={selectedSize as any}
									/>
								))}
								<IconButton
									icon={starIcon}
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									disabled
								/>
								<IconButton
									icon={starIcon}
									variant={variant}
									intent={intent}
									size={selectedSize as any}
									loading
								/>
							</div>
						</div>
					))}
				</div>
			))}
		</Section>
	);
}

/* ─── Surface variants showcase ────────────────────────── */

const SURFACE_VARIANTS = ["solid", "soft", "outline"] as const;
const SURFACE_LEVELS = [0, 1, 2, 3, 4, 5] as const;

const SURFACE_MODES = [
	{ id: "default", label: "Default" },
	{ id: "hoverable", label: "Hoverable" },
	{ id: "clickable", label: "Clickable" },
];

function SurfaceShowcase({ tokens }: { tokens: any }) {
	const [mode, setMode] = useState("default");

	return (
		<Section id="surface" title="Surface — Variant × Level" tokens={tokens}>
			<div style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
				<SegmentedControl
					options={SURFACE_MODES}
					selectedId={mode}
					onSelectionChange={setMode}
				/>
			</div>
			{SURFACE_VARIANTS.map((variant) => (
				<div key={variant} style={{ marginBlockEnd: toRem(tokens.spacing.xl) }}>
					<h3 style={{ fontSize: toRem(tokens.typography.headline.fontSize), fontWeight: 600, color: tokens.color.textPrimary, marginBlockEnd: toRem(tokens.spacing.sm), textTransform: "capitalize" }}>
						{variant}
					</h3>
					<div
						style={{
							display: "flex",
							gap: toRem(tokens.spacing.md),
							overflowX: "auto",
						}}
					>
						{SURFACE_LEVELS.map((level) => (
							<Surface
								key={level}
								variant={variant}
								level={level}
								hoverable={mode === "hoverable"}
								clickable={mode === "clickable"}
								onClick={mode === "clickable" ? () => {} : undefined}
							>
								<div style={{ padding: toRem(tokens.spacing.md), width: toRem(120) }}>
									<span
										style={{
											fontSize: toRem(tokens.typography.caption1.fontSize),
											fontWeight: 600,
											color: tokens.color.textPrimary,
											display: "block",
										}}
									>
										Level {level}
									</span>
									<span
										style={{
											fontSize: toRem(tokens.typography.caption2.fontSize),
											color: tokens.color.textMuted,
											marginBlockStart: toRem(tokens.spacing.xs),
											display: "block",
										}}
									>
										{variant}
									</span>
								</div>
							</Surface>
						))}
					</div>
				</div>
			))}
		</Section>
	);
}

/* ─── Text variants showcase ───────────────────────────── */

function TextShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="text" title="Text Variants" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
				<Text text="Heading 1 — Заголовок" variant="h1" />
				<Text text="Heading 2 — Подзаголовок" variant="h2" />
				<Text text="Heading 3 — Секция" variant="h3" />
				<Text text="Body — Основной текст для чтения. The quick brown fox jumps over the lazy dog. مرحبا بالعالم. 你好世界" variant="body" />
				<Text text="Caption — Подпись к элементам" variant="caption" />
			</div>
		</Section>
	);
}

/* ─── SegmentedControl showcase ────────────────────────── */

function SegmentedControlShowcase({ tokens }: { tokens: any }) {
	const [selected, setSelected] = useState("a");
	const [selectedSize, setSelectedSize] = useState("sm");

	return (
		<Section id="segmented-control" title="Segmented Control" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.lg) }}>
				{/* Size switcher */}
				<div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
					<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted }}>
						Size:
					</span>
					<SegmentedControl
						options={SIZE_OPTIONS}
						selectedId={selectedSize}
						onSelectionChange={setSelectedSize}
					/>
				</div>
				<div>
					<span style={{ fontSize: toRem(tokens.typography.footnote.fontSize), color: tokens.color.textMuted, display: "block", marginBlockEnd: toRem(tokens.spacing.xs) }}>
						3 options
					</span>
					<SegmentedControl
						options={[
							{ id: "a", label: "First" },
							{ id: "b", label: "Second" },
							{ id: "c", label: "Third" },
						]}
						selectedId={selected}
						onSelectionChange={setSelected}
						size={selectedSize as any}
					/>
				</div>
				<div>
					<span style={{ fontSize: toRem(tokens.typography.footnote.fontSize), color: tokens.color.textMuted, display: "block", marginBlockEnd: toRem(tokens.spacing.xs) }}>
						2 options
					</span>
					<SegmentedControl
						options={[
							{ id: "on", label: "Вкл" },
							{ id: "off", label: "Выкл" },
						]}
						selectedId="on"
						onSelectionChange={() => {}}
						size={selectedSize as any}
					/>
				</div>
			</div>
		</Section>
	);
}

/* ─── Main Page ────────────────────────────────────────── */

export default function ComponentsPage() {
	const router = useRouter();
	const { tokens, dir, setDir } = useUIKitTheme();

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
					justifyContent: "space-between",
					padding: `${toRem(tokens.spacing.md)} ${toRem(tokens.spacing.xl)}`,
					backgroundColor: tokens.color.surface,
				}}
			>
				<Button text="← Назад" variant="ghost" onClick={() => router.push("/")} />
				<div style={{ display: "flex", gap: toRem(tokens.spacing.sm), alignItems: "center" }}>
					<div style={{ width: "7.5rem" }}>
						<SegmentedControl
							options={DIR_OPTIONS}
							selectedId={dir}
							onSelectionChange={(id) => setDir(id as "ltr" | "rtl")}
						/>
					</div>
					<div style={{ width: "15rem" }}>
						<ThemeSwitcher />
					</div>
				</div>
			</div>

			{/* Content */}
			<main
				style={{
					paddingBlockStart: toRem(tokens.spacing.xxl),
					paddingBlockEnd: toRem(tokens.spacing.xxxxl),
					paddingInline: toRem(tokens.spacing.xl),
				}}
			>
				{/* Title */}
				<div
					style={{
						paddingBlock: toRem(tokens.spacing.lg),
						textAlign: "center",
					}}
				>
					<Text text="Components" variant="h1" />
					<p
						style={{
							fontSize: toRem(tokens.typography.body.fontSize),
							color: tokens.color.textSecondary,
							marginBlockStart: toRem(tokens.spacing.sm),
						}}
					>
						Кнопки, иконки, поверхности, текст, контролы
					</p>
				</div>

				{/* Sections */}
				<div
					style={{
						maxWidth: "960px",
						marginInline: "auto",
						display: "flex",
						flexDirection: "column",
						gap: toRem(tokens.spacing.xxxl),
					}}
				>
					<ButtonShowcase tokens={tokens} />
					<IconButtonShowcase tokens={tokens} />
					<SurfaceShowcase tokens={tokens} />
					<TextShowcase tokens={tokens} />
					<SegmentedControlShowcase tokens={tokens} />
					<HeightAlignmentShowcase tokens={tokens} />
				</div>
			</main>
		</div>
	);
}
