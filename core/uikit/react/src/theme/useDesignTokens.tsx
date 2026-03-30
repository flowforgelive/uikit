"use client";

import React, { createContext, useContext, type ReactNode } from "react";
import {
	DesignTokens,
	type DesignTokens as DesignTokensType,
} from "uikit-common";

const defaultTokens = DesignTokens.Companion.Default;

const DesignTokensContext = createContext<DesignTokensType>(defaultTokens);

export function useDesignTokens(): DesignTokensType {
	return useContext(DesignTokensContext);
}

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
