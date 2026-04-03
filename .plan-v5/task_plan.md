# Plan v5 — Исправление видимости: Ghost Surface, SC варианты, SC иконки

## Проблемы пользователя
1. **Ghost Surface не виден** — `bg=transparent`, `border=transparent` → карточка невидима в покое
2. **Варианты SegmentedControl не различаются** — neutral palette (#F5F5F5 vs #EAEAEA vs #D6D6D6) слишком близки
3. **Иконки SegmentedControl не видны** — код верный, но runtime не обновлён

## Корневые причины (выявлено исследованием)
- **Весь исходный код верный** (подтверждено sub-agent проверкой всех 12 файлов)
- **JS бандл содержит все изменения** (100 совпадений по icon-полям, файлы идентичны)
- **Проблема 1**: Ghost Surface по дизайну невидим (transparent bg+border). Нужен индикатор в showcase.
- **Проблема 2**: Soft SC использует `surfaceHover=#D6D6D6` (темнее Solid=#EAEAEA) — семантически неправильно.
- **Проблема 3**: `{...baseTokens}` spread для radius override может НЕ РАБОТАТЬ с KMP JS объектами (свойства в prototype chain, а не enumerable own).
- **Проблема 4**: Dev-сервер не перезапущен после v4 изменений.

## Фазы

### Phase 1: Rebuild & Verify Runtime `status: complete`
- Полный rebuild: `./gradlew :core:uikit:common:jsBrowserProductionLibraryDistribution`
- Запуск dev-сервера и визуальная проверка
- Подтверждение: showcase загружается без ошибок

### Phase 2: Fix KMP Object Spread `status: complete` (не требуется — spread корректен)
- Проверить `{...baseTokens}` spread правильность
- Если KMP объекты не распаковываются — использовать deep clone или прямое присвоение
- Файл: `apps/catalog-ui/react/src/app/second/page.tsx`

### Phase 3: Ghost Surface Showcase UX `status: complete`
- Ghost Surface по дизайну прозрачный → добавить в showcase визуальный индикатор
- Вариант: dashed border background container или подпись "hover to see"
- Файл: `apps/catalog-ui/react/src/app/second/page.tsx` (SurfaceShowcase)

### Phase 4: SegmentedControl Variant Colors `status: complete`
- Soft: `surfaceHover=#D6D6D6` слишком тёмный для "мягкого" фона
- Нужно: Soft trackBg = более светлый цвет (neutralSoft? primarySoft?)
- Проверить контрастность всех 5 вариантов
- Файл: `core/uikit/common/.../SegmentedControlStyleResolver.kt`

### Phase 5: Verify Icons End-to-End `status: complete`
- Запустить dev-сервер, проверить рендеринг иконок в SC
- Если не работают — диагностировать конкретную причину
- Файлы: SegmentedControl.tsx, SegmentedControlView.tsx

### Phase 6: Final Build & Verification `status: complete`
- Полный build: common JS + JVM, compose JVM
- TypeScript check
- Визуальная проверка всех 3 секций showcase

## Карта цветов SC вариантов (light theme)

| Variant | Track BG | Hex | Thumb BG | Border |
|---------|----------|-----|----------|--------|
| Surface | neutralSoft | #F5F5F5 | surface #FFF | borderSubtle #E5E5E5 |
| Soft | surfaceHover | #D6D6D6 ⚠️ | surface #FFF | transparent |
| Outline | transparent | — | surface #FFF | outlineVariant #C4C4C4 |
| Solid | surfaceContainerHigh | #EAEAEA | surface #FFF | transparent |
| Ghost | transparent | — | surfaceHover #D6D6D6 | transparent |

⚠️ Soft (#D6D6D6) ТЕМНЕЕ Solid (#EAEAEA) — семантически неправильно!

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
| (пусто — заполняется по ходу) | | |
