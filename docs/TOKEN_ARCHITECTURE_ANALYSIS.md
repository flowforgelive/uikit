# Анализ архитектуры токенов UIKit — Полный аудит

> Документ покрывает ВСЕ категории токенов (не только radius), выявляет архитектурные gap-ы
> и предлагает решения с учётом ROADMAP: SSR, BDUI, Generative UI, DesignAlgorithm.

---

## Содержание

- [Анализ архитектуры токенов UIKit — Полный аудит](#анализ-архитектуры-токенов-uikit--полный-аудит)
	- [Содержание](#содержание)
	- [1. Карта потребления: Компонент × Категория токена](#1-карта-потребления-компонент--категория-токена)
		- [Ключевые наблюдения](#ключевые-наблюдения)
	- [2. Проблемы по категориям](#2-проблемы-по-категориям)
		- [2.1 Radius: дублирование capping + Panel без cap](#21-radius-дублирование-capping--panel-без-cap)
		- [2.2 Motion: CSS hardcodes vs MotionTokens](#22-motion-css-hardcodes-vs-motiontokens)
		- [2.3 State: InteractiveStateTokens не токенизированы в CSS](#23-state-interactivestatetokens-не-токенизированы-в-css)
		- [2.4 Spacing: каталог showcases hardcode размеры](#24-spacing-каталог-showcases-hardcode-размеры)
		- [2.5 Colors: полная токенизация ✅](#25-colors-полная-токенизация-)
		- [2.6 Typography: полная токенизация ✅](#26-typography-полная-токенизация-)
		- [2.7 Sizing: Image/Skeleton не связаны с ComponentSize](#27-sizing-imageskeleton-не-связаны-с-componentsize)
		- [2.8 Shadows: полная токенизация ✅](#28-shadows-полная-токенизация-)
	- [3. Showcase hardcodes (каталог приложения)](#3-showcase-hardcodes-каталог-приложения)
		- [Showcase: что должно быть adaptive vs explicit](#showcase-что-должно-быть-adaptive-vs-explicit)
	- [4. Архитектурные gap-ы: готовность к BDUI / Generative UI / SSR](#4-архитектурные-gap-ы-готовность-к-bdui--generative-ui--ssr)
		- [4.1 SSR Safety — ✅ Фундамент готов](#41-ssr-safety---фундамент-готов)
		- [4.2 BDUI Readiness — ⚠️ 80% готов](#42-bdui-readiness--️-80-готов)
		- [4.3 Generative UI Readiness — ⚠️ 60% готов](#43-generative-ui-readiness--️-60-готов)
		- [4.4 DesignAlgorithm Readiness](#44-designalgorithm-readiness)
	- [5. Решения](#5-решения)
		- [5.1 AdaptiveRadiusResolver](#51-adaptiveradiusresolver)
		- [5.2 CSS Module Token Adoption](#52-css-module-token-adoption)
		- [5.3 Showcase Token Propagation](#53-showcase-token-propagation)
		- [5.4 Motion/State в ResolvedStyle (подготовка к DesignAlgorithm)](#54-motionstate-в-resolvedstyle-подготовка-к-designalgorithm)
		- [5.5 SpacingTokens Strategy](#55-spacingtokens-strategy)
	- [6. Приоритезация](#6-приоритезация)
		- [Phase 0 — Немедленно (\< 1 день)](#phase-0--немедленно--1-день)
		- [Phase 1 — Скоро (\< 3 дня)](#phase-1--скоро--3-дня)
		- [Phase 2 — При DesignAlgorithm (ROADMAP Phase)](#phase-2--при-designalgorithm-roadmap-phase)
		- [Не требует действий (by design)](#не-требует-действий-by-design)
	- [Приложение: Сводная матрица готовности](#приложение-сводная-матрица-готовности)

---

## 1. Карта потребления: Компонент × Категория токена

Какие категории токенов реально использует каждый StyleResolver:

| Компонент         | Color    | Controls | Radius     | Motion | State  | Shadow | Spacing | Typography | Sizing | SurfaceCtx |
| ----------------- | -------- | -------- | ---------- | ------ | ------ | ------ | ------- | ---------- | ------ | ---------- |
| **Button**        | ✅ ICR    | ✅ CSR    | ✅ via CSR  | ❌ View | ❌ View | ❌      | ❌       | ❌ via CSR  | ❌      | ✅ opt      |
| **IconButton**    | ✅ ICR    | ✅ CSR    | ✅ via CSR  | ❌ View | ❌ View | ❌      | ❌       | ❌          | ❌      | ✅ opt      |
| **Chip**          | ✅ ICR    | ✅ CSR    | ✅ via CSR  | ❌ View | ❌ View | ❌      | ❌       | ❌ via CSR  | ❌      | ✅ opt      |
| **SegmentedCtrl** | ✅ custom | ✅ CSR    | ✅ via CSR  | ❌ View | ❌ View | ❌      | ❌       | ❌ via CSR  | ❌      | ✅          |
| **Surface**       | ✅ lvl    | ❌        | ✅ static   | ❌ View | ❌ View | ✅      | ❌       | ❌          | ❌      | ❌          |
| **Panel**         | ✅ lvl    | ❌        | ⚠️ partial  | ✅      | ❌      | ✅      | ❌       | ❌          | ❌      | ❌          |
| **TextBlock**     | ✅ emph   | ❌        | ❌          | ❌      | ❌      | ❌      | ❌       | ✅          | ❌      | ❌          |
| **Icon**          | ✅ pass   | ✅ CSR    | ❌          | ❌      | ❌      | ❌      | ❌       | ❌          | ✅      | ❌          |
| **Divider**       | ✅ dflt   | ❌        | ❌          | ❌      | ❌      | ❌      | ❌       | ❌          | ❌      | ❌          |
| **Image**         | ✅ bdr    | ❌        | ✅ adaptive | ❌      | ❌      | ❌      | ❌       | ❌          | ❌      | ❌          |
| **Skeleton**      | ✅ clr    | ❌        | ✅ adaptive | ❌      | ❌      | ❌      | ❌       | ✅ textLine | ❌      | ❌          |

**Легенда**: ICR = InteractiveColorResolver, CSR = ComponentSizeResolver, View = потребляется на уровне View (не Resolver)

### Ключевые наблюдения

1. **Motion** — не потребляется ни одним StyleResolver, передаётся напрямую в View (CSS vars / Compose animation). Panel — единственный, кто включает `durationMs`/`easing` в resolved style.
2. **InteractiveStateTokens** (hoverOpacity, pressBrightness, rippleSpread) — не попадают в resolved styles, потребляются hardcoded в CSS.
3. **SpacingTokens** — НЕ используются ни одним StyleResolver/View компонента. Spacing существует в токенах, но компоненты используют `paddingH` из `ControlSizeScale`.
4. **SizingTokens** — используются только Icon (для size lookup), остальные идут через `ControlSizeScale`.

---

## 2. Проблемы по категориям

### 2.1 Radius: дублирование capping + Panel без cap

**Severity: P1**

**Проблема 1 — Дублирование логики**:
```kotlin
// ImageStyleResolver.kt:
val adaptive = min(width, height) * radiusFraction
val effectiveRadius = minOf(adaptive, maxContainerRadius)

// SkeletonStyleResolver.kt:
val adaptive = height * radiusFraction
val effectiveRadius = minOf(adaptive, maxContainerRadius)
```
Одна и та же формула `minOf(computed, maxContainerRadius)` скопирована. При добавлении Card, Avatar, Badge — снова копирование.

**Проблема 2 — Panel Inset без cap**:
```kotlin
// PanelStyleResolver.kt:
val radius = tokens.radius.lg * (radiusFraction / DEFAULT_RADIUS_FRACTION)
// При radiusFraction=1.0: 12 * (1.0/0.2) = 60dp — без ограничения!
```

**Проблема 3 — Surface не участвует в адаптивной системе**:
Surface использует статические `tokens.radius.{xs|sm|md|lg|xl|full}` через `SurfaceShape` enum. Это осознанное решение — Surface = контейнер с выбранной shape, не адаптивный. Но будущий `Card` (= Surface + content) может нуждаться в адаптивном radius.

**Решение → [5.1](#51-adaptiveradiusresolver)**

---

### 2.2 Motion: CSS hardcodes vs MotionTokens

**Severity: P1**

**Проблема**: MotionTokens определены (durationNormal=200, easingStandard="cubic-bezier(0.2,0,0,1)"), но в CSS Modules transition-значения **хардкодятся**:

| Файл CSS               | Hardcode                       | Должно быть                       |
| ---------------------- | ------------------------------ | --------------------------------- |
| ButtonView.module.css  | `300ms` (ripple)               | `var(--btn-ripple-fade-duration)` |
| ButtonView.module.css  | `70%` (ripple spread)          | `var(--btn-ripple-spread)`        |
| ButtonView.module.css  | `0.12` (press opacity)         | `var(--btn-press-opacity)`        |
| ButtonView.module.css  | `0.88` (press brightness)      | `var(--btn-press-brightness)`     |
| ChipView.module.css    | `300ms`, `70%`, `0.88`         | аналогично                        |
| SurfaceView.module.css | `300ms`, `70%`, `0.08`, `0.92` | аналогично                        |

**Контекст**: React View TSX-файлы **уже передают** CSS-переменные (`--btn-ripple-fade-duration`, etc.) из resolved tokens. Но CSS Modules **не подхватывают их** — используют літерали.

**Compose**: Полностью токенизирован через `animateColorAsState` / `animateDpAsState` с token-based durations. ✅

**Решение → [5.2](#52-css-module-token-adoption)**

---

### 2.3 State: InteractiveStateTokens не токенизированы в CSS

**Severity: P2**

**Проблема**: `InteractiveStateTokens` содержит 7 параметров:
- `hoverOpacity` (0.08) — overlay opacity при hover
- `pressOpacity` (0.12) — overlay при press
- `disabledOpacity` (0.6) — opacity disabled компонента
- `pressBrightness` (0.88) — filter brightness при press
- `pressBrightnessSurface` (0.92) — filter brightness для Surface (мягче)
- `rippleSpread` (70) — размер ripple в %
- `rippleFadeDurationMs` (300) — длительность ripple fade

Все эти значения **не попадают в ResolvedStyle** ни одного компонента — они потребляются на уровне View. В React View-файлах они **уже передаются как CSS variables**, но CSS Modules используют литеральные значения (см. 2.2).

**Почему это проблема для BDUI/Generative UI**: Если DesignAlgorithm хочет сменить стиль интерактивности (например, Illustrated → offset press вместо brightness filter), нужно чтобы эти параметры были **полностью token-driven**, а не hardcoded.

**Связь с 2.2**: Решается тем же фиксом — заменой литералов в CSS на `var(--...)`.

---

### 2.4 Spacing: каталог showcases hardcode размеры

**Severity: P2 (showcase-only, не затрагивает компонентную библиотеку)**

**Проблема**: В showcase-файлах каталога (Image, Skeleton, Surface, Divider) все размеры контейнеров — литералы:
```kotlin
// ImageShowcase:
ImageView(ImageConfig(width = 120.0, height = 80.0, cornerRadius = 40.0, ...))
ImageView(ImageConfig(width = 200.0, height = 140.0, cornerRadius = 70.0, ...))

// SkeletonShowcase:
SkeletonView(SkeletonConfig(width = 200.0, height = 120.0, cornerRadius = 30.0, ...))
SkeletonView(SkeletonConfig(width = 300.0, height = 40.0, ...))

// SurfaceShowcase:
width = 120.0  // контейнер

// DividerShowcase:
thickness = 3.0  // литерал
```

**Последствие**: Ползунки `globalSize` и `globalRadius` в каталоге не влияют на эти секции.

**Нюанс**: Image/Skeleton — контентные примитивы. Они НЕ связаны с ComponentSize по архитектуре (у кнопки есть "размер", у картинки — нет, картинка имеет произвольные width/height). Но showcase должен демонстрировать реакцию на глобальные параметры.

**Решение → [5.3](#53-showcase-token-propagation)**

---

### 2.5 Colors: полная токенизация ✅

**Статус: OK**

- **InteractiveColorResolver** — centralized матрица `VisualVariant × ColorIntent → ColorSet`. Используется Button, Chip, IconButton.
- **SurfaceContext** — `NestingColorStrategy` для адаптации Soft-вариантов к nesting depth.
- **SegmentedControl** — собственная color resolution (по variant: Surface/Soft/Outline/Solid/Ghost), но использует те же tokens.
- **TextBlock** — emphasis → color mapping через tokens (`textPrimary`, `textSecondary`, `textMuted`, `textDisabled`).
- **Surface** — level-based resolution (`resolveBg(level)` → surfaceContainerLowest...surfaceContainerHighest).

**Gap**: `NestingColorStrategy` жёстко привязан к 3 уровням (depth 0/1/2+). При DesignAlgorithm = Illustrated может быть другая стратегия, но это решается на уровне DesignAlgorithm (см. ROADMAP § 6).

---

### 2.6 Typography: полная токенизация ✅

**Статус: OK**

- 15 стилей (Display/Headline/Title/Body/Label × Large/Medium/Small) определены в `TypographyTokens`.
- `TextBlockStyleResolver` корректно разрешает `variant → TextStyle → fontSize/fontWeight/lineHeight/letterSpacing`.
- Interactive control typography через `ControlSizeInput.fontSize/fontWeight/letterSpacing`.
- `fontFamilyName` и `fontVariationSettings` в DesignTokens на корневом уровне.

---

### 2.7 Sizing: Image/Skeleton не связаны с ComponentSize

**Статус: By Design (не проблема)**

Image и Skeleton — **контентные примитивы**. Они принимают абсолютные `width`/`height` потому что:
- Изображение имеет intrinsic dimensions (aspect ratio)
- Skeleton placeholder имитирует конкретное содержимое

ComponentSize (Xs/Sm/Md/Lg/Xl) — это стандартизированные размеры **интерактивных контролов**: Button, Chip, SegmentedControl, IconButton. Они связаны с touch target и визуальной иерархией.

**Для BDUI/Generative UI**: Image/Skeleton будут получать width/height из layout engine (constraint-based sizing из ROADMAP § 9), а не из ComponentSize.

---

### 2.8 Shadows: полная токенизация ✅

**Статус: OK**

- `ShadowTokens` (sm/md/lg/xl + elevationDp) используется Surface и Panel напрямую.
- React: CSS string shadows → `box-shadow: var(--srf-shadow)`.
- Compose: `elevationDp` → shadow modifier.

---

## 3. Showcase hardcodes (каталог приложения)

Полная инвентаризация:

| Showcase       | Получает           | Hardcoded                                      | React ↔ Compose |
| -------------- | ------------------ | ---------------------------------------------- | --------------- |
| **Button**     | tokens, globalSize | variant/intent arrays (демо)                   | ✅ sync          |
| **IconButton** | tokens, globalSize | variant/intent arrays                          | ✅ sync          |
| **Chip**       | tokens, globalSize | variant subset                                 | ✅ sync          |
| **SegControl** | tokens, globalSize | variant list                                   | ✅ sync          |
| **Text**       | tokens             | TextBlockVariant enums                         | ✅ sync          |
| **Icon**       | tokens             | size list                                      | ✅ sync          |
| **Image**      | tokens             | **width/height: 80, 120, 200, 140, 160, 60** ❌ | ❌ sync bug      |
| **Image**      | tokens             | **cornerRadius: 8, 40, 12, 70** ❌              | ❌ sync bug      |
| **Skeleton**   | tokens             | **width/height: 80, 200, 300, 40, 56, 72** ❌   | ❌ sync bug      |
| **Skeleton**   | tokens             | **cornerRadius: 12, 30, 60** ❌                 | ❌ sync bug      |
| **Surface**    | tokens             | **width: 120** ❌                               | ❌ sync bug      |
| **Divider**    | tokens             | **thickness: 3** ❌                             | ❌ sync bug      |

**"sync bug"** = хардкоды идентичны в обеих платформах.

### Showcase: что должно быть adaptive vs explicit

Showcase должен содержать **две секции** per feature:
1. **Adaptive** (no explicit cornerRadius/size) — показывает как глобальные параметры влияют
2. **Explicit override** (explicit cornerRadius=0 / =9999) — показывает override механизм

Текущий состояние: секции перемешаны, и explicit overrides выглядят как "не реагирует на глобальный слайдер".

---

## 4. Архитектурные gap-ы: готовность к BDUI / Generative UI / SSR

### 4.1 SSR Safety — ✅ Фундамент готов

| Требование                        | Статус                                                    |
| --------------------------------- | --------------------------------------------------------- |
| StyleResolvers: pure functions    | ✅ Все `object` с `fun resolve()` — stateless, детерминизм |
| Configs: `@Serializable`          | ✅ Все data classes                                        |
| No window/document/navigator      | ✅ Common module                                           |
| React View: optional tokens prop  | ✅ SSR bypass Context                                      |
| UIKitThemeScript: FOUC prevention | ✅ Inline script в \<head\>                                |
| Cookie-based theme persistence    | ✅ uikit-theme, uikit-dir, uikit-resolved-theme            |

### 4.2 BDUI Readiness — ⚠️ 80% готов

| Требование                            | Статус | Gap                            |
| ------------------------------------- | ------ | ------------------------------ |
| Config-driven rendering               | ✅      | —                              |
| JSON serialization                    | ✅      | —                              |
| Platform-agnostic logic               | ✅      | —                              |
| Expression support in configs         | ❌      | Нет Expression Engine          |
| Layout constraints in configs         | ❌      | Нет Auto-layout                |
| Action routing (Navigate, ApiCall)    | ❌      | Нет Action Router              |
| **Полностью token-driven стили**      | ⚠️      | CSS hardcodes для state/motion |
| Component Registry (machine-readable) | ❌      | Нет формального реестра        |
| **ResolvedStyle содержит ВСЕ данные** | ⚠️      | Motion/State не в resolved     |

### 4.3 Generative UI Readiness — ⚠️ 60% готов

| Требование                             | Статус    | Gap                            |
| -------------------------------------- | --------- | ------------------------------ |
| Self-describing configs                | ✅         | —                              |
| Serializable configs                   | ✅         | —                              |
| DTCG machine-readable tokens           | ❌         | Tokens only in Kotlin          |
| Patterns layer                         | ❌         | Нет best-practice compositions |
| DesignAlgorithm interface              | ❌         | Запланирован в ROADMAP § 6     |
| DesignScale (density, spacing, type)   | ✅ partial | `scaleFactor` + `Density` есть |
| **Все визуальные параметры из tokens** | ⚠️         | CSS hardcodes                  |
| Component Registry for AI              | ❌         | Нет формального каталога       |

### 4.4 DesignAlgorithm Readiness

ROADMAP § 6 определяет `DesignAlgorithm` interface: `resolveColors`, `resolveSpacing`, `resolveRadius`, `resolveShadow`, `resolveTypography`, `resolveMotion`, `resolveBorder`.

**Для работы DesignAlgorithm нужно**:
1. ✅ Все computed values идут из tokens → StyleResolver → View
2. ⚠️ Motion/State values обходят StyleResolver → View напрямую (CSS hardcodes)
3. ❌ Нет `resolveBorder` на уровне tokens (borderWidth есть, но нет border style/color per component)

**Критический insight**: Когда `IllustratedAlgorithm` хочет сменить press-эффект с `brightness(0.88)` на `translateY(2px) + boxShadow(none)` (offset press), текущая архитектура не поддержит это — press-эффект hardcoded в CSS. Нужен token `pressEffect: "brightness" | "offset" | "scale"` на уровне DesignTokens.

---

## 5. Решения

### 5.1 AdaptiveRadiusResolver

**Цель**: Единая утилита для adaptive radius computation.

```kotlin
// foundation/AdaptiveRadiusResolver.kt

@JsExport
object AdaptiveRadiusResolver {
    /**
     * Radius для контейнеров (Image, Skeleton, Card, Avatar, Badge).
     * Explicit cornerRadius → возвращается как есть.
     * Null → adaptive: min(dimension) × radiusFraction, capped at maxContainerRadius.
     */
    fun resolve(
        explicitRadius: Double?,
        containerDimension: Double,  // min(width, height) для Image, height для Skeleton
        proportions: ControlProportions
    ): Double {
        if (explicitRadius != null) return explicitRadius
        val adaptive = containerDimension * proportions.radiusFraction
        return minOf(adaptive, proportions.maxContainerRadius)
    }
}
```

**Потребители**:
- `ImageStyleResolver` — заменить inline вычисление
- `SkeletonStyleResolver` — заменить inline вычисление
- `PanelStyleResolver` (Inset) — добавить cap:
  ```kotlin
  // Было:
  val radius = tokens.radius.lg * (radiusFraction / DEFAULT_RADIUS_FRACTION)
  // Стало:
  val scaled = tokens.radius.lg * (radiusFraction / DEFAULT_RADIUS_FRACTION)
  val radius = minOf(scaled, proportions.maxContainerRadius)
  ```
- Будущие: Card, Avatar, Badge

**Объём**: ~20 LOC utility + 3 файла рефакторинг.

---

### 5.2 CSS Module Token Adoption

**Цель**: Заменить все литералы в CSS Modules на `var(--...)`.

React View TSX-файлы **уже передают** нужные CSS-переменные. Нужно только подхватить их в CSS:

```css
/* БЫЛО: */
.ripple::after {
  animation-duration: 300ms;          /* ❌ литерал */
  width: 70%;                          /* ❌ литерал */
}
.button:active:not([aria-disabled="true"]) {
  filter: brightness(0.88);           /* ❌ литерал */
}

/* СТАЛО: */
.ripple::after {
  animation-duration: var(--btn-ripple-fade-duration, 300ms);  /* ✅ token + fallback */
  width: var(--btn-ripple-spread, 70%);                         /* ✅ token + fallback */
}
.button:active:not([aria-disabled="true"]) {
  filter: brightness(var(--btn-press-brightness, 0.88));       /* ✅ token + fallback */
}
```

**Файлы**: ButtonView.module.css, ChipView.module.css, SurfaceView.module.css, SegmentedControlView.module.css

**Объём**: ~15 строк замен в 4 CSS файлах. CSS fallback values обеспечивают SSR-безопасность.

---

### 5.3 Showcase Token Propagation

**Цель**: Showcase-файлы демонстрируют реакцию на глобальные параметры.

**Подход**:

1. **Image/Skeleton adaptive секции** — убрать explicit `cornerRadius`:
   ```kotlin
   // Было:
   ImageView(ImageConfig(width = 120.0, height = 80.0, cornerRadius = 40.0))
   // Стало:
   ImageView(ImageConfig(width = 120.0, height = 80.0))  // radius = adaptive
   ```

2. **Image/Skeleton shape override секции** — отдельная группа с явным комментарием:
   ```kotlin
   // Explicit radius overrides (не зависят от globalRadius):
   ImageView(ImageConfig(width = 80.0, height = 80.0, cornerRadius = 40.0))  // circle
   ImageView(ImageConfig(width = 200.0, height = 60.0, cornerRadius = 0.0))  // sharp
   ```

3. **Divider thickness** — оставить литерал (это демо кастомного значения), но добавить секцию "default thickness":
   ```kotlin
   DividerView(DividerConfig())  // default: tokens.borderWidth = 1.0
   DividerView(DividerConfig(thickness = 3.0))  // explicit override
   ```

4. **Surface width** — оставить (Surface — контейнер с произвольными размерами).

**Объём**: ~30 строк переупорядочивания в 4 showcase файлах × 2 платформы = 8 файлов.

---

### 5.4 Motion/State в ResolvedStyle (подготовка к DesignAlgorithm)

**Цель**: Все визуальные параметры проходят через StyleResolver → ResolvedStyle → View.

**Текущее состояние**: Motion/State идут мимо StyleResolver:
```
tokens.motion.durationNormal ──────────────────────────→ View (CSS var)
tokens.state.pressBrightness ──────────────────────────→ View (CSS var)
```

**Целевое**:
```
tokens ─→ StyleResolver.resolve() ─→ ResolvedStyle { motion, state } ─→ View
```

**Зачем**: Когда DesignAlgorithm подменяет tokens, нужно чтобы `resolve()` выдал полный набор visual params, включая motion и state. Иначе DesignAlgorithm не сможет сменить "brightness press" на "offset press".

**Предложение — НЕ сейчас**: Это Phase 2 (перед DesignAlgorithm). Сейчас фиксим CSS hardcodes (5.2), а передача motion/state через ResolvedStyle — при создании DesignAlgorithm в ROADMAP § 6.

**Причина отложить**: Добавление 7 полей (hoverOpacity, pressOpacity, pressBrightness, etc.) в каждый ResolvedStyle увеличит API surface на 70+ полей суммарно. Это premature для текущих 7 компонентов.

**Альтернативный path**: `ResolvedInteractiveState` — shared data class, один раз включается в каждый interactive resolved style:
```kotlin
@JsExport @Serializable
data class ResolvedInteractiveState(
    val hoverOpacity: Double,
    val pressOpacity: Double,
    val pressBrightness: Double,
    val rippleSpread: Double,
    val rippleFadeDurationMs: Double,
    val transitionDuration: Double,
    val transitionEasing: String,
)
```

---

### 5.5 SpacingTokens Strategy

**Текущее**: SpacingTokens (xxs=2...xxxxl=64) определены, но **ни один компонент** их не использует. Padding/gap вычисляются из `ControlProportions` (`paddingH = fontSize × paddingHRatio`).

**Вопрос**: Нужны ли SpacingTokens в компонентах?

**Ответ**: Нет на уровне компонентов. SpacingTokens нужны для:
1. **Patterns** (ROADMAP § 4, Level 4) — gap между компонентами в layout
2. **Auto-layout Engine** (ROADMAP § 9) — constraint-based spacing
3. **BDUI** — `LayoutConfig.spacing` для серверных layout

**Вывод**: SpacingTokens архитектурно корректны, их consumers ещё не реализованы. Это НЕ gap.

---

## 6. Приоритезация

### Phase 0 — Немедленно (< 1 день)

| #   | Задача                                                             | Severity | LOC | Файлов |
| --- | ------------------------------------------------------------------ | -------- | --- | ------ |
| 1   | **Panel Inset cap** — добавить `minOf(scaled, maxContainerRadius)` | P0       | 3   | 1      |
| 2   | **AdaptiveRadiusResolver** — утилита в foundation/                 | P1       | 20  | 1 new  |
| 3   | **Image/Skeleton refactor** — использовать AdaptiveRadiusResolver  | P1       | 6   | 2      |

### Phase 1 — Скоро (< 3 дня)

| #   | Задача                                                    | Severity | LOC | Файлов |
| --- | --------------------------------------------------------- | -------- | --- | ------ |
| 4   | **CSS hardcodes → var()** — motion/state в CSS Modules    | P1       | 15  | 4      |
| 5   | **Showcase cleanup** — adaptive vs explicit radius секции | P2       | 30  | 8      |

### Phase 2 — При DesignAlgorithm (ROADMAP Phase)

| #   | Задача                                                        | Severity |
| --- | ------------------------------------------------------------- | -------- |
| 6   | `ResolvedInteractiveState` — motion/state через StyleResolver | P3       |
| 7   | Press effect token (`brightness` / `offset` / `scale`)        | P3       |
| 8   | DTCG JSON → Kotlin codegen (§ 5)                              | P3       |
| 9   | Component Registry for AI/BDUI                                | P3       |

### Не требует действий (by design)

- Image/Skeleton не связаны с ComponentSize — контентные примитивы
- Surface статический radius — shape enum контейнера
- SpacingTokens не используются компонентами — для Patterns/Layout
- SizingTokens частично дублируют ControlSizeScale — legacy, не мешает

---

## Приложение: Сводная матрица готовности

| Цель                | Цвета | Размеры | Radius | Motion   | State    | Shadows | Typography | Spacing  |
| ------------------- | ----- | ------- | ------ | -------- | -------- | ------- | ---------- | -------- |
| **Runtime theme**   | ✅     | ✅       | ✅      | ⚠️ CSS    | ⚠️ CSS    | ✅       | ✅          | N/A      |
| **SSR**             | ✅     | ✅       | ✅      | ✅        | ✅        | ✅       | ✅          | N/A      |
| **BDUI**            | ✅     | ✅       | ⚠️ cap  | ⚠️ CSS    | ⚠️ CSS    | ✅       | ✅          | ❌ layout |
| **DesignAlgorithm** | ✅     | ✅       | ⚠️ cap  | ❌ bypass | ❌ bypass | ✅       | ✅          | ❌ layout |
| **Generative UI**   | ✅     | ✅       | ⚠️ cap  | ❌ bypass | ❌ bypass | ✅       | ✅          | ❌ layout |

**⚠️ CSS** = токены определены и передаются, но CSS Modules используют литералы.
**❌ bypass** = параметры обходят StyleResolver, не управляются через единый pipeline.
**❌ layout** = SpacingTokens есть, но layout engine ещё не реализован.
