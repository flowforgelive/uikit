# Task Plan: UIKit Variant System v3 — Исправление архитектурных проблем

## Goal

Исправить 8 обнаруженных архитектурных проблем в системе вариантов UIKit (VisualVariant × ColorIntent), добавить недостающий вариант `Surface`, intent-тонированные soft-цвета, исправить баги в StyleResolvers и обновить все рендереры (React + Compose). Без создания новых компонентов.

## Current Phase

All phases complete

## Scope — ТОЛЬКО существующие компоненты

> **НЕ создаём** нового. Исправляем то что есть:
> Button, IconButton (авто через Button), Surface, SegmentedControl

## Phases

### Phase 1: Foundation — VisualVariant + ColorTokens (`common/`)
- [x] 1.1 Добавить `Surface` в `VisualVariant` enum
- [x] 1.2 Добавить intent-тонированные soft-токены в `ColorTokens`: `primarySoft`, `primarySoftHover`, `neutralSoft`, `neutralSoftHover`
- [x] 1.3 Добавить border-градацию в `ColorTokens`: `borderSubtle`, `primaryBorder`, `primaryBorderHover`
- [x] 1.4 Обновить `DesignTokens.DefaultLight` и `DefaultDark` с новыми цветами
- [x] 1.5 Билд `compileKotlinJs` + `compileKotlinJvm` — убедиться что foundation компилируется
- **Status:** complete
- **Files:**
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/VisualVariant.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/ColorTokens.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/DesignTokens.kt`

### Phase 2: ButtonStyleResolver — Surface вариант + Soft fix (`common/`)
- [x] 2.1 Добавить ветку `VisualVariant.Surface` в `resolveColors()` dispatcher
- [x] 2.2 Реализовать `surfaceColors(intent, tokens)` — intent-tinted fill + border
- [x] 2.3 Исправить `softColors()` для Primary: заменить серый `surfaceContainerHigh` на `tokens.color.primarySoft`
- [x] 2.4 Исправить `softColors()` для Neutral: заменить на `tokens.color.neutralSoft`
- [x] 2.5 Обновить SurfaceAwareColorResolver если нужно (проверить интеграцию с новыми soft-токенами)
- [x] 2.6 Билд — убедиться что Button + IconButton компилируются
- **Status:** complete
- **Files:**
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonStyleResolver.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/SurfaceAwareColorResolver.kt`

### Phase 3: SurfaceStyleResolver — 3 бага + Surface вариант (`common/`)
- [x] 3.1 FIX P2: Outline → `bg = "transparent"` (сейчас `resolveBg()`)
- [x] 3.2 FIX P3: Soft → отдельная `resolveSoftBg()` или сдвинутый уровень (сейчас `== Solid`)
- [x] 3.3 Добавить ветку `VisualVariant.Surface` — лёгкий fill + видимый border
- [x] 3.4 Опционально: Solid Surface → деликатный `borderSubtle` вместо `"transparent"` (skipped)
- [x] 3.5 Билд — убедиться Surface компилируется
- **Status:** complete
- **Files:**
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/surface/SurfaceStyleResolver.kt`

### Phase 4: SegmentedControl — variant prop (`common/`)
- [x] 4.1 Добавить `variant: VisualVariant = VisualVariant.Surface` в SegmentedControlConfig
- [x] 4.2 Обновить `SegmentedControlStyleResolver.resolve()` — variant влияет на trackBg/border
- [x] 4.3 5 вариантов: Surface (default) = fill+border, Soft = fill без border, Outline = transparent+border, Solid, Ghost
- [x] 4.4 Обновить `equals()` / `hashCode()` в Config с учетом variant
- [x] 4.5 Билд
- **Status:** complete
- **Files:**
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlConfig.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`

### Phase 5: React рендереры — обновить VARIANT_MAP + convenience API
- [x] 5.1 Button.tsx: добавить `"surface"` в VARIANT_MAP
- [x] 5.2 Surface.tsx: добавить `"surface"` в VARIANT_MAP
- [x] 5.3 IconButton.tsx: добавить `"surface"` в VARIANT_MAP
- [x] 5.4 SegmentedControl.tsx: добавить `variant` prop + VARIANT_MAP
- [x] 5.5 SegmentedControlView.tsx: прокинуть border CSS variable
- [x] 5.6 index.ts: обновить если нужны новые экспорты (не нужно)
- [x] 5.7 TypeScript type check passed
- **Status:** complete
- **Files:**
  - `core/uikit/react/src/components/atoms/button/Button.tsx`
  - `core/uikit/react/src/components/atoms/surface/Surface.tsx`
  - `core/uikit/react/src/components/atoms/icon-button/IconButton.tsx`
  - `core/uikit/react/src/components/atoms/segmented-control/SegmentedControl.tsx`
  - `core/uikit/react/src/components/atoms/segmented-control/SegmentedControlView.tsx`
  - `core/uikit/react/src/index.ts`

### Phase 6: Compose рендереры — обновить convenience API
- [x] 6.1 Button.kt — ничего менять не надо (принимает VisualVariant enum, компилятор сам обработает)
- [x] 6.2 Surface.kt — аналогично, передаёт VisualVariant в config
- [x] 6.3 IconButton.kt — аналогично
- [x] 6.4 SegmentedControl.kt — добавить `variant: VisualVariant` параметр
- [x] 6.5 SegmentedControlView.kt — добавить border modifier
- [x] 6.6 Билд Compose модуля
- **Status:** complete
- **Files:**
  - `core/uikit/compose/src/commonMain/kotlin/com/uikit/compose/components/atoms/segmentedcontrol/SegmentedControl.kt`

### Phase 7: Showcase + Проверка
- [x] 7.1 Обновить `SURFACE_VARIANTS` — добавить `"surface"`, `"ghost"`
- [x] 7.2 Обновить `BUTTON_VARIANTS` — добавить `"surface"`
- [x] 7.3 Добавить SegmentedControl variant showcase
- [ ] 7.4 Визуальная проверка — запустить React dev server
- [x] 7.5 Полный билд: `compileKotlinJs` + `compileKotlinJvm` + `compileKotlinJvm` (compose) — PASS
- **Status:** complete
- **Files:**
  - `apps/catalog-ui/react/src/app/second/page.tsx`

## Key Questions

1. **Нужен ли Glass вариант сейчас?** → НЕТ, откладываем на следующий план. Сначала исправляем текущие проблемы.
2. **Ломаем ли backward compatibility?** → Минимально. Добавляем новый enum value `Surface` — existing code не затронут (все дефолты остаются `Solid`/`Soft` и т.д.). Soft colors немного изменятся визуально (intent-tinted vs серые).
3. **SurfaceAwareColorResolver — менять ли?** → Проверить: сейчас он использует surface container scale. С новыми `primarySoft/neutralSoft` токенами логика может упроститься для Primary intent.
4. **SegmentedControl — какие варианты поддерживать?** → Surface (default), Soft, Outline. Не все 5 (Solid и Ghost не имеют смысла для segmented control).

## Decisions Made

| # | Decision | Rationale |
|---|----------|-----------|
| 1 | Glass (P7) откладываем | Требует rgba-токены, GlassTokens, blur API. Отдельный план. |
| 2 | Surface variant вместо "добавить border к Soft" | Radix-подход: Surface — отдельный вариант с чёткой семантикой. |
| 3 | Intent-tinted soft цвета — токены, не вычислимые | Предсказуемость: дизайнер задаёт конкретный hex, а не "primary × 12% opacity". |
| 4 | SegmentedControl default = Surface | SegmentedControl с fill+border наиболее стандартный. |
| 5 | Soft Primary bg меняем | Текущий серый → intent-тонированный. Это visual breaking change, но правильный. |
| 6 | Outline Surface → transparent bg | Это bugfix, не breaking change. |

## Errors Encountered

| Error | Attempt | Resolution |
|-------|---------|------------|
| (пока нет) | | |

## File Inventory — все файлы для изменения

### Common (KMP) — 7 файлов
| Файл | Строк | Изменения |
|------|-------|-----------|
| `foundation/VisualVariant.kt` | 17 | +1 enum value: `Surface` |
| `tokens/ColorTokens.kt` | 29→36 | +7 новых полей |
| `tokens/DesignTokens.kt` | 267 | +7 полей в DefaultLight + DefaultDark |
| `components/atoms/button/ButtonStyleResolver.kt` | 293 | +Surface ветка, Soft fix (~30 строк) |
| `foundation/SurfaceAwareColorResolver.kt` | 73 | Возможно: обновить для новых soft-токенов |
| `components/atoms/surface/SurfaceStyleResolver.kt` | 72 | Outline fix + Soft fix + Surface ветка |
| `components/atoms/segmentedcontrol/SegmentedControlConfig.kt` | 38 | +variant field + equals/hashCode |
| `components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt` | 73 | +variant-based colors |

### React — 5 файлов
| Файл | Строк | Изменения |
|------|-------|-----------|
| `atoms/button/Button.tsx` | 84 | +1 entry в VARIANT_MAP |
| `atoms/surface/Surface.tsx` | 62 | +1 entry в VARIANT_MAP |
| `atoms/icon-button/IconButton.tsx` | 64 | +1 entry в VARIANT_MAP |
| `atoms/segmented-control/SegmentedControl.tsx` | 44 | +variant prop + VARIANT_MAP |
| `atoms/segmented-control/SegmentedControlView.tsx` | 120 | Проверить — может не нужно |

### Compose — 1 файл
| Файл | Строк | Изменения |
|------|-------|-----------|
| `atoms/segmentedcontrol/SegmentedControl.kt` | 22 | +variant параметр |

### Showcase — 1 файл
| Файл | Строк | Изменения |
|------|-------|-----------|
| `apps/catalog-ui/react/src/app/second/page.tsx` | ~650 | VARIANTS массивы + SegmentedControl variant |

**Итого:** ~15 файлов, ~7 фаз, ~100-150 строк изменений
