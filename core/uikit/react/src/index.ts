// Convenience API (primary)
export { Button } from "./components/composites/button/Button";
export { Icon } from "./components/primitives/icon/Icon";
export { Text } from "./components/primitives/text/Text";
export { SegmentedControl } from "./components/composites/segmented-control/SegmentedControl";
export { Surface } from "./components/primitives/surface/Surface";
export { Panel } from "./components/blocks/panel/Panel";
export { Chip } from "./components/composites/chip/Chip";

// Config-based API (advanced / SDUI)
export { ButtonView } from "./components/composites/button/ButtonView";
export { IconView } from "./components/primitives/icon/IconView";
export { TextBlockView } from "./components/primitives/text/TextBlockView";
export { SegmentedControlView } from "./components/composites/segmented-control/SegmentedControlView";
export { SurfaceView } from "./components/primitives/surface/SurfaceView";
export { PanelView } from "./components/blocks/panel/PanelView";
export { ChipView } from "./components/composites/chip/ChipView";

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
	TextBlockConfig,
	TextBlockVariant,
	TextEmphasis,
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
	IconConfig,
	IconStyleResolver,
	ChipConfig,
	ChipStyleResolver,
} from "uikit-common";
