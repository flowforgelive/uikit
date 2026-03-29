/**
 * Re-exports from the shared KMP module for convenient access.
 * The KMP JS output uses fully qualified namespaces (com.uikit.xxx).
 * This file flattens them for React usage.
 */
import shared from 'uikit-common';

// Tokens
export const DesignTokens = shared.com.uikit.tokens.DesignTokens;
export const ColorTokens = shared.com.uikit.tokens.ColorTokens;
export const SpacingTokens = shared.com.uikit.tokens.SpacingTokens;
export const TypographyTokens = shared.com.uikit.tokens.TypographyTokens;
export const SizingTokens = shared.com.uikit.tokens.SizingTokens;
export const RadiusTokens = shared.com.uikit.tokens.RadiusTokens;

// Components — Button
export const ButtonConfig = shared.com.uikit.components.atoms.button.ButtonConfig;
export const ButtonVariant = shared.com.uikit.components.atoms.button.ButtonVariant;
export const ButtonSize = shared.com.uikit.components.atoms.button.ButtonSize;
export const ButtonStyleResolver = shared.com.uikit.components.atoms.button.ButtonStyleResolver;
export const ColorSet = shared.com.uikit.components.atoms.button.ColorSet;
export const SizeSet = shared.com.uikit.components.atoms.button.SizeSet;
export const ResolvedButtonStyle = shared.com.uikit.components.atoms.button.ResolvedButtonStyle;

// Components — Text
export const TextBlockConfig = shared.com.uikit.components.atoms.text.TextBlockConfig;
export const TextBlockVariant = shared.com.uikit.components.atoms.text.TextBlockVariant;
export const TextBlockStyleResolver = shared.com.uikit.components.atoms.text.TextBlockStyleResolver;
export const ResolvedTextBlockStyle = shared.com.uikit.components.atoms.text.ResolvedTextBlockStyle;

// Foundation
export const Visibility = shared.com.uikit.foundation.Visibility;

// Type aliases for convenience
export type DesignTokensType = InstanceType<typeof DesignTokens>;
export type ButtonConfigType = InstanceType<typeof ButtonConfig>;
export type TextBlockConfigType = InstanceType<typeof TextBlockConfig>;
export type ResolvedButtonStyleType = InstanceType<typeof ResolvedButtonStyle>;
export type ResolvedTextBlockStyleType = InstanceType<typeof ResolvedTextBlockStyle>;
