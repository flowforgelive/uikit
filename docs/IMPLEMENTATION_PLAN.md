# UIKit: Пошаговый план реализации

> Полный план трансформации текущей flat-token системы в Apple-first дизайн-систему
> с переключаемыми стилями (Apple / Material / Illustration / Cartoon / Glass).
>
> Каждый шаг — конкретные файлы, before/after, зависимости. Ничего не упущено.

---

## Оглавление

- [UIKit: Пошаговый план реализации](#uikit-пошаговый-план-реализации)
  - [Оглавление](#оглавление)
  - [Текущее состояние](#текущее-состояние)
    - [Файлы и их проблемы](#файлы-и-их-проблемы)
    - [Ключевые числа](#ключевые-числа)
  - [Целевая архитектура](#целевая-архитектура)
  - [Phase 0: Seed Tokens + Algorithm Interface](#phase-0-seed-tokens--algorithm-interface)
    - [Шаг 0.1: SeedTokens](#шаг-01-seedtokens)
    - [Шаг 0.2: ResolvedTokens](#шаг-02-resolvedtokens)
      - [0.2.1: ResolvedColors](#021-resolvedcolors)
      - [0.2.2: ResolvedTypography](#022-resolvedtypography)
      - [0.2.3: ResolvedSpacing](#023-resolvedspacing)
      - [0.2.4: ResolvedSizing](#024-resolvedsizing)
      - [0.2.5: ResolvedRadius](#025-resolvedradius)
      - [0.2.6: ResolvedShadows](#026-resolvedshadows)
      - [0.2.7: ResolvedMaterials](#027-resolvedmaterials)
      - [0.2.8: ResolvedMotion](#028-resolvedmotion)
      - [0.2.9: ResolvedBreakpoints](#029-resolvedbreakpoints)
      - [0.2.10: ResolvedTokens (контейнер)](#0210-resolvedtokens-контейнер)
    - [Шаг 0.3: ThemeAlgorithm Interface](#шаг-03-themealgorithm-interface)
    - [Шаг 0.4: StyleTraits](#шаг-04-styletraits)
    - [Шаг 0.5: StylePreset](#шаг-05-stylepreset)
    - [Шаг 0.6: ThemeManager](#шаг-06-thememanager)
    - [Файловая структура после Phase 0](#файловая-структура-после-phase-0)
  - [Phase 1: Apple Algorithm — полноценный Apple-дизайн](#phase-1-apple-algorithm--полноценный-apple-дизайн)
    - [Шаг 1.1: Палитра цветов Apple HIG](#шаг-11-палитра-цветов-apple-hig)
    - [Шаг 1.2: Типографика Apple (11 стилей + tracking)](#шаг-12-типографика-apple-11-стилей--tracking)
    - [Шаг 1.3: Spacing, Sizing, Radius Apple](#шаг-13-spacing-sizing-radius-apple)
    - [Шаг 1.4: Shadows + Materials (Liquid Glass)](#шаг-14-shadows--materials-liquid-glass)
    - [Шаг 1.5: State Tokens (hover/focus/pressed/disabled)](#шаг-15-state-tokens-hoverfocuspresseddisabled)
    - [Шаг 1.6: Dark Theme Apple](#шаг-16-dark-theme-apple)
    - [Шаг 1.7: Motion Tokens](#шаг-17-motion-tokens)
  - [Phase 2: Миграция компонентов на ResolvedTokens](#phase-2-миграция-компонентов-на-resolvedtokens)
    - [Шаг 2.1: ButtonStyleResolver → ResolvedTokens](#шаг-21-buttonstyleresolver--resolvedtokens)
    - [Шаг 2.2: TextBlockStyleResolver → ResolvedTokens](#шаг-22-textblockstyleresolver--resolvedtokens)
    - [Шаг 2.3: React — CSS Custom Properties + rem](#шаг-23-react--css-custom-properties--rem)
    - [Шаг 2.4: React — ThemeProvider переход](#шаг-24-react--themeprovider-переход)
    - [Шаг 2.5: Compose — UIKitTheme переход](#шаг-25-compose--uikittheme-переход)
    - [Шаг 2.6: Обратная совместимость (migration path)](#шаг-26-обратная-совместимость-migration-path)
  - [Phase 3: Style Switching — Apple ↔ Material ↔ Glass](#phase-3-style-switching--apple--material--glass)
    - [Шаг 3.1: MaterialAlgorithm](#шаг-31-materialalgorithm)
    - [Шаг 3.2: GlassAlgorithm](#шаг-32-glassalgorithm)
    - [Шаг 3.3: Runtime Switching (React)](#шаг-33-runtime-switching-react)
    - [Шаг 3.4: Runtime Switching (Compose)](#шаг-34-runtime-switching-compose)
  - [Phase 4: Illustration + Cartoon + Custom стили](#phase-4-illustration--cartoon--custom-стили)
    - [Шаг 4.1: IllustrationAlgorithm](#шаг-41-illustrationalgorithm)
    - [Шаг 4.2: CartoonAlgorithm](#шаг-42-cartoonalgorithm)
    - [Шаг 4.3: Custom Algorithm API](#шаг-43-custom-algorithm-api)
  - [Phase 5: Component Tokens + Nested Themes](#phase-5-component-tokens--nested-themes)
    - [Шаг 5.1: ComponentTokens per-component](#шаг-51-componenttokens-per-component)
    - [Шаг 5.2: Nested Themes](#шаг-52-nested-themes)
  - [Phase 6: Responsive + Accessibility](#phase-6-responsive--accessibility)
    - [Шаг 6.1: Breakpoints](#шаг-61-breakpoints)
    - [Шаг 6.2: Fluid Typography (clamp)](#шаг-62-fluid-typography-clamp)
    - [Шаг 6.3: Contrast Levels](#шаг-63-contrast-levels)
  - [Карта файлов](#карта-файлов)
    - [Новые файлы (создать)](#новые-файлы-создать)
    - [Существующие файлы (изменить)](#существующие-файлы-изменить)
    - [Файлы для удаления (после полной миграции)](#файлы-для-удаления-после-полной-миграции)
  - [Порядок выполнения и зависимости](#порядок-выполнения-и-зависимости)
  - [Итого: что получим](#итого-что-получим)

---

## Текущее состояние

### Файлы и их проблемы

| Файл | Что делает сейчас | Проблема |
|---|---|---|
| `tokens/ColorTokens.kt` | 13 flat hex-строк | Нет палитры, нет semantic roles, нет dark, нет state variations |
| `tokens/TypographyTokens.kt` | 5 стилей × (size: Int + weight: Int) | Нет lineHeight, letterSpacing, 5 стилей мало (Apple HIG = 11), пиксели |
| `tokens/SpacingTokens.kt` | 5 Int (xs:4 → xl:32) | Пиксели, нет связи с sizeUnit, нет margin tokens |
| `tokens/RadiusTokens.kt` | 3 Int (sm:4, md:8, lg:12) | Мало уровней (нет none, xs, xl, full/pill), пиксели |
| `tokens/SizingTokens.kt` | 6 Int (button/icon × sm/md/lg) | Привязаны к конкретным компонентам, пиксели |
| `tokens/DesignTokens.kt` | Container со всеми токенами + Default | Default = generic синий (#3B82F6), не Apple |
| `ButtonStyleResolver.kt` | Config + Tokens → ResolvedStyle | Хардкод `"transparent"`, всегда `radius.md`, нет hover/focus tokens |
| `TextBlockStyleResolver.kt` | Config + Tokens → ResolvedStyle | lineHeight = size × magic number, нет letterSpacing |
| `ButtonView.tsx` | CSS custom properties → inline | `${value}px` — пиксели, нет CSS variables, нет hover styles |
| `TextBlockView.tsx` | Inline styles | `${value}px` — пиксели, нет clamp() |
| `useDesignTokens.tsx` | Context Provider для DesignTokens | Нет style switching, нет dark mode detection |
| `UIKitTheme.kt` | CompositionLocal для DesignTokens | Нет style switching |

### Ключевые числа

```
Сейчас:  13 цветов, 10 Int typography, 5 spacings, 3 radii, 6 sizings = 37 токенов
Цель:    18 seeds → Algorithm → 100+ resolved tokens + component overrides
```

---

## Целевая архитектура

```
┌─────────────────────────────────────────────────────────────────────┐
│                        StylePreset                                   │
│  ┌──────────┐  ┌──────────────┐  ┌────────────┐  ┌──────────────┐  │
│  │   Seed   │  │  Algorithm   │  │   Style    │  │  Component   │  │
│  │  Tokens  │  │     ID       │  │   Traits   │  │  Overrides   │  │
│  │ (18 val) │  │ ("apple")    │  │(blur,shape)│  │ (optional)   │  │
│  └────┬─────┘  └──────┬───────┘  └─────┬──────┘  └──────┬───────┘  │
└───────┼────────────────┼────────────────┼────────────────┼──────────┘
        │                │                │                │
        ▼                ▼                │                │
┌────────────────────────────────┐        │                │
│      ThemeManager.resolve()     │◀───────┘                │
│  algorithm.resolve(seed)        │                         │
│  → ResolvedTokens (100+ values) │◀────────────────────────┘
│  + merge componentOverrides     │
└──────────┬──────────────────────┘
           │
     ┌─────┴─────────────────────────────┐
     │         ResolvedTokens             │
     │  .colors   (30+ semantic colors)   │
     │  .typography (11 styles × 4 attrs) │
     │  .spacing    (8+ levels)           │
     │  .sizing     (generic, not comp)   │
     │  .radius     (7 levels)            │
     │  .shadows    (4 levels)            │
     │  .materials  (blur, vibrancy)      │
     │  .motion     (3 durations)         │
     │  .breakpoints (6 values)           │
     └─────┬─────────────┬───────────────┘
           │             │
     ┌─────▼─────┐ ┌────▼──────┐
     │   React   │ │  Compose  │
     │ CSS vars  │ │ Material  │
     │  clamp()  │ │  Theme    │
     │   rem     │ │   dp/sp   │
     └───────────┘ └───────────┘
```

---

## Phase 0: Seed Tokens + Algorithm Interface

> Цель: создать новую инфраструктуру **рядом** со старой, чтобы можно было мигрировать постепенно.

### Шаг 0.1: SeedTokens

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/SeedTokens.kt`

```kotlin
package com.uikit.theme

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * Минимальный набор значений, из которых алгоритм генерирует всю тему.
 * По аналогии с Ant Design Seed Tokens (~20 значений → 150+ resolved).
 */
@JsExport
@Serializable
data class SeedTokens(
    // ═══════ Colors (7 seeds → 60+ resolved) ═══════
    val colorPrimary: String = "#007AFF",        // Apple iOS Blue
    val colorSuccess: String = "#34C759",        // Apple Green
    val colorWarning: String = "#FF9500",        // Apple Orange
    val colorError: String = "#FF3B30",          // Apple Red
    val colorInfo: String = "#5AC8FA",           // Apple Teal
    val colorBgBase: String = "#FFFFFF",         // Фон (white = light, #000 = dark)
    val colorTextBase: String = "#000000",       // Текст (black = light, #fff = dark)

    // ═══════ Typography (4 seeds → 11+ resolved styles) ═══════
    val fontSize: Double = 17.0,                 // Base body size (Apple = 17pt)
    val typeScale: Double = 1.176,               // Apple-like (20/17 ≈ 1.176)
    val fontFamily: String = "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif",
    val fontFamilyMono: String = "'SF Mono', 'Menlo', 'Monaco', monospace",

    // ═══════ Spacing & Sizing (3 seeds → 20+ resolved) ═══════
    val sizeUnit: Double = 4.0,                  // Базовая единица (4px grid)
    val sizeStep: Int = 4,                       // Множитель шага
    val controlHeight: Double = 44.0,            // Apple min touch target

    // ═══════ Shape (2 seeds → 7+ resolved) ═══════
    val borderRadius: Double = 12.0,             // Apple default corner radius
    val borderWidth: Double = 1.0,               // Стандартная ширина бордера

    // ═══════ Motion (2 seeds → 6+ resolved) ═══════
    val motionEnabled: Boolean = true,
    val motionDuration: Int = 250,               // Apple default 250ms

    // ═══════ Features ═══════
    val wireframe: Boolean = false,              // Debug wireframe mode
)
```

**Почему именно эти поля:**
- 7 цветов: достаточно для генерации полной палитры (как Ant Design из colorPrimary генерирует 10+ оттенков)
- 4 типографики: `fontSize` + `typeScale` = вся шкала рассчитывается автоматически
- 3 размера: `sizeUnit` + `sizeStep` + `controlHeight` = вся размерная сетка
- 2 shape: `borderRadius` + `borderWidth` = вся форма
- 2 motion: включение + базовая длительность = fast/mid/slow автоматически
- **Итого 18 полей** (вместо 37 текущих flat-токенов) управляют всем

**Зависимости:** Нет — это новый файл, не трогает существующий код.

---

### Шаг 0.2: ResolvedTokens

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/` — пакет с resolved-токенами.

#### 0.2.1: ResolvedColors

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedColors.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ResolvedColors(
    // ═══════ Primary (accent) ═══════
    val primary: String,              // Основной акцент (#007AFF)
    val primaryHover: String,         // Hover (#0066CC)
    val primaryActive: String,        // Pressed (#004999)
    val primaryBg: String,            // Subtle bg (#E5F2FF)
    val primaryBgHover: String,       // Subtle bg hover (#CCE5FF)
    val primaryBorder: String,        // Border accent (#66B2FF)
    val primaryText: String,          // Text в цвете primary

    // ═══════ Success ═══════
    val success: String,
    val successBg: String,
    val successBorder: String,
    val successText: String,

    // ═══════ Warning ═══════
    val warning: String,
    val warningBg: String,
    val warningBorder: String,
    val warningText: String,

    // ═══════ Error (Danger) ═══════
    val error: String,
    val errorBg: String,
    val errorBorder: String,
    val errorText: String,

    // ═══════ Info ═══════
    val info: String,
    val infoBg: String,
    val infoBorder: String,
    val infoText: String,

    // ═══════ Surface (фоны — 5 уровней как M3) ═══════
    val surface: String,              // Основной фон (#FFFFFF)
    val surfaceSecondary: String,     // Вторичный фон (#F2F2F7 — Apple SystemGroupedBackground)
    val surfaceTertiary: String,      // Третичный фон (#FFFFFF — Apple SecondarySystemGroupedBackground)
    val surfaceElevated: String,      // Popup/modal/card
    val surfaceOverlay: String,       // Overlay mask (rgba)
    val surfaceHover: String,         // Hover state

    // ═══════ Text (4 уровня — Apple Labels) ═══════
    val textPrimary: String,          // label     — 100% opacity (#000000)
    val textSecondary: String,        // secondary — 60% (#3C3C4399)
    val textTertiary: String,         // tertiary  — 30% (#3C3C434D)
    val textQuaternary: String,       // quaternary — 18% (#3C3C432E)
    val textOnPrimary: String,        // Text на primary bg (#FFFFFF)
    val textOnError: String,          // Text на error bg (#FFFFFF)

    // ═══════ Border ═══════
    val border: String,               // Основной бордер (#C6C6C8 Apple separator)
    val borderSecondary: String,      // Менее заметный (#E5E5EA Apple opaque separator)
    val borderFocus: String,          // Focus ring

    // ═══════ Fill (для заливки иконок, badge и т.д.) ═══════
    val fill: String,                 // 20% opacity
    val fillSecondary: String,        // 16% opacity
    val fillTertiary: String,         // 12% opacity

    // ═══════ Disabled (computed) ═══════
    val surfaceDisabled: String,
    val textDisabled: String,
    val borderDisabled: String,
)
```

#### 0.2.2: ResolvedTypography

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedTypography.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * Один Typography style = size + weight + lineHeight + letterSpacing.
 * Apple HIG определяет 11 основных стилей.
 */
@JsExport
@Serializable
data class TextStyle(
    val fontSize: Double,          // В логических единицах (pt/rem)
    val fontWeight: Int,           // 100–900
    val lineHeight: Double,        // В логических единицах
    val letterSpacing: Double,     // В em (0.0 = normal, отрицательные = Apple tracking)
)

@JsExport
@Serializable
data class ResolvedTypography(
    // ═══════ Apple HIG Type Scale (11 стилей) ═══════
    val largeTitle: TextStyle,    // 34pt, Bold,   41pt LH, +0.37 tracking
    val title1: TextStyle,        // 28pt, Bold,   34pt LH, +0.36 tracking
    val title2: TextStyle,        // 22pt, Bold,   28pt LH, +0.35 tracking
    val title3: TextStyle,        // 20pt, Semibold, 25pt LH, +0.38 tracking
    val headline: TextStyle,      // 17pt, Semibold, 22pt LH, -0.41 tracking
    val body: TextStyle,          // 17pt, Regular, 22pt LH, -0.41 tracking
    val callout: TextStyle,       // 16pt, Regular, 21pt LH, -0.32 tracking
    val subheadline: TextStyle,   // 15pt, Regular, 20pt LH, -0.24 tracking
    val footnote: TextStyle,      // 13pt, Regular, 18pt LH, -0.08 tracking
    val caption1: TextStyle,      // 12pt, Regular, 16pt LH, +0.00 tracking
    val caption2: TextStyle,      // 11pt, Regular, 13pt LH, +0.07 tracking

    // ═══════ Шрифты ═══════
    val fontFamily: String,
    val fontFamilyMono: String,
)
```

#### 0.2.3: ResolvedSpacing

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedSpacing.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ResolvedSpacing(
    val none: Double,      // 0
    val xxs: Double,       // 2   (sizeUnit × 0.5)
    val xs: Double,        // 4   (sizeUnit × 1)
    val sm: Double,        // 8   (sizeUnit × 2)
    val md: Double,        // 16  (sizeUnit × 4)
    val lg: Double,        // 24  (sizeUnit × 6)
    val xl: Double,        // 32  (sizeUnit × 8)
    val xxl: Double,       // 48  (sizeUnit × 12)
    val xxxl: Double,      // 64  (sizeUnit × 16)
)
```

#### 0.2.4: ResolvedSizing

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedSizing.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ResolvedSizing(
    // ═══════ Control Heights (кнопки, инпуты и пр.) ═══════
    val controlXs: Double,     // 24  (compact pill)
    val controlSm: Double,     // 32  (small button)
    val controlMd: Double,     // 44  (Apple default touch target)
    val controlLg: Double,     // 52  (large button)

    // ═══════ Icon Sizes ═══════
    val iconXs: Double,        // 12
    val iconSm: Double,        // 16
    val iconMd: Double,        // 20
    val iconLg: Double,        // 24
    val iconXl: Double,        // 32
)
```

#### 0.2.5: ResolvedRadius

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedRadius.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ResolvedRadius(
    val none: Double,          // 0
    val xs: Double,            // 4   (borderRadius / 3)
    val sm: Double,            // 8   (borderRadius / 1.5)
    val md: Double,            // 12  (borderRadius)
    val lg: Double,            // 16  (borderRadius × 1.333)
    val xl: Double,            // 20  (borderRadius × 1.667)
    val full: Double,          // 9999 (pill)
)
```

#### 0.2.6: ResolvedShadows

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedShadows.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ShadowValue(
    val x: Double,
    val y: Double,
    val blur: Double,
    val spread: Double,
    val color: String,
)

@JsExport
@Serializable
data class ResolvedShadows(
    val none: List<ShadowValue>,       // Нет тени
    val sm: List<ShadowValue>,         // Subtle (cards)
    val md: List<ShadowValue>,         // Medium (dropdowns, popovers)
    val lg: List<ShadowValue>,         // Large (modals, dialogs)
    val xl: List<ShadowValue>,         // XL (toasts, floating)
    val focus: List<ShadowValue>,      // Focus ring glow
)
```

#### 0.2.7: ResolvedMaterials

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedMaterials.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * Apple-specialties: Liquid Glass, vibrancy, blur materials.
 * В стилях без blur (Material, Cartoon) — все disabled.
 */
@JsExport
@Serializable
data class MaterialValue(
    val enabled: Boolean,          // Включен ли material-эффект
    val bgColor: String,           // Фоновый цвет (с opacity)
    val blurRadius: Double,        // backdrop-filter: blur(Xpx), 0 = disabled
    val saturation: Double,        // backdrop-filter: saturate(X), 1.0 = normal
    val borderColor: String,       // Тонкий бордер для glass
    val borderWidth: Double,       // Ширина бордера
)

@JsExport
@Serializable
data class ResolvedMaterials(
    val regular: MaterialValue,    // Обычная поверхность (sidebar, toolbar)
    val thin: MaterialValue,       // Тонкий blur (tab bar)
    val thick: MaterialValue,      // Насыщенный blur (navigation bar)
    val overlay: MaterialValue,    // Overlay / mask
)
```

#### 0.2.8: ResolvedMotion

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedMotion.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ResolvedMotion(
    val enabled: Boolean,
    val durationFast: Int,         // 100ms (micro-interactions)
    val durationNormal: Int,       // 250ms (standard)
    val durationSlow: Int,         // 500ms (page transitions)
    val easing: String,            // CSS easing function (cubic-bezier)
    val easingEmphasized: String,  // For emphasized transitions
)
```

#### 0.2.9: ResolvedBreakpoints

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedBreakpoints.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ResolvedBreakpoints(
    val xs: Int,       // 0    (iPhone SE portrait)
    val sm: Int,       // 576  (iPhone landscape)
    val md: Int,       // 768  (iPad portrait)
    val lg: Int,       // 1024 (iPad landscape)
    val xl: Int,       // 1280 (Desktop)
    val xxl: Int,      // 1536 (Wide desktop)
)
```

#### 0.2.10: ResolvedTokens (контейнер)

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/resolved/ResolvedTokens.kt`

```kotlin
package com.uikit.theme.resolved

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * Полная раскрытая тема после прохождения SeedTokens через Algorithm.
 * Содержит 100+ конкретных значений, готовых к использованию компонентами.
 */
@JsExport
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

**Зависимости:** Зависит от шага 0.2.1–0.2.9.

---

### Шаг 0.3: ThemeAlgorithm Interface

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/ThemeAlgorithm.kt`

```kotlin
package com.uikit.theme

import com.uikit.theme.resolved.ResolvedTokens

/**
 * Алгоритм = чистая функция трансформации SeedTokens → ResolvedTokens.
 * Каждый стиль (Apple, Material, Glass, Cartoon, Illustration) имеет свой алгоритм.
 *
 * Аналог: Ant Design `theme.algorithm` (defaultAlgorithm, darkAlgorithm, compactAlgorithm).
 */
interface ThemeAlgorithm {
    /** Уникальный идентификатор алгоритма */
    val id: String

    /** Человекочитаемое имя */
    val displayName: String

    /** Генерация полной resolved палитры из seed values */
    fun resolve(seed: SeedTokens): ResolvedTokens

    /** Генерация dark-варианта. По умолчанию — инверсия светлого. */
    fun resolveDark(seed: SeedTokens): ResolvedTokens
}
```

> **Почему interface, а не @JsExport class?**
> `ThemeAlgorithm` — внутренний контракт. Он не экспортируется в JS напрямую. JS получает только результат — `ResolvedTokens`. Это позволяет реализовывать алгоритмы с `expect/actual` или pure Kotlin логикой.

**Зависимости:** Шаг 0.1 (SeedTokens) + Шаг 0.2.10 (ResolvedTokens).

---

### Шаг 0.4: StyleTraits

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/StyleTraits.kt`

```kotlin
package com.uikit.theme

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * Визуальные характеристики стиля, не зависящие от цветов.
 * Определяют "ощущение" стиля: blur? тени? форма углов? стратегия цвета?
 */
@JsExport
@Serializable
data class StyleTraits(
    // ═══════ Surface effects ═══════
    val usesBlur: Boolean = false,             // backdrop-filter: blur()
    val usesVibrancy: Boolean = false,         // В связке с blur — полупрозрачные поверхности
    val usesGradients: Boolean = false,        // Gradient backgrounds

    // ═══════ Shape ═══════
    val cornerStyle: String = "rounded",       // "rounded" | "continuous" (squircle) | "blob" | "sharp"
    val shadowStyle: String = "subtle",        // "subtle" | "elevation" | "glow" | "hard-drop" | "none"

    // ═══════ Surfaces ═══════
    val surfaceOpacity: Double = 1.0,          // 1.0 = solid, 0.1–0.3 = glass
    val colorStrategy: String = "monochrome",  // "monochrome" | "semantic" | "warm" | "high-saturation" | "gradient"

    // ═══════ Borders ═══════
    val borderStyle: String = "none",          // "none" | "subtle" | "outlined" | "thick" | "bold"
)
```

**Зависимости:** Нет — standalone data class.

---

### Шаг 0.5: StylePreset

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/StylePreset.kt`

```kotlin
package com.uikit.theme

import kotlin.js.JsExport
import kotlinx.serialization.Serializable

/**
 * Полная конфигурация стиля: seed tokens + какой алгоритм + визуальные traits.
 * Это то, что пользователь выбирает: "Apple", "Material", "Glass" и т.д.
 *
 * Аналог: <ConfigProvider theme={...}> в Ant Design.
 */
@JsExport
@Serializable
data class StylePreset(
    val name: String,                          // "Apple", "Material", "Glass", ...
    val algorithmId: String,                   // "apple", "material", "glass", ...
    val seed: SeedTokens,
    val traits: StyleTraits,
    val componentOverrides: Map<String, Map<String, String>> = emptyMap(),
)
```

**Зависимости:** Шаг 0.1 + Шаг 0.4.

---

### Шаг 0.6: ThemeManager

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/ThemeManager.kt`

```kotlin
package com.uikit.theme

import com.uikit.theme.resolved.ResolvedTokens
import kotlin.js.JsExport

/**
 * Центральная точка для разрешения темы.
 * Принимает StylePreset → возвращает ResolvedTokens.
 *
 * Регистрирует алгоритмы при создании, пользователь может добавить свои.
 */
@JsExport
class ThemeManager {

    private val algorithms = mutableMapOf<String, ThemeAlgorithm>()

    fun registerAlgorithm(algorithm: ThemeAlgorithm) {
        algorithms[algorithm.id] = algorithm
    }

    fun resolve(preset: StylePreset, isDark: Boolean = false): ResolvedTokens {
        val algorithm = algorithms[preset.algorithmId]
            ?: throw IllegalArgumentException("Unknown algorithm: ${preset.algorithmId}")
        return if (isDark) algorithm.resolveDark(preset.seed) else algorithm.resolve(preset.seed)
    }

    fun availableAlgorithms(): List<String> = algorithms.keys.toList()

    companion object {
        /**
         * Фабрика с предустановленными алгоритмами.
         * Вызывается при инициализации приложения.
         */
        fun createDefault(): ThemeManager {
            val manager = ThemeManager()
            // Алгоритмы регистрируются здесь по мере имплементации:
            // manager.registerAlgorithm(AppleAlgorithm())
            // manager.registerAlgorithm(MaterialAlgorithm())
            // manager.registerAlgorithm(GlassAlgorithm())
            return manager
        }
    }
}
```

**Зависимости:** Шаг 0.3 (ThemeAlgorithm) + Шаг 0.5 (StylePreset).

---

### Файловая структура после Phase 0

```
core/uikit/common/src/commonMain/kotlin/com/uikit/
├── theme/                              ← НОВЫЙ пакет
│   ├── SeedTokens.kt                  ← Шаг 0.1
│   ├── ThemeAlgorithm.kt              ← Шаг 0.3
│   ├── StyleTraits.kt                 ← Шаг 0.4
│   ├── StylePreset.kt                 ← Шаг 0.5
│   ├── ThemeManager.kt                ← Шаг 0.6
│   └── resolved/                       ← НОВЫЙ пакет
│       ├── ResolvedColors.kt          ← Шаг 0.2.1
│       ├── ResolvedTypography.kt      ← Шаг 0.2.2
│       ├── ResolvedSpacing.kt         ← Шаг 0.2.3
│       ├── ResolvedSizing.kt          ← Шаг 0.2.4
│       ├── ResolvedRadius.kt          ← Шаг 0.2.5
│       ├── ResolvedShadows.kt         ← Шаг 0.2.6
│       ├── ResolvedMaterials.kt       ← Шаг 0.2.7
│       ├── ResolvedMotion.kt          ← Шаг 0.2.8
│       ├── ResolvedBreakpoints.kt     ← Шаг 0.2.9
│       └── ResolvedTokens.kt          ← Шаг 0.2.10
├── tokens/                             ← СУЩЕСТВУЮЩИЙ (не трогать пока)
│   ├── ColorTokens.kt
│   ├── TypographyTokens.kt
│   ├── SpacingTokens.kt
│   ├── RadiusTokens.kt
│   ├── SizingTokens.kt
│   └── DesignTokens.kt
├── foundation/
│   └── Visibility.kt
└── components/
    └── atoms/
        ├── button/
        └── text/
```

> **Важно:** Старый пакет `tokens/` НЕ удаляется на Phase 0. Он продолжает работать. Миграция — в Phase 2.

---

## Phase 1: Apple Algorithm — полноценный Apple-дизайн

> Цель: реализовать `AppleAlgorithm` — первый алгоритм, создающий тему, соответствующую Apple HIG 2025/2026.

### Шаг 1.1: Палитра цветов Apple HIG

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/algorithms/AppleAlgorithm.kt`

Этот файл — **основной**. Он содержит всю логику генерации Apple-палитры из seed tokens.

```kotlin
package com.uikit.theme.algorithms

import com.uikit.theme.SeedTokens
import com.uikit.theme.ThemeAlgorithm
import com.uikit.theme.resolved.*

class AppleAlgorithm : ThemeAlgorithm {
    override val id = "apple"
    override val displayName = "Apple"

    override fun resolve(seed: SeedTokens): ResolvedTokens {
        return ResolvedTokens(
            colors = resolveColors(seed, isDark = false),
            typography = resolveTypography(seed),
            spacing = resolveSpacing(seed),
            sizing = resolveSizing(seed),
            radius = resolveRadius(seed),
            shadows = resolveShadows(isDark = false),
            materials = resolveMaterials(isDark = false),
            motion = resolveMotion(seed),
            breakpoints = resolveBreakpoints(),
        )
    }

    override fun resolveDark(seed: SeedTokens): ResolvedTokens {
        return ResolvedTokens(
            colors = resolveColors(seed, isDark = true),
            typography = resolveTypography(seed),
            spacing = resolveSpacing(seed),
            sizing = resolveSizing(seed),
            radius = resolveRadius(seed),
            shadows = resolveShadows(isDark = true),
            materials = resolveMaterials(isDark = true),
            motion = resolveMotion(seed),
            breakpoints = resolveBreakpoints(),
        )
    }

    // ... (private methods для каждой группы — описаны в шагах 1.1–1.7)
}
```

**Алгоритм генерации цветов** (внутри `resolveColors`):

```kotlin
private fun resolveColors(seed: SeedTokens, isDark: Boolean): ResolvedColors {
    // Apple HIG: монохромная стратегия
    // Фоны — чистые белые/чёрные (не тонированные как в Material)
    // Accent color — используется минимально (только primary actions)
    // Labels — через opacity от textBase

    if (isDark) {
        return ResolvedColors(
            // Primary
            primary = seed.colorPrimary,          // #0A84FF (elevated dark)
            primaryHover = lighten(seed.colorPrimary, 0.1),
            primaryActive = darken(seed.colorPrimary, 0.1),
            primaryBg = "rgba(10,132,255,0.15)",
            primaryBgHover = "rgba(10,132,255,0.25)",
            primaryBorder = "rgba(10,132,255,0.4)",
            primaryText = seed.colorPrimary,

            // Success
            success = seed.colorSuccess,
            successBg = "rgba(52,199,89,0.15)",
            successBorder = "rgba(52,199,89,0.4)",
            successText = seed.colorSuccess,

            // Warning
            warning = seed.colorWarning,
            warningBg = "rgba(255,149,0,0.15)",
            warningBorder = "rgba(255,149,0,0.4)",
            warningText = seed.colorWarning,

            // Error
            error = seed.colorError,
            errorBg = "rgba(255,59,48,0.15)",
            errorBorder = "rgba(255,59,48,0.4)",
            errorText = seed.colorError,

            // Info
            info = seed.colorInfo,
            infoBg = "rgba(90,200,250,0.15)",
            infoBorder = "rgba(90,200,250,0.4)",
            infoText = seed.colorInfo,

            // Surface (Apple Dark)
            surface = "#000000",                    // systemBackground
            surfaceSecondary = "#1C1C1E",           // secondarySystemBackground
            surfaceTertiary = "#2C2C2E",            // tertiarySystemBackground
            surfaceElevated = "#1C1C1E",            // elevated
            surfaceOverlay = "rgba(0,0,0,0.4)",
            surfaceHover = "rgba(255,255,255,0.05)",

            // Text (Apple Dark Labels)
            textPrimary = "#FFFFFF",
            textSecondary = "rgba(235,235,245,0.6)",
            textTertiary = "rgba(235,235,245,0.3)",
            textQuaternary = "rgba(235,235,245,0.18)",
            textOnPrimary = "#FFFFFF",
            textOnError = "#FFFFFF",

            // Border (Apple Dark Separators)
            border = "rgba(84,84,88,0.65)",
            borderSecondary = "rgba(56,56,58,1.0)",
            borderFocus = seed.colorPrimary,

            // Fill (Apple Dark)
            fill = "rgba(120,120,128,0.36)",
            fillSecondary = "rgba(120,120,128,0.32)",
            fillTertiary = "rgba(118,118,128,0.24)",

            // Disabled
            surfaceDisabled = "rgba(120,120,128,0.18)",
            textDisabled = "rgba(235,235,245,0.3)",
            borderDisabled = "rgba(84,84,88,0.3)",
        )
    }

    return ResolvedColors(
        // Primary (Apple Light)
        primary = seed.colorPrimary,              // #007AFF
        primaryHover = darken(seed.colorPrimary, 0.1),
        primaryActive = darken(seed.colorPrimary, 0.2),
        primaryBg = withOpacity(seed.colorPrimary, 0.1),
        primaryBgHover = withOpacity(seed.colorPrimary, 0.15),
        primaryBorder = withOpacity(seed.colorPrimary, 0.3),
        primaryText = seed.colorPrimary,

        // Success
        success = seed.colorSuccess,
        successBg = withOpacity(seed.colorSuccess, 0.1),
        successBorder = withOpacity(seed.colorSuccess, 0.3),
        successText = darken(seed.colorSuccess, 0.15),

        // Warning
        warning = seed.colorWarning,
        warningBg = withOpacity(seed.colorWarning, 0.1),
        warningBorder = withOpacity(seed.colorWarning, 0.3),
        warningText = darken(seed.colorWarning, 0.15),

        // Error
        error = seed.colorError,
        errorBg = withOpacity(seed.colorError, 0.1),
        errorBorder = withOpacity(seed.colorError, 0.3),
        errorText = darken(seed.colorError, 0.15),

        // Info
        info = seed.colorInfo,
        infoBg = withOpacity(seed.colorInfo, 0.1),
        infoBorder = withOpacity(seed.colorInfo, 0.3),
        infoText = darken(seed.colorInfo, 0.15),

        // Surface (Apple Light)
        surface = "#FFFFFF",                        // systemBackground
        surfaceSecondary = "#F2F2F7",               // secondarySystemGroupedBackground
        surfaceTertiary = "#FFFFFF",                // tertiarySystemGroupedBackground
        surfaceElevated = "#FFFFFF",                // elevated
        surfaceOverlay = "rgba(0,0,0,0.4)",
        surfaceHover = "rgba(0,0,0,0.03)",

        // Text (Apple Labels Light)
        textPrimary = "#000000",
        textSecondary = "rgba(60,60,67,0.6)",       // #3C3C43 + 60%
        textTertiary = "rgba(60,60,67,0.3)",        // #3C3C43 + 30%
        textQuaternary = "rgba(60,60,67,0.18)",     // #3C3C43 + 18%
        textOnPrimary = "#FFFFFF",
        textOnError = "#FFFFFF",

        // Border (Apple Separators Light)
        border = "rgba(60,60,67,0.29)",             // separator
        borderSecondary = "rgba(60,60,67,0.12)",    // opaque separator
        borderFocus = seed.colorPrimary,

        // Fill (Apple Light)
        fill = "rgba(120,120,128,0.2)",
        fillSecondary = "rgba(120,120,128,0.16)",
        fillTertiary = "rgba(118,118,128,0.12)",

        // Disabled
        surfaceDisabled = "rgba(120,120,128,0.12)",
        textDisabled = "rgba(60,60,67,0.3)",
        borderDisabled = "rgba(60,60,67,0.12)",
    )
}

// Вспомогательные функции (в пакете theme/util/):
// fun darken(hex: String, amount: Double): String
// fun lighten(hex: String, amount: Double): String
// fun withOpacity(hex: String, opacity: Double): String
```

**Вспомогательные цветовые функции:**

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/util/ColorUtil.kt`

```kotlin
package com.uikit.theme.util

/**
 * Утилиты для работы с цветами в алгоритмах.
 * Работают с hex-строками (#RRGGBB).
 */
object ColorUtil {
    /** Затемнить hex-цвет на amount (0.0–1.0) */
    fun darken(hex: String, amount: Double): String {
        val (r, g, b) = parseHex(hex)
        return toHex(
            (r * (1 - amount)).toInt().coerceIn(0, 255),
            (g * (1 - amount)).toInt().coerceIn(0, 255),
            (b * (1 - amount)).toInt().coerceIn(0, 255),
        )
    }

    /** Осветлить hex-цвет на amount (0.0–1.0) */
    fun lighten(hex: String, amount: Double): String {
        val (r, g, b) = parseHex(hex)
        return toHex(
            (r + (255 - r) * amount).toInt().coerceIn(0, 255),
            (g + (255 - g) * amount).toInt().coerceIn(0, 255),
            (b + (255 - b) * amount).toInt().coerceIn(0, 255),
        )
    }

    /** Вернуть rgba() строку с заданной opacity */
    fun withOpacity(hex: String, opacity: Double): String {
        val (r, g, b) = parseHex(hex)
        return "rgba($r,$g,$b,$opacity)"
    }

    private fun parseHex(hex: String): Triple<Int, Int, Int> {
        val h = hex.removePrefix("#")
        return Triple(
            h.substring(0, 2).toInt(16),
            h.substring(2, 4).toInt(16),
            h.substring(4, 6).toInt(16),
        )
    }

    private fun toHex(r: Int, g: Int, b: Int): String =
        "#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}"
}
```

---

### Шаг 1.2: Типографика Apple (11 стилей + tracking)

**Логика** (внутри `AppleAlgorithm`):

```kotlin
private fun resolveTypography(seed: SeedTokens): ResolvedTypography {
    // Apple HIG определяет точные значения для каждого стиля.
    // typeScale используется только для custom алгоритмов.
    // Для Apple — хардкод по спецификации.
    return ResolvedTypography(
        largeTitle = TextStyle(fontSize = 34.0, fontWeight = 700, lineHeight = 41.0, letterSpacing = 0.37),
        title1     = TextStyle(fontSize = 28.0, fontWeight = 700, lineHeight = 34.0, letterSpacing = 0.36),
        title2     = TextStyle(fontSize = 22.0, fontWeight = 700, lineHeight = 28.0, letterSpacing = 0.35),
        title3     = TextStyle(fontSize = 20.0, fontWeight = 600, lineHeight = 25.0, letterSpacing = 0.38),
        headline   = TextStyle(fontSize = 17.0, fontWeight = 600, lineHeight = 22.0, letterSpacing = -0.41),
        body       = TextStyle(fontSize = 17.0, fontWeight = 400, lineHeight = 22.0, letterSpacing = -0.41),
        callout    = TextStyle(fontSize = 16.0, fontWeight = 400, lineHeight = 21.0, letterSpacing = -0.32),
        subheadline= TextStyle(fontSize = 15.0, fontWeight = 400, lineHeight = 20.0, letterSpacing = -0.24),
        footnote   = TextStyle(fontSize = 13.0, fontWeight = 400, lineHeight = 18.0, letterSpacing = -0.08),
        caption1   = TextStyle(fontSize = 12.0, fontWeight = 400, lineHeight = 16.0, letterSpacing = 0.0),
        caption2   = TextStyle(fontSize = 11.0, fontWeight = 400, lineHeight = 13.0, letterSpacing = 0.07),
        fontFamily = seed.fontFamily,
        fontFamilyMono = seed.fontFamilyMono,
    )
}
```

**Ключевое отличие от текущей системы:**
- Было: 5 стилей (h1/h2/h3/body/caption) × 2 атрибута (size, weight)
- Стало: 11 стилей × 4 атрибута (size, weight, lineHeight, letterSpacing) = 44 значения вместо 10

---

### Шаг 1.3: Spacing, Sizing, Radius Apple

```kotlin
private fun resolveSpacing(seed: SeedTokens): ResolvedSpacing {
    val u = seed.sizeUnit  // 4.0
    return ResolvedSpacing(
        none = 0.0,
        xxs  = u * 0.5,     // 2
        xs   = u * 1.0,     // 4
        sm   = u * 2.0,     // 8
        md   = u * 4.0,     // 16
        lg   = u * 6.0,     // 24
        xl   = u * 8.0,     // 32
        xxl  = u * 12.0,    // 48
        xxxl = u * 16.0,    // 64
    )
}

private fun resolveSizing(seed: SeedTokens): ResolvedSizing {
    val h = seed.controlHeight  // 44.0
    return ResolvedSizing(
        controlXs = h * 0.545,    // ≈24
        controlSm = h * 0.727,    // ≈32
        controlMd = h,            // 44 (Apple default)
        controlLg = h * 1.182,    // ≈52
        iconXs  = 12.0,
        iconSm  = 16.0,
        iconMd  = 20.0,
        iconLg  = 24.0,
        iconXl  = 32.0,
    )
}

private fun resolveRadius(seed: SeedTokens): ResolvedRadius {
    val r = seed.borderRadius  // 12.0
    return ResolvedRadius(
        none = 0.0,
        xs   = (r / 3.0),        // 4
        sm   = (r / 1.5),        // 8
        md   = r,                 // 12 (Apple default)
        lg   = (r * 1.333),      // 16
        xl   = (r * 1.667),      // 20
        full = 9999.0,           // pill
    )
}
```

---

### Шаг 1.4: Shadows + Materials (Liquid Glass)

```kotlin
private fun resolveShadows(isDark: Boolean): ResolvedShadows {
    // Apple HIG: минималистичные тени, без тяжёлых drop-shadows
    val shadowColor = if (isDark) "rgba(0,0,0,0.5)" else "rgba(0,0,0,0.1)"
    val shadowColorMd = if (isDark) "rgba(0,0,0,0.6)" else "rgba(0,0,0,0.15)"

    return ResolvedShadows(
        none   = emptyList(),
        sm     = listOf(ShadowValue(0.0, 1.0, 3.0, 0.0, shadowColor)),
        md     = listOf(
            ShadowValue(0.0, 2.0, 8.0, 0.0, shadowColor),
            ShadowValue(0.0, 1.0, 2.0, 0.0, shadowColorMd),
        ),
        lg     = listOf(
            ShadowValue(0.0, 8.0, 24.0, 0.0, shadowColor),
            ShadowValue(0.0, 2.0, 8.0, 0.0, shadowColorMd),
        ),
        xl     = listOf(
            ShadowValue(0.0, 16.0, 48.0, 0.0, shadowColor),
            ShadowValue(0.0, 4.0, 16.0, 0.0, shadowColorMd),
        ),
        focus  = listOf(ShadowValue(0.0, 0.0, 0.0, 3.0, "rgba(0,122,255,0.4)")),
    )
}

private fun resolveMaterials(isDark: Boolean): ResolvedMaterials {
    // Apple Liquid Glass (WWDC 2025 / visionOS → iOS)
    return ResolvedMaterials(
        regular = MaterialValue(
            enabled = true,
            bgColor = if (isDark) "rgba(30,30,30,0.72)" else "rgba(255,255,255,0.72)",
            blurRadius = 20.0,
            saturation = 1.8,
            borderColor = if (isDark) "rgba(255,255,255,0.1)" else "rgba(0,0,0,0.06)",
            borderWidth = 0.5,
        ),
        thin = MaterialValue(
            enabled = true,
            bgColor = if (isDark) "rgba(30,30,30,0.5)" else "rgba(255,255,255,0.5)",
            blurRadius = 12.0,
            saturation = 1.5,
            borderColor = if (isDark) "rgba(255,255,255,0.08)" else "rgba(0,0,0,0.04)",
            borderWidth = 0.5,
        ),
        thick = MaterialValue(
            enabled = true,
            bgColor = if (isDark) "rgba(30,30,30,0.85)" else "rgba(255,255,255,0.85)",
            blurRadius = 30.0,
            saturation = 2.0,
            borderColor = if (isDark) "rgba(255,255,255,0.15)" else "rgba(0,0,0,0.08)",
            borderWidth = 0.5,
        ),
        overlay = MaterialValue(
            enabled = true,
            bgColor = if (isDark) "rgba(0,0,0,0.5)" else "rgba(0,0,0,0.4)",
            blurRadius = 5.0,
            saturation = 1.0,
            borderColor = "transparent",
            borderWidth = 0.0,
        ),
    )
}
```

---

### Шаг 1.5: State Tokens (hover/focus/pressed/disabled)

Состояния уже заложены в `ResolvedColors`:
- `primaryHover`, `primaryActive` — computed от primary
- `surfaceHover` — hover для surface элементов
- `surfaceDisabled`, `textDisabled`, `borderDisabled` — disabled state
- `shadows.focus` — focus ring

Дополнительные state tokens управляются через **CSS на стороне React** (`:hover`, `:focus-visible`, `:active`, `:disabled`) и `Modifier` на Compose.

---

### Шаг 1.6: Dark Theme Apple

Dark theme генерируется вызовом `resolveDark(seed)` — это тот же алгоритм с `isDark = true`.

**Что меняется в dark:**
- Surface: `#FFFFFF` → `#000000`
- Text labels: `rgba(0,0,0,x)` → `rgba(235,235,245,x)`
- Separators: `rgba(60,60,67,x)` → `rgba(84,84,88,x)`
- Fills: `rgba(120,120,128,x)` → `rgba(120,120,128,x+0.16)`
- Shadows: opacity × 2
- Materials: bgColor инвертируется

---

### Шаг 1.7: Motion Tokens

```kotlin
private fun resolveMotion(seed: SeedTokens): ResolvedMotion {
    val base = seed.motionDuration  // 250
    return ResolvedMotion(
        enabled = seed.motionEnabled,
        durationFast = (base * 0.4).toInt(),              // 100ms
        durationNormal = base,                             // 250ms
        durationSlow = (base * 2.0).toInt(),               // 500ms
        easing = "cubic-bezier(0.25, 0.1, 0.25, 1.0)",    // Apple standard
        easingEmphasized = "cubic-bezier(0.22, 1, 0.36, 1)", // Spring-like
    )
}
```

---

## Phase 2: Миграция компонентов на ResolvedTokens

> Цель: перевести ButtonStyleResolver, TextBlockStyleResolver и все View-компоненты на ResolvedTokens.

### Шаг 2.1: ButtonStyleResolver → ResolvedTokens

**Изменить:** `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonStyleResolver.kt`

**Было:**

```kotlin
fun resolve(config: ButtonConfig, tokens: DesignTokens): ResolvedButtonStyle
```

**Стало:**

```kotlin
fun resolve(config: ButtonConfig, tokens: ResolvedTokens): ResolvedButtonStyle
```

**Полные изменения:**

```kotlin
package com.uikit.components.atoms.button

import com.uikit.theme.resolved.ResolvedTokens
import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class ColorSet(
    val bg: String,
    val text: String,
    val border: String,
    val bgHover: String,       // НОВОЕ: hover state
    val bgActive: String,      // НОВОЕ: pressed state
    val textHover: String,     // НОВОЕ: text hover
    val focusRing: String,     // НОВОЕ: focus ring color
)

@JsExport
@Serializable
data class SizeSet(
    val height: Double,        // ИЗМЕНЕНО: Int → Double
    val paddingH: Double,      // ИЗМЕНЕНО: Int → Double
    val fontSize: Double,      // ИЗМЕНЕНО: Int → Double
    val fontWeight: Int,
    val lineHeight: Double,    // НОВОЕ
    val letterSpacing: Double, // НОВОЕ
    val iconSize: Double,      // ИЗМЕНЕНО: Int → Double
)

@JsExport
@Serializable
data class ResolvedButtonStyle(
    val colors: ColorSet,
    val sizes: SizeSet,
    val radius: Double,        // ИЗМЕНЕНО: Int → Double
    val shadow: String,        // НОВОЕ: box-shadow для кнопки
    val transition: String,    // НОВОЕ: CSS transition
)

@JsExport
object ButtonStyleResolver {

    fun resolve(config: ButtonConfig, tokens: ResolvedTokens): ResolvedButtonStyle {
        val colors = if (config.disabled) {
            ColorSet(
                bg = tokens.colors.surfaceDisabled,
                text = tokens.colors.textDisabled,
                border = tokens.colors.borderDisabled,
                bgHover = tokens.colors.surfaceDisabled,     // no hover when disabled
                bgActive = tokens.colors.surfaceDisabled,
                textHover = tokens.colors.textDisabled,
                focusRing = "transparent",
            )
        } else {
            when (config.variant) {
                ButtonVariant.Primary -> ColorSet(
                    bg = tokens.colors.primary,
                    text = tokens.colors.textOnPrimary,
                    border = "transparent",
                    bgHover = tokens.colors.primaryHover,
                    bgActive = tokens.colors.primaryActive,
                    textHover = tokens.colors.textOnPrimary,
                    focusRing = tokens.colors.borderFocus,
                )
                ButtonVariant.Secondary -> ColorSet(
                    bg = tokens.colors.surface,
                    text = tokens.colors.primary,
                    border = tokens.colors.border,
                    bgHover = tokens.colors.surfaceHover,
                    bgActive = tokens.colors.fill,
                    textHover = tokens.colors.primaryHover,
                    focusRing = tokens.colors.borderFocus,
                )
                ButtonVariant.Ghost -> ColorSet(
                    bg = "transparent",
                    text = tokens.colors.primary,
                    border = "transparent",
                    bgHover = tokens.colors.surfaceHover,
                    bgActive = tokens.colors.fill,
                    textHover = tokens.colors.primaryHover,
                    focusRing = tokens.colors.borderFocus,
                )
                ButtonVariant.Danger -> ColorSet(
                    bg = tokens.colors.error,
                    text = tokens.colors.textOnError,
                    border = "transparent",
                    bgHover = tokens.colors.errorText,       // darker
                    bgActive = tokens.colors.errorBorder,
                    textHover = tokens.colors.textOnError,
                    focusRing = tokens.colors.errorBorder,
                )
                ButtonVariant.Link -> ColorSet(
                    bg = "transparent",
                    text = tokens.colors.primary,
                    border = "transparent",
                    bgHover = "transparent",
                    bgActive = "transparent",
                    textHover = tokens.colors.primaryHover,
                    focusRing = tokens.colors.borderFocus,
                )
            }
        }

        val sizes = when (config.size) {
            ButtonSize.Sm -> SizeSet(
                height = tokens.sizing.controlSm,
                paddingH = tokens.spacing.sm,
                fontSize = tokens.typography.footnote.fontSize,
                fontWeight = tokens.typography.headline.fontWeight,
                lineHeight = tokens.typography.footnote.lineHeight,
                letterSpacing = tokens.typography.footnote.letterSpacing,
                iconSize = tokens.sizing.iconSm,
            )
            ButtonSize.Md -> SizeSet(
                height = tokens.sizing.controlMd,
                paddingH = tokens.spacing.md,
                fontSize = tokens.typography.body.fontSize,
                fontWeight = tokens.typography.headline.fontWeight,
                lineHeight = tokens.typography.body.lineHeight,
                letterSpacing = tokens.typography.body.letterSpacing,
                iconSize = tokens.sizing.iconMd,
            )
            ButtonSize.Lg -> SizeSet(
                height = tokens.sizing.controlLg,
                paddingH = tokens.spacing.lg,
                fontSize = tokens.typography.body.fontSize,
                fontWeight = tokens.typography.headline.fontWeight,
                lineHeight = tokens.typography.body.lineHeight,
                letterSpacing = tokens.typography.body.letterSpacing,
                iconSize = tokens.sizing.iconLg,
            )
        }

        // Transition из motion tokens
        val transition = if (tokens.motion.enabled) {
            "background-color ${tokens.motion.durationFast}ms ${tokens.motion.easing}, " +
            "color ${tokens.motion.durationFast}ms ${tokens.motion.easing}, " +
            "box-shadow ${tokens.motion.durationFast}ms ${tokens.motion.easing}"
        } else ""

        return ResolvedButtonStyle(
            colors = colors,
            sizes = sizes,
            radius = tokens.radius.md,
            shadow = "",  // Кнопки в Apple стиле — без тени по умолчанию
            transition = transition,
        )
    }
}
```

---

### Шаг 2.2: TextBlockStyleResolver → ResolvedTokens

**Изменить:** `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/text/TextBlockStyleResolver.kt`

```kotlin
@JsExport
@Serializable
data class ResolvedTextBlockStyle(
    val color: String,
    val fontSize: Double,        // ИЗМЕНЕНО: Int → Double
    val fontWeight: Int,
    val lineHeight: Double,      // ИЗМЕНЕНО: calculated → from tokens
    val letterSpacing: Double,   // НОВОЕ
    val fontFamily: String,      // НОВОЕ
)

@JsExport
object TextBlockStyleResolver {

    fun resolve(config: TextBlockConfig, tokens: ResolvedTokens): ResolvedTextBlockStyle {
        val textStyle = when (config.variant) {
            TextBlockVariant.H1 -> tokens.typography.largeTitle
            TextBlockVariant.H2 -> tokens.typography.title1
            TextBlockVariant.H3 -> tokens.typography.title2
            TextBlockVariant.Body -> tokens.typography.body
            TextBlockVariant.Caption -> tokens.typography.caption1
        }

        val color = when (config.variant) {
            TextBlockVariant.Caption -> tokens.colors.textSecondary
            else -> tokens.colors.textPrimary
        }

        return ResolvedTextBlockStyle(
            color = color,
            fontSize = textStyle.fontSize,
            fontWeight = textStyle.fontWeight,
            lineHeight = textStyle.lineHeight,
            letterSpacing = textStyle.letterSpacing,
            fontFamily = tokens.typography.fontFamily,
        )
    }
}
```

**Расширить TextBlockVariant** (чтобы соответствовать Apple HIG 11 стилям):

```kotlin
@JsExport
@Serializable
enum class TextBlockVariant {
    LargeTitle,
    Title1,
    Title2,
    Title3,
    Headline,
    Body,
    Callout,
    Subheadline,
    Footnote,
    Caption1,
    Caption2,

    // Legacy aliases (для обратной совместимости)
    H1,       // → LargeTitle
    H2,       // → Title1
    H3,       // → Title2
    Caption,  // → Caption1
}
```

---

### Шаг 2.3: React — CSS Custom Properties + rem

**Изменить:** `core/uikit/react/src/components/atoms/button/ButtonView.tsx`

**Ключевые изменения:**
1. `${value}px` → `${value / 16}rem` (или значения уже в rem)
2. Добавить CSS custom properties для hover/focus/active
3. Использовать tokens.motion для transition
4. Добавить focus-visible стили

```typescript
// ButtonView.tsx — React
// Вместо:
style={{
  '--btn-height': `${style.sizes.height}px`,
}}

// Станет:
style={{
  '--btn-height': `${style.sizes.height}px`,  // или rem через конфиг
  '--btn-bg': style.colors.bg,
  '--btn-bg-hover': style.colors.bgHover,
  '--btn-bg-active': style.colors.bgActive,
  '--btn-text': style.colors.text,
  '--btn-text-hover': style.colors.textHover,
  '--btn-border': style.colors.border,
  '--btn-focus-ring': style.colors.focusRing,
  '--btn-radius': `${style.radius}px`,
  '--btn-font-size': `${style.sizes.fontSize}px`,
  '--btn-font-weight': style.sizes.fontWeight,
  '--btn-line-height': `${style.sizes.lineHeight}px`,
  '--btn-letter-spacing': `${style.sizes.letterSpacing}em`,
  '--btn-transition': style.transition,
} as React.CSSProperties}
```

**Создать CSS-файл для компонента:**

**Создать:** `core/uikit/react/src/components/atoms/button/button.css`

```css
.uikit-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: var(--btn-height);
  padding: 0 var(--btn-padding-h);
  background: var(--btn-bg);
  color: var(--btn-text);
  border: 1px solid var(--btn-border);
  border-radius: var(--btn-radius);
  font-size: var(--btn-font-size);
  font-weight: var(--btn-font-weight);
  line-height: var(--btn-line-height);
  letter-spacing: var(--btn-letter-spacing);
  cursor: pointer;
  transition: var(--btn-transition);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  user-select: none;
}

.uikit-button:hover:not(:disabled) {
  background: var(--btn-bg-hover);
  color: var(--btn-text-hover);
}

.uikit-button:active:not(:disabled) {
  background: var(--btn-bg-active);
}

.uikit-button:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px var(--btn-focus-ring);
}

.uikit-button:disabled {
  cursor: not-allowed;
  opacity: 1; /* opacity уже в token colors, не double-applying */
}
```

---

### Шаг 2.4: React — ThemeProvider переход

**Изменить:** `core/uikit/react/src/theme/useDesignTokens.tsx`

```typescript
'use client';

import React, { createContext, useContext, useState, useCallback, type ReactNode } from 'react';
import {
  ThemeManager,
  type StylePreset,
  type ResolvedTokens,
} from 'uikit-common';

// Инициализируем ThemeManager (singleton)
const themeManager = ThemeManager.Companion.createDefault();

interface ThemeContextValue {
  tokens: ResolvedTokens;
  preset: StylePreset;
  isDark: boolean;
  setPreset: (preset: StylePreset) => void;
  setDark: (isDark: boolean) => void;
}

const ThemeContext = createContext<ThemeContextValue | null>(null);

export function useTheme(): ThemeContextValue {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error('useTheme must be used within ThemeProvider');
  return ctx;
}

/** Shortcut для получения resolved tokens */
export function useDesignTokens(): ResolvedTokens {
  return useTheme().tokens;
}

interface ThemeProviderProps {
  preset: StylePreset;
  isDark?: boolean;
  children: ReactNode;
}

export function ThemeProvider({ preset: initialPreset, isDark: initialDark = false, children }: ThemeProviderProps) {
  const [preset, setPreset] = useState(initialPreset);
  const [isDark, setDark] = useState(initialDark);

  const tokens = themeManager.resolve(preset, isDark);

  const value: ThemeContextValue = {
    tokens,
    preset,
    isDark,
    setPreset,
    setDark,
  };

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
}
```

**Использование в app:**

```tsx
// apps/catalog-ui/react/src/app/layout.tsx
import { ThemeProvider, applePreset } from '@uikit/react';

export default function RootLayout({ children }) {
  return (
    <ThemeProvider preset={applePreset}>
      {children}
    </ThemeProvider>
  );
}
```

---

### Шаг 2.5: Compose — UIKitTheme переход

**Изменить:** `core/uikit/compose/src/commonMain/kotlin/com/uikit/compose/theme/UIKitTheme.kt`

```kotlin
package com.uikit.compose.theme

import androidx.compose.runtime.*
import com.uikit.theme.StylePreset
import com.uikit.theme.ThemeManager
import com.uikit.theme.resolved.ResolvedTokens

val LocalResolvedTokens = staticCompositionLocalOf<ResolvedTokens> {
    error("No ResolvedTokens provided. Wrap your content in UIKitTheme.")
}

@Composable
fun UIKitTheme(
    preset: StylePreset,
    isDark: Boolean = false,
    themeManager: ThemeManager = remember { ThemeManager.createDefault() },
    content: @Composable () -> Unit,
) {
    val tokens = remember(preset, isDark) {
        themeManager.resolve(preset, isDark)
    }

    CompositionLocalProvider(LocalResolvedTokens provides tokens) {
        content()
    }
}

/** Shortcut для доступа к токенам в Composable */
object UIKitTokens {
    val current: ResolvedTokens
        @Composable
        get() = LocalResolvedTokens.current
}
```

---

### Шаг 2.6: Обратная совместимость (migration path)

Чтобы не ломать всё одновременно, создаём **bridge**:

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/compat/TokensBridge.kt`

```kotlin
package com.uikit.theme.compat

import com.uikit.tokens.DesignTokens
import com.uikit.theme.resolved.ResolvedTokens

/**
 * Мост между старыми DesignTokens и новыми ResolvedTokens.
 * Используется для постепенной миграции.
 * УДАЛИТЬ после полной миграции всех компонентов.
 */
object TokensBridge {

    /** Конвертировать старые DesignTokens в ResolvedTokens (потеря данных — нет новых полей) */
    fun toResolved(old: DesignTokens): ResolvedTokens {
        // Маппинг старых flat-токенов на новую структуру.
        // Недостающие значения заполняются дефолтами.
        // ...
    }

    /** Конвертировать ResolvedTokens обратно в DesignTokens (для legacy компонентов) */
    fun toLegacy(resolved: ResolvedTokens): DesignTokens {
        return DesignTokens(
            color = ColorTokens(
                primary = resolved.colors.primary,
                primaryHover = resolved.colors.primaryHover,
                secondary = resolved.colors.textSecondary,
                danger = resolved.colors.error,
                surface = resolved.colors.surface,
                surfaceHover = resolved.colors.surfaceHover,
                textPrimary = resolved.colors.textPrimary,
                textOnPrimary = resolved.colors.textOnPrimary,
                textOnDanger = resolved.colors.textOnError,
                textDisabled = resolved.colors.textDisabled,
                surfaceDisabled = resolved.colors.surfaceDisabled,
                borderDisabled = resolved.colors.borderDisabled,
                border = resolved.colors.border,
            ),
            // ... маппинг для остальных
        )
    }
}
```

---

## Phase 3: Style Switching — Apple ↔ Material ↔ Glass

### Шаг 3.1: MaterialAlgorithm

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/algorithms/MaterialAlgorithm.kt`

**Ключевые отличия от Apple:**

| Аспект | Apple | Material |
|---|---|---|
| Surfaces | Чистые белые/чёрные | Tinted (primary оттенок) |
| Accent | Один цвет, sparingly | primary/secondary/tertiary |
| Shadows | Subtle | Elevation system |
| Radius | 12px continuous | 20px+ rounded |
| Touch targets | 44pt | 48dp |
| Typography | 11 Apple styles | 15 M3 styles |
| Motion | Cubic-bezier | Spring-based emphasized |
| Blur | Liquid Glass | Нет |

```kotlin
class MaterialAlgorithm : ThemeAlgorithm {
    override val id = "material"
    override val displayName = "Material You"

    override fun resolve(seed: SeedTokens): ResolvedTokens {
        return ResolvedTokens(
            colors = resolveMaterialColors(seed, isDark = false),
            typography = resolveMaterialTypography(seed),
            spacing = resolveMaterialSpacing(seed),
            sizing = resolveMaterialSizing(seed),
            radius = resolveMaterialRadius(seed),
            shadows = resolveMaterialShadows(isDark = false),
            materials = resolveDisabledMaterials(), // Material не использует blur
            motion = resolveMaterialMotion(seed),
            breakpoints = resolveBreakpoints(),
        )
    }

    // Цвета: tinted surfaces (surface = чуть тонированный primary)
    // Typography: Roboto, 15 стилей (display/headline/title/body/label × S/M/L)
    // Radius: 20px base (более скруглённые)
    // Shadows: elevation levels (1dp, 3dp, 6dp, 8dp, 12dp)
    // Motion: spring-based easing
}
```

---

### Шаг 3.2: GlassAlgorithm

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/algorithms/GlassAlgorithm.kt`

```kotlin
class GlassAlgorithm : ThemeAlgorithm {
    override val id = "glass"
    override val displayName = "Glassmorphism"

    // Цвета: тёмная база, яркие акценты, gradient support
    // Surface: rgba(255,255,255, 0.05–0.15)
    // Shadows → glow (colored box-shadow with spread)
    // Materials: heavy blur (20–32px), low saturation
    // Border: 1px rgba(255,255,255, 0.1–0.2)
    // Motion: smooth, slightly slower
}
```

---

### Шаг 3.3: Runtime Switching (React)

```tsx
// Переключение стиля в приложении:
import { useTheme, applePreset, materialPreset, glassPreset } from '@uikit/react';

function StyleSwitcher() {
  const { setPreset, preset } = useTheme();

  return (
    <select
      value={preset.name}
      onChange={(e) => {
        const presets = { Apple: applePreset, Material: materialPreset, Glass: glassPreset };
        setPreset(presets[e.target.value]);
      }}
    >
      <option value="Apple">Apple</option>
      <option value="Material">Material You</option>
      <option value="Glass">Glassmorphism</option>
    </select>
  );
}
```

Все компоненты автоматически перерендерятся с новыми токенами — переключение одной строкой.

---

### Шаг 3.4: Runtime Switching (Compose)

```kotlin
@Composable
fun App() {
    var currentPreset by remember { mutableStateOf(Presets.apple) }
    var isDark by remember { mutableStateOf(false) }

    UIKitTheme(preset = currentPreset, isDark = isDark) {
        Column {
            // Style switcher
            Row {
                Button(onClick = { currentPreset = Presets.apple }) { Text("Apple") }
                Button(onClick = { currentPreset = Presets.material }) { Text("Material") }
                Button(onClick = { currentPreset = Presets.glass }) { Text("Glass") }
            }

            // Toggle dark
            Switch(checked = isDark, onCheckedChange = { isDark = it })

            // Все компоненты используют LocalResolvedTokens.current
            ButtonView(text = "Click me")
        }
    }
}
```

---

## Phase 4: Illustration + Cartoon + Custom стили

### Шаг 4.1: IllustrationAlgorithm

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/algorithms/IllustrationAlgorithm.kt`

| Характеристика | Значение |
|---|---|
| Палитра | Пастельные тона (seed lightened 70-80%) |
| Шрифт | Rounded sans-serif (Nunito, Comic Neue) |
| Radius | 16px+ (super-rounded) |
| Тени | Цветные soft shadows |
| Borders | 2px solid |
| Surfaces | Warm/cream тона (#FFF8F0) |
| Motion | Bouncy spring easing |

---

### Шаг 4.2: CartoonAlgorithm

**Создать:** `core/uikit/common/src/commonMain/kotlin/com/uikit/theme/algorithms/CartoonAlgorithm.kt`

| Характеристика | Значение |
|---|---|
| Палитра | Яркие чистые цвета (chroma максимальная) |
| Шрифт | Display/fun (Fredoka One, Baloo 2) |
| Radius | 24px (blob-like) |
| Тени | Hard drop (4px offset, no blur, dark) |
| Borders | 3px bold black (#000) |
| Surfaces | Чистый белый, без градаций |
| Motion | Exaggerated (overshoot, bounce) |

---

### Шаг 4.3: Custom Algorithm API

Пользователи могут создавать свои стили:

```kotlin
// В приложении пользователя:
class BrandAlgorithm : ThemeAlgorithm {
    override val id = "brand"
    override val displayName = "Our Brand"

    override fun resolve(seed: SeedTokens): ResolvedTokens {
        // Базируется на Apple, но с brand-специфичными настройками:
        val appleBase = AppleAlgorithm().resolve(seed)
        return appleBase.copy(
            colors = appleBase.colors.copy(
                primary = "#FF6600",  // brand orange
            ),
            typography = appleBase.typography.copy(
                fontFamily = "'Brand Font', sans-serif",
            ),
        )
    }

    override fun resolveDark(seed: SeedTokens): ResolvedTokens {
        val appleBase = AppleAlgorithm().resolveDark(seed)
        return appleBase.copy(
            colors = appleBase.colors.copy(
                primary = "#FF8800",
            ),
        )
    }
}

// Регистрация:
themeManager.registerAlgorithm(BrandAlgorithm())
```

---

## Phase 5: Component Tokens + Nested Themes

### Шаг 5.1: ComponentTokens per-component

**Механизм:** `StylePreset.componentOverrides` — map в формате `"ComponentName" → { "tokenPath" → "value" }`.

```kotlin
val myPreset = Presets.apple.copy(
    componentOverrides = mapOf(
        "Button" to mapOf(
            "radius" to "9999",         // pill buttons
            "fontWeight" to "700",      // bolder
        ),
        "TextBlock" to mapOf(
            "fontFamily" to "'Georgia', serif",  // serif для текста
        ),
    ),
)
```

В ButtonStyleResolver:
```kotlin
// После расчёта базовых значений, применить overrides:
val overrides = componentOverrides["Button"]
val finalRadius = overrides?.get("radius")?.toDoubleOrNull() ?: baseRadius
```

---

### Шаг 5.2: Nested Themes

**React:**

```tsx
<ThemeProvider preset={applePreset}>
  <Header />
  <ThemeProvider preset={glassPreset}> {/* Sidebar в Glass стиле */}
    <Sidebar />
  </ThemeProvider>
  <Content />
</ThemeProvider>
```

**Compose:**

```kotlin
UIKitTheme(preset = Presets.apple) {
    Header()
    UIKitTheme(preset = Presets.glass) { // Вложенная тема
        Sidebar()
    }
    Content()
}
```

React Context и Compose CompositionLocal **уже поддерживают вложенность** — никакого дополнительного кода не нужно.

---

## Phase 6: Responsive + Accessibility

### Шаг 6.1: Breakpoints

`ResolvedBreakpoints` уже создан в Phase 0.

**React:** использовать для responsive логики:

```typescript
import { useTheme } from '@uikit/react';

function useBreakpoint() {
  const { tokens } = useTheme();
  const [width, setWidth] = useState(window.innerWidth);
  // ... resize listener
  if (width >= tokens.breakpoints.xl) return 'xl';
  if (width >= tokens.breakpoints.lg) return 'lg';
  // ...
}
```

---

### Шаг 6.2: Fluid Typography (clamp)

На Web — шрифты масштабируются через `clamp()`:

```typescript
// Вместо: fontSize: '17px'
// Использовать: fontSize: `clamp(15px, calc(15px + 0.25vw), 17px)`

function fluidType(minPx: number, maxPx: number): string {
  const minVw = 320;
  const maxVw = 1280;
  const slope = (maxPx - minPx) / (maxVw - minVw);
  const intercept = minPx - slope * minVw;
  return `clamp(${minPx}px, ${intercept.toFixed(4)}px + ${(slope * 100).toFixed(4)}vw, ${maxPx}px)`;
}
```

---

### Шаг 6.3: Contrast Levels

Три уровня контрастности (как M3):

```kotlin
enum class ContrastLevel {
    Standard,   // Дефолт
    Medium,     // Increased contrast (3:1 minimum для любого текста)
    High,       // Maximum contrast (7:1 minimum)
}
```

ThemeManager:
```kotlin
fun resolve(preset: StylePreset, isDark: Boolean, contrast: ContrastLevel): ResolvedTokens
```

В алгоритме `contrast` влияет на:
- Яркость текстовых цветов
- Прозрачность secondary/tertiary text
- Ширину бордеров
- Интенсивность теней

---

## Карта файлов

### Новые файлы (создать)

| # | Файл | Phase | Описание |
|---|---|---|---|
| 1 | `theme/SeedTokens.kt` | 0 | 18 seed values |
| 2 | `theme/ThemeAlgorithm.kt` | 0 | Interface для алгоритмов |
| 3 | `theme/StyleTraits.kt` | 0 | Визуальные характеристики |
| 4 | `theme/StylePreset.kt` | 0 | Конфигурация стиля |
| 5 | `theme/ThemeManager.kt` | 0 | Центральный менеджер |
| 6 | `theme/resolved/ResolvedColors.kt` | 0 | 35+ semantic colors |
| 7 | `theme/resolved/ResolvedTypography.kt` | 0 | 11 стилей × 4 attrs |
| 8 | `theme/resolved/ResolvedSpacing.kt` | 0 | 9 уровней |
| 9 | `theme/resolved/ResolvedSizing.kt` | 0 | Control + icon sizes |
| 10 | `theme/resolved/ResolvedRadius.kt` | 0 | 7 уровней (none → full) |
| 11 | `theme/resolved/ResolvedShadows.kt` | 0 | 6 уровней + focus |
| 12 | `theme/resolved/ResolvedMaterials.kt` | 0 | Blur/vibrancy configs |
| 13 | `theme/resolved/ResolvedMotion.kt` | 0 | 3 durations + easing |
| 14 | `theme/resolved/ResolvedBreakpoints.kt` | 0 | 6 breakpoints |
| 15 | `theme/resolved/ResolvedTokens.kt` | 0 | Container |
| 16 | `theme/util/ColorUtil.kt` | 1 | darken/lighten/withOpacity |
| 17 | `theme/algorithms/AppleAlgorithm.kt` | 1 | Apple HIG алгоритм |
| 18 | `theme/algorithms/MaterialAlgorithm.kt` | 3 | Material You алгоритм |
| 19 | `theme/algorithms/GlassAlgorithm.kt` | 3 | Glassmorphism алгоритм |
| 20 | `theme/algorithms/IllustrationAlgorithm.kt` | 4 | Illustration алгоритм |
| 21 | `theme/algorithms/CartoonAlgorithm.kt` | 4 | Cartoon алгоритм |
| 22 | `theme/presets/Presets.kt` | 1+ | Готовые StylePreset конфиги |
| 23 | `theme/compat/TokensBridge.kt` | 2 | Мост old ↔ new |
| 24 | `react/src/components/atoms/button/button.css` | 2 | CSS для кнопки |

### Существующие файлы (изменить)

| # | Файл | Phase | Что изменится |
|---|---|---|---|
| 1 | `ButtonStyleResolver.kt` | 2 | `DesignTokens → ResolvedTokens`, добавить hover/focus/active |
| 2 | `TextBlockStyleResolver.kt` | 2 | `DesignTokens → ResolvedTokens`, добавить letterSpacing |
| 3 | `TextBlockConfig.kt` | 2 | Расширить TextBlockVariant до 11+ вариантов |
| 4 | `ButtonView.tsx` | 2 | CSS vars + hover/focus, rem |
| 5 | `TextBlockView.tsx` | 2 | CSS vars + letterSpacing, rem |
| 6 | `useDesignTokens.tsx` | 2 | → ThemeProvider с setPreset/setDark |
| 7 | `Button.tsx` | 2 | Обновить под новые типы |
| 8 | `Text.tsx` | 2 | Обновить под новые варианты |
| 9 | `index.ts` | 2 | Добавить экспорты Theme API |
| 10 | `UIKitTheme.kt` (Compose) | 2 | → UIKitTheme(preset, isDark) |
| 11 | `ColorUtil.kt` (Compose) | 2 | Поддержка rgba() строк |
| 12 | `ButtonView.kt` (Compose) | 2 | ResolvedTokens |
| 13 | `TextBlockView.kt` (Compose) | 2 | ResolvedTokens |

### Файлы для удаления (после полной миграции)

| # | Файл | Когда | Почему |
|---|---|---|---|
| 1 | `tokens/ColorTokens.kt` | После Phase 2 | Заменён на ResolvedColors |
| 2 | `tokens/TypographyTokens.kt` | После Phase 2 | Заменён на ResolvedTypography |
| 3 | `tokens/SpacingTokens.kt` | После Phase 2 | Заменён на ResolvedSpacing |
| 4 | `tokens/RadiusTokens.kt` | После Phase 2 | Заменён на ResolvedRadius |
| 5 | `tokens/SizingTokens.kt` | После Phase 2 | Заменён на ResolvedSizing |
| 6 | `tokens/DesignTokens.kt` | После Phase 2 | Заменён на ResolvedTokens |
| 7 | `theme/compat/TokensBridge.kt` | После Phase 2 | Bridge больше не нужен |

---

## Порядок выполнения и зависимости

```
Phase 0: Foundation (параллельные шаги)
═══════════════════════════════════════
  0.1 SeedTokens         ─┐
  0.2 ResolvedTokens      │─ Параллельно (нет зависимостей)
  0.4 StyleTraits         ─┘
       ↓
  0.3 ThemeAlgorithm      (зависит от 0.1 + 0.2)
       ↓
  0.5 StylePreset          (зависит от 0.1 + 0.4)
       ↓
  0.6 ThemeManager         (зависит от 0.3 + 0.5)

Phase 1: Apple Algorithm (последовательно)
═══════════════════════════════════════════
  1.0 ColorUtil           (утилиты)
       ↓
  1.1 AppleAlgorithm      (зависит от Phase 0 + 1.0)
       ↓
  1.2–1.7 Наполнение      (внутри AppleAlgorithm)
       ↓
  ★ CHECKPOINT: `ThemeManager.resolve(applePreset)` → ResolvedTokens работает

Phase 2: Миграция компонентов (можно частично параллелить)
═════════════════════════════════════════════════════════
  2.6 TokensBridge        (мост для миграции)
       ↓
  2.1 ButtonStyleResolver  ─┐
  2.2 TextBlockStyleResolver│─ Параллельно (независимые компоненты)
       ↓                   ─┘
  2.3 React CSS + rem     ─┐
  2.4 React ThemeProvider  │─ React stack
       ↓                   ─┘
  2.5 Compose UIKitTheme  (Compose stack, параллельно с React)
       ↓
  ★ CHECKPOINT: Всё рендерится через ResolvedTokens, старые tokens deprecated

Phase 3: Additional algorithms (параллельно)
════════════════════════════════════════════
  3.1 MaterialAlgorithm   ─┐
  3.2 GlassAlgorithm      │─ Параллельно (независимые)
       ↓                   ─┘
  3.3 React switching      ─┐
  3.4 Compose switching    │─ Параллельно
       ↓                   ─┘
  ★ CHECKPOINT: 3 стиля переключаются в runtime

Phase 4: Fun styles (параллельно)
═════════════════════════════════
  4.1 IllustrationAlgorithm ─┐
  4.2 CartoonAlgorithm      │─ Параллельно
       ↓                    ─┘
  4.3 Custom Algorithm API   (зависит от 4.1/4.2 для тестирования)
       ↓
  ★ CHECKPOINT: 5 стилей + custom API

Phase 5: Advanced features
══════════════════════════
  5.1 Component Tokens     (независимый)
  5.2 Nested Themes        (уже работает, нужен только тест)

Phase 6: Responsive + A11y
═══════════════════════════
  6.1 Breakpoints hook     (React)
  6.2 Fluid typography     (React/Web specific)
  6.3 Contrast levels      (расширение ThemeManager)

Финал: Cleanup
══════════════
  Удалить tokens/ пакет
  Удалить TokensBridge
  Обновить README / docs
```

---

## Итого: что получим

```
БЫЛО (37 flat tokens):                СТАНЕТ (100+ resolved tokens):
════════════════════                   ═════════════════════════════

13 hex colors                      →  35+ semantic colors + 5 groups + dark
5 text styles × 2 attrs (10)      →  11 text styles × 4 attrs (44)
5 spacings                         →  9 spacings (none → xxxl)
3 radii                            →  7 radii (none → full/pill)
6 component sizings                →  9 generic sizings (control + icon)
0 shadows                          →  6 shadow levels + focus
0 materials                        →  4 material levels (blur/glass)
0 motion                           →  3 durations + 2 easings
0 breakpoints                      →  6 responsive breakpoints
0 style presets                    →  5+ built-in (Apple/Material/Glass/Illustration/Cartoon)
0 algorithms                       →  5+ built-in + custom API
1 theme (generic blue)             →  Light/Dark × 5+ styles = 10+ themes
No switching                       →  1-line runtime switching
```

**Архитектурный принцип**: 18 seed values → алгоритм → 100+ resolved tokens → компоненты. Меняешь алгоритм — меняется весь UI.
