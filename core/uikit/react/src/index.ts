// Convenience API (primary)
export { Button } from './components/atoms/button/Button';
export { Text } from './components/atoms/text/Text';

// Config-based API (advanced / SDUI)
export { ButtonView } from './components/atoms/button/ButtonView';
export { TextBlockView } from './components/atoms/text/TextBlockView';

// Theme
export { useDesignTokens, DesignTokensProvider } from './theme/useDesignTokens';

// Re-exports from shared KMP module
export {
  DesignTokens,
  ButtonConfig,
  ButtonVariant,
  ButtonSize,
  TextBlockConfig,
  TextBlockVariant,
  Visibility,
  ButtonStyleResolver,
  TextBlockStyleResolver,
} from './shared';

export type {
  DesignTokensType,
  ButtonConfigType,
  TextBlockConfigType,
} from './shared';
