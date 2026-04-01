"use client";

import React, { createContext, useContext, type ReactNode } from "react";
import { SurfaceContext } from "uikit-common";

const defaultSurfaceContext = new SurfaceContext(0, "transparent");

const SurfaceContextReact = createContext<SurfaceContext>(defaultSurfaceContext);

export function useSurfaceContext(): SurfaceContext {
	return useContext(SurfaceContextReact);
}

export function SurfaceContextProvider({
	value,
	children,
}: {
	value: SurfaceContext;
	children: ReactNode;
}) {
	return (
		<SurfaceContextReact.Provider value={value}>
			{children}
		</SurfaceContextReact.Provider>
	);
}
