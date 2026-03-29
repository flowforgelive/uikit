'use client';

import { useRouter } from 'next/navigation';
import {
  ButtonView,
  TextBlockView,
  ButtonConfig,
  ButtonVariant,
  ButtonSize,
  TextBlockConfig,
  TextBlockVariant,
  Visibility,
} from '@uikit/react';

export default function FirstPage() {
  const router = useRouter();

  const titleConfig = new TextBlockConfig(
    'title-first',
    'Первая страница',
    TextBlockVariant.H1,
    null,
    Visibility.Visible,
  );

  const buttonConfig = new ButtonConfig(
    'btn-go-second',
    'Перейти на вторую страницу',
    ButtonVariant.Primary,
    ButtonSize.Lg,
    false,
    false,
    '/second',
    null,
    Visibility.Visible,
  );

  const handleAction = (route: string) => {
    router.push(route);
  };

  return (
    <main
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        gap: '24px',
      }}
    >
      <TextBlockView config={titleConfig} />
      <ButtonView config={buttonConfig} onAction={handleAction} />
    </main>
  );
}
