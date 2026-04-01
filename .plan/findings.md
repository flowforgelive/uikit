# UIKit Design System — Research Findings

**Updated:** 2026-04-01

---

## 1. Архитектурный анализ: Token Layers

### 1.1 Текущая архитектура (2-уровневая)

```
Raw Tokens (DesignTokens)
    │
    └── Component Resolvers (ButtonStyleResolver, etc.)
            │
            └── Resolved Styles (ResolvedButtonStyle, etc.)
```

### 1.2 Индустриальный стандарт (3-уровневая)

| Уровень | Material Design 3 | Adobe Spectrum | Radix UI | UIKit (текущий) |
|---------|-------------------|----------------|----------|-----------------|
| **Reference** (сырые) | `md.ref.palette.secondary90` | `--spectrum-gray-100` | `--gray-1` | `ColorTokens.primary` ✓ |
| **Semantic** (намерение) | `md.sys.color.primary-container` | `--spectrum-semantic-background-100` | `--color-background` | **ОТСУТСТВУЕТ** ❌ |
| **Component** (специфичный) | `md.comp.button.label-text.color` | `--spectrum-button-background-color` | Button `variant` | `ButtonStyleResolver` ✓ |

### 1.3 Проблема: кросс-компонентные заимствования

**Код из SegmentedControlStyleResolver.kt:58:**
```kotlin
height = tokens.sizing.buttonSm,  // SegmentedControl заимствует у Button
```

**Код из SizingTokens.kt:**
```kotlin
data class SizingTokens(
    val buttonXs: Double,  // 24.0 — button-specific naming
    val buttonSm: Double,  // 32.0
    val buttonMd: Double,  // 40.0
    val buttonLg: Double,  // 48.0
    val buttonXl: Double,  // 56.0
)
```

**Семантическая ошибка:** SegmentedControl не "хочет быть как кнопка". Оба являются **интерактивными контролами** одного размера. Должен быть промежуточный слой `controlHeight.sm = 32.0`.

---

## 2. Surface-Aware Color Adaptation (Critical)

### 2.1 Проблема: Ghost/Soft кнопки не адаптируются к фону

**ButtonStyleResolver.resolve() signature:**
```kotlin
fun resolve(config: ButtonConfig, tokens: DesignTokens): ResolvedButtonStyle
```

Resolver **не знает** текущий SurfaceLevel. Он не может адаптировать цвета.

### 2.2 Конкретные поломки

#### Soft Neutral Button на разных Surface:

| Surface | Surface BG | Soft Button BG | Результат |
|---------|------------|----------------|-----------|
| Level0 (light) | `#FFFFFF` | `surfaceContainerLow = #F7F7F7` | Еле видна (2% контраст) |
| Level2 (light) | `#F7F7F7` | `surfaceContainerLow = #F7F7F7` | **НЕВИДИМА (0% контраст)** |
| Level4 (light) | `#EAEAEA` | `surfaceContainerLow = #F7F7F7` | Инвертирована — кнопка светлее фона |

**Вывод:** Soft Neutral Button на Surface Level2 **буквально невидима**. Это не edge-case — это паттерн "карточка с кнопкой".

### 2.3 Решения в индустрии

| Система | Подход |
|---------|--------|
| Material 3 | `surface-tint-color` + elevation overlay |
| Adobe Spectrum | Contextual color tokens — контекст контейнера автоматически адаптирует цвета |
| Radix UI | Тема пробрасывается через Context, компоненты используют CSS custom properties |
| Tailwind | Manual `bg-*` классы — нет автоматики |

### 2.4 Рекомендуемое решение

```kotlin
data class SurfaceContext(
    val level: Int,           // 0-5
    val backgroundColor: Long // ARGB
)

// Compose
val LocalSurfaceContext = staticCompositionLocalOf { SurfaceContext(0, 0xFFFFFFFF) }

// В SurfaceRenderer
@Composable
fun SurfaceRenderer(config: SurfaceConfig, content: @Composable () -> Unit) {
    val context = SurfaceContext(config.level, resolvedBackgroundColor)
    CompositionLocalProvider(LocalSurfaceContext provides context) {
        content()
    }
}

// В ButtonStyleResolver
fun resolve(
    config: ButtonConfig,
    tokens: DesignTokens,
    surfaceContext: SurfaceContext  // NEW
): ResolvedButtonStyle
```

---

## 3. Size Definitions Inconsistency

### 3.1 Button SizeSet (ButtonStyleResolver.kt:85-93)

```kotlin
data class SizeSet(
    val height: Double,       // tokens.sizing.buttonMd = 40
    val paddingH: Double,     // tokens.spacing.lg = 16
    val fontSize: Double,     // tokens.typography.body.fontSize = 17
    val fontWeight: Int,      // tokens.typography.headline.fontWeight = 600
    val iconSize: Double,
    val letterSpacing: Double,
)
```

### 3.2 SegmentedControl Sizes (SegmentedControlStyleResolver.kt:48-55)

```kotlin
data class SegmentedControlSizes(
    val height: Double,       // tokens.sizing.buttonSm = 32
    val paddingH: Double,     // tokens.spacing.sm = 8
    val fontSize: Double,     // tokens.typography.caption1.fontSize = 12
    val radius: Double,
    val thumbRadius: Double,
    val trackPadding: Double,
)
```

### 3.3 Несоответствия

| Свойство | Button | SegmentedControl | Проблема |
|----------|--------|------------------|----------|
| height | 40 (Md) | 32 (Sm) | SC не имеет size enum |
| fontWeight | 600 | отсутствует | Если меняем weight контролов — меняем в N местах |
| letterSpacing | есть | отсутствует | Inconsistent |

### 3.4 Рекомендация: Unified Size Table

```kotlin
object ComponentSizeResolver {
    fun resolve(size: ComponentSize, tokens: DesignTokens): ResolvedComponentSize {
        return when (size) {
            Xs -> ResolvedComponentSize(
                height = 24.0 * tokens.scaleFactor,
                paddingH = tokens.spacing.xs,
                fontSize = tokens.typography.caption2.fontSize,
                fontWeight = 600,
                iconSize = 12.0,
                radius = tokens.radius.sm
            )
            Sm -> ResolvedComponentSize(...)
            Md -> ResolvedComponentSize(...)
            Lg -> ResolvedComponentSize(...)
            Xl -> ResolvedComponentSize(...)
        }
    }
}
```

---

## 4. Unused Parameters

### 4.1 scaleFactor

**DesignTokens.kt:**
```kotlin
data class DesignTokens(
    ...
    val scaleFactor: Double,  // = 1.0 — НИКОГДА НЕ ИСПОЛЬЗУЕТСЯ
)
```

**Ни один resolver не обращается к scaleFactor.**

### 4.2 Radix UI пример

Radix UI Themes:
```css
--scaling: 90% | 95% | 100% | 105% | 110%
height: calc(var(--space-5) * var(--scaling));
```

Один параметр → глобальное масштабирование всего UI.

---

## 5. Hardcoded Values (Magic Numbers)

### 5.1 SegmentedControlStyleResolver.kt

```kotlin
thumbRadius = tokens.radius.sm + 2.0,  // magic number
trackPadding = 2.0,                     // magic number
```

### 5.2 SurfaceStyleResolver.kt

```kotlin
shadow = "0 1px 3px 0 rgba(0,0,0,0.1), 0 1px 2px -1px rgba(0,0,0,0.1)"  // hardcoded CSS
```

### 5.3 ButtonStyleResolver.kt

```kotlin
radius = tokens.radius.md,  // hardcoded для всех размеров кнопок
```

### 5.4 Рекомендация

Создать `ShadowTokens`:
```kotlin
data class ShadowTokens(
    val sm: Shadow,  // elevation 1
    val md: Shadow,  // elevation 2
    val lg: Shadow,  // elevation 3
    val xl: Shadow,  // elevation 4
)

data class Shadow(
    val offsetX: Double,
    val offsetY: Double,
    val blur: Double,
    val spread: Double,
    val color: Long,
    val opacity: Double
)
```

---

## 6. Additional Findings

### 6.1 SegmentedControl не имеет ComponentSize enum

**ButtonConfig.kt:**
```kotlin
data class ButtonConfig(
    val size: ComponentSize = ComponentSize.Md,
    ...
)
```

**SegmentedControlConfig.kt:**
```kotlin
data class SegmentedControlConfig(
    // NO SIZE PARAMETER
    val items: List<Item>,
    ...
)
```

SegmentedControl всегда одного размера. Это ограничение — в реальных UI нужны разные размеры SC.

### 6.2 ColorIntent vs VisualVariant naming

```kotlin
// ColorIntent.kt
enum class ColorIntent { Neutral, Primary, Success, Warning, Danger }

// VisualVariant.kt
enum class VisualVariant { Solid, Soft, Ghost, Outline }
```

Это **правильно** и соответствует индустрии. ColorIntent = semantic intent, VisualVariant = visual treatment.

### 6.3 ThemeProvider pattern

```kotlin
interface ThemeProvider {
    val tokens: DesignTokens
}

class InMemoryThemeProvider(override val tokens: DesignTokens) : ThemeProvider
```

**Хорошо:** Абстракция позволяет подменять источник токенов.
**Улучшение:** Добавить `surfaceContext: StateFlow<SurfaceContext>` для reactive updates.

---

## 7. Cross-Platform Consistency Risks

### 7.1 Текущие platform implementations

| Platform | File | Risk |
|----------|------|------|
| Compose | `ButtonRenderer.kt` | Medium — единый resolver |
| iOS Swift | `ButtonView.swift` (SKIE bundled) | **High** — отдельная логика стилей |
| Angular | `button.component.ts` | **High** — отдельная логика |
| React | `Button.tsx` | **High** — отдельная логика |

### 7.2 Проблема

Если resolver логика дублируется на каждой платформе, изменение токена/алгоритма требует изменения в 4 местах.

### 7.3 Рекомендация

`ResolvedButtonStyle` (result of resolver) должен быть **единственным источником правды**. Платформы только рендерят resolved values, не вычисляют цвета/размеры самостоятельно.

---

## 8. Performance Considerations

### 8.1 Текущие оптимизации (хорошо)

- `remember(variant, state, colors)` в Compose — кеширование стилей
- Structural sharing в ViewModel — cached subtrees
- Reference-stable JS state — `JsSampleState` cached in Kotlin

### 8.2 Потенциальные проблемы с Surface Context

Если `SurfaceContext` меняется часто (анимации, scroll), перевычисление стилей может быть дорогим.

**Митигация:**
- Мемоизация `SurfaceAwareColorResolver` по (surfaceLevel, colorIntent, visualVariant)
- Использовать `derivedStateOf` в Compose для batched updates

---

## 9. Additional Findings (Extended Analysis)

### 9.1 ButtonSize vs ComponentSize — Divergent Size Enums

**Обнаружено:** Button использует собственный `ButtonSize` enum, а не общий `ComponentSize`.

**ButtonConfig.kt:11-15:**
```kotlin
enum class ButtonSize {
    Sm,
    Md,
    Lg,
}
```

**Проблема:** Каждый компонент может определять свой size enum:
- Button → `ButtonSize` (Sm, Md, Lg)
- SegmentedControl → нет size вообще (фиксированный)
- Будущий Input → `InputSize`?
- Будущий Select → `SelectSize`?

**Рекомендация:** Единый `ComponentSize` enum (Xs, Sm, Md, Lg, Xl) для всех компонентов. Component-specific size enum как alias: `typealias ButtonSize = ComponentSize`.

### 9.2 SurfaceContext отсутствует на ВСЕХ платформах

**Compose (SurfaceView.kt:91-93):**
```kotlin
Box(...) {
    content()  // NO CONTEXT PROPAGATION
}
```

**React (SurfaceView.tsx:76):**
```tsx
<Tag ...>
    {children}  {/* NO CONTEXT PROPAGATION */}
</Tag>
```

Ни одна платформа не пробрасывает Surface context детям. Это архитектурный пробел, требующий исправления на **всех 4 платформах** (Compose, React, iOS, Angular).

### 9.3 React использует CSS Custom Properties (хорошо)

**SurfaceView.tsx:59-70:**
```tsx
style={{
    "--surface-bg": style.bg,
    "--surface-bg-hover": style.bgHover,
    "--surface-border": style.border,
    // ...
}}
```

Это правильный паттерн для web. Однако для Surface-Aware адаптации потребуется React Context.

### 9.4 StyleResolver Singleton Pattern (React)

**SurfaceView.tsx:29:**
```tsx
const style = SurfaceStyleResolver.getInstance().resolve(config, tokens);
```

React использует `getInstance()` (singleton) вместо прямого вызова `SurfaceStyleResolver.resolve()`. Это Kotlin/JS особенность — object singleton экспортируется как `getInstance()`.

### 9.5 Angular отсутствует в core/uikit

Компоненты Angular находятся в `angularApp/`, а не в `core/uikit/angular/`. Это означает:
- Нет shared Angular renderer library
- Angular компоненты дублируют логику стилей
- Изменения нужно делать в 2 местах: `core/uikit/react/` и `angularApp/`

**Рекомендация:** Создать `core/uikit/angular/` для унификации.

### 9.6 Web компоненты используют Kotlin/JS StyleResolver

**Positive finding:** React ButtonView использует тот же `SurfaceStyleResolver` из Kotlin/JS:
```tsx
import { SurfaceStyleResolver } from "uikit-common";
```

Это означает, что логика стилей централизована в Kotlin, а не дублируется на TypeScript. Однако SurfaceContext придётся реализовать на каждой платформе отдельно.

### 9.7 Текущая структура проекта (фактическая)

```
uikit/
├── core/uikit/
│   ├── common/     # Kotlin Multiplatform — токены, resolvers, configs
│   ├── compose/    # Compose renderers
│   └── react/      # React renderers (TypeScript + Kotlin/JS)
├── apps/catalog-ui/
│   ├── compose/    # Compose demo app
│   └── react/      # React demo app
└── docs/
```

**Важно:** iOS (Swift) и Angular **отсутствуют** в текущей реализации. MEMORY.md описывает архитектуру с `iosApp/`, `angularApp/`, `sharedUI/` — это либо план, либо устаревшая информация.

### 9.8 Платформы требующие Surface Context implementation

| Платформа | Статус | Требуется |
|-----------|--------|-----------|
| Compose | Существует | `LocalSurfaceContext` CompositionLocal |
| React | Существует | `SurfaceContext` React Context |
| iOS (Swift) | **НЕ существует** | `@Environment(\.surfaceContext)` — отложено |
| Angular | **НЕ существует** | DI/Signals context — отложено |

---

## 10. References

### 9.1 Material Design 3
- Token architecture: Reference → System → Component
- Dynamic color via `Material You` algorithm
- Elevation overlays for surfaces

### 9.2 Adobe Spectrum
- `component-height-100/200/300/400` — unified height scale
- Contextual color tokens
- `--spectrum-semantic-*` variables

### 9.3 Radix UI Themes
- `scaling: 90% | 95% | 100% | 105% | 110%` — global density
- CSS custom properties everywhere
- `appearance` prop for color schemes

### 9.4 Brad Frost (Design System Authority)
- 3-tier token architecture
- "Design tokens are the subatomic particles of a design system"

### 9.5 EightShapes
- Component spec methodology
- Token naming conventions
