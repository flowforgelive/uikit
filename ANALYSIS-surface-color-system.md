# Анализ системы Surface → Color адаптации

> Дата: июль 2025
> Scope: UIKit Design System (KMP Common + React + Compose)

---

## 1. Как устроена текущая архитектура

### 1.1. SurfaceContext — механизм пропагации

```
SurfaceContext(level: Int, backgroundColor: String, nestingDepth: Int)
```

| Поле              | Назначение                                              | Пример значений          |
| ----------------- | ------------------------------------------------------- | ------------------------ |
| `level`           | Уровень Surface (0–5), определяет фоновый цвет          | 0 = base, 3 = elevated   |
| `backgroundColor` | Hex-цвет фона текущего Surface                          | `#1C1C1E`, `transparent` |
| `nestingDepth`    | Счётчик вложенных интерактивных компонентов (Soft tone) | 0, 1, 2, 3               |

**Пропагация:**
- React: `<SurfaceContextReact.Provider value={surfaceContext}>` → `useSurfaceContext()`
- Compose: `CompositionLocalProvider(LocalSurfaceContext provides surfaceContext)` → `LocalSurfaceContext.current`

### 1.2. Потребители SurfaceContext

| Компонент        | Использует `level` | Использует `backgroundColor` | Использует `nestingDepth` |
| ---------------- | ------------------ | ---------------------------- | ------------------------- |
| Button           | ❌                  | ❌                            | ✅ (Soft variant)          |
| IconButton       | ❌                  | ❌                            | ✅ (Soft variant)          |
| SegmentedControl | ✅                  | ❌                            | ❌                         |
| Surface (child)  | ✅                  | ✅ (передаёт дальше)          | ✅ (передаёт дальше)       |
| **TextBlock**    | **❌**              | **❌**                        | **❌**                     |
| Divider          | ❌                  | ❌                            | ❌                         |
| Badge            | ❌                  | ❌                            | ❌                         |

### 1.3. InteractiveColorResolver — центральная матрица цветов

Матрица `VisualVariant × ColorIntent → ColorSet`:

| Variant     | Bg source                                           | Text source                                      | Border source                               | Surface-aware?         |
| ----------- | --------------------------------------------------- | ------------------------------------------------ | ------------------------------------------- | ---------------------- |
| **Solid**   | `primary` / `surfaceContainerHighest` / `danger`    | `textOnPrimary` / `textPrimary` / `textOnDanger` | `transparent`                               | ❌                      |
| **Soft**    | `NestingColorStrategy.resolveSoftBg(intent, depth)` | `textPrimary` / `textSecondary` / `danger`       | `transparent`                               | ✅ через `nestingDepth` |
| **Surface** | `primarySoft` / `neutralSoft` / `dangerSoft`        | `textPrimary` / `textSecondary` / `danger`       | `primaryBorder` / `borderSubtle` / `danger` | ❌                      |
| **Outline** | `transparent` → `surfaceContainerLowest` on hover   | `primary` / `textSecondary` / `danger`           | `outline` / `outlineVariant` / `danger`     | ❌                      |
| **Ghost**   | `transparent` → `surfaceContainerLow` on hover      | `primary` / `textSecondary` / `danger`           | `transparent`                               | ❌                      |

### 1.4. NestingColorStrategy — Tonal Staircase

Для Soft варианта каждый уровень вложенности берёт следующий шаг в шкале:

```
depth 0: primarySoft          → primarySoftHover       → primarySoftActive       
depth 1: primarySoftHover     → primarySoftActive      → surfaceContainerHigh    
depth 2: primarySoftActive    → surfaceContainerHigh   → surfaceContainerHighest 
depth 3: surfaceContainerHigh → surfaceContainerHighest → surfaceHover           
```

Шкала из 6 цветов, сдвиг по `nestingDepth` с `coerceIn(0, scale.size - 3)`.

### 1.5. SegmentedControl — уникальная Surface-адаптация

SC адаптирует thumb/track по `level`:
- Track bg: `level == 0` → `neutralSoft`, иначе → `SurfaceLevelResolver.resolveColor(level+1)`
- Thumb bg (dark scheme): `SurfaceLevelResolver.resolveColor((level+4).coerceAtMost(5))`
- На level 0 выглядит как на фоне страницы, на level 3+ — адаптирует контрасты

---

## 2. Выявленные пробелы (Gap Analysis)

### Gap 1: TextBlock не адаптируется к Surface

**Проблема:** `TextBlockStyleResolver.resolve(config, tokens)` — принимает только `config` и `tokens`, без `surfaceContext`. Текст внутри любого Surface всегда получает цвет из глобальных `tokens.color.textPrimary/textSecondary/textMuted`.

**Последствие:** На тёмном Surface level 0 и на светлом Surface level 3 текст одного цвета. Если Surface.bg тёмный, а textPrimary — тоже тёмный → нечитаемость.

**В Button/SegmentedControl проблема решена:** каждый интерактивный компонент вычисляет свой `ColorSet.text` через `InteractiveColorResolver` — текст привязан к фону кнопки, а не к Surface.

### Gap 2: SurfaceContext не несёт foregroundColor

**Проблема:** `SurfaceContext` содержит `backgroundColor`, но не содержит `foregroundColor` (= какой цвет текста контрастен на этом фоне).

**Сравнение:**
| Система         | Несёт foreground в контексте?                                   |
| --------------- | --------------------------------------------------------------- |
| Material 3      | ✅ `on-surface`, `on-surface-variant`, `on-primary`              |
| Radix           | ✅ 12-шаговая шкала: step 11/12 = text на step 1/2 bg            |
| Apple HIG (iOS) | ✅ Semantic `label/secondaryLabel/tertiaryLabel/quaternaryLabel` |
| Adobe Spectrum  | ✅ `gray-50..900` + `background/foreground` пары                 |
| **UIKit (наш)** | **❌** Только `textPrimary/textSecondary` глобально              |

### Gap 3: Отсутствие "on-surface" semantic token пар

В M3 каждый цвет поверхности имеет парный цвет текста:
- `surface` → `on-surface`
- `surface-variant` → `on-surface-variant`
- `primary-container` → `on-primary-container`

У нас: `surfaceContainerLow / surfaceContainer / surfaceContainerHigh / surfaceContainerHighest` существуют, но для них нет `onSurfaceContainer*` токенов.

### Gap 4: Нет поддержки glass/blur/translucency

Ни один компонент в кодовой базе не реализует `backdrop-filter`, `blur`, или glass-эффекты. ROADMAP содержит упоминание `"blur": { "surface": "20px" }` как будущий токен, но реализации нет.

---

## 3. Сравнительная таблица: UIKit vs M3 vs Radix vs Apple vs Spectrum

### 3.1. Токены и организация

| Аспект                     | UIKit (наш)                                                       | Material 3                                           | Radix                               | Apple HIG                            | Adobe Spectrum                   |
| -------------------------- | ----------------------------------------------------------------- | ---------------------------------------------------- | ----------------------------------- | ------------------------------------ | -------------------------------- |
| **Цветовая модель**        | Hex strings, semantic names                                       | HCT (Hue-Chroma-Tone), dynamic                       | 12-step alpha scales per hue        | System colors + dynamic materials    | RGB + 50–900 gray scale          |
| **Surface уровни**         | 6 уровней (0–5) через `SurfaceLevelResolver`                      | 5 containers: LowestLow→Highest                      | Нет формальных уровней, alpha scale | 3 bg уровня (system/grouped) + glass | 2 уровня: `layer-1`, `layer-2`   |
| **Foreground token pairs** | ❌ Отсутствуют                                                     | ✅ `on-*` для каждого `surface-*`                     | ✅ Step 11/12 = text на step 1/2     | ✅ `label/secondaryLabel/...`         | ✅ `foreground-color` per surface |
| **Тональная вложенность**  | ✅ `NestingColorStrategy` (Soft)                                   | ❌ Нет встроенной                                     | ❌ Нет встроенной                    | ❌ Нет встроенной                     | ❌ Нет встроенной                 |
| **Variant матрица**        | ✅ 5×3 (Solid/Soft/Surface/Outline/Ghost × Primary/Neutral/Danger) | 5 типов кнопок (Filled/Tonal/Outlined/Elevated/Text) | Variant + Color per component       | System button styles                 | Variant + StaticColor            |
| **Token format**           | Kotlin `@JsExport data class`                                     | DTCG JSON + code-gen                                 | CSS custom properties               | Swift API + Asset Catalogs           | DTCG + Style Dictionary          |

### 3.2. Surface-aware адаптация

| Аспект                        | UIKit (наш)                       | Material 3                            | Radix                      | Apple HIG                   | Adobe Spectrum                  |
| ----------------------------- | --------------------------------- | ------------------------------------- | -------------------------- | --------------------------- | ------------------------------- |
| **Кнопка на цветном Surface** | ✅ Soft shifts через nesting depth | Partial: elevation overlay            | Manual: pick contrast step | ✅ System auto-adapts in LG  | Manual: variant per context     |
| **Текст на Surface**          | ❌ Всегда глобальный               | ✅ `on-surface` / `on-surface-variant` | ✅ Step 11/12 auto-contrast | ✅ Vibrant labels auto-adapt | ✅ Foreground per layer          |
| **Nested interactive**        | ✅ Tonal staircase (unique!)       | ❌ Не решает                           | ❌ Не решает                | ❌ Не решает                 | ❌ Не решает                     |
| **Dark/Light dual support**   | ✅ Через `DesignTokens` presets    | ✅ Dynamic color scheme                | ✅ `*-dark` scale variants  | ✅ System auto light/dark    | ✅ `darkest/dark/light/lightest` |

### 3.3. Glass / Blur / Translucency

| Аспект                        | UIKit (наш) | Material 3               | Radix | Apple Liquid Glass                       | Adobe Spectrum |
| ----------------------------- | ----------- | ------------------------ | ----- | ---------------------------------------- | -------------- |
| **Glass material**            | ❌ Нет       | ❌ Нет (только elevation) | ❌ Нет | ✅ Liquid Glass: regular + clear          | ❌ Нет          |
| **Background blur**           | ❌ Нет       | ❌ Нет                    | ❌ Нет | ✅ 4 уровня: ultraThin/thin/regular/thick | ❌ Нет          |
| **Vibrancy/Foreground adapt** | ❌ Нет       | ❌ Нет                    | ❌ Нет | ✅ label/secondaryLabel/tertiaryLabel     | ❌ Нет          |
| **Scroll edge effect**        | ❌ Нет       | ❌ Нет                    | ❌ Нет | ✅ Blur + opacity reduce при скролле      | ❌ Нет          |
| **Refraction/specular**       | ❌ Нет       | ❌ Нет                    | ❌ Нет | ✅ Optical glass simulation               | ❌ Нет          |

### 3.4. Accessibility и Contrast гарантии

| Аспект                       | UIKit (наш)           | Material 3                | Radix                    | Apple HIG                       | Adobe Spectrum        |
| ---------------------------- | --------------------- | ------------------------- | ------------------------ | ------------------------------- | --------------------- |
| **Contrast ratio guarantee** | Вручную               | ✅ Tone-based (T80 on T20) | ✅ Steps 11/12 on 1/2     | ✅ Increased contrast mode       | ✅ WCAG AA built-in    |
| **Disabled states**          | ✅ `resolveDisabled()` | ✅ 38% opacity convention  | ✅ Step 8 text, step 3 bg | ✅ System dimmed                 | ✅ `is-disabled` token |
| **Reduced transparency**     | ❌ Нет                 | ❌ Нет                     | ❌ Нет                    | ✅ Respects reduce transparency  | ❌ Нет                 |
| **High contrast mode**       | ❌ Нет                 | ✅ Medium/High contrast    | ✅ Scale swaps available  | ✅ Bold Text + Increase Contrast | ✅ Large/medium scale  |

---

## 4. Apple Liquid Glass: Глубокий анализ

### 4.1. Что такое Liquid Glass

Liquid Glass — новый материал Apple (WWDC 2025, обновлён Sept 2025), который:
- **Объединяет дизайн-язык** всех Apple-платформ (iOS, macOS, tvOS, watchOS, visionOS)
- **Создаёт функциональный слой** для контролов и навигации, плавающий над контентом
- **Позволяет контенту просвечивать** через навигационные элементы
- **Адаптируется** к фону, освещению, системным настройкам

### 4.2. Оптические свойства

```
┌──────────────────────────────────────────────────┐
│               LIQUID GLASS LAYER                 │
│                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ Refraction│  │Blur Layer│  │ Specular │      │
│  │ (искажает│  │(размытие │  │ Highlight │      │
│  │ фон за   │  │ фона за  │  │ (блик    │      │
│  │ стеклом) │  │ стеклом) │  │ сверху)  │      │
│  └──────────┘  └──────────┘  └──────────┘      │
│                                                  │
│  Adaptive tint: стекло окрашивается              │
│  цветами контента, который за ним                │
└──────────────────────────────────────────────────┘
                 ↕ content scrolls beneath
┌──────────────────────────────────────────────────┐
│               CONTENT LAYER                      │
│  (списки, карточки, медиа, текст)                │
└──────────────────────────────────────────────────┘
```

### 4.3. Два варианта Liquid Glass

| Свойство       | Regular                                     | Clear                                      |
| -------------- | ------------------------------------------- | ------------------------------------------ |
| Прозрачность   | Умеренная, с blur                           | Высокая, максимально просвечивает          |
| Legibility     | ✅ Blur + luminosity для текста/контролов    | ⚠️ Нужен dimming layer для яркого фона      |
| Scroll edge    | ✅ Blur + opacity reduction                  | ❌                                          |
| Use case       | Tab bars, sidebars, alerts, popovers        | Компоненты над фото/видео (media controls) |
| Adaptive to bg | Темнеет на тёмном, светлеет на светлом фоне | Следует за цветами фона                    |

### 4.4. Эволюция blur в Apple (хронология)

| Год       | Событие                                                            |
| --------- | ------------------------------------------------------------------ |
| 2013      | iOS 7: `UIBlurEffect` + `UIVibrancyEffect` — первый системный blur |
| 2014      | Yosemite: `NSVisualEffectView` с blending modes                    |
| 2019      | Dark Mode: blur адаптируется к appearance                          |
| 2023      | visionOS: Glass material — адаптивный к физическому окружению      |
| 2025      | **Liquid Glass** (WWDC25): unified material across all platforms   |
| 2025 Sept | **Обновление LG**: уточнённые guidelines, scroll edge effect       |

### 4.5. Как Apple решает "что за кнопка" (legibility through glass)

1. **Luminosity adjustment**: Regular LG автоматически регулирует яркость blur-зоны
2. **Scroll edge effect**: при скролле контент под glass дополнительно размывается и теряет opacity
3. **Vibrant colors**: специальные `UIVibrancyEffectStyle` — label/secondaryLabel/tertiaryLabel — foreground элементы "тянут" цвет из фона, создавая контраст
4. **Adaptive light/dark**: стекло на тёмном фоне становится темнее (с светлыми бликами), на светлом — светлее
5. **Colored tint**: accent color применяется к bg стекла (как `Done` кнопка = blue glass), НО sparingly — только для primary action
6. **Dimming layer**: для Clear варианта над ярким контентом добавляется тёмный слой 35% opacity

### 4.6. Реализация glass-эффекта на web (CSS)

Apple нативно использует SwiftUI `glassEffect(_:in:)`, но для web аналог:

```css
.liquid-glass-regular {
  /* Blur фона */
  backdrop-filter: blur(20px) saturate(1.8);
  -webkit-backdrop-filter: blur(20px) saturate(1.8);
  
  /* Полупрозрачный фон с adaptive tint */
  background: rgba(255, 255, 255, 0.15); /* light mode */
  /* background: rgba(0, 0, 0, 0.25); dark mode */
  
  /* Тонкий border для "edge" стекла */
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  /* Specular highlight (верхний блик) */
  box-shadow: 
    inset 0 1px 0 rgba(255, 255, 255, 0.3),
    0 4px 16px rgba(0, 0, 0, 0.1);
  
  /* Скруглённые углы (concentric с содержимым) */
  border-radius: 22px;
  
  transition: background 0.3s ease, backdrop-filter 0.3s ease;
}

.liquid-glass-clear {
  backdrop-filter: blur(8px) saturate(1.2);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
```

---

## 5. Наблюдения из landing.pen дизайнов

### 5.1. Паттерны Surface Nesting в Mealance

Из анализа 19 экранов мобильного приложения Mealance (dark theme, calorie tracker):

```
Level 0: Тёмный фон (#0A0A0C или similar)
  └── Level 1: Карточки (#1C1C1E), rounded corners ~14px
       └── Level 2: Вложенные элементы (progress bars, tags, icons)
```

### 5.2. Конкретные паттерны

| Экран           | Surface pattern                            | Adaptive elements             |
| --------------- | ------------------------------------------ | ----------------------------- |
| Home (Activity) | Dark bg → Gray cards → Nutrient bars       | Button primary на card        |
| Add Meal        | Dark bg → Input fields (soft bg) → Sliders | SegmentedControl на card      |
| Food Search     | Dark bg → SegmentedControl → Product cards | Chips на Search surface       |
| Barcode Scanner | Camera bg → Glass-like overlay controls    | **Glass-effect нужен здесь!** |
| Product Detail  | Dark bg → Hero image → Info cards          | Text adaptation по contrast   |

### 5.3. Где Glass/Blur нужен в дизайне

1. **Bottom Tab Bar** — "ПИТАНИЕ" / "ИСТОРИЯ" + FAB `+` — плавает над контентом, просвечивает
2. **Camera controls** — overlay кнопки над камерой (barcode scan) — нужен clear glass
3. **Status bar area** — верхняя область может blur-ить content при скролле
4. **Action sheets / Modals** — если будут, нуждаются в glass-backdrop

---

## 6. Рекомендации по развитию

### 6.1. Приоритет 1: Text Surface-Awareness

**Изменение:** Добавить `surfaceContext` в `TextBlockStyleResolver.resolve()`.

**Подход А — Минимальный (рекомендуемый):**
```kotlin
// SurfaceContext.kt — добавить foregroundColor
data class SurfaceContext(
    val level: Int,
    val backgroundColor: String,
    val nestingDepth: Int = 0,
    val foregroundColor: String = "",  // NEW: "" = use token defaults
)

// TextBlockStyleResolver.kt — опциональный surfaceContext
fun resolve(
    config: TextBlockConfig,
    tokens: DesignTokens,
    surfaceContext: SurfaceContext? = null, // NEW
): ResolvedTextBlockStyle {
    val typography = resolveTypography(config.variant, tokens)
    val color = if (surfaceContext?.foregroundColor?.isNotEmpty() == true) {
        resolveSurfaceAwareColor(config.emphasis, config.variant, surfaceContext, tokens)
    } else {
        resolveEmphasisColor(config.emphasis, config.variant, tokens)
    }
    return fromTextStyle(typography, color)
}
```

**Подход Б — on-surface tokens:**
```kotlin
// ColorTokens.kt — добавить пары
val onSurface: String            // текст на surface
val onSurfaceVariant: String     // secondary текст на surface
val onSurfaceMuted: String       // muted текст на surface
```

Рекомендация: **Подход А** — backwards-compatible, не требует новых токенов, SurfaceStyleResolver вычисляет foregroundColor при создании контекста.

### 6.2. Приоритет 2: Glass Material Support

**Новый примитив: `GlassSurface`**

Два слоя: Surface (существующий, solid bg) → GlassSurface (новый, blur bg).

```kotlin
// GlassSurfaceConfig.kt
@JsExport @Serializable
data class GlassSurfaceConfig(
    val variant: GlassVariant = GlassVariant.Regular,  // Regular | Clear
    val tintColor: String = "",  // Accent tint, "" = auto from bg
    val blurRadius: Double = 20.0,  // dp
    val opacity: Double = 0.15,  // bg alpha
    val borderOpacity: Double = 0.2,
    // ...standard fields
)

enum class GlassVariant { Regular, Clear }
```

**React реализация:**
```css
/* GlassSurface.module.css */
.glass {
  backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
  -webkit-backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--glass-radius);
}
```

**Compose реализация:**
```kotlin
// Require Android 12+ (API 31) for RenderEffect.createBlurEffect
// или Modifier.blur() из accompanist/coil
```

### 6.3. Приоритет 3: DTCG Token Format + on-* pairs

Добавить формальные on-* пары для всех surface levels:

```json
{
  "color": {
    "surface": {
      "container-low":   { "$value": "#1C1C1E" },
      "on-container-low": { "$value": "#F5F5F5" }
    }
  }
}
```

### 6.4. Приоритет 4: Reduced Transparency / High Contrast

Для accessibility:
- Если `prefers-reduced-transparency` — glass → solid bg с matched color
- Если high contrast — увеличить непрозрачность glass, усилить border

---

## 7. Матрица затронутых файлов для реализации

### Text Surface-Awareness (Приоритет 1)

| Файл                                         | Изменение                                      |
| -------------------------------------------- | ---------------------------------------------- |
| `common/.../SurfaceContext.kt`               | Добавить `foregroundColor: String = ""`        |
| `common/.../text/TextBlockStyleResolver.kt`  | Добавить `surfaceContext?` param               |
| `common/.../text/TextBlockConfig.kt`         | Без изменений                                  |
| `react/.../text/TextBlockView.tsx`           | Использовать `useResolvedStyle` (уже есть хук) |
| `compose/.../text/TextBlockView.kt`          | Добавить `LocalSurfaceContext.current`         |
| `common/.../surface/SurfaceStyleResolver.kt` | Вычислять `foregroundColor` из bg brightness   |

### Glass Material (Приоритет 2)

| Файл                                                             | Изменение                   |
| ---------------------------------------------------------------- | --------------------------- |
| `common/.../primitives/glass/GlassSurfaceConfig.kt`              | Новый (config)              |
| `common/.../primitives/glass/GlassSurfaceStyleResolver.kt`       | Новый (resolver)            |
| `react/.../primitives/glass-surface/GlassSurface.tsx`            | Новый                       |
| `react/.../primitives/glass-surface/GlassSurfaceView.tsx`        | Новый                       |
| `react/.../primitives/glass-surface/GlassSurfaceView.module.css` | Новый                       |
| `compose/.../primitives/glass/GlassSurface.kt`                   | Новый                       |
| `compose/.../primitives/glass/GlassSurfaceView.kt`               | Новый                       |
| `common/.../tokens/DesignTokens.kt`                              | Добавить `blur` token group |

---

## 8. Ключевые выводы

### Что у нас уже хорошо (уникальные преимущества)

1. **Tonal Staircase** (`NestingColorStrategy`) — ни одна из проанализированных дизайн-систем не имеет встроенного решения для nested interactive soft colors
2. **5×3 Variant Matrix** — полная матрица VisualVariant × ColorIntent с hover/active состояниями
3. **Cross-platform consistency** — KMP StyleResolvers гарантируют одинаковый результат на React и Compose
4. **SSR-safe architecture** — все resolvers stateless, pure, deterministic

### Что нужно добавить (в порядке приоритета)

| #   | Что                           | Влияние                     | Сложность          |
| --- | ----------------------------- | --------------------------- | ------------------ |
| 1   | Text surface-awareness        | Критично для читаемости     | Низкая (1-2 дня)   |
| 2   | `foregroundColor` в SC        | Фундамент для #1            | Низкая (часы)      |
| 3   | Glass/Blur material           | Тренд 2025+, дизайн требует | Средняя (3-5 дней) |
| 4   | on-surface token pairs        | Formal contract для #1      | Низкая (1 день)    |
| 5   | Reduced transparency fallback | a11y compliance             | Низкая (1 день)    |
| 6   | DTCG format export            | Industry standard           | Средняя (3 дня)    |

### Что НЕ нужно делать

- ❌ Менять `InteractiveColorResolver` — он работает отлично
- ❌ Добавлять Material 3 HCT — наша hex-модель проще и кросс-платформенна
- ❌ Копировать Radix 12-step scale — overkill для нашей архитектуры
- ❌ Реализовывать полный Liquid Glass (refraction/specular) — CSS `backdrop-filter` + tint достаточно для web
