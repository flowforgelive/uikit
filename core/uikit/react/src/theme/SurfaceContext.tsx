"use client";

import React, { createContext, useContext, type ReactNode } from "react";

export interface SurfaceContextValue {
	level: number;
	backgroundColor: string;
}

const defaultSurfaceContext: SurfaceContextValue = {
	level: 0,
	backgroundColor: "#FFFFFF",
};

const SurfaceContextReact = createContext<SurfaceContextValue>(defaultSurfaceContext);

export function useSurfaceContext(): SurfaceContextValue {
	return useContext(SurfaceContextReact);
}

export function SurfaceContextProvider({
	value,
	children,
}: {
	value: SurfaceContextValue;
	children: ReactNode;
}) {
	return (
		<SurfaceContextReact.Provider value={value}>
			{children}
		</SurfaceContextReact.Provider>
	);
}
