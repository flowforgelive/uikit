"use client";

import React, { useMemo, useCallback } from "react";
import { useDesignTokens } from "../../../theme/useDesignTokens";
import {
	ButtonStyleResolver,
	Visibility,
	type ButtonConfig,
	type DesignTokens,
} from "uikit-common";

interface ButtonViewProps {
	config: ButtonConfig;
	onAction?: (route: string) => void;
	onClick?: () => void;
	tokens?: DesignTokens;
	className?: string;
}

export const ButtonView: React.FC<ButtonViewProps> = React.memo(
	({ config, onAction, onClick, tokens: tokensProp, className }) => {
		if (config.visibility === Visibility.Gone) return null;

		const contextTokens = useDesignTokens();
		const tokens = tokensProp ?? contextTokens;
		const style = useMemo(
			() => ButtonStyleResolver.getInstance().resolve(config, tokens),
			[config, tokens],
		);

		const handleClick = useCallback(() => {
			if (config.isInteractive) {
				onClick?.();
				if (config.actionRoute) {
					onAction?.(config.actionRoute!);
				}
			}
		}, [config.isInteractive, config.actionRoute, onAction, onClick]);

		return (
			<button
				onClick={handleClick}
				disabled={!config.isInteractive}
				data-testid={config.testTag ?? config.id}
				className={`uikit-button ${className ?? ""}`}
				style={
					{
						"--btn-bg": style.colors.bg,
						"--btn-text": style.colors.text,
						"--btn-border": style.colors.border,
						"--btn-height": `${style.sizes.height}px`,
						"--btn-padding-h": `${style.sizes.paddingH}px`,
						"--btn-font-size": `${style.sizes.fontSize}px`,
						"--btn-radius": `${style.radius}px`,
						visibility:
							config.visibility === Visibility.Invisible ? "hidden" : undefined,
					} as React.CSSProperties
				}
			>
				{config.loading ? (
					<span className="uikit-button__spinner" />
				) : (
					config.text
				)}
			</button>
		);
	},
);

ButtonView.displayName = "ButtonView";
