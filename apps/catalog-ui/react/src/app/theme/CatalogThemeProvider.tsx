'use client';

import React, { createContext, useContext, useState, useCallback, type ReactNode } from 'react';
import { DesignTokensProvider, DesignTokens } from '@uikit/react';

type ThemeMode = 'light' | 'dark';

interface CatalogThemeContextValue {
  mode: ThemeMode;
  toggleMode: (mode: ThemeMode) => void;
}

const CatalogThemeContext = createContext<CatalogThemeContextValue>({
  mode: 'dark',
  toggleMode: () => {},
});

export function useCatalogTheme() {
  return useContext(CatalogThemeContext);
}

const tokensForMode = (mode: ThemeMode) =>
  mode === 'dark'
    ? DesignTokens.Companion.DefaultDark
    : DesignTokens.Companion.DefaultLight;

export function CatalogThemeProvider({ children }: { children: ReactNode }) {
  const [mode, setMode] = useState<ThemeMode>('dark');

  const toggleMode = useCallback((newMode: ThemeMode) => {
    setMode(newMode);
  }, []);

  const tokens = tokensForMode(mode);

  return (
    <CatalogThemeContext.Provider value={{ mode, toggleMode }}>
      <DesignTokensProvider tokens={tokens}>
        <div
          className="catalog-theme-root"
          style={{
            backgroundColor: tokens.color.surface,
            color: tokens.color.textPrimary,
            minHeight: '100vh',
            transition: 'background-color 0.2s ease, color 0.2s ease',
          }}
        >
          {children}
        </div>
      </DesignTokensProvider>
    </CatalogThemeContext.Provider>
  );
}
