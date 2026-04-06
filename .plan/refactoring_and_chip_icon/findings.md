# Findings: Вложенные цвета + Icon → Button → Chip

## 1. Анализ проблемы вложенных цветов

### Текущее состояние InteractiveColorResolver

`InteractiveColorResolver.softColors()` поддерживает **только 1 уровень** адаптации:
```kotlin
if (surfaceBg != null && surfaceBg == tokens.color.primarySoft) {
    // escalation: bg = primarySoftHover, bgHover = surfaceHover
}
```

Это покрывает кейс `Surface(Soft,Primary) → Button(Soft,Primary)` (2 уровня), но НЕ покрывает:
- 3 уровня: `Surface → Chip(Soft) → Button(Ghost)` — Ghost не адаптируется к nestingDepth
- Danger intent — вообще не имеет surface-awareness
- Стекинг hover'ов — каждый уровень показывает свой `:hover` одновременно

### Индустриальные подходы (из NESTED_INTERACTIVE_COLORS_GUIDE.md)

| Подход | Суть | Оценка для UIKit |
|--------|------|-----------------|
| **M3 State Layers** | overlay onColor @ 8% поверх bg | Хороший для hover, требует runtime alpha-blend |
| **Radix 12-Step** | step 3/4/5 для bg/hover/active по уровню | Отлично, но нужна шкала — у нас только 2 step (soft + softHover) |
| **Spectrum Semantic Tokens** | каждый компонент берёт pre-resolved token | Жёстко, нет runtime flexibility |
| **Hybrid (рекомендуемый)** | Tonal Staircase + State Layer + SurfaceContext | ✅ Лучший для UIKit |

### Выбранная стратегия: Tonal Staircase + Step Down Rule

1. **Tonal Staircase**: для Soft-варианта массив шагов по nestingDepth:
   ```
   depth 0: primarySoft → primarySoftHover
   depth 1: primarySoftHover → surfaceContainerHigh  
   depth 2: surfaceContainerHigh → surfaceContainerHighest
   ```

2. **Step Down Rule**: при вложенности вариант "смягчается":
   ```
   Solid → Soft → Ghost (при увеличении depth)
   ```
   Это конвенция для дизайнеров, а не автоматический enforcement в коде.

3. **Anti-stacking**: CSS `:has()` и Compose `CompositionLocal` — hover показывается только на самом глубоком hovered элементе.

### Почему НЕ StateLayerCalculator

`StateLayerCalculator` (runtime alpha-blend) — мощный, но:
- Требует HEX → RGB → blend → HEX на каждый рендер
- Для SSR нужно рассчитывать в Node.js (overhead)
- У нас уже есть `primarySoftHover` и т.д. — этого ДОСТАТОЧНО для 3 уровней
- StateLayerCalculator будет полезен когда появятся произвольные brand-цвета (DTCG Phase)

**Решение: Tonal Staircase с предрассчитанными токенами сейчас, StateLayerCalculator — позже.**

---

## 2. Анализ IconButton: почему удалять

### Текущее дублирование

| Аспект | Button | IconButton | Дублирование |
|--------|--------|------------|-------------|
| Config fields | 12 полей | 10 полей | 8 общих (variant, intent, size, disabled, loading, id, actionRoute, testTag, visibility) |
| StyleResolver | InteractiveColorResolver + SizeSet | InteractiveColorResolver + IconButtonSizeSet | Цвета — идентичный код |
| View (React) | ~100 LOC | ~65 LOC | handleClick, aria-disabled, CSS vars, spinner — copy-paste |
| View (Compose) | ~120 LOC | ~90 LOC | interactionSource, hover, click, disabled semantics — copy-paste |
| CSS | ~85 LOC | ~65 LOC | hover/active/disabled/focus-visible — copy-paste |

**Суммарно:** ~500 LOC дублирования которое можно устранить одним `isIconOnly` в Button.

### Ключевое отличие: padding и размер

IconButton: `width = height` (квадрат), `padding = 0`, `radius` из scale.
Button: `width = auto`, `padding-inline = paddingH`, `min-height = height`.

Для icon-only mode в Button:
```
iconOnlyPadding = (height - iconSize) / 2
```

При `radius = height / 2` (полный круг):
```
height = 40, iconSize = 19.2, radius = 20
iconOnlyPadding = (40 - 19.2) / 2 = 10.4
```
Иконка 19.2 + padding 2×10.4 = 40 = height ✅ Вписывается в круг.

### Circular Icon Button

При `radius ≥ height / 2` кнопка визуально круглая. Padding гарантирует что иконка не обрезается:
```
┌─────────────────────┐
│     ╭─────────╮     │  radius = height/2
│     │  icon   │     │  icon area = iconSize × iconSize  
│     ╰─────────╯     │  padding = (height - iconSize) / 2
└─────────────────────┘
```

Текущий IconButton уже делает это неявно (`width=height`, `padding=0`, icon centered). Button icon-only mode делает это **явно** через `iconOnlyPadding`.

---

## 3. Анализ Icon Primitive

### Зачем нужен Icon как отдельный primitive

Сейчас: `iconStart?: React.ReactNode` — потребитель передаёт raw `<svg>` или любой элемент. Проблемы:
1. Нет контроля размера — потребитель может передать иконку любого размера
2. Нет системного цвета — `currentColor` работает только если svg использует `fill="currentColor"`
3. Нет консистентности — каждый потребитель по-своему оборачивает svg
4. Нет BDUI — сервер не может указать иконку по имени

### Что Icon primitive даёт

1. **Системный размер**: Icon знает свой размер из `ComponentSize` или явный `customSize`
2. **Цвет через наследование**: `color: inherit` в CSS, `LocalContentColor` в Compose
3. **BDUI-ready**: `name: String` позволяет серверу указать иконку
4. **Правильный контракт**: Icon — не просто обёртка, а полноценный компонент с Config + Resolver

### Подход к rendering

**React:** Icon рендерится как `<span>` обёртка с `width/height/color`. Содержимое — `children` (ReactNode):
```tsx
<Icon size="md"><PlusIcon /></Icon>
```
Или будущее: registry-based `<Icon name="plus" size="md" />`

**Compose:** Icon рендерится как `Box(Modifier.size(size.dp))` с `LocalContentColor`:
```kotlin
Icon(size = ComponentSize.Md) { PlusIcon() }
```

### Slot vs Name подход

- **Slot** (ReactNode / Composable): гибко, работает сейчас, потребитель контролирует содержимое
- **Name** (String -> registry): нужен icon registry, BDUI-friendly, не работает без инфраструктуры

**Решение: Slot сейчас, Name как опциональное поле для BDUI в будущем.**

---

## 4. Анализ Chip composite

### Chip в индустрии (M3, Radix, Shadcn)

| Система | Варианты | Анатомия |
|---------|----------|----------|
| M3 | Assist, Filter, Input, Suggestion | Container + Label + Leading Icon + Trailing Icon + Checkmark |
| Radix | Soft, Solid, Outline, Surface | Text + optional close |
| Shadcn | Default, Secondary, Destructive, Outline | Label + optional close |

### Наша модель (минимальная)

3 варианта использования:
1. **Info** — отображение тега/метки (не интерактивный или clickable)
2. **Selectable** — toggle selected state (filter chips)
3. **Dismissible** — с кнопкой закрытия ×

**Chip = Surface(Soft) + Icon?(leading) + Text + CloseButton?(Ghost)**

### Вложенность в Chip

```
<Surface>                    ← depth 0
  <Chip variant="soft">      ← depth 1 (interactive → nestingDepth++)
    <Text />                  ← inherits color
    <Icon />                  ← inherits color  
    <CloseButton ghost />     ← depth 2 (interactive → nestingDepth++)
  </Chip>
</Surface>
```

Close button внутри Chip — это Button icon-only с variant=Ghost. Step Down Rule выполняется автоматически: Surface(Solid) → Chip(Soft) → CloseButton(Ghost).

### Размеры Chip vs Button

Chip обычно компактнее Button:
- M3: Chip height = 32dp vs Button height = 40dp (при одинаковом font size)
- Наш подход: использовать те же `ComponentSize`, но Chip берёт **на 1 step меньше** или использует отдельный `heightRatio`

**Простое решение:** Chip использует `ComponentSizeResolver` с модифицированными пропорциями:
```kotlin
// Chip proportions: компактнее Button
val chipHeightRatio = 2.0    // vs Button 2.5
val chipPaddingHRatio = 0.75 // vs Button 1.0
```

Или: Chip всегда на 1 size меньше чем Button того же "логического" размера:
```
Chip(Md) ≈ Button(Sm) по высоте
```

**Решение: отдельные proportions для Chip в InteractiveControlTokens (chipProportions).**

---

## 5. Текущая структура файлов (snapshot)

```
core/uikit/common/src/commonMain/kotlin/com/uikit/
├── foundation/
│   ├── ColorConstants.kt
│   ├── ColorIntent.kt        ← Primary | Neutral | Danger
│   ├── ColorSet.kt           ← bg, bgHover, text, textHover, border, borderHover
│   ├── ComponentSize.kt      ← Xs | Sm | Md | Lg | Xl
│   ├── ComponentSizeResolver.kt ← resolve(size, controls, scaleFactor)
│   ├── Density.kt
│   ├── IconPosition.kt       ← None | Start | End | Top | Bottom
│   ├── InteractiveColorResolver.kt ← resolve(variant, intent, tokens, surface?)
│   ├── SurfaceContext.kt      ← level, backgroundColor
│   ├── VisualVariant.kt      ← Solid | Soft | Surface | Outline | Ghost
│   └── ...
├── tokens/
│   ├── ColorTokens.kt        ← 40+ color tokens
│   ├── InteractiveControlTokens.kt ← proportions + xs-xl size inputs
│   └── ...
└── components/
    ├── primitives/
    │   ├── surface/
    │   └── text/
    ├── composites/
    │   ├── button/            ← ButtonConfig + ButtonStyleResolver
    │   ├── iconbutton/        ← IconButtonConfig + IconButtonStyleResolver  ← БУДЕТ УДАЛЕНО
    │   └── segmentedcontrol/
    └── blocks/
        └── panel/
```

**После рефакторинга:**
```
├── primitives/
│   ├── icon/          ← НОВЫЙ: IconConfig + IconStyleResolver
│   ├── surface/
│   └── text/
├── composites/
│   ├── button/        ← ИЗМЕНЁН: text optional, icon-only mode, ariaLabel
│   ├── chip/          ← НОВЫЙ: ChipConfig + ChipStyleResolver
│   └── segmentedcontrol/
```
