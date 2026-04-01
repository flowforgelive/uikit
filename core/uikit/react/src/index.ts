// Convenience API (primary)
export { Button } from "./components/atoms/button/Button";
export { Text } from "./components/atoms/text/Text";
export { SegmentedControl } from "./components/atoms/segmented-control/SegmentedControl";
export { Surface } from "./components/atoms/surface/Surface";

// Config-based API (advanced / SDUI)
export { ButtonView } from "./components/atoms/button/ButtonView";
export { TextBlockView } from "./components/atoms/text/TextBlockView";
export { SegmentedControlView } from "./components/atoms/segmented-control/SegmentedControlView";
export { SurfaceView } from "./components/atoms/surface/SurfaceView";

// Theme
export { useDesignTokens, DesignTokensProvider } from "./theme/useDesignTokens";
export { UIKitThemeProvider, useUIKitTheme } from "./theme/UIKitThemeProvider";
export { UIKitThemeScript } from "./theme/UIKitThemeScript";
export { useSurfaceContext, SurfaceContextProvider } from "./theme/SurfaceContext";
export type { SurfaceContextValue } from "./theme/SurfaceContext";

// Utils
export { toRem } from "./utils/units";

// Re-exports from KMP module (ESM flat exports)
export {
	DesignTokens,
	ButtonConfig,
	ComponentSize,
	ComponentSize as ButtonSize,
	ColorIntent,
	VisualVariant,
	TextBlockConfig,
	TextBlockVariant,
	SegmentedControlConfig,
	SegmentedControlOption,
	SegmentedControlStyleResolver,
	ThemeMode,
	Visibility,
	ButtonStyleResolver,
	TextBlockStyleResolver,
	SurfaceConfig,
	SurfaceLevel,
	SurfaceShape,
	SurfaceStyleResolver,
	SurfaceContext,
	ShadowTokens,
	InteractiveStateTokens,
	Density,
} from "uikit-common";
