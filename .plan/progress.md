# UIKit Design System — Progress Log

---

## Session 1: Initial Analysis
**Date:** 2026-04-01
**Duration:** ~30 min
**Status:** Complete

### Completed
- [x] Прочитан весь код токенов и resolvers в `core/uikit/common/src/`
- [x] Проанализированы файлы:
  - `DesignTokens.kt` — main token container
  - `SizingTokens.kt` — button-specific naming issue
  - `SpacingTokens.kt` — ok
  - `ColorTokens.kt` — reference layer ok
  - `RadiusTokens.kt` — ok
  - `TypographyTokens.kt` — ok
  - `MotionTokens.kt` — ok
  - `ButtonStyleResolver.kt` — SizeSet, color logic
  - `ButtonConfig.kt` — ButtonSize (not ComponentSize!)
  - `SurfaceStyleResolver.kt` — hardcoded shadows
  - `SurfaceConfig.kt` — level-based
  - `SegmentedControlStyleResolver.kt` — borrows from button, magic numbers
  - `SegmentedControlConfig.kt` — no size enum
  - `TextBlockStyleResolver.kt` — simple
  - `ColorIntent.kt`, `VisualVariant.kt` — enums ok
  - `ThemeProvider.kt`, `InMemoryThemeProvider.kt` — abstraction ok
- [x] Проанализированы Compose renderers:
  - `UIKitTheme.kt` — LocalDesignTokens present, NO LocalSurfaceContext
  - `SurfaceView.kt` — no context propagation to children
  - `ButtonView.kt` — uses LocalDesignTokens, memoizes style
  - `Surface.kt`, `Button.kt` — thin wrappers
- [x] Проанализированы React renderers:
  - `useDesignTokens.tsx` — Context-based, no SurfaceContext
  - `SurfaceView.tsx` — CSS custom properties, no context propagation
  - `ButtonView.tsx` — uses Kotlin/JS StyleResolver (good!)
  - `Surface.tsx`, `Button.tsx` — thin wrappers
- [x] Исследованы best practices 2026:
  - Material Design 3 — 3-tier tokens, dynamic color
  - Adobe Spectrum — component-height scale, contextual colors
  - Radix UI Themes — scaling, CSS custom properties
  - Brad Frost — token architecture
  - EightShapes — spec methodology
- [x] Идентифицированы 8 основных проблем (было 6 + 2 новых)
- [x] Создан task_plan.md с 8 фазами
- [x] Создан findings.md с детальным анализом
- [x] Обновлены findings.md и task_plan.md после дополнительного анализа

### Key Findings (Updated)

| # | Проблема | Критичность | Усилия |
|---|----------|-------------|--------|
| 1 | Surface-Aware адаптация цветов | **Critical** | Medium |
| 2 | Отсутствие семантического слоя токенов | **Critical** | Medium |
| 3 | Независимые SizeSet в каждом компоненте | Medium | Low |
| 4 | scaleFactor не используется | Low | Low |
| 5 | Hardcoded shadows/magic numbers | Low | Low |
| 6 | SegmentedControl без ComponentSize | Medium | Low |
| 7 | **NEW:** ButtonSize vs ComponentSize — divergent enums | Medium | Low |
| 8 | **NEW:** Angular компоненты не в core/uikit/ | Low | Medium |

### Files Created
- `.plan/task_plan.md` — 8 фаз (Phase 8 blocked)
- `.plan/findings.md` — 10+ разделов анализа
- `.plan/progress.md` (this file)

### Important Discovery: Project Structure
Фактическая структура проекта **отличается** от MEMORY.md:
- **Существует:** `core/uikit/common/`, `core/uikit/compose/`, `core/uikit/react/`
- **НЕ существует:** `iosApp/`, `angularApp/`, `sharedUI/`

iOS и Angular платформы **не реализованы**. Phase 8 (iOS/Angular) помечена как `blocked`.

### Next Steps
1. ~~Начать Phase 1: Semantic Token Layer~~ DONE
2. ~~Создать `InteractiveControlTokens.kt`~~ DONE
3. ~~Рефакторить SizingTokens — убрать button-specific naming~~ DONE

---

## Session 2: Phase 1 — Semantic Token Layer
**Date:** 2026-04-01
**Status:** Complete

### Completed
- [x] Создан `InteractiveControlTokens.kt` — `ControlSizeScale` (height, paddingH, fontSize, fontWeight, iconSize, letterSpacing, radius) × 5 sizes (xs/sm/md/lg/xl)
- [x] `SizingTokens` — переименованы `buttonXs..buttonXl` → `controlXs..controlXl`
- [x] `DesignTokens` — добавлен `controls: InteractiveControlTokens` + `defaultControls` preset
- [x] `ButtonStyleResolver` — мигрирован на `tokens.controls.{sm,md,lg}` (включая radius per-size)
- [x] `SegmentedControlStyleResolver` — мигрирован на `tokens.controls.sm`
- [x] `CatalogApp.kt` — обновлены ссылки `sizing.button*` → `sizing.control*`
- [x] `page.tsx` — обновлены ссылки
- [x] Билд: JVM ✓, JS ✓, Compose ✓, Catalog ✓

### Key Changes
- Button теперь получает radius из control tokens (per-size), а не глобальный `radius.md`
- SegmentedControl thumbRadius вычисляется как `scale.radius - 2.0` вместо `radius.sm + 2.0`
- Семантический слой позволяет будущим компонентам (Input, Select) использовать те же размеры

### Files Modified
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/InteractiveControlTokens.kt` (new)
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/SizingTokens.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/DesignTokens.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonStyleResolver.kt`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`
- `apps/catalog-ui/compose/src/commonMain/kotlin/CatalogApp.kt`
- `apps/catalog-ui/react/src/app/second/page.tsx`

### Next Steps
1. Phase 2: Surface Context Propagation
2. Phase 3: Unified Size Resolution (ComponentSizeResolver)

---

## Session 3: Phase 2 + 3 + 7 Implementation
**Date:** 2026-04-01
**Status:** Complete

### Phase 2: Surface Context Propagation — Completed
- [x] Created `SurfaceContext` data class in `com.uikit.foundation` (@JsExport)
- [x] Created `SurfaceAwareColorResolver` — adapts Soft bg colors relative to current surface
- [x] Created `LocalSurfaceContext` CompositionLocal in Compose UIKitTheme
- [x] Updated Compose `SurfaceView` — propagates SurfaceContext via CompositionLocalProvider
- [x] Updated Compose `ButtonView` — reads LocalSurfaceContext, passes to resolver
- [x] Updated `ButtonStyleResolver.resolve()` — new optional `surfaceContext` parameter
- [x] Updated `softColors()` — uses SurfaceAwareColorResolver when surfaceContext is present
- [x] Created React `SurfaceContext.tsx` — React Context for surface context
- [x] Updated React `SurfaceView.tsx` — wraps children in SurfaceContextProvider
- [x] Updated React `ButtonView.tsx` — reads useSurfaceContext, creates Kotlin SurfaceContext for resolver
- [x] Updated React `index.ts` — exports SurfaceContext, useSurfaceContext, SurfaceContextProvider

### Phase 3: Unified Size Resolution — Completed
- [x] Created `ComponentSize` enum (Xs/Sm/Md/Lg/Xl) in `com.uikit.foundation`
- [x] Created `ComponentSizeResolver` object — maps ComponentSize → ControlSizeScale
- [x] Migrated `ButtonConfig` — `ButtonSize` → `typealias ButtonSize = ComponentSize`
- [x] Migrated `ButtonStyleResolver` — uses ComponentSizeResolver.resolve() instead of when-blocks
- [x] Added `size: ComponentSize` to `SegmentedControlConfig` (default: Sm)
- [x] Updated `SegmentedControlStyleResolver.resolve(config, tokens)` — uses ComponentSizeResolver
- [x] Added backward-compatible `resolve(tokens)` overload with @JsName("resolveDefault")
- [x] Updated Compose `SegmentedControlView` — passes config to resolver
- [x] Updated React `SegmentedControlView.tsx` — passes config to resolver
- [x] Updated React `Button.tsx` — uses ComponentSize (xs/sm/md/lg/xl map)
- [x] Updated React `index.ts` — `ComponentSize as ButtonSize` for backward compat

### Build Results
- JVM: ✓, JS: ✓, Compose: ✓, Catalog: ✓, JS Distribution: ✓

### Files Created
- `core/uikit/common/.../foundation/SurfaceContext.kt`
- `core/uikit/common/.../foundation/SurfaceAwareColorResolver.kt`
- `core/uikit/common/.../foundation/ComponentSize.kt`
- `core/uikit/common/.../foundation/ComponentSizeResolver.kt`
- `core/uikit/react/src/theme/SurfaceContext.tsx`

### Files Modified
- `core/uikit/common/.../tokens/DesignTokens.kt`
- `core/uikit/common/.../components/atoms/button/ButtonConfig.kt`
- `core/uikit/common/.../components/atoms/button/ButtonStyleResolver.kt`
- `core/uikit/common/.../components/atoms/segmentedcontrol/SegmentedControlConfig.kt`
- `core/uikit/common/.../components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`
- `core/uikit/compose/.../theme/UIKitTheme.kt`
- `core/uikit/compose/.../components/atoms/surface/SurfaceView.kt`
- `core/uikit/compose/.../components/atoms/button/ButtonView.kt`
- `core/uikit/compose/.../components/atoms/segmentedcontrol/SegmentedControlView.kt`
- `core/uikit/react/src/components/atoms/surface/SurfaceView.tsx`
- `core/uikit/react/src/components/atoms/button/ButtonView.tsx`
- `core/uikit/react/src/components/atoms/button/Button.tsx`
- `core/uikit/react/src/components/atoms/segmented-control/SegmentedControlView.tsx`
- `core/uikit/react/src/index.ts`

### Next Steps
1. Phase 4: Density Scaling (scaleFactor) — Low priority
2. Phase 5: Shadow Tokens & Interactive State Tokens — Low priority
3. Phase 6: Migration & Testing — Needs rework (no iOS/Angular)

---

## Session Template

## Session 4: Phase 4 + 5 + 6 (Final)
**Date:** 2026-04-01
**Status:** Complete

### Phase 4: Density Scaling — Completed
- [x] Created `Density.kt` — Comfortable (1.0), Cozy (0.9), Compact (0.8) presets
- [x] Updated `ComponentSizeResolver.resolve()` — new `scaleFactor` param (default 1.0)
- [x] Scales height, paddingH, iconSize, radius; preserves fontSize, fontWeight, letterSpacing
- [x] Updated `ButtonStyleResolver` — passes `tokens.scaleFactor` to resolver
- [x] Updated `SegmentedControlStyleResolver` — both overloads now pass `tokens.scaleFactor`

### Phase 5: Shadow Tokens & Interactive State Tokens — Completed
- [x] Created `ShadowTokens.kt` — sm/md/lg/xl CSS box-shadow strings
- [x] Created `InteractiveStateTokens.kt` — hoverOpacity, pressOpacity, disabledOpacity
- [x] Added `shadows` and `state` fields to `DesignTokens` with defaults
- [x] Migrated `SurfaceStyleResolver` — replaced hardcoded shadow CSS with `tokens.shadows.md`
- [x] Extracted magic numbers in `SegmentedControlStyleResolver` — `TRACK_PADDING = 2.0` constant
- [x] Added JS exports: `ShadowTokens`, `InteractiveStateTokens`, `Density` in React index.ts

### Phase 6: Migration & Testing — Completed
- [x] Verified TextBlockStyleResolver — no migration needed (uses only typography tokens)
- [x] iOS/Angular platforms don't exist — N/A
- [x] React components already migrated in Session 3
- [x] Full build verification: JVM ✓, JS ✓, Compose ✓, Catalog ✓, JS Distribution ✓

### Files Created
- `core/uikit/common/.../foundation/Density.kt`
- `core/uikit/common/.../tokens/ShadowTokens.kt`
- `core/uikit/common/.../tokens/InteractiveStateTokens.kt`

### Files Modified
- `core/uikit/common/.../foundation/ComponentSizeResolver.kt`
- `core/uikit/common/.../tokens/DesignTokens.kt`
- `core/uikit/common/.../components/atoms/button/ButtonStyleResolver.kt`
- `core/uikit/common/.../components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`
- `core/uikit/common/.../components/atoms/surface/SurfaceStyleResolver.kt`
- `core/uikit/react/src/index.ts`

### Build Results
- JVM: ✓, JS: ✓, Compose: ✓, Catalog: ✓, JS Distribution: ✓

### Summary
All 7 phases of the UIKit Design System Unification plan are now complete.
Architecture upgraded from 2-level to 3-level token system.

---

## Session Template

### Session N: [Title]
**Date:** YYYY-MM-DD
**Duration:**
**Status:** In Progress | Complete

### Completed
- [ ] Task 1
- [ ] Task 2

### Blockers
- (none)

### Files Modified
- `path/to/file.kt`

### Test Results
```
./gradlew :sharedUI:jvmTest
...
```

### Next Steps
1. ...

---

## Test Results History

| Date | Command | Result |
|------|---------|--------|
| (none yet) | | |

---

## Build Status

| Date | Target | Status |
|------|--------|--------|
| (none yet) | | |
