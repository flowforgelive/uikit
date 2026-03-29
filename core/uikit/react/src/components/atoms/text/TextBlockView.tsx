'use client';

import React, { useMemo } from 'react';
import { useDesignTokens } from '../../../theme/useDesignTokens';
import {
  TextBlockStyleResolver,
  TextBlockVariant,
  Visibility,
  type TextBlockConfigType,
} from '../../../shared';

interface TextBlockViewProps {
  config: TextBlockConfigType;
  className?: string;
}

export const TextBlockView: React.FC<TextBlockViewProps> = React.memo(
  ({ config, className }) => {
    if (config.visibility === Visibility.Gone) return null;

    const tokens = useDesignTokens();
    const style = useMemo(
      () => TextBlockStyleResolver.resolve(config, tokens),
      [config, tokens],
    );

    const Tag =
      config.variant === TextBlockVariant.H1
        ? 'h1'
        : config.variant === TextBlockVariant.H2
          ? 'h2'
          : config.variant === TextBlockVariant.H3
            ? 'h3'
            : 'p';

    return (
      <Tag
        data-testid={config.testTag ?? config.id}
        className={`uikit-text-block ${className ?? ''}`}
        style={{
          color: style.color,
          fontSize: `${style.fontSize}px`,
          fontWeight: style.fontWeight,
          lineHeight: `${style.lineHeight}px`,
          margin: 0,
          visibility:
            config.visibility === Visibility.Invisible ? 'hidden' : undefined,
        }}
      >
        {config.text}
      </Tag>
    );
  },
);

TextBlockView.displayName = 'TextBlockView';
