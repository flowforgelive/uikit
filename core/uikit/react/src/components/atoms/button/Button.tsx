'use client';

import React, { useMemo } from 'react';
import { ButtonView } from './ButtonView';
import {
  ButtonConfig,
  ButtonVariant,
  ButtonSize,
  Visibility,
} from '../../../shared';

const VARIANT_MAP = {
  primary: ButtonVariant.Primary,
  secondary: ButtonVariant.Secondary,
  ghost: ButtonVariant.Ghost,
  danger: ButtonVariant.Danger,
  link: ButtonVariant.Link,
} as const;

const SIZE_MAP = {
  sm: ButtonSize.Sm,
  md: ButtonSize.Md,
  lg: ButtonSize.Lg,
} as const;

interface ButtonProps {
  text: string;
  onClick?: () => void;
  variant?: keyof typeof VARIANT_MAP;
  size?: keyof typeof SIZE_MAP;
  disabled?: boolean;
  loading?: boolean;
  className?: string;
}

export const Button: React.FC<ButtonProps> = React.memo(
  ({
    text,
    onClick,
    variant = 'primary',
    size = 'md',
    disabled = false,
    loading = false,
    className,
  }) => {
    const config = useMemo(
      () =>
        new ButtonConfig(
          '',
          text,
          VARIANT_MAP[variant],
          SIZE_MAP[size],
          disabled,
          loading,
          null,
          null,
          Visibility.Visible,
        ),
      [text, variant, size, disabled, loading],
    );

    return (
      <ButtonView
        config={config}
        onAction={onClick}
        className={className}
      />
    );
  },
);

Button.displayName = 'Button';
