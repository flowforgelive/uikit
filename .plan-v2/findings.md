# Findings & Decisions — UIKit SSR

## Requirements
- Исправить SSR-проблемы в React-слое UIKit
- Не нарушить принцип Kotlin-first (вся логика дизайн-системы в Kotlin)
- Не ломать существующий функционал (Compose Desktop, React Catalog)
- Обеспечить path к Server Components для SEO/performance

---

## Обнаруженные проблемы (результат анализа)

### Проблема 1: КРИТИЧЕСКАЯ — Rules of Hooks violation
**Серьёзность:** Критическая
**Когда стрельнёт:** При использовании `Visibility.Gone` в runtime (SDUI, условный рендер)
**Где:** 4 файла — ButtonView.tsx, SurfaceView.tsx, TextBlockView.tsx, SegmentedControlView.tsx
**Суть:** Early `return null` вызывается **до** вызова хуков (`useDesignTokens`, `useSurfaceContext`, `useMemo`, `useCallback`, `useRef`). React требует, чтобы хуки вызывались безусловно, в одном порядке.
**Почему не падает сейчас:** `Visibility.Gone` нигде не устанавливается в текущем MVP.
**Что сломается:** React Error: `Rendered fewer hooks than expected. This may be caused by an accidental early return statement.`

### Проблема 2: ВЫСОКАЯ — SurfaceContext.tsx hardcoded #FFFFFF
**Серьёзность:** Высокая
**Когда стрельнёт:** При использовании Soft-кнопок в dark theme без Surface-обёртки
**Где:** `core/uikit/react/src/theme/SurfaceContext.tsx` (L10-13)
**Суть:** Default context содержит `backgroundColor: "#FFFFFF"` — magic value, не связанный с Kotlin токенами. `SurfaceAwareColorResolver` использует этот цвет для расчёта Soft-вариантов.
**Последствия:**
  - Soft-кнопки вне Surface рассчитываются относительно белого фона даже в dark mode
  - Визуальный flash при hydration (сервер рендерит "#FFFFFF", клиент — реальный surface)
  - Нарушение Kotlin-first: цвет не из токенов

### Проблема 3: СРЕДНЯЯ — 100% Client Components
**Серьёзность:** Средняя (блокирует будущие оптимизации)
**Когда стрельнёт:** При SEO/performance требованиях
**Где:** Все компоненты в `core/uikit/react/src/components/`
**Суть:** Все компоненты помечены `"use client"`, включая чисто презентационные (TextBlockView). Потребители UIKit не могут использовать Server Components для статического контента.
**Важно:** `"use client"` НЕ означает "не рендерится на сервере". Next.js рендерит Client Components в HTML на сервере. Но:
  - Компоненты требуют hydration JS на клиенте
  - Server Components не могут импортировать их без boundary
  - Весь код попадает в client bundle

### Проблема 4: СРЕДНЯЯ — Hydration mismatch при mode=system
**Серьёзность:** Средняя
**Когда стрельнёт:** При первом визите с theme=system
**Где:** `UIKitThemeProvider.tsx` — функция `useSystemDark()`
**Суть:** Сервер **всегда** возвращает `isDark = true` при SSR (`typeof window === "undefined"`). Если пользователь в светлой теме, inline стили на компонентах будут для тёмной темы → flash + hydration warning.
**UIKitThemeScript** смягчает проблему для CSS (ставит `data-theme`), но inline `style={}` на компонентах всё равно содержат серверные значения.

### Проблема 5: НИЗКАЯ — Module-level Kotlin singleton init
**Серьёзность:** Низкая (текущий код safe)
**Когда стрельнёт:** При добавлении side effects в Kotlin companion object
**Где:** `useDesignTokens.tsx` (L9): `const defaultTokens = DesignTokens.Companion.Default`
**Суть:** Kotlin синглтоны при компиляции в JS кэшируются в Node.js между запросами. Если кто-то добавит mutable state или побочные эффекты — данные будут "протекать" между запросами.

### Проблема 6: НИЗКАЯ — дублирование SurfaceContext типа
**Серьёзность:** Низкая
**Когда стрельнёт:** При расширении Kotlin `SurfaceContext`
**Где:** React `interface SurfaceContextValue` vs Kotlin `data class SurfaceContext`
**Суть:** Два независимых определения одной структуры. Расхождение при изменении Kotlin-класса.

---

## Technical Decisions

| Решение | Альтернатива | Почему выбрано |
|---------|-------------|----------------|
| Хуки до early return (Phase 1) | Обернуть в HOC | Минимальное изменение, стандартный React-паттерн |
| SurfaceContextProvider в UIKitThemeProvider (Phase 2) | useSurfaceContext с fallback на tokens | Единая точка инициализации, не добавляет хук |
| Использовать Kotlin-тип SurfaceContext (Phase 3) | Оставить interface | Kotlin-first: один источник правды |
| Resolved cookie (Phase 4) | CSS custom properties для всех цветов | Не ломает текущую inline-style архитектуру |
| Server-only TextBlockView (Phase 5) | Убрать "use client" с TextBlockView | Отдельный файл = обратная совместимость |
| Документация SSR constraints (Phase 6) | Lint rules | Прагматичнее для текущего масштаба команды |

---

## Исследование архитектуры

### Стек рендеринга (текущий)
```
layout.tsx (Server Component)
  → UIKitThemeScript (Server Component — inline <script>)
  → UIKitThemeProvider (Client Component)
      → UIKitThemeContext.Provider
          → DesignTokensContext.Provider
              → page.tsx (Client Component)
                  → Surface → SurfaceContextProvider → Button/Text
```

### Стек рендеринга (после фиксов)
```
layout.tsx (Server Component)
  → UIKitThemeScript (Server Component — inline <script> + resolved cookie)
  → UIKitThemeProvider (Client Component)
      → UIKitThemeContext.Provider
          → DesignTokensContext.Provider
              → SurfaceContextProvider (level=0, bg=tokens.color.surface)  ← НОВОЕ
                  → page.tsx (Client или Server Component)
                      → TextServer (Server Component, tokens prop)     ← НОВОЕ
                      → Surface → SurfaceContextProvider → Button/Text
```

### Kotlin JS Module
- **Формат:** ESM (.mjs)
- **Bundling:** Next.js `transpilePackages: ["@uikit/react", "uikit-common"]`
- **Singleton access:** `StyleResolver.getInstance().resolve(config, tokens, ...)` — safe, stateless
- **Companion object:** `DesignTokens.Companion.DefaultLight / DefaultDark` — pure data, no side effects
- **Window/Document deps:** НУЛЬ в Kotlin коде. Только в React слое с `typeof` проверками.

### Context hierarchy
1. `UIKitThemeContext` — mode, resolvedMode, tokens, dir, setMode, setDir
2. `DesignTokensContext` — shortcuts для `useDesignTokens()` (вложен в #1)
3. `SurfaceContextReact` — level + backgroundColor (вложен в Surface компонент, скоро + в #1)

### CSS strategy
- **CSS Modules** для layout/interaction (.module.css)
- **CSS Custom Properties** для динамических значений от Kotlin (`--btn-bg`, `--surface-radius`)
- **Inline style={{}}** для пробрасывания CSS vars
- **globals.css** для `[data-theme="dark"]` и `[dir="rtl"]`

---

## Риски и ограничения

| Риск | Вероятность | Митигация |
|------|-------------|-----------|
| Kotlin JS не загрузится в Node.js SSR | Низкая | transpilePackages уже настроен, ESM → CJS |
| Server Components не смогут импортить Kotlin singletons | Средняя | Тестировать в Phase 5, возможно нужна обёртка |
| Resolved cookie десинхронизируется с system theme | Низкая | UIKitThemeScript обновляет cookie при каждой загрузке |
| Добавление нового хука после early return fix | Нулевая | Стандартный паттерн, ESLint `react-hooks/rules-of-hooks` подхватит |
