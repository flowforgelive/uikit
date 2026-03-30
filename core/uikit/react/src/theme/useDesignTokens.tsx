"use client";

import React, { createContext, useContext, type ReactNode } from "react";
import {
	DesignTokens,
	type DesignTokens as DesignTokensType,
} from "uikit-common";

const defaultTokens = DesignTokens.Companion.Default;

const DesignTokensContext = createContext<DesignTokensType>(defaultTokens);

/**
 * Хук для доступа к текущим design tokens.
 * Сначала пробует UIKitThemeProvider context, затем — DesignTokensProvider.
 */
export function useDesignTokens(): DesignTokensType {
	return useContext(DesignTokensContext);
}

/**
 * Низкоуровневый провайдер (legacy).
 * Предпочитайте UIKitThemeProvider для полного управления темой.
 */
export function DesignTokensProvider({
	tokens,
	children,
}: {
	tokens?: DesignTokensType;
	children: ReactNode;
}) {
	return (
		<DesignTokensContext.Provider value={tokens ?? defaultTokens}>
			{children}
		</DesignTokensContext.Provider>
	);
}

export { DesignTokensContext };
