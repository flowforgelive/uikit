'use client';

import { useRouter } from 'next/navigation';
import { Button, Text } from '@uikit/react';

export default function FirstPage() {
  const router = useRouter();

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
      <Text text="Первая страница" variant="h1" />
      <Button
        text="Перейти на вторую страницу"
        onClick={() => router.push('/second')}
      />
    </main>
  );
}
