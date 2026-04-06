# Рефакторинг: Вложенные цвета + Icon → Button → Chip + удаление IconButton

**Цель:** Решить проблему цветовых конфликтов при вложенности интерактивных компонентов, создать Icon primitive, интегрировать его в Button, создать Chip composite, и объединить IconButton в Button как icon-only mode.

**Принципы:**
- Не нарушать `@JsExport @Serializable` контракт (KMP/JS совместимость)
- SSR-безопасность: stateless resolvers, pure functions, детерминизм
- Каждая фаза компилируется и работает отдельно
- Обратная совместимость React convenience API где возможно (deprecation → removal)
- Все числовые значения — `Double`, все цвета — `String` (hex)

**Ограничения KMP/JS:**
- `@JsExport data class` не поддерживает интерфейсы
- Extension functions не экспортируются в JS
- `modifier: Modifier` всегда последний параметр в Compose

---

## Фазы

### Phase 1: nestingDepth в SurfaceContext + NestingColorStrategy [COMPLETE]
**Решает:** цветовые конфликты при вложенности Surface → Chip → IconButton (3+ уровня)
**Критичность:** 🔴 Высокая — фундамент для всех вложенных интерактивных компонентов

**Проблема:**
- Текущий `InteractiveColorResolver.softColors()` проверяет только 1 уровень (`if surfaceBg == primarySoft`)
- При 3+ уровнях вложенности (Surface → Chip → Button) цвета сливаются
- Hover 3-х уровней одновременно стекается визуально

**Решение: Tonal Staircase (из NESTED_INTERACTIVE_COLORS_GUIDE.md, Стратегия E Hybrid)**

**Что делать:**
1. Расширить `SurfaceContext` — добавить `nestingDepth: Int = 0`
2. Создать `foundation/NestingColorStrategy.kt` — `@JsExport object` с тональной лестницей
   - `resolveSoftBg(intent, nestingDepth, tokens) → Pair<bg, bgHover>`
   - Для каждого intent — массив шагов: `[primarySoft, primarySoftHover, surfaceContainerHigh, surfaceContainerHighest]`
   - depth 0: bg=step[0], hover=step[1]; depth 1: bg=step[1], hover=step[2]; и т.д.
3. Обновить `InteractiveColorResolver.softColors()` — использовать `NestingColorStrategy` вместо одиночного `if`
4. Обновить `InteractiveColorResolver.ghostColors()` — Ghost на глубине 2+ использует surfaceContext.backgroundColor как визуальный bg, hover = blend
5. Каждый интерактивный компонент при рендеринге инкрементирует `nestingDepth` в контексте для дочерних элементов

**Файлы:**
- ИЗМЕНИТЬ: `core/uikit/common/.../foundation/SurfaceContext.kt` — добавить `nestingDepth: Int = 0`
- СОЗДАТЬ: `core/uikit/common/.../foundation/NestingColorStrategy.kt`
- ИЗМЕНИТЬ: `core/uikit/common/.../foundation/InteractiveColorResolver.kt` — softColors + ghostColors
- ИЗМЕНИТЬ: `core/uikit/react/src/theme/SurfaceContext.tsx` — пробросить nestingDepth
- ИЗМЕНИТЬ: `core/uikit/compose/.../theme/LocalSurfaceContext` — пробросить nestingDepth

**Тесты:** soft colors на depth 0/1/2 возвращают разные bg; ghost hover на depth 2 отличается от depth 0.

---

### Phase 2: Anti-stacking hover'ов (CSS `:has()` + Compose) [COMPLETE]
**Решает:** при наведении на дочерний интерактивный элемент родитель НЕ должен показывать свой hover
**Критичность:** 🟡 Средняя — визуальный polish, можно делать параллельно с Phase 3+
**Зависимость:** Phase 1 (nestingDepth должен быть в контексте)

**Решение:**

**React (CSS `:has()`):**
- Добавить `data-interactive` атрибут на все интерактивные компоненты (Button, Chip, etc.)
- В CSS hover-правилах: `.button:hover:not(:has([data-interactive]:hover)):not([aria-disabled="true"])`
- Это отключает hover родителя, когда дочерний интерактивный элемент hovered

**Compose:**
- `LocalParentHoverCallback` — CompositionLocal для сигнализации child hover → parent
- Или: interceptPointerInput — parent не показывает hover, если pointer в зоне child
- Рекомендация: начать с простого — добавить `isChildHovered` state через CompositionLocal

**Файлы:**
- ИЗМЕНИТЬ: `ButtonView.module.css` — hover selector с `:has()`
- ИЗМЕНИТЬ: `ButtonView.tsx` — добавить `data-interactive` атрибут
- СОЗДАТЬ (позже): `ChipView.module.css` — аналогичный hover selector
- ИЗМЕНИТЬ: Compose `ButtonView.kt` — `LocalParentHoverCallback` или pointer interception

**Тесты:** визуальная проверка — при hover на вложенный Button внутри Chip, Chip не меняет bg.

---

### Phase 3: Icon Primitive [COMPLETE]
**Решает:** отсутствие собственного Icon-примитива; иконки сейчас это просто ReactNode / @Composable слоты
**Критичность:** 🔴 Высокая — используется в Button, Chip, будущих компонентах
**Зависимость:** нет (независимая от Phase 1-2)

**Что такое Icon primitive:**
- Отображает иконку заданного размера с цветом, наследуемым от parent
- Config содержит: `name: String` (идентификатор иконки), `size`, `color` (опционально), `testTag`
- НЕ содержит: интерактивность, hover, click — это задача parent-компонента
- Рендеринг: React — `<svg>` или slot для иконки; Compose — кастомный Icon Box

**Архитектура:**

```
components/primitives/icon/
├── IconConfig.kt           ← name, size, color?, ariaHidden, testTag
└── IconStyleResolver.kt    ← resolve(config, tokens) → ResolvedIconStyle (size, color)
```

**Размеры иконки:**
- Привязаны к `ComponentSize` через `ControlSizeScale.iconSize`
- Или явный `customSize: Double?` для произвольного размера
- Цвет: по умолчанию "inherit" (наследуется от parent через CSS color / LocalContentColor)

**Что делать:**
1. Создать `IconConfig.kt` — data class: `name`, `size` (ComponentSize), `customSize?`, `color?` (String?), `ariaHidden: Boolean = true`, `testTag`
2. Создать `IconStyleResolver.kt` — object: `resolve(config, tokens) → ResolvedIconStyle`
   - `ResolvedIconStyle(size: Double, color: String?)` — size в dp, color = null означает inherit
3. React: `Icon.tsx` (convenience) + `IconView.tsx` + `IconView.module.css`
   - Icon принимает `children: ReactNode` (svg-элемент) или `name: string` (для будущего icon registry)
   - IconView: `<span>` обёртка с контролем размера, цвета через currentColor
4. Compose: `Icon.kt` (convenience) + `IconView.kt`
   - Icon принимает `painter: Painter` или `content: @Composable () -> Unit`
   - IconView: `Box(Modifier.size(style.size.dp))` с `LocalContentColor`
5. Добавить экспорты в `react/src/index.ts`

**Файлы:**
- СОЗДАТЬ: `core/uikit/common/.../components/primitives/icon/IconConfig.kt`
- СОЗДАТЬ: `core/uikit/common/.../components/primitives/icon/IconStyleResolver.kt`
- СОЗДАТЬ: `core/uikit/react/src/components/primitives/icon/Icon.tsx`
- СОЗДАТЬ: `core/uikit/react/src/components/primitives/icon/IconView.tsx`
- СОЗДАТЬ: `core/uikit/react/src/components/primitives/icon/IconView.module.css`
- СОЗДАТЬ: `core/uikit/compose/.../components/primitives/icon/Icon.kt`
- СОЗДАТЬ: `core/uikit/compose/.../components/primitives/icon/IconView.kt`
- ИЗМЕНИТЬ: `core/uikit/react/src/index.ts` — добавить Icon exports

---

### Phase 4: Интеграция Icon в Button [NOT STARTED]
**Решает:** Button использует raw ReactNode/Composable для иконок вместо Icon primitive
**Критичность:** 🟡 Средняя — предпосылка для Chip; улучшает consistency
**Зависимость:** Phase 3 (Icon должен существовать)

**Текущее состояние Button:**
- `iconStart?: React.ReactNode` / `iconEnd?: React.ReactNode` — произвольные React элементы
- `iconStart: (@Composable () -> Unit)?` / `iconEnd: (@Composable () -> Unit)?` — произвольные composables
- Размер иконки контролируется через `.icon` обёртку в CSS / `Box(Modifier.size(iconSize))`

**Подход: сохранить slot-based API, но рекомендовать Icon**
- Не ломаем API: `iconStart`/`iconEnd` по-прежнему принимают ReactNode/Composable
- В convenience API `Button.tsx` / `Button.kt` — iconStart/iconEnd typed как `ReactNode` / `@Composable`
- Внутренне: обёртка `.icon` уже контролирует размер — Icon primitive будет корректно наследовать цвет и ограничиваться размером
- Документация/catalog: показывать использование с `<Icon>`, не с голыми svg

**Что делать:**
1. Обновить catalog-ui: demo Button с `<Icon>` в iconStart/iconEnd
2. Убедиться, что `Icon` корректно наследует `color: inherit` внутри `.icon` обёртки Button
3. Опционально: добавить в ButtonConfig `iconName?: String` для BDUI (серверная иконка по имени)

**Файлы:**
- ИЗМЕНИТЬ: `apps/catalog-ui/react/src/app/second/page.tsx` — demo с Icon в Button
- ИЗМЕНИТЬ: `apps/catalog-ui/compose/...` — demo с Icon в Button
- (ОПЦИОНАЛЬНО) ИЗМЕНИТЬ: `ButtonConfig.kt` — добавить `iconName: String? = null` для BDUI

---

### Phase 5: Объединение IconButton → Button (icon-only mode) [COMPLETE]
**Решает:** IconButton — это дублирование Button с отдельным Config, Resolver, View для кейса "только иконка"
**Критичность:** 🔴 Высокая — устраняет дублирование, упрощает API
**Зависимость:** Phase 3 (Icon), Phase 4 (Icon в Button)

**Анализ текущего IconButton:**
- `IconButtonConfig`: `variant, intent, size, disabled, loading, id, actionRoute, testTag, visibility, ariaLabel`
- `IconButtonStyleResolver`: использует `InteractiveColorResolver` + `resolveSize` → `IconButtonSizeSet(size=height, iconSize, radius)`
- `IconButtonView`: квадратный `width=height=size`, иконка по центру, нулевой padding
- Ключевое отличие от Button: **нет text, квадратная форма, нет padding-inline**

**Решение: Button с `text = ""` → icon-only mode**

Когда `text` пустой и есть иконка → Button автоматически переключается в icon-only mode:
- **Ширина = высота** (квадрат)
- **padding-inline не от текста**, а рассчитывается для круглой формы
- **Расчёт padding для круглой иконки:**
  ```
  // Чтобы иконка вписалась в круг (radius ≥ height/2):
  // iconOnly padding = (height - iconSize) / 2
  // Это гарантирует что при radius=height/2 (полный круг) иконка не обрезается
  iconOnlyPadding = (scale.height - scale.iconSize) / 2
  ```
- **radius**: по умолчанию берётся из ControlSizeScale.radius, но МОЖЕТ быть переопределён через конфиг для полностью круглой формы (`radius = height / 2`)

**Что делать:**

1. **ButtonConfig.kt:**
   - Сделать `text: String = ""` (опциональный)
   - Добавить `ariaLabel: String? = null` (для icon-only кнопок, accessibility)
   - Добавить computed: `val isIconOnly: Boolean get() = text.isEmpty() && (hasIconStart || hasIconEnd)`

2. **ButtonStyleResolver.kt:**
   - Добавить `iconOnlyPadding` в `SizeSet`: `val iconOnlyPadding: Double = 0.0`
   - В `resolve()`: если `config.isIconOnly` → `paddingH = (height - iconSize) / 2`, убрать paddingV
   - Высота остаётся как есть (из scale.height), ширина = высота (квадрат) — контролируется во View

3. **React ButtonView.tsx:**
   - Если `config.isIconOnly` → добавить CSS класс `.iconOnly`
   - `.iconOnly`: `width: var(--btn-height)`, `padding: 0`, `--btn-padding-h: var(--btn-icon-only-padding)`

4. **React ButtonView.module.css:**
   - `.button.iconOnly` — квадрат, переопределение padding

5. **Compose ButtonView.kt:**
   - Если `config.isIconOnly` → `.size(height.dp)` вместо `.defaultMinSize(minHeight = height.dp)`
   - Padding: `iconOnlyPadding` вместо `paddingH`

6. **Deprecation IconButton:**
   - Пометить convenience `IconButton.tsx`, `IconButton.kt` как deprecated (ссылка на Button с icon-only)
   - Convenience wrappers: `IconButton` → `Button(text="", iconEnd=icon, ariaLabel=label)`
   - В следующем major: удалить файлы

7. **React convenience Button.tsx:**
   - `text` становится optional: `text?: string` (default `""`)
   - `ariaLabel?: string` — новый prop

**Файлы:**
- ИЗМЕНИТЬ: `core/uikit/common/.../button/ButtonConfig.kt` — text optional, ariaLabel, isIconOnly
- ИЗМЕНИТЬ: `core/uikit/common/.../button/ButtonStyleResolver.kt` — iconOnlyPadding, квадратный режим
- ИЗМЕНИТЬ: `core/uikit/react/.../button/ButtonView.tsx` — iconOnly mode
- ИЗМЕНИТЬ: `core/uikit/react/.../button/ButtonView.module.css` — .iconOnly styles
- ИЗМЕНИТЬ: `core/uikit/react/.../button/Button.tsx` — text optional, ariaLabel
- ИЗМЕНИТЬ: `core/uikit/compose/.../button/Button.kt` — text optional, ariaLabel
- ИЗМЕНИТЬ: `core/uikit/compose/.../button/ButtonView.kt` — iconOnly mode
- ИЗМЕНИТЬ: `core/uikit/react/.../icon-button/IconButton.tsx` — deprecation wrapper
- ИЗМЕНИТЬ: `core/uikit/compose/.../iconbutton/IconButton.kt` — deprecation wrapper

**Тесты:**
- `ButtonStyleResolver` с `isIconOnly=true` возвращает `paddingH = (height - iconSize) / 2`
- `ButtonStyleResolver` с `isIconOnly=true` и `radius = height/2` — иконка вписывается в круг
- Существующие Button тесты проходят без изменений (text ≠ "")

---

### Phase 6: Chip Composite [COMPLETE]
**Решает:** отсутствие Chip-компонента (9-й composite из ROADMAP)
**Критичность:** 🟡 Средняя — новый компонент, не блокирует другие
**Зависимость:** Phase 1 (nestingDepth), Phase 3 (Icon), Phase 5 (Button icon-only для close action)

**Chip = Surface + Text + Icon (lead) + close action (Button icon-only)**

Варианты Chip из ROADMAP: `Selectable, Dismissible, Info`

**Архитектура:**

```
components/composites/chip/
├── ChipConfig.kt           ← text, variant, intent, size, leadingIcon?, dismissible, selected, disabled
└── ChipStyleResolver.kt    ← resolve(config, tokens, surfaceContext?) → ResolvedChipStyle
```

**ChipConfig fields:**
- `text: String` — текст чипа
- `variant: VisualVariant = VisualVariant.Soft` — по умолчанию Soft (follow Step Down rule)
- `intent: ColorIntent = ColorIntent.Neutral` — по умолчанию Neutral
- `size: ComponentSize = ComponentSize.Md`
- `hasLeadingIcon: Boolean = false` — есть ли иконка слева
- `dismissible: Boolean = false` — показывать ли кнопку закрытия (×)
- `selected: Boolean = false` — состояние выбора (для Selectable)
- `disabled: Boolean = false`
- `id, testTag, visibility` — стандартные

**ResolvedChipStyle:**
- `colors: ColorSet` — bg, bgHover, text и т.д.
- `sizes: ChipSizes` — height, paddingH, fontSize, fontWeight, iconSize, iconGap, closeButtonSize, radius
- `selectedColors: ColorSet?` — отдельные цвета для selected state (Soft → Solid shift)

**Step Down правило для вложенности:**
- Chip по умолчанию `Soft` → дочерний close button будет `Ghost`
- Если Chip на Surface(Solid) → корректный контраст через nestingDepth

**Что делать:**
1. `ChipConfig.kt` — data class
2. `ChipStyleResolver.kt` — object, использует `InteractiveColorResolver` + `ComponentSizeResolver`
   - Chip sizes: height меньше чем button (0.8×), paddingH меньше, fontSize меньше
   - Или: отдельные `ControlProportions` для Chip в `InteractiveControlTokens`
   - Простое решение: использовать те же `ComponentSize`, но с `chipHeightRatio = 2.0` вместо `2.5`
3. React: `Chip.tsx` + `ChipView.tsx` + `ChipView.module.css`
   - `leadingIcon?: ReactNode`, `onDismiss?: () => void`
   - Close button: inline `<button>` с Ghost стилем, `×` или IconView
   - CSS: `.chip:has([data-interactive]:hover):hover` для anti-stacking
   - `data-interactive` на close button
4. Compose: `Chip.kt` + `ChipView.kt`
   - `leadingIcon: (@Composable () -> Unit)?`, `onDismiss: (() -> Unit)?`
   - Close: небольшой Box-button с × иконкой, Ghost цвета
5. Добавить экспорты в `react/src/index.ts`
6. Catalog: демо Chip на Surface с Button внутри

**Файлы:**
- СОЗДАТЬ: `core/uikit/common/.../components/composites/chip/ChipConfig.kt`
- СОЗДАТЬ: `core/uikit/common/.../components/composites/chip/ChipStyleResolver.kt`
- СОЗДАТЬ: `core/uikit/react/src/components/composites/chip/Chip.tsx`
- СОЗДАТЬ: `core/uikit/react/src/components/composites/chip/ChipView.tsx`
- СОЗДАТЬ: `core/uikit/react/src/components/composites/chip/ChipView.module.css`
- СОЗДАТЬ: `core/uikit/compose/.../components/composites/chip/Chip.kt`
- СОЗДАТЬ: `core/uikit/compose/.../components/composites/chip/ChipView.kt`
- ИЗМЕНИТЬ: `core/uikit/react/src/index.ts` — добавить Chip exports

---

### Phase 7: Удаление IconButton + Catalog обновление [COMPLETE]
**Решает:** финальная очистка после deprecation в Phase 5
**Критичность:** 🟢 Низкая — cleanup
**Зависимость:** Phase 5 (IconButton deprecated), Phase 6 (Chip готов)

**Что делать:**
1. Удалить `core/uikit/common/.../composites/iconbutton/` — весь каталог
2. Удалить `core/uikit/react/src/components/composites/icon-button/` — весь каталог
3. Удалить `core/uikit/compose/.../composites/iconbutton/` — весь каталог
4. Убрать из `react/src/index.ts` — IconButton, IconButtonView, IconButtonConfig, IconButtonStyleResolver
5. Обновить catalog-ui: заменить все `<IconButton>` на `<Button text="" iconEnd={...}>`
6. Обновить catalog-ui compose: аналогично
7. Убрать `IconButtonConfig`, `IconButtonStyleResolver` из KMP JS runtime
8. Обновить тесты

**Файлы:**
- УДАЛИТЬ: `core/uikit/common/.../composites/iconbutton/` (весь каталог)
- УДАЛИТЬ: `core/uikit/react/src/components/composites/icon-button/` (весь каталог)
- УДАЛИТЬ: `core/uikit/compose/.../composites/iconbutton/` (весь каталог)
- ИЗМЕНИТЬ: `core/uikit/react/src/index.ts` — убрать IconButton экспорты
- ИЗМЕНИТЬ: catalog-ui (react + compose) — заменить IconButton → Button icon-only

---

## Порядок выполнения

```
Phase 1 ─────────┐
                  ├──→ Phase 2
Phase 3 ──────┐  │
              ├──┼──→ Phase 4 ──→ Phase 5 ──→ Phase 7
              │  │                    │
              │  └──→ Phase 6 ←──────┘
              │         ↑
              └─────────┘
```

- **Phase 1 и Phase 3 параллельны** — независимые задачи
- **Phase 2** зависит от Phase 1 (nestingDepth)
- **Phase 4** зависит от Phase 3 (Icon)
- **Phase 5** зависит от Phase 3+4 (Icon в Button)
- **Phase 6** зависит от Phase 1 (nestingDepth) + Phase 3 (Icon) + Phase 5 (Button icon-only для close)
- **Phase 7** — финальная очистка после всего

---

## Errors Encountered

| Error | Attempt | Resolution |
|-------|---------|------------|
| (пока нет) | | |
