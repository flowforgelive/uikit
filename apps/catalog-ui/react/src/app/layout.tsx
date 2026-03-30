import { CatalogThemeProvider } from './theme/CatalogThemeProvider';
import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'UIKit Catalog',
  description: 'UIKit MVP — Compose Multiplatform + React',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ru">
      <body>
        <CatalogThemeProvider>{children}</CatalogThemeProvider>
      </body>
    </html>
  );
}
