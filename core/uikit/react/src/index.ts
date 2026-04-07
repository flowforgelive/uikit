// Convenience API (primary)
export { Button } from "./components/composites/button/Button";
export { Icon } from "./components/primitives/icon/Icon";
export { Text } from "./components/primitives/text/Text";
export { SegmentedControl } from "./components/composites/segmented-control/SegmentedControl";
export { Surface } from "./components/primitives/surface/Surface";
export { Panel } from "./components/blocks/panel/Panel";
export { Chip } from "./components/composites/chip/Chip";
export { Divider } from "./components/primitives/divider/Divider";
export { Image } from "./components/primitives/image/Image";
export { Skeleton } from "./components/primitives/skeleton/Skeleton";

// Config-based API (advanced / SDUI)
export { ButtonView } from "./components/composites/button/ButtonView";
export { IconView } from "./components/primitives/icon/IconView";
export { TextBlockView } from "./components/primitives/text/TextBlockView";
export { SegmentedControlView } from "./components/composites/segmented-control/SegmentedControlView";
export { SurfaceView } from "./components/primitives/surface/SurfaceView";
export { PanelView } from "./components/blocks/panel/PanelView";
export { ChipView } from "./components/composites/chip/ChipView";
export { DividerView } from "./components/primitives/divider/DividerView";
export { ImageView } from "./components/primitives/image/ImageView";
export { SkeletonView } from "./components/primitives/skeleton/SkeletonView";

// Theme
export { useDesignTokens, DesignTokensProvider } from "./theme/useDesignTokens";
export { UIKitThemeProvider, useUIKitTheme } from "./theme/UIKitThemeProvider";
export { UIKitThemeScript } from "./theme/UIKitThemeScript";
export { useSurfaceContext, SurfaceContextProvider } from "./theme/SurfaceContext";

// Utils
export { toRem, toEm, toLineHeightRatio, textStyle, buildFontStack } from "./utils/units";
export { VARIANT_MAP, INTENT_MAP, SIZE_MAP, ICON_POSITION_MAP } from "./utils/enumMaps";
export { buildInteractiveStyleVars, buildSpinnerStyleVars } from "./utils/interactiveStyleVars";

// Hooks
export { useResolvedStyle } from "./hooks/useResolvedStyle";
export { useInteractiveHandler } from "./hooks/useInteractiveHandler";

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
	scaleDesignTokens,
	withControlProportions,
	PanelConfig,
	PanelVariant,
	PanelSide,
	PanelCollapsible,
	PanelStyleResolver,
	IconConfig,
	IconStyleResolver,
	ChipConfig,
	ChipStyleResolver,
	DividerConfig,
	DividerOrientation,
	DividerStyleResolver,
	ImageConfig,
	ImageFit,
	ImageLoading,
	ImageStyleResolver,
	SkeletonConfig,
	SkeletonShape,
	SkeletonStyleResolver,
} from "uikit-common";
