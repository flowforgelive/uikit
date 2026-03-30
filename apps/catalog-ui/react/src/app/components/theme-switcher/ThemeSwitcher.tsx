'use client';

import React from 'react';
import { SegmentedControl } from '@uikit/react';
import { useCatalogTheme } from '../../theme/CatalogThemeProvider';

const THEME_OPTIONS = [
  { id: 'dark', label: 'Тёмная' },
  { id: 'light', label: 'Светлая' },
];

export function ThemeSwitcher() {
  const { mode, toggleMode } = useCatalogTheme();

  return (
    <SegmentedControl
      options={THEME_OPTIONS}
      selectedId={mode}
      onSelectionChange={(id) => toggleMode(id as 'light' | 'dark')}
    />
  );
}
