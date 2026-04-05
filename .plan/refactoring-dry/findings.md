# Findings — DRY-нарушения UIKit Common

Результат детального анализа кодовой базы `core/uikit/common/`.

---

## Проблема #1: Цветовая матрица заперта внутри ButtonStyleResolver

### Текущее состояние
`ButtonStyleResolver.kt` (~290 строк) содержит 5 private-методов цветовой логики:
- `solidColors(intent, tokens)` — 3 intent-ветки, Solid всегда border=transparent
- `softColors(intent, tokens, surfaceContext?)` — 3 intent-ветки + surface-aware адаптация
- `surfaceColors(intent, tokens)` — 3 intent-ветки с конкретными border-ами
- `outlineColors(intent, tokens)` — 3 intent-ветки, bg=transparent, видимые border-ы
- `ghostColors(intent, tokens)` — 3 intent-ветки, bg+border=transparent

**Всего: ~160 строк** чисто цветовой логики, которая **не зависит от Button**.

### Симптом: IconButtonStyleResolver создаёт фиктивный ButtonConfig
```kotlin
val btnConfig = ButtonConfig(text = "", variant = config.variant, ...)
val btnStyle = ButtonStyleResolver.resolve(btnConfig, tokens, surfaceContext)
```
Это создаёт **ложную зависимость** IconButton → Button. Добавляет ненужные вычисления размеров.

### Решение: InteractiveColorResolver
Сигнатура: `resolve(variant, intent, tokens, surfaceContext?) → ColorSet`

**Почему object а не companion:** ColorSet принадлежит Button, но используется многими компонентами. Создание отдельного object в `foundation/` — правильная декомпозиция. Все будущие компоненты (Chip, Toggle, Badge, Input, etc.) будут использовать этот же resolver.

### Риски
- Нужно убедиться что `@JsExport object` корректно экспортируется — ✅ уже работает с `ComponentSizeResolver`
- ColorSet определён в `button/` — нужно вынести в `foundation/` или оставить и импортировать
  - **Решение:** `ColorSet` перенести в `foundation/ColorSet.kt` — это общий тип, не привязанный к Button
  - `ButtonStyleResolver.kt` сохраняет `SizeSet` и `ResolvedButtonStyle` (component-specific)

---

## Проблема #2: Disabled ColorSet дублируется

### Текущее состояние
Один блок в `ButtonStyleResolver.resolve()`:
```kotlin
ColorSet(
    bg = tokens.color.surfaceDisabled,
    bgHover = tokens.color.surfaceDisabled,
    text = tokens.color.textDisabled,
    textHover = tokens.color.textDisabled,
    border = tokens.color.borderDisabled,
    borderHover = tokens.color.borderDisabled,
)
```

`IconButtonStyleResolver` не дублирует — делегирует через фиктивный ButtonConfig. Но при масштабировании каждый новый интерактивный компонент будет вынужден либо:
- Копировать этот блок
- Создавать фиктивные ButtonConfig

### Решение
`InteractiveColorResolver.resolveDisabled(tokens)` — единственный источник disabled-состояния для ВСЕХ интерактивных компонентов.

Альтернатива `ColorSet.Companion.disabled(tokens)` — менее чистая, загрязняет data class бизнес-логикой.

---

## Проблема #3: Вертикальный layout — copy-paste

### Текущее состояние
Идентичная формула в 2 файлах:

**ButtonStyleResolver.kt (строки ~85-96):**
```kotlin
val isVerticalWithIcon = (config.iconPosition == IconPosition.Top
    || config.iconPosition == IconPosition.Bottom) && config.hasIcon
if (isVerticalWithIcon) {
    paddingV = (scale.height - scale.lineHeight) / 2.0
    height = scale.iconSize + scale.iconGap + scale.lineHeight + 2.0 * paddingV
} else {
    paddingV = 0.0
    height = scale.height
}
```

**SegmentedControlStyleResolver.kt (строки ~64-73):**
```kotlin
val isVerticalLayout = config.iconPosition == IconPosition.Top
    || config.iconPosition == IconPosition.Bottom
if (isVerticalLayout) {
    paddingV = (scale.height - scale.lineHeight) / 2.0
    height = scale.iconSize + scale.iconGap + scale.lineHeight + 2.0 * paddingV
} else {
    paddingV = 0.0
    height = scale.height
}
```

**Разница:** в Button есть доп. условие `&& config.hasIcon` — но это снаружи формулы. Сама формула вычисления `height/paddingV` на основе `scale` — идентична.

### Решение
`ComponentSizeResolver.resolveVerticalLayout(scale, isVertical)` — возвращает `VerticalLayout(height, paddingV)`. Логика определения `isVertical` остаётся в компоненте (у каждого свои условия).

---

## Проблема #4: Magic string "transparent"

### Текущее состояние
Подсчёт вхождений в StyleResolvers:
- `ButtonStyleResolver.kt`: 18 вхождений "transparent" (border/borderHover в Solid, Soft, Ghost)
- `SegmentedControlStyleResolver.kt`: 4 вхождения
- `SurfaceStyleResolver.kt`: 2 вхождения "transparent" + 1 "none" (shadow)

**Итого: ~24 magic strings.**

### Решение
```kotlin
// foundation/ColorConstants.kt
object ColorConstants {
    const val TRANSPARENT = "transparent"
    const val SHADOW_NONE = "none"
}
```

**Почему object + const val, а не top-level const:**
- `@JsExport` не поддерживает top-level const val
- `object` — стандартный паттерн для констант в KMP проекте

**Альтернативы рассмотрены и отклонены:**
- `val` в `ColorTokens` — семантически некорректно, transparent не является токеном дизайна
- Top-level `const val` — не экспортируется в JS
- `companion object` в `ColorSet` — загрязняет data class

---

## Проблема #5: Border per-intent вместо per-variant

### Текущее состояние
Анализ border-паттернов в `ButtonStyleResolver`:

| Variant  | Primary border         | Neutral border          | Danger border          |
|----------|----------------------|------------------------|----------------------|
| Solid    | transparent          | transparent             | transparent          |
| Soft     | transparent          | transparent             | transparent          |
| Surface  | primaryBorder/Hover  | borderSubtle/outline    | danger/dangerHover   |
| Outline  | outline/primary      | outlineVariant/outline  | danger/dangerHover   |
| Ghost    | transparent          | transparent             | transparent          |

**Вывод:** border = transparent для Solid, Soft, Ghost (независимо от intent). Только Surface и Outline имеют intent-зависимые border-ы.

### Решение
В `InteractiveColorResolver` разделить:
```kotlin
private fun resolveBorder(variant, intent, tokens): Pair<String, String> =
    when (variant) {
        Solid, Soft, Ghost -> TRANSPARENT to TRANSPARENT
        Surface -> surfaceBorder(intent, tokens)
        Outline -> outlineBorder(intent, tokens)
    }
```

Это сократит количество мест, где border прописан, с 15 на 5 (только Surface×3 + Outline×3... нет, точнее сам when по variant — 5 строк + 2 подметода по 3 ветки).

**Фактически:** решение интегрируется в Phase 1 (InteractiveColorResolver) как архитектурное улучшение.

---

## Проблема #6: Два алгоритма surface-aware цветов

### Текущее состояние

**Алгоритм 1 — ButtonStyleResolver.softColors():**
```kotlin
val surfaceBg = surfaceContext?.backgroundColor
if (surfaceBg != null && surfaceBg == tokens.color.primarySoft) {
    // Прямое сравнение с одним конкретным токеном → fallback на hover shade
    ColorSet(bg = tokens.color.primarySoftHover, ...)
} else {
    ColorSet(bg = tokens.color.primarySoft, ...)
}
```
Простой: если bg == exactToken → swap на hover. Повторяется для Neutral аналогично.

**Алгоритм 2 — SurfaceAwareColorResolver:**
```kotlin
fun resolveSoftBg(surfaceBg, colors, isPrimary): String {
    val scale = listOf(surfaceContainerLowest..surfaceContainerHighest)
    // Ищет surfaceBg в scale → берёт +2 шага вверх
    // Для Primary: всегда containerHigh (или Highest если уже на nigh)
}
```
Сложный: работает с surface container scale, берёт шаг вверх.

**Какой правильный?**
- `SurfaceAwareColorResolver` — более гибкий (работает с любым surface уровнем)
- `ButtonStyleResolver` — проще, работает только с soft-токенами (primarySoft, neutralSoft)
- **На практике используется ТОЛЬКО ButtonStyleResolver.** SurfaceAwareColorResolver **НИГДЕ не вызывается** (кроме своего определения).

### Решение
1. Перенести логику из `ButtonStyleResolver.softColors()` в `InteractiveColorResolver`
2. Удалить `SurfaceAwareColorResolver.kt` — он не используется и решает другую задачу
3. Если в будущем нужен surface-scale алгоритм — вернуть как отдельный утилитный объект

---

## Проблема #7: isInteractive copy-paste

### Текущее состояние
```kotlin
// ButtonConfig.kt
val isInteractive: Boolean get() = !disabled && !loading

// IconButtonConfig.kt
val isInteractive: Boolean get() = !disabled && !loading
```

### Анализ ограничений
- `@JsExport data class` НЕ может implements interface с default implementation → JS не видит метод
- `@JsExport` НЕ поддерживает extension properties → JS не видит
- Sealed interface → `@JsExport` не поддерживает

### Решение: принять как KMP trade-off
- 1 строка кода в каждом Config — минимальный overhead
- При 15 интерактивных компонентах — 15 строк. Не критично.
- Для Kotlin-only (Compose): можно добавить internal extension для удобства, но НЕ стоит — это over-engineering для 1 строки

---

## Проблема #8: Общие поля Config (id, testTag, visibility)

### Текущее состояние
Каждый Config содержит:
```kotlin
val id: String = "",
val testTag: String? = null,
val visibility: Visibility = Visibility.Visible,
```
Интерактивные добавляют:
```kotlin
val disabled: Boolean = false,
val loading: Boolean = false,
val actionRoute: String? = null,
```

### Анализ вариантов

**Вариант A: Вложенный `CommonProps` data class:**
```kotlin
data class CommonProps(val id: String = "", val testTag: String? = null, val visibility: Visibility = Visibility.Visible)
data class ButtonConfig(val text: String, ..., val common: CommonProps = CommonProps())
```
- ❌ Ломает API: `config.id` → `config.common.id`
- ❌ Ухудшает ergonomics
- ❌ Не стоит ради 3 полей

**Вариант B: KSP code generation:**
- ✅ Генерирует boilerplate автоматически
- ❌ Добавляет сложность build pipeline
- ❌ Не стоит при 5 компонентах, может стоить при 50

**Вариант C: Принять как trade-off:**
- ✅ Простота, ясность, zero overhead
- ✅ IDE autocomplete подсказывает всё
- ✅ `@Serializable` работает нативно
- ❌ Копирование 3-6 полей

### Решение: принять как trade-off (вариант C)
Зафиксировать в AGENTS.md как "Known KMP Trade-offs". Вернуться к KSP при 30+ компонентах.

---

## Проблема #9: resolveDefault дублирует resolve

### Текущее состояние
`SegmentedControlStyleResolver` имеет два метода:
1. `resolve(config, tokens)` — основной, 30 строк
2. `resolve(tokens)` (JsName="resolveDefault") — backward-compatible, 25 строк

Тело `resolveDefault` дублирует конструкцию `SegmentedControlSizes` с 11 полями. Единственная разница:
- `height = scale.height` вместо вычисленного через vertical layout
- `paddingV = 0.0` вместо вычисленного

### Решение
```kotlin
@JsName("resolveDefault")
fun resolve(tokens: DesignTokens): ResolvedSegmentedControlStyle =
    resolve(SegmentedControlConfig(options = emptyArray(), selectedId = ""), tokens)
```

Дефолтный конфиг с `iconPosition = IconPosition.None` → `isVertical = false` → `paddingV = 0.0`, `height = scale.height`. Результат идентичен.

**Проверка:** `SegmentedControlConfig` имеет defaults: `size = Sm`, `variant = Surface`, `iconPosition = None`. `resolveDefault` использует `Sm` и `Surface`. Совпадает ✅

---

## Проблема #10: Boilerplate вызов ComponentSizeResolver

### Текущее состояние
4 вызова одной и той же тройки:
```kotlin
val scale = ComponentSizeResolver.resolve(config.size, tokens.controls, tokens.scaleFactor)
```

### Решение
Kotlin extension function (НЕ @JsExport — extension functions не экспортируются в JS):
```kotlin
// в ComponentSizeResolver.kt
fun DesignTokens.resolveSize(size: ComponentSize): ControlSizeScale =
    ComponentSizeResolver.resolve(size, controls, scaleFactor)
```

**Допустимо** потому что:
- Extension вызывается ТОЛЬКО из StyleResolver-ов (Kotlin code)
- StyleResolver-ы сами `@JsExport`, но их internal implementation — чистый Kotlin
- JS-потребители вызывают `StyleResolver.resolve()`, а не `ComponentSizeResolver` напрямую

---

## Вынос ColorSet из Button в Foundation

### Текущее состояние
`ColorSet` определён в `com.uikit.components.atoms.button.ButtonStyleResolver.kt`.
Используется в:
- `ButtonStyleResolver` → `ResolvedButtonStyle.colors`
- `IconButtonStyleResolver` → `ResolvedIconButtonStyle.colors` (импорт из button)

### Проблема
`ColorSet` — **общий тип** для всех интерактивных компонентов. Его определение в `button/` создаёт ложную зависимость.

### Решение
Перенести `ColorSet` в `foundation/ColorSet.kt`. Обновить все импорты.

**Impact:**
- Public API не меняется (для JS потребителей package path виден, но `@JsExport` data class переносим — namespace в JS будет `com.uikit.foundation`)
- Kotlin-потребители в Compose/React обновят import — breaking change для внутреннего кода, не для пользователей

---

## Архитектурная диаграмма (после рефакторинга)

```
foundation/
├── ColorConstants.kt           ← NEW: TRANSPARENT, SHADOW_NONE
├── ColorSet.kt                 ← MOVED from button/
├── InteractiveColorResolver.kt ← NEW: resolve(variant, intent, tokens, surface?) → ColorSet
├── ComponentSizeResolver.kt    ← UPDATED: + resolveVerticalLayout() + DesignTokens.resolveSize()
├── ComponentSize.kt
├── ColorIntent.kt
├── VisualVariant.kt
├── IconPosition.kt
├── SurfaceContext.kt
├── Visibility.kt
└── ... (остальные без изменений)

components/atoms/
├── button/
│   ├── ButtonConfig.kt          ← без изменений
│   └── ButtonStyleResolver.kt   ← SIMPLIFIED: ~60 строк, делегирует цвета
├── iconbutton/
│   ├── IconButtonConfig.kt      ← без изменений
│   └── IconButtonStyleResolver.kt ← SIMPLIFIED: прямой вызов InteractiveColorResolver
├── segmentedcontrol/
│   ├── SegmentedControlConfig.kt ← без изменений
│   └── SegmentedControlStyleResolver.kt ← SIMPLIFIED: resolveDefault делегирует
├── surface/
│   └── ... (минимальные изменения: ColorConstants)
└── text/
    └── ... (без изменений)

УДАЛЕНЫ:
├── SurfaceAwareColorResolver.kt ← не используется, заменён InteractiveColorResolver
```
