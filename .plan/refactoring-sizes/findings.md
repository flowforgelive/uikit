# Findings & Decisions

## Источник: CROSS_PLATFORM_AUDIT.md (аудит от 2026-04-03)

## Инвентаризация hardcoded значений

### Критические (вызывают визуальные расхождения)

| # | Файл | Строка | Hardcoded | Эталон (Common) | Влияние |
|---|------|--------|-----------|-----------------|---------|
| 1 | `SegmentedControlView.module.css` | 44 | `font-weight: 500` | 600 (ControlSizeScale) | React жирность ≠ эталон |
| 2 | `SegmentedControlView.module.css` | 40 | `padding-inline: 12px` | paddingH из resolver (13dp Sm, 15dp Md) | React отступ фиксированный |
| 3 | `SegmentedControlView.kt` | Text() | нет fontWeight | 600 (ControlSizeScale) | Compose жирность = 400 (default) |
| 4 | `SegmentedControlView.kt` | Text() | нет letterSpacing | -0.24 (Md), 0.07 (Xs) | Compose letterSpacing = 0 (default) |
| 5 | `ButtonView.module.css` | 95 | `border: 2px` (spinner) | нет токена → нужен | При scaleFactor≠1 разойдётся |
| 6 | `IconButtonView.module.css` | 72 | `border: 2px` (spinner) | нет токена → нужен | Аналогично |
| 7 | `ButtonView.kt` | 126 | `strokeWidth = 2.dp` | нет токена → нужен | При scaleFactor≠1 разойдётся |
| 8 | `IconButtonView.kt` | 112 | `strokeWidth = 2.dp` | нет токена → нужен | Аналогично |
| 9 | `SurfaceView.kt` | 68 | `.shadow(4.dp, shape)` | ShadowTokens (CSS string) | Compose shadow ≠ React shadow |
| 10 | `SegmentedControlStyleResolver.kt` | 49 | `TRACK_PADDING = 2.0` | нет токена → нужен | Невозможно переопределить |

### Допустимые fallback-значения (не требуют немедленного исправления)

| # | Файл | Значение | Причина допуска |
|---|------|----------|-----------------|
| 1 | `*.module.css` | `var(--*-focus-ring-width, 2px)` | CSS fallback = `tokens.focusRingWidth` значение по умолчанию |
| 2 | `*.module.css` | `var(--*-border-width, 1px)` | CSS fallback = `tokens.borderWidth` значение по умолчанию |
| 3 | `SegmentedControlView.module.css:10` | `border: 1px solid` | = tokens.borderWidth по умолчанию |

## Пропущенные поля в StyleResolver

### SegmentedControlSizes (текущее состояние)
```
✅ height, paddingH, fontSize, radius, thumbRadius, trackPadding, iconSize, iconGap
❌ fontWeight — не включен (есть в ControlSizeScale)
❌ letterSpacing — не включен (есть в ControlSizeScale)
```

### ButtonSizeSet (эталон — корректное состояние)
```
✅ height, paddingH, fontSize, fontWeight, iconSize, iconGap, letterSpacing
```

## Вычисленные размеры (scaleFactor=1.0, из ComponentSizeResolver)

| Size | fontSize | height | paddingH | iconSize | iconGap | radius | fontWeight | letterSpacing |
|------|----------|--------|----------|----------|---------|--------|------------|---------------|
| Xs | 11.0 | 27.5 | 11.0 | 13.2 | 5.5 | 5.5 | 600 | 0.07 |
| Sm | 13.0 | 32.5 | 13.0 | 15.6 | 6.5 | 6.5 | 600 | 0.0 |
| Md | 15.0 | 37.5 | 15.0 | 18.0 | 7.5 | 7.5 | 600 | -0.24 |
| Lg | 17.0 | 42.5 | 17.0 | 20.4 | 8.5 | 8.5 | 600 | -0.41 |
| Xl | 20.0 | 50.0 | 20.0 | 24.0 | 10.0 | 10.0 | 600 | -0.41 |

## Конвертация dp → rem (React)

Формула: `dp / 16 = rem`

| dp | rem |
|----|-----|
| 11.0 | 0.6875 |
| 13.0 | 0.8125 |
| 15.0 | 0.9375 |
| 37.5 | 2.34375 |
| 2.0 (trackPadding) | 0.125 |

## Различия в каталоге (catalog-ui)

- React каталог: текст кнопок lowercase (`solid md`, `soft md`)
- Compose каталог: текст кнопок Title Case (`Solid Md`, `Soft Md`)
- Причина: разные строковые данные в каталогах, не баг компонентов

## Архитектурные паттерны (эталон: Button)

### Правильный поток данных:
```
ControlSizeInput (tokens)
        ↓
ComponentSizeResolver.resolve()
        ↓
ControlSizeScale (height, paddingH, fontSize, fontWeight, iconSize, iconGap, letterSpacing, radius)
        ↓
{Component}StyleResolver.resolve()
        ↓
Resolved{Component}Style (colors + sizes + radius)
        ↓
View: CSS custom properties (React) / remember {} (Compose)
```

### SegmentedControl нарушает на шаге 4:
- ControlSizeScale содержит fontWeight и letterSpacing
- SegmentedControlSizes НЕ пробрасывает их
- Обе платформы вынужденно используют hardcoded/default значения

## Technical Decisions
| Decision | Rationale |
|----------|-----------|
| fontWeight из ControlSizeScale (600) | Единый стандарт для всех контролов, изменяется через токены |
| paddingH → option padding-inline | В SegmentedControl track padding = отдельный trackPadding, paddingH = для содержимого опции |
| spinnerStrokeWidth в DesignTokens | Глобальный спиннер-токен, т.к. используется в >1 компоненте |
| TRACK_PADDING → InteractiveControlTokens | Позволяет переопределять для кастомных тем |
| Gradle task как основной lint | Работает в CI без настройки git hooks; покрывает оба модуля |

## Issues Encountered
- Пока нет
