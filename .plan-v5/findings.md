# Findings — .plan-v5

## Исследование 1: JS Bundle Sync
- `apps/catalog-ui/react/node_modules/uikit-common` → symlink → `core/uikit/common/build/dist/js/productionLibrary/`
- Оба бандла (`build/js/packages/` и `build/dist/js/productionLibrary/`) **идентичны** (md5 совпадает)
- Оба содержат 100 совпадений по `iconId|iconPosition|iconSize|iconGap`
- **Вывод**: JS bundle актуален, проблема НЕ в sync'е

## Исследование 2: KMP JS конструкторы
- `SegmentedControlConfig(options, selectedId, size, variant, iconPosition, id, testTag, visibility)` — 8 args
- `VOID === undefined` in KMP JS → пропущенные аргументы корректно получают дефолты
- `SegmentedControlOption(id, label, iconId)` — свойства доступны напрямую `.id`, `.label`, `.iconId`
- **Вывод**: Конструкторы работают корректно

## Исследование 3: Variant resolver (JS)
- `resolveColors_0` использует `variant.v_1` (ordinal) для switch/case
- Все 5 вариантов (0-4) обрабатываются: Solid, Soft, Surface, Outline, Ghost
- Результат: объект `SegmentedControlColors(trackBg, thumbBg, textActive, textInactive, border)`
- **Вывод**: Resolver работает, но цвета слишком похожи

## Исследование 4: Ghost Surface
- `SurfaceStyleResolver`: Ghost → `bg="transparent"`, `border="transparent"`, `bgHover=surfaceHover`
- С `hoverable=true` hover-эффект есть, но REST состояние полностью прозрачное
- В showcase текст внутри виден (textPrimary), но граница карточки невидима
- **Вывод**: Дизайн правильный, showcase нуждается в индикаторе

## Исследование 5: Object Spread с KMP
- `{...baseTokens}` в `page.tsx` для radius override
- KMP JS classes используют прямые свойства (`this.xxx = value`), НЕ prototype chain
- Однако `{...obj}` копирует только own enumerable properties
- KMP JS также создаёт proto функции (`.ue()`, `.ve()` etc.) — они НЕ копируются spread'ом
- `tokens.controls.proportions` вложенный объект — spread работает на первом уровне
- Код использует вложенный spread: `{...baseTokens, controls: {...baseTokens.controls, proportions: {...baseTokens.controls.proportions, radiusFraction: fraction}}}`
- **Потенциальная проблема**: если `baseTokens.controls` — KMP объект, то `{...baseTokens.controls}` может потерять методы
- Но resolve() обращается к `.proportions.radiusFraction` — это данные, не методы
- **Вывод**: Spread должен работать для data properties, но может сломать method calls

## Цветовая палитра после v4 (neutral monochromatic)

### Light theme
| Token | Hex | Назначение |
|-------|-----|-----------|
| surface | #FFFFFF | Основной фон |
| surfaceHover | #D6D6D6 | Hover-эффект |
| surfaceContainerHigh | #EAEAEA | Elevated container |
| neutralSoft | #F5F5F5 | Subtle neutral bg |
| borderSubtle | #E5E5E5 | Subtle border |
| outlineVariant | #C4C4C4 | Stronger outline |

### Dark theme
| Token | Hex | Назначение |
|-------|-----|-----------|
| surface | #0C0C0E | Основной фон |
| surfaceHover | #333333 | Hover-эффект |
| surfaceContainerHigh | #2B2B2B | Elevated container |
| neutralSoft | #1C1C1C | Subtle neutral bg |
| borderSubtle | #333333 | Subtle border |
| outlineVariant | #444444 | Stronger outline |
