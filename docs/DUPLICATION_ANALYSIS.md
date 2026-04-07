# Анализ дублирования и архитектурных проблем UIKit

> Дата: 2026-04-07 · Охват: common + compose + react модули · Компонентов: 10

---

## Оглавление

1. [Критические проблемы](#1-критические-проблемы)
2. [Common модуль: scaled() и токены](#2-common-модуль-scaled-и-токены)
3. [Common модуль: SurfaceLevel → color маппинг](#3-common-модуль-surfacelevel--color-маппинг)
4. [Common модуль: Config data classes](#4-common-модуль-config-data-classes)
5. [Common модуль: Size output types](#5-common-модуль-size-output-types)
6. [Compose модуль: View boilerplate](#6-compose-модуль-view-boilerplate)
7. [React модуль: MAP объекты и hooks](#7-react-модуль-map-объекты-и-hooks)
8. [React модуль: CSS дублирование](#8-react-модуль-css-дублирование)
9. [Сводная таблица](#9-сводная-таблица)
10. [Рекомендации по рефакторингу](#10-рекомендации-по-рефакторингу)

---

## 1. Критические проблемы

### Системная проблема: отсутствие generics-базированного scaling

Корневая причина дублирования `scaled()` — архитектурное решение делать каждый token data class полностью изолированным `@JsExport @Serializable` классом. `@JsExport` не поддерживает интерфейсы, sealed-иерархии и generics, поэтому каждый token класс вынужден реализовывать идентичный паттерн `scaled()` **вручную**.

Текущая ситуация: **7 определений** `fun XxxTokens.scaled(factor: Double)` в 6 файлах с идентичной логикой `field * factor` для каждого числового поля.

Это **не баг, а системный trade-off** из-за KMP/JS interop, но при масштабировании (50+ компонентов) становится серьёзным tech debt.

---

## 2. Common модуль: `scaled()` и токены

### 2.1 Проблема: 7 функций `scaled()` с идентичным паттерном

| Файл                          | Функция                             | Полей для масштабирования                |
| ----------------------------- | ----------------------------------- | ---------------------------------------- |
| `TextStyle.kt`                | `TextStyle.scaled()`                | 2 (fontSize, lineHeight)                 |
| `SpacingTokens.kt`            | `SpacingTokens.scaled()`            | 9                                        |
| `SizingTokens.kt`             | `SizingTokens.scaled()`             | 11                                       |
| `TypographyTokens.kt`         | `TypographyTokens.scaled()`         | 15 (делегирует в TextStyle.scaled)       |
| `InteractiveControlTokens.kt` | `ControlSizeInput.scaled()`         | 1                                        |
| `InteractiveControlTokens.kt` | `InteractiveControlTokens.scaled()` | 6 (делегирует в ControlSizeInput.scaled) |
| `DesignTokens.kt`             | `DesignTokens.scaled()`             | 6 (делегирует в sub-tokens)              |

**Паттерн каждой функции:**
```kotlin
fun XxxTokens.scaled(factor: Double): XxxTokens =
    if (factor == 1.0) this
    else copy(
        field1 = field1 * factor,
        field2 = field2 * factor,
        // ... N полей
    )
```

**Причина**: `@JsExport` не поддерживает:
- Interface с default implementation (`Scalable<T>`)
- Abstract data class
- KSP-генерированные расширения (пока)

### 2.2 Нарушенный принцип

**OCP (Open-Closed Principle)**: добавление нового числового поля в любой token class **требует** ручного обновления соответствующей `scaled()` функции. Забытое поле — silent bug (поле не масштабируется, no compile error).

### 2.3 Конфликт: `SizingTokens` vs `ComponentSizeResolver`

`SizingTokens` содержит хардкодные `controlXs..controlXl` и `iconXs..iconXl`, но **все интерактивные компоненты** вычисляют размеры через `ComponentSizeResolver` из `InteractiveControlTokens`. `SizingTokens.scaled()` масштабирует значения, которые нигде не используются в компонентах (или используются параллельно, создавая две системы размеров).

**Вопрос**: зачем `SizingTokens.controlMd = 40.0`, если `ButtonStyleResolver` вычисляет `height = fontSize * heightRatio = 15 * 2.5 = 37.5`? Два источника правды для одной и той же метрики.

---

## 3. Common модуль: `SurfaceLevel → color` маппинг

### 3.1 Проблема: 3 копии `level → containerColor` маппинга

Идентичная when-expression дублируется в трёх файлах:

| Файл                               | Функция               | Тип маппинга                                   |
| ---------------------------------- | --------------------- | ---------------------------------------------- |
| `SurfaceStyleResolver.kt`          | `resolveBg()`         | `SurfaceLevel → color`                         |
| `PanelStyleResolver.kt`            | `resolveBg()`         | `SurfaceLevel → color` (100% идентичен)        |
| `SegmentedControlStyleResolver.kt` | `containerForLevel()` | `Int → color` (та же логика, другая сигнатура) |

```kotlin
// Копия 1: SurfaceStyleResolver.resolveBg()
private fun resolveBg(level: SurfaceLevel, tokens: DesignTokens): String =
    when (level) {
        SurfaceLevel.Level0 -> tokens.color.surface
        SurfaceLevel.Level1 -> tokens.color.surfaceContainerLowest
        SurfaceLevel.Level2 -> tokens.color.surfaceContainerLow
        SurfaceLevel.Level3 -> tokens.color.surfaceContainer
        SurfaceLevel.Level4 -> tokens.color.surfaceContainerHigh
        SurfaceLevel.Level5 -> tokens.color.surfaceContainerHighest
    }

// Копия 2: PanelStyleResolver.resolveBg() — ИДЕНТИЧНА

// Копия 3: SegmentedControlStyleResolver.containerForLevel(Int) — та же таблица
```

Также `SurfaceStyleResolver.resolveSoftBg()` — это та же таблица со сдвигом на 1 уровень.

### 3.2 Предлагаемое решение

Вынести в `foundation/SurfaceLevelResolver.kt`:
```kotlin
object SurfaceLevelResolver {
    fun resolveColor(level: SurfaceLevel, tokens: DesignTokens): String
    fun resolveColor(level: Int, tokens: DesignTokens): String
    fun resolveSoftColor(level: SurfaceLevel, tokens: DesignTokens): String
}
```

---

## 4. Common модуль: Config data classes

### 4.1 Повторяющиеся поля в каждом Config

Из-за ограничений `@JsExport` (нет интерфейсов с default impl) следующие поля **копируются** в каждый Config:

| Поле                                                       | Компонентов      | Статус               |
| ---------------------------------------------------------- | ---------------- | -------------------- |
| `id: String = ""`                                          | 10/10            | Осознанный trade-off |
| `testTag: String? = null`                                  | 10/10            | Осознанный trade-off |
| `visibility: Visibility = Visibility.Visible`              | 10/10            | Осознанный trade-off |
| `val isInteractive: Boolean get() = !disabled && !loading` | 2 (Button, Chip) | Будет расти          |
| `disabled: Boolean = false`                                | 3                | Будет расти          |
| `loading: Boolean = false`                                 | 3                | Будет расти          |
| `actionRoute: String? = null`                              | 2 (Button, Chip) | Будет расти          |

**Масштаб**: при 50 компонентах — 150+ строк чистого копипаста полей. Уже задокументировано как trade-off, но без стратегии митигации.

### 4.2 `SegmentedControlConfig`: ручной `equals()/hashCode()` из-за `Array`

`SegmentedControlConfig` использует `Array<SegmentedControlOption>` вместо `List`, что требует ручных `equals()/hashCode()` (20 строк). Причина — `@JsExport` не поддерживает `List`. Каждый будущий Config с массивами столкнётся с тем же.

---

## 5. Common модуль: Size output types

### 5.1 Проблема: множество типов для одной концепции "размеры компонента"

Каждый интерактивный компонент определяет **свой** data class для размеров, хотя 80% полей совпадают:

| Класс                   | Поля                                                                                                                                 | Уникальные поля                                |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------- |
| `SizeSet` (Button)      | height, paddingH, paddingV, fontSize, fontWeight, iconSize, iconGap, letterSpacing, lineHeight, isIconOnly                           | `isIconOnly`                                   |
| `ChipSizes`             | height, paddingStart, paddingEnd, fontSize, fontWeight, iconSize, iconGap, closeButtonSize, closeIconSize, letterSpacing, lineHeight | `paddingStart/End`, `closeButtonSize/IconSize` |
| `SegmentedControlSizes` | height, paddingH, paddingV, fontSize, fontWeight, letterSpacing, radius, thumbRadius, trackPadding, iconSize, iconGap, lineHeight    | `thumbRadius`, `trackPadding`                  |

**Общих полей**: `height`, `fontSize`, `fontWeight`, `iconSize`, `iconGap`, `letterSpacing`, `lineHeight` = **7 из ~10**.

Каждый новый composite component добавит ещё один `XxxSizes` data class с 70% overlap. Это не нарушение per se, но при 20+ компонентах создаст серьёзную проблему поддержки.

### 5.2 Конфликт: `ControlSizeScale` vs `SizeSet` vs `ChipSizes`

`ComponentSizeResolver.resolve()` возвращает `ControlSizeScale` — уже **универсальный** тип размеров. Но каждый StyleResolver создаёт **свой** output type, копируя 70% полей из `ControlSizeScale`. Промежуточный шаг (`ControlSizeScale` → `SizeSet`) — чистый маппинг без трансформации для большинства полей.

---

## 6. Compose модуль: View boilerplate

### 6.1 Паттерны дублирования в Compose View

Каждый Compose View повторяет идентичные блоки:

| Паттерн                                                              | Файлов | Строк/файл | Всего |
| -------------------------------------------------------------------- | ------ | ---------- | ----- |
| `remember(config) { Resolver.resolve(config, tokens) }`              | 10     | 3-5        | ~40   |
| `if (config.visibility == Visibility.Gone) return`                   | 10     | 1          | 10    |
| `Modifier.testTag(config.testTag)`                                   | 10     | 2          | 20    |
| Interactivity setup: `interactionSource`, `isHovered`, `isFocused`   | 4      | 5-7        | ~24   |
| Anti-stacking hover: `LocalChildHoverState.current.value`            | 3      | 5-7        | ~18   |
| Border + focus ring: `if (borderColor != TRANSPARENT) border(...)`   | 4      | 4-5        | ~18   |
| Spinner inside composable (LoadingContent)                           | 2      | 7          | 14    |
| Text styling: `TextStyle(fontSize.sp, fontWeight, letterSpacing.sp)` | 3      | 8-11       | ~27   |
| Icon + text layout: Row/Column branch                                | 3      | 40-70      | ~150  |
| `RoundedCornerShape(radius.dp)` creation                             | 6      | 1          | 6     |

**Итого**: ~350+ строк чистого дублирования в 10 Compose View файлах.

### 6.2 Отсутствующие утилиты

Нет shared composables для:
- `InteractiveBox` — Box с hover/focus/press state, border, focus ring, testTag, visibility
- `StyledText` — Text с fontSize/fontWeight/letterSpacing/lineHeight из resolved style
- `IconTextLayout` — Row/Column branch с icon gap

Это привело к тому, что каждый интерактивный компонент (Button, Chip, SegmentedControl, Surface) реализует **свою** копию одного и того же state management.

---

## 7. React модуль: MAP объекты и hooks

### 7.1 Проблема: MAP дублирование в convenience components

`VARIANT_MAP`, `INTENT_MAP`, `SIZE_MAP` дублируются **дословно** в 4+ файлах:

| MAP           |  Button.tsx   |   Chip.tsx    | SegmentedControl.tsx |  Surface.tsx  |
| ------------- | :-----------: | :-----------: | :------------------: | :-----------: |
| `VARIANT_MAP` | ✅ (5 entries) | ✅ (4 entries) |    ✅ (4 entries)     | ✅ (5 entries) |
| `INTENT_MAP`  | ✅ (3 entries) | ✅ (3 entries) |          —           | ✅ (3 entries) |
| `SIZE_MAP`    | ✅ (5 entries) | ✅ (5 entries) |    ✅ (5 entries)     |       —       |

`INTENT_MAP` и `SIZE_MAP` идентичны в **каждом** файле. `VARIANT_MAP` отличается только подмножеством (Button = все 5, Chip = без Surface).

**Решение**: `utils/enumMaps.ts`
```typescript
export const VARIANT_MAP = { solid: VisualVariant.Solid, ... } as const;
export const INTENT_MAP = { primary: ColorIntent.Primary, ... } as const;
export const SIZE_MAP = { xs: ComponentSize.Xs, ... } as const;
```

### 7.2 Проблема: Resolver boilerplate в View компонентах

Все 9 View компонентов повторяют:
```typescript
const contextTokens = useDesignTokens();
const tokens = tokensProp ?? contextTokens;
const surface = useSurfaceContext();
const style = useMemo(
    () => XxxStyleResolver.getInstance().resolve(config, tokens, surface),
    [config, tokens, surface],
);
```

**Решение**: `useResolvedStyle(config, resolver)` hook.

### 7.3 Проблема: CSS custom properties boilerplate

Интерактивные View компоненты (Button, Chip, Surface, SegmentedControl) маппят 8-12 **одинаковых** CSS variables:
```typescript
"--prefix-duration": `${tokens.motion.durationFast}ms`,
"--prefix-easing": tokens.motion.easingStandard,
"--prefix-focus-ring": tokens.color.focusRing,
"--prefix-border-width": `${tokens.borderWidth}px`,
"--prefix-focus-ring-width": `${tokens.focusRingWidth}px`,
"--prefix-disabled-opacity": tokens.state.disabledOpacity,
```

**Решение**: `buildInteractiveStyleVars(tokens, prefix)` utility.

### 7.4 Проблема: `handleClick` + `actionRoute` дублирование

5+ компонентов реализуют идентичный `useCallback`:
```typescript
const handleClick = useCallback(() => {
    if (config.isInteractive) {
        onClick?.();
        if (config.actionRoute) onAction?.(config.actionRoute!);
    }
}, [config.isInteractive, config.actionRoute, onAction, onClick]);
```

**Решение**: `useInteractiveHandler(config, onClick, onAction)`.

---

## 8. React модуль: CSS дублирование

### 8.1 Интерактивные CSS-модули

Файлы `ButtonView.module.css`, `ChipView.module.css`, `SurfaceView.module.css` содержат идентичные блоки:

| CSS паттерн                                   | Файлов | Строк/блок |
| --------------------------------------------- | ------ | ---------- |
| hover: `:hover:not([aria-disabled="true"])`   | 3      | 5-7        |
| active: `:active:not([aria-disabled="true"])` | 3      | 3-5        |
| focus-visible: `box-shadow: inset 0 0 0 ...`  | 3      | 4-6        |
| disabled: `cursor: not-allowed; opacity: ...` | 3      | 3-4        |
| spinner: `@keyframes spin` + border animation | 2      | 8          |
| ripple: `::after` pseudo-element              | 4      | 10-12      |

**Итого**: ~120 строк дублированного CSS.

### 8.2 `@keyframes spin`

Определён в **двух** CSS модулях (Button, Chip). `@keyframes` с одинаковым именем в CSS Modules получают разные хеши, но логика идентична.

---

## 9. Сводная таблица

| #   | Проблема                                                       | Модуль  | Приоритет            | Строки дубликации | Масштабируемость   |
| --- | -------------------------------------------------------------- | ------- | -------------------- | ----------------- | ------------------ |
| 1   | `scaled()` extension на каждый token                           | common  | 🔴 ВЫСОКИЙ            | ~80               | ×N tokens          |
| 2   | `SurfaceLevel → color` маппинг ×3                              | common  | 🔴 ВЫСОКИЙ            | ~40               | Фиксирован         |
| 3   | `SizeSet` / `ChipSizes` / `SegControl Sizes` (70% overlap)     | common  | 🟡 СРЕДНИЙ            | ~45               | ×N composites      |
| 4   | `SizingTokens` конфликт с `ComponentSizeResolver`              | common  | 🟡 СРЕДНИЙ            | ~35               | Архитектурный долг |
| 5   | Config fields (`id`, `testTag`, `visibility`, `isInteractive`) | common  | 🟠 ACCEPTED TRADE-OFF | ~60               | ×N components      |
| 6   | Compose View interactive boilerplate                           | compose | 🔴 ВЫСОКИЙ            | ~350              | ×N components      |
| 7   | MAP объекты (VARIANT/INTENT/SIZE)                              | react   | 🔴 ВЫСОКИЙ            | ~60               | ×N components      |
| 8   | useResolvedStyle + CSS vars boilerplate                        | react   | 🔴 ВЫСОКИЙ            | ~114              | ×N components      |
| 9   | CSS hover/focus/disabled/ripple                                | react   | 🟡 СРЕДНИЙ            | ~120              | ×N interactive     |
| 10  | handleClick + actionRoute                                      | react   | 🟡 СРЕДНИЙ            | ~35               | ×N interactive     |
|     | **ИТОГО**                                                      |         |                      | **~940 строк**    |                    |

---

## 10. Рекомендации по рефакторингу

### Фаза 1: Quick wins (устраняют ~300 строк)

#### 1.1 Common: `SurfaceLevelResolver` 
```kotlin
// foundation/SurfaceLevelResolver.kt
@JsExport
object SurfaceLevelResolver {
    fun resolveColor(level: Int, tokens: DesignTokens): String = when (level) {
        0 -> tokens.color.surface
        1 -> tokens.color.surfaceContainerLowest
        2 -> tokens.color.surfaceContainerLow
        3 -> tokens.color.surfaceContainer
        4 -> tokens.color.surfaceContainerHigh
        else -> tokens.color.surfaceContainerHighest
    }
}
```
Заменяет 3 приватных функции в SurfaceStyleResolver, PanelStyleResolver, SegmentedControlStyleResolver.

#### 1.2 React: `utils/enumMaps.ts`
Общие `VARIANT_MAP`, `INTENT_MAP`, `SIZE_MAP`, `ICON_POSITION_MAP`.

#### 1.3 React: `useResolvedStyle()` hook
```typescript
function useResolvedStyle<T>(
    config: unknown,
    resolve: (config: any, tokens: DesignTokens, surface?: SurfaceContext) => T,
    tokensProp?: DesignTokens,
): T
```

#### 1.4 React: `buildInteractiveStyleVars(tokens, prefix)` utility

### Фаза 2: Compose утилиты (устраняют ~200 строк)

#### 2.1 `InteractiveModifier` — extension на Modifier
```kotlin
fun Modifier.interactive(
    isInteractive: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    borderColor: Color,
    focusRingColor: Color,
    focusRingWidth: Dp,
    borderWidth: Dp,
    shape: Shape,
): Modifier
```

#### 2.2 `StyledText` composable
Единый composable для текста с fontSize/fontWeight/letterSpacing/lineHeight/color.

#### 2.3 `IconTextRow` composable
Общий layout для icon + text с переключаемым Row/Column.

### Фаза 3: Архитектурные решения (long-term)

#### 3.1 KSP code generation для `scaled()`
```kotlin
@AutoScale
data class SpacingTokens(val xxs: Double, val xs: Double, ...)
// → генерирует fun SpacingTokens.scaled(factor: Double): SpacingTokens
```
Устраняет проблему "забытого поля" и необходимость ручного написания.

#### 3.2 Унификация `SizingTokens` и `ComponentSizeResolver`
Либо:
- **A)** Удалить `SizingTokens.controlXs..controlXl` (они не используются компонентами)
- **B)** Сделать `ComponentSizeResolver` fallback к `SizingTokens` для нестандартных компонентов

#### 3.3 Base `ControlSizes` для Size output types
```kotlin
@JsExport @Serializable
data class ControlSizes(
    val height: Double, val fontSize: Double, val fontWeight: Int,
    val iconSize: Double, val iconGap: Double,
    val letterSpacing: Double, val lineHeight: Double,
)

@JsExport @Serializable
data class ResolvedButtonStyle(
    val colors: ColorSet,
    val base: ControlSizes,  // общие размеры
    val paddingH: Double,    // специфичные
    val paddingV: Double,
    val radius: Double,
    val isIconOnly: Boolean,
)
```

#### 3.4 CSS Layers / Base styles для React
Единый `interactive-base.module.css` с hover/focus/disabled/ripple паттернами, composable через CSS `composes:` или `@layer`.

---

## Заключение

Основной корень дублирования — ограничения `@JsExport`, заставляющие data-first архитектуру без наследования и generics. Фазы 1-2 решают ~500 строк дублирования без архитектурных изменений. Фаза 3 (KSP, рефакторинг SizingTokens) — стратегические решения, которые нужно принимать до масштабирования до 20+ компонентов.
