"use client";

import React, { useState, useCallback } from "react";
import { Button, toRem, textStyle } from "@uikit/react";
import { Section } from "../../components/catalog/Section";

const DURATION_KEYS = [
	["durationInstant", "Instant"],
	["durationFast", "Fast"],
	["durationNormal", "Normal"],
	["durationSlow", "Slow"],
	["durationSlower", "Slower"],
] as const;

export function MotionShowcase({ tokens }: { tokens: any }) {
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
							<span style={{ ...textStyle(tokens.typography.labelMedium, tokens), color: tokens.color.textMuted, minWidth: "6rem", textAlign: "end" }}>
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
