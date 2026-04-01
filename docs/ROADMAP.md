# UIKit Design System — Стратегический Roadmap

> От идеально вылизанного UIKit → через BDUI → к Generative UI  
> Кросс-платформа: Compose + React из единой Kotlin Multiplatform логики  
> Горизонт: 2026–2028+

---

## Содержание

1. [Анализ текущего состояния](#1-анализ-текущего-состояния)
2. [Индустриальные тренды и предсказания](#2-индустриальные-тренды-и-предсказания)
3. [Целевая архитектура](#3-целевая-архитектура)
4. [Иерархия компонентов](#4-иерархия-компонентов-новая-таксономия)
5. [DTCG Token Architecture](#5-dtcg-token-architecture)
6. [Адаптивная система дизайна (Dynamic Design)](#6-адаптивная-система-дизайна-dynamic-design)
7. [BDUI Engine](#7-bdui-engine--server-driven-ui)
8. [Generative UI](#8-generative-ui)
9. [Auto-расчёт размеров и адаптивный layout](#9-auto-расчёт-размеров-и-адаптивный-layout)
10. [SSR и кросс-платформа](#10-ssr-и-кросс-платформенная-стратегия)
11. [Производительность и качество](#11-производительность-и-качество)
12. [Поэтапный план реализации](#12-поэтапный-план-реализации)

---

## 1. Анализ текущего состояния

### Что уже есть (апрель 2026)

```
core/uikit/
├── common/          ← KMP (jvm + js), Config + StyleResolver, 10 типов токенов
│   ├── tokens/      ← ColorTokens, SpacingTokens, TypographyTokens, SizingTokens,
│   │                   RadiusTokens, BreakpointTokens, MotionTokens,
│   │                   InteractiveControlTokens, InteractiveStateTokens, ShadowTokens
│   ├── foundation/  ← VisualVariant, ColorIntent, ComponentSize, ThemeMode, 
│   │                   SurfaceContext, Visibility, ThemeProvider
│   └── components/atoms/
│       ├── button/            ← ButtonConfig + ButtonStyleResolver
│       ├── text/              ← TextBlockConfig + TextBlockStyleResolver
│       ├── surface/           ← SurfaceConfig + SurfaceStyleResolver
│       └── segmentedcontrol/  ← SegmentedControlConfig + SegmentedControlStyleResolver
├── compose/         ← Compose Desktop рендереры (~30 LOC каждый)
└── react/           ← React 19 рендереры (~30 LOC каждый)
```

### Сильные стороны ✅

| Аспект                       | Детали                                                        |
| ---------------------------- | ------------------------------------------------------------- |
| **KMP Shared Logic**         | Config + StyleResolver в common → тонкие рендереры            |
| **Config-Driven UI**         | `@Serializable` data classes, готовность к SDUI/BDUI          |
| **Семантическое именование** | `ColorIntent.Primary`, `VisualVariant.Solid` (Radix-inspired) |
| **3-уровневые токены**       | Primitive → Semantic → Component (логика есть)                |
| **Runtime Theme Switching**  | Полная смена токенов без перекомпиляции                       |
| **SSR Safety**               | Pure functions, stateless resolvers, immutable data           |
| **Surface-Aware Context**    | Компоненты адаптируются к фону родительского Surface          |
| **Illustrated Style**        | Wireframe preset — доказательство multi-style flexibility     |

### Точки роста ❌

| Аспект                     | Проблема                                                             |
| -------------------------- | -------------------------------------------------------------------- |
| **4 из 25+ компонентов**   | Только Button, Text, Surface, SegmentedControl                       |
| **`atoms/` naming**        | Реликт Atomic Design, не соответствует трендам 2026                  |
| **Нет DTCG JSON**          | Токены только в Kotlin, нет интеропа с Figma/Style Dictionary        |
| **Нет Patterns layer**     | Нет best-practice композиций (FormPattern, CardList, etc.)           |
| **Нет BDUI Engine**        | SDUI описан в architecture doc, но не реализован                     |
| **Нет Expressions**        | Нет runtime expressions для динамических значений                    |
| **Accessibility**          | Базовые aria-атрибуты, нет contrast-чеков, нет a11y labels в configs |
| **Нет Auto Layout Engine** | Нет auto-расчёта размеров на основе content + constraints            |

---

## 2. Индустриальные тренды и предсказания

### 2026: Текущие тренды

| Тренд                              | Описание                                         | Наш статус         |
| ---------------------------------- | ------------------------------------------------ | ------------------ |
| **Tokens → Components → Patterns** | 3-уровневая организация вместо Atomic Design     | Частично           |
| **DTCG Standard**                  | Machine-readable JSON токены с `$value`, `$type` | ❌                  |
| **Multi-Brand Orchestration**      | Primitive → Semantic override per brand          | Фундамент          |
| **AI-Enforced Consistency**        | AI-агенты проверяют соответствие guidelines      | ❌                  |
| **Agentic Design-to-Code**         | AI оркестрирует Figma → Code → Deploy pipeline   | ❌                  |
| **Material 3 Expressive**          | Более индивидуальные, выразительные дизайны      | Illustrated preset |
| **Apple Liquid Glass**             | Depth, blur, glassmorphism как стандарт          | ❌                  |

### 2027: Предсказания на ближайшее будущее

| Тренд                            | Описание                                                                              |
| -------------------------------- | ------------------------------------------------------------------------------------- |
| **BDUI Everywhere**              | Server-Driven UI становится стандартом для enterprise (DivKit, Airbnb Ghost Platform) |
| **Design System as API**         | Дизайн-система как RESTful/GraphQL API для AI и инструментов                          |
| **Intent-Adaptive UI**           | UI адаптируется под задачу пользователя, не только под размер экрана                  |
| **Zero-Latency Personalization** | Edge-cached персонализированные layout-конфиги                                        |
| **Component-Level Analytics**    | Каждый компонент трекает engagement metrics автоматически                             |

### 2028+: Generative UI эра

| Тренд                               | Описание                                                                  |
| ----------------------------------- | ------------------------------------------------------------------------- |
| **Generative UI**                   | AI строит экраны из компонентов в реальном времени по intent пользователя |
| **Natural Language → Screen**       | "Покажи мне дашборд продаж за Q3" → готовый экран                         |
| **Context-Aware Layouts**           | Layout адаптируется к данным, устройству, задаче, привычкам               |
| **Design System as Language Model** | Дизайн-система как "словарь" для AI сборки UI                             |
| **Self-Healing UI**                 | Автокоррекция layout-проблем, accessibility violations                    |

### Ключевой вывод

> **Generative UI невозможен без идеально вылизанного UIKit.** AI может "собирать" экраны только из надёжных, предсказуемых, self-describing компонентов. Чем лучше компонентная база — тем качественнее генерация.

**Путь**: Идеальный UIKit → BDUI (серверная сборка) → Generative UI (AI-сборка)

---

## 3. Целевая архитектура

### Слоёный пирог: от токенов до генеративного UI

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     GENERATIVE UI LAYER (2028+)                         │
│  AI Agent получает intent → выбирает Pattern → наполняет Components     │
│  → применяет Brand Tokens → рендерит на целевой платформе              │
├─────────────────────────────────────────────────────────────────────────┤
│                     BDUI / SDUI ENGINE (2027)                           │
│  Server → JSON ScreenConfig → Client кэш → Renderer                   │
│  Expressions: "{data.count > 0 ? 'visible' : 'gone'}"                 │
│  Actions: Navigate, ApiCall, UpdateState, ShowFeedback, Custom         │
│  States, Timers, Patches, Variables, Triggers                          │
├─────────────────────────────────────────────────────────────────────────┤
│                     PATTERNS (best-practice compositions)               │
│  FormPattern, CardListPattern, DashboardPattern, OnboardingPattern     │
│  NavigationPattern, SearchResultsPattern, SettingsPattern              │
│  Каждый Pattern = Layout + набор Components + UX-правила              │
├─────────────────────────────────────────────────────────────────────────┤
│                     COMPONENTS (UI строительные блоки)                  │
│  ┌──────────────┐  ┌────────────────┐  ┌─────────────────────────┐    │
│  │  Primitives   │  │   Composites   │  │       Blocks            │    │
│  │  (неделимые)  │  │  (из primitives)│  │  (самодостаточные)     │    │
│  │               │  │                │  │                         │    │
│  │  Icon         │  │  Button        │  │  Card                   │    │
│  │  Text         │  │  TextField     │  │  ListSection            │    │
│  │  Divider      │  │  Select        │  │  Accordion              │    │
│  │  Spacer       │  │  Checkbox      │  │  Dialog                 │    │
│  │  Badge        │  │  Radio         │  │  BottomSheet            │    │
│  │  Indicator    │  │  Toggle        │  │  Tabs + TabContent      │    │
│  │  Avatar       │  │  Chip          │  │  NavigationBar          │    │
│  │  Skeleton     │  │  Slider        │  │  DataTable              │    │
│  │  Surface      │  │  Snackbar      │  │  Calendar               │    │
│  │               │  │  Tag           │  │  FileUpload             │    │
│  │               │  │  SegmentedCtrl │  │  RichTextEditor         │    │
│  │               │  │  Tooltip       │  │                         │    │
│  │               │  │  ProgressBar   │  │                         │    │
│  └──────────────┘  └────────────────┘  └─────────────────────────┘    │
├─────────────────────────────────────────────────────────────────────────┤
│                     DESIGN TOKENS (DTCG Standard)                      │
│  ┌─────────────┐  ┌──────────────────┐  ┌────────────────────────┐    │
│  │  Primitives  │  │    Semantics     │  │  Component Tokens      │    │
│  │  (raw values)│  │  (intent-based)  │  │  (per-component)       │    │
│  │              │  │                  │  │                        │    │
│  │  blue.500    │  │  color.action    │  │  button.primary.bg     │    │
│  │  space.4     │  │  color.surface   │  │  card.radius           │    │
│  │  radius.md   │  │  color.danger    │  │  textfield.border      │    │
│  │  font.16     │  │  text.primary    │  │  dialog.overlay        │    │
│  └─────────────┘  └──────────────────┘  └────────────────────────┘    │
│                                                                         │
│  Brand Overrides: brand-a.json ← deep merge → default.json            │
├─────────────────────────────────────────────────────────────────────────┤
│                     FOUNDATION                                          │
│  ThemeMode, LayoutDirection, Density, Accessibility, MotionPreference  │
│  VisualVariant, ColorIntent, ComponentSize, SurfaceContext, Visibility │
└─────────────────────────────────────────────────────────────────────────┘
         │                                    │
         ▼                                    ▼
┌──────────────────┐              ┌──────────────────┐
│  Compose Renderer │              │  React Renderer   │
│  (Android/Desktop)│              │  (Web/SSR)        │
│  ~30 LOC/component│              │  ~30 LOC/component│
└──────────────────┘              └──────────────────┘
```

---

## 4. Иерархия компонентов (новая таксономия)

### Почему не atoms/molecules/organisms

В 2026 году Atomic Design naming устарел:
- **Неоднозначность**: Button — это atom или molecule? Споры вместо работы
- **Кнопка ≠ "неделимый атом"**: Button = Icon + Text + Surface + States
- **Химическая метафора** не масштабируется на 50+ компонентов

### Новая 3-уровневая иерархия

Вместо atoms/molecules/organisms используем **семантические уровни**:

```
components/
├── primitives/      ← Неделимые визуальные элементы (настоящие "атомы")
├── composites/      ← Составные: комбинации primitives + логика взаимодействия
├── blocks/          ← Самодостаточные блоки: сложная логика, layout, state
└── patterns/        ← Best-practice шаблоны сборки (не компоненты, а рецепты)
```

### Уровень 1: Primitives (неделимые)

Примитив — то, что **нельзя разложить дальше** без потери смысла.

| #   | Primitive     | Описание                      | Варианты                             |
| --- | ------------- | ----------------------------- | ------------------------------------ |
| 1   | **Icon**      | Векторная иконка              | Размеры: Xs/Sm/Md/Lg/Xl              |
| 2   | **Text**      | Текстовый блок с типографикой | H1-H3, Body, Caption, Overline       |
| 3   | **Divider**   | Разделитель                   | Horizontal, Vertical                 |
| 4   | **Spacer**    | Управляемый отступ            | Fixed, Flexible                      |
| 5   | **Surface**   | Контейнер-плоскость           | Solid, Soft, Outline, Ghost × Level  |
| 6   | **Indicator** | Точка/число состояния         | Dot, Numeric                         |
| 7   | **Skeleton**  | Placeholder загрузки          | Text, Circle, Rectangle              |
| 8   | **Image**     | Отображение изображения       | Static, Async, Placeholder, Fallback |

### Уровень 2: Composites (составные)

Composite = комбинация primitives + интерактивная логика.

| #   | Composite            | Состав                                | Варианты                                     |
| --- | -------------------- | ------------------------------------- | -------------------------------------------- |
| 1   | **Button**           | Surface + Text + Icon + States        | Solid/Soft/Outline/Ghost × Intent × Size     |
| 2   | **IconButton**       | Surface + Icon + States               | Solid/Soft/Outline/Ghost × Size              |
| 3   | **TextField**        | Surface + Text + Icon + Label + Error | Default, Search, Password, Multiline         |
| 4   | **Select**           | TextField + Dropdown + Options        | Single, Multi, Searchable                    |
| 5   | **Checkbox**         | Surface + Icon + Label                | Default, Indeterminate                       |
| 6   | **Radio**            | Surface + Indicator + Label           | Default, Card                                |
| 7   | **Toggle**           | Surface + Indicator + Label           | Default, WithLabel                           |
| 8   | **Slider**           | Track + Thumb + Label                 | Single, Range                                |
| 9   | **Chip**             | Surface + Text + Icon + Close         | Selectable, Dismissible, Info                |
| 10  | **Tag**              | Surface + Text + Icon                 | Status, Category, Colored                    |
| 11  | **Badge**            | Surface + Indicator + Text            | Dot, Numeric                                 |
| 12  | **Avatar**           | Surface + Image/Initials/Icon         | Image, Initials, Icon × Size                 |
| 13  | **SegmentedControl** | Surface + Buttons + Thumb             | Default, WithIcons                           |
| 14  | **ProgressBar**      | Track + Fill + Label                  | Linear, Circular, Determinate, Indeterminate |
| 15  | **Snackbar**         | Surface + Text + Action               | Info, Success, Warning, Error                |
| 16  | **Tooltip**          | Surface + Text + Arrow                | Top, Bottom, Left, Right                     |
| 17  | **DatePicker**       | TextField + Calendar popup            | Date, DateTime, DateRange                    |

### Уровень 3: Blocks (самодостаточные блоки)

Block = сложный компонент с собственным layout, state management, дочерними elements.

| #   | Block              | Описание                       | Варианты                                   |
| --- | ------------------ | ------------------------------ | ------------------------------------------ |
| 1   | **Card**           | Контейнер с header/body/footer | Default, Elevated, Outlined, Interactive   |
| 2   | **ListItem**       | Строка списка с action areas   | Default, WithIcon, WithAction, WithSwipe   |
| 3   | **Accordion**      | Expandable секция              | Single, Multi, Nested                      |
| 4   | **Tabs**           | Табуляция с контентом          | Default, Scrollable, Pill, Vertical        |
| 5   | **BottomSheet**    | Overlay снизу                  | Fixed, Expandable, Persistent              |
| 6   | **Dialog**         | Модальное окно                 | Alert, Confirm, Custom, FullScreen         |
| 7   | **Banner**         | Информационная полоса          | Info, Success, Warning, Error, Dismissible |
| 8   | **NavigationBar**  | Верхняя панель навигации       | Default, Large, Collapsing                 |
| 9   | **DataTable**      | Таблица данных                 | Sortable, Selectable, Paginated            |
| 10  | **Calendar**       | Календарь                      | Month, Week, Range picker                  |
| 11  | **FileUpload**     | Загрузка файлов                | Drag&Drop, Button, Multi                   |
| 12  | **RichTextEditor** | Форматированный текст          | Basic, Full, Markdown                      |

### Уровень 4: Patterns (шаблоны сборки)

Pattern ≠ компонент. Pattern = **рецепт**: какие Components в каком Layout с какими UX-правилами.

| Pattern                  | Описание                     | Components используемые                            |
| ------------------------ | ---------------------------- | -------------------------------------------------- |
| **FormPattern**          | Best-practice форма          | TextField, Select, Checkbox, Radio, Button, Banner |
| **CardListPattern**      | Вертикальный список карточек | Card, ListItem, Skeleton, ProgressBar              |
| **SearchResultsPattern** | Поиск с фильтрами            | TextField, Chip, Card, Tabs, Skeleton              |
| **DashboardPattern**     | Информационная панель        | Card, DataTable, ProgressBar, Tabs, Badge          |
| **OnboardingPattern**    | Пошаговый онбординг          | Surface, Text, Image, Button, ProgressBar          |
| **SettingsPattern**      | Экран настроек               | ListItem, Toggle, Select, Dialog                   |
| **NavigationPattern**    | Навигационная структура      | NavigationBar, Tabs, BottomSheet                   |
| **EmptyStatePattern**    | Пустое состояние             | Image, Text, Button                                |
| **ErrorPattern**         | Обработка ошибок             | Banner, Button, Text                               |
| **AuthPattern**          | Авторизация                  | TextField, Button, Divider, Text                   |

### Маппинг из текущей структуры

```
ТЕКУЩЕЕ:                           → НОВОЕ:
components/atoms/button/           → components/composites/button/
components/atoms/text/             → components/primitives/text/
components/atoms/surface/          → components/primitives/surface/
components/atoms/segmentedcontrol/ → components/composites/segmented-control/
```

---

## 5. DTCG Token Architecture

### Текущая проблема

Токены определены **только в Kotlin** — нет machine-readable формата для:
- Figma Token Studio
- Style Dictionary
- AI-инструментов
- Кросс-тулчейн (Storybook, DevTools)

### Целевая архитектура: Source of Truth → Generated Code

```
tokens/                          ← DTCG JSON — Source of Truth
├── foundation.tokens.json       ← Primitives: palette, spacing scale, font scale
├── semantic.tokens.json         ← Semantics: color.action.primary, text.heading
├── component/
│   ├── button.tokens.json       ← Component tokens: button.primary.bg
│   ├── card.tokens.json
│   └── ...
└── brands/
    ├── default.json             ← Default brand (light + dark)
    ├── brand-alpha.json         ← Override for Brand Alpha
    ├── brand-fantasy.json       ← Fantasy/wireframe style
    └── brand-apple.json         ← Apple-like clean style

codegen/                          ← Token generator
├── generate-kotlin.ts           ← DTCG JSON → Kotlin data classes
├── generate-css.ts              ← DTCG JSON → CSS custom properties
└── generate-figma.ts            ← DTCG JSON → Figma Token Studio format

GENERATED:
core/uikit/common/.../tokens/    ← Auto-generated Kotlin (DO NOT EDIT)
core/uikit/react/styles/tokens/  ← Auto-generated CSS variables
```

### DTCG JSON формат (пример)

```json
{
  "color": {
    "palette": {
      "neutral": {
        "0": { "$value": "#FFFFFF", "$type": "color" },
        "50": { "$value": "#F9FAFB", "$type": "color" },
        "900": { "$value": "#111827", "$type": "color" },
        "1000": { "$value": "#000000", "$type": "color" }
      },
      "blue": {
        "500": { "$value": "#3B82F6", "$type": "color" },
        "600": { "$value": "#2563EB", "$type": "color" }
      }
    },
    "action": {
      "primary": {
        "$value": "{color.palette.neutral.900}",
        "$type": "color",
        "$description": "Primary action background"
      },
      "primary-hover": {
        "$value": "{color.palette.neutral.800}",
        "$type": "color"
      },
      "danger": {
        "$value": "{color.palette.red.500}",
        "$type": "color"
      }
    },
    "surface": {
      "default": { "$value": "{color.palette.neutral.0}", "$type": "color" },
      "container": { "$value": "{color.palette.neutral.50}", "$type": "color" }
    },
    "text": {
      "primary": { "$value": "{color.palette.neutral.900}", "$type": "color" },
      "secondary": { "$value": "{color.palette.neutral.500}", "$type": "color" },
      "on-action": { "$value": "{color.palette.neutral.0}", "$type": "color" }
    }
  },
  "space": {
    "1": { "$value": "2px", "$type": "dimension" },
    "2": { "$value": "4px", "$type": "dimension" },
    "3": { "$value": "8px", "$type": "dimension" },
    "4": { "$value": "12px", "$type": "dimension" },
    "5": { "$value": "16px", "$type": "dimension" },
    "6": { "$value": "24px", "$type": "dimension" },
    "7": { "$value": "32px", "$type": "dimension" },
    "8": { "$value": "48px", "$type": "dimension" },
    "9": { "$value": "64px", "$type": "dimension" }
  },
  "typography": {
    "heading": {
      "lg": {
        "$value": {
          "fontSize": "34px",
          "fontWeight": 700,
          "lineHeight": "41px",
          "letterSpacing": "0.37px"
        },
        "$type": "typography"
      }
    }
  }
}
```

### Brand override (deep merge)

```json
// brands/brand-fantasy.json — Illustrated/wireframe стиль
{
  "color": {
    "action": {
      "primary": { "$value": "{color.palette.neutral.1000}" }
    }
  },
  "borderWidth": {
    "default": { "$value": "2px" }
  },
  "shadow": {
    "sm": { "$value": "3px 3px 0px {color.palette.neutral.1000}" }
  },
  "fontWeight": {
    "offset": { "$value": 100, "$type": "number", "$description": "Bold-er for comic style" }
  }
}

// brands/brand-apple.json — Чистый Apple-like стиль
{
  "color": {
    "surface": {
      "default": { "$value": "rgba(255,255,255,0.72)" }
    }
  },
  "radius": {
    "md": { "$value": "14px" }
  },
  "shadow": {
    "sm": { "$value": "0 2px 10px rgba(0,0,0,0.08)" }
  },
  "blur": {
    "surface": { "$value": "20px", "$type": "dimension", "$description": "Glassmorphism" }
  }
}
```

---

## 6. Адаптивная система дизайна (Dynamic Design)

### Проблема

Хочется чтобы дизайн переключался динамически: от fantasy/wireframe до Apple-like clean, и чтобы размеры, отступы, радиусы — всё автоматически пересчитывалось.

### Решение: Design Algorithms + Scale Factors

```kotlin
/**
 * DesignAlgorithm — чистая функция, которая трансформирует
 * базовые токены в конкретный визуальный стиль.
 * 
 * Один набор компонентов. Разные алгоритмы = разный look & feel.
 */
interface DesignAlgorithm {
    val id: String
    
    /** Трансформация цветовой палитры */
    fun resolveColors(palette: PrimitiveColors, mode: ThemeMode): SemanticColors
    
    /** Трансформация spacing с учётом density */
    fun resolveSpacing(base: SpacingScale, density: Density): SpacingTokens
    
    /** Трансформация radius (sharp vs rounded vs pill) */
    fun resolveRadius(base: RadiusScale, style: ShapeStyle): RadiusTokens
    
    /** Трансформация shadow (flat vs elevated vs offset) */
    fun resolveShadow(base: ShadowScale, style: ElevationStyle): ShadowTokens
    
    /** Трансформация typography с учётом scale */
    fun resolveTypography(base: TypeScale, scale: TypeScaleFactor): TypographyTokens
    
    /** Трансформация motion (reduced vs standard vs expressive) */
    fun resolveMotion(base: MotionScale, preference: MotionPreference): MotionTokens
    
    /** Трансформация border (none vs thin vs wireframe) */
    fun resolveBorder(style: BorderStyle): BorderTokens
}
```

### Встроенные алгоритмы

```kotlin
// 1. Clean — Apple-like минимализм
object CleanAlgorithm : DesignAlgorithm {
    // Тонкие тени, pill-радиусы, glassmorphism blur
    // borderWidth = 0, shadow = soft diffuse
    // radius = generously rounded
}

// 2. Illustrated — Fantasy/Wireframe/Comic
object IllustratedAlgorithm : DesignAlgorithm {
    // Толстые бордеры (2-3px), offset shadow, bold fonts
    // borderWidth = 2, shadow = offset solid
    // radius = slightly rounded
}

// 3. Corporate — Enterprise строгий
object CorporateAlgorithm : DesignAlgorithm {
    // Минимальные тени, sharp radii, нейтральные цвета
    // borderWidth = 1, shadow = subtle
    // radius = small
}

// 4. Expressive — Material 3 Expressive
object ExpressiveAlgorithm : DesignAlgorithm {
    // Яркие градиенты, крупные radii, bouncy анимации
    // borderWidth = 0, shadow = colored
    // radius = extra rounded, pill shapes
}
```

### Auto-расчёт через Scale Factors

```kotlin
/**
 * Density и Scale управляют автоматическим расчётом всех размеров.
 * Одна переменная масштабирует ВСЁ: кнопки, отступы, шрифты, иконки.
 */
data class DesignScale(
    val density: Density,           // Comfortable(1.0), Cozy(0.9), Compact(0.8)
    val typeScale: TypeScaleFactor, // 1.0 = default, 1.2 = large text, 0.9 = dense
    val iconScale: Double = 1.0,    // масштаб иконок
    val spacingScale: Double = 1.0, // масштаб отступов
    val radiusScale: Double = 1.0,  // масштаб радиусов
)

// Применение:
// controlHeight = baseHeight * density.factor
// padding       = baseSpacing * density.factor * spacingScale
// fontSize      = baseFontSize * typeScale
// iconSize      = baseIconSize * density.factor * iconScale
// radius        = baseRadius * radiusScale
```

### Динамическое переключение в runtime

```kotlin
// Compose
UIKitTheme(
    tokens = DesignTokens.Default,
    algorithm = IllustratedAlgorithm,  // ← переключаем стиль
    scale = DesignScale(density = Density.Comfortable),
) {
    App()
}

// React
<UIKitThemeProvider
    tokens={defaultTokens}
    algorithm="illustrated"           // ← переключаем стиль
    scale={{ density: 'comfortable' }}
>
    <App />
</UIKitThemeProvider>
```

---

## 7. BDUI Engine — Server-Driven UI

### Зачем BDUI

| Сценарий                | Без BDUI                 | С BDUI                      |
| ----------------------- | ------------------------ | --------------------------- |
| A/B тест нового дизайна | Релиз + Feature flag     | Смена JSON на сервере       |
| Починка бага в layout   | Релиз в Store (1-7 дней) | Patch за минуты             |
| Разный UI для сегментов | Код для каждого сегмента | JSON per segment            |
| Новый промо-экран       | Разработка + релиз       | JSON конфиг + данные        |
| Персонализация          | Custom logic в коде      | Dynamic template resolution |

### Архитектура (вдохновлённая DivKit + Airbnb Ghost Platform)

```
Server (BFF / BDUI Service)
    │
    │  JSON ScreenConfig
    │  (LayoutConfig + SectionConfig[] + ComponentConfig[])
    │  + Expressions + Variables + Actions
    │
    ▼
┌─────────────────────────────────────────────────┐
│  BDUI Client (KMP common)                        │
│                                                   │
│  ┌───────────────────────────────────────────┐   │
│  │  Config Parser                             │   │
│  │  JSON → ScreenConfig (type-safe)           │   │
│  │  Schema validation + fallback              │   │
│  └────────────────┬──────────────────────────┘   │
│                   │                               │
│  ┌────────────────▼──────────────────────────┐   │
│  │  Expression Engine                         │   │
│  │  "@{data.count > 0 ? 'visible' : 'gone'}" │   │
│  │  Mini-interpreter для runtime expressions  │   │
│  │  Поддержка: math, string, boolean, ternary │   │
│  └────────────────┬──────────────────────────┘   │
│                   │                               │
│  ┌────────────────▼──────────────────────────┐   │
│  │  State Manager                             │   │
│  │  Variables + Triggers + Timers             │   │
│  │  Reactive: variable change → re-resolve    │   │
│  └────────────────┬──────────────────────────┘   │
│                   │                               │
│  ┌────────────────▼──────────────────────────┐   │
│  │  Action Router                             │   │
│  │  Navigate, ApiCall, UpdateState,           │   │
│  │  ShowFeedback, Sequence, Parallel, Custom  │   │
│  └────────────────┬──────────────────────────┘   │
│                   │                               │
│  ┌────────────────▼──────────────────────────┐   │
│  │  Cache Layer                               │   │
│  │  Strategy: Cache-First, Network-Refresh    │   │
│  │  LRU memory + disk cache + TTL             │   │
│  │  Offline fallback → embedded configs       │   │
│  └───────────────────────────────────────────┘   │
│                                                   │
└──────────────────────┬──────────────────────────┘
                       │ ComponentConfig
                       ▼
               Platform Renderers
          (Compose / React — те же самые)
```

### Expression Engine

Ключевая фича BDUI — runtime expressions. Без них сервер не может управлять визуальной логикой.

```json
{
  "type": "text",
  "config": {
    "text": "@{variables.userName}",
    "visibility": "@{variables.isLoggedIn ? 'visible' : 'gone'}",
    "variant": "@{data.items.length > 0 ? 'body' : 'caption'}"
  }
}
```

```kotlin
/**
 * Expression Engine — интерпретатор мини-языка.
 * Pure function: (expression: String, context: ExpressionContext) → Any
 * 
 * Поддерживаемые операции:
 * - Арифметика: +, -, *, /, %
 * - Сравнение: ==, !=, <, >, <=, >=
 * - Логика: &&, ||, !
 * - Тернарный: condition ? a : b
 * - Доступ к свойствам: data.user.name
 * - Функции: len(), toUpper(), format(), min(), max()
 * - String interpolation: "Hello, #{name}!"
 */
object ExpressionEngine {
    fun evaluate(expr: String, context: ExpressionContext): Any
}
```

### Режимы работы

```
┌────────────────┬──────────────────────────────────┬────────────────────────┐
│  Режим         │ Описание                         │ Latency                │
├────────────────┼──────────────────────────────────┼────────────────────────┤
│  Static        │ Config зашит в код               │ 0ms (compile-time)     │
│  Cached        │ Config из disk cache, bg refresh │ <5ms (disk read)       │
│  Hybrid        │ Skeleton → Cache → Network       │ Skeleton: 0ms, Full: N │
│  Full BDUI     │ Config с сервера каждый раз      │ Network RTT            │
│  Generative    │ AI Agent строит Config           │ AI inference time      │
└────────────────┴──────────────────────────────────┴────────────────────────┘
```

**Ключевой принцип**: Component рендереры **одинаковые** во всех режимах. Меняется только **источник** Config.

---

## 8. Generative UI

### Предпосылки

Generative UI работает только когда:
1. ✅ Все компоненты — self-describing (Config = полное описание)
2. ✅ Все компоненты — serializable (JSON ↔ Kotlin/TS)
3. ✅ Есть machine-readable каталог компонентов (DTCG tokens + Component Registry)
4. ✅ Есть Patterns — AI знает best-practice для типовых задач
5. ✅ Есть Expression Engine — AI может добавлять условную логику
6. ✅ Есть BDUI — генерация и доставка без релиза

### Как работает

```
User Intent (NL / Structured)
    │
    │  "Покажи дашборд продаж за Q3 с разбивкой по категориям"
    │
    ▼
┌─────────────────────────────────────────────────────┐
│  Generative UI Agent                                 │
│                                                       │
│  1. Intent Analysis                                   │
│     → task=dashboard, data=sales, period=Q3,          │
│       groupBy=category                                │
│                                                       │
│  2. Pattern Selection                                 │
│     → DashboardPattern (Card + DataTable + Chart)     │
│                                                       │
│  3. Data Schema Resolution                            │
│     → API: /sales?period=Q3&groupBy=category          │
│     → fields: [name, amount, growth, category]        │
│                                                       │
│  4. Layout Assembly                                   │
│     → Header (Text: "Продажи Q3")                     │
│     → KPI Cards (3x Card with ProgressBar)            │
│     → DataTable (sortable, columns from schema)       │
│     → Chart (BarChart, data source = API)             │
│                                                       │
│  5. Token Application                                 │
│     → Apply brand tokens + DesignAlgorithm            │
│     → Density based on data density                   │
│                                                       │
│  6. Output: ScreenConfig JSON                         │
│     → Same format as BDUI                             │
│     → Can be cached, edited, versioned                │
└─────────────────────────────────────────────────────┘
    │
    │  ScreenConfig JSON
    │
    ▼
  BDUI Engine → Platform Renderer → Native UI
```

### Component Registry для AI

```kotlin
/**
 * Machine-readable описание каждого компонента.
 * AI использует это как "словарь" для сборки экранов.
 */
@Serializable
data class ComponentDescriptor(
    val type: String,                          // "button", "card", "dataTable"
    val level: ComponentLevel,                 // Primitive, Composite, Block
    val description: String,                   // Natural language description
    val configSchema: JsonSchema,              // JSON Schema для Config
    val supportedVariants: List<String>,        // ["solid", "soft", "outline", "ghost"]
    val supportedSizes: List<String>,           // ["xs", "sm", "md", "lg", "xl"]
    val supportedIntents: List<String>,         // ["primary", "neutral", "danger"]
    val slots: List<SlotDescriptor>,           // Children slots: header, body, footer
    val accessibility: AccessibilityDescriptor, // Required a11y attributes
    val usageGuidelines: String,               // When to use this component
    val examples: List<ExampleConfig>,         // Pre-built examples
)
```

---

## 9. Auto-расчёт размеров и адаптивный layout

### Проблема

Ручной расчёт размеров для каждого breakpoint, density, и brand — не масштабируется.

### Решение: Constraint-Based Auto Layout

```kotlin
/**
 * LayoutConstraints — описывают ПРАВИЛА, не конкретные числа.
 * Конкретные значения вычисляются автоматически из токенов + контекста.
 */
@Serializable
data class LayoutConstraints(
    val minWidth: SizeValue? = null,    // "auto" | "100%" | "200dp" | "{token.sizing.sm}"
    val maxWidth: SizeValue? = null,
    val minHeight: SizeValue? = null,
    val maxHeight: SizeValue? = null,
    val aspectRatio: Double? = null,     // width:height ratio
    val alignment: Alignment = Alignment.Start,
    val padding: SpacingValue = SpacingValue.Auto, // auto = из токенов по density
    val gap: SpacingValue = SpacingValue.Auto,
    val grow: Double = 0.0,              // flex-grow
    val shrink: Double = 1.0,            // flex-shrink
)

/**
 * SizeValue — может быть абсолютной, относительной, или auto.
 */
@Serializable
sealed interface SizeValue {
    data class Fixed(val dp: Double) : SizeValue
    data class Percent(val value: Double) : SizeValue
    data class Token(val ref: String) : SizeValue   // "{sizing.control.md}"
    object Auto : SizeValue                          // Рассчитать из content
    object Fill : SizeValue                          // Занять доступное пространство
}
```

### Layout Engine

```kotlin
/**
 * LayoutEngine — вычисляет конкретные размеры из constraints + tokens + viewport.
 * Pure function: (constraints, tokens, viewport) → ComputedLayout
 * 
 * Работает одинаково в Compose и React (KMP common).
 */
object LayoutEngine {
    fun compute(
        node: LayoutNode,           // Дерево nodes с constraints
        tokens: DesignTokens,       // Текущие токены
        viewport: Viewport,         // Размеры экрана, safe area insets
        density: Density,           // Comfortable / Cozy / Compact
    ): ComputedLayout
}
```

### Responsive без медиа-запросов

```kotlin
/**
 * Вместо @media breakpoints — адаптивные правила на токенах.
 * Layout автоматически переключается по breakpoint-tokens.
 */
@Serializable
data class ResponsiveConfig<T>(
    val compact: T,          // телефон portrait
    val medium: T? = null,   // телефон landscape / планшет portrait
    val expanded: T? = null, // планшет landscape / desktop
    val large: T? = null,    // desktop wide
) {
    fun resolve(windowClass: WindowSizeClass): T =
        when (windowClass) {
            WindowSizeClass.Compact -> compact
            WindowSizeClass.Medium -> medium ?: compact
            WindowSizeClass.Expanded -> expanded ?: medium ?: compact
            WindowSizeClass.Large -> large ?: expanded ?: medium ?: compact
        }
}

// Использование:
data class CardListConfig(
    val columns: ResponsiveConfig<Int> = ResponsiveConfig(
        compact = 1,
        medium = 2,
        expanded = 3,
        large = 4,
    ),
    val gap: ResponsiveConfig<String> = ResponsiveConfig(
        compact = "{space.4}",
        expanded = "{space.5}",
    ),
)
```

---

## 10. SSR и кросс-платформенная стратегия

### Текущий подход (сохраняем и развиваем)

```
┌────────────────────────────────────────────────────────────────┐
│  KMP Common (Kotlin → JVM + JS)                                │
│                                                                  │
│  - Config data classes (@Serializable, @JsExport)              │
│  - StyleResolvers (pure functions, stateless)                  │
│  - Expression Engine (pure interpreter)                        │
│  - Layout Engine (constraint solver)                           │
│  - Token definitions (from DTCG JSON codegen)                  │
│  - Component Registry (machine-readable catalog)               │
│                                                                  │
│  SSR SAFETY RULES:                                              │
│  1. No mutable state in object singletons                      │
│  2. No side effects in companion objects                        │
│  3. StyleResolvers = pure functions only                        │
│  4. No window/document/navigator access                        │
│  5. Deterministic: same inputs → same output                   │
└─────────┬──────────────────────────────────┬───────────────────┘
          │                                  │
          ▼                                  ▼
┌──────────────────┐              ┌──────────────────┐
│  Compose          │              │  React (Next.js)  │
│  Platform Renderer│              │  Platform Renderer│
│                    │              │                    │
│  @Composable fun   │              │  React.FC<Config>  │
│  ButtonView(config)│              │  SSR: tokens prop  │
│                    │              │  CSR: useContext    │
│  ~30 LOC/component │              │  ~30 LOC/component │
└──────────────────┘              └──────────────────┘
```

### SSR Enhancement: Streaming + Partial Hydration

```
Server (Node.js):
  1. Resolve tokens for request (brand, theme, locale)
  2. BDUI: fetch ScreenConfig from BDUI service (cached)
  3. Evaluate expressions with server-side data
  4. Render to HTML stream (React Server Components)
  5. Send HTML + minimal JSON for hydration

Client:
  1. Show server-rendered HTML immediately
  2. Hydrate only interactive components (partial hydration)
  3. Non-interactive components (Text, Divider, Image) = zero JS
  4. Expression Engine takes over for client state changes
```

### Будущие платформы

```
KMP Common (один раз написали)
    ├── Compose Desktop (JVM) ✅ сейчас
    ├── React Web (JS) ✅ сейчас
    ├── Compose Android (JVM) ← добавить
    ├── Compose iOS (Native) ← добавить
    ├── React Native (JS) ← возможно
    └── Kotlin/WASM ← исследовать (прямой Compose в браузере)
```

---

## 11. Производительность и качество

### Performance Budget

| Метрика              | Целевое значение        | Как достичь                              |
| -------------------- | ----------------------- | ---------------------------------------- |
| **StyleResolver**    | <1ms per component      | Pure function + memoization              |
| **Expression eval**  | <0.5ms per expression   | Compiled expression cache                |
| **Layout compute**   | <5ms per screen         | Single-pass constraint solver            |
| **Token resolution** | <1ms per brand switch   | Pre-resolved token sets                  |
| **BDUI parse**       | <10ms per screen JSON   | Streaming JSON parser                    |
| **First Paint**      | <100ms from config      | Zero-allocation hot path                 |
| **Recomposition**    | Skip unchanged subtrees | @Stable/@Immutable + structural equality |

### Quality Gates

```
CI Pipeline:
  ┌──── Build ────┐
  │  KMP common    │
  │  Compose       │
  │  React         │
  └───────┬───────┘
          │
  ┌───────▼───────┐
  │  Unit Tests    │
  │  StyleResolver │
  │  Expression    │
  │  LayoutEngine  │
  │  Token codegen │
  └───────┬───────┘
          │
  ┌───────▼───────┐
  │  Visual Tests  │  ← Screenshot comparison (Compose + React)
  │  Per component │
  │  Per brand     │
  │  Per breakpoint│
  └───────┬───────┘
          │
  ┌───────▼───────┐
  │  A11y Checks   │  ← Automated accessibility validation
  │  Contrast      │
  │  Focus order   │
  │  ARIA attrs    │
  │  Touch target  │
  └───────┬───────┘
          │
  ┌───────▼───────┐
  │  Performance   │  ← Benchmark regression detection
  │  Render time   │
  │  Bundle size   │
  │  Memory usage  │
  └───────┬───────┘
          │
  ┌───────▼───────┐
  │  Publish       │
  │  npm + Maven   │
  └───────────────┘
```

### Accessibility как первоклассный гражданин

```kotlin
/**
 * AccessibilityConfig — часть каждого ComponentConfig.
 * Не опциональный add-on, а обязательный слой.
 */
@Serializable
data class AccessibilityConfig(
    val label: String? = null,           // screen reader label
    val hint: String? = null,            // usage hint
    val role: AccessibilityRole? = null,  // button, heading, image, etc.
    val liveRegion: LiveRegion? = null,  // polite, assertive
    val stateDescription: String? = null, // "selected", "expanded"
    val traversalOrder: Int? = null,      // focus order override
)

// В DTCG tokens:
// Contrast ratios встроены как минимальные constraints
// Touch target size = minTouchTarget token (44dp)
// Focus ring = обязательный, не опциональный
```

---

## 12. Поэтапный план реализации

### Phase 0: Рефакторинг фундамента (2-3 недели)

**Цель**: Привести текущую базу в соответствие с новой таксономией

| #   | Задача                        | Описание                                             |
| --- | ----------------------------- | ---------------------------------------------------- |
| 0.1 | **Переименование структуры**  | `atoms/` → `primitives/` + `composites/`             |
| 0.2 | **DTCG JSON Source of Truth** | Создать `tokens/*.tokens.json` в DTCG формате        |
| 0.3 | **Token codegen**             | Скрипт: DTCG JSON → Kotlin data classes + CSS vars   |
| 0.4 | **AccessibilityConfig**       | Добавить `accessibility` поле во все ComponentConfig |
| 0.5 | **ComponentDescriptor**       | Machine-readable каталог компонентов                 |

### Phase 1: Core Primitives (3-4 недели)

**Цель**: Все неделимые примитивы

| #   | Компонент     | Приоритет | Примечание                           |
| --- | ------------- | --------- | ------------------------------------ |
| 1.1 | **Icon**      | P0        | Нужен почти всем composites          |
| 1.2 | **Divider**   | P0        | Простой, нужен в lists/forms         |
| 1.3 | **Spacer**    | P1        | Управляемый отступ для layout        |
| 1.4 | **Skeleton**  | P1        | Loading states                       |
| 1.5 | **Image**     | P1        | Async loading, placeholder, fallback |
| 1.6 | **Indicator** | P2        | Dot/numeric badge base               |

### Phase 2: Essential Composites (6-8 недель)

**Цель**: Базовые interactive-компоненты для форм и навигации

| #    | Компонент       | Приоритет | Зависимости           |
| ---- | --------------- | --------- | --------------------- |
| 2.1  | **IconButton**  | P0        | Icon                  |
| 2.2  | **TextField**   | P0        | Text, Icon, Surface   |
| 2.3  | **Checkbox**    | P0        | Icon, Surface         |
| 2.4  | **Radio**       | P0        | Indicator, Surface    |
| 2.5  | **Toggle**      | P0        | Indicator, Surface    |
| 2.6  | **Chip**        | P1        | Text, Icon, Surface   |
| 2.7  | **Tag**         | P1        | Text, Icon, Surface   |
| 2.8  | **Badge**       | P1        | Indicator, Text       |
| 2.9  | **Avatar**      | P1        | Image, Text, Surface  |
| 2.10 | **Tooltip**     | P2        | Text, Surface         |
| 2.11 | **ProgressBar** | P1        | Surface               |
| 2.12 | **Slider**      | P2        | Surface, Text         |
| 2.13 | **Snackbar**    | P1        | Text, Button, Surface |

### Phase 3: Blocks + Patterns (4-6 недель)

**Цель**: Сложные самодостаточные блоки и первые patterns

| #    | Компонент/Паттерн     | Приоритет |
| ---- | --------------------- | --------- |
| 3.1  | **Card**              | P0        |
| 3.2  | **ListItem**          | P0        |
| 3.3  | **Accordion**         | P1        |
| 3.4  | **Tabs**              | P0        |
| 3.5  | **Dialog**            | P0        |
| 3.6  | **BottomSheet**       | P1        |
| 3.7  | **Banner**            | P1        |
| 3.8  | **NavigationBar**     | P0        |
| 3.9  | **FormPattern**       | P0        |
| 3.10 | **CardListPattern**   | P1        |
| 3.11 | **EmptyStatePattern** | P1        |

### Phase 4: Design Algorithms + Multi-Brand (3-4 недели)

**Цель**: Динамическое переключение визуального стиля

| #   | Задача                                                        |
| --- | ------------------------------------------------------------- |
| 4.1 | **DesignAlgorithm interface**                                 |
| 4.2 | **CleanAlgorithm** (Apple-like)                               |
| 4.3 | **IllustratedAlgorithm** (рефакторинг текущего illustrated)   |
| 4.4 | **CorporateAlgorithm** (enterprise)                           |
| 4.5 | **ExpressiveAlgorithm** (Material 3 Expressive)               |
| 4.6 | **Brand JSON override system**                                |
| 4.7 | **Visual regression tests** для каждого algorithm × component |

### Phase 5: BDUI Engine (6-8 недель)

**Цель**: Полный BDUI pipeline от сервера до рендера

| #   | Задача                                                              |
| --- | ------------------------------------------------------------------- |
| 5.1 | **Expression Engine** (KMP common) — мини-интерпретатор             |
| 5.2 | **ScreenConfig extended** — Layout + Sections + Actions расширенные |
| 5.3 | **ActionRouter** — Navigate, ApiCall, UpdateState, ShowFeedback     |
| 5.4 | **State Manager** — Variables + Triggers + Reactive updates         |
| 5.5 | **BDUI Client** — Cache-First strategy, offline fallback            |
| 5.6 | **Component Registry** — O(1) type → renderer mapping               |
| 5.7 | **Template System** — Server-side шаблоны с наследованием           |
| 5.8 | **Patch System** — Incremental UI updates без полной перезагрузки   |

### Phase 6: Auto Layout Engine (4-5 недель)

**Цель**: Constraint-based auto-расчёт размеров

| #   | Задача                                                    |
| --- | --------------------------------------------------------- |
| 6.1 | **LayoutConstraints** data classes                        |
| 6.2 | **LayoutEngine** — single-pass constraint solver          |
| 6.3 | **ResponsiveConfig** — breakpoint-aware responsive values |
| 6.4 | **Compose LayoutEngine adapter**                          |
| 6.5 | **React LayoutEngine adapter** (CSS flex/grid generation) |
| 6.6 | **Auto-spacing** — density-aware spacing resolution       |

### Phase 7: Generative UI Foundation (4-6 недель)

**Цель**: Инфраструктура для AI-генерации экранов

| #   | Задача                                                              |
| --- | ------------------------------------------------------------------- |
| 7.1 | **Component Descriptor Registry** — полный machine-readable каталог |
| 7.2 | **Pattern Templates** — JSON шаблоны для каждого pattern            |
| 7.3 | **AI Schema Export** — OpenAPI/JSON Schema для всех configs         |
| 7.4 | **Screen Builder API** — программный интерфейс сборки экранов       |
| 7.5 | **Validation Engine** — проверка собранного экрана на корректность  |
| 7.6 | **Preview Server** — мгновенный рендер собранного конфига           |

### Phase 8: Generative UI Agent (8-12 недель)

**Цель**: AI-агент, собирающий экраны по intent

| #   | Задача                                                         |
| --- | -------------------------------------------------------------- |
| 8.1 | **Intent Parser** — NL → structured intent                     |
| 8.2 | **Pattern Selector** — intent → best pattern match             |
| 8.3 | **Data Schema Resolver** — автоподключение к API               |
| 8.4 | **Layout Assembler** — intent + pattern + data → ScreenConfig  |
| 8.5 | **Token Applicator** — автоприменение brand/density/breakpoint |
| 8.6 | **Feedback Loop** — пользователь правит → AI учится            |
| 8.7 | **A/B Generator** — AI генерирует варианты для A/B тестов      |

---

## Сводная таблица: от текущего состояния к целевому

| Аспект          | Сейчас (Phase 0) | После Phase 3                | После Phase 5     | После Phase 8   |
| --------------- | ---------------- | ---------------------------- | ----------------- | --------------- |
| **Компоненты**  | 4                | 30+                          | 30+               | 30+             |
| **Иерархия**    | atoms            | primitives/composites/blocks | + patterns        | + AI catalog    |
| **Токены**      | Kotlin only      | DTCG JSON → codegen          | + expressions     | + AI-queryable  |
| **Multi-brand** | 2 presets        | 4+ algorithms                | + brand JSON      | + dynamic brand |
| **BDUI**        | ❌                | Static only                  | Full BDUI         | + Generative    |
| **Layout**      | Manual           | Manual                       | + LayoutEngine    | Auto + AI       |
| **A11y**        | Basic            | Full a11y config             | + contrast checks | + self-healing  |
| **SSR**         | ✅                | ✅                            | + streaming       | + edge-cached   |
| **AI**          | ❌                | ❌                            | ❌                 | Full Generative |

---

## Принципы (сквозные на все фазы)

1. **Config = единица UI**. Неважно откуда Config пришёл (код, сервер, AI) — рендерер рисует одинаково.

2. **KMP Common = Single Source of Logic**. Вся бизнес-логика, стили, токены, expressions — в shared Kotlin. Рендереры тонкие.

3. **SSR-safe by default**. Любой новый код в common — pure functions, no state, no side effects.

4. **DTCG tokens = Source of Truth**. Kotlin/CSS/Figma генерируются из JSON. Никогда не правим generated-файлы.

5. **Accessibility first**. Каждый компонент рождается с AccessibilityConfig. Не добавляем потом.

6. **Performance budgets**. StyleResolver < 1ms. Layout < 5ms. Benchmark в CI.

7. **Visual regression**. Screenshot-тесты для каждого компонента × brand × breakpoint × state.

8. **Backwards-compatible BDUI**. Static mode работает без BDUI engine. BDUI — надстройка, не замена.

9. **AI-ready components**. Каждый компонент — self-describing (ComponentDescriptor). AI может прочитать каталог и собрать экран.

10. **Design → DTCG → Code → UI, не наоборот**. Дизайнеры работают в Figma → экспорт в DTCG JSON → codegen → UI. Один источник правды.
