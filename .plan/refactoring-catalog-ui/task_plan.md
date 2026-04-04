# Task Plan: Рефакторинг catalog-ui — единые layout-компоненты и токены

## Goal
Извлечь из монолитных файлов каталога (CatalogApp.kt 1460 строк, second/page.tsx 677, foundation/page.tsx 530) переиспользуемые layout-компоненты с едиными отступами/размерами из токенов, устранить дублирование и хардкод, разбить по фичам.

## Current Phase
Phase 1

## Phases

### Phase 1: Извлечение shared-компонентов (React)
Вынести переиспользуемые компоненты из React-страниц в общие файлы.

- [ ] 1.1 Создать `app/components/catalog/Section.tsx` — вынести `Section` из foundation/page.tsx и second/page.tsx (идентичный код в обоих файлах)
- [ ] 1.2 Создать `app/components/catalog/SubSectionTitle.tsx` — единый `<h3>` стиль (заголовок подсекции), сейчас дублируется в каждом showcase
- [ ] 1.3 Создать `app/components/catalog/CatalogPage.tsx` — layout-обёртка: `minHeight: 100vh`, `backgroundColor`, transition, top bar, `<main>` с padding `xxl/xxxxl/xl`, `maxWidth: 960px` контейнер, title-слот
- [ ] 1.4 Создать `app/components/icons/index.tsx` — вынести все SVG-иконки (searchIcon, starIcon, checkIcon, closeIcon, plusIcon, settingsIcon, arrowLeftIcon, arrowRightIcon, chevronRightIcon, svgStyle, svgProps) из second/page.tsx
- [ ] 1.5 Обновить `foundation/page.tsx` — использовать CatalogPage, Section из shared, удалить дублированный код Section, layout-обёртку
- [ ] 1.6 Обновить `second/page.tsx` — использовать CatalogPage, Section, SubSectionTitle, icons из shared, удалить весь дублированный код
- [ ] 1.7 Обновить `page.tsx` (first page) — заменить хардкод `"24px"`, `"16px"`, `"8px"`, `"7.5rem"`, `"15rem"` на `toRem(tokens.spacing.*)` 
- [ ] 1.8 Проверить сборку React: `cd apps/catalog-ui/react && npm run build`
- **Status:** pending

### Phase 2: Разбиение showcase-секций (React)
Каждый showcase — отдельный файл-компонент.

- [ ] 2.1 Создать `app/foundation/showcases/TypographyShowcase.tsx` — вынести из foundation/page.tsx
- [ ] 2.2 Создать `app/foundation/showcases/ColorsShowcase.tsx`
- [ ] 2.3 Создать `app/foundation/showcases/SpacingShowcase.tsx`
- [ ] 2.4 Создать `app/foundation/showcases/SizingShowcase.tsx`
- [ ] 2.5 Создать `app/foundation/showcases/RadiusShowcase.tsx`
- [ ] 2.6 Создать `app/foundation/showcases/MotionShowcase.tsx`
- [ ] 2.7 Создать `app/foundation/showcases/BreakpointsShowcase.tsx`
- [ ] 2.8 Создать `app/second/showcases/TextShowcase.tsx` — вынести из second/page.tsx
- [ ] 2.9 Создать `app/second/showcases/ButtonShowcase.tsx`
- [ ] 2.10 Создать `app/second/showcases/IconButtonShowcase.tsx`
- [ ] 2.11 Создать `app/second/showcases/SurfaceShowcase.tsx`
- [ ] 2.12 Создать `app/second/showcases/SegmentedControlShowcase.tsx`
- [ ] 2.13 Создать `app/second/showcases/HeightAlignmentShowcase.tsx`
- [ ] 2.14 Обновить `foundation/page.tsx` — только импорты showcase + CatalogPage, ~20 строк
- [ ] 2.15 Обновить `second/page.tsx` — только импорты showcase + CatalogPage + state для size/radius, ~40 строк
- [ ] 2.16 Проверить сборку React: `npm run build`
- **Status:** pending

### Phase 3: Разбиение CatalogApp.kt (Compose)
Разбить монолит 1460 строк на модули.

- [ ] 3.1 Создать `catalog/CatalogPage.kt` — @Composable layout-обёртка: Column + scroll + top bar + content area + title + sections (параллельно CatalogPage.tsx)
- [ ] 3.2 Создать `catalog/ShowcaseSection.kt` — вынести ShowcaseSection из CatalogApp.kt
- [ ] 3.3 Создать `catalog/SubSectionTitle.kt` — единый @Composable для подзаголовков h3
- [ ] 3.4 Создать `icons/CatalogIcons.kt` — вынести все Icon-константы и @Composable иконки
- [ ] 3.5 Создать `catalog/ThemeSwitcherControl.kt` — вынести ThemeSwitcherControl + DirSwitcherControl
- [ ] 3.6 Создать `screens/FirstScreen.kt` — вынести из CatalogApp.kt
- [ ] 3.7 Создать `screens/FoundationScreen.kt` — вынести из CatalogApp.kt
- [ ] 3.8 Создать `screens/ComponentsScreen.kt` — вынести из CatalogApp.kt
- [ ] 3.9 Создать foundation showcases: `showcases/TypographyShowcase.kt`, `ColorsShowcase.kt`, `SpacingShowcase.kt`, `SizingShowcase.kt`, `RadiusShowcase.kt`, `MotionShowcase.kt`, `BreakpointsShowcase.kt`
- [ ] 3.10 Создать component showcases: `showcases/TextShowcase.kt`, `ButtonShowcase.kt`, `IconButtonShowcase.kt`, `SurfaceShowcase.kt`, `SegmentedControlShowcase.kt`, `HeightAlignmentShowcase.kt`
- [ ] 3.11 Упростить `CatalogApp.kt` — только навигация + theme provider, ~50 строк
- [ ] 3.12 Заменить хардкод на токены: `Spacer(Modifier.height(24.dp))` → `tokens.spacing.xl.dp`, `12.dp` → `tokens.spacing.md.dp`, `.padding(16.dp)` → `tokens.spacing.lg.dp`
- [ ] 3.13 Проверить сборку Compose: `./gradlew :apps:catalog-ui:compose:compileKotlinJvm`
- **Status:** pending

### Phase 4: Удаление мёртвого кода и финальная очистка
- [ ] 4.1 Убедиться что в foundation/page.tsx не осталось дублированных Section, layout-обёрток, showcase-компонентов
- [ ] 4.2 Убедиться что в second/page.tsx не осталось дублированных Section, layout-обёрток, SVG-иконок, showcase-компонентов
- [ ] 4.3 Убедиться что в CatalogApp.kt не осталось вынесенного кода (showcases, Section, icons, screens)
- [ ] 4.4 Проверить что нет неиспользуемых импортов во всех файлах
- [ ] 4.5 Git diff — review всех изменений
- **Status:** pending

### Phase 5: Верификация
- [ ] 5.1 `npm run build` для React каталога — без ошибок
- [ ] 5.2 `./gradlew :apps:catalog-ui:compose:compileKotlinJvm` — без ошибок
- [ ] 5.3 Визуальная проверка: запустить React `npm run dev`, проверить все 3 страницы
- [ ] 5.4 Визуальная проверка: запустить Compose `./gradlew :apps:catalog-ui:compose:run`, проверить все 3 экрана
- [ ] 5.5 Сравнить размеры файлов: до и после, убедиться что монолиты уменьшились
- **Status:** pending

## Constraints
- Визуально ничего не должно измениться — refactoring only
- Все отступы/размеры должны использовать `tokens.spacing.*` / `tokens.typography.*` — никакого хардкода
- React и Compose каталоги должны использовать идентичные значения токенов для layout
- Не трогать core/uikit — только apps/catalog-ui

## File Structure After Refactoring

### React
```
apps/catalog-ui/react/src/app/
├── layout.tsx                          (без изменений)
├── globals.css                         (без изменений)
├── page.tsx                            (~30 строк, токены вместо хардкода)
├── components/
│   ├── catalog/
│   │   ├── CatalogPage.tsx             (NEW: layout-обёртка + top bar + content)
│   │   ├── Section.tsx                 (NEW: showcase section title + divider)
│   │   └── SubSectionTitle.tsx         (NEW: h3 подзаголовок)
│   ├── icons/
│   │   └── index.tsx                   (NEW: все SVG-иконки)
│   └── theme-switcher/
│       └── ThemeSwitcher.tsx           (без изменений)
├── foundation/
│   ├── page.tsx                        (~20 строк: CatalogPage + showcase импорты)
│   └── showcases/
│       ├── TypographyShowcase.tsx      (NEW)
│       ├── ColorsShowcase.tsx          (NEW)
│       ├── SpacingShowcase.tsx         (NEW)
│       ├── SizingShowcase.tsx          (NEW)
│       ├── RadiusShowcase.tsx          (NEW)
│       ├── MotionShowcase.tsx          (NEW)
│       └── BreakpointsShowcase.tsx     (NEW)
└── second/
    ├── page.tsx                        (~40 строк: CatalogPage + state + showcase импорты)
    └── showcases/
        ├── TextShowcase.tsx            (NEW)
        ├── ButtonShowcase.tsx          (NEW)
        ├── IconButtonShowcase.tsx      (NEW)
        ├── SurfaceShowcase.tsx         (NEW)
        ├── SegmentedControlShowcase.tsx(NEW)
        └── HeightAlignmentShowcase.tsx (NEW)
```

### Compose
```
apps/catalog-ui/compose/src/commonMain/kotlin/
├── CatalogApp.kt                      (~50 строк: навигация + theme)
├── Main.kt                            (без изменений, jvmMain)
├── catalog/
│   ├── CatalogPage.kt                 (NEW: layout-обёртка)
│   ├── ShowcaseSection.kt             (NEW)
│   ├── SubSectionTitle.kt             (NEW)
│   └── ThemeSwitcherControl.kt        (NEW: theme + dir switchers)
├── icons/
│   └── CatalogIcons.kt                (NEW: все иконки)
├── screens/
│   ├── FirstScreen.kt                 (NEW)
│   ├── FoundationScreen.kt            (NEW)
│   └── ComponentsScreen.kt            (NEW)
└── showcases/
    ├── TypographyShowcase.kt          (NEW)
    ├── ColorsShowcase.kt              (NEW)
    ├── SpacingShowcase.kt             (NEW)
    ├── SizingShowcase.kt              (NEW)
    ├── RadiusShowcase.kt              (NEW)
    ├── MotionShowcase.kt              (NEW)
    ├── BreakpointsShowcase.kt         (NEW)
    ├── TextShowcase.kt                (NEW)
    ├── ButtonShowcase.kt              (NEW)
    ├── IconButtonShowcase.kt          (NEW)
    ├── SurfaceShowcase.kt             (NEW)
    ├── SegmentedControlShowcase.kt    (NEW)
    └── HeightAlignmentShowcase.kt     (NEW)
```

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
|       |         |            |
