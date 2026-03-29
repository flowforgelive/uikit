# Стандарты UX/UI: порядок создания дизайн-системы

## Общая картина

Создание дизайн-системы идёт **снизу вверх** — от абстрактных значений к готовым экранам. Ниже — полная последовательность этапов.

---

## Этап 0 — Design Tokens (дизайн-токены)

> Самый фундамент. Здесь нет ещё никаких компонентов — только **голые значения**, которые потом будут использоваться везде.

### Что определяем:

| Категория                       | Примеры значений                                                                                               |
| ------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Цвета (palette)**             | `gray-100…900`, `blue-500`, `red-500` — полная палитра                                                         |
| **Семантические цвета**         | `color-primary`, `color-error`, `color-bg-surface`, `color-text-primary`                                       |
| **Типографика — шрифты**        | Font family: `Inter`, `Roboto`, `SF Pro`                                                                       |
| **Типографика — размеры**       | `font-size-xs: 12px`, `font-size-sm: 14px`, `font-size-md: 16px`, `font-size-lg: 20px`, `font-size-xl: 24px` … |
| **Типографика — высота строки** | `line-height-tight: 1.2`, `line-height-normal: 1.5`, `line-height-loose: 1.75`                                 |
| **Типографика — начертания**    | `font-weight-regular: 400`, `font-weight-medium: 500`, `font-weight-bold: 700`                                 |
| **Отступы (spacing)**           | Шкала с множителем: `4px, 8px, 12px, 16px, 20px, 24px, 32px, 40px, 48px, 64px`                                 |
| **Скругления (border-radius)**  | `radius-sm: 4px`, `radius-md: 8px`, `radius-lg: 12px`, `radius-full: 9999px`                                   |
| **Тени (shadows)**              | `shadow-sm`, `shadow-md`, `shadow-lg`                                                                          |
| **Размеры элементов (sizing)**  | Высоты контролов: `height-sm: 32px`, `height-md: 40px`, `height-lg: 48px`                                      |
| **Ширины**                      | `width-input-sm: 200px`, `width-input-md: 320px`, `max-width-content: 1200px`                                  |
| **Брейкпоинты**                 | `breakpoint-sm: 640px`, `breakpoint-md: 768px`, `breakpoint-lg: 1024px`, `breakpoint-xl: 1280px`               |
| **Z-index**                     | `z-dropdown: 100`, `z-modal: 200`, `z-tooltip: 300`                                                            |
| **Анимации**                    | `duration-fast: 150ms`, `duration-normal: 300ms`, `easing-default: ease-in-out`                                |
| **Иконки — размеры**            | `icon-sm: 16px`, `icon-md: 20px`, `icon-lg: 24px`                                                              |

### Принцип:
- Spacing и sizing строятся на **базовой единице** (обычно `4px` или `8px`).
- Высота всех интерактивных элементов (кнопки, инпуты, селекты, чипы) на одном уровне **должна совпадать** — чтобы элементы стояли в ряд без пляски.

---

## Этап 1 — Atoms (Атомы)

> Самые маленькие, неделимые элементы интерфейса. Каждый атом использует токены из Этапа 0.

### Примеры атомов:

- **Button** — кнопка (`primary`, `secondary`, `ghost`, `danger`)
- **Input** — текстовое поле
- **Label** — подпись
- **Icon** — иконка
- **Badge** — бейджик / счётчик
- **Avatar** — аватар пользователя
- **Checkbox** — чекбокс
- **Radio** — радиокнопка
- **Toggle / Switch** — переключатель
- **Tag / Chip** — тег
- **Divider** — разделитель
- **Spinner / Loader** — индикатор загрузки
- **Link** — текстовая ссылка
- **Tooltip** — всплывающая подсказка

### Правила:
- У каждого атома есть **варианты** (variants): `size: sm | md | lg`, `state: default | hover | focus | disabled | error`.
- Атомы **не содержат бизнес-логику** — они тупые и переиспользуемые.

---

## Этап 2 — Molecules (Молекулы)

> Группы атомов, объединённые для выполнения одной задачи.

### Примеры молекул:

- **Form Field** = `Label` + `Input` + `Helper Text / Error`
- **Search Bar** = `Input` + `Icon` (лупа) + `Button` (очистить)
- **Menu Item** = `Icon` + `Text` + `Badge`
- **Breadcrumb Item** = `Link` + `Divider`
- **Notification** = `Icon` + `Text` + `Close Button`
- **Input Group** = `Addon` + `Input` + `Button`
- **Tabs Item** = `Icon` + `Label` + `Badge`
- **Stepper Item** = `Number/Icon` + `Label` + `Connector Line`
- **Dropdown Trigger** = `Button` + `Chevron Icon`
- **Pagination Item** = `Button` (с номером) или `Ellipsis`

---

## Этап 3 — Organisms (Организмы)

> Сложные, самостоятельные блоки интерфейса, состоящие из молекул и атомов.

### Примеры организмов:

- **Header / Navbar** = `Logo` + `Nav Links` + `Search Bar` + `Avatar` + `Button`
- **Sidebar / Navigation** = список `Menu Item` + `Divider` + `Logo`
- **Form** = несколько `Form Field` + `Button` (submit)
- **Card** = `Image` + `Title` + `Description` + `Tags` + `Button`
- **Data Table** = `Table Header` + `Table Row` * N + `Pagination`
- **Modal / Dialog** = `Header` + `Content` + `Footer с кнопками`
- **Dropdown Menu** = `Dropdown Trigger` + список `Menu Item`
- **Tabs** = набор `Tabs Item` + `Content Panel`
- **Stepper** = набор `Stepper Item` в линию
- **Comment Block** = `Avatar` + `Name` + `Date` + `Text` + `Actions`
- **Breadcrumbs** = набор `Breadcrumb Item`

---

## Этап 4 — Templates (Шаблоны)

> Структура страницы **без реального контента** — скелет лейаута.

### Примеры шаблонов:

- **Dashboard Layout** = `Sidebar` + `Header` + `Content Area` (сетка карточек)
- **Settings Page** = `Sidebar Nav` + `Form Area`
- **Auth Layout** = центрированная `Card` на полноэкранном фоне
- **List Page** = `Header` + `Filters` + `Table` + `Pagination`
- **Detail Page** = `Breadcrumbs` + `Hero Section` + `Tabs` + `Content`

### Что определяется:
- **Grid / Layout system** — сколько колонок, отступы, max-width
- **Responsive поведение** — как шаблон адаптируется к брейкпоинтам
- **Зоны контента** — где шапка, где сайдбар, где main

---

## Этап 5 — Pages (Страницы)

> Конкретная реализация шаблона с **реальными данными**.

### Примеры:

- **Dashboard** = Dashboard Layout + реальные карточки с метриками
- **User Profile** = Detail Page + данные пользователя
- **Product List** = List Page + товары из каталога
- **Login** = Auth Layout + форма логина
- **404** = Auth Layout + сообщение об ошибке

---

## Визуальная схема иерархии

```
┌─────────────────────────────────────────────────┐
│                  PAGES (Страницы)                │
│    Реальный контент + данные                     │
├─────────────────────────────────────────────────┤
│               TEMPLATES (Шаблоны)                │
│    Лейауты, сетки, зоны                         │
├─────────────────────────────────────────────────┤
│              ORGANISMS (Организмы)               │
│    Header, Sidebar, Form, Card, Table            │
├─────────────────────────────────────────────────┤
│              MOLECULES (Молекулы)                 │
│    Form Field, Search Bar, Menu Item             │
├─────────────────────────────────────────────────┤
│                ATOMS (Атомы)                      │
│    Button, Input, Icon, Label, Badge             │
├─────────────────────────────────────────────────┤
│            DESIGN TOKENS (Токены)                 │
│    Цвета, шрифты, отступы, размеры, тени         │
└─────────────────────────────────────────────────┘
```

---

## Чек-лист: порядок работы

```
1. [ ] Определить Design Tokens
       ├── Цветовая палитра + семантические цвета
       ├── Типографика (шрифты, размеры, высоты строк, начертания)
       ├── Spacing шкала (4px grid)
       ├── Sizing (высоты контролов — одинаковые!)
       ├── Border-radius, shadows
       ├── Breakpoints, z-index, animation
       └── Задокументировать всё в одном месте

2. [ ] Создать Atoms
       ├── Button (все варианты и состояния)
       ├── Input, Checkbox, Radio, Toggle, Select
       ├── Typography компоненты (Heading, Text, Caption)
       ├── Icon set
       ├── Badge, Avatar, Tag, Divider, Spinner
       └── Проверить все состояния: default, hover, focus, active, disabled, error

3. [ ] Собрать Molecules
       ├── Form Field (label + input + error)
       ├── Search Bar, Menu Item, Notification
       └── Проверить, что молекулы переиспользуемые и не содержат лишней логики

4. [ ] Собрать Organisms
       ├── Header, Sidebar, Forms, Cards, Tables, Modals
       └── Проверить адаптивность каждого организма

5. [ ] Определить Templates
       ├── Основные лейауты (dashboard, auth, list, detail)
       ├── Grid system, breakpoints
       └── Документировать responsive-поведение

6. [ ] Собрать Pages
       └── Наполнить шаблоны реальными данными и проверить UX
```

---

## Ключевые принципы

| Принцип                          | Описание                                                                                             |
| -------------------------------- | ---------------------------------------------------------------------------------------------------- |
| **Consistency (единообразие)**   | Все кнопки одинаковой высоты, все отступы кратны 4px, одни и те же цвета для одних и тех же значений |
| **Single source of truth**       | Токены определяются один раз и используются везде; изменил токен — обновилось всё                    |
| **Composition over inheritance** | Сложное собирается из простого, а не наследуется                                                     |
| **States everywhere**            | Каждый интерактивный элемент имеет все состояния: default, hover, focus, active, disabled            |
| **Accessibility**                | Контраст текста ≥ 4.5:1, focus-кольца видимые, touch-target ≥ 44×44px                                |
| **8px grid**                     | Все размеры и отступы кратны 4 или 8 — это даёт визуальный ритм                                      |

---

## Источники и методология

- **Atomic Design** — Brad Frost ([atomicdesign.bradfrost.com](https://atomicdesign.bradfrost.com))
- **Design Tokens** — W3C Community Group, Style Dictionary
- **Material Design 3** — Google (пример зрелой дизайн-системы)
- **Human Interface Guidelines** — Apple
- **Carbon Design System** — IBM
