# UIKit Design System Unification Plan

**Goal:** Унифицировать архитектуру токенов и стилей UIKit, устранив дублирование, обеспечив Surface-Aware адаптацию цветов и соответствие индустриальным стандартам 2026 года (Material Design 3, Adobe Spectrum, Radix UI Themes).

**Created:** 2026-04-01
**Status:** Planning

---

## Executive Summary

Текущая архитектура UIKit имеет 2-уровневую систему токенов (Reference → Component), тогда как индустриальный стандарт — 3-уровневая (Reference → Semantic → Component). Это приводит к:
- O(N) дублированию кода при добавлении новых компонентов
- Невозможности Surface-Aware адаптации цветов (Soft Button невидима на Level2 Surface)
- Несогласованности размеров между компонентами
- Неиспользуемым параметрам (scaleFactor)

---

## Phases

### Phase 1: Semantic Token Layer
**Status:** `done`
**Priority:** Critical
**Effort:** Medium (2-3 days)

**Цель:** Ввести промежуточный семантический слой `InteractiveControlTokens` между raw tokens и component resolvers.

**Задачи:**
- [ ] Создать `InteractiveControlTokens` data class
- [ ] Определить `controlHeight: Map<ComponentSize, Double>`
- [ ] Определить `controlPaddingH: Map<ComponentSize, Double>`
- [ ] Определить `controlFontSize: Map<ComponentSize, TextStyle>`
- [ ] Определить `controlRadius: Map<ComponentSize, Double>`
- [ ] Определить `controlIconSize: Map<ComponentSize, Double>`
- [ ] Интегрировать в `DesignTokens`
- [ ] Мигрировать `ButtonStyleResolver` на новые токены
- [ ] Мигрировать `SegmentedControlStyleResolver` на новые токены
- [ ] Обновить тесты

**Файлы:**
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/InteractiveControlTokens.kt` (новый)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/DesignTokens.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonStyleResolver.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`

---

### Phase 2: Surface Context Propagation
**Status:** `done`
**Priority:** Critical
**Effort:** Medium (2-3 days)

**Цель:** Реализовать механизм пробрасывания `SurfaceContext` вниз по дереву компонентов для Surface-Aware адаптации цветов.

**Scope:** Compose + React (iOS/Angular — future work, платформы не существуют)

**Задачи:**
- [ ] Создать `SurfaceContext` data class (surfaceLevel, surfaceColor) в `core/uikit/common/`
- [ ] Создать `SurfaceAwareColorResolver` — вычисляет контрастные цвета относительно текущего Surface
- [ ] **Compose:** создать `LocalSurfaceContext` CompositionLocal
- [ ] **Compose:** обернуть `SurfaceView` в провайдер контекста
- [ ] Обновить `ButtonStyleResolver.resolve()` — принимать optional `SurfaceContext`
- [ ] Реализовать алгоритм выбора Soft/Ghost цветов на основе контраста
- [ ] **React:** создать `SurfaceContext` через React Context
- [ ] **React:** обернуть `SurfaceView` в Context.Provider
- [ ] Тесты: Soft Neutral Button на Level0/Level2/Level4 Surface

**Файлы:**
- `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/SurfaceContext.kt` (новый)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/SurfaceAwareColorResolver.kt` (новый)
- `core/uikit/compose/src/.../SurfaceView.kt`
- `core/uikit/react/src/theme/SurfaceContext.tsx` (новый)
- `core/uikit/react/src/components/atoms/surface/SurfaceView.tsx`

---

### Phase 3: Unified Size Resolution
**Status:** `done`
**Priority:** Medium
**Effort:** Medium (2 days)

**Цель:** Создать единый `ComponentSizeResolver`, из которого все компоненты берут базовые размеры.

**Задачи:**
- [ ] Создать `ComponentSizeResolver` object/class
- [ ] Определить единую таблицу Size → (height, paddingH, fontSize, fontWeight, iconSize, radius)
- [ ] Рефакторинг `ButtonStyleResolver.SizeSet` → использовать `ComponentSizeResolver`
- [ ] Рефакторинг `SegmentedControlStyleResolver.SegmentedControlSizes` → использовать `ComponentSizeResolver`
- [ ] Добавить `ComponentSize` enum в SegmentedControl (xs/sm/md/lg/xl)
- [ ] Обновить `SegmentedControlConfig` — добавить `size: ComponentSize`

**Файлы:**
- `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/ComponentSizeResolver.kt` (новый)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/ComponentSize.kt` (если отсутствует)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonStyleResolver.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlConfig.kt`

---

### Phase 4: Density Scaling (scaleFactor)
**Status:** `done`
**Priority:** Low
**Effort:** Low (1 day)

**Цель:** Активировать `scaleFactor` в DesignTokens для глобального масштабирования UI density.

**Задачи:**
- [ ] Обновить `ComponentSizeResolver` — умножать все размеры на `tokens.scaleFactor`
- [ ] Добавить presets: `Density.Comfortable` (1.0), `Density.Cozy` (0.9), `Density.Compact` (0.8)
- [ ] Тест: переключение density в runtime
- [ ] Документация: как использовать density scaling

**Файлы:**
- `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/Density.kt` (новый)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/ComponentSizeResolver.kt`

---

### Phase 5: Shadow Tokens & Interactive State Tokens
**Status:** `done`
**Priority:** Low
**Effort:** Low (1 day)

**Цель:** Вынести hardcoded тени и состояния в токены.

**Задачи:**
- [ ] Создать `ShadowTokens` — sm, md, lg, xl тени
- [ ] Создать `InteractiveStateTokens` — hoverOpacity, pressScale, disabledOpacity
- [ ] Мигрировать `SurfaceStyleResolver` — использовать ShadowTokens
- [ ] Мигрировать `ButtonStyleResolver` — использовать InteractiveStateTokens
- [ ] Убрать magic numbers (thumbRadius = tokens.radius.sm + 2.0 → отдельный токен)

**Файлы:**
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/ShadowTokens.kt` (новый)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/InteractiveStateTokens.kt` (новый)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/DesignTokens.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/surface/SurfaceStyleResolver.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`

---

### Phase 6: Migration & Testing
**Status:** `done`
**Priority:** High
**Effort:** Medium (2 days)

**Цель:** Полная миграция всех компонентов и платформ.

**Задачи:**
- [ ] Миграция TextBlockStyleResolver
- [ ] Миграция iOS Swift рендереров
- [ ] Миграция Angular компонентов
- [ ] Миграция React компонентов
- [ ] E2E тесты: визуальная регрессия
- [ ] Performance бенчмарки
- [ ] Документация архитектуры

---

## Decisions Log

| Date | Decision | Rationale |
|------|----------|-----------|
| 2026-04-01 | 3-уровневая архитектура токенов | Индустриальный стандарт (Material 3, Spectrum, Radix) |
| 2026-04-01 | SurfaceContext через CompositionLocal/Context | Неинвазивный способ пробрасывания контекста |
| 2026-04-01 | Единый ComponentSizeResolver | DRY, O(1) изменение размеров вместо O(N) |

---

## Errors Encountered

| Error | Attempt | Resolution |
|-------|---------|------------|
| (none yet) | | |

---

### Phase 7: Unified Size Enum
**Status:** `done`
**Priority:** Medium
**Effort:** Low (0.5 day)

**Цель:** Унифицировать size enum'ы — единый `ComponentSize` вместо `ButtonSize`, `InputSize`, etc.

**Задачи:**
- [ ] Создать/подтвердить `ComponentSize` enum (Xs, Sm, Md, Lg, Xl) в `foundation/`
- [ ] Изменить `ButtonConfig.size: ButtonSize` → `size: ComponentSize`
- [ ] Добавить `typealias ButtonSize = ComponentSize` для обратной совместимости
- [ ] Обновить React, Angular, iOS wrappers
- [ ] Обновить тесты

**Файлы:**
- `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/ComponentSize.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonConfig.kt`
- `core/uikit/react/src/components/atoms/button/Button.tsx`

---

### Phase 8: Future Platform Support (iOS/Angular)
**Status:** `blocked` (platforms not implemented yet)
**Priority:** Low
**Effort:** High (5+ days when needed)

**Цель:** Когда iOS и Angular платформы будут добавлены — реализовать SurfaceContext и унифицированные размеры.

**Задачи (future):**
- [ ] iOS Swift: `@Environment(\.surfaceContext)` SwiftUI environment
- [ ] iOS Swift: Surface renderers с context propagation
- [ ] Angular: SurfaceContext через DI/Signals
- [ ] Angular: Component renderers

**Blocker:** Платформы не существуют в текущем проекте. MEMORY.md описывает архитектуру с `iosApp/`, `angularApp/`, `sharedUI/` — это либо план на будущее, либо устаревшая документация.

---

## Dependencies Graph

```
Phase 1 (Semantic Tokens)
    │
    ├──► Phase 2 (Surface Context) ──► Phase 6 (Migration & Testing)
    │                                        ▲
    │                                        │
    └──► Phase 3 (Unified Size) ─────────────┤
                                             │
Phase 4 (Density Scaling) ───────────────────┤
                                             │
Phase 5 (Shadow Tokens) ─────────────────────┤
                                             │
Phase 7 (Unified Size Enum) ─────────────────┘

Phase 8 (iOS/Angular) ─── BLOCKED ─── depends on platforms being added
```

## Dependencies (Detailed)

- Phase 2 зависит от Phase 1 (InteractiveControlTokens нужны для SurfaceAwareColorResolver)
- Phase 3 может выполняться параллельно с Phase 2
- Phases 4-5 независимы друг от друга
- Phase 7 может выполняться параллельно с остальными
- Phase 6 (финальная миграция) зависит от Phases 1-5, 7
- **Phase 8 BLOCKED** — iOS/Angular платформы не существуют в текущем проекте
