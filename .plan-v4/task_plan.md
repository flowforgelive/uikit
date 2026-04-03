# Task Plan: UIKit v4 — Цветовая палитра, иконки в SegmentedControl, глобальные контролы

## Goal

Исправить 4 группы проблем обнаруженных после v3:
1. **Цветовая палитра**: убрать "выжигающий глаза" индиго-синий из soft/border токенов, перейти на нейтральную монохромную палитру (вдохновлённую x.ai)
2. **Ghost Surface**: ghost вариант Surface не виден визуально (transparent bg + transparent border + white bg = невидимый)
3. **Иконки в SegmentedControl**: добавить поддержку иконок с позиционированием (top/start/end/bottom)
4. **Глобальные контролы каталога**: перенести Size переключатель в top bar (убрать дублирование), добавить глобальный переключатель border-radius

## Current Phase

Phase 1

## Scope

> Исправляем: ColorTokens, DesignTokens, SurfaceStyleResolver, SegmentedControlConfig/StyleResolver/View (common+react+compose), catalog-ui showcase page

## Phases

### Phase 1: Цветовая палитра — нейтральная монохромная (x.ai-inspired)
- [ ] 1.1 Заменить индиго-тонированные `primarySoft/primarySoftHover` на нейтральные серые в Light
- [ ] 1.2 Заменить индиго `primaryBorder/primaryBorderHover` на нейтральные серые в Light
- [ ] 1.3 Обновить Dark theme: surface с `#000000` → warm near-black `#0C0C0E`
- [ ] 1.4 Обновить Dark theme primarySoft/primaryBorder → нейтральные, убрать индиго (#1E1B4B → neutral)
- [ ] 1.5 Обновить ButtonStyleResolver — проверить что surfaceColors/softColors используют нейтральные токены
- [ ] 1.6 Билд common (JS + JVM)
- **Status:** pending
- **Files:**
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/tokens/DesignTokens.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/button/ButtonStyleResolver.kt` (проверка)

### Phase 2: Ghost Surface — визуальная различимость
- [ ] 2.1 Showcase: ghost surface рендерить с `hoverable={true}` (чтобы показать hover bg)
- [ ] 2.2 Опционально: добавить dashed border для ghost surfaces в showcase для наглядности
- [ ] 2.3 Проверить что Ghost Surface clickable/hoverable работает корректно
- **Status:** pending
- **Files:**
  - `apps/catalog-ui/react/src/app/second/page.tsx`

### Phase 3: SegmentedControl — поддержка иконок (common/)
- [ ] 3.1 Расширить `SegmentedControlOption` — добавить `iconId: String?` поле
- [ ] 3.2 Расширить `SegmentedControlConfig` — добавить `iconPosition: IconPosition = IconPosition.None`
- [ ] 3.3 Обновить `SegmentedControlSizes` — добавить `iconSize: Double`, `iconGap: Double`
- [ ] 3.4 Обновить `SegmentedControlStyleResolver.resolve()` — вычислять iconSize/iconGap из scale
- [ ] 3.5 Обновить equals/hashCode для Config/Option
- [ ] 3.6 Билд common
- **Status:** pending
- **Files:**
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlConfig.kt`
  - `core/uikit/common/src/commonMain/kotlin/com/uikit/components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`

### Phase 4: SegmentedControl — React рендерер с иконками
- [ ] 4.1 SegmentedControl.tsx: добавить `iconPosition` prop + обновить options interface (ReactNode icon)
- [ ] 4.2 SegmentedControlView.tsx: рендерить icon+label в option (flex layout зависит от iconPosition)
- [ ] 4.3 SegmentedControlView.module.css: стили для icon slot (.optionIcon, .optionContent, flexDirection)
- [ ] 4.4 TypeScript проверка
- **Status:** pending
- **Files:**
  - `core/uikit/react/src/components/atoms/segmented-control/SegmentedControl.tsx`
  - `core/uikit/react/src/components/atoms/segmented-control/SegmentedControlView.tsx`
  - `core/uikit/react/src/components/atoms/segmented-control/SegmentedControlView.module.css`

### Phase 5: SegmentedControl — Compose рендерер с иконками
- [ ] 5.1 SegmentedControl.kt: добавить `iconPosition` параметр + icons list
- [ ] 5.2 SegmentedControlView.kt: рендерить icon+label в option (Row/Column зависит от iconPosition)
- [ ] 5.3 Билд compose
- **Status:** pending
- **Files:**
  - `core/uikit/compose/src/commonMain/kotlin/com/uikit/compose/components/atoms/segmentedcontrol/SegmentedControl.kt`
  - `core/uikit/compose/src/commonMain/kotlin/com/uikit/compose/components/atoms/segmentedcontrol/SegmentedControlView.kt`

### Phase 6: Глобальные контролы каталога — Size + Border Radius в top bar
- [ ] 6.1 Создать React state в top-level: `globalSize`, `globalRadius`
- [ ] 6.2 Добавить SegmentedControl для Size (XS/SM/MD/LG/XL) в top bar
- [ ] 6.3 Добавить SegmentedControl для Border Radius (None/SM/MD/LG/XL/Full) в top bar
- [ ] 6.4 Прокинуть globalSize/globalRadius во все showcase-компоненты как props
- [ ] 6.5 Удалить дублирующиеся Size переключатели из отдельных showcase
- [ ] 6.6 Визуальная проверка
- **Status:** pending
- **Files:**
  - `apps/catalog-ui/react/src/app/second/page.tsx`

### Phase 7: Showcase — иконки в SegmentedControl + финальная проверка
- [ ] 7.1 Добавить showcase секцию иконок: SegmentedControl с icon+label в разных позициях
- [ ] 7.2 Полный билд: common (JS+JVM) + compose (JVM)
- [ ] 7.3 JS bundle генерация
- [ ] 7.4 TypeScript type check
- **Status:** pending
- **Files:**
  - `apps/catalog-ui/react/src/app/second/page.tsx`

## Key Questions

1. **Нужен ли primarySoft/primaryBorder токен если primary = нейтральный (#1A1A1A)?** → Да, оставляем tokens, но values меняем на более тёплые серые (x.ai-inspired)
2. **Как отличить Soft Primary от Soft Neutral если оба нейтральные?** → Soft Primary чуть темнее / контрастнее Neutral. В x.ai подходе разница минимальная — accent через weight/size, не через цвет
3. **Нужны ли icon slots в SegmentedControlConfig?** → В Config передаём `iconId: String?` (KMP). В React/Compose View — функция маппинга iconId → ReactNode/Composable
4. **Какой подход к border-radius?** → Глобальный множитель к tokens.radius, или список фикс значений для showcase

## Decisions Made

| # | Decision | Rationale |
|---|----------|-----------|
| 1 | Убираем индиго (Indigo-50..300) из цветовых токенов | primary = #1A1A1A = чёрный. Soft/border для чёрного primary не должен быть индиго-синим |
| 2 | Dark theme surface: #000000 → #0C0C0E | x.ai: #1f2228. Чистый чёрный слишком harsh. Warm near-black лучше для глаз |
| 3 | SegmentedControl icon через iconId в Config + render prop в View | KMP Config — @Serializable, ReactNode нельзя. iconId: String? + маппинг в рендерере |
| 4 | Global Size/Radius в showcase через React state + props | Не меняем KMP-код для этого. Это UI каталога, не библиотека |
| 5 | Ghost Surface в showcase — hoverable по дефолту | Ghost = invisible by design. В showcase показываем с hoverable=true |

## Errors Encountered

| Error | Attempt | Resolution |
|-------|---------|------------|
| (пока нет) | | |

## File Inventory — все файлы для изменения

### Common (KMP) — 3 файла
| Файл | Изменения |
|------|-----------|
| `tokens/DesignTokens.kt` | Заменить 7 индиго цветов на neutral в Light+Dark |
| `components/atoms/segmentedcontrol/SegmentedControlConfig.kt` | +iconId в Option, +iconPosition в Config |
| `components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt` | +iconSize, +iconGap в Sizes |

### React — 3 файла
| Файл | Изменения |
|------|-----------|
| `atoms/segmented-control/SegmentedControl.tsx` | +icon prop mapping, +iconPosition |
| `atoms/segmented-control/SegmentedControlView.tsx` | Рендер icon+label в option |
| `atoms/segmented-control/SegmentedControlView.module.css` | +icon styles, flex layout |

### Compose — 2 файла
| Файл | Изменения |
|------|-----------|
| `atoms/segmentedcontrol/SegmentedControl.kt` | +iconPosition param, +icons |
| `atoms/segmentedcontrol/SegmentedControlView.kt` | Рендер icon+label в option |

### Showcase — 1 файл
| Файл | Изменения |
|------|-----------|
| `apps/catalog-ui/react/src/app/second/page.tsx` | Global controls, icon showcase, ghost fix |

**Итого:** ~9 файлов, 7 фаз
