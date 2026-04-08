"use client";

import React, { useMemo } from "react";
import { BadgeView } from "./BadgeView";
import { BadgeConfig, BadgeVariant } from "uikit-common";
import { INTENT_MAP } from "../../../utils/enumMaps";

const BADGE_VARIANT_MAP = {
	dot: BadgeVariant.Dot,
	numeric: BadgeVariant.Numeric,
} as const;

interface BadgeProps {
	variant?: keyof typeof BADGE_VARIANT_MAP;
	value?: number;
	maxValue?: number;
	intent?: keyof typeof INTENT_MAP;
	className?: string;
}

export const Badge: React.FC<BadgeProps> = React.memo(
	({
		variant = "dot",
		value,
		maxValue,
		intent = "danger",
		className,
	}) => {
		const config = useMemo(
			() =>
				new BadgeConfig(
					BADGE_VARIANT_MAP[variant],
					value ?? 0,
					maxValue ?? 99,
					INTENT_MAP[intent],
				),
			[variant, value, maxValue, intent],
		);

		return <BadgeView config={config} className={className} />;
	},
);

Badge.displayName = "Badge";
