# Вложенные интерактивные компоненты: расчёт цветов и hover-состояний

> Гайд по решению проблемы цветовых конфликтов при вложенности `Surface → Chip → IconButton` и аналогичных комбинаций.

---

## 1. Формулировка проблемы

```
<Surface>            ← bg: #F5F5F5, hover: #EBEBEB
  <Chip>             ← bg: #E8E8E8, hover: #DEDEDE
    <IconButton/>    ← bg: #DEDEDE, hover: #D4D4D4
  </Chip>
</Surface>
```

Три уровня вложенности, каждый с hover-эффектом. Проблемы:

1. **Визуальное слияние** — фон ребёнка совпадает с hover-ом родителя (Chip bg ≈ Surface hover);
2. **Ghost-стекинг** — при наведении на IconButton, Chip тоже получает hover (pointer events propagation), и оба overlay'а складываются;
3. **Недостаток контраста** — 3 уровня lightness-смещения в одну сторону «сжимают» палитру до неразличимого диапазона;
4. **Несимметричность тем** — delta, подобранная для light-темы, ломает dark-тему (и наоборот).

---

## 2. Подходы ведущих дизайн-систем (2024–2026)

### 2.1 Material Design 3: State Layers + Tonal Surfaces

**Принцип: elevation → tonal color, hover → semi-transparent overlay «on color» поверх контейнера.**

| Концепт | Как работает |
|---------|-------------|
| **Surface Container Levels** | 5 уровней (`Lowest` → `Highest`) с фиксированным тональным шагом. Каждый уровень = другой tone value в HCT color space |
| **State Layer** | Полупрозрачный overlay цветом «on color» контейнера: hover = 8%, focus = 10%, press = 10%, drag = 16% |
| **Isolation** | Hover применяется **только к ближайшему** интерактивному элементу — нет стекинга слоёв |
| **Elevation Change** | Hover поднимает elevation на 1 уровень → tonal shift |

Формула state layer для hover:

```
resultColor = alphaBlend(containerColor, onColor, 0.08)

// Alpha-композитинг (Porter-Duff source-over):
alphaBlend(base, overlay, α) =
  overlay × α + base × (1 − α)
```

**Ключевое правило M3:** state layer всегда использует `on-color` родного контейнера, а НЕ абсолютный цвет. Поэтому overlay автоматически даёт контраст на ЛЮБОМ уровне.

### 2.2 Radix Colors: 12-Step Scale

**Принцип: у каждого цвета 12 чётких steps, каждый step = конкретный use case.**

| Step | Назначение |
|------|-----------|
| 1–2 | App/Subtle backgrounds |
| **3** | **UI element background (normal)** |
| **4** | **UI element background (hover)** |
| **5** | **UI element background (active/selected)** |
| 6 | Subtle borders |
| 7 | UI element borders |
| 8 | Hovered borders |
| 9–10 | Solid backgrounds + hover |
| 11–12 | Text |

При вложенности:
```
Surface = step 2
  Chip    = step 3 (normal) → step 4 (hover)
    IconButton = step 4 (normal) → step 5 (hover)
```

**Гарантия:** каждый step имеет предсказуемый contrast delta — нет визуального слияния.

### 2.3 Adobe Spectrum: Semantic Tokens + Alias System

**Принцип: flat token naming, component-specific tokens, жёсткий alias chain.**  
Компонент никогда не «вычисляет» цвет — берёт уже рассчитанный token. При вложенности используются разные token aliases.

---

## 3. Фундаментальные стратегии (5 подходов)

### Стратегия A: State Layer Overlay (M3)

```
hoverColor(component) = alphaBlend(component.bg, component.onColor, 0.08)
```

**Плюсы:** Один overlay-процент, автоматически адаптируется к любому bg.  
**Минусы:** Требует runtime alpha-compositing. На глубоких вложенностях overlay'ы могут складываться при CSS propagation.

**Решение стекинга:** `pointer-events: none` на state layer родителя, когда pointer в зоне ребёнка. Или — JavaScript `stopPropagation()`.

### Стратегия B: Tonal Step Escalation (Radix)

```
normalBg(depth) = scale[baseStep + depth]
hoverBg(depth)  = scale[baseStep + depth + 1]
```

**Плюсы:** Каждый уровень гарантированно отличим. Предсказуемо.  
**Минусы:** Максимум ~4 уровня вложенности (steps заканчиваются). Нет адаптивности к произвольным цветам.

### Стратегия C: Surface Context Propagation (наш UIKit)

```kotlin
// Ребёнок «знает» цвет родителя и адаптируется:
if (surfaceContext.backgroundColor == tokens.primarySoft) {
    // используем primarySoftHover вместо primarySoft
} else {
    // нормальный primarySoft
}
```

**Плюсы:** Кросс-платформенный (context/CompositionLocal), SSR-safe.  
**Минусы:** Ручные проверки `if bg == X then Y`. Не масштабируется на 3+ уровней без cascade of conditions.

### Стратегия D: Relative Lightness/Alpha Offset

```
hoverBg(component) = adjustLightness(component.bg, delta)
// delta рассчитывается как функция от nesting level:
delta(level) = baseHoverDelta × (1 - level × 0.15)
```

**Плюсы:** Математически масштабируется на N уровней.  
**Минусы:** Требует runtime HCT/OKLCH манипуляций. Дорого для SSR. Непредсказуемые edge cases.

### Стратегия E: Hybrid (рекомендуемый)

**Комбинация B + C с элементами A:**

1. **Tonal Step по уровню вложенности** — определяет базовый bg;
2. **State Layer overlay для hover** — единый процент, применяемый через `on-color`;
3. **Surface Context** — propagation уровня + фона для адаптации Soft-вариантов.

---

## 4. Рекомендуемая архитектура для UIKit

### 4.1 Расширенный SurfaceContext

```kotlin
@JsExport @Serializable
data class SurfaceContext(
    val level: Int,                  // Уровень вложенности (0–5)
    val backgroundColor: String,     // HEX текущего фона
    val nestingDepth: Int = 0,       // Глубина вложенности интерактивных компонентов
)
```

Каждый интерактивный компонент при рендеринге **инкрементирует `nestingDepth`** и передаёт обновлённый контекст вниз:

```kotlin
// В Chip/Surface/Card рендерере:
val childContext = surfaceContext.copy(
    nestingDepth = surfaceContext.nestingDepth + 1,
    backgroundColor = resolvedBg
)
```

### 4.2 Тональная лестница (Tonal Staircase)

Основная идея: для эквивалентных variant+intent, каждый уровень вложенности берёт bg на 1 step темнее (light theme) или светлее (dark theme):

```kotlin
object NestingColorStrategy {
    /**
     * Возвращает bg-цвет для Soft-варианта на заданном уровне вложенности.
     *
     * Level 0: primarySoft
     * Level 1: primarySoftHover (на 1 step темнее)  
     * Level 2: surfaceContainerHigh (на 2 step темнее)
     *
     * Hover — ещё +1 step от текущего bg.
     */
    fun resolveSoftBg(
        intent: ColorIntent,
        nestingDepth: Int,
        tokens: DesignTokens
    ): Pair<String, String> { // bg, bgHover
        val scale = getSoftScale(intent, tokens)
        val idx = nestingDepth.coerceIn(0, scale.lastIndex - 1)
        return scale[idx] to scale[idx + 1]
    }
    
    private fun getSoftScale(intent: ColorIntent, tokens: DesignTokens): List<String> =
        when (intent) {
            ColorIntent.Primary -> listOf(
                tokens.color.primarySoft,        // depth 0: bg
                tokens.color.primarySoftHover,    // depth 0: hover / depth 1: bg
                tokens.color.surfaceContainerHigh,// depth 1: hover / depth 2: bg
                tokens.color.surfaceContainerHighest, // depth 2: hover
            )
            ColorIntent.Neutral -> listOf(
                tokens.color.neutralSoft,
                tokens.color.neutralSoftHover,
                tokens.color.surfaceContainerHigh,
                tokens.color.surfaceContainerHighest,
            )
            ColorIntent.Danger -> listOf(
                tokens.color.dangerSoft,
                tokens.color.dangerSoftHover,
                tokens.color.surfaceContainer,
                tokens.color.surfaceContainerHigh,
            )
        }
}
```

### 4.3 State Layer для hover (вместо статических hover-токенов)

Вместо отдельного токена `bgHover` можно рассчитывать hover цвет **формулой**, что автоматически гарантирует контраст:

```kotlin
/**
 * Рассчитывает hover-цвет как alpha-blend onColor поверх bg.
 * 
 * По спецификации Material 3:
 * - Hover: contentColor @ 8% opacity
 * - Press: contentColor @ 10%  opacity
 * - Focus: contentColor @ 10% opacity
 * - Drag:  contentColor @ 16% opacity
 */
object StateLayerCalculator {
    
    const val HOVER_OPACITY = 0.08
    const val FOCUS_OPACITY = 0.10
    const val PRESS_OPACITY = 0.10
    const val DRAG_OPACITY = 0.16
    
    /**
     * containerColor — bg компонента (напр. #E8E0F0)
     * contentColor  — цвет текста/иконок (on-color) (напр. #1D1B20)
     * opacity       — интенсивность overlay
     * 
     * result = contentColor × opacity + containerColor × (1 - opacity)
     */
    fun blend(
        containerColor: String,    // "#RRGGBB"
        contentColor: String,      // "#RRGGBB" 
        opacity: Double = HOVER_OPACITY
    ): String {
        val (cR, cG, cB) = parseHex(containerColor)
        val (oR, oG, oB) = parseHex(contentColor)
        
        val r = (oR * opacity + cR * (1.0 - opacity)).toInt().coerceIn(0, 255)
        val g = (oG * opacity + cG * (1.0 - opacity)).toInt().coerceIn(0, 255)
        val b = (oB * opacity + cB * (1.0 - opacity)).toInt().coerceIn(0, 255)
        
        return "#${r.hex()}${g.hex()}${b.hex()}"
    }
}
```

**Преимущество**: hover для ЛЮБОГО bg и ЛЮБОГО уровня вложенности рассчитывается по единой формуле.  
Не нужны `primarySoftHover`, `neutralSoftHover` etc. — hover всегда = `blend(bg, textColor, 0.08)`.

### 4.4 Предотвращение стекинга hover'ов

Когда `IconButton` внутри `Chip`, и курсор на `IconButton`:

```
❌ Без защиты:
Surface (hover) + Chip (hover) + IconButton (hover) = 3 overlay'а

✅ С защитой:  
Surface (normal) + Chip (normal) + IconButton (hover) = 1 overlay
```

#### CSS-решение (React):

```css
/* Chip отключает свой hover, если hover на дочернем интерактивном элементе */
.chip:has(.iconButton:hover):hover {
    /* НЕ применяем hover-стили к chip */
    --chip-bg: var(--chip-bg-normal);
}

/* Или через pointer-events на state layer */
.chip:hover:not(:has(:hover[data-interactive])) {
    --chip-bg: var(--chip-bg-hover);
}
```

#### Kotlin/Compose-решение:

```kotlin
// В Chip:
val isChildHovered = remember { mutableStateOf(false) }

Box(
    modifier = Modifier
        .hoverable(interactionSource)
        // Показываем hover только если ребёнок не hovered
        .background(
            if (isHovered && !isChildHovered.value) hoverBg else normalBg
        )
) {
    CompositionLocalProvider(
        LocalParentHoverCallback provides { isChildHovered.value = it }
    ) {
        content()
    }
}
```

#### Рекомендация: CSS `:has()` + `pointer-events`

Самый чистый подход — CSS `:has()` (поддержка 96%+ в 2026). Это не требует JavaScript/state management и работает на уровне browser engine.

---

## 5. Матрица варианта × вложенность

Как каждый визуальный вариант ведёт себя при вложенности:

| Родитель variant | Ребёнок variant | Стратегия |
|-----------------|----------------|-----------|
| **Surface (Solid)** | Soft | Soft bg = step N адаптирован к Surface level → автоматический контраст |
| **Surface (Solid)** | Ghost | Transparent bg → виден фон Surface. Hover = state layer overlay |
| **Surface (Solid)** | Outline | Transparent bg + border. Hover = subtle fill |
| **Chip (Soft)** | IconButton (Ghost) | ✅ **Лучшая комбинация**: Ghost на Soft-bg максимально чистый |
| **Chip (Soft)** | IconButton (Soft) | ⚠️ Требует tonal escalation (bg ребёнка темнее bg родителя) |
| **Chip (Solid)** | IconButton (Ghost) | ✅ Ghost прозрачен → контраст гарантирован |
| **Chip (Solid)** | IconButton (Solid) | ❌ **Опасно**: два opaque bg друг на друге — высокий риск конфликта |

### Рекомендация: правило «Step Down»

> **При вложенности интерактивных компонентов, каждый следующий уровень должен использовать менее «материальный» визуальный вариант:**
>
> `Solid → Soft → Ghost/Outline`  
> Чем глубже элемент, тем «легче» его визуальный стиль.

```
Surface (Solid, Level 2)
  └── Chip (Soft, Primary)
        └── IconButton (Ghost, Primary)
```

Это правило автоматически решает проблему цветовых конфликтов и является стандартом ведущих дизайн-систем.

---

## 6. Алгоритм расчёта цветов для N-уровневой вложенности

### Финальная формула

```
Для компонента на уровне вложенности D (0-indexed):

1. Определить визуальный вариант:
   inferredVariant(D) = 
     D == 0 → variant из config (может быть Solid/Soft/etc.)
     D == 1 → max(Soft, configVariant)
     D >= 2 → max(Ghost, configVariant)
   
   // max() здесь = "более лёгкий" в порядке Solid < Soft < Outline < Ghost

2. Определить bg:
   bg(D) = 
     Ghost/Outline → transparent
     Soft → softScale[D].bg  // тональная лестница
     Solid → containerScale[level + D]

3. Определить hover:
   hoverBg(D) = StateLayer.blend(bg(D), textColor(D), 0.08)

4. Гарантировать контраст:
   assert contrastRatio(bg(D), bg(D-1)) >= 1.1  // минимум 10% tonal difference
   assert contrastRatio(hoverBg(D), bg(D-1)) >= 1.15
```

### Пример расчёта

Light theme, Primary intent:

```
Surface (D=0, Solid, Level 2)
  bg       = surfaceContainerLow = #F7F2FA  (tone 96)
  hover    = blend(#F7F2FA, #1D1B20, 0.08) = #EBE7EF
  
  Chip (D=1, Soft→Soft, Primary)
    bg     = primarySoft = #E8DEF8  (tone 90)
    hover  = blend(#E8DEF8, #1D1B20, 0.08) = #DCD3EC
    
    IconButton (D=2, Soft→Ghost, Primary)
      bg   = transparent (визуально = #E8DEF8 от Chip)
      hover= blend(#E8DEF8, #65558F, 0.08) = #E2D7F1
```

**Результат: 3 различимых hover-эффекта, ни один не конфликтует с родителем/ребёнком.**

---

## 7. Реализация в текущей архитектуре UIKit

### 7.1 Что уже работает

Текущая реализация `InteractiveColorResolver` уже поддерживает **базовый уровень** адаптации для Soft-варианта:

```kotlin
// Уже есть:
if (surfaceBg == tokens.color.primarySoft) {
    bg = tokens.color.primarySoftHover  // escalation на 1 step
}
```

Это покрывает кейс `Surface(Soft, Primary) → Button(Soft, Primary)` (2 уровня).

### 7.2 Что нужно для полной поддержки 3+ уровней

| Улучшение | Сложность | Приоритет |
|-----------|-----------|-----------|
| `nestingDepth` в `SurfaceContext` | Низкая | Высокий — основа для всего |
| `NestingColorStrategy` с тональной лестницей | Средняя | Высокий |
| `StateLayerCalculator` для runtime-hover | Средняя | Средний (приятный бонус) |
| CSS `:has()` для anti-stacking | Низкая | Высокий |
| Auto-variant inference по глубине | Низкая | Средний (опционально) |

### 7.3 Минимальное решение (рекомендация)

Для покрытия проблемы `Surface → Chip → IconButton`:

1. **Chip использует Soft** — контраст с Surface (Solid) гарантирован;
2. **IconButton внутри Chip использует Ghost** — прозрачный bg, hover = state layer;
3. **CSS `:has()` предотвращает стекинг** hover-ов.

Это **не требует изменений в common/** — только правильный выбор variant'ов в дизайн-спецификации и CSS-правила в React.

---

## 8. Спецификация контраста между уровнями

### Минимальные требования

| Пара | Minimum ΔL* (CIE Lab) | Minimum Contrast Ratio |
|------|----------------------|----------------------|
| Parent bg ↔ Child bg | ≥ 5 | ≥ 1.15:1 |
| Parent hover ↔ Child hover | ≥ 7 | ≥ 1.2:1 |
| Child bg ↔ Parent hover | ≥ 3 | ≥ 1.1:1 |
| Text on any bg | ≥ 45 | ≥ 4.5:1 (WCAG AA) |

### HCT Tone Values (Material 3 Reference)

```
Light theme:
  Surface           = tone 98
  SurfaceContainer  = tone 94
  ContainerHigh     = tone 92
  ContainerHighest  = tone 90
  
  Primary           = tone 40
  PrimaryContainer  = tone 90
  PrimarySoft       = tone 90–92
  PrimarySoftHover  = tone 85–87
  
  State Layer hover overlay:
    on-color @ 8% → ≈ 3 tone shift
    on-color @ 10% → ≈ 4 tone shift
    on-color @ 16% → ≈ 6 tone shift
```

---

## 9. Чек-лист для дизайнера

- [ ] **Правило Step Down**: Solid → Soft → Ghost при вложенности
- [ ] **Не больше 3 уровней** интерактивных hover-ов (Surface, Chip, IconButton)
- [ ] **Ghost для глубоких элементов**: IconButton внутри Chip = Ghost variant
- [ ] **Не дублировать variant**: если Chip = Soft(Primary), IconButton ≠ Soft(Primary) на том же уровне
- [ ] **Hover не стекается**: визуально проверить, что при наведении на IconButton Chip НЕ показывает свой hover
- [ ] **Dark theme**: проверить все комбинации — delta инвертируется
- [ ] **Contrast audit**: ΔL* ≥ 5 между соседними уровнями bg

---

## 10. Источники

| Источник | Применимость |
|----------|-------------|
| **Material Design 3 — State Layers** | State layer overlay formula, hover/focus/press opacity values |
| **Material Design 3 — Color Roles** | Surface container levels, on-color concept, tonal surfaces |
| **Material Design 3 — Elevation** | Tonal difference вместо shadow, surface color roles hierarchy |
| **Radix Colors — Understanding the Scale** | 12-step scale, step 3/4/5 для bg/hover/active |
| **Adobe Spectrum — Design Tokens** | Semantic token aliasing, flat naming, component-specific tokens |
| **W3C Compositing & Blending Level 1** | Alpha compositing formula (Porter-Duff source-over) |
| **APCA (Advanced Perceptual Contrast Algorithm)** | Lightness contrast для text readability (замена WCAG 2.x formula) |

---

## Резюме

**Главный принцип**: при вложенности интерактивных компонентов каждый уровень должен быть «легче» предыдущего — от opaque (Solid) через semi-filled (Soft) к transparent (Ghost). Hover рассчитывается как state layer overlay `onColor @ 8%` поверх текущего bg, а не как отдельный статический токен. Стекинг hover'ов предотвращается через CSS `:has()` или эквивалентный механизм на каждой платформе.

**Формула для кросс-платформенного UIKit:**
```
bg(depth)    = depth == 0 ? resolved_from_variant : transparent_or_escalated_step
hover(depth) = alphaBlend(bg(depth), onColor, 0.08)
variant(depth) = clamp(configVariant, minVariant[depth])  // Solid→Soft→Ghost
```
