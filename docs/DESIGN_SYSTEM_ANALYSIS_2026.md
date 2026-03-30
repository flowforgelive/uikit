# Анализ дизайн-системы UIKit: Apple HIG + Material You + Ant Design + Style Switching

> Дата: 29 марта 2026
> Контекст: Комплексный анализ дизайн-токенов UIKit на соответствие Apple HIG (Liquid Glass, WWDC 2025), Google Material You (M3), Ant Design (трёхслойная система токенов), лучшим практикам адаптивных единиц и архитектуре переключаемых стилей/тем.

---

## Содержание

### Часть I — Apple HIG + Адаптивные единицы
1. [Текущее состояние токенов](#1-текущее-состояние-токенов)
2. [Apple Liquid Glass — новая парадигма (WWDC 2025)](#2-apple-liquid-glass--новая-парадигма-wwdc-2025)
3. [Цветовая система: сравнение с Apple HIG](#3-цветовая-система-сравнение-с-apple-hig)
4. [Типографика: Dynamic Type vs наш подход](#4-типографика-dynamic-type-vs-наш-подход)
5. [Адаптивные единицы: rem/em/clamp вместо px](#5-адаптивные-единицы-rememclamp-вместо-px)
6. [Поверхности, материалы и глубина](#6-поверхности-материалы-и-глубина)
7. [Адаптивность: Size Classes и отзывчивые токены](#7-адаптивность-size-classes-и-отзывчивые-токены)
8. [LTR/RTL и интернационализация](#8-ltrrtl-и-интернационализация)
9. [Тёмная/светлая тема](#9-тёмнаясветлая-тема)
10. [Сводная таблица проблем](#10-сводная-таблица-проблем)
11. [Рекомендации по приоритетам](#11-рекомендации-по-приоритетам)

### Часть II — Переключение стилей, Material You, Ant Design
12. [Google Material You (M3): система динамических цветов](#12-google-material-you-m3-система-динамических-цветов)
13. [Ant Design: трёхслойная токен-архитектура](#13-ant-design-трёхслойная-токен-архитектура)
14. [Material Design 3: архитектура токенов](#14-material-design-3-архитектура-токенов)
15. [Сравнительный анализ: наша система vs Ant Design vs Material You](#15-сравнительный-анализ-наша-система-vs-ant-design-vs-material-you)
16. [Переключаемые стили: Apple / Illustration / Cartoon / Glass](#16-переключаемые-стили-apple--illustration--cartoon--glass)
17. [Архитектура Theme Engine для KMP UIKit](#17-архитектура-theme-engine-для-kmp-uikit)
18. [Алгоритмы генерации тем](#18-алгоритмы-генерации-тем)
19. [Component Tokens: переопределение по компонентам](#19-component-tokens-переопределение-по-компонентам)
20. [Обновлённая сводная таблица проблем](#20-обновлённая-сводная-таблица-проблем)
21. [Итоговая архитектура: Unified Theme System](#21-итоговая-архитектура-unified-theme-system)

---

## 1. Текущее состояние токенов

### Что есть сейчас (commonMain)

| Файл | Содержание | Проблемы |
|---|---|---|
| `ColorTokens.kt` | 13 строковых полей (`primary`, `secondary`, `danger`, `surface`, ...) | Плоская структура, нет семантической группировки, нет dark-варианта, нет surface-уровней |
| `TypographyTokens.kt` | 10 `Int` полей (h1–caption: size + weight) | Всё в пикселях (`Int`), нет lineHeight/letterSpacing/fontFamily, всего 5 стилей |
| `SpacingTokens.kt` | 5 уровней: xs(4), sm(8), md(16), lg(24), xl(32) | Пиксельные `Int`, xs и xl не используются, нет флюидности |
| `RadiusTokens.kt` | 3 уровня: sm(4), md(8), lg(12) | sm/lg не используются (всё → md), нет `none` и `full` |
| `SizingTokens.kt` | 6 полей: button sm/md/lg + icon sm/md/lg | Пиксельные `Int`, нет адаптивности по размеру экрана |
| `ButtonStyleResolver.kt` | Резолвит `ButtonConfig` + `DesignTokens` → стили | Захардкожены `"transparent"`, lineHeight, borderWidth |
| `TextBlockStyleResolver.kt` | Резолвит текстовые стили | Захардкожены lineHeight-множители 1.2/1.5 |

### Ключевая проблема

**Все размеры — абсолютные `Int` в пикселях.** Это противоречит:
- Apple HIG (Dynamic Type, points-система)
- Web best practices (rem/em/clamp для доступности)
- Принципам отзывчивого дизайна (размер шрифта должен масштабироваться с контекстом)

---

## 2. Apple Liquid Glass — новая парадигма (WWDC 2025)

### Что такое Liquid Glass

С WWDC 2025 Apple представила **Liquid Glass** — новый материал, объединяющий дизайн-язык всех платформ Apple. Это полупрозрачный эффект "жидкого стекла", который:

- **Формирует отдельный функциональный слой** для контролов и навигации (tab bars, toolbars, sidebars), парящий над контентом
- **Пропускает контент** снизу, создавая ощущение глубины и динамичности
- **Адаптируется** между светлым и тёмным видом в зависимости от подлежащего контента
- **Не имеет собственного цвета** по умолчанию — перенимает цвета из контентного слоя за ним

### Два варианта Liquid Glass

| Вариант | Описание | Когда использовать |
|---|---|---|
| **Regular** | Больше размытия и коррекции яркости, более непрозрачный | Сайдбары, алерты, поповеры — где много текста |
| **Clear** | Высоко прозрачный, фон виден чётко | Контролы поверх медиа-контента (фото, видео) |

### Принципы использования цвета в Liquid Glass

- Цвет **применяется экономно** — только для primary actions (например, кнопка Done)
- Цвет наносится на **фон** кнопки (не на текст/иконки) для выделения primary action
- Остальные контролы используют **монохромную** схему
- Символы и текст на Liquid Glass адаптируются: тёмные на светлом фоне, светлые на тёмном

### Что это значит для наших токенов

| Аспект | У нас сейчас | Что нужно по Apple HIG |
|---|---|---|
| Материалы | Нет концепции материалов | Нужны токены для blur, opacity, backdrop-filter |
| Слои UI | Нет разделения control/content | Liquid Glass = controls, standard materials = content |
| Цвет на контролах | Кнопка окрашена сплошным цветом | Только primary action цветная, остальное монохром |
| Прозрачность | Нет токенов прозрачности | ultraThin/thin/regular/thick материалы |
| Адаптация к фону | Нет | Контролы должны адаптироваться к подлежащему контенту |

---

## 3. Цветовая система: сравнение с Apple HIG

### Apple HIG: семантическая система цветов

Apple определяет цвета **по назначению**, а не по внешнему виду:

#### Фоновые цвета (iOS/iPadOS)

| Уровень | System | Grouped |
|---|---|---|
| Primary | `systemBackground` | `systemGroupedBackground` |
| Secondary | `secondarySystemBackground` | `secondarySystemGroupedBackground` |
| Tertiary | `tertiarySystemBackground` | `tertiarySystemGroupedBackground` |

#### Цвета текста/фронта

| Роль | API | Назначение |
|---|---|---|
| Label | `label` | Основной текст |
| Secondary Label | `secondaryLabel` | Вторичный текст |
| Tertiary Label | `tertiaryLabel` | Третичный текст |
| Quaternary Label | `quaternaryLabel` | Наименее важный текст |
| Placeholder | `placeholderText` | Подсказка в полях |
| Separator | `separator` | Разделитель (полупрозрачный) |
| Opaque Separator | `opaqueSeparator` | Непрозрачный разделитель |
| Link | `link` | Ссылки |

#### Системные цвета (14 штук)

Каждый имеет **4 варианта**: Light Default, Dark Default, Light Accessible, Dark Accessible:
`red`, `orange`, `yellow`, `green`, `mint`, `teal`, `cyan`, `blue`, `indigo`, `purple`, `pink`, `brown` + 6 уровней серого (`systemGray` ... `systemGray6`)

### Наша текущая система vs Apple

```
APPLE HIG                                НАША СИСТЕМА
───────────────────────                  ───────────────────
14 system colors × 4 variants            1 primary (#3B82F6)
6 gray levels × 4 variants              1 secondary (#6B7280)
3 background levels × 2 sets            1 surface (#FFFFFF)
4 label levels                           1 textPrimary (#111827)
separator, link, placeholder             1 border (#D1D5DB)
Всего: ~100+ семантических цветов        Всего: 13 цветов, flat
```

### Критические пробелы

1. **Нет иерархии поверхностей** — у Apple 3+ уровня фона (primary/secondary/tertiary), у нас 1 `surface`
2. **Нет иерархии текста** — у Apple 4 уровня label, у нас 1 `textPrimary`
3. **Нет разделения на light/dark** — у Apple каждый цвет имеет 4 варианта
4. **Нет accent color** — у Apple это ключевой концепт для Liquid Glass
5. **Нет системных цветов** — red/orange/yellow/green/teal/blue и т.д. для статусов
6. **Нет серой шкалы** — 6 уровней серого у Apple покрывают все потребности от фонов до разделителей

---

## 4. Типографика: Dynamic Type vs наш подход

### Apple Dynamic Type

Apple определяет **11 текстовых стилей** (не 5 как у нас), каждый с полным набором атрибутов:

| Стиль | Дефолт (iOS) | Weight | Leading |
|---|---|---|---|
| Large Title | 34pt | Regular | 41pt |
| Title 1 | 28pt | Regular | 34pt |
| Title 2 | 22pt | Regular | 28pt |
| Title 3 | 20pt | Regular | 25pt |
| Headline | 17pt | Semibold | 22pt |
| Body | 17pt | Regular | 22pt |
| Callout | 16pt | Regular | 21pt |
| Subhead | 15pt | Regular | 20pt |
| Footnote | 13pt | Regular | 18pt |
| Caption 1 | 12pt | Regular | 16pt |
| Caption 2 | 11pt | Regular | 13pt |

#### Ключевая особенность: **масштабирование по 12 размерным ступеням**

Каждый стиль текста масштабируется от **xSmall** до **AX5** (12 ступеней!):

```
Пример для Body:
xSmall:  14pt
Small:   15pt
Medium:  16pt
Large:   17pt (дефолт)
xLarge:  19pt
xxLarge: 21pt
xxxLarge:23pt
AX1:     28pt
AX2:     33pt
AX3:     40pt
AX4:     47pt
AX5:     53pt
```

### Наша система vs Apple

```
APPLE DYNAMIC TYPE                       НАША СИСТЕМА
───────────────────────                  ───────────────────
11 стилей × 3 атрибута                   5 стилей × 2 атрибута
(size, weight, leading)                  (size, weight)
+ tracking (letter-spacing)              нет tracking
+ 12 размерных ступеней                  фиксированные пиксели
+ SF Pro / NY font families              нет fontFamily
= ~400 уникальных значений              = 10 значений Int
```

### Критические пробелы

1. **5 стилей vs 11** — нет Callout, Subhead, Footnote, Headline (отдельный от H*), Large Title
2. **Нет leading (line-height)** — захардкожена в TextBlockStyleResolver (1.2/1.5)
3. **Нет tracking (letter-spacing)** — Apple определяет tracking для каждого pt size
4. **Нет fontFamily** — разные шрифты для разных контекстов
5. **Нет Dynamic Type** — размер шрифта НЕ масштабируется с пользовательскими настройками
6. **Всё в пикселях Int** — нет выражения через относительные единицы

---

## 5. Адаптивные единицы: rem/em/clamp вместо px

### Почему пиксели — плохо

| Проблема | Описание |
|---|---|
| **Игнорирование настроек пользователя** | `px` для шрифтов — браузер НЕ масштабирует текст при изменении базового размера в настройках |
| **Сломанная доступность** | Пользователи, увеличившие дефолт font-size (а их больше, чем пользователей Edge), не увидят изменений |
| **Ручная адаптация** | Каждый брейкпоинт требует ручной правки каждого значения |
| **Нет пропорциональности** | Изменение одного значения не каскадируется |

### Правильный подход: флюидная типографика

#### Единицы измерения

| Единица | Для чего | Описание |
|---|---|---|
| `rem` | Блочные элементы (отступы, размеры, font-size) | Относительно корневого font-size (обычно 16px). Масштабируется с настройками пользователя |
| `em` | Инлайновые элементы (иконки рядом с текстом, padding в кнопках) | Относительно font-size родителя. Иконка `0.75em` всегда пропорциональна тексту |
| `ch` | Ширина текстовых блоков | Ширина символа "0". `max-width: 60ch` — оптимальная ширина строки |
| `vw/vh` | Флюидное масштабирование | 1% ширины/высоты viewport |
| `clamp()` | Всё адаптивное | `clamp(min, preferred, max)` — флюидный диапазон |

#### Формула флюидной типографики (Utopia-подход)

```css
/* Базовый подход */
:root {
  font-size: calc(1rem + 0.5vw);
}

/* Более точный через clamp (Utopia) */
--step-0: clamp(1.125rem, 1.074rem + 0.227vw, 1.25rem);   /* Body */
--step-1: clamp(1.35rem,  1.263rem + 0.386vw, 1.5625rem);  /* H3 */
--step-2: clamp(1.62rem,  1.484rem + 0.606vw, 1.9531rem);  /* H2 */
--step-3: clamp(1.944rem, 1.741rem + 0.904vw, 2.4414rem);  /* H1 */
```

Ключевая идея: определяем **шкалу** (type scale) для минимального viewport (например, 1.2× на мобильном) и максимального (1.25× на десктопе), а `clamp` интерполирует между ними.

#### Spacing тоже должен быть флюидным

```css
--space-s: clamp(0.75rem, 0.707rem + 0.227vw, 0.875rem);
--space-m: clamp(1.125rem, 1.074rem + 0.227vw, 1.25rem);
--space-l: clamp(1.5rem,   1.364rem + 0.682vw, 1.875rem);
```

### Как это применить к KMP

В Kotlin Multiplatform у нас **две платформы**:

| Платформа | Подход к адаптивности |
|---|---|
| **React/Web** | Токены в `rem` → CSS custom properties с `clamp()` → всё масштабируется |
| **Compose** | `sp` для текста (масштабируется с настройками доступности), `dp` для остальное — аналог Apple `pt` |

#### Предлагаемая модель: токены хранят **базовые** + **множители**

```kotlin
// TokensТекущий подход (ПЛОХО):
data class TypographyTokens(val bodySize: Int = 16) // пиксели, не масштабируются

// Предлагаемый подход:
data class TypographyTokens(
    val baseSize: Double = 1.0,     // rem (1rem = настройки пользователя)
    val scaleRatio: Double = 1.25,  // Major Third
    // Step -1: base / ratio = 0.8rem
    // Step  0: base = 1.0rem (body)
    // Step  1: base × ratio = 1.25rem
    // Step  2: base × ratio² = 1.5625rem
)
```

Резолвер на **Web** превращает в `clamp()`, на **Compose** — в `sp` с учётом `fontScale`.

---

## 6. Поверхности, материалы и глубина

### Apple HIG: система материалов

Apple определяет **два типа MaterialTokens**:

#### 1. Liquid Glass (контроли и навигация)

| Вариант | Blur | Opacity | Когда |
|---|---|---|---|
| Regular | Сильный | Умеренная | Сайдбары, алерты, множество текста |
| Clear | Минимальный | Высокая прозрачность | Поверх медиа |

#### 2. Standard Materials (контентный слой)

| Материал | Описание |
|---|---|
| `ultraThin` | Максимально прозрачный, для светлых схем |
| `thin` | Slight darkening, лёгкое затемнение |
| `regular` | Дефолтный — баланс прозрачности и читаемости |
| `thick` | Самый непрозрачный — для тёмных схем |

### Elevation/Depth в iOS Dark Mode

В Dark Mode iOS использует **два набора** фоновых цветов:
- **Base**: тусклые — задний план "отступает"
- **Elevated**: ярче — передний план "выступает" (модалки, поповеры)

### У нас: ничего из этого нет

Наш `surface` — один белый цвет `#FFFFFF`. Нет:
- Уровней глубины (base/elevated)
- Blur/backdrop-filter токенов
- Opacity токенов для материалов
- vibrancy для фронтальных элементов

### Необходимые MaterialTokens

```kotlin
data class MaterialTokens(
    // Blur (backdrop-filter: blur(Xpx))
    val blurNone: Int = 0,
    val blurSm: Int = 8,
    val blurMd: Int = 16,
    val blurLg: Int = 24,
    val blurXl: Int = 40,  // Liquid Glass regular

    // Opacity
    val opacityDimming: Double = 0.35,   // dimming layer для clear Liquid Glass
    val opacityThin: Double = 0.7,
    val opacityRegular: Double = 0.82,
    val opacityThick: Double = 0.92,

    // Surface levels (background colors)
    // light theme: white → slightly gray
    // dark theme: dark → slightly lighter
    val surfacePrimary: String,    // main background
    val surfaceSecondary: String,  // grouped/card bg
    val surfaceTertiary: String,   // nested card/input bg
    val surfaceElevated: String,   // modals, popovers
)
```

---

## 7. Адаптивность: Size Classes и отзывчивые токены

### Apple Size Classes

Apple НЕ использует фиксированные breakpoints. Вместо этого — **Size Classes**:

| Size Class | Width | Height | Примеры |
|---|---|---|---|
| Compact Width | < ~390pt | — | iPhone portrait |
| Regular Width | ≥ ~390pt | — | iPad, iPhone landscape (Max) |
| Compact Height | — | < ~414pt | iPhone landscape |
| Regular Height | — | ≥ ~414pt | iPhone portrait, iPad |

### Наша система: нет никакой адаптивности

Все значения фиксированные `Int`. Кнопка `buttonMd = 40` одинакова на iPhone SE и iPad Pro 12.9".

### Правильный подход: адаптивные spacing и sizing

#### Web: через CSS clamp()

```css
:root {
  /* Spacing масштабируется с viewport */
  --space-sm: clamp(0.5rem, 0.45rem + 0.25vw, 0.75rem);
  --space-md: clamp(1rem,   0.91rem + 0.45vw, 1.25rem);
  --space-lg: clamp(1.5rem, 1.36rem + 0.68vw, 2rem);

  /* Button height масштабируется */
  --btn-height-md: clamp(2.25rem, 2.1rem + 0.5vw, 2.75rem);
}
```

#### Compose: через WindowSizeClass

```kotlin
enum class WindowSizeClass { Compact, Medium, Expanded }

fun spacingForSizeClass(sizeClass: WindowSizeClass): SpacingTokens = when (sizeClass) {
    Compact  -> SpacingTokens(sm = 6, md = 12, lg = 20)
    Medium   -> SpacingTokens(sm = 8, md = 16, lg = 24)
    Expanded -> SpacingTokens(sm = 10, md = 20, lg = 32)
}
```

### Что нужно в KMP-токенах

Вместо фиксированных `Int`, хранить **семантические описания**, а платформенный резолвер конвертирует:

```kotlin
// Концепт: относительные значения
data class ResponsiveValue(
    val compact: Double,    // rem или dp для Compact
    val regular: Double,    // rem или dp для Regular
)
```

---

## 8. LTR/RTL и интернационализация

### Apple HIG: Right-to-Left

Apple требует:
- **Зеркалирование layout** для RTL-языков (арабский, иврит)
- Reading order: элементы размещаются от leading к trailing (не от left к right)
- Иконки с направлением (стрелки) зеркалируются

### Web best practices: logical properties

```css
/* ПЛОХО: физические свойства */
margin-left: 16px;
padding-right: 8px;
text-align: left;
border-top-left-radius: 12px;

/* ХОРОШО: логические свойства */
margin-inline-start: 1rem;
padding-inline-end: 0.5rem;
text-align: start;
border-start-start-radius: 0.75rem;
```

### У нас: нет поддержки RTL

- `paddingH` в `SizeSet` — горизонтальный padding, ок (симметричный)
- Но нет логических свойств в CSS (`ButtonView.tsx` использует px)
- Нет `LayoutDirection` токена
- В `ButtonStyleResolver` нет различий start/end

### Что нужно

```kotlin
// Токен направления
enum class LayoutDirection { LTR, RTL }

// В ResolvedButtonStyle:
data class SpacingSet(
    val paddingInlineStart: Double,
    val paddingInlineEnd: Double,
    val paddingBlock: Double,
)
```

Web-рендерер должен использовать `margin-inline-start/end` вместо `margin-left/right`.

---

## 9. Тёмная/светлая тема

### Apple HIG: обязательная поддержка

> "Even if your app ships in a single appearance mode, provide both light and dark colors to support Liquid Glass adaptivity."

Apple требует предоставления **обоих** вариантов цветов — даже если приложение только светлое — потому что Liquid Glass адаптируется к подлежащему контенту.

### Система цветов Dark Mode (iOS)

- **Base backgrounds**: `systemBackground` (R28,G28,B30), `secondary` (R44,G44,B46), `tertiary` (R58,G58,B60)
- **Elevated backgrounds**: чуть ярче base (для модалок, поповеров)
- **Labels**: белые с разной opacity (primary → quaternary)
- **System colors**: ярче чем в light mode (vibrant)
- Для каждого цвета: **4 варианта** (light, dark, light-accessible, dark-accessible)

### У нас: нет тёмной темы

`ColorTokens` содержит один набор захардкоженных hex-значений в светлой палитре. Нет:
- Dark-варианта
- Accessible-варианта (повышенный контраст)
- Переключения тем

### Необходимая структура

```kotlin
data class ColorScheme(
    val light: ColorTokens,
    val dark: ColorTokens,
    val lightAccessible: ColorTokens? = null,  // increased contrast
    val darkAccessible: ColorTokens? = null,
)
```

Или двухуровневый подход:
```kotlin
// 1. Palette (сырые цвета)
data class ColorPalette(
    val blue500: String,
    val blue600: String,
    val gray100: String,
    // ...
)

// 2. Semantic tokens (направлением на palette)
data class SemanticColors(
    val primary: String,           // → blue500 (light) / blue400 (dark)
    val onPrimary: String,         // → white (light) / white (dark)
    val surfacePrimary: String,    // → white (light) / gray900 (dark)
    val surfaceSecondary: String,  // → gray50 (light) / gray800 (dark)
    val labelPrimary: String,      // → gray900 (light) / white (dark)
    val labelSecondary: String,    // → gray600 (light) / gray400 (dark)
    val separator: String,
    val link: String,
    // ...
)
```

---

## 10. Сводная таблица проблем

| # | Проблема | Серьёзность | Apple HIG | Best Practice | У нас |
|---|---|---|---|---|---|
| 1 | **Все размеры в пикселях (Int)** | 🔴 Критично | pt + Dynamic Type (12 ступеней) | rem/em/clamp | Int px |
| 2 | **Нет тёмной темы** | 🔴 Критично | Обязательно (даже для single-mode apps) | light/dark + accessible | Нет |
| 3 | **1 уровень surface** | 🔴 Критично | 3 уровня × 2 набора + elevated | M3: 5 уровней | 1 цвет |
| 4 | **5 текстовых стилей** | 🟠 Высоко | 11 стилей (Large Title → Caption 2) | 10-15 стилей | 5 стилей |
| 5 | **Нет lineHeight/letterSpacing/fontFamily** | 🟠 Высоко | leading + tracking + SF Pro/NY | line-height, letter-spacing, font-family | Захардкожено / нет |
| 6 | **Нет материалов (blur/opacity)** | 🟠 Высоко | Liquid Glass + 4 standard materials | backdrop-filter + opacity | Нет |
| 7 | **13 плоских цветов** | 🟠 Высоко | ~100+ семантических ролей | Palette + semantic tokens | 13 flat |
| 8 | **Нет RTL/i18n** | 🟡 Средне | Обязательно (leading/trailing) | logical properties (inline-start/end) | Нет |
| 9 | **3 радиуса, 2 не используются** | 🟡 Средне | 10 уровней (M3) / corner-radius per component | None → Full шкала | sm(4), md(8), lg(12) |
| 10 | **Нет адаптивных spacing/sizing** | 🟠 Высоко | Size Classes (compact/regular) | clamp(), media queries | Фикс Int |
| 11 | **Нет state tokens (hover/focus/pressed)** | 🟡 Средне | Vibrancy, opacity-based | State layer tokens | Нет |
| 12 | **Нет animation tokens** | 🟡 Средне | Liquid Glass transitions defined | duration, easing | Нет |
| 13 | **Нет z-index/elevation** | 🟡 Средне | Base/Elevated фоны | z-stack tokens | Нет |
| 14 | **Шрифт не масштабируется** | 🔴 Критично | Dynamic Type: xSmall → AX5 | rem + user prefs | Фикс px |
| 15 | **Нет accent color** | 🟠 Высоко | Ключевой концепт для Liquid Glass | Brand color system | Нет |

---

## 11. Рекомендации по приоритетам

### P0 — Фундаментальный сдвиг (делать первым)

#### 1. Перейти с `Int` пикселей на относительные единицы

**Web (React/Next.js):**
```css
/* Типоргафика */
--font-body: clamp(1rem, 0.95rem + 0.25vw, 1.125rem);
--font-h1:   clamp(1.8rem, 1.5rem + 1.5vw, 2.5rem);

/* Spacing */
--space-sm: clamp(0.5rem, 0.45rem + 0.25vw, 0.625rem);
--space-md: clamp(1rem, 0.91rem + 0.45vw, 1.25rem);

/* Sizing */
--btn-height-md: clamp(2.5rem, 2.3rem + 0.5vw, 2.75rem);
```

**Compose (Android):**
```kotlin
// sp для текста — масштабируется с Accessibility settings
Text(fontSize = 16.sp) // 16sp → может стать 20sp при Large Text
// dp для spacing — density-independent pixels
Modifier.padding(16.dp)
```

**KMP-токены** должны хранить **семантические значения**, а не конкретные пиксели:
```kotlin
data class TypographyTokens(
    val bodySizeRem: Double = 1.0,
    val bodyWeight: Int = 400,
    val bodyLineHeight: Double = 1.5,
    val bodyLetterSpacing: Double = 0.0,
    // ...
)
```

Web-резолвер преобразует `rem` → `clamp()`:
```typescript
function toFluidSize(rem: number, growFactor: number = 0.25): string {
  const min = rem;
  const max = rem * 1.15;
  return `clamp(${min}rem, ${min - 0.05}rem + ${growFactor}vw, ${max}rem)`;
}
```

Compose-резолвер преобразует `rem` → `sp`/`dp`:
```kotlin
fun Double.toSp() = (this * 16).sp // 1rem = 16sp default
fun Double.toDp() = (this * 16).dp
```

#### 2. Структурировать цвета по ролям

```kotlin
data class ColorTokens(
    // Accent / Brand
    val accent: String,             // primary action, Liquid Glass tint
    val onAccent: String,           // text on accent bg

    // Surfaces (иерархия глубины)
    val surfacePrimary: String,     // main bg
    val surfaceSecondary: String,   // card / grouped bg
    val surfaceTertiary: String,    // nested element bg
    val surfaceElevated: String,    // modal / popover bg

    // Labels / Text (иерархия важности)
    val labelPrimary: String,       // primary text
    val labelSecondary: String,     // secondary text
    val labelTertiary: String,      // placeholder, hints
    val labelQuaternary: String,    // watermarks, disabled

    // Semantic (status)
    val success: String,
    val warning: String,
    val error: String,
    val info: String,

    // Utility
    val separator: String,
    val link: String,
    val fill: String,               // control backgrounds

    // Destructive
    val destructive: String,
    val onDestructive: String,
)
```

#### 3. Добавить тёмную тему

```kotlin
object ThemePresets {
    val light = ColorTokens(
        accent = "#007AFF",          // iOS blue
        surfacePrimary = "#FFFFFF",
        surfaceSecondary = "#F2F2F7",
        labelPrimary = "#000000",
        labelSecondary = "#3C3C43", // 60% opacity
        // ...
    )
    val dark = ColorTokens(
        accent = "#0A84FF",          // brighter in dark
        surfacePrimary = "#000000",
        surfaceSecondary = "#1C1C1E",
        labelPrimary = "#FFFFFF",
        labelSecondary = "#EBEBF5", // 60% opacity
        // ...
    )
}
```

### P1 — Типографика и материалы

#### 4. Расширить текстовые стили до 11 (по Apple HIG)

```kotlin
data class TextStyle(
    val sizeRem: Double,
    val weight: Int,
    val lineHeight: Double,     // множитель (1.2, 1.4, 1.5)
    val letterSpacing: Double,  // em
)

data class TypographyTokens(
    val largeTitle: TextStyle,  // 2.125rem, 400, 1.2, -0.02
    val title1: TextStyle,      // 1.75rem, 400, 1.2, -0.01
    val title2: TextStyle,      // 1.375rem, 400, 1.25, 0
    val title3: TextStyle,      // 1.25rem, 400, 1.25, 0
    val headline: TextStyle,    // 1.0625rem, 600, 1.3, 0
    val body: TextStyle,        // 1.0625rem, 400, 1.4, 0
    val callout: TextStyle,     // 1rem, 400, 1.35, 0
    val subhead: TextStyle,     // 0.9375rem, 400, 1.35, 0
    val footnote: TextStyle,    // 0.8125rem, 400, 1.4, 0
    val caption1: TextStyle,    // 0.75rem, 400, 1.35, 0
    val caption2: TextStyle,    // 0.6875rem, 500, 1.2, 0
)
```

#### 5. Добавить материалы (blur/opacity)

```kotlin
data class MaterialTokens(
    val blurSm: Int = 8,
    val blurMd: Int = 16,
    val blurLg: Int = 24,
    val blurXl: Int = 40,

    val opacityUltraThin: Double = 0.5,
    val opacityThin: Double = 0.65,
    val opacityRegular: Double = 0.8,
    val opacityThick: Double = 0.9,

    val liquidGlassRegularOpacity: Double = 0.82,
    val liquidGlassClearOpacity: Double = 0.45,
)
```

### P2 — Адаптивность

#### 6. Responsive spacing через множители

```kotlin
enum class SizeClass { Compact, Regular, Expanded }

data class ResponsiveSpacing(
    val baseRem: Double,        // 1.0rem для md
    val compactScale: Double,   // 0.75
    val expandedScale: Double,  // 1.25
)
```

Web: `clamp(base × compactScale, base, base × expandedScale)` с vw
Compose: выбор по `WindowSizeClass`

#### 7. Шкала радиусов (10 уровней по M3 + Apple)

```kotlin
data class RadiusTokens(
    val none: Int = 0,
    val xs: Int = 4,
    val sm: Int = 8,
    val md: Int = 12,
    val lg: Int = 16,
    val xl: Int = 20,
    val xxl: Int = 28,
    val xxxl: Int = 48,
    val full: Int = 9999,  // pill shape
)
```

### P3 — RTL, State tokens, Animation

#### 8. RTL

Web: CSS logical properties (`margin-inline-start` вместо `margin-left`)
Compose: `Modifier.padding(start = X.dp)` уже RTL-aware
KMP: `LayoutDirection` enum, резолвер учитывает при генерации стилей

#### 9. State tokens (vibrancy)

```kotlin
data class StateTokens(
    val hoverOpacity: Double = 0.08,
    val focusOpacity: Double = 0.12,
    val pressedOpacity: Double = 0.16,
    val draggedOpacity: Double = 0.24,
    val disabledOpacity: Double = 0.38,
)
```

#### 10. Animation tokens

```kotlin
data class AnimationTokens(
    val durationFast: Int = 150,     // ms
    val durationNormal: Int = 300,
    val durationSlow: Int = 500,
    val easingDefault: String = "ease-in-out",
    val easingSpring: String = "cubic-bezier(0.34, 1.56, 0.64, 1)",
)
```

---

## Резюме: разница между "у нас" и "как надо"

```
ТЕКУЩЕЕ СОСТОЯНИЕ                      ЦЕЛЕВОЕ СОСТОЯНИЕ
══════════════════                     ══════════════════

13 flat hex colors                  →  ~30 semantic color roles × light/dark = 60+
5 text styles (Int px)              →  11 text styles (rem, weight, lineHeight, letterSpacing)
Fixed px sizes                      →  Fluid rem/clamp (web) + sp/dp (Compose)
1 surface color                     →  4 surface levels + elevated
No materials                        →  Blur, opacity, Liquid Glass tokens
No dark theme                       →  Light + Dark + Accessible presets
3 radius (2 unused)                 →  9 radius levels (none → full)
No RTL                              →  Logical properties + LayoutDirection
No adaptivity                       →  Size Classes + clamp() + responsive tokens
No state tokens                     →  hover/focus/pressed/disabled opacity
No animation                        →  duration + easing tokens
```

Главный фокус: **Apple-first дизайн** с чёрно-белой основой, минимальным использованием цвета, полупрозрачными материалами (Liquid Glass), адаптивной типографикой и полной поддержкой тёмной темы.

---

# Часть II — Переключение стилей, Material You, Ant Design

---

## 12. Google Material You (M3): система динамических цветов

### Что такое Material You

**Material You** (Material Design 3) — дизайн-система Google, ключевое нововведение которой — **Dynamic Color**: система, генерирующая полную цветовую схему из одного seed-цвета.

### Как работает генерация цветов

```
Source Color (1 цвет)
    ↓ Алгоритм (HCT color space)
5 Key Colors:
    ├── Primary
    ├── Secondary
    ├── Tertiary
    ├── Neutral
    └── Neutral Variant
    ↓
5 Tonal Palettes (по 13 тонов: 0,10,20,...90,95,98,99,100)
    ↓
26 Color Roles (light + dark автоматически)
    ↓
UI Elements (через токены)
```

### HCT Color Space

Material You использует собственное цветовое пространство **HCT** (Hue, Chroma, Tone):

| Компонент | Описание | Диапазон |
|---|---|---|
| **Hue** | Оттенок (красный, синий, зелёный...) | 0–360° (круговой) |
| **Chroma** | Насыщенность (серый → яркий) | 0–~120 |
| **Tone** | Светлота (чёрный → белый) | 0–100 |

**Ключевое преимущество HCT**: можно менять hue и chroma **не влияя на tone** (в отличие от HSL/RGB). Это гарантирует **контрастность** при любой палитре.

### 26 стандартных Color Roles

| Группа | Роли | Назначение |
|---|---|---|
| **Primary** | primary, onPrimary, primaryContainer, onPrimaryContainer | Главные акцентные элементы (FAB, основные кнопки) |
| **Secondary** | secondary, onSecondary, secondaryContainer, onSecondaryContainer | Менее заметные элементы (filter chips, tonal buttons) |
| **Tertiary** | tertiary, onTertiary, tertiaryContainer, onTertiaryContainer | Контрастные акценты (badges, input fields) |
| **Error** | error, onError, errorContainer, onErrorContainer | Ошибки (статические, не меняются от dynamic color) |
| **Surface** | surface, onSurface, onSurfaceVariant, surfaceContainerLowest/Low/Default/High/Highest | Фоны, 5 уровней глубины |
| **Outline** | outline, outlineVariant | Границы и разделители |

### 3 уровня контрастности

Material You автоматически поддерживает **3 уровня контраста**:
- **Standard** — дефолт, 3:1 для элементов
- **Medium** — минимум 3:1 для людей с пониженным зрением
- **High** — 7:1 для максимальной чёткости

### Источники динамического цвета

| Источник | Описание | Пример |
|---|---|---|
| **User-generated** | Цвет из обоев пользователя | Android Wallpaper → quantization → source color |
| **Content-based** | Цвет из контента приложения | Обложка альбома → цветовая схема плеера |
| **Hand-picked** | Выбран дизайнером вручную | Brand color → Material Theme Builder → полная схема |

### Что это значит для нас

```
MATERIAL YOU                           НАША СИСТЕМА
══════════════════                    ══════════════
1 seed color → 65+ цветов             13 захардкоженных hex
5 tonal palettes × 13 тонов          0 палитр
26 color roles × 2 themes            13 flat-цветов, 1 тема
3 уровня контраста                    0 (фикс)
HCT + алгоритмы генерации            Ручной подбор
```

**Вывод**: нам нужна не ручная подборка цветов, а **алгоритм генерации** палитры из seed color (по аналогии с Material Color Utilities).

---

## 13. Ant Design: трёхслойная токен-архитектура

### Архитектура Design Tokens в Ant Design

Ant Design (v5/v6) реализует **трёхслойную деривацию** токенов — это ключевая архитектура для понимания style switching:

```
Seed Tokens (семена — минимальный набор)
    ↓ Algorithm (defaultAlgorithm / darkAlgorithm / compactAlgorithm)
Map Tokens (градиентные — производные от seed)
    ↓ Aliasing
Alias Tokens (алиасы — семантические имена для компонентов)
    ↓
Component Tokens (переопределения для конкретных компонентов)
```

### Seed Tokens (≈20 значений — управляют всем)

| Seed Token | Тип | Дефолт | Что порождает |
|---|---|---|---|
| `colorPrimary` | color | #1677ff | 10+ оттенков primary (Bg, Border, Hover, Active, Text) |
| `colorSuccess` | color | #52c41a | 7 оттенков success |
| `colorWarning` | color | #faad14 | 7 оттенков warning |
| `colorError` | color | #ff4d4f | 7 оттенков error |
| `colorInfo` | color | #1677ff | 7 оттенков info |
| `colorTextBase` | color | #000 | 4 уровня text (primary → quaternary) |
| `colorBgBase` | color | #fff | Container, Elevated, Layout, Mask, Spotlight BG |
| `fontSize` | number | 14 | SM(12), LG(16), XL(20), Heading1-5 |
| `borderRadius` | number | 6 | XS(2), SM(4), LG(8), Outer(4) |
| `controlHeight` | number | 32 | SM(24), LG(40), XS(16) |
| `sizeUnit` | number | 4 | Вся размерная шкала (XS→XXL) |
| `sizeStep` | number | 4 | Шаг изменения (compact mode = 2) |
| `fontFamily` | string | system fonts | Основной шрифт |
| `lineWidth` | number | 1 | Bold(2), Focus(3) |
| `lineType` | string | solid | Тип бордера |
| `motion` | boolean | true | Включение/выключение анимаций |
| `wireframe` | boolean | false | Переключение в wireframe-стиль (V4 look) |

### Алгоритмы (ключ к style switching!)

```typescript
// Ant Design предоставляет 3 готовых алгоритма:
import { theme } from 'antd';
const { defaultAlgorithm, darkAlgorithm, compactAlgorithm } = theme;

// Алгоритмы КОМБИНИРУЮТСЯ:
const config = {
  algorithm: [darkAlgorithm, compactAlgorithm], // тёмная + компактная тема
};

// Алгоритм — это ФУНКЦИЯ:
// (seedTokens: SeedToken) => MapToken
```

**Алгоритм трансформирует seed tokens в map tokens** — именно здесь происходит "магия" стиля. Изменяя алгоритм, из тех же seed-значений получается совершенно другой визуал:

| Алгоритм | Что делает |
|---|---|
| `defaultAlgorithm` | Стандартная светлая тема |
| `darkAlgorithm` | Тёмная тема (инвертированные палитры) |
| `compactAlgorithm` | Уменьшенные размеры (`sizeStep: 2` вместо 4) |
| **Custom algorithm** | Любая кастомная трансформация! |

### Map Tokens (≈150+ значений — автогенерация)

Map tokens — это **полная палитра**, сгенерированная из seed:

| Категория | Примеры | Кол-во |
|---|---|---|
| Color gradients | colorPrimaryBg, colorPrimaryBgHover, colorPrimaryBorder, colorPrimaryHover, colorPrimaryActive, colorPrimaryText | ~50+ |
| Background levels | colorBgContainer, colorBgElevated, colorBgLayout, colorBgMask, colorBgSpotlight, colorBgBlur | 8+ |
| Text levels | colorText, colorTextSecondary, colorTextTertiary, colorTextQuaternary | 4 |
| Fill levels | colorFill, colorFillSecondary, colorFillTertiary, colorFillQuaternary | 4 |
| Border | colorBorder, colorBorderSecondary, colorBorderDisabled | 3 |
| Sizing | size(16), sizeXS(8), sizeSM(12), sizeMD(20), sizeLG(24), sizeXL(32), sizeXXL(48) | 8 |
| Typography | fontSize, fontSizeSM(12), fontSizeLG(16), fontSizeXL(20), fontSizeHeading1-5 + все lineHeight | ~15 |
| Radius | borderRadius, borderRadiusXS(2), borderRadiusSM(4), borderRadiusLG(8), borderRadiusOuter(4) | 5 |
| Motion | motionDurationFast(0.1s), motionDurationMid(0.2s), motionDurationSlow(0.3s) | 3+ |
| Control heights | controlHeightXS(16), controlHeightSM(24), controlHeight(32), controlHeightLG(40) | 4 |

### Alias Tokens (семантические — для конкретных сценариев)

```
colorBgContainerDisabled → rgba(0,0,0,0.04)
colorTextDisabled        → rgba(0,0,0,0.25)
colorTextDescription     → rgba(0,0,0,0.45)
colorTextHeading         → rgba(0,0,0,0.88)
colorTextPlaceholder     → rgba(0,0,0,0.25)
colorIcon                → rgba(0,0,0,0.45)
boxShadow                → 0 6px 16px 0 rgba(0,0,0,0.08), ...
boxShadowSecondary       → ...
boxShadowTertiary        → ...
```

### Component Tokens (переопределение на уровне компонента)

```typescript
const theme = {
  components: {
    Button: {
      colorPrimary: '#00b96b',
      borderColorDisabled: '#d9d9d9',
      algorithm: true,  // включить алгоритм для генерации производных
    },
    Input: {
      controlHeight: 40,
    },
  },
};
```

### Dynamic Theme Switching

```typescript
// Переключение темы В РАНТАЙМЕ — одна строка:
<ConfigProvider theme={{ algorithm: isDark ? darkAlgorithm : defaultAlgorithm }}>
  <App />
</ConfigProvider>

// Вложенные темы (разные секции с разными стилями):
<ConfigProvider theme={globalTheme}>
  <Header />
  <ConfigProvider theme={sidebarTheme}> {/* наследует и переопределяет */}
    <Sidebar />
  </ConfigProvider>
</ConfigProvider>
```

---

## 14. Material Design 3: архитектура токенов

### Трёхклассовая система токенов M3

M3 определяет **3 класса токенов**, аналогичных Ant Design, но с другой номенклатурой:

```
Reference Tokens (ref) — все доступные значения
    ↓
System Tokens (sys) — решения с учётом контекста (тема/платформа)
    ↓
Component Tokens (comp) — конкретные элементы компонента
```

### Reference Tokens (ref)

Содержат **все** возможные значения палитры — это "словарь" цветов:

```
md.ref.palette.primary0    → #000000
md.ref.palette.primary10   → #21005D
md.ref.palette.primary20   → #381E72
md.ref.palette.primary30   → #4F378B
md.ref.palette.primary40   → #6750A4
md.ref.palette.primary50   → #7F67BE
...
md.ref.palette.primary100  → #FFFFFF

md.ref.typeface.plain      → Roboto
md.ref.typeface.brand      → Product Sans
```

### System Tokens (sys)

Определяют **назначение** reference token в конкретном контексте:

```
md.sys.color.primary         → md.ref.palette.primary40  (light)
md.sys.color.primary         → md.ref.palette.primary80  (dark)

md.sys.color.on-primary      → md.ref.palette.primary100 (light)
md.sys.color.on-primary      → md.ref.palette.primary20  (dark)

md.sys.color.surface         → md.ref.palette.neutral99  (light)
md.sys.color.surface         → md.ref.palette.neutral10  (dark)
```

**Контексты** меняют, на какой reference token указывает system token:
- Light / Dark theme
- Standard / Medium / High contrast
- Compact / Regular layout density

### Component Tokens (comp)

Привязка к конкретным элементам компонента:

```
md.comp.filled-button.container.color      → md.sys.color.primary
md.comp.filled-button.label-text.color     → md.sys.color.on-primary
md.comp.filled-button.container.shape      → md.sys.shape.corner.full
md.comp.filled-button.container.height     → 40dp

md.comp.fab.primary.container.color        → md.sys.color.primary-container
md.comp.fab.primary.icon.color             → md.sys.color.on-primary-container
```

---

## 15. Сравнительный анализ: наша система vs Ant Design vs Material You

### Архитектура токенов

| Аспект | Наша система | Ant Design v6 | Material You (M3) |
|---|---|---|---|
| **Слои токенов** | 1 (flat) | 3 (Seed → Map → Alias) + Component | 3 (Reference → System → Component) |
| **Seed/Core значений** | 13 hex + 10 Int | ~20 seed tokens | 1 source color + 5 key colors |
| **Генерируемые токены** | 0 | ~150+ map → ~80+ alias | 65+ tonal → 26+ roles |
| **Алгоритмы** | Нет | 3 встроенных + custom | HCT + 3 алгоритма (user/content/custom) |
| **Dark theme** | Нет | Одна строка (darkAlgorithm) | Автоматически (из tonal palettes) |
| **Compact mode** | Нет | compactAlgorithm (sizeStep: 2) | Density tokens |
| **Component override** | Нет | Полный (per-component tokens) | Полный (comp tokens) |
| **Runtime switching** | Нет | Да (ConfigProvider) | Да (через theme overlay) |
| **Nested themes** | Нет | Да (вложенные ConfigProvider) | Да (через theme scoping) |
| **Контрастность** | Фиксированная | Нет встроенной | 3 уровня (standard/medium/high) |
| **CSS Variables** | Нет | Да (cssVar: true) | Да (design tokens → CSS custom properties) |
| **Design tool** | Нет | Theme Editor (web) | Material Theme Builder (Figma) |

### Генерация цветов

| Аспект | Наша система | Ant Design | Material You |
|---|---|---|---|
| **Input** | 13 hex вручную | ~6 seed colors | 1 source color |
| **Палитра** | Нет | 10 оттенков на цвет (алгоритм палитры) | 13 тонов × 5 палитр = 65+ |
| **Фоны** | 1 (`surface: #FFFFFF`) | 6+ (Container, Elevated, Layout, Mask, Blur, Spotlight) | 7+ (surface + 5 container levels + dim/bright) |
| **Текст** | 1 (`textPrimary`) | 4 уровня (primary → quaternary) | 2 (onSurface, onSurfaceVariant) |
| **Состояния** | Нет | Bg, BgHover, Border, BorderHover, Hover, Active, Text, TextHover, TextActive на каждый цвет | State layer opacity + color pairs |
| **Accessible contrast** | Нет | Нет | Встроено (tone-based pairing) |

### Размерная система

| Аспект | Наша система | Ant Design | Material You |
|---|---|---|---|
| **Единицы** | `Int` (px) | `number` (px) | `dp`/`sp` (Android), CSS vars (web) |
| **Шкала** | 5 spacings, 6 sizings | 8+ sizes (XS→XXL), 4 control heights | Density-independent, window size classes |
| **Компактность** | Нет | `compactAlgorithm` (sizeStep: 2) | Dense layout tokens |
| **Типографика** | 5 стилей × 2 атрибута | 6 heading sizes + SM/LG + lineHeight | Type scale (display/headline/title/body/label × S/M/L) |
| **Breakpoints** | Нет | 6 (XS:480 → XXXL:1920) | Window size classes |

---

## 16. Переключаемые стили: Apple / Illustration / Cartoon / Glass

### Концепция Style Presets

По аналогии с тем, как Ant Design позволяет переключать `algorithm`, нашему UIKit нужна система **Style Presets** — готовых комбинаций токенов, определяющих визуальный стиль:

```
StylePreset = {
    name: string,
    seedTokens: SeedTokens,          // базовые значения
    algorithm: Algorithm,             // функция трансформации
    componentOverrides: Map<Component, ComponentTokens>,  // per-component
}
```

### Примеры Style Presets

#### 1. Apple Style (по умолчанию)

```kotlin
val appleStyle = StylePreset(
    name = "Apple",
    seed = SeedTokens(
        colorPrimary = "#007AFF",     // iOS Blue
        colorBgBase = "#FFFFFF",
        fontSize = 17.0,               // Body default in pt
        borderRadius = 12.0,           // Rounded corners
        controlHeight = 44.0,          // iOS touch target
        fontFamily = "SF Pro, -apple-system, system-ui",
    ),
    algorithm = AppleAlgorithm,        // Liquid Glass materials, SF Pro tracking
    traits = StyleTraits(
        usesBlur = true,               // backdrop-filter
        usesVibrancy = true,           // glass effect
        cornerStyle = CornerStyle.Continuous,  // squircle
        shadowStyle = ShadowStyle.Subtle,
        surfaceOpacity = 0.82,         // Liquid Glass
        colorStrategy = ColorStrategy.Monochrome, // только accent для primary action
        borderStyle = BorderStyle.None,
    ),
)
```

#### 2. Material Style

```kotlin
val materialStyle = StylePreset(
    name = "Material",
    seed = SeedTokens(
        colorPrimary = "#6750A4",     // M3 Purple
        colorBgBase = "#FFFBFE",
        fontSize = 14.0,
        borderRadius = 20.0,          // M3 uses large radius (full for FAB)
        controlHeight = 40.0,
        fontFamily = "Roboto, sans-serif",
    ),
    algorithm = MaterialAlgorithm,    // HCT tonal palette generation
    traits = StyleTraits(
        usesBlur = false,
        usesVibrancy = false,
        cornerStyle = CornerStyle.Rounded,  // standard border-radius
        shadowStyle = ShadowStyle.Elevation, // M3 elevation system
        surfaceOpacity = 1.0,          // solid surfaces
        colorStrategy = ColorStrategy.Semantic, // primary/secondary/tertiary
        borderStyle = BorderStyle.Outlined,
    ),
)
```

#### 3. Illustration Style

```kotlin
val illustrationStyle = StylePreset(
    name = "Illustration",
    seed = SeedTokens(
        colorPrimary = "#FF6B35",     // Тёплый оранжевый
        colorBgBase = "#FFF8F0",       // Кремовый фон
        fontSize = 16.0,
        borderRadius = 16.0,
        controlHeight = 44.0,
        fontFamily = "Nunito, Comic Neue, cursive",
    ),
    algorithm = IllustrationAlgorithm, // Мягкие пастельные палитры
    traits = StyleTraits(
        usesBlur = false,
        usesVibrancy = false,
        cornerStyle = CornerStyle.SuperRounded, // ещё более скруглённые
        shadowStyle = ShadowStyle.Playful,      // цветные тени
        surfaceOpacity = 1.0,
        colorStrategy = ColorStrategy.Warm,      // тёплые пастельные тона
        borderStyle = BorderStyle.Thick,         // толстые 2px бордеры
    ),
)
```

#### 4. Cartoon Style

```kotlin
val cartoonStyle = StylePreset(
    name = "Cartoon",
    seed = SeedTokens(
        colorPrimary = "#FFD700",     // Яркий жёлтый
        colorBgBase = "#FFFFFF",
        fontSize = 18.0,              // Крупнее
        borderRadius = 24.0,          // Очень скруглённые
        controlHeight = 48.0,         // Крупные контролы
        fontFamily = "Fredoka One, Baloo 2, cursive",
    ),
    algorithm = CartoonAlgorithm,     // Яркие насыщенные цвета, высокий контраст
    traits = StyleTraits(
        usesBlur = false,
        usesVibrancy = false,
        cornerStyle = CornerStyle.Blob,          // органические формы
        shadowStyle = ShadowStyle.HardDrop,      // чёткие тени без размытия
        surfaceOpacity = 1.0,
        colorStrategy = ColorStrategy.HighSaturation, // яркие чистые цвета
        borderStyle = BorderStyle.Bold,          // жирный 3px бордер
    ),
)
```

#### 5. Glass Style (Glassmorphism)

```kotlin
val glassStyle = StylePreset(
    name = "Glass",
    seed = SeedTokens(
        colorPrimary = "#8B5CF6",     // Фиолетовый
        colorBgBase = "#0F172A",       // Тёмно-синий
        fontSize = 15.0,
        borderRadius = 16.0,
        controlHeight = 42.0,
        fontFamily = "Inter, -apple-system, sans-serif",
    ),
    algorithm = GlassAlgorithm,       // Полупрозрачные поверхности, light borders
    traits = StyleTraits(
        usesBlur = true,               // Сильный backdrop-filter: blur(20px)
        usesVibrancy = true,
        cornerStyle = CornerStyle.Rounded,
        shadowStyle = ShadowStyle.Glow,          // свечение вместо тени
        surfaceOpacity = 0.15,         // почти прозрачные surfaces
        colorStrategy = ColorStrategy.Gradient,   // градиенты
        borderStyle = BorderStyle.Subtle,        // тонкий rgba(255,255,255,0.1)
    ),
)
```

### Как стиль влияет на компоненты

Пример: один и тот же Button с разными стилями:

| Свойство | Apple | Material | Illustration | Cartoon | Glass |
|---|---|---|---|---|---|
| Background | Liquid Glass / accent | Primary (solid) | Пастельный fill | Яркий solid | rgba(255,255,255,0.1) |
| Border | None | Outlined variant | 2px solid | 3px bold black | 1px rgba(255,255,255,0.2) |
| Radius | 12px squircle | 20px rounded | 16px super-round | 24px blob | 16px rounded |
| Shadow | Subtle | Elevation 1 | Colored soft | Hard drop | Glow effect |
| Font | SF Pro 17pt | Roboto 14pt | Nunito 16pt | Fredoka 18pt | Inter 15pt |
| Hover | Opacity reduce | State layer | Color shift | Scale + bounce | Glow increase |

---

## 17. Архитектура Theme Engine для KMP UIKit

### Общая схема

```
┌─────────────────────────────────────────────────────────┐
│                    Theme Engine (KMP)                     │
│                                                           │
│  ┌──────────┐    ┌──────────────┐    ┌────────────────┐  │
│  │   Seed   │───▶│  Algorithm   │───▶│  Resolved      │  │
│  │  Tokens  │    │  (Function)  │    │  Tokens        │  │
│  └──────────┘    └──────────────┘    └────────┬───────┘  │
│                                                │          │
│  ┌──────────┐                         ┌───────▼───────┐  │
│  │Component │                         │  Merged       │  │
│  │Overrides │────────────────────────▶│  Theme        │  │
│  └──────────┘                         └───────┬───────┘  │
│                                                │          │
│  ┌──────────┐                         ┌───────▼───────┐  │
│  │  Style   │                         │  Platform     │  │
│  │  Traits  │────────────────────────▶│  Resolver     │  │
│  └──────────┘                         └───────┬───────┘  │
│                                                │          │
└────────────────────────────────────────────────┼──────────┘
                                                 │
                    ┌────────────────────────────┼────────────────┐
                    │                            │                │
              ┌─────▼─────┐              ┌──────▼──────┐  ┌─────▼─────┐
              │  React    │              │  Compose    │  │  SwiftUI  │
              │  CSS Vars │              │  Material   │  │  Native   │
              │  clamp()  │              │  Theme      │  │  Theme    │
              └───────────┘              └─────────────┘  └───────────┘
```

### Ключевые интерфейсы

```kotlin
// === Seed Tokens (минимальный набор, из которого генерируется всё) ===
@Serializable
data class SeedTokens(
    // Colors
    val colorPrimary: String = "#007AFF",
    val colorSuccess: String = "#34C759",
    val colorWarning: String = "#FF9500",
    val colorError: String = "#FF3B30",
    val colorInfo: String = "#5AC8FA",
    val colorBgBase: String = "#FFFFFF",
    val colorTextBase: String = "#000000",

    // Typography
    val fontSize: Double = 16.0,          // base font (rem)
    val fontFamily: String = "system-ui",
    val typeScale: Double = 1.25,         // Major Third scale ratio

    // Spacing & Sizing
    val sizeUnit: Double = 0.25,          // rem (= 4px at 16px base)
    val sizeStep: Int = 4,                // step multiplier
    val controlHeight: Double = 2.5,      // rem

    // Shape
    val borderRadius: Double = 0.75,      // rem
    val borderWidth: Double = 0.0625,     // rem (= 1px)
    val cornerStyle: String = "rounded",  // rounded | continuous | blob

    // Motion
    val motionEnabled: Boolean = true,
    val motionDurationBase: Int = 200,    // ms

    // Features
    val wireframe: Boolean = false,       // V4-style wireframe mode
)

// === Algorithm: функция трансформации Seed → Resolved ===
interface ThemeAlgorithm {
    fun resolve(seed: SeedTokens): ResolvedTokens
}

// === Style Traits: визуальные характеристики стиля ===
@Serializable
data class StyleTraits(
    val usesBlur: Boolean = false,
    val usesVibrancy: Boolean = false,
    val usesGradients: Boolean = false,
    val cornerStyle: String = "rounded",      // rounded | continuous | blob
    val shadowStyle: String = "subtle",        // subtle | elevation | glow | hard-drop | none
    val surfaceOpacity: Double = 1.0,
    val colorStrategy: String = "semantic",    // semantic | monochrome | warm | high-saturation | gradient
    val borderStyle: String = "none",          // none | subtle | outlined | thick | bold
)

// === Style Preset: полная конфигурация стиля ===
@Serializable
data class StylePreset(
    val name: String,
    val seed: SeedTokens,
    val algorithmId: String,               // "apple" | "material" | "illustration" | "cartoon" | "glass" | "custom"
    val traits: StyleTraits,
    val componentOverrides: Map<String, Map<String, String>> = emptyMap(),
)

// === Resolved Tokens: полная раскрытая палитра ===
@Serializable
data class ResolvedTokens(
    val colors: ResolvedColors,
    val typography: ResolvedTypography,
    val spacing: ResolvedSpacing,
    val sizing: ResolvedSizing,
    val radius: ResolvedRadius,
    val shadows: ResolvedShadows,
    val materials: ResolvedMaterials,
    val motion: ResolvedMotion,
    val breakpoints: ResolvedBreakpoints,
)
```

### Переключение тем в рантайме

```kotlin
// === Theme Manager (KMP) ===
class ThemeManager {
    private val algorithms: Map<String, ThemeAlgorithm> = mapOf(
        "apple" to AppleAlgorithm(),
        "material" to MaterialAlgorithm(),
        "illustration" to IllustrationAlgorithm(),
        "cartoon" to CartoonAlgorithm(),
        "glass" to GlassAlgorithm(),
    )

    fun resolve(preset: StylePreset): ResolvedTokens {
        val algorithm = algorithms[preset.algorithmId]
            ?: algorithms["apple"]!!
        val base = algorithm.resolve(preset.seed)
        return applyComponentOverrides(base, preset.componentOverrides)
    }

    // Регистрировать кастомные алгоритмы:
    fun registerAlgorithm(id: String, algorithm: ThemeAlgorithm)
}
```

```typescript
// === React: переключение стиля одной строкой ===
const [style, setStyle] = useState<StylePreset>(applePreset);

// Переключить на Glass:
setStyle(glassPreset);

// В корне:
<ThemeProvider tokens={themeManager.resolve(style)}>
  <App />
</ThemeProvider>
```

```kotlin
// === Compose: через CompositionLocal ===
val LocalTheme = compositionLocalOf { themeManager.resolve(applePreset) }

@Composable
fun App(style: StylePreset) {
    CompositionLocalProvider(LocalTheme provides themeManager.resolve(style)) {
        MainScreen()
    }
}
```

---

## 18. Алгоритмы генерации тем

### Что алгоритм делает

Алгоритм — **чистая функция**: `SeedTokens → ResolvedTokens`. Он отвечает за:

1. **Генерацию палитры** из seed color (10–13 оттенков)
2. **Назначение ролей** (какой оттенок идёт на какую роль)
3. **Расчёт производных** (hover = darken 10%, disabled = opacity 38%)
4. **Адаптацию для dark theme** (инверсия тонов)
5. **Расчёт типографической шкалы** от base fontSize и typeScale ratio
6. **Расчёт spacing шкалы** от sizeUnit и sizeStep

### Примеры алгоритмов

#### Apple Algorithm

```
Input: colorPrimary = "#007AFF"
Logic:
  1. Цветовая схема: монохромная, основной цвет = accent
  2. Surfaces: белые/чёрные (не тонированные)
  3. Labels: 4 уровня через opacity (100%, 60%, 30%, 18%)
  4. Backgrounds: systemBackground (#FFFFFF), secondary (#F2F2F7), tertiary (#FFFFFF)
  5. Materials: blur + opacity (Liquid Glass)
  6. Tracking (letter spacing) рассчитывается per font-size (Apple spec)
  7. Corner radius: continuous (squircle), не circular
```

#### Material Algorithm (HCT-based)

```
Input: colorPrimary = "#6750A4"
Logic:
  1. Конвертировать в HCT space
  2. Сгенерировать 5 key colors (hue shift + chroma adjustment)
  3. Для каждого key color: 13-тоновая палитра (tone 0→100)
  4. Назначить тоны на 26 ролей (Primary40→primary, Primary100→onPrimary)
  5. Dark theme: зеркалить тоны (Primary80→primary, Primary20→onPrimary)
  6. Surface tint: лёгкий оттенок primary на elevated surfaces
  7. State layers: overlay с opacity (hover: 8%, focus: 12%, pressed: 16%)
```

#### Glass Algorithm

```
Input: colorPrimary = "#8B5CF6"
Logic:
  1. Base surfaces: очень тёмные (#0F172A), surface opacity = 0.1–0.2
  2. Акцентные цвета: яркие, с gradient variants
  3. Borders: rgba(255,255,255, 0.1–0.2)
  4. Shadows → glow (box-shadow с цветом primary и расширенным spread)
  5. Blur: backdrop-filter: blur(16px–32px)
  6. Text: белый с opacity levels
  7. Elevated surfaces: чуть более прозрачные, усиленный blur
```

---

## 19. Component Tokens: переопределение по компонентам

### Зачем нужны Component Tokens

Глобальные токены задают **общий визуал**, но отдельные компоненты иногда нуждаются в отклонениях. Обе лидирующие системы (Ant Design и M3) поддерживают **per-component overrides**.

### Пример: Button

```kotlin
// Глобальный стиль говорит:
// borderRadius = 12px, controlHeight = 44px

// Но для маленьких кнопок в навигации хочется другое:
val componentOverrides = mapOf(
    "Button" to mapOf(
        "borderRadius" to "8",       // меньший radius для SM
        "fontWeight" to "600",       // жирнее
    ),
    "Input" to mapOf(
        "controlHeight" to "48",     // выше стандартный input
        "borderWidth" to "2",        // толще бордер для фокуса
    ),
)
```

### Три уровня специфичности (как CSS cascade)

```
1. Seed Tokens (самый общий)
   └── "colorPrimary": "#007AFF"

2. Resolved/Algorithm Tokens (для всех компонентов)
   └── "colorPrimaryHover": "#0066CC"
   └── "colorPrimaryBg": "#E5F0FF"

3. Component Tokens (для конкретного компонента)
   └── Button.colorPrimary: "#00B96B"  ← переопределение только для Button
   └── Button.borderRadius: "999"      ← pill shape только для Button
```

Это даёт **максимальную гибкость** без дублирования: 90% стилей идёт из глобальных токенов, 10% — компонентные переопределения.

---

## 20. Обновлённая сводная таблица проблем

| # | Проблема | Серьёзность | Ant Design | Material You | У нас |
|---|---|---|---|---|---|
| 1 | **Все размеры в пикселях (Int)** | 🔴 Критично | px (но sizeUnit/sizeStep) | dp/sp | Int px |
| 2 | **Нет тёмной темы** | 🔴 Критично | darkAlgorithm | Авто из tonal palettes | Нет |
| 3 | **1 уровень surface** | 🔴 Критично | 6+ bg-уровней | 7+ surface roles | 1 цвет |
| 4 | **Нет алгоритмов/генерации** | 🔴 Критично | Seed → 150+ tokens авто | 1 color → 65+ авто | Всё вручную |
| 5 | **5 текстовых стилей** | 🟠 Высоко | 7+ (fontSize SM/LG/XL + H1-5) | 15 (5 categories × 3) | 5 стилей |
| 6 | **Нет lineHeight/letterSpacing** | 🟠 Высоко | Авто lineHeight из fontSize | Полный type scale | Захардкожено |
| 7 | **Нет материалов (blur/opacity)** | 🟠 Высоко | colorBgBlur | Нет встроенных | Нет |
| 8 | **13 плоских цветов** | 🔴 Критично | ~20 seed → 150+ resolved | 1 seed → 65+ tonal | 13 flat |
| 9 | **Нет component tokens** | 🟠 Высоко | Полный per-component | comp.* tokens | Нет |
| 10 | **Нет style/theme switching** | 🔴 Критично | ConfigProvider + algorithm | Dynamic color | Нет |
| 11 | **Нет nested themes** | 🟡 Средне | Вложенные ConfigProvider | Theme overlay | Нет |
| 12 | **Нет state tokens** | 🟡 Средне | Hover/Active/Disabled на каждый цвет | State layer opacity | Нет |
| 13 | **Нет breakpoints** | 🟠 Высоко | 6 breakpoints | Window size classes | Нет |
| 14 | **Шрифт не масштабируется** | 🔴 Критично | fontSize → авто SM/LG/H1-5 | Type scale | Фикс px |
| 15 | **Нет accent color** | 🟠 Высоко | colorPrimary seed | Dynamic primary | Нет |
| 16 | **Нет wireframe/compact mode** | 🟡 Средне | wireframe + compactAlgorithm | Density tokens | Нет |
| 17 | **Нет CSS variables** | 🟡 Средне | cssVar: true | CSS custom properties | Inline px |
| 18 | **Нет shadow tokens** | 🟡 Средне | 3 уровня boxShadow | Elevation system | Нет |
| 19 | **Нет seed → palette алгоритма** | 🔴 Критично | Встроенная генерация палитр | HCT + MCU | Нет |

---

## 21. Итоговая архитектура: Unified Theme System

### Целевая архитектура (Phase 0 → Phase 3)

#### Phase 0: Foundation (первый шаг)

```
Задача: Перейти от flat Int to structured SeedTokens + Algorithm
Результат: Один AppleAlgorithm, переход на rem, light + dark тема

SeedTokens (≈15 значений)
    ↓ AppleAlgorithm
ResolvedTokens (≈60+ значений)
    ↓ Platform Resolver
Web: CSS Custom Properties    |    Compose: MaterialTheme
```

#### Phase 1: Multi-Theme (добавить style switching)

```
Задача: Добавить MaterialAlgorithm + GlassAlgorithm
Результат: 3 встроенных стиля, runtime switching

StylePreset.apple     → AppleAlgorithm    → ResolvedTokens → UI
StylePreset.material  → MaterialAlgorithm → ResolvedTokens → UI
StylePreset.glass     → GlassAlgorithm    → ResolvedTokens → UI

ThemeManager.setPreset(preset) → re-render
```

#### Phase 2: Component Tokens + Nested Themes

```
Задача: Per-component overrides + вложенные темы
Результат: Компаненты кастомизируются независимо, секции страницы могут иметь разные темы

Global Theme (apple)
├── Header (inherit)
├── Sidebar (glass override — другой стиль для навигации)
│   └── Search (inherit from sidebar)
├── Content (inherit)
└── Footer (dark override — тёмная тема)
```

#### Phase 3: Custom Algorithms + Design Tool

```
Задача: Пользователи создают свои стили
Результат: API для регистрации кастомных алгоритмов, визуальный Theme Editor

themeManager.registerAlgorithm("brand", BrandAlgorithm())
themeManager.registerPreset(brandPreset)
```

### Минимальный набор Seed Tokens для максимальной кастомизации

На основе анализа Ant Design (~20 seeds) и Material You (1 seed + 5 keys), оптимальный набор для нашего UIKit:

```kotlin
@Serializable
data class SeedTokens(
    // === Colors (7 seeds → 60+ resolved) ===
    val colorPrimary: String,          // Brand/accent color
    val colorSuccess: String,          // Positive actions
    val colorWarning: String,          // Caution states
    val colorError: String,            // Error/destructive
    val colorInfo: String,             // Informational
    val colorBgBase: String,           // Background base (white/dark)
    val colorTextBase: String,         // Text base (black/white)

    // === Typography (4 seeds → 15+ resolved styles) ===
    val fontSize: Double,              // Base font size (rem)
    val typeScale: Double,             // Scale ratio (1.2 Minor Third / 1.25 Major Third)
    val fontFamily: String,            // Primary font
    val fontFamilyCode: String,        // Monospace font

    // === Spacing & Sizing (3 seeds → 20+ resolved) ===
    val sizeUnit: Double,              // Base unit (rem, default 0.25)
    val sizeStep: Int,                 // Step multiplier (4 default, 2 compact)
    val controlHeight: Double,         // Base control height (rem)

    // === Shape (2 seeds → 8+ resolved) ===
    val borderRadius: Double,          // Base radius (rem)
    val borderWidth: Double,           // Base border width (rem)

    // === Motion (2 seeds → 6+ resolved) ===
    val motionEnabled: Boolean,
    val motionDuration: Int,           // Base duration (ms)
)
```

**18 seed tokens** → алгоритм генерирует **100+ resolved tokens** → из них строится весь UI.

### Обзор трансформаций

```
colorPrimary: "#007AFF"
    → colorPrimaryBg:       lighten 90%     // фон для subtle elements
    → colorPrimaryBgHover:  lighten 80%     // hover на subtle
    → colorPrimaryBorder:   lighten 50%     // бордер акцентных элементов
    → colorPrimaryHover:    darken 10%      // hover на primary кнопках
    → colorPrimaryActive:   darken 20%      // pressed
    → colorPrimaryText:     same            // текст акцентного цвета

fontSize: 1.0 (rem), typeScale: 1.25
    → caption2:  1.0 / 1.25³ = 0.512rem
    → caption1:  1.0 / 1.25² = 0.64rem
    → footnote:  1.0 / 1.25  = 0.8rem
    → body:      1.0rem
    → callout:   1.0 × 1.25  = 1.25rem
    → title3:    1.0 × 1.25² = 1.5625rem
    → title1:    1.0 × 1.25³ = 1.953rem
    → largeTitle:1.0 × 1.25⁴ = 2.441rem

borderRadius: 0.75rem
    → radiusNone: 0
    → radiusXs:   0.75 / 3    = 0.25rem
    → radiusSm:   0.75 / 1.5  = 0.5rem
    → radiusMd:   0.75rem
    → radiusLg:   0.75 × 1.5  = 1.125rem
    → radiusXl:   0.75 × 2    = 1.5rem
    → radiusFull: 999rem       // pill
```

### Итоговое сравнение

```
ТЕКУЩЕЕ СОСТОЯНИЕ                      ЦЕЛЕВОЕ СОСТОЯНИЕ (Unified Theme System)
══════════════════                     ════════════════════════════════════════

13 flat hex colors                  →  18 seed tokens → Algorithm → 100+ resolved
5 text styles (Int px)              →  Type scale от fontSize + ratio → 11+ styles
Fixed px sizes                      →  rem + clamp (web), sp/dp (Compose)
No theme switching                  →  StylePreset + Algorithm → runtime switching
1 surface color                     →  6+ surface levels (Container/Elevated/Layout/Mask)
No dark theme                       →  Авто dark через алгоритм инверсии тонов
No materials                        →  StyleTraits → blur/opacity/vibrancy per стиль
3 radius (2 unused)                 →  borderRadius seed → 7 уровней (none→full)
No component tokens                 →  Per-component overrides (как Ant Design)
No nested themes                    →  Вложенные ThemeProvider (секции с другим стилем)
No state tokens                     →  Hover/focus/pressed/disabled автогенерация
No animation tokens                 →  motionDuration seed → fast/normal/slow
No style presets                    →  5+ встроенных стилей (Apple/Material/Glass/Illustration/Cartoon)
No design tool                      →  Theme Editor для создания кастомных стилей
No algorithm                        →  Pluggable алгоритмы (Apple, Material HCT, Glass, custom)
No breakpoints                      →  6 breakpoints + Size Classes
No CSS variables                    →  CSS Custom Properties + clamp()
Manual color picking                →  Seed → алгоритмическая генерация палитр с контролем контраста
```

### Приоритеты реализации

| Приоритет | Что | Результат | Усилия |
|---|---|---|---|
| **P0** | SeedTokens + AppleAlgorithm + rem | Структурированная система, dark theme бесплатно | Высокие |
| **P0** | CSS custom properties на React | Все стили через --token-name, не inline px | Средние |
| **P1** | Theme switching (apple↔material↔glass) | 3 стиля переключаются в runtime | Средние |
| **P1** | Расширенная типографика (11 стилей + lineHeight/tracking) | Apple HIG compliant | Средние |
| **P2** | Component tokens | Per-component overrides | Средние |
| **P2** | Nested themes | Секции с разным стилем | Низкие |
| **P2** | Illustration + Cartoon presets | Расширенная коллекция стилей | Низкие |
| **P3** | Custom algorithm API | Пользователи создают стили | Высокие |
| **P3** | Visual Theme Editor | UI для создания тем | Высокие |
| **P3** | Accessible contrast levels (Standard/Medium/High) | A11y compliant | Средние |
