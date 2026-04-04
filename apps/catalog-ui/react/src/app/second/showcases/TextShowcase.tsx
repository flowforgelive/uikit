import React from "react";
import { Text, toRem } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

export function TextShowcase({ tokens }: { tokens: any }) {
	return (
		<Section id="text" title="Варианты текста (Text)" tokens={tokens}>
			<div style={{ display: "flex", flexDirection: "column", gap: toRem(tokens.spacing.md) }}>
				<Text text="Headline Large — Заголовок" variant="headline-large" />
				<Text text="Headline Medium — Подзаголовок" variant="headline-medium" />
				<Text text="Title Large — Секция" variant="title-large" />
				<Text text="Body Large — Основной текст для чтения. The quick brown fox jumps over the lazy dog. مرحبا بالعالم. 你好世界" variant="body-large" />
				<Text text="Label Medium — Подпись к элементам" variant="label-medium" />
			</div>
		</Section>
	);
}
