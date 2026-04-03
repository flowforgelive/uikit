# Findings & Decisions — UIKit Variant System v3

## Requirements

Из анализа `docs/VARIANT_SYSTEM_ANALYSIS.md` и обсуждения:

- **P1 CRITICAL:** Добавить `Surface` вариант (fill 3-10% + border) в `VisualVariant` enum
- **P2 BUG:** Surface Outline имеет фон `resolveBg()` вместо `"transparent"`
- **P3 BUG:** Surface Soft идентичен Solid (одна и та же `resolveBg()` функция)
- **P4 UX:** Button Soft Primary = серый, а не intent-тонированный (нет `primarySoft` токена)
- **P5 MISSING:** SegmentedControl не поддерживает варианты — нет `variant` prop
- **P6 SHOWCASE:** Ghost Surface не показан в showcase
- **P8 MINOR:** Border width harcoded в некоторых местах

> P7 (Glass/Blur) и P8 (border width из токенов) — вне скоупа этого плана.

## Текущее состояние кода — Snapshot

### VisualVariant.kt (17 строк)
```kotlin
enum class VisualVariant {
    Solid,    // 100% fill, нет border
    Soft,     // лёгкая fill, нет border
    Outline,  // нет fill, border
    Ghost,    // ничего, hover → fill
    // ❌ Нет Surface (fill + border)
}
```

### ColorTokens.kt (29 полей)
```
✅ primary, primaryHover
✅ danger, dangerHover, dangerSoft, dangerSoftHover
✅ surface → surfaceContainerHighest (6 уровней + hover)
✅ outline, outlineVariant, border
✅ text: textPrimary, textSecondary, textMuted, textOnPrimary, textOnDanger, textDisabled
✅ surfaceDisabled, borderDisabled, focusRing
❌ primarySoft, primarySoftHover         — нет
❌ neutralSoft, neutralSoftHover         — нет
❌ borderSubtle                          — нет
❌ primaryBorder, primaryBorderHover     — нет
```

### ButtonStyleResolver.kt — resolveColors dispatcher (строка ~77)
```kotlin
private fun resolveColors(variant, intent, tokens, surfaceContext) = when (variant) {
    Solid   → solidColors(intent, tokens)
    Soft    → softColors(intent, tokens, surfaceContext)
    Outline → outlineColors(intent, tokens)
    Ghost   → ghostColors(intent, tokens)
    // ❌ Нет Surface ветки
}
```

### ButtonStyleResolver — softColors для Primary (строки ~98-155)
```
Primary soft bg → surfaceContainerHigh (серый #EAEAEA)  ← ❌ не intent-tinted
                  Через SurfaceAwareColorResolver.resolveSoftBg(isPrimary=true)
Neutral soft bg → surfaceContainerLow (#F7F7F7)
                  Через SurfaceAwareColorResolver.resolveSoftBg(isPrimary=false)
Danger  soft bg → dangerSoft (#FEE2E2)  ← ✅ intent-тонированный
```

### SurfaceStyleResolver.kt — resolve() (строка ~37)
```kotlin
val bg = when (config.variant) {
    Ghost → "transparent"
    else  → resolveBg(config.level, tokens)  // ❌ Outline тоже сюда!
}

val border = when (config.variant) {
    Outline → tokens.color.outlineVariant
    else    → "transparent"
}
```
**Баги:** Outline.bg = Solid.bg, Soft.bg = Solid.bg

### SegmentedControlConfig.kt — нет variant
```kotlin
data class SegmentedControlConfig(
    val options: Array<SegmentedControlOption>,
    val selectedId: String,
    val size: ComponentSize = ComponentSize.Sm,
    // ❌ Нет variant: VisualVariant
)
```

### SegmentedControlStyleResolver.kt — фиксированные цвета
```kotlin
// Всегда одинаковые, не зависят от variant:
trackBg = tokens.color.surfaceHover
thumbBg = tokens.color.surface
border = tokens.color.border
```

### React VARIANT_MAP — текущее состояние
```tsx
// Button.tsx
const VARIANT_MAP = { solid: Solid, soft: Soft, outline: Outline, ghost: Ghost }
// ❌ Нет "surface"

// Surface.tsx
const VARIANT_MAP = { solid: Solid, soft: Soft, outline: Outline, ghost: Ghost }
// ❌ Нет "surface"

// IconButton.tsx
const VARIANT_MAP = { solid: Solid, soft: Soft, outline: Outline, ghost: Ghost }
// ❌ Нет "surface"

// SegmentedControl.tsx — нет VARIANT_MAP вообще, нет variant prop
```

### Showcase (second/page.tsx)
```tsx
const BUTTON_VARIANTS = ["solid", "soft", "outline", "ghost"]  // ❌ Нет "surface"
const SURFACE_VARIANTS = ["solid", "soft", "outline"]            // ❌ Нет "ghost", "surface"
// SegmentedControl: нет variant showcase
```

## Technical Decisions

| # | Decision | Rationale |
|---|----------|-----------|
| 1 | Добавляем только `Surface` в enum, НЕ `Glass` | Glass требует GlassTokens, rgba, blur API — это отдельная фаза |
| 2 | Новые color tokens вместо вычисляемых | Дизайнер контролирует точные HEX значения; не зависим от runtime alpha calculations |
| 3 | `borderSubtle` — один токен для всех Surface вариантов | Уменьшает взрыв комбинаторики. Один subtle border для Surface variant всех intent-ов |
| 4 | `primaryBorder`/`primaryBorderHover` — для Surface variant Primary | Surface Primary = primary-tinted fill + primary-tinted border |
| 5 | SegmentedControl поддерживает 3 варианта: Surface, Soft, Outline | Solid и Ghost не имеют смысла для segmented control track |
| 6 | SurfaceAwareColorResolver — рефакторим для нового Soft Primary | Сейчас он использует surface container scale. С `primarySoft` токеном нужно упростить |
| 7 | Compose Button/Surface/IconButton — НЕ МЕНЯЕМ view-файлы | Они принимают VisualVariant enum → Config → Resolver. Новый enum value автоматически обработается |
| 8 | CSS модули НЕ МЕНЯЕМ | Стили задаются через CSS custom properties из resolver. Новый вариант = новые значения переменных |

## Новые токены — конкретные значения

### Light Theme
```kotlin
primarySoft = "#EEF2FF"          // Indigo-50 (intent-тонированный, НЕ серый)
primarySoftHover = "#E0E7FF"     // Indigo-100
neutralSoft = "#F5F5F5"          // Gray-100 (чуть теплее surfaceContainerLow)  
neutralSoftHover = "#E5E5E5"     // Gray-200
borderSubtle = "#E5E7EB"         // Gray-200, для Surface variant
primaryBorder = "#C7D2FE"        // Indigo-200, primary-тонированный border
primaryBorderHover = "#A5B4FC"   // Indigo-300
```

### Dark Theme
```kotlin
primarySoft = "#1E1B4B"          // Indigo-950
primarySoftHover = "#312E81"     // Indigo-900
neutralSoft = "#262626"          // Neutral-800
neutralSoftHover = "#333333"     // Neutral-700  
borderSubtle = "#374151"         // Gray-700
primaryBorder = "#4338CA"        // Indigo-700
primaryBorderHover = "#4F46E5"   // Indigo-600
```

## Контракт вариантов — что должно быть после фикса

```
                Fill        Border      Text color
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SOLID           100%        ❌ transparent  onPrimary/onDanger
SOFT            8-15%       ❌ transparent  intent color
SURFACE         3-10%       ✅ subtle       intent color
OUTLINE         0%          ✅ strong       intent color
GHOST           0%          ❌ transparent  intent color → hover fill 5%
```

## SurfaceAwareColorResolver — план рефакторинга

**Текущая логика `resolveSoftBg()`:**
1. Берёт surface container scale (5 шагов)
2. Для Primary: всегда `surfaceContainerHigh` (или `Highest` если bg == High)
3. Для Neutral: сдвигает на +2 шага от текущего surface bg

**Новая логика:**
1. Для Primary: просто возвращает `tokens.color.primarySoft` (intent-тонированный!)
2. Для Neutral: просто возвращает `tokens.color.neutralSoft`
3. Для Danger: уже работает через `tokens.color.dangerSoft` ← так и оставить

**Альтернативно:** оставить SurfaceAwareColorResolver для edge cases (когда soft bg совпадает с surface bg), но default значение — intent-тонированный.

## Surface StyleResolver — план рефакторинга

**Было:**
```kotlin
val bg = when(variant) {
    Ghost → "transparent"
    else → resolveBg(level, tokens)  // ONE function for Solid, Soft, Outline, Surface
}
```

**Стало:**
```kotlin
val bg = when(variant) {
    Solid   → resolveBg(level, tokens)                    // Полная заливка по level
    Soft    → resolveSoftBg(level, tokens)                // Пониженная (shifted level)
    Surface → resolveSoftBg(level, tokens)                // Такая же лёгкая + border
    Outline → "transparent"                                // ← FIX: было resolveBg!
    Ghost   → "transparent"
}

val border = when(variant) {
    Outline → tokens.color.outlineVariant                 // Сильный border
    Surface → tokens.color.borderSubtle                   // Деликатный border  
    Solid   → "transparent"                                // Опционально: borderSubtle
    Soft    → "transparent"
    Ghost   → "transparent"
}
```

## Issues Encountered

| Issue | Resolution |
|-------|------------|
| (пока нет) | |

## Resources

- Анализ: `docs/VARIANT_SYSTEM_ANALYSIS.md`
- Radix UI Themes: https://www.radix-ui.com/themes/docs/components/button
- Material Design 3: https://m3.material.io/styles/color/static/baseline
- AGENTS.md — архитектурные конвенции проекта
- ROADMAP.md — будущие планы
