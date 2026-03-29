'use client';

import React, { useMemo, useCallback } from 'react';
import { useDesignTokens } from '../../../theme/useDesignTokens';
import {
  ButtonStyleResolver,
  Visibility,
  type ButtonConfigType,
} from '../../../shared';

interface ButtonViewProps {
  config: ButtonConfigType;
  onAction?: (route: string) => void;
  className?: string;
}

export const ButtonView: React.FC<ButtonViewProps> = React.memo(
  ({ config, onAction, className }) => {
    if (config.visibility === Visibility.Gone) return null;

    const tokens = useDesignTokens();
    const style = useMemo(
      () => ButtonStyleResolver.resolve(config, tokens),
      [config, tokens],
    );

    const handleClick = useCallback(() => {
      if (config.isInteractive) {
        if (config.actionRoute) {
          onAction?.(config.actionRoute!);
        } else {
          onAction?.('');
        }
      }
    }, [config.isInteractive, config.actionRoute, onAction]);

    return (
      <button
        onClick={handleClick}
        disabled={!config.isInteractive}
        data-testid={config.testTag ?? config.id}
        className={`uikit-button ${className ?? ''}`}
        style={
          {
            '--btn-bg': style.colors.bg,
            '--btn-text': style.colors.text,
            '--btn-border': style.colors.border,
            '--btn-height': `${style.sizes.height}px`,
            '--btn-padding-h': `${style.sizes.paddingH}px`,
            '--btn-font-size': `${style.sizes.fontSize}px`,
            '--btn-radius': `${style.radius}px`,
            visibility:
              config.visibility === Visibility.Invisible ? 'hidden' : undefined,
          } as React.CSSProperties
        }
      >
        {config.loading ? (
          <span className="uikit-button__spinner" />
        ) : (
          config.text
        )}
      </button>
    );
  },
);

ButtonView.displayName = 'ButtonView';
