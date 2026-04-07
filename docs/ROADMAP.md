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
│   │                   SurfaceContext, Visibility, ThemeProvider, Density,
│   │                   IconPosition, TextEmphasis, LayoutDirection,
│   │                   InteractiveColorResolver, ComponentSizeResolver, ColorConstants
│   └── components/
│       ├── primitives/
│       │   ├── icon/      ← IconConfig + IconStyleResolver
│       │   ├── text/      ← TextBlockConfig + TextBlockStyleResolver
│       │   └── surface/   ← SurfaceConfig + SurfaceStyleResolver
│       ├── composites/
│       │   ├── button/            ← ButtonConfig + ButtonStyleResolver
│       │   ├── chip/              ← ChipConfig + ChipStyleResolver
│       │   └── segmentedcontrol/  ← SegmentedControlConfig + SegmentedControlStyleResolver
│       └── blocks/
│           └── panel/             ← PanelConfig + PanelStyleResolver
├── compose/         ← Compose Desktop рендереры (~30 LOC каждый)
│   └── components/{primitives|composites|blocks}/  ← Icon, Text, Surface, Button, Chip, SegmentedControl, Panel + Spinner
└── react/           ← React 19 рендереры (~30 LOC каждый)
    └── components/{primitives|composites|blocks}/  ← Icon, Text, Surface, Button, Chip, SegmentedControl, Panel
```

**Итого: 3 primitives + 3 composites + 1 block = 7 компонентов.**
Таксономия `primitives/composites/blocks/` уже применена во всех трёх модулях.

### Сильные стороны ✅

| Аспект                       | Детали                                                            |
| ---------------------------- | ----------------------------------------------------------------- |
| **KMP Shared Logic**         | Config + StyleResolver в common → тонкие рендереры                |
| **Config-Driven UI**         | `@Serializable` data classes, готовность к SDUI/BDUI              |
| **Семантическое именование** | `ColorIntent.Primary`, `VisualVariant.Solid` (Radix-inspired)     |
| **3-уровневые токены**       | Primitive → Semantic → Component (логика есть)                    |
| **Runtime Theme Switching**  | Полная смена токенов без перекомпиляции                           |
| **SSR Safety**               | Pure functions, stateless resolvers, immutable data               |
| **Surface-Aware Context**    | Компоненты адаптируются к фону родительского Surface              |
| **Illustrated Style**        | Wireframe preset — доказательство multi-style flexibility         |
| **Новая таксономия**         | `primitives/composites/blocks/` вместо устаревшего `atoms/`       |
| **Shared Foundation**        | InteractiveColorResolver, ComponentSizeResolver, ColorSet — общие |

### Точки роста ❌

| Аспект                     | Проблема                                                                                                                    |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| **7 из 30+ компонентов**   | Нет: ListItem, Toggle, Image, ProgressBar, Card, NavigationBar, BottomSheet, TabBar, Divider, Badge, Avatar, Banner, Slider |
| **Нет DTCG JSON**          | Токены только в Kotlin, нет интеропа с Figma/Style Dictionary                                                               |
| **Нет Patterns layer**     | Нет best-practice композиций (SettingsPattern, CardGridPattern, DashboardPattern)                                           |
| **Нет BDUI Engine**        | SDUI описан в architecture doc, но не реализован                                                                            |
| **Нет Expressions**        | Нет runtime expressions для динамических значений                                                                           |
| **Accessibility**          | Базовые aria-атрибуты, нет contrast-чеков, нет a11y labels в configs                                                        |
| **Нет Auto Layout Engine** | Нет auto-расчёта размеров на основе content + constraints                                                                   |

### Валидация на реальном приложении (трекер питания)

Анализ экранов реального приложения выявил **критический gap** в текущей компонентной базе:

| Экран приложения      | Существующие компоненты  | Недостающие компоненты                                      |
| --------------------- | ------------------------ | ----------------------------------------------------------- |
| **Профиль/Настройки** | Text, Surface, Button    | ListItem, Toggle, Divider                                   |
| **Каталог рецептов**  | Text, Chip, Icon, Button | Card, Image, NavigationBar                                  |
| **Детали рецепта**    | Text, Button             | Image, Card, BottomSheet, Badge, NavigationBar              |
| **Главный дашборд**   | Text, Surface            | ProgressBar (circular+linear), Card, TabBar, Banner, Avatar |
| **Модал гидратации**  | Text, Button             | BottomSheet, Slider, Image                                  |
| **Шаги рецепта**      | Text, Surface, Button    | ListItem, Badge, NavigationBar                              |
| **Дневник питания**   | Text                     | BottomSheet, ListItem, Image, Tag                           |

**Вывод**: Для покрытия реальных экранов приоритетно нужны **ListItem**, **Toggle**, **Image**, **ProgressBar**, **Card** — они встречаются на 5+ из 8 экранов. Icon-only кнопки (назад, закрыть, подтвердить) уже покрываются существующим Button через `isIconOnly` режим.

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

| #   | Primitive     | Описание                      | Варианты                             | Статус | Приоритет (из анализа приложения)                                          |
| --- | ------------- | ----------------------------- | ------------------------------------ | ------ | -------------------------------------------------------------------------- |
| 1   | **Icon**      | Векторная иконка              | Размеры: Xs/Sm/Md/Lg/Xl              | ✅ Done | —                                                                          |
| 2   | **Text**      | Текстовый блок с типографикой | H1-H3, Body, Caption, Overline       | ✅ Done | —                                                                          |
| 3   | **Surface**   | Контейнер-плоскость           | Solid, Soft, Outline, Ghost × Level  | ✅ Done | —                                                                          |
| 4   | **Image**     | Отображение изображения       | Static, Async, Placeholder, Fallback | ❌      | 🔴 Hero-фото рецептов, сетка карточек, эмодзи ингредиентов — на 6/8 экранов |
| 5   | **Divider**   | Разделитель                   | Horizontal, Vertical                 | ❌      | 🟡 Разделители в настройках, между строками списков                         |
| 6   | **Spacer**    | Управляемый отступ            | Fixed, Flexible                      | ❌      | 🔵 Утилитарный, реализуется по необходимости                                |
| 7   | **Indicator** | Точка/число состояния         | Dot, Numeric                         | ❌      | 🟢 Основа для Badge, нотификации                                            |
| 8   | **Skeleton**  | Placeholder загрузки          | Text, Circle, Rectangle              | ❌      | 🟢 Для загрузки данных (подразумевается)                                    |

### Уровень 2: Composites (составные)

Composite = комбинация primitives + интерактивная логика.

| #   | Composite            | Состав                                | Варианты                                     | Статус | Приоритет (из анализа приложения)                                    |
| --- | -------------------- | ------------------------------------- | -------------------------------------------- | ------ | -------------------------------------------------------------------- |
| 1   | **Button**           | Surface + Text + Icon + States        | Solid/Soft/Outline/Ghost × Intent × Size     | ✅ Done | —                                                                    |
| 2   | **Chip**             | Surface + Text + Icon + Close         | Selectable, Dismissible, Info                | ✅ Done | —                                                                    |
| 3   | **SegmentedControl** | Surface + Buttons + Thumb             | Default, WithIcons                           | ✅ Done | —                                                                    |
| 4   | **Toggle**           | Surface + Indicator + Label           | Default, WithLabel                           | ❌      | 🔴 Экран настроек (3+ toggle на одном экране)                         |
| 6   | **ProgressBar**      | Track + Fill + Label                  | Linear, Circular, Determinate, Indeterminate | ❌      | 🟡 Калорийное кольцо, бары макросов Б/Ж/У, шкала полезности           |
| 7   | **Badge**            | Surface + Indicator + Text            | Dot, Numeric                                 | ❌      | 🟡 Номера шагов рецепта, streak 🔥1, нотификации                       |
| 8   | **Avatar**           | Surface + Image/Initials/Icon         | Image, Initials, Icon × Size                 | ❌      | 🟡 Иконки приёмов пищи, профиль, круглые эмодзи                       |
| 8   | **Tag**              | Surface + Text + Icon                 | Status, Category, Colored                    | ❌      | 🟢 Метка «Завтрак» на записях дневника                                |
| 9   | **TextField**        | Surface + Text + Icon + Label + Error | Default, Search, Password, Multiline         | ❌      | 🟢 Поиск рецептов, ввод данных (не виден на скриншотах, но необходим) |
| 10  | **Slider**           | Track + Thumb + Label                 | Single, Range                                | ❌      | 🟢 Горизонтальный пикер в модале гидратации                           |
| 12  | **Select**           | TextField + Dropdown + Options        | Single, Multi, Searchable                    | ❌      | 🔵 Выбор значений в настройках                                        |
| 13  | **Checkbox**         | Surface + Icon + Label                | Default, Indeterminate                       | ❌      | 🔵 Формы (не видно на скриншотах, но стандартный элемент)             |
| 14  | **Radio**            | Surface + Indicator + Label           | Default, Card                                | ❌      | 🔵 Выбор опций (не видно на скриншотах)                               |
| 15  | **Snackbar**         | Surface + Text + Action               | Info, Success, Warning, Error                | ❌      | 🔵 Обратная связь (подразумевается)                                   |
| 16  | **Tooltip**          | Surface + Text + Arrow                | Top, Bottom, Left, Right                     | ❌      | 🔵 По необходимости                                                   |
| 17  | **DatePicker**       | TextField + Calendar popup            | Date, DateTime, DateRange                    | ❌      | 🔵 Специализированный                                                 |

### Уровень 3: Blocks (самодостаточные блоки)

Block = сложный компонент с собственным layout, state management, дочерними elements.

| #   | Block              | Описание                       | Варианты                                             | Статус | Приоритет (из анализа приложения)                                             |
| --- | ------------------ | ------------------------------ | ---------------------------------------------------- | ------ | ----------------------------------------------------------------------------- |
| 1   | **Panel**          | Контейнер с padding/background | Default, Elevated                                    | ✅ Done | —                                                                             |
| 2   | **ListItem**       | Строка списка с action areas   | Default, WithIcon, WithAction, WithSwipe, WithToggle | ❌      | 🔴 Настройки профиля (10+ строк), ингредиенты, шаги — **самый частый** паттерн |
| 3   | **Card**           | Контейнер с header/body/footer | Default, Elevated, Outlined, Interactive             | ❌      | 🔴 Карточки рецептов, карточка калорий, секции настроек                        |
| 4   | **NavigationBar**  | Верхняя панель навигации       | Default, Large, Collapsing                           | ❌      | 🟡 На каждом экране (← Title ×), (← Рецепты ⚙)                                 |
| 5   | **BottomSheet**    | Overlay снизу                  | Fixed, Expandable, Persistent                        | ❌      | 🟡 Модал гидратации, дневник, детали рецепта                                   |
| 6   | **TabBar**         | Нижняя навигация               | Default, WithBadge                                   | ❌      | 🟡 Питание / Фитнес / Тренер / Прогресс                                        |
| 7   | **Banner**         | Информационная полоса          | Info, Success, Warning, Error, Promo                 | ❌      | 🟢 Промо «Велми Про» с подарком                                                |
| 8   | **Dialog**         | Модальное окно                 | Alert, Confirm, Custom, FullScreen                   | ❌      | 🟢 Подтверждения действий                                                      |
| 9   | **Accordion**      | Expandable секция              | Single, Multi, Nested                                | ❌      | 🔵 По необходимости                                                            |
| 10  | **Tabs**           | Табуляция с контентом          | Default, Scrollable, Pill, Vertical                  | ❌      | 🔵 Возможно на основе SegmentedControl                                         |
| 11  | **DataTable**      | Таблица данных                 | Sortable, Selectable, Paginated                      | ❌      | 🔵 Специализированный                                                          |
| 12  | **Calendar**       | Календарь                      | Month, Week, Range picker                            | ❌      | 🔵 Специализированный                                                          |
| 13  | **FileUpload**     | Загрузка файлов                | Drag&Drop, Button, Multi                             | ❌      | 🔵 Специализированный                                                          |
| 14  | **RichTextEditor** | Форматированный текст          | Basic, Full, Markdown                                | ❌      | 🔵 Специализированный                                                          |

### Уровень 4: Patterns (шаблоны сборки)

Pattern ≠ компонент. Pattern = **рецепт**: какие Components в каком Layout с какими UX-правилами.

| Pattern                  | Описание                     | Components используемые                            | Приоритет (из анализа приложения)              |
| ------------------------ | ---------------------------- | -------------------------------------------------- | ---------------------------------------------- |
| **SettingsPattern**      | Экран настроек               | ListItem, Toggle, Divider, Select, Dialog, Surface | 🔴 Экран профиля — 10+ строк с toggle/chevron   |
| **CardGridPattern**      | Сетка карточек               | Card, Image, Text, Badge                           | 🔴 Каталог рецептов — 2-колоночная сетка        |
| **DashboardPattern**     | Информационная панель        | Card, ProgressBar, Text, Badge, Avatar, Banner     | 🟡 Главный экран с калориями и макросами        |
| **DetailPattern**        | Экран деталей                | Image, Card, Button, Text, NavigationBar, ListItem | 🟡 Детали рецепта (hero image + инфо + actions) |
| **FormPattern**          | Best-practice форма          | TextField, Select, Checkbox, Radio, Button, Banner | 🟢 Стандартный паттерн                          |
| **CardListPattern**      | Вертикальный список карточек | Card, ListItem, Skeleton, ProgressBar              | 🟢 Списки ингредиентов, шаги рецепта            |
| **SearchResultsPattern** | Поиск с фильтрами            | TextField, Chip, Card, Tabs, Skeleton              | 🟢 Поиск рецептов (подразумевается)             |
| **OnboardingPattern**    | Пошаговый онбординг          | Surface, Text, Image, Button, ProgressBar          | 🔵 Будущее                                      |
| **EmptyStatePattern**    | Пустое состояние             | Image, Text, Button                                | 🔵 Когда нет данных                             |
| **ErrorPattern**         | Обработка ошибок             | Banner, Button, Text                               | 🔵 Стандартный паттерн                          |
| **AuthPattern**          | Авторизация                  | TextField, Button, Divider, Text                   | 🔵 Стандартный паттерн                          |
| **NavigationPattern**    | Навигационная структура      | NavigationBar, TabBar, BottomSheet                 | 🟡 Каркас всего приложения                      |

### Маппинг из текущей структуры

Миграция с `atoms/` на `primitives/composites/blocks/` **уже выполнена**:

```
БЫЛО (atoms/):                      → СТАЛО (текущее):
components/atoms/button/             → components/composites/button/     ✅
components/atoms/text/               → components/primitives/text/       ✅
components/atoms/surface/            → components/primitives/surface/    ✅
components/atoms/segmentedcontrol/   → components/composites/segmented-control/ ✅
(не было)                            → components/primitives/icon/       ✅ NEW
(не было)                            → components/composites/chip/       ✅ NEW
(не было)                            → components/blocks/panel/          ✅ NEW
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

### Философия: вертикальный срез вместо горизонтальных слоёв

> **Не делаем** «все примитивы → все composites → все блоки» послойно.
> **Делаем** вертикальный срез: берём связанную группу компонентов Icon → Button (включая icon-only режим) → Chip → Tag → TextField → SegmentedControl и доводим до production-quality.
>
> **Почему**: Кнопки встречаются *везде* — внутри TextField (clear-иконка), внутри Chip (dismiss), внутри SegmentedControl (сегменты = Button icon-only). Если размеры, отступы и слоты не состыкованы — это станет техдолгом на 30+ компонентов. Лучше проверить гипотезу переиспользуемости на 7 компонентах, чем переделывать 30.

```
Phase 0 — Hypothesis Validation (вертикальный срез)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  0a: Icon ──────► 0b: Button (+ icon-only mode) ──────► 0c: Chip + Tag
       │                     │                                  │
       │                     │       ┌──────────────────────────┘
       │                     ▼       ▼
       │              0d: TextField (Icon + Button icon-only внутри)
       │                     │ 
       │                     ▼
       └──────────► 0e: SegmentedControl (Button icon-only внутри)

  Каждый шаг валидирует: размеры стыкуются? иконки правильные?
  отступы консистентны? вложенные компоненты переиспользуются?
```

---

### Phase 0: Hypothesis Validation — вертикальный срез (4-6 недель)

**Цель**: Проверить архитектуру переиспользуемости на связанной группе компонентов.
Все 7 компонентов должны использовать единую `ControlSizeScale`, единый Icon, единые правила отступов.

**Статус**: ✅ **В основном завершена.** Реализованы 7 компонентов (Icon, Text, Surface, Button, Chip, SegmentedControl, Panel).
Таксономия `primitives/composites/blocks/` применена. `ControlSizeScale` валидирована на Button, Chip, SegmentedControl.

**Осталось доделать в Phase 0**:
- [ ] Tag (0c.4-0c.5) — лёгкий composite на базе Chip-подобного подхода
- [ ] Ретроспектива 0f — зафиксировать SIZING_STANDARDS.md

> **Примечание**: IconButton как отдельный компонент **не нужен** — Button уже поддерживает icon-only режим через computed property `isIconOnly` (когда `text.isEmpty() && hasIcon`). StyleResolver автоматически делает кнопку квадратной с центрированной иконкой.

#### Сквозная размерная сетка (Unified Sizing Grid)

Все компоненты Phase 0 строятся на одной сетке. Это **главная гипотеза** для валидации.

```
ControlSizeScale (текущие значения):
┌──────┬────────┬──────────┬──────────┬────────────┬──────────┬───────────┬────────┐
│ Size │ Height │ PaddingH │ FontSize │ FontWeight │ IconSize │ LetterSp  │ Radius │
├──────┼────────┼──────────┼──────────┼────────────┼──────────┼───────────┼────────┤
│ Xs   │  24    │    4     │   11     │    600     │   12     │   0.07    │   4    │
│ Sm   │  32    │    8     │   12     │    600     │   16     │   0.0     │   6    │
│ Md   │  40    │   16     │   17     │    600     │   20     │  -0.41    │   8    │
│ Lg   │  48    │   24     │   17     │    600     │   24     │  -0.41    │  10    │
│ Xl   │  56    │   32     │   17     │    600     │   32     │  -0.41    │  12    │
└──────┴────────┴──────────┴──────────┴────────────┴──────────┴───────────┴────────┘

Как компоненты используют эту сетку:
┌────────────────────┬───────────────────────────────────────────────────────────────┐
│ Компонент          │ Использование ControlSizeScale                               │
├────────────────────┼───────────────────────────────────────────────────────────────┤
│ Icon               │ iconSize из шкалы (12/16/20/24/32)                           │
│ Button             │ height, paddingH, fontSize, iconSize, radius — всё из шкалы  │
│ Button (icon-only) │ height = width (квадрат), iconSize из шкалы, radius = full   │
│ Chip               │ height из шкалы, iconSize, paddingH, fontSize — из шкалы     │
│ Tag                │ высота = Xs/Sm из шкалы (компактный), iconSize, paddingH     │
│ TextField          │ height из шкалы, внутренние Button icon-only — на 1-2 ↓      │
│ SegmentedControl   │ height из шкалы, внутренние сегменты = Button icon-only       │
└────────────────────┴───────────────────────────────────────────────────────────────┘

Правило вложенности: вложенный контрол = размер родителя минус 1-2 ступени.
  Button Md (40dp) внутри TextField Lg (48dp) ✓
  Button icon-only Xs (24dp) внутри Chip Sm (32dp) ✓
  Button icon-only Sm (32dp) внутри SegmentedControl Md (40dp - 2*trackPadding) ✓
```

#### 0a: Icon (примитив) — неделя 1

**Цель**: Единый Icon-компонент, привязанный к `ControlSizeScale.iconSize`.

| #    | Задача                    | Описание                                                                              |
| ---- | ------------------------- | ------------------------------------------------------------------------------------- |
| 0a.1 | **IconConfig**            | `name: String`, `size: ComponentSize`, `intent: ColorIntent`, `customSizeDp: Double?` |
| 0a.2 | **IconStyleResolver**     | Резолвит размер из `ControlSizeScale.iconSize` + цвет из `ColorTokens`                |
| 0a.3 | **Icon Compose renderer** | Material Icons / custom SVG, размер = `tokens.iconSize.dp`                            |
| 0a.4 | **Icon React renderer**   | SVG / icon font, размер = `toRem(iconSize)`                                           |
| 0a.5 | **Icon в каталоге**       | Showcase всех размеров Xs-Xl + все intents в catalog-ui                               |

```kotlin
// IconConfig — привязан к единой сетке размеров
@Serializable
data class IconConfig(
    val name: String,                                    // "close", "search", "chevron-right"
    val size: ComponentSize = ComponentSize.Md,           // → iconSize: 12/16/20/24/32
    val intent: ColorIntent = ColorIntent.Neutral,        // → цвет иконки
    val customSizeDp: Double? = null,                    // override для нестандартных случаев
    val decorative: Boolean = false,                     // true = aria-hidden, не для screen reader
    val id: String = "",
    val visibility: Visibility = Visibility.Visible,
)

// Размеры иконок — строго из ControlSizeScale:
// Xs=12dp, Sm=16dp, Md=20dp, Lg=24dp, Xl=32dp
// Совпадают с iconSize в кнопках, chip, TextField, etc.
```

**Критерий успеха**: Icon с `size = Md` (20dp) идеально вписывается в Button с `size = Md` (height=40dp) по визуальному балансу.

#### 0b: Button — ✅ реализован (включая icon-only режим)

**Статус**: ✅ Done. Button уже поддерживает:
- `isIconOnly` — computed property: `text.isEmpty() && (hasIconStart || hasIconEnd)` → квадратная кнопка
- `hasIconStart` / `hasIconEnd` — icon slots
- `iconPosition` — Start/End/Top/Bottom
- StyleResolver автоматически вычисляет `paddingH = (height - iconSize) / 2` для icon-only

**Правила отступов Button с иконкой** (вдохновлено M3 Expressive):

```
Button с текстом (без иконки):
┌──── paddingH ────┬── text ──┬──── paddingH ────┐
│                   │          │                   │

Button с leadingIcon (IconPosition.Start):
┌── paddingIcon ──┬── icon ──┬── gap ──┬── text ──┬── paddingH ──┐
│    paddingH-4    │  20dp   │  4-8dp  │          │              │

Button с trailingIcon (IconPosition.End):
┌── paddingH ──┬── text ──┬── gap ──┬── icon ──┬── paddingIcon ──┐
│              │          │  4-8dp  │  20dp   │    paddingH-4    │

Button icon-only (квадрат):
┌───────────────────┐
│    ╭─────────╮    │
│    │  icon   │    │  width = height = controlHeight
│    ╰─────────╯    │  paddingH = (height - iconSize) / 2
└───────────────────┘
```

**Критерий успеха**: Button icon-only с `size = Sm` (32×32dp, icon=16dp) визуально и размерно совместим со слотом внутри SegmentedControl и Chip.

#### 0c: Chip + Tag — недели 2-3

**Цель**: Chip и Tag используют ту же `ControlSizeScale`, что и Button. Chip содержит dismiss-Button (icon-only) внутри.

| #    | Задача                                        | Описание                                                                            |
| ---- | --------------------------------------------- | ----------------------------------------------------------------------------------- |
| 0c.1 | **ChipConfig**                                | `label`, `leadingIcon?`, `dismissible`, `selected`, `size`, `variant`               |
| 0c.2 | **Chip sizing = Button sizing**               | `height` берётся из `ControlSizeScale` — Chip Sm = 32dp = Button Sm                 |
| 0c.3 | **Chip dismiss = вложенный Button icon-only** | Dismiss-крестик = Button icon-only на 2 ступени меньше (Chip Md → dismiss Xs)       |
| 0c.4 | **TagConfig**                                 | Упрощённый chip: `label`, `icon?`, `intent`, `size` (только Xs/Sm)                  |
| 0c.5 | **Tag как компактный Chip**                   | Tag использует Xs/Sm из `ControlSizeScale`, не интерактивный (не dismissible)       |
| 0c.6 | **Compose + React renderers**                 | ChipView, TagView для обеих платформ                                                |
| 0c.7 | **Каталог**                                   | Showcase: Chip (все варианты, с/без иконки, dismissible) + Tag (все intents, sizes) |

```kotlin
@Serializable
data class ChipConfig(
    val label: String,
    val leadingIcon: String? = null,                     // иконка слева
    val dismissible: Boolean = false,                    // показать крестик
    val selected: Boolean = false,                       // выделенное состояние
    val size: ComponentSize = ComponentSize.Sm,           // height из ControlSizeScale
    val variant: VisualVariant = VisualVariant.Outline,   // Outline по умолчанию (как M3)
    val intent: ColorIntent = ColorIntent.Neutral,
    val disabled: Boolean = false,
    val id: String = "",
    val actionRoute: String? = null,
    val testTag: String? = null,
    val visibility: Visibility = Visibility.Visible,
)

@Serializable
data class TagConfig(
    val label: String,
    val icon: String? = null,                            // иконка слева
    val size: ComponentSize = ComponentSize.Xs,           // компактный: Xs(24dp) или Sm(32dp)
    val intent: ColorIntent = ColorIntent.Neutral,        // Status, Category coloring
    val variant: VisualVariant = VisualVariant.Soft,      // Soft по умолчанию
    val id: String = "",
    val testTag: String? = null,
    val visibility: Visibility = Visibility.Visible,
)
```

**Правило вложенности Chip**:

```
Chip Sm (height=32dp):
┌── 8dp ──┬── icon(16dp) ──┬── 4dp ──┬── label ──┬── 4dp ──┬── ✕(Xs=12dp) ──┬── 4dp ──┐
│ paddingL │   leadingIcon   │   gap   │   text    │   gap   │ dismiss Btn  │ paddR   │
                                                              ↑
                                                    Button icon-only Xs: 20×20dp внутри 
                                                    (компактнее стандартного 24dp)

Chip Md (height=40dp):  
┌── 12dp ──┬── icon(20dp) ──┬── 6dp ──┬── label ──┬── 4dp ──┬── ✕(Sm=16dp) ──┬── 8dp ──┐
│ paddingL  │   leadingIcon   │   gap   │   text    │   gap   │ dismiss Btn  │ paddR   │
                                                               ↑
                                                     Button icon-only Xs: 24×24dp внутри
```

**Критерий успеха**: Chip Sm (32dp) + Button Sm (32dp) + SegmentedControl Sm (32dp) — идентичная высота. Dismiss-крестик в Chip — переиспользованный Button icon-only.

#### 0d: TextField — недели 3-4

**Цель**: TextField использует Icon и Button icon-only внутри, высота из `ControlSizeScale`.

| #    | Задача                                  | Описание                                                                                        |
| ---- | --------------------------------------- | ----------------------------------------------------------------------------------------------- |
| 0d.1 | **TextFieldConfig**                     | `value`, `placeholder`, `label?`, `leadingIcon?`, `trailingIcon?`, `clearable`, `size`          |
| 0d.2 | **TextField height = ControlSizeScale** | TextField Md = 40dp, Lg = 48dp (как Button)                                                     |
| 0d.3 | **Внутренние иконки = Icon**            | `leadingIcon` = Icon (декоративная, `intent = Neutral`)                                         |
| 0d.4 | **Clear button = Button icon-only**     | `clearable = true` → Button(icon-only, icon="close") Xs/Sm внутри (на 2 ступени ↓ от TextField) |
| 0d.5 | **Состояния**                           | Default, Focused, Error, Disabled + hover transitions                                           |
| 0d.6 | **Label + Error message**               | Floating label (опционально) + error Text под полем                                             |
| 0d.7 | **Compose + React renderers**           | TextFieldView для обеих платформ                                                                |
| 0d.8 | **Каталог**                             | Showcase: варианты (default/search/password), размеры, состояния, с иконками                    |

```kotlin
@Serializable
data class TextFieldConfig(
    val value: String = "",
    val placeholder: String = "",
    val label: String? = null,                            // floating label
    val leadingIcon: String? = null,                      // Icon слева (декоративная)
    val trailingIcon: String? = null,                     // Icon справа (или custom action)
    val clearable: Boolean = false,                       // крестик очистки (= Button icon-only)
    val size: ComponentSize = ComponentSize.Md,            // height из ControlSizeScale
    val variant: TextFieldVariant = TextFieldVariant.Outline, // Outline / Filled / Ghost
    val inputType: TextFieldInputType = TextFieldInputType.Text,
    val multiline: Boolean = false,
    val maxLines: Int = 1,
    val error: String? = null,                            // error message под полем
    val disabled: Boolean = false,
    val readOnly: Boolean = false,
    val id: String = "",
    val testTag: String? = null,
    val visibility: Visibility = Visibility.Visible,
)

@Serializable
enum class TextFieldVariant { Outline, Filled, Ghost }

@Serializable
enum class TextFieldInputType { Text, Password, Email, Number, Search, Tel, Url }
```

**Layout TextField с вложенными компонентами**:

```
TextField Md (height=40dp, Outline variant):
┌─────────────────────────────────────────────────────────────────────┐
│ ┌──────┐                                              ┌──────────┐ │
│ │ Icon │  placeholder / value text                    │  clear  │ │
│ │ 20dp │  fontSize=17, из ControlSizeScale.Md         │ Btn Xs  │ │
│ └──────┘                                              └──────────┘ │
└─────── paddingH=16dp ──────────────────── paddingH=8dp ───────────┘
   ↑                                           ↑
   leadingIcon = Icon(Md, 20dp)                clearable → Button icon-only (Xs, icon="close")
   
TextField Lg (height=48dp):
┌─────────────────────────────────────────────────────────────────────┐
│ ┌──────┐                                              ┌──────────┐ │
│ │ Icon │  placeholder / value text                    │  clear  │ │
│ │ 24dp │  fontSize=17, из ControlSizeScale.Lg         │ Btn Sm  │ │
│ └──────┘                                              └──────────┘ │
└─────── paddingH=24dp ──────────────────── paddingH=12dp ──────────┘
   ↑                                           ↑
   leadingIcon = Icon(Lg, 24dp)                clearable → Button icon-only (Sm, icon="close")
```

**Критерий успеха**: Icon и Button icon-only внутри TextField — **те же самые** компоненты, что используются standalone. Размеры корректно вычисляются через правило вложенности «родитель - 2 ступени».

#### 0e: SegmentedControl (доработка) — недели 4-5

**Цель**: SegmentedControl поддерживает иконки (Icon-only, Icon+Text), внутренние сегменты используют sizing на основе Button icon-only.

| #    | Задача                                 | Описание                                                                                    |
| ---- | -------------------------------------- | ------------------------------------------------------------------------------------------- |
| 0e.1 | **SegmentedControlOption → icon slot** | Добавить `icon: String?` к `SegmentedControlOption`                                         |
| 0e.2 | **Icon-only сегменты**                 | Если `label = ""` и `icon != null` → иконка-only сегмент (квадратный, как Button icon-only) |
| 0e.3 | **Icon + Text сегменты**               | `icon + label` → иконка слева от текста с gap (аналогично Button с leadingIcon)             |
| 0e.4 | **Внутренний sizing**                  | Thumb и сегменты используют iconSize из ControlSizeScale (согласовано с Button)             |
| 0e.5 | **Compose + React renderers**          | Обновить SegmentedControlView для обеих платформ                                            |
| 0e.6 | **Каталог**                            | Showcase: text-only, icon-only, icon+text, разные размеры                                   |

```kotlin
// Обновлённый SegmentedControlOption с icon слотом
@Serializable
data class SegmentedControlOption(
    val id: String,
    val label: String = "",                // пустой label → icon-only сегмент
    val icon: String? = null,              // имя иконки
)

// Sizing сегментов: 
// SegmentedControl Md (height=40dp, trackPadding=2dp)
//   → внутренний сегмент высота = 40 - 2*2 = 36dp
//   → icon внутри = iconSize Md = 20dp
//   → визуально совпадает с Button icon-only между Sm(32) и Md(40)
```

**Layout SegmentedControl с иконками**:

```
SegmentedControl Md (height=40dp), text-only (текущее поведение):
┌──────────────────────────────────────────────────────────┐
│ ┌─── trackPad ──┬──────────┬──────────┬──────────┬─────┐│
│ │               │ Option A │ Option B │ Option C │     ││
│ │     2dp       │ text-only│ text-only│ text-only│ 2dp ││
│ └───────────────┴──────────┴──────────┴──────────┴─────┘│
└──────────────────────────────────────────────────────────┘

SegmentedControl Md (height=40dp), icon-only:
┌──────────────────────────────────────────────────────────┐
│ ┌─── 2dp ──┬──────────┬──────────┬──────────┬── 2dp ──┐│
│ │          │  ⊞ 20dp  │  ≡ 20dp  │  ◷ 20dp  │         ││
│ │          │ icon-only│ icon-only│ icon-only│         ││
│ └──────────┴──────────┴──────────┴──────────┴─────────┘│
└──────────────────────────────────────────────────────────┘

SegmentedControl Md (height=40dp), icon + text:
┌─────────────────────────────────────────────────────────────────────┐
│ ┌── 2dp ──┬─────────────────┬─────────────────┬─────────┬── 2dp ──┐│
│ │         │ ⊞ 20dp  Grid    │ ≡ 20dp  List    │ ◷ Map   │         ││
│ │         │ icon+text       │ icon+text       │ i+txt   │         ││
│ └─────────┴─────────────────┴─────────────────┴─────────┴─────────┘│
└─────────────────────────────────────────────────────────────────────┘
```

**Критерий успеха**: Иконки в сегментах SegmentedControl визуально идентичны standalone Icon с тем же `ComponentSize`. Переключение между icon-only/text-only/icon+text не ломает высоту и alignment.

#### 0f: Ретроспектива и фиксация стандартов — неделя 5-6

| #    | Задача                                   | Описание                                                                           |
| ---- | ---------------------------------------- | ---------------------------------------------------------------------------------- |
| 0f.1 | **Sizing Audit**                         | Проверить все 7 компонентов: высоты, отступы, iconSize совпадают по шкале?         |
| 0f.2 | **Nesting Rules Document**               | Зафиксировать правила вложенности (родитель - N ступеней = дочерний размер)        |
| 0f.3 | **Visual Regression Baseline**           | Screenshot-тесты для ВСЕХ комбинаций 7 компонентов × 5 размеров × 2 тем            |
| 0f.4 | **Token Gap Analysis**                   | Нужны ли дополнительные токены (iconTextGap, chipDismissSize, innerControlOffset)? |
| 0f.5 | **Cross-platform Pixel-Perfect Check**   | Compose vs React рендер — совпадение по размерам, отступам, радиусам               |
| 0f.6 | **Фиксация InteractiveControlTokens v2** | Расширить ControlSizeScale если нужны доп. поля (iconTextGap, innerPadding)        |

**Выход Phase 0**: Документ `SIZING_STANDARDS.md` с зафиксированными правилами + 7 production-ready компонентов + visual regression baseline.

---

### Phase 1: App-Critical Components — покрытие реальных экранов (4-6 недель)

**Цель**: Реализовать компоненты, без которых невозможно собрать ни один экран реального приложения.
Приоритизация основана на анализе скриншотов трекера питания (8 экранов).

> **Принцип**: Сначала — компоненты, которые встречаются на 5+ экранах из 8.

| #   | Компонент       | Тип       | Описание                                                                                                                                                                                                                             | Экранов |
| --- | --------------- | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------- |
| 1.1 | **ListItem**    | Block     | Строка списка: leading(Icon/Image) + content(label+description) + trailing(value+chevron/toggle/action). Варианты: Navigation (→), Value (значение →), Toggle (переключатель), Action (кнопка). Самый частый компонент в приложении. | 7/8     |
| 1.2 | **Toggle**      | Composite | Switch с анимированным thumb. Label + Description слоты. States: on/off/disabled. Физический размер из ControlSizeScale.                                                                                                             | 5/8     |
| 1.3 | **Image**       | Primitive | Загрузка из URL/ресурса. Placeholder → loading → loaded/error. Варианты: fill, fit, cover. Поддержка aspect ratio. cornerRadius из RadiusTokens.                                                                                     | 6/8     |
| 1.4 | **ProgressBar** | Composite | Linear Track + Fill + Label. Circular (ring) + value. Градиентная заливка (шкала полезности). Determinate/Indeterminate.                                                                                                             | 4/8     |
| 1.5 | **Divider**     | Primitive | Horizontal/Vertical. Толщина 1dp, цвет из ColorTokens.borderDefault. Inset (с отступом для ListItem).                                                                                                                                | 5/8     |
| 1.6 | **Badge**       | Composite | Dot (без текста) или Numeric (с числом). Overlay-positioned на родителе. Используется для step numbers, streak, нотификаций.                                                                                                         | 3/8     |

**Ключевая валидация Phase 1**: ListItem должен уметь содержать Toggle, Button (icon-only), Badge, Image, chevron-Icon — все как вложенные компоненты с правильным sizing.

### Phase 2: Layout Blocks — каркас приложения (4-5 недель)

**Цель**: Блоки, из которых строится layout каждого экрана: навигация, карточки, модалы.

| #   | Компонент         | Тип       | Описание                                                                                                                                    | Экранов                     |
| --- | ----------------- | --------- | ------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------- |
| 2.1 | **Card**          | Block     | Surface + header/body/footer slots. Варианты: Default, Elevated, Outlined, Interactive (clickable). Поддержка Image как hero-slot.          | 5/8                         |
| 2.2 | **NavigationBar** | Block     | Верхняя панель: leading Button icon-only (←/×) + title Text + trailing actions. Large variant с большим заголовком.                         | 7/8                         |
| 2.3 | **BottomSheet**   | Block     | Overlay снизу. Drag handle + content. Fixed / Expandable / Persistent. Header slot (title + close Button icon-only).                        | 3/8                         |
| 2.4 | **TabBar**        | Block     | Нижняя навигация. 3-5 items: Icon + Label. Active/Inactive states. Badge overlay.                                                           | 1/8 но на **каждом** экране |
| 2.5 | **Banner**        | Block     | Промо/информация. Icon + Title + Description + Action. Варианты: Info, Success, Warning, Error, Promo. Dismissible.                         | 2/8                         |
| 2.6 | **Avatar**        | Composite | Круглое изображение/иконка. Image / Initials / Icon fallback. Размеры из ControlSizeScale. Используется для иконок приёмов пищи, категорий. | 3/8                         |
| 2.7 | **Tag**           | Composite | Компактный info-лейбл. label + intent coloring. Xs/Sm из ControlSizeScale. Не интерактивный.                                                | 2/8                         |

### Phase 3: Form Controls + Extended Blocks (4-6 недель)

**Цель**: Формовые элементы и расширенные блоки.

| #    | Компонент/Паттерн    | Тип       | Описание                                                                                                     |
| ---- | -------------------- | --------- | ------------------------------------------------------------------------------------------------------------ |
| 3.1  | **TextField**        | Composite | Input с label, placeholder, leading/trailing icons, clear button (= вложенный Button icon-only), error state |
| 3.2  | **Select**           | Composite | TextField + Dropdown + Options                                                                               |
| 3.3  | **Slider**           | Composite | Track + Thumb + Label (горизонтальный пикер)                                                                 |
| 3.4  | **Checkbox**         | Composite | Icon(check) + Surface + Label                                                                                |
| 3.5  | **Radio**            | Composite | Indicator(dot) + Surface + Label                                                                             |
| 3.6  | **Dialog**           | Block     | Модальное окно с Button actions                                                                              |
| 3.7  | **Accordion**        | Block     | Expandable секции                                                                                            |
| 3.8  | **Skeleton**         | Primitive | Loading placeholders (Text/Circle/Rectangle)                                                                 |
| 3.9  | **Snackbar**         | Composite | Feedback: Text + Action + auto-dismiss                                                                       |
| 3.10 | **SettingsPattern**  | Pattern   | ListItem + Toggle + Divider + Surface sections                                                               |
| 3.11 | **CardGridPattern**  | Pattern   | Card + Image в responsive grid                                                                               |
| 3.12 | **DashboardPattern** | Pattern   | ProgressBar + Card + sections layout                                                                         |

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

| Аспект           | Сейчас (Phase 0)     | После Phase 1                                         | После Phase 2                                                 | После Phase 3                                                                    | После Phase 5     | После Phase 8   |
| ---------------- | -------------------- | ----------------------------------------------------- | ------------------------------------------------------------- | -------------------------------------------------------------------------------- | ----------------- | --------------- |
| **Компоненты**   | 7 (+Tag todo)        | 15 (+ListItem,Toggle,Image,ProgressBar,Divider,Badge) | 22 (+Card,NavigationBar,BottomSheet,TabBar,Banner,Avatar,Tag) | 30+ (+TextField,Select,Slider,Checkbox,Radio,Dialog,Accordion,Skeleton,Snackbar) | 30+               | 30+             |
| **Покрытие app** | ~20% экранов         | ~60% экранов                                          | ~85% экранов                                                  | ~95% экранов                                                                     | 95%+              | 100%            |
| **Sizing**       | ControlSizeScale     | + ListItem height rules                               | + Card spacing                                                | + form control sizing                                                            | + auto-layout     | + AI-sizing     |
| **Иерархия**     | prim + comp + blocks | + critical blocks                                     | + layout blocks                                               | + patterns                                                                       | + patterns        | + AI catalog    |
| **Токены**       | Kotlin only          | Kotlin only                                           | DTCG JSON → codegen                                           | + component tokens                                                               | + expressions     | + AI-queryable  |
| **Multi-brand**  | 2 presets            | 2 presets                                             | 4+ algorithms                                                 | + brand JSON                                                                     | + brand JSON      | + dynamic brand |
| **BDUI**         | ❌                    | ❌                                                     | Static only                                                   | Static only                                                                      | Full BDUI         | + Generative    |
| **A11y**         | Basic                | + aria для новых                                      | Full a11y config                                              | + contrast checks                                                                | + contrast checks | + self-healing  |
| **SSR**          | ✅                    | ✅                                                     | ✅                                                             | ✅                                                                                | + streaming       | + edge-cached   |
| **Patterns**     | ❌                    | ❌                                                     | ❌                                                             | 3 patterns                                                                       | 10 patterns       | + AI-generated  |

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
