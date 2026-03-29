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

export default function SecondPage() {
  const router = useRouter();

  const titleConfig = new TextBlockConfig(
    'title-second',
    'Вторая страница',
    TextBlockVariant.H1,
    null,
    Visibility.Visible,
  );

  const buttonConfig = new ButtonConfig(
    'btn-go-back',
    'Вернуться назад',
    ButtonVariant.Secondary,
    ButtonSize.Lg,
    false,
    false,
    '/first',
    null,
    Visibility.Visible,
  );

  const handleAction = () => {
    router.back();
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
