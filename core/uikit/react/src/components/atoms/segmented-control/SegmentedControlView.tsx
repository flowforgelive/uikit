'use client';

import React, { useMemo, useCallback } from 'react';
import { useDesignTokens } from '../../../theme/useDesignTokens';
import {
  SegmentedControlStyleResolver,
  Visibility,
  type SegmentedControlConfig,
  type DesignTokens,
} from 'uikit-common';

interface SegmentedControlViewProps {
  config: SegmentedControlConfig;
  onSelectionChange?: (id: string) => void;
  tokens?: DesignTokens;
  className?: string;
}

export const SegmentedControlView: React.FC<SegmentedControlViewProps> =
  React.memo(({ config, onSelectionChange, tokens: tokensProp, className }) => {
    if (config.visibility === Visibility.Gone) return null;

    const contextTokens = useDesignTokens();
    const tokens = tokensProp ?? contextTokens;
    const style = useMemo(
      () => SegmentedControlStyleResolver.getInstance().resolve(tokens),
      [tokens],
    );

    const options = config.options;
    const selectedIndex = Math.max(
      0,
      options.findIndex((o: any) => o.id === config.selectedId),
    );
    const segmentPercent = options.length > 0 ? 100 / options.length : 0;

    return (
      <div
        data-testid={config.testTag ?? config.id}
        className={`uikit-segmented-control ${className ?? ''}`}
        style={
          {
            '--sc-track-bg': style.colors.trackBg,
            '--sc-thumb-bg': style.colors.thumbBg,
            '--sc-text-active': style.colors.textActive,
            '--sc-text-inactive': style.colors.textInactive,
            '--sc-height': `${style.sizes.height}px`,
            '--sc-font-size': `${style.sizes.fontSize}px`,
            '--sc-radius': `${style.sizes.radius}px`,
            '--sc-thumb-radius': `${style.sizes.thumbRadius}px`,
            '--sc-track-padding': `${style.sizes.trackPadding}px`,
            '--sc-thumb-offset': `${selectedIndex * segmentPercent}%`,
            '--sc-thumb-width': `${segmentPercent}%`,
            visibility:
              config.visibility === Visibility.Invisible ? 'hidden' : undefined,
          } as React.CSSProperties
        }
      >
        <div className="uikit-segmented-control__thumb" />
        {Array.from(options).map((option: any) => (
          <button
            key={option.id}
            type="button"
            className={`uikit-segmented-control__option ${
              option.id === config.selectedId
                ? 'uikit-segmented-control__option--active'
                : ''
            }`}
            onClick={() => onSelectionChange?.(option.id)}
          >
            {option.label}
          </button>
        ))}
      </div>
    );
  });

SegmentedControlView.displayName = 'SegmentedControlView';
