# Progress Log — UIKit SSR Fixes

## Session: 2026-04-01

### Анализ (предшествующий план)
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - Полный аудит React-слоя UIKit на SSR проблемы
  - Прочитаны все файлы: SurfaceContext.tsx, UIKitThemeProvider.tsx, UIKitThemeScript.tsx, useDesignTokens.tsx
  - Прочитаны все View-компоненты: ButtonView, SurfaceView, TextBlockView, SegmentedControlView
  - Прочитаны все convenience wrappers: Button, Surface, Text, SegmentedControl
  - Прочитаны app файлы: layout.tsx, page.tsx, second/page.tsx, ThemeSwitcher.tsx
  - Проанализирован Kotlin JS output: ESM, singleton pattern, pure resolvers
  - Проверен next.config.mjs: transpilePackages настроен
  - Идентифицировано 8 проблем (6 текущих + 2 будущих)
  - Для каждой проблемы определено решение, совместимое с Kotlin-first
- Findings:
  - Rules of Hooks violation в 4 файлах — критический баг
  - SurfaceContext hardcoded #FFFFFF — несовместимо с dark theme
  - 100% "use client" — блокирует Server Components
  - Hydration mismatch при system theme — SSR default = dark
  - Module-level singleton init — safe сейчас, risky при росте
  - SurfaceContextValue дублирует Kotlin SurfaceContext
- Reference: детальный анализ в findings.md

### Создание плана
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - Создан .plan-v2/task_plan.md — 7 фаз с детальными решениями
  - Создан .plan-v2/findings.md — все проблемы, решения, архитектурные заметки
  - Создан .plan-v2/progress.md — этот файл
- Files created:
  - `.plan-v2/task_plan.md`
  - `.plan-v2/findings.md`
  - `.plan-v2/progress.md`

---

### Phase 1: Rules of Hooks fix
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - Перенесены все хуки выше `if (config.visibility === Visibility.Gone) return null` в 4 файлах
  - ButtonView.tsx: useDesignTokens, useSurfaceContext, useMemo, useCallback → до early return
  - SurfaceView.tsx: useDesignTokens, useMemo×3, useCallback×2 → до early return
  - TextBlockView.tsx: useDesignTokens, useMemo → до early return
  - SegmentedControlView.tsx: useDesignTokens, useMemo, useRef, useCallback → до early return
- Files modified:
  - `core/uikit/react/src/components/atoms/button/ButtonView.tsx`
  - `core/uikit/react/src/components/atoms/surface/SurfaceView.tsx`
  - `core/uikit/react/src/components/atoms/text/TextBlockView.tsx`
  - `core/uikit/react/src/components/atoms/segmented-control/SegmentedControlView.tsx`

### Phase 2: SurfaceContext tokens integration
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - Добавлен import SurfaceContextProvider в UIKitThemeProvider.tsx
  - Обёрнуты children в SurfaceContextProvider с `{ level: 0, backgroundColor: tokens.color.surface }`
  - Добавлена мемоизация surfaceContext через useMemo
  - Заменён hardcoded `#FFFFFF` на `transparent` в SurfaceContext.tsx default (fallback для вне-Provider случаев)
- Files modified:
  - `core/uikit/react/src/theme/UIKitThemeProvider.tsx`
  - `core/uikit/react/src/theme/SurfaceContext.tsx`

### Phase 3: SurfaceContext type unification
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - Заменён `interface SurfaceContextValue` на import `SurfaceContext` из uikit-common в SurfaceContext.tsx
  - Default теперь `new SurfaceContext(0, "transparent")` (экземпляр Kotlin-класса)
  - UIKitThemeProvider: surfaceContext создаётся как `new SurfaceContext(0, tokens.color.surface)`
  - SurfaceView: surfaceContext создаётся как `new SurfaceContext(level, bg)`
  - ButtonView: экземпляр из контекста передаётся напрямую в resolver (удалён лишний `new SurfaceContext`)
  - Удалён неиспользуемый import SurfaceContext из ButtonView
  - Удалён `export type { SurfaceContextValue }` из index.ts
  - Kotlin `DefaultSurfaceContext` обновлён: `#FFFFFF` → `transparent`
- Files modified:
  - `core/uikit/react/src/theme/SurfaceContext.tsx`
  - `core/uikit/react/src/theme/UIKitThemeProvider.tsx`
  - `core/uikit/react/src/components/atoms/surface/SurfaceView.tsx`
  - `core/uikit/react/src/components/atoms/button/ButtonView.tsx`
  - `core/uikit/react/src/index.ts`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/SurfaceContext.kt`

### Phase 4: Resolved theme cookie
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - UIKitThemeScript: inline script теперь записывает cookie `uikit-resolved-theme` с resolved темой
  - UIKitThemeProvider: новый проп `initialResolved?: "light" | "dark"`
  - useSystemDark: принимает `initialResolved` и использует его как SSR default вместо hardcoded `true`
  - layout.tsx: читает `uikit-resolved-theme` cookie и передаёт в UIKitThemeProvider
- Files modified:
  - `core/uikit/react/src/theme/UIKitThemeScript.tsx`
  - `core/uikit/react/src/theme/UIKitThemeProvider.tsx`
  - `apps/catalog-ui/react/src/app/layout.tsx`

### Phase 5: Server Component Text
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - Создан TextBlockView.server.tsx — Server Component без "use client", tokens как обязательный проп
  - Создан Text.server.tsx — convenience wrapper с упрощённым API
  - Обновлён index.ts — добавлены экспорты TextServer, TextBlockViewServer
  - Обновлён package.json exports — `./text-server` entrypoint
- Files created:
  - `core/uikit/react/src/components/atoms/text/TextBlockView.server.tsx`
  - `core/uikit/react/src/components/atoms/text/Text.server.tsx`
- Files modified:
  - `core/uikit/react/src/index.ts`
  - `core/uikit/react/package.json`

### Phase 6: SSR documentation
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - Добавлен SSR-safety комментарий в DesignTokens.kt companion object
  - Добавлены SSR-safety комментарии в 4 StyleResolver objects
  - Создан docs/SSR_CONSTRAINTS.md — 6 правил + архитектурный обзор
- Files created:
  - `docs/SSR_CONSTRAINTS.md`
- Files modified:
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/DesignTokens.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonStyleResolver.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/text/TextBlockStyleResolver.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/surface/SurfaceStyleResolver.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`

### Phase 7: Verification
- **Status:** complete
- **Started:** 2026-04-01
- Actions taken:
  - ✅ `./gradlew :core:uikit:common:jsBrowserProductionLibraryDistribution` — BUILD SUCCESSFUL (9s, 17 tasks)
  - ✅ `./gradlew :apps:catalog-ui:react:build` — Next.js 15.5.14 compiled (1771ms), все маршруты сгенерированы: / (140kB), /second (144kB), /_not-found (103kB)
  - ✅ `./gradlew :apps:catalog-ui:compose:run` — BUILD SUCCESSFUL (8s, 14 tasks), Compose Desktop запустился
  - ⬚ Dev server, theme switching, RTL/LTR, surface nesting — требуют ручной визуальной проверки
- Results:
  - Все автоматические сборки прошли без ошибок
  - Kotlin JS, Next.js production, Compose Desktop — совместимы с внесёнными изменениями
  - Нет регрессий в размерах бандлов
  - Ручное тестирование (theme switching, RTL, surface nesting) рекомендуется перед merge
