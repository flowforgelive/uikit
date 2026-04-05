# UIKit Design System — Руководство для AI-агентов

Кросс-платформенный UIKit: Kotlin Multiplatform (Common) → Compose (JVM) + React (Next.js SSR).
Все числовые значения — `Double` (абстрактные dp). Все цвета — `String` (hex). Эталонный компонент: **Button**.

## Структура модулей

```
core/uikit/
├── common/    ← KMP : Config + StyleResolver + токены
│   ├── foundation/    ← базовые типы: ComponentSize, VisualVariant, ColorIntent
│   ├── tokens/        ← DesignTokens, ColorTokens, InteractiveControlTokens, etc.
│   └── components/
│       ├── primitives/{component}/    ← неделимые: Text, Surface
│       ├── composites/{component}/    ← составные: Button, IconButton, SegmentedControl
│       └── blocks/{component}/        ← самодостаточные блоки: Panel
├── compose/   ← Compose рендереры (~30 LOC каждый)
│   └── components/{primitives|composites|blocks}/{component}/  ← {Component}.kt + {Component}View.kt
└── react/     ← React рендереры + CSS Modules
    ├── components/{primitives|composites|blocks}/{component-name}/  ← {Component}.tsx + {Component}View.tsx + {Component}View.module.css
    ├── theme/     ← UIKitThemeProvider, useDesignTokens, SurfaceContext
    └── index.ts   ← реестр всех экспортов
```

Пакеты Kotlin: `com.uikit.foundation`, `com.uikit.tokens`, `com.uikit.components.{primitives|composites|blocks}.{name}`.
Директории React: kebab-case (`segmented-control/`). Файлы React/Compose: PascalCase.

## Архитектура компонента: Config → StyleResolver → View

Каждый компонент реализуется через 3 слоя:

1. **Config** (`common/`) — `@JsExport @Serializable data class`. Только данные + computed `get()`.
   Никакой логики, side-effects, mutable state. Дефолты через параметры конструктора.
2. **StyleResolver** (`common/`) — `@JsExport object`. Stateless singleton, pure function `resolve(config, tokens, surfaceContext?) → ResolvedStyle`.
   SSR-безопасен: нет window/document/navigator, детерминизм (одни входы → один выход).
3. **View** (`compose/` и `react/`) — только рендеринг, без бизнес-логики. Принимает Config + опциональный tokens prop (SSR).

Выходные типы StyleResolver — `@JsExport @Serializable data class`:
- `ColorSet` — bg, bgHover, text, textHover, border, borderHover
- `SizeSet` / `{Component}Sizes` — height, paddingH, fontSize, fontWeight, iconSize, etc.
- `Resolved{Component}Style` — объединяет colors + sizes + radius и прочее

## Чек-лист: создание нового компонента

### Common (KMP)
- [ ] `{Component}Config.kt` — data class с `@JsExport @Serializable`. Общие поля в конце: `id`, `testTag`, `visibility`
- [ ] `{Component}StyleResolver.kt` — object с `fun resolve(config, tokens, surfaceContext?) → Resolved{Component}Style`
- [ ] Выходные data classes (`ColorSet`, `{Component}Sizes`, `Resolved{Component}Style`) — `@JsExport @Serializable`
- [ ] Размеры через `tokens.resolveSize(config.size)` (extension из `ComponentSizeResolver.kt`)
- [ ] Цвета через `InteractiveColorResolver.resolve(variant, intent, tokens, surfaceContext)` для интерактивных компонентов
- [ ] Disabled: `InteractiveColorResolver.resolveDisabled(tokens)`
- [ ] Vertical layout: `ComponentSizeResolver.resolveVerticalLayout(scale, isVertical)`
- [ ] Строковые константы: `ColorConstants.TRANSPARENT`, `ColorConstants.SHADOW_NONE`

### React
- [ ] `{Component}.tsx` — convenience API: string props (`variant?: "solid" | "soft"`) → Config через MAP-объекты
- [ ] `{Component}View.tsx` — config-based API: `useMemo(() => Resolver.getInstance().resolve(config, tokens, surface))`
- [ ] `{Component}View.module.css` — стили через CSS custom properties: `var(--{prefix}-{prop})`
- [ ] Оба компонента обёрнуты в `React.memo()`, с `displayName`
- [ ] Добавить экспорты в `react/src/index.ts`: convenience + View + Config + Resolver

### Compose
- [ ] `{Component}.kt` — convenience @Composable: параметры напрямую, `modifier: Modifier = Modifier` **всегда последний**
- [ ] `{Component}View.kt` — config-based @Composable: `val style = remember(config) { Resolver.resolve(config, tokens) }`
- [ ] Цвета: `parseColor(style.colors.bg)`, размеры: `style.sizes.height.dp`

## Конвенции именования

| Контекст                  | Стиль               | Примеры                                 |
| ------------------------- | ------------------- | --------------------------------------- |
| Kotlin классы/enum values | PascalCase          | `ButtonConfig`, `ComponentSize.Md`      |
| Kotlin свойства           | camelCase           | `bgHover`, `fontSize`, `letterSpacing`  |
| React компоненты          | PascalCase          | `Button`, `ButtonView`                  |
| React props               | camelCase           | `onClick`, `variant`, `size`            |
| React prop string values  | kebab-case          | `"solid"`, `"primary"`, `"md"`          |
| CSS custom properties     | `--{prefix}-{prop}` | `--btn-bg`, `--ib-size`, `--seg-height` |
| CSS классы (modules)      | camelCase           | `.button`, `.content`, `.icon`          |
| Composable функции        | PascalCase          | `Button()`, `ButtonView()`              |

Префиксы CSS переменных по компонентам: Button → `btn`, IconButton → `ib`, SegmentedControl → `seg`, Surface → `srf`.

## Размеры и токены

Все интерактивные компоненты используют **единый** `ComponentSize` enum: `Xs | Sm | Md | Lg | Xl`.

Размеры вычисляются пропорционально через `ComponentSizeResolver`:
- `height = fontSize × heightRatio (2.5)`
- `paddingH = fontSize × paddingHRatio (1.0)`
- `iconSize = fontSize × iconSizeRatio (1.2)`
- `radius = height × radiusFraction (0.2)`
- Все масштабируются через `scaleFactor` для density

**Запрещено**: хардкодить пиксельные значения. Все размеры берутся из `DesignTokens` и `InteractiveControlTokens`.

React: `toRem(dpValue)` для всех размеров. Compose: `value.dp` / `value.sp`.

## Цвета и варианты

- **VisualVariant**: `Solid | Soft | Outline | Ghost` — визуальный стиль
- **ColorIntent**: `Primary | Neutral | Danger` — смысловой цвет
- Каждый интерактивный компонент поддерживает матрицу `VisualVariant × ColorIntent`
- **Disabled всегда отдельно** — не зависит от variant/intent, использует `tokens.color.surfaceDisabled/textDisabled`
- **SurfaceContext** — компоненты адаптируют Soft-цвета к фону родительского Surface

## Стилизация

### React
- Все стили через **CSS custom properties** в inline `style={{}}` → потребляются в `.module.css`
- Переходы через `transition` по CSS-переменным `--{prefix}-duration` и `--{prefix}-easing`
- `aria-disabled="true"` вместо `disabled` атрибута. `:focus-visible` для фокуса.
- Hover/active/focus через CSS селекторы: `.button:hover:not([aria-disabled="true"])`

### Compose
- `remember(config) { Resolver.resolve(config, tokens) }` — мемоизация
- `modifier` chaining: `.defaultMinSize()`, `.background()`, `.clip()`, `.clickable()`
- Цвета: `parseColor(hex)`. Нет Material3 компонентов — кастомные Box-based реализации.

## SSR-безопасность

- StyleResolvers: stateless, pure functions, no singletons with mutable state
- React View-компоненты: опциональный `tokens` prop (SSR bypass Context)
- Все Config data classes: `@Serializable` — готовность к JSON SDUI/BDUI
- `"use client"` директива на React-компонентах с интерактивностью

## Контекст развития

Структура компонентов организована по 3-уровневой таксономии: `primitives/` (неделимые: Text, Surface) → `composites/` (составные: Button, SegmentedControl) → `blocks/` (самодостаточные: Panel).
Полный список будущих компонентов и архитектурных решений: `docs/ROADMAP.md`.
Все Config уже `@Serializable` — это фундамент для будущего BDUI-движка.

## Shared Foundation Utilities

| Утилита                    | Пакет                       | Назначение                                                                             |
| -------------------------- | --------------------------- | -------------------------------------------------------------------------------------- |
| `InteractiveColorResolver` | `foundation/`               | Shared цветовая матрица `VisualVariant × ColorIntent → ColorSet` + `resolveDisabled()` |
| `ComponentSizeResolver`    | `foundation/`               | `resolve()` — размеры, `resolveVerticalLayout()` — vertical icon layout                |
| `ColorConstants`           | `foundation/`               | `TRANSPARENT`, `SHADOW_NONE` — замена magic strings                                    |
| `ColorSet`                 | `foundation/`               | Общий тип цветового контракта для всех интерактивных компонентов                       |
| `VerticalLayout`           | `foundation/`               | Результат `resolveVerticalLayout()` — height + paddingV                                |
| `tokens.resolveSize()`     | extension на `DesignTokens` | Convenience: `tokens.resolveSize(config.size)` (Kotlin-only, не экспортируется в JS)   |

## Known KMP Trade-offs

Осознанные архитектурные компромиссы из-за ограничений `@JsExport`:

1. **`isInteractive` копируется в каждый Config** — `@JsExport data class` не поддерживает интерфейсы с default implementations. Каждый Config (Button, IconButton, etc.) содержит `val isInteractive: Boolean get() = !disabled && !loading`. Это 1 строка на Config — приемлемо.

2. **Общие поля (`id`, `testTag`, `visibility`) повторяются** — нельзя наследовать интерфейсы, sealed classes, или использовать abstract data class с `@JsExport`. Копирование 3-6 полей в каждый Config — осознанный trade-off. При масштабировании до 50+ компонентов — рассмотреть KSP code generation.

3. **Extension functions не видны из JS** — `tokens.resolveSize()` доступен только в Kotlin StyleResolvers. Это допустимо, т.к. JS-потребители вызывают `Resolver.resolve(config, tokens)`, а не работают с `ComponentSizeResolver` напрямую.
