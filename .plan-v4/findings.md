# Findings & Decisions — UIKit v4

## Проблема 1: "Выжигающий глаза" индиго-синий

### Текущее состояние цветов (v3)

Добавленные в v3 токены используют **Tailwind Indigo** шкалу:

```
Light Theme:
  primarySoft      = "#EEF2FF"  ← Indigo-50  (голубовато-фиолетовый!)
  primarySoftHover = "#E0E7FF"  ← Indigo-100 
  primaryBorder    = "#C7D2FE"  ← Indigo-200 (ЯРКИЙ синий border!)
  primaryBorderHover = "#A5B4FC" ← Indigo-300 (ЕЩЁ ЯРЧЕ!)

Dark Theme:
  primarySoft      = "#1E1B4B"  ← Indigo-950 (тёмный фиолетовый)
  primarySoftHover = "#312E81"  ← Indigo-900
  primaryBorder    = "#4338CA"  ← Indigo-700 (ЯРКИЙ фиолетовый border!)
  primaryBorderHover = "#4F46E5" ← Indigo-600 (ЕЩЁ ЯРЧЕ!)
```

### Почему это НЕПРАВИЛЬНО

Наш `primary` color = `#1A1A1A` (Light) / `#FFFFFF` (Dark) — это **монохромная** система!
- Primary = чёрный/белый
- Secondary = серый
- Danger = красный

_Единственный цветной accent — danger._

Индиго-синий для soft/border **противоречит** всей палитре. Когда primary = чёрный, soft primary должен быть **серым**, не синим.

### x.ai design system — эталон монохромной палитры

Из анализа https://github.com/VoltAgent/awesome-design-md/tree/main/design-md/x.ai:

```
Background:    #1f2228 (warm near-black, НЕ чистый чёрный)
Text Primary:  #FFFFFF
Text Secondary: rgba(255,255,255, 0.7)
Text Muted:    rgba(255,255,255, 0.5)
Border:        rgba(255,255,255, 0.1)  — еле виден
Border Strong: rgba(255,255,255, 0.2)
Surface:       rgba(255,255,255, 0.03) — barely visible
Surface Hover: rgba(255,255,255, 0.08)
```

Ключевое: **НОЛЬ цветных accent-ов**. Всё через opacity белого на тёмном фоне.

### Наш dark theme vs x.ai

```
                    Наш текущий    x.ai           Проблема
surface:            #000000        #1f2228        Чистый чёрный слишком harsh
surfaceContainerLow: #171717      ~rgba(w,0.03)   OK, близко  
border:             #2A2A2A        rgba(w,0.1)    OK
primarySoft:        #1E1B4B        -              ИНДИГО! Не монохромный
primaryBorder:      #4338CA        -              ЯРКИЙ ИНДИГО!
```

### Новые значения — нейтральная монохромная палитра

#### Light Theme
```kotlin
primarySoft      = "#F0F0F0"   // Neutral-100 (тёплый светло-серый)
primarySoftHover = "#E5E5E5"   // Neutral-200  
neutralSoft      = "#F5F5F5"   // Как было — OK
neutralSoftHover = "#EBEBEB"   // Чуть теплее чем было
borderSubtle     = "#E5E5E5"   // Neutral, как было = OK  
primaryBorder    = "#D4D4D4"   // Neutral-300 (чистый серый, НЕ индиго)
primaryBorderHover = "#A3A3A3" // Neutral-400 
```

#### Dark Theme  
```kotlin
surface          = "#0C0C0E"   // Warm near-black (не #000000!)
primarySoft      = "#1A1A1A"   // Neutral-900 (тёплый тёмно-серый)
primarySoftHover = "#262626"   // Neutral-800
neutralSoft      = "#171717"   // Чуть выше surface
neutralSoftHover = "#262626"   // Neutral-800
borderSubtle     = "#2A2A2A"   // Neutral-800, чуть светлее border
primaryBorder    = "#404040"   // Neutral-700 (нейтральный, НЕ индиго)
primaryBorderHover = "#525252" // Neutral-600
```

### Также обновить dark theme surface шкалу

```kotlin
// Текущие
surface                = "#000000"  → "#0C0C0E"  (warm near-black)
surfaceContainerLowest = "#0A0A0A"  → "#111113"  
surfaceContainerLow    = "#171717"  → "#191919"  
surfaceContainer       = "#1E1E1E"  → "#202022"
surfaceContainerHigh   = "#2B2B2B"  → "#2B2B2D"     
surfaceContainerHighest = "#383838" → "#383839"
background             = "#000000"  → "#0A0A0C"
```

## Проблема 2: Ghost Surface невидим

### Анализ

SurfaceStyleResolver для Ghost:
- `bg = "transparent"`
- `border = "transparent"`
- `bgHover = tokens.color.surfaceHover` (только если clickable || hoverable)

В showcase: `hoverable={mode === "hoverable"}` → в режиме "Default" ghost surface = 100% невидимый (transparent всё).

### Решение

В showcase для ghost variant форсировать `hoverable={true}` + добавить визуальный индикатор (dashed border через CSS class). Это НЕ меняет библиотеку — только showcase.

## Проблема 3: Иконки в SegmentedControl

### Текущая структура

```kotlin
data class SegmentedControlOption(
    val id: String,
    val label: String,
    // ❌ Нет icon
)
```

### Целевая структура (KMP-совместимая)

```kotlin
data class SegmentedControlOption(
    val id: String,
    val label: String,
    val iconId: String? = null,  // Опциональный ID иконки (KMP-safe)
)

data class SegmentedControlConfig(
    // ...existing...
    val iconPosition: IconPosition = IconPosition.None,
    // IconPosition.None → только label
    // IconPosition.Start → icon слева от label
    // IconPosition.End → icon справа от label  
    // IconPosition.Top → icon сверху label
    // IconPosition.Bottom → icon снизу label
)
```

React/Compose: маппинг `iconId → ReactNode` / `iconId → @Composable` через render prop / icons map.

### Layout в option

```
IconPosition.Start:   [icon] [label]    → flexDirection: row
IconPosition.End:     [label] [icon]    → flexDirection: row-reverse
IconPosition.Top:     [icon]            → flexDirection: column
                      [label]
IconPosition.Bottom:  [label]           → flexDirection: column-reverse
                      [icon]
```

### Sizes

```kotlin
data class SegmentedControlSizes(
    // ...existing...
    val iconSize: Double,   // НОВОЕ: из scale.iconSize
    val iconGap: Double,    // НОВОЕ: из scale.iconGap
)
```

## Проблема 4: Глобальные контролы каталога

### Текущее: дублирование

Каждый showcase имеет свой Size переключатель:
- ButtonShowcase → selectedSize
- IconButtonShowcase → selectedSize  
- SurfaceShowcase → нет size (Surface не зависит от size)
- SegmentedControlShowcase → selectedSize
- HeightAlignmentShowcase → selectedSize

### Целевое: единые контролы в top bar

```
┌────────────────────────────────────────────────────────┐
│ ← Назад       [LTR|RTL] [Size: XS SM MD LG XL]      │
│                [Radius: None SM MD LG XL Full]         │
│                [Dark|Light|System]                      │
└────────────────────────────────────────────────────────┘
```

- `globalSize` → передаётся во все showcase через props
- `globalRadius` → передаётся как override для tokens.radius (или как множитель)
- Удаляются локальные size переключатели

### Как работает globalRadius

Вариант A: Override конкретного значения RadiusTokens:
```tsx
// Создаём модифицированные tokens с изменённым radius
const modifiedTokens = { ...tokens, radius: { ...tokens.radius, md: radiusValue } };
```

Вариант B: Множитель (scaleFactor для radius):
```tsx
// radiusScale: 0 (none), 0.5 (sm), 1.0 (md-default), 1.5 (lg), 2.0 (xl)
```

→ **Решение: Вариант B не подходит** (нет radiusScale в tokens). 
→ **Используем React state для конкретного radiusFraction** который прокидываем в showcase, т.к. радиус вычисляется через `height × radiusFraction` из `ControlProportions`.

Самый чистый подход: передавать `radiusFraction` override в `InteractiveControlTokens.proportions`, создавая модифицированные tokens в top-level.
