import { useMemo } from "react";
import { useDesignTokens } from "../theme/useDesignTokens";
import { useSurfaceContext } from "../theme/SurfaceContext";
import type { DesignTokens, SurfaceContext } from "uikit-common";

/**
 * Resolves a component style from config + tokens + surface context.
 * Replaces the 4-line boilerplate (useDesignTokens → fallback → useSurfaceContext → useMemo)
 * with a single hook call.
 *
 * @param config - component config (used as useMemo dependency)
 * @param resolve - pure resolver function: (config, tokens, surface) → style
 * @param tokensProp - optional SSR token override (bypasses context)
 * @returns { style, tokens } — resolved style + effective tokens (for CSS custom properties)
 */
export function useResolvedStyle<C, S>(
	config: C,
	resolve: (config: C, tokens: DesignTokens, surface: SurfaceContext) => S,
	tokensProp?: DesignTokens,
): { style: S; tokens: DesignTokens } {
	const contextTokens = useDesignTokens();
	const tokens = tokensProp ?? contextTokens;
	const surface = useSurfaceContext();
	const style = useMemo(() => resolve(config, tokens, surface), [config, tokens, surface]);
	return { style, tokens };
}
