import React, { useState } from "react";
import { Image, Text, Button, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";
import { SubSectionTitle } from "../../components/catalog/SubSectionTitle";

// picsum.photos — random photos from Unsplash, /id/{N}/{W}/{H}.jpg for stable results
const PHOTO_1 = "https://picsum.photos/id/10/400/300.jpg";   // forest
const PHOTO_2 = "https://picsum.photos/id/1015/400/300.jpg"; // river
const PHOTO_SQ = "https://picsum.photos/id/237/200/200.jpg"; // puppy

// Large images for visible loading demo
const PHOTO_LARGE_1 = "https://picsum.photos/id/29/1600/1200.jpg";
const PHOTO_LARGE_2 = "https://picsum.photos/id/47/1600/1200.jpg";
const PHOTO_LARGE_3 = "https://picsum.photos/id/76/1600/1200.jpg";

const FITS = ["cover", "contain", "fill", "none", "scale-down"] as const;

export function ImageShowcase({ tokens }: { tokens: any }) {
	const [reloadKey, setReloadKey] = useState(0);

	return (
		<Section id="image" title="Изображение (Image)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xl) }}>

				<Text
					text="Image — <img> с размерами, скруглением, object-fit, subtle border, loading skeleton и fallback."
					variant="body-small"
					emphasis="muted"
				/>

				{/* Adaptive corner radius (follows global radiusFraction, capped by maxContainerRadius) */}
				<div>
					<SubSectionTitle tokens={tokens}>Адаптивное скругление (глобальное с cap)</SubSectionTitle>
					<Text
						text="Без явного cornerRadius → min(min(w,h) × radiusFraction, maxContainerRadius). Переключите слайдер скругления."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", alignItems: "flex-start", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						<Image src={PHOTO_SQ} alt="Square adaptive" width={80} height={80} showBorder />
						<Image src={PHOTO_1} alt="Landscape adaptive" width={120} height={80} showBorder />
						<Image src={PHOTO_2} alt="Wide adaptive" width={200} height={140} showBorder />
						<Image src={PHOTO_SQ} alt="Large adaptive" width={120} height={120} showBorder />
					</div>
				</div>

				{/* Explicit shapes: circle (avatar), pill (banner), sharp */}
				<div>
					<SubSectionTitle tokens={tokens}>Принудительная форма (explicit)</SubSectionTitle>
					<Text
						text="Явное cornerRadius игнорирует глобальный cap. Circle = min(w,h)/2, pill = height/2, sharp = 0."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", alignItems: "flex-start", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Image src={PHOTO_SQ} alt="Circle avatar" width={80} height={80} cornerRadius={40} showBorder />
							<Text text="Circle (avatar)" variant="label-small" />
						</div>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Image src={PHOTO_1} alt="Pill banner" width={160} height={60} cornerRadius={30} showBorder />
							<Text text="Pill (banner)" variant="label-small" />
						</div>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Image src={PHOTO_2} alt="Sharp" width={120} height={80} cornerRadius={0} showBorder />
							<Text text="Sharp (0)" variant="label-small" />
						</div>
					</div>
				</div>

				{/* Adaptive cap vs Explicit — same size, different behavior */}
				<div>
					<SubSectionTitle tokens={tokens}>Adaptive cap vs Explicit (200×140)</SubSectionTitle>
					<Text
						text="Оба изображения 200×140. Слева — adaptive (cap ограничивает). Справа — explicit cornerRadius=70 (без cap)."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ display: "flex", alignItems: "flex-start", gap: toRem(tokens.spacing.md), marginTop: toRem(tokens.spacing.sm) }}>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Image src={PHOTO_1} alt="Adaptive capped" width={200} height={140} showBorder />
							<Text text="Adaptive (с cap)" variant="label-small" emphasis="muted" />
						</div>
						<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
							<Image src={PHOTO_1} alt="Explicit no cap" width={200} height={140} cornerRadius={70} showBorder />
							<Text text="Explicit 70dp (без cap)" variant="label-small" emphasis="muted" />
						</div>
					</div>
				</div>

				{/* Border */}
				<div>
					<SubSectionTitle tokens={tokens}>Subtle border (showBorder)</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "flex-start", gap: toRem(tokens.spacing.md) }}>
						<Image src={PHOTO_SQ} alt="With border" width={80} height={80} cornerRadius={8} showBorder />
						<Image src={PHOTO_SQ} alt="Circle border" width={80} height={80} cornerRadius={40} showBorder />
						<Image src={PHOTO_2} alt="No border" width={120} height={80} cornerRadius={12} />
					</div>
				</div>

				{/* Object-fit variants */}
				<div>
					<SubSectionTitle tokens={tokens}>Варианты objectFit</SubSectionTitle>
					<div style={{ display: "flex", gap: toRem(tokens.spacing.md), flexWrap: "wrap" }}>
						{FITS.map((fit) => (
							<div key={fit} style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.xs) }}>
								<Image
									src={PHOTO_1}
									alt={fit}
									width={80}
									height={80}
									objectFit={fit}
									showBorder
								/>
								<Text text={fit} variant="label-small" />
							</div>
						))}
					</div>
				</div>

				{/* Skeleton loading demo */}
				<div>
					<SubSectionTitle tokens={tokens}>Skeleton → загрузка изображения</SubSectionTitle>
					<Text
						text="При загрузке отображается shimmer-skeleton. Нажмите кнопку для перезагрузки."
						variant="body-small"
						emphasis="muted"
					/>
					<div style={{ marginTop: toRem(tokens.spacing.sm) }}>
						<Button
							text="⟳ Перезагрузить"
							variant="outline"
							size="sm"
							onClick={() => setReloadKey(k => k + 1)}
						/>
					</div>
					<div
						key={reloadKey}
						style={{
							display: "flex",
							alignItems: "flex-start",
							gap: toRem(tokens.spacing.md),
							marginTop: toRem(tokens.spacing.md),
						}}
					>
						<Image
							src={`${PHOTO_LARGE_1}?v=${reloadKey}`}
							alt="Large landscape"
							width={200}
							height={140}
							cornerRadius={12}
							showBorder
						/>
						<Image
							src={`${PHOTO_LARGE_2}?v=${reloadKey}`}
							alt="Large square"
							width={140}
							height={140}
							cornerRadius={70}
							showBorder
						/>
						<Image
							src={`${PHOTO_LARGE_3}?v=${reloadKey}`}
							alt="Large rect"
							width={180}
							height={120}
							cornerRadius={8}
							showBorder
						/>
					</div>
				</div>

				{/* Fallback on error */}
				<div>
					<SubSectionTitle tokens={tokens}>Fallback при ошибке загрузки</SubSectionTitle>
					<div style={{ display: "flex", alignItems: "flex-start", gap: toRem(tokens.spacing.md) }}>
						<Image
							src="https://invalid.url/broken.jpg"
							alt="Fallback demo"
							fallback={PHOTO_SQ}
							width={100}
							height={100}
							cornerRadius={8}
							showBorder
						/>
					</div>
				</div>
			</div>
		</Section>
	);
}
