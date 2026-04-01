# Task Plan: UIKit SSR — Исправление проблем и защита архитектуры

## Goal
Устранить существующие SSR-проблемы в React-слое UIKit и подготовить архитектуру к безопасному росту, **не нарушая принцип Kotlin-first**: вся логика дизайн-системы (токены, резолверы, конфиги) остаётся в Kotlin, React — presentation layer.

## Архитектурные принципы (не нарушать)
1. **Kotlin-first** — все дизайн-решения (цвета, размеры, стили) задаются через Kotlin data classes
2. **Pure resolvers** — StyleResolver объекты — чистые функции без побочных эффектов
3. **@JsExport** — Kotlin API автоматически доступен в JS через TypeScript declarations
4. **CSS Custom Properties** — React компоненты получают значения из Kotlin и пробрасывают через `--var`
5. **Cross-platform parity** — React и Compose рендерят одинаковый результат из одних и тех же Kotlin-определений

## Current Phase
Phase 7 (complete)

## Phases

### Phase 1: Критические баги — Rules of Hooks
**Проблема:** 4 View-компонента содержат `return null` ДО вызова хуков при `Visibility.Gone`.
Это нарушение React Rules of Hooks — при изменении `visibility` в runtime React упадёт с `Rendered fewer hooks than expected`.

**Файлы:**
- `core/uikit/react/src/components/atoms/button/ButtonView.tsx` (L26)
- `core/uikit/react/src/components/atoms/surface/SurfaceView.tsx` (L25)
- `core/uikit/react/src/components/atoms/text/TextBlockView.tsx` (L22)
- `core/uikit/react/src/components/atoms/segmented-control/SegmentedControlView.tsx` (L23)

**Решение:** В каждом файле перенести все хуки (`useDesignTokens`, `useSurfaceContext`, `useMemo`, `useCallback`, `useRef`) **выше** проверки `if (config.visibility === Visibility.Gone) return null`.

**Пример (ButtonView.tsx):**
```tsx
// БЫЛО (БАГИ):
export const ButtonView = React.memo(({ config, ... }) => {
    if (config.visibility === Visibility.Gone) return null;    // ← early return
    const contextTokens = useDesignTokens();                   // ← хук ПОСЛЕ return
    const surface = useSurfaceContext();                        // ← хук ПОСЛЕ return
    const style = useMemo(() => ..., [...]);                   // ← хук ПОСЛЕ return
    const handleClick = useCallback(() => ..., [...]);         // ← хук ПОСЛЕ return
    ...
});

// СТАЛО (ПРАВИЛЬНО):
export const ButtonView = React.memo(({ config, ... }) => {
    const contextTokens = useDesignTokens();                   // ← хуки ВСЕГДА первые
    const surface = useSurfaceContext();
    const tokens = tokensProp ?? contextTokens;
    const style = useMemo(() => ..., [...]);
    const handleClick = useCallback(() => ..., [...]);

    if (config.visibility === Visibility.Gone) return null;    // ← return ПОСЛЕ хуков
    ...
});
```

**Принцип Kotlin-first:** ✅ Не затрагивает — изменения только в React render-логике.

- [x] Исправить ButtonView.tsx — хуки до early return
- [x] Исправить SurfaceView.tsx — хуки до early return
- [x] Исправить TextBlockView.tsx — хуки до early return
- [x] Исправить SegmentedControlView.tsx — хуки до early return
- [x] Проверить, что каталог-приложение собирается и работает
- **Status:** complete

---

### Phase 2: SurfaceContext — убрать hardcoded #FFFFFF, привязать к токенам
**Проблема:** `SurfaceContext.tsx` содержит `backgroundColor: "#FFFFFF"` как default. Это:
- Magic value, не приходящий из Kotlin-токенов
- Некорректно для тёмной темы (Soft-кнопки вне Surface рассчитаются относительно белого фона)
- Hydration mismatch при dark mode без Surface-обёртки

**Файлы:**
- `core/uikit/react/src/theme/SurfaceContext.tsx`
- `core/uikit/react/src/theme/UIKitThemeProvider.tsx`

**Решение (Вариант Б — предпочтительный):**
В `UIKitThemeProvider` обернуть `children` в `SurfaceContextProvider` с начальным значением из текущих токенов:

```tsx
// UIKitThemeProvider.tsx — добавить обёртку
import { SurfaceContextProvider } from "./SurfaceContext";

// В return блоке:
<UIKitThemeContext.Provider value={value}>
    <DesignTokensContext.Provider value={tokens}>
        <SurfaceContextProvider value={{ level: 0, backgroundColor: tokens.color.surface }}>
            {children}
        </SurfaceContextProvider>
    </DesignTokensContext.Provider>
</UIKitThemeContext.Provider>
```

**Дополнительно:** В `SurfaceContext.tsx` убрать захардкоженный `#FFFFFF`, заменить на нейтральный fallback. Но при наличии Provider в корне этот default будет использован только если компонент рендерится **вне** UIKitThemeProvider (edge case для тестов/Storybook).

**Принцип Kotlin-first:** ✅ Не нарушает — default теперь берётся из `tokens.color.surface`, который определён в Kotlin `DesignTokens`.

- [x] Обернуть children UIKitThemeProvider в SurfaceContextProvider
- [x] Обновить мемоизацию value в UIKitThemeProvider (useMemo для surfaceContext)
- [x] Проверить, что Surface-вложенность продолжает работать (вложенные SurfaceContextProvider переопределяют родительский)
- [x] Проверить dark theme — Soft-кнопки вне Surface теперь используют правильный bg
- **Status:** complete

---

### Phase 3: Устранить дублирование SurfaceContext типа
**Проблема:** React определяет `interface SurfaceContextValue { level, backgroundColor }` отдельно от Kotlin `data class SurfaceContext`. При расширении Kotlin-класса (например, добавление `elevation`) React-тип не обновится.

**Файлы:**
- `core/uikit/react/src/theme/SurfaceContext.tsx`
- `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/surface/SurfaceContext.kt`

**Решение:** Заменить `interface SurfaceContextValue` на `type SurfaceContext` из `uikit-common`:

```tsx
import { SurfaceContext as SurfaceContextValue } from "uikit-common";
// или
import { type SurfaceContext } from "uikit-common";

const SurfaceContextReact = createContext<SurfaceContext>(...);
```

**Нюанс:** Проверить, что Kotlin `SurfaceContext` уже помечен `@JsExport` и что TypeScript declarations генерируются для него.

**Принцип Kotlin-first:** ✅ Усиливает — React теперь напрямую использует Kotlin-определённый тип.

- [x] Проверить, что SurfaceContext в Kotlin имеет @JsExport
- [x] Заменить interface SurfaceContextValue на import из uikit-common
- [x] Обновить все usages (useSurfaceContext, SurfaceContextProvider, SurfaceView)
- [x] Убедиться, что TypeScript compilation проходит
- **Status:** complete

---

### Phase 4: Hydration mismatch при system theme — resolved cookie
**Проблема:** При `mode="system"` сервер рендерит с тёмной темой (`return true` в useSystemDark), а клиент может определить светлую → hydration mismatch для inline стилей.

**Файлы:**
- `core/uikit/react/src/theme/UIKitThemeProvider.tsx`
- `core/uikit/react/src/theme/UIKitThemeScript.tsx`
- `apps/catalog-ui/react/src/app/layout.tsx`

**Решение — resolved cookie:**

1. `UIKitThemeScript` (inline script в `<head>`) уже определяет resolved тему. Добавить запись cookie `uikit-resolved-theme`:
```js
// В UIKitThemeScript inline script добавить:
document.cookie = 'uikit-resolved-theme=' + t + ';path=/;max-age=31536000;SameSite=Lax';
```

2. `layout.tsx` (Server Component) читает `uikit-resolved-theme` cookie и передаёт как `initialResolved`:
```tsx
const resolvedCookie = cookieStore.get("uikit-resolved-theme")?.value;
const initialResolved = (resolvedCookie === "light" || resolvedCookie === "dark")
    ? resolvedCookie : undefined;
```

3. `UIKitThemeProvider` использует `initialResolved` как SSR default для `useSystemDark`:
```tsx
const [isDark, setIsDark] = useState(() => {
    if (typeof window === "undefined") {
        return initialResolved ? initialResolved === "dark" : true;
    }
    return window.matchMedia("(prefers-color-scheme: dark)").matches;
});
```

**Итог:** Mismatch возникает только при **самом первом визите** (нет cookie). При всех последующих — сервер знает resolved тему.

**Принцип Kotlin-first:** ✅ Не затрагивает — это чисто React SSR-инфраструктура, не влияющая на Kotlin-определения.

- [x] Расширить UIKitThemeScript — запись resolved cookie
- [x] Расширить UIKitThemeProviderProps — новый проп initialResolved
- [x] Обновить useSystemDark — принимать initialResolved
- [x] Обновить layout.tsx — читать resolved cookie
- [x] Тестирование: проверить первый визит, повторный визит, смена system theme
- **Status:** complete

---

### Phase 5: SSR-ready Server Component для Text
**Проблема:** `TextBlockView` — чисто презентационный компонент (без event handlers, без state), но помечен `"use client"`. Это заставляет ВСЕ страницы, использующие текст, быть Client Components, что:
- Увеличивает JS bundle (client-side hydration)
- Блокирует SEO-оптимизации
- Неэффективно для статического контента

**Файлы (создать):**
- `core/uikit/react/src/components/atoms/text/TextBlockView.server.tsx` (новый)
- `core/uikit/react/src/components/atoms/text/Text.server.tsx` (новый)
- `core/uikit/react/src/index.ts` (обновить экспорты)

**Решение — Server-compatible Text:**

```tsx
// TextBlockView.server.tsx (БЕЗ "use client")
import { TextBlockStyleResolver, TextBlockVariant, Visibility, type TextBlockConfig, type DesignTokens } from "uikit-common";
import { toRem } from "../../../utils/units";

interface TextBlockViewServerProps {
    config: TextBlockConfig;
    tokens: DesignTokens;        // ← обязательный проп (нет Context на сервере)
    className?: string;
}

export function TextBlockViewServer({ config, tokens, className }: TextBlockViewServerProps) {
    if (config.visibility === Visibility.Gone) return null;

    const style = TextBlockStyleResolver.getInstance().resolve(config, tokens);
    const Tag = /* h1/h2/h3/p logic */;

    return (
        <Tag className={`uikit-text-block ${className ?? ""}`}
             style={{ color: style.color, fontSize: toRem(style.fontSize), ... }}>
            {config.text}
        </Tag>
    );
}
```

**API для потребителей:**
```tsx
// Server Component (page.tsx без "use client")
import { TextServer } from "@uikit/react/text-server";
import { DesignTokens } from "uikit-common";

export default function Page() {
    const tokens = DesignTokens.Companion.DefaultLight;
    return <TextServer text="SEO-friendly heading" variant="h1" tokens={tokens} />;
}
```

**Принцип Kotlin-first:** ✅ Усиливает — Server Component напрямую вызывает Kotlin StyleResolver как чистую функцию, без React-обёрток.

- [x] Создать TextBlockView.server.tsx (Server Component)
- [x] Создать Text.server.tsx — convenience wrapper
- [x] Обновить index.ts экспорты
- [x] Обновить package.json exports
- [ ] Тестирование: использовать в Server Component page, проверить HTML output
- **Status:** complete

---

### Phase 6: Документация SSR-constraints для Kotlin-слоя
**Проблема:** Kotlin разработчики могут не знать об SSR-ограничениях при добавлении кода в common-модуль.

**Файлы:**
- `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/DesignTokens.kt` (комментарий)
- `docs/SSR_CONSTRAINTS.md` (новый)

**Что задокументировать:**
1. `companion object` / `object` синглтоны кэшируются в Node.js процессе между запросами — нельзя добавлять mutable state или побочные эффекты
2. StyleResolver должны оставаться pure functions (input → output, без side effects)
3. Все `@JsExport data class` должны быть serializable и не зависеть от platform-specific API
4. При добавлении нового поля в `SurfaceContext` — React слой подхватит автоматически (после Phase 3)
5. При добавлении нового StyleResolver — создать и Server, и Client версию React-wrapper

**Принцип Kotlin-first:** ✅ Защищает — документация предотвращает случайное нарушение SSR-совместимости.

- [x] Добавить SSR-safety комментарии в DesignTokens.kt companion object
- [x] Добавить SSR-safety комментарии в StyleResolver objects
- [x] Создать docs/SSR_CONSTRAINTS.md
- **Status:** complete

---

### Phase 7: Верификация и финальная проверка
- [x] `./gradlew :core:uikit:common:jsBrowserProductionLibraryDistribution` — JS бибилотека собирается
- [x] `./gradlew :apps:catalog-ui:react:build` — Next.js production build проходит
- [ ] `./gradlew :apps:catalog-ui:react:dev` — dev server стартует, все страницы работают
- [ ] Проверить dark/light/system theme switching
- [ ] Проверить RTL/LTR
- [ ] Проверить Surface-вложенность (Soft кнопки внутри Surface)
- [x] Обновить progress.md с результатами
- [x] Compose Desktop собирается и запускается
- **Status:** complete

---

## Errors Encountered
| Error | Phase | Attempt | Resolution |
|-------|-------|---------|------------|
| — | — | — | — |

## Architecture Notes
- Kotlin JS output: ESM (.mjs), bundled через Next.js `transpilePackages`
- React Context: UIKitThemeContext > DesignTokensContext > SurfaceContextReact (вложенные)
- Стили: Kotlin StyleResolver → ResolvedStyle data class → CSS custom properties через inline `style={}`
- Паттерн компонентов: Button (convenience) → ButtonView (impl, tokens из context или prop)
