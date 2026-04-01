"use client";

import React, { useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import {
	Button,
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

/* ─── Section wrapper (divider style — matches Compose ShowcaseSection) ── */

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

/* ─── Typography showcase ──────────────────────────────── */

const TYPOGRAPHY_STYLES = [
	["largeTitle", "Large Title"],
	["title1", "Title 1"],
	["title2", "Title 2"],
	["title3", "Title 3"],
	["headline", "Headline"],
	["body", "Body"],
	["callout", "Callout"],
	["subhead", "Subhead"],
	["footnote", "Footnote"],
	["caption1", "Caption 1"],
	["caption2", "Caption 2"],
] as const;

function TypographyShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="typography" title="Typography" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
				{TYPOGRAPHY_STYLES.map(([key, label]) => {
					const style = tokens.typography[key];
					return (
						<div key={key} style={{ display: "flex", alignItems: "baseline", gap: toRem(tokens.spacing.lg), flexWrap: "wrap" }}>
							<span
								style={{
									fontSize: toRem(style.fontSize),
									fontWeight: style.fontWeight,
									lineHeight: toRem(style.lineHeight),
									letterSpacing: toRem(style.letterSpacing),
									color: tokens.color.textPrimary,
								}}
							>
								{label}
							</span>
							<span
								style={{
									fontSize: toRem(tokens.typography.caption1.fontSize),
									color: tokens.color.textMuted,
								}}
							>
								{style.fontSize}dp / {style.fontWeight} / {style.lineHeight}dp
							</span>
						</div>
					);
				})}
			</div>
		</Section>
	);
}

/* ─── Colors showcase ──────────────────────────────────── */

const COLOR_KEYS = [
	["primary", "Primary"],
	["primaryHover", "Primary Hover"],
	["secondary", "Secondary"],
	["danger", "Danger"],
	["dangerHover", "Danger Hover"],
	["dangerSoft", "Danger Soft"],
	["dangerSoftHover", "Danger Soft Hover"],
	["background", "Background"],
	["surface", "Surface"],
	["surfaceContainerLowest", "Container Lowest"],
	["surfaceContainerLow", "Container Low"],
	["surfaceContainer", "Surface Container"],
	["surfaceContainerHigh", "Container High"],
	["surfaceContainerHighest", "Container Highest"],
	["surfaceHover", "Surface Hover"],
	["onSurface", "On Surface"],
	["outline", "Outline"],
	["outlineVariant", "Outline Variant"],
	["textPrimary", "Text Primary"],
	["textSecondary", "Text Secondary"],
	["textMuted", "Text Muted"],
	["textOnPrimary", "Text on Primary"],
	["textOnDanger", "Text on Danger"],
	["textDisabled", "Text Disabled"],
	["surfaceDisabled", "Surface Disabled"],
	["borderDisabled", "Border Disabled"],
	["border", "Border"],
] as const;

function ColorsShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="colors" title="Colors" tokens={tokens}>
			<div
				style={{
					display: "grid",
					gridTemplateColumns: "repeat(auto-fill, minmax(120px, 1fr))",
					gap: toRem(tokens.spacing.md),
				}}
			>
				{COLOR_KEYS.map(([key, label]) => {
					const hex = tokens.color[key];
					return (
						<div key={key} style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<div
								style={{
									width: "100%",
									aspectRatio: "1",
									backgroundColor: hex,
									borderRadius: toRem(tokens.radius.md),
								boxShadow: `inset 0 0 0 1px ${tokens.color.outlineVariant}`,
								}}
							/>
							<span style={{ fontSize: toRem(tokens.typography.caption2.fontSize), fontWeight: 600, color: tokens.color.textPrimary }}>
								{label}
							</span>
							<span style={{ fontSize: toRem(tokens.typography.caption2.fontSize), color: tokens.color.textMuted, fontFamily: "monospace" }}>
								{hex}
							</span>
						</div>
					);
				})}
			</div>
		</Section>
	);
}

/* ─── Spacing showcase ─────────────────────────────────── */

const SPACING_KEYS = [
	["xxs", "xxs"],
	["xs", "xs"],
	["sm", "sm"],
	["md", "md"],
	["lg", "lg"],
	["xl", "xl"],
	["xxl", "xxl"],
	["xxxl", "xxxl"],
	["xxxxl", "xxxxl"],
] as const;

function SpacingShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="spacing" title="Spacing" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
				{SPACING_KEYS.map(([key, label]) => {
					const val = tokens.spacing[key];
					return (
						<div key={key} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
							<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted, minWidth: "4rem", textAlign: "end" }}>
								{label} ({val}dp)
							</span>
							<div
								style={{
									width: toRem(val),
									height: toRem(tokens.spacing.lg),
									backgroundColor: tokens.color.primary,
									borderRadius: toRem(tokens.radius.xs),
									transition: `width ${tokens.motion.durationNormal}ms ${tokens.motion.easingStandard}`,
								}}
							/>
						</div>
					);
				})}
			</div>
		</Section>
	);
}

/* ─── Sizing showcase ──────────────────────────────────── */

function SizingShowcase({ tokens }: { tokens: any }) {
	const btnSizes = [
		["buttonXs", "XS", tokens.sizing.buttonXs],
		["buttonSm", "SM", tokens.sizing.buttonSm],
		["buttonMd", "MD", tokens.sizing.buttonMd],
		["buttonLg", "LG", tokens.sizing.buttonLg],
		["buttonXl", "XL", tokens.sizing.buttonXl],
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
			<h3 style={{ fontSize: toRem(tokens.typography.headline.fontSize), fontWeight: 600, color: tokens.color.textPrimary, marginBlockEnd: toRem(tokens.spacing.sm) }}>
				Button Heights
			</h3>
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
								fontSize: toRem(tokens.typography.caption2.fontSize),
								fontWeight: 600,
							}}
						>
							{val}dp
						</div>
						<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted }}>{label}</span>
					</div>
				))}
			</div>

			<h3 style={{ fontSize: toRem(tokens.typography.headline.fontSize), fontWeight: 600, color: tokens.color.textPrimary, marginBlockEnd: toRem(tokens.spacing.sm) }}>
				Icon Sizes
			</h3>
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
						<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted }}>{label} ({val}dp)</span>
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
						fontSize: toRem(tokens.typography.caption2.fontSize),
						color: tokens.color.danger,
						fontWeight: 600,
					}}
				>
					{tokens.sizing.minTouchTarget}dp
				</div>
				<span style={{ fontSize: toRem(tokens.typography.footnote.fontSize), color: tokens.color.textSecondary }}>
					Min Touch Target (Apple HIG 44dp / Material 48dp)
				</span>
			</div>
		</Section>
	);
}

/* ─── Radius showcase ──────────────────────────────────── */

const RADIUS_KEYS = [
	["xs", "xs"],
	["sm", "sm"],
	["md", "md"],
	["lg", "lg"],
	["xl", "xl"],
	["full", "full"],
] as const;

function RadiusShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="radius" title="Radius" tokens={tokens}>
			<div style={{ display: "flex", gap: toRem(tokens.spacing.lg), flexWrap: "wrap" }}>
				{RADIUS_KEYS.map(([key, label]) => {
					const val = tokens.radius[key];
					return (
						<div key={key} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: toRem(tokens.spacing.xs) }}>
							<div
								style={{
									width: toRem(56),
									height: toRem(56),
									backgroundColor: tokens.color.primary,
									borderRadius: toRem(val),
								}}
							/>
							<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted }}>
								{label} ({val}dp)
							</span>
						</div>
					);
				})}
			</div>
		</Section>
	);
}

/* ─── Motion showcase ──────────────────────────────────── */

const DURATION_KEYS = [
	["durationInstant", "Instant"],
	["durationFast", "Fast"],
	["durationNormal", "Normal"],
	["durationSlow", "Slow"],
	["durationSlower", "Slower"],
] as const;

function MotionShowcase({ tokens }: { tokens: any }) {
	const [animate, setAnimate] = useState(false);

	const toggle = useCallback(() => {
		setAnimate((a) => !a);
	}, []);

	return (
		<Section id="motion" title="Motion" tokens={tokens}>
			<Button text={animate ? "Reset" : "Play Animation"} variant="soft" onClick={toggle} />
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md), marginBlockStart: toRem(tokens.spacing.lg) }}>
				{DURATION_KEYS.map(([key, label]) => {
					const val = tokens.motion[key];
					return (
						<div key={key} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
							<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted, minWidth: "6rem", textAlign: "end" }}>
								{label} ({val}ms)
							</span>
							<div
								style={{
									width: toRem(40),
									height: toRem(40),
									backgroundColor: tokens.color.primary,
									borderRadius: toRem(tokens.radius.md),
									transform: animate ? "translateX(200px)" : "translateX(0)",
									transition: `transform ${val}ms ${tokens.motion.easingStandard}`,
								}}
							/>
						</div>
					);
				})}
			</div>
		</Section>
	);
}

/* ─── Button variants showcase ─────────────────────────── */

const BUTTON_VARIANTS = ["solid", "soft", "outline", "ghost"] as const;
const BUTTON_INTENTS = ["primary", "neutral", "danger"] as const;
const BUTTON_SIZES = ["sm", "md", "lg"] as const;

function ButtonShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="buttons" title="Button — Visual × Intent" tokens={tokens}>
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
								{BUTTON_SIZES.map((size) => (
									<Button key={size} text={`${variant} ${size}`} variant={variant} intent={intent} size={size} />
								))}
								<Button text="Disabled" variant={variant} intent={intent} disabled />
								<Button text="Loading" variant={variant} intent={intent} loading />
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
	return (
		<Section id="segmented-control" title="Segmented Control" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.lg) }}>
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
					/>
				</div>
			</div>
		</Section>
	);
}

/* ─── Breakpoints showcase ─────────────────────────────── */

function BreakpointsShowcase({ tokens }: { tokens: any }) {
	const bp = [
		["compact", tokens.breakpoints.compact],
		["medium", tokens.breakpoints.medium],
		["expanded", tokens.breakpoints.expanded],
		["large", tokens.breakpoints.large],
		["extraLarge", tokens.breakpoints.extraLarge],
	] as const;

	return (
		<Section id="breakpoints" title="Breakpoints (Material Design 3)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.sm) }}>
				{bp.map(([label, val]) => (
					<div key={label} style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.md) }}>
						<span style={{ fontSize: toRem(tokens.typography.caption1.fontSize), color: tokens.color.textMuted, minWidth: "6rem", textAlign: "end" }}>
							{label}
						</span>
						<span style={{ fontSize: toRem(tokens.typography.body.fontSize), fontWeight: 600, color: tokens.color.textPrimary, fontFamily: "monospace" }}>
							{val}dp
						</span>
					</div>
				))}
			</div>
		</Section>
	);
}

/* ─── Main Page ────────────────────────────────────────── */

export default function SecondPage() {
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
			<header
				style={{
					position: "sticky",
					top: 0,
					zIndex: 10,
					display: "flex",
					alignItems: "center",
					justifyContent: "space-between",
					padding: `${toRem(tokens.spacing.md)} ${toRem(tokens.spacing.xl)}`,
					backgroundColor: tokens.color.surface,
					boxShadow: `inset 0 -1px 0 0 ${tokens.color.outlineVariant}`,
				}}
			>
				<Button text="← Назад" variant="ghost" size="sm" onClick={() => router.back()} />
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
			</header>

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
						marginBlockEnd: toRem(tokens.spacing.xxl),
						maxWidth: "960px",
						marginInline: "auto",
					}}
				>
					<h1
						style={{
							fontSize: toRem(tokens.typography.largeTitle.fontSize),
							fontWeight: tokens.typography.largeTitle.fontWeight,
							lineHeight: toRem(tokens.typography.largeTitle.lineHeight),
							color: tokens.color.textPrimary,
						}}
					>
						Design System Showcase
					</h1>
					<p
						style={{
							fontSize: toRem(tokens.typography.body.fontSize),
							color: tokens.color.textSecondary,
							marginBlockStart: toRem(tokens.spacing.sm),
						}}
					>
						Все токены, размеры и компоненты UIKit
					</p>
				</div>

				{/* Sections */}
				<div
					style={{
						maxWidth: "960px",
						marginInline: "auto",
						display: "flex",
						flexDirection: "column",
						gap: toRem(tokens.spacing.xl),
					}}
				>
					<TypographyShowcase tokens={tokens} />
					<ColorsShowcase tokens={tokens} />
					<SpacingShowcase tokens={tokens} />
					<SizingShowcase tokens={tokens} />
					<RadiusShowcase tokens={tokens} />
					<MotionShowcase tokens={tokens} />
					<BreakpointsShowcase tokens={tokens} />
					<ButtonShowcase tokens={tokens} />
					<SurfaceShowcase tokens={tokens} />
					<TextShowcase tokens={tokens} />
					<SegmentedControlShowcase tokens={tokens} />
				</div>
			</main>
		</div>
	);
}
