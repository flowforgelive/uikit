# Анализ: Переиспользуемые компоненты для кроссплатформенной разработки

> Compose Multiplatform · React / React Native · iOS (SwiftUI) 

## Содержание

1. [Постановка задачи](#1-постановка-задачи)  
2. [Обзор ключевых паттернов](#2-обзор-ключевых-паттернов)  
3. [Паттерн 1: Config-Driven Design (UI Intent)](#3-паттерн-1-config-driven-design-ui-intent)  
4. [Паттерн 2: Design Tokens + Atomic Design](#4-паттерн-2-design-tokens--atomic-design)  
5. [Паттерн 3: Server-Driven UI (SDUI)](#5-паттерн-3-server-driven-ui-sdui)  
6. [Паттерн 4: Compose Multiplatform Shared UI](#6-паттерн-4-compose-multiplatform-shared-ui)  
7. [Паттерн 5: React Native New Architecture + C++ Core](#7-паттерн-5-react-native-new-architecture--c-core)  
8. [Сравнительная матрица подходов](#8-сравнительная-матрица-подходов)
9. [Рекомендуемая архитектура для Compose MP + React + iOS](#9-рекомендуемая-архитектура-для-compose-mp--react--ios)  
10. [Источники](#10-источники)

---

## 1. Постановка задачи

**Цель:** максимизировать переиспользование кода при разработке UI-компонентов для трёх платформ — Android (Compose), iOS (SwiftUI), Web (React) — при этом сохранив **нативную производительность** на каждой из них.

Слои domain / application / infrastructure хорошо переиспользуются через Kotlin Multiplatform. Основной вопрос — **как максимально переиспользовать компонентный слой (UI)** без потери перформанса.

---

## 2. Обзор ключевых паттернов

| Паттерн                             | Суть                                            | Что шарится                          | Перфоманс                            |
| ----------------------------------- | ----------------------------------------------- | ------------------------------------ | ------------------------------------ |
| **Config-Driven Design**            | Шарим "intent" UI, рендерим нативно             | Config + Style + ViewModel           | ★★★★★ нативный                       |
| **Design Tokens + Atomic Design**   | Шарим дизайн-спецификацию, рендерим из неё      | Tokens (JSON) → код каждой платформы | ★★★★★ нативный                       |
| **Server-Driven UI (SDUI)**         | Backend описывает UI, клиенты рендерят по схеме | GraphQL schema + Section components  | ★★★★☆ нативный с overhead на парсинг |
| **Compose Multiplatform Shared UI** | Один UI-код на все платформы через Compose      | Composable functions                 | ★★★★☆ Skia/native рендеринг          |
| **React Native New Architecture**   | Shared C++ renderer + JSI                       | JavaScript UI + shared C++ core      | ★★★☆☆ улучшается с Fabric            |

---

## 3. Паттерн 1: Config-Driven Design (UI Intent)

> **Источник:** Anmol Verma — "Beyond Shared Logic: Building a Whitelabel App with KMP" (ProAndroidDev, Jan 2026)

### Ключевая идея

> «Мы не шарим UI-код. Мы шарим UI intent.»

Вместо того чтобы писать UI-логику во View, она переносится в shared KMP-слой. Каждый компонент определяется через shared `Config` объект — data class, содержащий **контент и намерение** компонента.

### Архитектура

```
┌─────────────────────────────────────────────┐
│            Shared (commonMain)               │
│  ┌─────────────┐  ┌──────────────────────┐  │
│  │  Config      │  │  ViewModel           │  │
│  │  (data class)│  │  (StateFlow<Config>) │  │
│  │  - text      │  │  - events → state    │  │
│  │  - action    │  │  - use cases         │  │
│  │  - variant   │  │  - state machine     │  │
│  │  - state     │  └──────────────────────┘  │
│  │  + style()   │                            │
│  └─────────────┘                             │
├──────────────────┬──────────────────────────-┤
│ Android (Compose)│      iOS (SwiftUI)        │
│                  │                           │
│  @Composable     │  struct PButtonView: View │
│  fun PButton(    │  {                        │
│    config: ...   │    let config: KButton.   │
│  ) { ... }       │    Config                 │
│                  │    var body: some View {.. │
│                  │  }                        │
└──────────────────┴───────────────────────────┘
```

### Пример: Button

**Shared Config (commonMain):**
```kotlin
object KButton {
    @Immutable
    data class Config(
        val text: KStringDesc? = null,
        val action: (() -> Unit)? = null,
        val variant: Variant = Variant.Primary,
        val state: State = State.Default,
    ) {
        fun style(): Style = Style(variant, state)
    }

    @Immutable
    data class Style(val variant: Variant, val state: State) {
        val backgroundColor: KColorResource
            get() = when (variant) {
                Variant.Primary -> KThemeSpec.colors.buttonPrimaryBg
                Variant.Secondary -> KThemeSpec.colors.buttonSecondaryBg
            }
    }
}
```

**Android (Compose) — "тонкий" рендерер:**
```kotlin
@Composable
fun PButton(config: KButton.Config) {
    val style = config.style()
    Button(
        onClick = { config.action?.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = style.backgroundColor.toColor()
        )
    ) { /* Render content */ }
}
```

**iOS (SwiftUI) — "тонкий" рендерер:**
```swift
struct PButtonView: View {
    let config: KButton.Config
    private let style: KButton.Style

    init(config: KButton.Config) {
        self.config = config
        self.style = config.style()
    }

    var body: some View {
        Button(action: { config.action?() }) {
            // Render content
        }
        .background(Color(style.backgroundColor.name))
    }
}
```

### Shared ViewModel (State Machine)

```kotlin
class SampleViewModel(
    private val useCase: SampleUseCase
) : StateMachine<SampleConfig, SampleEvent> {

    private val _state = MutableStateFlow(SampleConfig.initial())
    override val state: StateFlow<SampleConfig> = _state

    override fun onEvent(event: SampleEvent) {
        when (event) {
            is SampleEvent.OnInputChanged -> {
                _state.update { it.copy(input = event.value) }
            }
            is SampleEvent.OnSubmitClicked -> {
                // call use case, update state
            }
        }
    }
}
```

### Результаты в production

- **~70% code sharing** (бизнес-логика, сеть, дизайн-система)
- **40% ускорение** разработки фич
- **100% нативный UI** — каждая платформа рендерит через свой SDK
- Консистентность через type system — невозможно создать компонент, нарушающий гайдлайны

### White-labeling через JSON

Каждый клиент имеет `config.json`, из которого Gradle генерирует:
- Android XML / Compose Color objects
- iOS `.xcassets`
- Feature flags → динамическая загрузка модулей

**Онбординг нового клиента:** 2 дня вместо 2 недель.

### Ключевые инструменты

- **SKIE** (Touchlab) — превращает Kotlin sealed classes в Swift enum, упрощает потребление Flow в SwiftUI
- **XcodeGen** — генерация .xcodeproj из конфигурации (не коммитим Xcode-проекты)
- **Gradle Composite Builds** — 16+ модулей (Auth, Wallet, Transactions…)

---

## 4. Паттерн 2: Design Tokens + Atomic Design

> **Источник:** Brad Frost — "Creating Themeable Design Systems" + Atomic Design methodology

### Ключевая идея

**Design Tokens** — именованные сущности, хранящие визуальные атрибуты дизайна. Вместо хардкода hex-значений или пикселей используются токены, которые транслируются в код каждой платформы.

### Трёхуровневая система переменных

```
Tier 1: Brand Definitions (абстрактные)
  --color-brand-primary: #00704a
  --font-family-primary: 'Roboto', sans-serif

          ↓ маппинг

Tier 2: Application Variables (высокоуровневые)
  --primary-action-background-color: var(--color-brand-primary)
  --border-color-subtle: var(--color-gray-07)

          ↓ маппинг

Tier 3: Component Variables (компонент-специфичные)
  --button-background-color: var(--primary-action-background-color)
  --button-font-family: var(--primary-action-font-family)
```

### Как это работает кроссплатформенно

```
┌────────────────────────────────────────────┐
│       Design Tokens (JSON / YAML)          │
│  {                                         │
│    "color": {                              │
│      "primary": { "value": "#0066FF" },    │
│      "secondary": { "value": "#FF6600" }   │
│    },                                      │
│    "spacing": {                            │
│      "sm": { "value": "8px" },             │
│      "md": { "value": "16px" }             │
│    }                                       │
│  }                                         │
└─────────────┬──────────────────────────────┘
              │ Style Dictionary / Token Pipeline
    ┌─────────┼──────────┬───────────┐
    ▼         ▼          ▼           ▼
 Compose    SwiftUI    React CSS   React Native
 Theme      Assets     Variables   StyleSheet
 object     .xcassets  :root {}    tokens.ts
```

### Инструменты

| Инструмент                    | Описание                                        |
| ----------------------------- | ----------------------------------------------- |
| **Style Dictionary** (Amazon) | Трансформация токенов в код для любой платформы |
| **Figma Tokens**              | Экспорт токенов из Figma                        |
| **Theo** (Salesforce)         | Компиляция дизайн-токенов                       |
| **Specify**                   | Sync дизайн-токенов между Figma и кодом         |

### Преимущество для multi-brand

Меняете Tier 1 (Brand Definitions) → все компоненты автоматически обновляются:
- Один JSON → N брендов
- Компоненты не меняются, меняются только токены

---

## 5. Паттерн 3: Server-Driven UI (SDUI)

> **Источник:** Airbnb Engineering — "A Deep Dive into Airbnb's Server-Driven UI System" (Ghost Platform)

### Ключевая идея

Backend контролирует **и данные, и то, как эти данные отображаются** на всех клиентах одновременно. Клиенты не знают контекста данных — они получают описание UI и рендерят его нативно.

### Архитектура Ghost Platform (Airbnb)

```
┌──────────────────────────────────────┐
│           Backend (GraphQL)           │
│  GPResponse:                         │
│  ├── screens[]                       │
│  │   ├── layout (ILayout)            │
│  │   └── placements → sectionIds     │
│  ├── sections[]                      │
│  │   ├── sectionComponentType        │
│  │   └── section data model          │
│  └── actions[] (IAction)             │
└──────────────┬───────────────────────┘
               │  Единая GraphQL Schema
    ┌──────────┼──────────┬────────────┐
    ▼          ▼          ▼            ▼
  Web (TS)  iOS (Swift) Android (KT)  
  Section    Section     Section      
  Components Components  Components   
  (render    (render     (render      
   natively)  natively)   natively)   
```

### Ключевые компоненты

1. **Sections** — примитивные блоки UI, полностью независимые друг от друга
2. **SectionComponentType** — определяет КАК рендерить секцию (одна data model → разный визуал)
3. **Screens** — описывают layout и размещение секций
4. **ILayout** — интерфейс с разными реализациями (SingleColumn, TwoColumn, etc.)
5. **IAction** — действия при взаимодействии пользователя (навигация, scroll и т.д.)

### Пример Section Component (Android)

```kotlin
class TitleSectionComponent : SectionComponent<TitleSection>() {
    override fun buildUI(section: TitleSection): View {
        return titleView {
            text = section.title
            subtitle = section.subtitle
            onSubtitleClick = {
                GPActionHandler.handleIAction(section.onSubtitleClickAction)
            }
        }
    }
}
```

### Когда использовать SDUI

| Подходит ✅                             | Не подходит ❌                    |
| -------------------------------------- | -------------------------------- |
| Контентные экраны (листинги, каталоги) | Сложные интерактивные анимации   |
| A/B тестирование UI без релизов        | Оффлайн-first приложения         |
| Множество клиентов/брендов             | Real-time UI (чат, видео)        |
| Быстрые итерации UI                    | Приложения с минимальным backend |

---

## 6. Паттерн 4: Compose Multiplatform Shared UI

> **Источник:** JetBrains — Compose Multiplatform 1.7.0+ / 1.10.0 Release Notes

### Ключевая идея

Compose Multiplatform позволяет писать **один UI-код** через `@Composable` функции, который компилируется и рендерится нативно на Android, iOS, Desktop и Web (Wasm).

### Текущее состояние (март 2026)

| Платформа     | Статус           | Рендеринг                |
| ------------- | ---------------- | ------------------------ |
| Android       | Stable           | Нативный Jetpack Compose |
| iOS           | Stable (с 1.7.0) | Skia → UIKit canvas      |
| Desktop (JVM) | Stable           | Skia                     |
| Web (Wasm)    | Beta (с 1.9.0)   | Canvas / DOM             |

### Performance на iOS (1.7.0+ с Kotlin 2.0.20)

- **LazyGrid** scrolling — **~9% быстрее**, значительно меньше пропущенных кадров
- **VisualEffects** — **3.6x быстрее** (8.8с → 2.4с на 1000 кадров)
- **AnimatedVisibility** — **~6% быстрее**
- С Concurrent GC marking — **вдвое меньше** пропущенных кадров, p25 GC пауза 1.7ms → 0.4ms

### Стратегии использования

#### Стратегия A: Полностью общий UI (Compose for all)
```
commonMain/
  └── ui/
      ├── screens/
      ├── components/
      └── theme/
```
**Плюсы:** Максимальное переиспользование (~95% UI)  
**Минусы:** Не использует нативные UIKit/SwiftUI; ограничения для iOS-специфичных паттернов

#### Стратегия B: Shared UI + expect/actual для платформенных компонентов
```kotlin
// commonMain
@Composable
expect fun PlatformDatePicker(
    state: DatePickerState,
    onDateSelected: (LocalDate) -> Unit
)

// androidMain
@Composable
actual fun PlatformDatePicker(...) {
    // Material3 DatePicker
}

// iosMain
@Composable
actual fun PlatformDatePicker(...) {
    // UIKitView wrapping UIDatePicker
}
```
**Плюсы:** Нативные UX-паттерны где нужно  
**Минусы:** Дублирование для платформенных компонентов

#### Стратегия C: Config-Driven + Compose Multiplatform (гибрид)

Комбинация паттернов 1 и 4:
- **Config + ViewModel** в `commonMain` (KMP)
- **Compose UI** в `commonMain` для компонентов, одинаковых на всех платформах
- **expect/actual Composables** для платформенно-специфичных компонентов
- **Add React/Web target** через Kotlin/Wasm или отдельную кодовую базу, потребляющую те же Config объекты

---

## 7. Паттерн 5: React Native New Architecture + C++ Core

> **Источник:** React Native Blog — "New Architecture is here" (Oct 2024); Shopify Engineering — "Managing Native Code with React Native"

### Ключевые изменения

1. **JSI (JavaScript Interface)** — замена асинхронного Bridge на прямой C++ доступ
2. **Fabric Renderer** — новый рендерер на C++, общий для всех платформ
3. **Codegen** — строго типизированные контракты между JS и native
4. **Event Loop** — aligned с HTML spec, поддержка Concurrent React

### Shopify подход: KMP + React Native

Shopify использует React Native для UI, но тяжёлую логику (sync, background jobs) выносит в **Kotlin Multiplatform**:

```
React Native App
  ├── UI Layer (React/TypeScript) 
  ├── Native Modules (bridge to KMP)
  └── multiplatform/
      └── common/ (Kotlin)
          ├── Domain logic
          ├── Sync engine
          └── Data persistence
```

**Результат:** ~95% code sharing в common/, 170 LOC iOS + 178 LOC Android специфичного кода. Скорость sync: 30с → 2-3с.

### Как подключить React (Web) к shared Config

```
┌─────────────────────────────┐
│    Kotlin Multiplatform      │
│    (Config + ViewModel)      │
│    ↓ Export                   │
├──────────────┬───────────────┤
│ Kotlin/Wasm  │  JSON Schema  │
│ → React/Wasm │  → TypeScript │
│   bindings   │    codegen    │
└──────────────┴───────────────┘
```

**Вариант A: Kotlin/Wasm → React**
- Config/ViewModel компилируется в Wasm
- React потребляет через Wasm bindings
- Максимальное переиспользование, но Wasm ещё в Beta

**Вариант B: JSON Schema → TypeScript Codegen**
- Config описывается в KMP
- Генерируется JSON Schema из Kotlin data classes
- TypeScript типы генерируются из JSON Schema
- React получает типизированные Config объекты через API
- Более зрелый подход, легче debug

**Вариант C: Protocol Buffers / GraphQL Schema**
- Описание Config через .proto или .graphql
- Codegen для Kotlin, Swift, TypeScript
- Единый контракт для всех платформ

---

## 8. Сравнительная матрица подходов

| Критерий                    |  Config-Driven (KMP)  | Design Tokens |     SDUI      |  Compose MP Full  |       RN New Arch        |
| --------------------------- | :-------------------: | :-----------: | :-----------: | :---------------: | :----------------------: |
| **% переиспользования UI**  |     ~70% (логика)     | ~30% (визуал) | ~60% (schema) |      ~90–95%      |    ~85% с shared C++     |
| **Нативный перформанс**     |         ★★★★★         |     ★★★★★     |     ★★★★☆     |       ★★★★☆       |          ★★★☆☆           |
| **Поддержка React/Web**     | ★★★☆☆ (через codegen) |     ★★★★★     |     ★★★★★     | ★★★☆☆ (Wasm Beta) | ★★★★★ (React Native Web) |
| **iOS нативный UX**         |    ★★★★★ (SwiftUI)    |     ★★★★★     |     ★★★★★     |   ★★★☆☆ (Skia)    |          ★★★☆☆           |
| **Скорость разработки**     |         ★★★★☆         |     ★★★☆☆     |     ★★★★☆     |       ★★★★★       |          ★★★★☆           |
| **Сложность архитектуры**   |        Средняя        |    Низкая     |    Высокая    |      Низкая       |         Средняя          |
| **Multi-brand/White-label** |         ★★★★★         |     ★★★★★     |     ★★★★★     |       ★★★☆☆       |          ★★★☆☆           |
| **Зрелость экосистемы**     |         ★★★★☆         |     ★★★★★     |     ★★★★☆     |       ★★★★☆       |          ★★★★★           |

---

## 9. Рекомендуемая архитектура для Compose MP + React + iOS

### Многослойная гибридная архитектура

Оптимальный подход для задачи **Compose MP + React + iOS с максимальным перформансом** — комбинация Config-Driven Design + Design Tokens + платформенный рендеринг:

```
═══════════════════════════════════════════════════════
                    SHARED LAYER (KMP)
═══════════════════════════════════════════════════════

  ┌──────────────────────────────────────────────────┐
  │                 Domain Layer                      │
  │   Models • Interfaces • Use Cases                │
  │   (100% shared, zero platform code)              │
  └──────────────────────────────────────────────────┘
                         │
  ┌──────────────────────────────────────────────────┐
  │              Application Layer                    │
  │   ViewModels • State Machines • Event handling   │
  │   StateFlow<Config> → UI intent                  │
  │   (100% shared)                                  │
  └──────────────────────────────────────────────────┘
                         │
  ┌──────────────────────────────────────────────────┐
  │           Component Config Layer                  │
  │   @Immutable data class Config(...)              │
  │   Style resolution • Variant mapping             │
  │   Interaction callbacks                          │
  │   (100% shared)                                  │
  └──────────────────────────────────────────────────┘
                         │
  ┌──────────────────────────────────────────────────┐
  │             Design Tokens Layer                   │
  │   Colors • Typography • Spacing • Elevation      │
  │   JSON/YAML → per-platform codegen               │
  │   Multi-tier: Brand → Application → Component    │
  └──────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════
                 PLATFORM RENDERERS
═══════════════════════════════════════════════════════

  ┌────────────────┬─────────────────┬───────────────┐
  │    Android      │      iOS        │   React/Web   │
  │                │                 │               │
  │  Jetpack       │   SwiftUI       │  React        │
  │  Compose       │   (нативный)    │  Components   │
  │  (нативный)    │                 │  (нативные)   │
  │                │   Потребляет    │               │
  │  Прямое        │   Config через  │  Config через │
  │  использование │   SKIE +        │  Kotlin/Wasm  │
  │  Config из KMP │   Swift interop │  или JSON API │
  │                │                 │  + TS codegen │
  └────────────────┴─────────────────┴───────────────┘
```

### Практические рекомендации

#### 1. Config объекты (shared commonMain)

```kotlin
// Каждый компонент = Config + Style + Variants
object KCard {
    @Immutable
    data class Config(
        val title: KStringDesc,
        val subtitle: KStringDesc? = null,
        val imageUrl: String? = null,
        val action: (() -> Unit)? = null,
        val variant: Variant = Variant.Default,
    ) {
        fun style(tokens: DesignTokens): Style = Style(variant, tokens)
    }

    @Immutable
    data class Style(
        val variant: Variant,
        val tokens: DesignTokens,
    ) {
        val backgroundColor get() = tokens.surface.primary
        val cornerRadius get() = tokens.radius.md
        val elevation get() = when (variant) {
            Variant.Default -> tokens.elevation.sm
            Variant.Highlighted -> tokens.elevation.md
        }
    }
}
```

#### 2. Design Tokens (shared, generatable)

```kotlin
// commonMain — generated from JSON
@Immutable
data class DesignTokens(
    val color: ColorTokens,
    val spacing: SpacingTokens,
    val typography: TypographyTokens,
    val radius: RadiusTokens,
    val elevation: ElevationTokens,
)

// Разные бренды = разные инстансы DesignTokens
val brandA = DesignTokens(
    color = ColorTokens(primary = 0xFF0066FF, ...),
    ...
)
```

#### 3. Рендерер Android (Compose) — тонкий

```kotlin
@Composable
fun CardView(config: KCard.Config, tokens: DesignTokens = LocalTokens.current) {
    val style = config.style(tokens)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(style.backgroundColor)),
        shape = RoundedCornerShape(style.cornerRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = style.elevation.dp),
        onClick = { config.action?.invoke() }
    ) {
        // Render title, subtitle, image from config
    }
}
```

#### 4. Рендерер iOS (SwiftUI) — тонкий

```swift
struct CardView: View {
    let config: KCard.Config
    let tokens: DesignTokens
    
    private var style: KCard.Style { config.style(tokens: tokens) }
    
    var body: some View {
        VStack(alignment: .leading) {
            // Render title, subtitle, image from config
        }
        .background(Color(hex: style.backgroundColor))
        .cornerRadius(CGFloat(style.cornerRadius))
        .shadow(radius: CGFloat(style.elevation))
        .onTapGesture { config.action?() }
    }
}
```

#### 5. Рендерер React (Web) — через TypeScript types

```typescript
// Auto-generated from Kotlin Config via JSON Schema or Kotlin/Wasm
interface KCardConfig {
  title: string;
  subtitle?: string;
  imageUrl?: string;
  variant: 'default' | 'highlighted';
}

interface KCardStyle {
  backgroundColor: string;
  cornerRadius: number;
  elevation: number;
}

// React component — тонкий рендерер
const CardView: React.FC<{ config: KCardConfig; tokens: DesignTokens }> = ({ config, tokens }) => {
  const style = resolveCardStyle(config.variant, tokens);
  
  return (
    <div
      style={{
        backgroundColor: style.backgroundColor,
        borderRadius: style.cornerRadius,
        boxShadow: `0 ${style.elevation}px ${style.elevation * 2}px rgba(0,0,0,0.1)`,
      }}
    >
      <h3>{config.title}</h3>
      {config.subtitle && <p>{config.subtitle}</p>}
      {config.imageUrl && <img src={config.imageUrl} alt={config.title} />}
    </div>
  );
};
```

### Pipeline генерации

```
┌─────────────────┐     ┌──────────────────────┐
│  Figma           │────▶│  Design Tokens JSON   │
│  (дизайнеры)     │     │  tokens.json          │
└─────────────────┘     └──────────┬───────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              ▼                    ▼                    ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │ Kotlin           │ │ Swift            │ │ TypeScript       │
    │ DesignTokens.kt  │ │ DesignTokens.    │ │ designTokens.ts  │
    │ (generated)      │ │ swift (generated)│ │ (generated)      │
    └─────────────────┘ └─────────────────┘ └─────────────────┘

┌─────────────────┐     ┌──────────────────────┐
│  Kotlin Config   │────▶│  JSON Schema          │
│  data classes    │     │  (auto-generated)     │
└─────────────────┘     └──────────┬───────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              ▼                    ▼                    ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │ Kotlin (direct)  │ │ Swift (SKIE)     │ │ TypeScript       │
    │                  │ │ sealed→enum      │ │ (codegen from    │
    │                  │ │ Flow→AsyncSeq    │ │  JSON Schema)    │
    └─────────────────┘ └─────────────────┘ └─────────────────┘
```

### Итоговые метрики (ожидания)

| Метрика                       | Значение                                  |
| ----------------------------- | ----------------------------------------- |
| Код domain/app/infra          | 100% shared (KMP)                         |
| Код ViewModel + Config        | 100% shared (KMP)                         |
| Код Design Tokens             | 100% shared (JSON → codegen)              |
| Код UI рендереров             | 0% shared (нативный для каждой платформы) |
| **Общий % переиспользования** | **~70-75%**                               |
| **Перформанс**                | **Нативный на каждой платформе**          |
| Стоимость добавления бренда   | ~2 дня (JSON config + tokens)             |

### Когда этот подход выбрать

✅ **Выбирайте, если:**
- Нужен 100% нативный UX на каждой платформе
- Есть команды специалистов по Android, iOS и Web
- Важен White-labeling / multi-brand
- Высокие требования к перфомансу (banking, fintech, trading)

⚠️ **Рассмотрите Compose MP full shared UI, если:**
- Маленькая команда (1-3 разработчика)
- UI одинаков на всех платформах (нет платформенной специфики)
- Скорость разработки важнее нативного UX
- Web не является приоритетной платформой

---

## 10. Источники

1. **Anmol Verma** — "Beyond Shared Logic: Building a Whitelabel App with Kotlin Multiplatform"  
   ProAndroidDev, January 2026  
   https://proandroiddev.com/beyond-shared-logic-building-a-whitelabel-app-with-kotlin-multiplatform-d220a0b196b2

2. **Brad Frost** — "Creating Themeable Design Systems"  
   bradfrost.com, April 2018  
   https://bradfrost.com/blog/post/creating-themeable-design-systems/

3. **Ryan Brooks (Airbnb)** — "A Deep Dive into Airbnb's Server-Driven UI System"  
   Airbnb Tech Blog, June 2021  
   https://medium.com/airbnb-engineering/a-deep-dive-into-airbnbs-server-driven-ui-system-842244c5f5

4. **React Native Team (Meta)** — "New Architecture is here"  
   reactnative.dev, October 2024  
   https://reactnative.dev/blog/2024/10/23/the-new-architecture-is-here

5. **Colin Gray (Shopify)** — "Management of Native Code and React Native at Shopify"  
   Shopify Engineering, April 2021  
   https://shopify.engineering/managing-native-code-react-native

6. **JetBrains** — "Compose Multiplatform 1.7.0 Released"  
   JetBrains Blog, October 2024  
   https://blog.jetbrains.com/kotlin/2024/10/compose-multiplatform-1-7-0-released/

7. **JetBrains** — "Compose Multiplatform 1.10.0: Unified @Preview, Navigation 3, and Stable Compose Hot Reload"  
   JetBrains Blog, January 2026  
   https://blog.jetbrains.com/kotlin/2026/01/compose-multiplatform-1-10-0/

8. **Brad Frost** — "Atomic Design" (книга)  
   https://atomicdesign.bradfrost.com/

9. **Salesforce** — Lightning Design System: Design Tokens  
   https://www.lightningdesignsystem.com/design-tokens/

10. **Touchlab** — SKIE (Swift-Kotlin Interop Enhancer)  
    https://skie.touchlab.co/
