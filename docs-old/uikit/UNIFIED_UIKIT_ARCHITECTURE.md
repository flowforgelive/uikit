# Unified UIKit Architecture: 5000+ приложений, один стандарт

> Config-Driven + SDUI/BDUI + Design Tokens — объединённая архитектура  
> Android (Compose) + Web (React) · Максимальная производительность · Опциональные слои

---

## Содержание

1. [Главная идея](#1-главная-идея)
2. [Архитектура слоёного пирога](#2-архитектура-слоёного-пирога)
3. [Layer 0: Design Tokens — единый визуальный язык](#3-layer-0-design-tokens--единый-визуальный-язык)
4. [Layer 1: Component Primitives — стандартизированный набор](#4-layer-1-component-primitives--стандартизированный-набор)
5. [Layer 2: Config-Driven Engine — offline-first логика](#5-layer-2-config-driven-engine--offline-first-логика)
6. [Layer 3: SDUI/BDUI Engine — серверное управление (опционально)](#6-layer-3-sduibdui-engine--серверное-управление-опционально)
7. [Layer 4: Platform Renderers — нативный перфоманс](#7-layer-4-platform-renderers--нативный-перфоманс)
8. [Режимы работы: от static до full SDUI](#8-режимы-работы-от-static-до-full-sdui)
9. [Унифицированная JSON Schema](#9-унифицированная-json-schema)
10. [Component Registry — O(1) рендеринг](#10-component-registry--o1-рендеринг)
11. [Полный пример: экран каталога товаров](#11-полный-пример-экран-каталога-товаров)
12. [Перфоманс-оптимизации](#12-перфоманс-оптимизации)
13. [Multi-brand за 1 день](#13-multi-brand-за-1-день)
14. [Структура проекта](#14-структура-проекта)
15. [Метрики и сравнение](#15-метрики-и-сравнение)
16. [Источники и вдохновение](#16-источники-и-вдохновение)

---

## 1. Главная идея

### Проблема

5000+ приложений — каждое со своим дизайном, но:
- 90% компонентов одинаковы (кнопки, карточки, формы, списки, навигация)
- 90% логики одинакова (валидация, state-management, действия)
- Различается только **внешний вид** (бренд) и **где/какие компоненты стоят** (layout)

### Решение: Unified UIKit с опциональными слоями

```
╔══════════════════════════════════════════════════════════════════════╗
║                                                                      ║
║   Не "или Config-Driven, или SDUI" —                                ║
║   а ОДИН движок, где SDUI — это надстройка над Config-Driven.       ║
║                                                                      ║
║   Приложение САМО выбирает режим:                                    ║
║   • Static: Config зашит в код — zero latency                       ║
║   • Hybrid: Config из кэша, фоновый sync с сервером                 ║
║   • Full SDUI: Layout + Configs с сервера в реал-тайме              ║
║                                                                      ║
║   Компоненты, токены, рендереры — ОДНИ И ТЕ ЖЕ во всех режимах.    ║
║                                                                      ║
╚══════════════════════════════════════════════════════════════════════╝
```

### Ключевой принцип

> **Config — это единица UI.** Неважно, откуда он пришёл (зашит в код, загружен с сервера, собран динамически) — рендерер всегда получает Config и рисует нативный компонент.

---

## 2. Архитектура слоёного пирога

```
┌───────────────────────────────────────────────────────────────────┐
│                        App (5000+ приложений)                     │
│  Выбирает режим: Static / Hybrid / Full SDUI                     │
├───────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Layer 4: PLATFORM RENDERERS (нативные, тонкие)                  │
│  ┌──────────────────────┐  ┌──────────────────────┐              │
│  │ Android (Compose)     │  │ Web (React)           │              │
│  │ ButtonView()          │  │ <ButtonView />        │              │
│  │ CardView()            │  │ <CardView />          │              │
│  │ ListSectionView()     │  │ <ListSectionView />   │              │
│  └──────────────────────┘  └──────────────────────┘              │
│                          ▲                                        │
│                          │ Config                                │
│  ┌───────────────────────┴───────────────────────────────────┐   │
│  │ Layer 3: SDUI/BDUI ENGINE (опциональный)                   │   │
│  │ Получает Layout + Section[] с сервера                      │   │
│  │ Маппит section.type → ComponentConfig                      │   │
│  │ Управляет Layout, Placement, Actions                       │   │
│  │ ⚡ МОЖЕТ БЫТЬ ВЫКЛЮЧЕН — Config-Driven работает без него  │   │
│  └───────────────────────┬───────────────────────────────────┘   │
│                          │                                        │
│  ┌───────────────────────┴───────────────────────────────────┐   │
│  │ Layer 2: CONFIG-DRIVEN ENGINE (ядро)                       │   │
│  │ ComponentConfig data classes (Kotlin Multiplatform)        │   │
│  │ Style resolution: Config × Tokens → ResolvedStyle         │   │
│  │ ViewModel / State Machine (StateFlow)                      │   │
│  │ Action dispatching (onClick, navigate, validate)           │   │
│  └───────────────────────┬───────────────────────────────────┘   │
│                          │                                        │
│  ┌───────────────────────┴───────────────────────────────────┐   │
│  │ Layer 1: COMPONENT PRIMITIVES (стандартный набор)           │   │
│  │ 50-70 компонентов: Button, Card, TextField, List, ...     │   │
│  │ Каждый = Config + Style + Renderer(Android) + Renderer(Web)│   │
│  └───────────────────────┬───────────────────────────────────┘   │
│                          │                                        │
│  ┌───────────────────────┴───────────────────────────────────┐   │
│  │ Layer 0: DESIGN TOKENS (фундамент)                         │   │
│  │ tokens.json → Kotlin + TypeScript + CSS                    │   │
│  │ Brand override через JSON merge                            │   │
│  └───────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

**Почему это работает для 5000+ приложений:**
- Layer 0-2: общие для ВСЕХ приложений (npm/maven пакеты)
- Layer 3: подключается ТОЛЬКО если нужен SDUI
- Layer 4: тонкие рендереры, одинаковые везде
- Различия между приложениями = **только JSON** (токены бренда + конфиги экранов)

---

## 3. Layer 0: Design Tokens — единый визуальный язык

### Структура токенов

```
tokens/
├── foundation.json        ← примитивы (цвета, шрифты, отступы, radii)
├── semantic.json           ← семантические (primary, danger, surface, text)
├── component.json          ← компонентные (button.height.md, card.radius)
└── brands/
    ├── default.json        ← базовый бренд (UIKit default)
    ├── brand-alpha.json    ← переопределения для Brand Alpha
    ├── brand-beta.json     ← переопределения для Brand Beta
    └── ...                 ← 5000 файлов = 5000 брендов
```

### Принцип: 3-tier token resolution

```
foundation.json          semantic.json              component.json
───────────────          ─────────────              ──────────────
blue.500: #3B82F6   →   primary: {blue.500}    →   button.primary.bg: {primary}
blue.600: #2563EB   →   primaryHover: {blue.600}→  button.primary.bg.hover: {primaryHover}
gray.50:  #F9FAFB   →   surface: {gray.50}      →  card.bg: {surface}
gray.900: #111827   →   textPrimary: {gray.900}  → button.text: {textOnPrimary}
```

### Brand override (deep merge)

```json
// brands/brand-alpha.json — переопределяет ТОЛЬКО то, что отличается
{
  "foundation": {
    "blue.500": "#6366F1",
    "blue.600": "#4F46E5"
  },
  "semantic": {
    "radius.default": "16px"
  }
}
// Всё остальное наследуется из default.json
```

### Генерация (одна команда)

```bash
# Генерирует Kotlin + TypeScript + CSS для конкретного бренда
uikit-codegen --brand=brand-alpha --output=generated/
```

**Kotlin (auto-generated):**
```kotlin
// generated/DesignTokens.kt
@Immutable
data class DesignTokens(
    val color: ColorTokens,
    val spacing: SpacingTokens,
    val radius: RadiusTokens,
    val typography: TypographyTokens,
    val sizing: SizingTokens,
    val elevation: ElevationTokens,
    val motion: MotionTokens,
) {
    companion object {
        val Default = DesignTokens(/* default values */)
        fun fromJson(json: String): DesignTokens = /* parse */
    }
}
```

**TypeScript (auto-generated):**
```typescript
// generated/tokens.ts
export interface DesignTokens { /* mirror of Kotlin */ }
export const defaultTokens: DesignTokens = { /* values */ };
```

**CSS Custom Properties (auto-generated):**
```css
:root[data-brand="alpha"] {
  --color-primary: #6366F1;
  --color-primary-hover: #4F46E5;
  --radius-default: 16px;
  /* остальное наследуется из :root */
}
```

### Runtime token switching (zero rebuild)

```kotlin
// Android — смена бренда в runtime
val tokens = DesignTokens.fromJson(brandConfigJson)
CompositionLocalProvider(LocalDesignTokens provides tokens) {
    App()
}
```

```typescript
// React — смена бренда в runtime
<DesignTokensProvider tokens={loadBrandTokens(brandId)}>
  <App />
</DesignTokensProvider>
```

---

## 4. Layer 1: Component Primitives — стандартизированный набор

### Каталог компонентов (стандарт UIKit)

Каждый компонент = конечная единица, не зависит от контекста экрана.

| #   | Компонент       | Тип        | Варианты                                             |
| --- | --------------- | ---------- | ---------------------------------------------------- |
| 1   | **Button**      | Action     | Primary, Secondary, Ghost, Danger, Link / Sm, Md, Lg |
| 2   | **IconButton**  | Action     | Filled, Outlined, Ghost / Sm, Md, Lg                 |
| 3   | **TextField**   | Input      | Default, Search, Password, Multiline                 |
| 4   | **Select**      | Input      | Single, Multi, Searchable                            |
| 5   | **Checkbox**    | Input      | Default, Indeterminate                               |
| 6   | **Radio**       | Input      | Default, Card                                        |
| 7   | **Toggle**      | Input      | Default, WithLabel                                   |
| 8   | **Slider**      | Input      | Single, Range                                        |
| 9   | **DatePicker**  | Input      | Date, DateTime, DateRange                            |
| 10  | **Card**        | Container  | Default, Elevated, Outlined                          |
| 11  | **ListItem**    | Container  | Default, WithIcon, WithAction, WithSwipe             |
| 12  | **Accordion**   | Container  | Single, Multi                                        |
| 13  | **Tabs**        | Navigation | Default, Scrollable, Pill                            |
| 14  | **BottomSheet** | Overlay    | Fixed, Expandable                                    |
| 15  | **Dialog**      | Overlay    | Alert, Confirm, Custom                               |
| 16  | **Snackbar**    | Feedback   | Info, Success, Warning, Error                        |
| 17  | **Banner**      | Feedback   | Info, Success, Warning, Error, Dismissible           |
| 18  | **Badge**       | Indicator  | Numeric, Dot                                         |
| 19  | **Chip**        | Indicator  | Selectable, Dismissible, Info                        |
| 20  | **Avatar**      | Display    | Image, Initials, Icon / Sm, Md, Lg                   |
| 21  | **Image**       | Display    | Default, Async, Placeholder                          |
| 22  | **Divider**     | Display    | Horizontal, Vertical                                 |
| 23  | **Skeleton**    | Display    | Text, Circle, Rectangle, Custom                      |
| 24  | **ProgressBar** | Feedback   | Linear, Circular, Determinate, Indeterminate         |
| 25  | **Tag**         | Indicator  | Default, Closable, Colored                           |

> 25 базовых примитивов покрывают ~95% UI для типовых приложений.  
> Каталог расширяется централизованно через UIKit team.

### Анатомия одного компонента (на примере Button)

```
KButton (commonMain — Kotlin)
├── Config         ← immutable data class, определяет ЧТО показывать
├── StyleResolver  ← pure function: Config × Tokens → ResolvedStyle  
├── Variant enum   ← Primary | Secondary | Ghost | Danger | Link
└── Size enum      ← Sm | Md | Lg

ButtonView.kt (Android)
└── @Composable fun ButtonView(config: KButton.Config)
    ← тонкая обёртка, ~30 строк, только рендеринг

ButtonView.tsx (React)
└── const ButtonView: FC<{ config: ButtonConfig }>
    ← тонкая обёртка, ~30 строк, только рендеринг
```

---

## 5. Layer 2: Config-Driven Engine — offline-first логика

### ComponentConfig — единая валюта UI

```kotlin
// commonMain (KMP) — shared между Android и Web

/**
 * Каждый компонент в UIKit описывается через sealed interface.
 * Config = полное описание того, ЧТО рендерить.
 * Config — Serializable, можно передавать через сеть, хранить, кэшировать.
 */
@Serializable
sealed interface ComponentConfig {
    val id: String                    // уникальный ID для reconciliation
    val testTag: String?              // для авто-тестов
    val visibility: Visibility        // visible | gone | invisible
    val accessibilityLabel: String?   // a11y
}

@Serializable
enum class Visibility { Visible, Gone, Invisible }
```

### Пример: Button Config

```kotlin
@Serializable
@Immutable
data class ButtonConfig(
    override val id: String,
    override val testTag: String? = null,
    override val visibility: Visibility = Visibility.Visible,
    override val accessibilityLabel: String? = null,

    val text: String,
    val variant: Variant = Variant.Primary,
    val size: Size = Size.Md,
    val disabled: Boolean = false,
    val loading: Boolean = false,
    val icon: IconRef? = null,
    val iconPosition: IconPosition = IconPosition.Leading,
    val action: ActionConfig? = null,  // ← что делать при клике
) : ComponentConfig {

    enum class Variant { Primary, Secondary, Ghost, Danger, Link }
    enum class Size { Sm, Md, Lg }
    enum class IconPosition { Leading, Trailing, Only }

    val isInteractive: Boolean get() = !disabled && !loading
}
```

### ActionConfig — стандартизированные действия

```kotlin
@Serializable
sealed interface ActionConfig {
    /** Навигация по роуту */
    @Serializable
    data class Navigate(
        val route: String,
        val params: Map<String, String> = emptyMap(),
        val presentation: Presentation = Presentation.Push,
    ) : ActionConfig {
        enum class Presentation { Push, Modal, Replace, Back }
    }

    /** HTTP вызов */
    @Serializable
    data class ApiCall(
        val method: String,      // GET, POST, PUT, DELETE
        val path: String,        // относительный путь
        val body: JsonElement? = null,
        val onSuccess: ActionConfig? = null,
        val onError: ActionConfig? = null,
    ) : ActionConfig

    /** Обновление state */
    @Serializable
    data class UpdateState(
        val targetId: String,    // id компонента для обновления
        val patch: JsonElement,  // partial config update
    ) : ActionConfig

    /** Показать snackbar/toast */
    @Serializable
    data class ShowFeedback(
        val message: String,
        val variant: FeedbackVariant = FeedbackVariant.Info,
    ) : ActionConfig

    /** Цепочка действий */
    @Serializable
    data class Sequence(val actions: List<ActionConfig>) : ActionConfig

    /** Параллельные действия */
    @Serializable
    data class Parallel(val actions: List<ActionConfig>) : ActionConfig

    /** Кастомное действие (расширяемость) */
    @Serializable
    data class Custom(
        val type: String,
        val payload: JsonElement? = null,
    ) : ActionConfig
}
```

### Style Resolution — pure function, zero allocation на hot path

```kotlin
object ButtonStyleResolver {
    /**
     * Pure function — без side effects, без allocation на hot path.
     * Результат кэшируется через Compose @Stable / React useMemo.
     */
    fun resolve(config: ButtonConfig, tokens: DesignTokens): ResolvedButtonStyle {
        val colorSet = when {
            config.disabled -> ColorSet(
                bg = tokens.color.surfaceDisabled,
                text = tokens.color.textDisabled,
                border = tokens.color.borderDisabled,
            )
            else -> when (config.variant) {
                ButtonConfig.Variant.Primary -> ColorSet(
                    bg = tokens.color.primary,
                    text = tokens.color.textOnPrimary,
                    border = Color.Transparent,
                )
                ButtonConfig.Variant.Secondary -> ColorSet(
                    bg = tokens.color.surface,
                    text = tokens.color.primary,
                    border = tokens.color.primary,
                )
                ButtonConfig.Variant.Ghost -> ColorSet(
                    bg = Color.Transparent,
                    text = tokens.color.primary,
                    border = Color.Transparent,
                )
                ButtonConfig.Variant.Danger -> ColorSet(
                    bg = tokens.color.danger,
                    text = tokens.color.textOnDanger,
                    border = Color.Transparent,
                )
                ButtonConfig.Variant.Link -> ColorSet(
                    bg = Color.Transparent,
                    text = tokens.color.primary,
                    border = Color.Transparent,
                )
            }
        }

        val sizeSet = when (config.size) {
            ButtonConfig.Size.Sm -> SizeSet(
                height = tokens.sizing.buttonSm,
                paddingH = tokens.spacing.sm,
                fontSize = tokens.typography.bodySm,
                iconSize = tokens.sizing.iconSm,
            )
            ButtonConfig.Size.Md -> SizeSet(
                height = tokens.sizing.buttonMd,
                paddingH = tokens.spacing.md,
                fontSize = tokens.typography.bodyMd,
                iconSize = tokens.sizing.iconMd,
            )
            ButtonConfig.Size.Lg -> SizeSet(
                height = tokens.sizing.buttonLg,
                paddingH = tokens.spacing.lg,
                fontSize = tokens.typography.bodyLg,
                iconSize = tokens.sizing.iconLg,
            )
        }

        return ResolvedButtonStyle(colorSet, sizeSet, tokens.radius.md)
    }
}

@Immutable
data class ResolvedButtonStyle(
    val colors: ColorSet,
    val sizes: SizeSet,
    val radius: Int,
)
```

### ScreenConfig — описание целого экрана

```kotlin
/**
 * Экран = Layout + список Sections.
 * Одинаковый формат для Static mode и SDUI mode.
 */
@Serializable
data class ScreenConfig(
    val id: String,
    val layout: LayoutConfig,
    val sections: List<SectionConfig>,
    val toolbar: ToolbarConfig? = null,
    val fab: ButtonConfig? = null,
)

@Serializable
sealed interface LayoutConfig {
    /** Вертикальный скролл (самый частый) */
    @Serializable
    data class SingleColumn(
        val topPlacement: List<String> = emptyList(),    // section IDs
        val mainPlacement: List<String> = emptyList(),   // scrollable
        val bottomPlacement: List<String> = emptyList(), // sticky footer
    ) : LayoutConfig

    /** Двухколоночный (desktop web, tablet) */
    @Serializable
    data class TwoColumn(
        val leftPlacement: List<String> = emptyList(),
        val rightPlacement: List<String> = emptyList(),
        val leftRatio: Float = 0.5f,
    ) : LayoutConfig

    /** Таб-based */
    @Serializable
    data class Tabbed(
        val tabs: List<TabConfig>,
    ) : LayoutConfig

    /** Grid */
    @Serializable
    data class Grid(
        val columns: Int,
        val items: List<String> = emptyList(), // section IDs
    ) : LayoutConfig
}

@Serializable
data class SectionConfig(
    val id: String,
    val type: String,            // ← ключ в ComponentRegistry
    val component: ComponentConfig,
    val padding: SpacingConfig? = null,
    val background: String? = null,
)
```

---

## 6. Layer 3: SDUI/BDUI Engine — серверное управление (опционально)

### Когда подключать SDUI

| Сценарий                          |        SDUI нужен?        |
| --------------------------------- | :-----------------------: |
| Статический профиль, настройки    |       ❌ Static mode       |
| Каталог товаров с A/B тестами     |        ✅ Full SDUI        |
| Онбординг с сегментацией          |        ✅ Full SDUI        |
| Чат / мессенджер в реал-тайме     | ❌ Static mode + WebSocket |
| Контент-платформа (новости, блог) |       ✅ Hybrid SDUI       |
| Формы с серверной валидацией      |         ⚡ Hybrid          |
| Лендинг / маркетинг               |        ✅ Full SDUI        |
| Административная панель           |       ❌ Static mode       |

### SDUI Response — расширение Airbnb Ghost Platform

```kotlin
/**
 * Ответ SDUI-сервера.
 * Обратите внимание: sections содержат те же ComponentConfig,
 * что используются в Static mode. ОДИН формат — два режима.
 */
@Serializable
data class SDUIResponse(
    val screen: ScreenConfig,
    val version: Int,                          // для кэширования
    val ttl: Long = 300_000,                   // cache TTL в мс (5 мин по умолчанию)
    val actions: Map<String, ActionConfig> = emptyMap(), // глобальные действия
    val variables: Map<String, JsonElement> = emptyMap(), // runtime переменные
)
```

### SDUI Client — кэширование + offline fallback

```kotlin
class SDUIClient(
    private val httpClient: HttpClient,
    private val cache: SDUICache,           // LRU + disk cache
    private val fallbackProvider: FallbackProvider, // зашитые fallback-экраны
) {
    /**
     * Стратегия: Cache-First, Network-Refresh.
     * 1. Показываем из кэша МГНОВЕННО
     * 2. В фоне загружаем свежую версию
     * 3. Если изменилось — обновляем UI (animated diff)
     * 4. Если офлайн — кэш, если кэша нет — fallback
     */
    fun getScreen(screenId: String): Flow<ScreenConfig> = flow {
        // 1. Instant cache hit
        cache.get(screenId)?.let { emit(it.screen) }

        // 2. Network refresh
        try {
            val response = httpClient.get<SDUIResponse>("/sdui/screens/$screenId")
            cache.put(screenId, response)
            emit(response.screen)
        } catch (e: Exception) {
            // 3. Fallback
            if (cache.get(screenId) == null) {
                emit(fallbackProvider.getScreen(screenId))
            }
        }
    }
}
```

### Action Router — обработка серверных действий

```kotlin
class ActionRouter(
    private val navigator: Navigator,
    private val apiClient: ApiClient,
    private val stateManager: ScreenStateManager,
    private val feedbackManager: FeedbackManager,
    private val customHandlers: Map<String, CustomActionHandler> = emptyMap(),
) {
    suspend fun dispatch(action: ActionConfig) {
        when (action) {
            is ActionConfig.Navigate -> {
                navigator.navigate(action.route, action.params, action.presentation)
            }
            is ActionConfig.ApiCall -> {
                try {
                    val result = apiClient.call(action.method, action.path, action.body)
                    action.onSuccess?.let { dispatch(it) }
                } catch (e: Exception) {
                    action.onError?.let { dispatch(it) }
                }
            }
            is ActionConfig.UpdateState -> {
                stateManager.patchComponent(action.targetId, action.patch)
            }
            is ActionConfig.ShowFeedback -> {
                feedbackManager.show(action.message, action.variant)
            }
            is ActionConfig.Sequence -> {
                action.actions.forEach { dispatch(it) }  // последовательно
            }
            is ActionConfig.Parallel -> {
                coroutineScope {
                    action.actions.map { async { dispatch(it) } }.awaitAll()
                }
            }
            is ActionConfig.Custom -> {
                customHandlers[action.type]?.handle(action.payload)
            }
        }
    }
}
```

### DivKit — reference implementation

> **Yandex DivKit** — open-source SDUI фреймворк для Android, iOS, Web.
> Используется в Яндексе, X5 Group, ВСК Страхование и др.
> Подход похож на нашу архитектуру: JSON → нативный рендеринг.
>
> Ключевые идеи DivKit, которые мы интегрируем:
> - **Templates** — наследование и композиция JSON-описаний
> - **States** — переключение внешнего вида без перезагрузки
> - **Variables + Triggers** — реактивность внутри JSON
> - **Patches** — инкрементальные обновления без полной перезагрузки
> - **Expressions** — вычисления в JSON (`"text": "@{count} items"`)

---

## 7. Layer 4: Platform Renderers — нативный перфоманс

> Рендереры — **тонкие обёртки**. Вся логика в Config + StyleResolver.
> Средний размер рендерера: 25-40 строк кода.

### Android (Jetpack Compose)

```kotlin
@Composable
fun ButtonView(
    config: ButtonConfig,
    onAction: (ActionConfig) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (config.visibility == Visibility.Gone) return

    val tokens = LocalDesignTokens.current
    val style = remember(config, tokens) { ButtonStyleResolver.resolve(config, tokens) }

    Button(
        onClick = { config.action?.let(onAction) },
        enabled = config.isInteractive,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(style.colors.bg),
            contentColor = Color(style.colors.text),
        ),
        border = if (style.colors.border != Color.Transparent)
            BorderStroke(1.dp, Color(style.colors.border)) else null,
        shape = RoundedCornerShape(style.radius.dp),
        modifier = modifier
            .height(style.sizes.height.dp)
            .then(if (config.visibility == Visibility.Invisible) Modifier.alpha(0f) else Modifier)
            .semantics { config.accessibilityLabel?.let { contentDescription = it } }
            .testTag(config.testTag ?: config.id),
    ) {
        ButtonContent(config, style)
    }
}

@Composable
private fun ButtonContent(config: ButtonConfig, style: ResolvedButtonStyle) {
    if (config.loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(style.sizes.iconSize.dp),
            color = Color(style.colors.text),
            strokeWidth = 2.dp,
        )
        return
    }

    val iconComposable: (@Composable () -> Unit)? = config.icon?.let {
        { Icon(it.toImageVector(), null, Modifier.size(style.sizes.iconSize.dp)) }
    }

    if (config.iconPosition == ButtonConfig.IconPosition.Only) {
        iconComposable?.invoke()
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LocalDesignTokens.current.spacing.xs.dp),
        ) {
            if (config.iconPosition == ButtonConfig.IconPosition.Leading) iconComposable?.invoke()
            Text(config.text, fontSize = style.sizes.fontSize.sp)
            if (config.iconPosition == ButtonConfig.IconPosition.Trailing) iconComposable?.invoke()
        }
    }
}
```

### React (Web)

```tsx
interface ButtonViewProps {
  config: ButtonConfig;
  onAction?: (action: ActionConfig) => void;
  className?: string;
}

const ButtonView: React.FC<ButtonViewProps> = React.memo(({ config, onAction, className }) => {
  if (config.visibility === 'gone') return null;

  const tokens = useDesignTokens();
  const style = useMemo(() => resolveButtonStyle(config, tokens), [config, tokens]);

  const handleClick = useCallback(() => {
    if (config.isInteractive && config.action) {
      onAction?.(config.action);
    }
  }, [config.action, config.isInteractive, onAction]);

  return (
    <button
      onClick={handleClick}
      disabled={!config.isInteractive}
      data-testid={config.testTag ?? config.id}
      aria-label={config.accessibilityLabel ?? undefined}
      className={clsx('uikit-button', className)}
      style={{
        '--btn-bg': style.colors.bg,
        '--btn-text': style.colors.text,
        '--btn-border': style.colors.border,
        '--btn-height': `${style.sizes.height}px`,
        '--btn-padding-h': `${style.sizes.paddingH}px`,
        '--btn-font-size': `${style.sizes.fontSize}px`,
        '--btn-radius': `${style.radius}px`,
        '--btn-icon-size': `${style.sizes.iconSize}px`,
        visibility: config.visibility === 'invisible' ? 'hidden' : undefined,
      } as React.CSSProperties}
    >
      <ButtonContent config={config} />
    </button>
  );
});

// CSS (через CSS custom properties — zero JS в hot path)
// .uikit-button {
//   background: var(--btn-bg);
//   color: var(--btn-text);
//   border: 1px solid var(--btn-border);
//   height: var(--btn-height);
//   padding-inline: var(--btn-padding-h);
//   font-size: var(--btn-font-size);
//   border-radius: var(--btn-radius);
//   /* transitions, focus states, hover — всё в CSS */
// }
```

---

## 8. Режимы работы: от static до full SDUI

### Mode A: Static (Config в коде)

> Нулевая latency. Все configs зашиты в Kotlin/TypeScript.  
> Для приложений, которым не нужно менять UI без релиза.

```kotlin
// Android — экран зашит в код
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val config by viewModel.screenConfig.collectAsState()
    ScreenRenderer(config) // тот же рендерер, что и для SDUI!
}

class ProfileViewModel : ViewModel() {
    val screenConfig = MutableStateFlow(
        ScreenConfig(
            id = "profile",
            layout = LayoutConfig.SingleColumn(
                mainPlacement = listOf("avatar-section", "info-section", "settings-section"),
            ),
            sections = listOf(
                SectionConfig(id = "avatar-section", type = "avatar-header", component = AvatarHeaderConfig(/*...*/)),
                SectionConfig(id = "info-section", type = "info-list", component = ListConfig(/*...*/)),
                SectionConfig(id = "settings-section", type = "settings-list", component = ListConfig(/*...*/)),
            ),
        )
    )
}
```

```tsx
// React — экран зашит в код
function ProfileScreen() {
  const config = useProfileViewModel();
  return <ScreenRenderer config={config} />;
}
```

### Mode B: Hybrid (кэш + фоновый sync)

> Мгновенный load из кэша, фоновое обновление.  
> Для приложений с A/B тестами, персонализацией, промо-баннерами.

```kotlin
@Composable
fun CatalogScreen(
    sduiClient: SDUIClient = koinInject(),
    fallback: ScreenConfig = CatalogFallback.config, // зашитый fallback
) {
    val screenConfig by sduiClient
        .getScreen("catalog")
        .collectAsState(initial = fallback)  // показываем fallback моментально

    ScreenRenderer(screenConfig)
}
```

### Mode C: Full SDUI (сервер контролирует всё)

> Для маркетинговых экранов, лендингов, промо-акций.  
> UI полностью управляется с бэкенда, включая layout.

```kotlin
@Composable
fun DynamicScreen(screenId: String) {
    val screenConfig by sduiClient
        .getScreen(screenId)
        .collectAsState(initial = ScreenConfig.Loading) // показываем skeleton

    when {
        screenConfig == ScreenConfig.Loading -> FullScreenSkeleton()
        else -> ScreenRenderer(screenConfig)
    }
}
```

### Ключевое преимущество: ScreenRenderer — ОДИН на все режимы

```kotlin
/**
 * Универсальный рендерер экрана.
 * Не знает, откуда пришёл ScreenConfig — из кода, кэша или сервера.
 * Принимает ScreenConfig → рисует нативный UI.
 */
@Composable
fun ScreenRenderer(
    config: ScreenConfig,
    onAction: (ActionConfig) -> Unit = LocalActionRouter.current::dispatch,
) {
    val registry = LocalComponentRegistry.current

    Scaffold(
        topBar = { config.toolbar?.let { ToolbarView(it, onAction) } },
        floatingActionButton = { config.fab?.let { ButtonView(it, onAction) } },
    ) { padding ->
        when (val layout = config.layout) {
            is LayoutConfig.SingleColumn -> SingleColumnLayout(layout, config.sections, registry, onAction, padding)
            is LayoutConfig.TwoColumn -> TwoColumnLayout(layout, config.sections, registry, onAction, padding)
            is LayoutConfig.Tabbed -> TabbedLayout(layout, config.sections, registry, onAction, padding)
            is LayoutConfig.Grid -> GridLayout(layout, config.sections, registry, onAction, padding)
        }
    }
}
```

---

## 9. Унифицированная JSON Schema

### Полный JSON Response (SDUI mode)

```json
{
  "screen": {
    "id": "product-catalog",
    "layout": {
      "type": "single-column",
      "topPlacement": ["search-bar"],
      "mainPlacement": ["categories", "featured-grid", "product-list"],
      "bottomPlacement": ["cart-button"]
    },
    "toolbar": {
      "id": "toolbar",
      "title": "Каталог",
      "actions": [
        {
          "id": "cart-icon",
          "icon": "shopping-cart",
          "badge": { "count": 3 },
          "action": { "type": "navigate", "route": "/cart" }
        }
      ]
    },
    "sections": [
      {
        "id": "search-bar",
        "type": "text-field",
        "component": {
          "id": "search-input",
          "variant": "search",
          "placeholder": "Поиск товаров...",
          "action": {
            "type": "api-call",
            "method": "GET",
            "path": "/api/search?q=${value}",
            "onSuccess": {
              "type": "update-state",
              "targetId": "product-list",
              "patch": "${response.items}"
            }
          }
        }
      },
      {
        "id": "categories",
        "type": "chip-group",
        "component": {
          "id": "category-chips",
          "items": [
            { "id": "all", "text": "Все", "selected": true },
            { "id": "electronics", "text": "Электроника" },
            { "id": "clothing", "text": "Одежда" }
          ],
          "selectionMode": "single"
        }
      },
      {
        "id": "featured-grid",
        "type": "card-grid",
        "component": {
          "id": "featured",
          "columns": 2,
          "items": [
            {
              "id": "card-1",
              "type": "product-card",
              "image": "https://cdn.example.com/p1.webp",
              "title": "iPhone 16 Pro",
              "subtitle": "от 89 990 ₽",
              "badge": { "text": "Хит", "variant": "success" },
              "action": { "type": "navigate", "route": "/product/iphone-16-pro" }
            }
          ]
        }
      },
      {
        "id": "product-list",
        "type": "list",
        "component": {
          "id": "products",
          "items": [],
          "emptyState": {
            "icon": "search-off",
            "title": "Ничего не найдено",
            "subtitle": "Попробуйте другой запрос"
          },
          "pagination": {
            "type": "infinite-scroll",
            "pageSize": 20,
            "endpoint": "/api/products?page=${page}"
          }
        }
      },
      {
        "id": "cart-button",
        "type": "button",
        "component": {
          "id": "open-cart",
          "text": "Корзина (3)",
          "variant": "primary",
          "size": "lg",
          "action": { "type": "navigate", "route": "/cart" }
        }
      }
    ]
  },
  "version": 42,
  "ttl": 300000
}
```

---

## 10. Component Registry — O(1) рендеринг

### Идея

Registry — это Map<String, Renderer>. Получив `section.type`, рендерер находится за O(1). Никаких if/when цепочек.

### Android Registry

```kotlin
object ComponentRegistry {
    private val renderers = mutableMapOf<String, @Composable (ComponentConfig, (ActionConfig) -> Unit) -> Unit>()

    init {
        // Базовые компоненты (поставляются UIKit-библиотекой)
        register("button") { config, onAction -> ButtonView(config as ButtonConfig, onAction) }
        register("text-field") { config, onAction -> TextFieldView(config as TextFieldConfig, onAction) }
        register("card") { config, onAction -> CardView(config as CardConfig, onAction) }
        register("list") { config, onAction -> ListView(config as ListConfig, onAction) }
        register("list-item") { config, onAction -> ListItemView(config as ListItemConfig, onAction) }
        register("chip-group") { config, onAction -> ChipGroupView(config as ChipGroupConfig, onAction) }
        register("card-grid") { config, onAction -> CardGridView(config as CardGridConfig, onAction) }
        register("avatar-header") { config, onAction -> AvatarHeaderView(config as AvatarHeaderConfig, onAction) }
        register("banner") { config, onAction -> BannerView(config as BannerConfig, onAction) }
        register("divider") { config, _ -> DividerView(config as DividerConfig) }
        register("skeleton") { config, _ -> SkeletonView(config as SkeletonConfig) }
        register("image") { config, onAction -> ImageView(config as ImageConfig, onAction) }
        // ... все 25+ компонентов
    }

    fun register(type: String, renderer: @Composable (ComponentConfig, (ActionConfig) -> Unit) -> Unit) {
        renderers[type] = renderer
    }

    @Composable
    fun Render(section: SectionConfig, onAction: (ActionConfig) -> Unit) {
        val renderer = renderers[section.type]
        if (renderer != null) {
            renderer(section.component, onAction)
        } else {
            // Graceful degradation — не крашимся, просто пропускаем
            if (BuildConfig.DEBUG) {
                Text("Unknown component: ${section.type}", color = Color.Red)
            }
        }
    }
}
```

### React Registry

```tsx
type ComponentRenderer = React.FC<{ config: any; onAction: (action: ActionConfig) => void }>;

class ComponentRegistry {
  private renderers = new Map<string, ComponentRenderer>();

  constructor() {
    // Базовые компоненты (поставляются @uikit/react пакетом)
    this.register('button', ButtonView);
    this.register('text-field', TextFieldView);
    this.register('card', CardView);
    this.register('list', ListView);
    this.register('list-item', ListItemView);
    this.register('chip-group', ChipGroupView);
    this.register('card-grid', CardGridView);
    this.register('avatar-header', AvatarHeaderView);
    this.register('banner', BannerView);
    this.register('divider', DividerView);
    this.register('skeleton', SkeletonView);
    this.register('image', ImageView);
    // ... все 25+ компонентов
  }

  register(type: string, renderer: ComponentRenderer) {
    this.renderers.set(type, renderer);
  }

  getRenderer(type: string): ComponentRenderer | undefined {
    return this.renderers.get(type);
  }
}

// React-компонент рендерер секции
const SectionRenderer: React.FC<{
  section: SectionConfig;
  onAction: (action: ActionConfig) => void;
}> = React.memo(({ section, onAction }) => {
  const registry = useComponentRegistry();
  const Renderer = registry.getRenderer(section.type);

  if (!Renderer) {
    if (process.env.NODE_ENV === 'development') {
      return <div style={{ color: 'red' }}>Unknown: {section.type}</div>;
    }
    return null;
  }

  return <Renderer config={section.component} onAction={onAction} />;
});
```

### Кастомные компоненты (расширяемость для конкретного приложения)

```kotlin
// App-specific — регистрируем кастомный компонент
ComponentRegistry.register("loyalty-card") { config, onAction ->
    LoyaltyCardView(config as LoyaltyCardConfig, onAction) // свой компонент
}
```

```tsx
// App-specific — регистрируем кастомный компонент
registry.register('loyalty-card', LoyaltyCardView);
```

---

## 11. Полный пример: экран каталога товаров

### В Static mode (Config в коде)

```kotlin
// Android — ViewModel создаёт ScreenConfig
class CatalogViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _screen = MutableStateFlow(buildCatalogScreen(emptyList()))
    val screen: StateFlow<ScreenConfig> = _screen.asStateFlow()

    init {
        viewModelScope.launch {
            val products = productRepository.getProducts()
            _screen.value = buildCatalogScreen(products)
        }
    }

    private fun buildCatalogScreen(products: List<Product>): ScreenConfig {
        return ScreenConfig(
            id = "catalog",
            layout = LayoutConfig.SingleColumn(
                mainPlacement = listOf("header", "product-grid"),
            ),
            sections = listOf(
                SectionConfig(
                    id = "header",
                    type = "text-block",
                    component = TextBlockConfig(
                        id = "catalog-title",
                        text = "Каталог",
                        variant = TextBlockConfig.Variant.H1,
                    ),
                ),
                SectionConfig(
                    id = "product-grid",
                    type = "card-grid",
                    component = CardGridConfig(
                        id = "products",
                        columns = 2,
                        items = products.map { product ->
                            CardConfig(
                                id = "product-${product.id}",
                                title = product.name,
                                subtitle = "${product.price} ₽",
                                image = ImageConfig(url = product.imageUrl),
                                action = ActionConfig.Navigate(route = "/product/${product.id}"),
                            )
                        },
                    ),
                ),
            ),
        )
    }
}
```

### В SDUI mode (тот же UI, Config с сервера)

Бэкенд отдаёт **ровно такой же JSON**, как `buildCatalogScreen()` создаёт программно. Клиент просто рендерит.

```kotlin
@Composable
fun CatalogScreen(mode: UIKitMode = UIKitMode.Hybrid) {
    val config = when (mode) {
        UIKitMode.Static -> {
            val vm: CatalogViewModel = koinViewModel()
            vm.screen.collectAsState().value
        }
        UIKitMode.Hybrid, UIKitMode.SDUI -> {
            val sduiClient: SDUIClient = koinInject()
            sduiClient.getScreen("catalog")
                .collectAsState(initial = CatalogFallback.config).value
        }
    }

    ScreenRenderer(config)  // ← ОДИН рендерер для всех режимов
}
```

```tsx
// React — ровно такой же паттерн
function CatalogScreen({ mode = 'hybrid' }: { mode?: UIKitMode }) {
  const config = mode === 'static'
    ? useCatalogViewModel()
    : useSDUIScreen('catalog', CatalogFallback);

  return <ScreenRenderer config={config} />;
}
```

---

## 12. Перфоманс-оптимизации

### Android (Compose)

| Оптимизация                | Как реализовано                                                                               |
| -------------------------- | --------------------------------------------------------------------------------------------- |
| **@Immutable Configs**     | Все Config — `@Immutable data class`. Compose skips recomposition если reference не изменился |
| **remember StyleResolver** | `remember(config, tokens) { resolve(config, tokens) }` — стиль вычисляется ОДИН раз           |
| **LazyColumn для секций**  | ScrollLayout используется `LazyColumn` — рендерятся только видимые секции                     |
| **DiffUtil для SDUI**      | При обновлении с сервера — structural diff, анимированные изменения                           |
| **Baseline Profiles**      | Предкомпиляция критических path (UIKit components) в AOT                                      |
| **R8 tree-shaking**        | Неиспользуемые компоненты из библиотеки удаляются                                             |
| **Image: Coil с кэшом**    | Disk + Memory cache, placeholder, crossfade                                                   |

```kotlin
// Пример: Stable keys для LazyColumn (минимальные recompositions)
LazyColumn {
    items(
        items = sections,
        key = { it.id }, // stable key = O(1) diff
    ) { section ->
        ComponentRegistry.Render(section, onAction)
    }
}
```

### React (Web)

| Оптимизация                       | Как реализовано                                                 |
| --------------------------------- | --------------------------------------------------------------- |
| **React.memo на каждый рендерер** | Shallow compare config — skip render если не изменился          |
| **useMemo для StyleResolver**     | `useMemo(() => resolveStyle(config, tokens), [config, tokens])` |
| **CSS Custom Properties**         | Стили через `--var` — браузер оптимизирует layout/paint         |
| **Virtualized List**              | `react-window` / `@tanstack/virtual` для длинных списков        |
| **Code Splitting**                | Каждый компонент = dynamic import, загружается по требованию    |
| **Incremental JSON parse**        | Streaming parse для больших SDUI response                       |
| **Service Worker кэш**            | SDUI responses кэшируются в SW для offline                      |

```tsx
// Пример: Code splitting компонентов (загружаем только что нужно)
const componentLoaders: Record<string, () => Promise<{ default: ComponentRenderer }>> = {
  'button': () => import('./components/ButtonView'),
  'card': () => import('./components/CardView'),
  'list': () => import('./components/ListView'),
  'chart': () => import('./components/ChartView'),  // тяжёлый, не грузим сразу
  'map': () => import('./components/MapView'),       // тяжёлый, не грузим сразу
};

// Lazy Registry
class LazyComponentRegistry {
  private loaded = new Map<string, ComponentRenderer>();
  private loading = new Map<string, Promise<ComponentRenderer>>();

  async getRenderer(type: string): Promise<ComponentRenderer | undefined> {
    if (this.loaded.has(type)) return this.loaded.get(type)!;

    if (!this.loading.has(type)) {
      const loader = componentLoaders[type];
      if (!loader) return undefined;

      this.loading.set(type, loader().then(m => {
        this.loaded.set(type, m.default);
        return m.default;
      }));
    }
    return this.loading.get(type);
  }
}
```

### Общие оптимизации

```
┌────────────────────────────────────────────────────────────────┐
│                    PERFORMANCE BUDGET                           │
│                                                                │
│  Метрика                      Target         Как достигаем     │
│  ─────────────────────────────────────────────────────────────  │
│  First Paint                  < 100ms        Static fallback   │
│  TTI (from cache)             < 200ms        Config cache hit  │
│  TTI (from server)            < 500ms        Streaming parse   │
│  Config parse (50 sections)   < 5ms          Binary format     │
│  Section render (single)      < 2ms          @Immutable/memo   │
│  Full screen render           < 16ms         Lazy rendering    │
│  SDUI diff + update           < 8ms          Structural diff   │
│  Memory per component         < 1KB config   Compact format    │
│  Bundle size (core UIKit)     < 50KB web     Tree-shaking      │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 13. Multi-brand за 1 день

### Как добавить новый бренд (Brand #5001)

```bash
# Шаг 1: Создать файл токенов (5 минут)
cp tokens/brands/default.json tokens/brands/brand-5001.json
# Отредактировать цвета, шрифты, radii

# Шаг 2: Сгенерировать код (1 команда, 2 секунды)
uikit-codegen --brand=brand-5001

# Шаг 3: Подключить в приложении
```

**Android:**
```kotlin
class Brand5001App : Application() {
    override fun onCreate() {
        super.onCreate()
        UIKit.init(
            brandTokens = "brand-5001.json",  // из assets или с сервера
            mode = UIKitMode.Hybrid,
            sduiBaseUrl = "https://api.brand5001.com/sdui",
        )
    }
}
```

**React:**
```tsx
createRoot(document.getElementById('root')!).render(
  <UIKitProvider
    brand="brand-5001"
    tokens={brand5001Tokens}  // import из generated/ или fetch с CDN
    mode="hybrid"
    sduiBaseUrl="https://api.brand5001.com/sdui"
  >
    <App />
  </UIKitProvider>
);
```

### Что различается между брендами

```
┌─────────────────────────────────────────────────────────┐
│ ОДИНАКОВОЕ (UIKit пакет)        │ РАЗЛИЧАЕТСЯ (JSON)    │
│─────────────────────────────────│───────────────────────│
│ Component Configs (Kotlin)      │ Brand tokens (JSON)   │
│ Style Resolvers (Kotlin)        │ Screen configs (SDUI) │
│ Platform Renderers (Compose/    │ App icon, splash      │
│   React)                        │ Feature flags         │
│ SDUI Client                     │ API endpoints         │
│ Action Router                   │ Custom components     │
│ Navigation                      │   (если есть)         │
│ Validation                      │                       │
└─────────────────────────────────┴───────────────────────┘
```

### Версионирование UIKit

```
@uikit/core        v3.2.1    ← KMP: Configs, Resolvers, SDUI Client
@uikit/android     v3.2.1    ← Compose Renderers
@uikit/react       v3.2.1    ← React Renderers
@uikit/tokens-cli  v1.5.0    ← Codegen utility
```

Все 5000 приложений используют одну версию UIKit. Обновление = bump version в gradle/package.json.

---

## 14. Структура проекта

```
uikit/
├── shared/                             ← KMP Library (published to Maven/npm)
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/com/uikit/
│       │   ├── tokens/
│       │   │   ├── DesignTokens.kt           ← token data classes
│       │   │   ├── ColorTokens.kt
│       │   │   ├── SpacingTokens.kt
│       │   │   └── TokenParser.kt            ← JSON → DesignTokens
│       │   ├── components/
│       │   │   ├── core/
│       │   │   │   ├── ComponentConfig.kt     ← sealed interface
│       │   │   │   ├── ActionConfig.kt        ← action types
│       │   │   │   ├── Visibility.kt
│       │   │   │   └── IconRef.kt
│       │   │   ├── button/
│       │   │   │   ├── ButtonConfig.kt
│       │   │   │   └── ButtonStyleResolver.kt
│       │   │   ├── card/
│       │   │   │   ├── CardConfig.kt
│       │   │   │   └── CardStyleResolver.kt
│       │   │   ├── textfield/
│       │   │   ├── list/
│       │   │   ├── chip/
│       │   │   ├── banner/
│       │   │   └── ... (25+ компонентов)
│       │   ├── screen/
│       │   │   ├── ScreenConfig.kt            ← экран = layout + sections
│       │   │   ├── LayoutConfig.kt            ← SingleColumn, TwoColumn, ...
│       │   │   └── SectionConfig.kt           ← обёртка над ComponentConfig
│       │   ├── sdui/
│       │   │   ├── SDUIResponse.kt
│       │   │   ├── SDUIClient.kt              ← fetch + cache + fallback
│       │   │   ├── SDUICache.kt
│       │   │   └── ConfigParser.kt            ← JSON → ScreenConfig
│       │   ├── actions/
│       │   │   ├── ActionRouter.kt
│       │   │   └── CustomActionHandler.kt
│       │   └── validation/
│       │       ├── EmailValidator.kt
│       │       ├── PhoneValidator.kt
│       │       └── FormValidator.kt
│       ├── androidMain/                        ← Android-specific (Ktor engine, etc.)
│       └── jsMain/                             ← JS/Wasm-specific
│
├── android/                             ← Android UI Library (published to Maven)
│   ├── build.gradle.kts                 ← depends on :shared
│   └── src/main/kotlin/com/uikit/android/
│       ├── renderers/
│       │   ├── ButtonView.kt             ← @Composable (~30 lines each)
│       │   ├── CardView.kt
│       │   ├── TextFieldView.kt
│       │   ├── ListView.kt
│       │   ├── ChipGroupView.kt
│       │   ├── BannerView.kt
│       │   └── ...
│       ├── screen/
│       │   ├── ScreenRenderer.kt         ← universal screen renderer
│       │   ├── SingleColumnLayout.kt
│       │   ├── TwoColumnLayout.kt
│       │   └── GridLayout.kt
│       ├── registry/
│       │   └── ComponentRegistry.kt      ← type → @Composable
│       └── theme/
│           └── UIKitTheme.kt             ← CompositionLocals
│
├── react/                               ← React UI Library (published to npm)
│   ├── package.json                     ← @uikit/react
│   ├── src/
│   │   ├── renderers/
│   │   │   ├── ButtonView.tsx            ← React.memo (~30 lines each)
│   │   │   ├── CardView.tsx
│   │   │   ├── TextFieldView.tsx
│   │   │   ├── ListView.tsx
│   │   │   ├── ChipGroupView.tsx
│   │   │   ├── BannerView.tsx
│   │   │   └── ...
│   │   ├── screen/
│   │   │   ├── ScreenRenderer.tsx
│   │   │   ├── SingleColumnLayout.tsx
│   │   │   ├── TwoColumnLayout.tsx
│   │   │   └── GridLayout.tsx
│   │   ├── registry/
│   │   │   └── ComponentRegistry.ts
│   │   ├── hooks/
│   │   │   ├── useDesignTokens.ts
│   │   │   ├── useActionRouter.ts
│   │   │   ├── useSDUIScreen.ts
│   │   │   └── useComponentRegistry.ts
│   │   ├── styles/
│   │   │   ├── tokens.css                ← generated CSS custom properties
│   │   │   └── components.css            ← component base styles
│   │   └── index.ts                      ← public API
│   └── tsconfig.json
│
├── tokens/                              ← Design Tokens Source of Truth
│   ├── foundation.json
│   ├── semantic.json
│   ├── component.json
│   ├── brands/
│   │   ├── default.json
│   │   └── brand-*.json                 ← один файл на бренд
│   └── scripts/
│       ├── generate.ts                  ← генератор Kotlin + TS + CSS
│       └── validate.ts                  ← schema validation
│
├── sdui-server/                         ← SDUI Backend (опционально)
│   ├── src/main/kotlin/
│   │   ├── routes/                      ← GET /sdui/screens/{id}
│   │   ├── builders/                    ← ScreenConfig builders
│   │   ├── ab/                          ← A/B testing integration
│   │   └── personalization/             ← user segment → screen variant
│   └── build.gradle.kts
│
└── docs/
    ├── component-catalog.md             ← автогенерируемый каталог
    ├── migration-guide.md
    └── brand-setup-guide.md             ← инструкция для нового бренда
```

---

## 15. Метрики и сравнение

### Итоговая архитектура vs альтернативы

| Критерий                 | **Unified UIKit** (наш) |   Чистый SDUI (Airbnb)   | Чистый Config-Driven |   DivKit (Yandex)   | Compose MP Wasm |
| ------------------------ | :---------------------: | :----------------------: | :------------------: | :-----------------: | :-------------: |
| **Работает без сервера** |      ✅ Static mode      |            ❌             |          ✅           |          ❌          |        ✅        |
| **Работает с сервером**  |       ✅ SDUI mode       |            ✅             |          ⚠️           |          ✅          |        ❌        |
| **Offline-first**        |   ✅ Cache + Fallback    |       ⚠️ Cache only       |          ✅           |    ⚠️ Cache only     |        ✅        |
| **Android перфоманс**    |     ★★★★★ нативный      |          ★★★★★           |        ★★★★★         |        ★★★★☆        |      ★★★★★      |
| **Web перфоманс**        |  ★★★★★ нативный React   |          ★★★★★           |        ★★★★★         |        ★★★★☆        |   ★★★★☆ Wasm    |
| **Web SEO**              |     ★★★★★ HTML DOM      |          ★★★★★           |        ★★★★★         |        ★★★☆☆        |      ★☆☆☆☆      |
| **Web Accessibility**    |          ★★★★★          |          ★★★★★           |        ★★★★★         |        ★★★☆☆        |      ★★★☆☆      |
| **Multi-brand (5000+)**  |      ✅ JSON tokens      |         ⚠️ кастом         |          ✅           |          ⚠️          |        ❌        |
| **A/B testing UI**       |       ✅ SDUI mode       |            ✅             |          ❌           |          ✅          |        ❌        |
| **Update без релиза**    |       ✅ SDUI mode       |            ✅             |          ❌           |          ✅          |        ❌        |
| **Кастомные компоненты** |       ✅ Registry        |            ✅             |          ✅           |    ⚠️ ограничено     |        ✅        |
| **Код-шаринг**           |         ~75-80%         |         ~50-60%          |       ~70-75%        |        ~40%         |      ~95%       |
| **Стоимость команды**    |     Kotlin + React      | Kotlin + React + Backend |    Kotlin + React    |      JSON-only      |   Kotlin only   |
| **Зрелость**             |       Архитектура       |   Production (Airbnb)    |      Production      | Production (Yandex) |      Beta       |

### Что шарится между 5000+ приложениями

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│  100% SHARED (UIKit пакет — одинаковый для всех)                │
│  ─────────────────────────────────────────────────               │
│  • 25+ Component Configs (Kotlin)                                │
│  • 25+ Style Resolvers (Kotlin)                                  │
│  • 25+ Android Renderers (Compose)                               │
│  • 25+ React Renderers (TypeScript)                              │
│  • ScreenRenderer (Android + React)                              │
│  • Layout engine (SingleColumn, TwoColumn, Grid, Tabbed)        │
│  • ComponentRegistry                                             │
│  • SDUI Client + Cache                                           │
│  • ActionRouter                                                  │
│  • Validators                                                    │
│  • Token parser                                                  │
│                                                                  │
│  PER-APP (уникальное для каждого приложения)                     │
│  ─────────────────────────────────────────────                   │
│  • Brand tokens JSON (~50 значений)                              │
│  • Screen configs (в коде или на SDUI сервере)                   │
│  • Custom components (если есть — 0-5 штук)                      │
│  • App icon, splash, app-specific assets                         │
│  • Feature flags                                                 │
│  • API endpoints                                                 │
│                                                                  │
│  ИТОГО: новое приложение = UIKit + JSON + иконки                │
│         Время запуска: 1-3 дня                                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 16. Источники и вдохновение

| #   | Источник                                                                                                  | Что взяли                                                                        |
| --- | --------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| 1   | **Airbnb Ghost Platform** — Ryan Brooks, "A Deep Dive into Airbnb's Server-Driven UI System"              | Sections + Layouts + Actions, SectionComponentType, платформенные рендереры      |
| 2   | **Anmol Verma** — "Beyond Shared Logic: Building a Whitelabel App with KMP" (ProAndroidDev)               | Config-Driven pattern, shared ViewModel, style resolution через tokens           |
| 3   | **Yandex DivKit** — [divkit.tech](https://divkit.tech/)                                                   | Templates, States, Variables, Patches, Expressions в JSON, production-grade SDUI |
| 4   | **Brad Frost** — Atomic Design + "Creating Themeable Design Systems"                                      | 3-tier token system, component hierarchy, brand theming                          |
| 5   | **Radix UI Primitives** — [radix-ui.com](https://www.radix-ui.com/primitives)                             | Headless/unstyled components, WAI-ARIA, open architecture                        |
| 6   | **Shopify** — Colin Gray, "Management of Native Code and React Native at Shopify"                         | KMP для shared logic + React/нативный UI, ~95% shared code                       |
| 7   | **JetBrains** — Compose Multiplatform 1.9.0 / 1.10.0                                                      | Wasm Beta, Material 3, adaptive layouts, Navigation 3                            |
| 8   | **Amazon Style Dictionary** — [amzn.github.io/style-dictionary](https://amzn.github.io/style-dictionary/) | Token pipeline JSON → multi-platform codegen                                     |
| 9   | **Figma** — "The Future of Design Systems is Semantic"                                                    | Semantic tokens, variables, design-to-code pipeline                              |
| 10  | **Spotify** — Juli Sombat, "How Spotify's Design System Goes Beyond Platforms"                            | Cross-platform design system strategy                                            |
