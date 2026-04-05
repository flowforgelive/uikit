// Convenience API (primary)
export { Button } from "./components/composites/button/Button";
export { IconButton } from "./components/composites/icon-button/IconButton";
export { Text } from "./components/primitives/text/Text";
export { SegmentedControl } from "./components/composites/segmented-control/SegmentedControl";
export { Surface } from "./components/primitives/surface/Surface";
export { Panel } from "./components/blocks/panel/Panel";

// Config-based API (advanced / SDUI)
export { ButtonView } from "./components/composites/button/ButtonView";
export { IconButtonView } from "./components/composites/icon-button/IconButtonView";
export { TextBlockView } from "./components/primitives/text/TextBlockView";
export { SegmentedControlView } from "./components/composites/segmented-control/SegmentedControlView";
export { SurfaceView } from "./components/primitives/surface/SurfaceView";
export { PanelView } from "./components/blocks/panel/PanelView";

// Theme
export { useDesignTokens, DesignTokensProvider } from "./theme/useDesignTokens";
export { UIKitThemeProvider, useUIKitTheme } from "./theme/UIKitThemeProvider";
export { UIKitThemeScript } from "./theme/UIKitThemeScript";
export { useSurfaceContext, SurfaceContextProvider } from "./theme/SurfaceContext";

// Utils
export { toRem, toEm, toLineHeightRatio, textStyle, buildFontStack } from "./utils/units";

// Re-exports from KMP module (ESM flat exports)
export {
	DesignTokens,
	ButtonConfig,
	ComponentSize,
	ComponentSize as ButtonSize,
	ColorIntent,
	VisualVariant,
	IconPosition,
	IconButtonConfig,
	IconButtonStyleResolver,
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
	PanelConfig,
	PanelVariant,
	PanelSide,
	PanelCollapsible,
	PanelStyleResolver,
} from "uikit-common";
