# Переиспользуемые компоненты: Android (Compose) + Web (React)

> Максимальное переиспользование кода при нативном перфомансе на каждой платформе

---

## Содержание

1. [Проблема и цель](#1-проблема-и-цель)
2. [Стратегии шаринга UI между Compose и React](#2-стратегии-шаринга-ui-между-compose-и-react)
3. [Паттерн: Config-Driven Components](#3-паттерн-config-driven-components)
4. [Паттерн: Design Tokens Pipeline](#4-паттерн-design-tokens-pipeline)
5. [Паттерн: Headless UI + Platform Renderers](#5-паттерн-headless-ui--platform-renderers)
6. [Паттерн: Compose Multiplatform for Web (Wasm)](#6-паттерн-compose-multiplatform-for-web-wasm)
7. [Паттерн: Server-Driven UI](#7-паттерн-server-driven-ui)
8. [Полная архитектура: KMP + React + Compose](#8-полная-архитектура-kmp--react--compose)
9. [Сравнение подходов](#9-сравнение-подходов)
10. [Источники](#10-источники)

---

## 1. Проблема и цель

Domain / Application / Infrastructure слои отлично переиспользуются через **Kotlin Multiplatform**. Вопрос — **что делать с UI-компонентами?**

Две платформы с принципиально разными UI-фреймворками:
- **Android** — Jetpack Compose (Kotlin, декларативный)
- **Web** — React (TypeScript/JavaScript, декларативный)

Оба декларативные, оба component-based, оба reactive — это создаёт возможность для шаринга **логики компонентов** даже если визуальный код различается.

**Цель:** максимум переиспользования при **нативном перфомансе** на каждой платформе. Никаких WebView на Android. Никаких canvas-хаков на Web (если это не осознанный выбор).

---

## 2. Стратегии шаринга UI между Compose и React

### Что реально шарить

```
┌─────────────────────────────────────────────────────────────┐
│                   ЧТО МОЖНО ШАРИТЬ                          │
│                                                             │
│  ✅ Бизнес-логика (use cases, repositories)         100%   │
│  ✅ ViewModel / State management                     100%   │
│  ✅ Component Config (структура, варианты, стили)   100%   │
│  ✅ Design Tokens (цвета, шрифты, отступы)          100%   │
│  ✅ Валидация, форматирование, маппинг              100%   │
│  ✅ Навигационная логика (роуты, deep links)        ~90%   │
│  ✅ Accessibility descriptions                       ~80%   │
│                                                             │
│  ⚠️ Анимации (описание, не реализация)              ~60%   │
│  ⚠️ Layout rules (описание, не реализация)          ~50%   │
│                                                             │
│  ❌ Визуальный код (Composable / JSX)                 0%   │
│  ❌ Платформенные API (Camera, FileSystem)             0%   │
└─────────────────────────────────────────────────────────────┘
```

### Три уровня амбиций

| Уровень            | Подход                                           | % шаринга | Перфоманс         |
| ------------------ | ------------------------------------------------ | --------- | ----------------- |
| **A. Максималист** | Compose MP for Web (Wasm) — один код             | ~95%      | ★★★★☆ Skia canvas |
| **B. Прагматик**   | Shared Config/ViewModel + нативные рендереры     | ~70-75%   | ★★★★★ нативный    |
| **C. Минималист**  | Shared tokens + контракты, раздельная реализация | ~40-50%   | ★★★★★ нативный    |

---

## 3. Паттерн: Config-Driven Components

> Источник: Anmol Verma — "Beyond Shared Logic: Building a Whitelabel App with KMP"

### Идея

> «Мы не шарим UI-код. Мы шарим UI intent.»

Каждый компонент определяется через **Config** — immutable data class в общем KMP-коде. Config содержит **что показывать** и **как реагировать**, но не **как рендерить**. Платформы — тонкие «рендереры», которые превращают Config в нативный UI.

### Архитектура

```
                   ┌──────────────────────────────┐
                   │      Shared (KMP)             │
                   │                              │
                   │  Config ← ViewModel ← Domain │
                   │  (data classes, StateFlow)    │
                   └──────────┬───────────────────┘
                              │
               ┌──────────────┴──────────────┐
               ▼                             ▼
    ┌────────────────────┐       ┌────────────────────┐
    │   Android           │       │   Web (React)       │
    │   Jetpack Compose   │       │   React Components  │
    │   @Composable       │       │   JSX/TSX           │
    │   fun Button(       │       │   <Button            │
    │     config: ...     │       │     config={...}     │
    │   )                 │       │   />                 │
    └────────────────────┘       └────────────────────┘
```

### Пример: Button

**Shared Config (commonMain — Kotlin):**

```kotlin
object KButton {
    enum class Variant { Primary, Secondary, Ghost, Danger }
    enum class Size { Small, Medium, Large }

    @Immutable
    data class Config(
        val text: String,
        val action: (() -> Unit)? = null,
        val variant: Variant = Variant.Primary,
        val size: Size = Size.Medium,
        val disabled: Boolean = false,
        val loading: Boolean = false,
        val icon: IconRef? = null,
        val iconPosition: IconPosition = IconPosition.Leading,
    ) {
        val isInteractive: Boolean get() = !disabled && !loading

        fun style(tokens: DesignTokens): Style = Style(variant, size, disabled, tokens)
    }

    @Immutable
    data class Style(
        val variant: Variant,
        val size: Size,
        val disabled: Boolean,
        val tokens: DesignTokens,
    ) {
        val backgroundColor: Long get() = when {
            disabled -> tokens.color.surfaceDisabled
            else -> when (variant) {
                Variant.Primary -> tokens.color.primary
                Variant.Secondary -> tokens.color.secondary
                Variant.Ghost -> tokens.color.transparent
                Variant.Danger -> tokens.color.danger
            }
        }

        val textColor: Long get() = when {
            disabled -> tokens.color.textDisabled
            else -> when (variant) {
                Variant.Primary -> tokens.color.textOnPrimary
                Variant.Secondary -> tokens.color.textOnSecondary
                Variant.Ghost -> tokens.color.textPrimary
                Variant.Danger -> tokens.color.textOnDanger
            }
        }

        val height: Int get() = when (size) {
            Size.Small -> tokens.sizing.buttonSm
            Size.Medium -> tokens.sizing.buttonMd
            Size.Large -> tokens.sizing.buttonLg
        }

        val horizontalPadding: Int get() = when (size) {
            Size.Small -> tokens.spacing.sm
            Size.Medium -> tokens.spacing.md
            Size.Large -> tokens.spacing.lg
        }

        val fontSize: Int get() = when (size) {
            Size.Small -> tokens.typography.bodySm
            Size.Medium -> tokens.typography.bodyMd
            Size.Large -> tokens.typography.bodyLg
        }
    }
}
```

**Android Renderer (Jetpack Compose):**

```kotlin
@Composable
fun ButtonView(
    config: KButton.Config,
    tokens: DesignTokens = LocalDesignTokens.current
) {
    val style = config.style(tokens)

    Button(
        onClick = { config.action?.invoke() },
        enabled = config.isInteractive,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(style.backgroundColor),
            contentColor = Color(style.textColor),
            disabledContainerColor = Color(style.backgroundColor),
            disabledContentColor = Color(style.textColor),
        ),
        modifier = Modifier
            .height(style.height.dp)
            .padding(horizontal = style.horizontalPadding.dp),
        shape = RoundedCornerShape(tokens.radius.md.dp),
    ) {
        if (config.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color(style.textColor),
                strokeWidth = 2.dp,
            )
        } else {
            if (config.icon != null && config.iconPosition == IconPosition.Leading) {
                Icon(imageVector = config.icon.toImageVector(), contentDescription = null)
                Spacer(Modifier.width(tokens.spacing.xs.dp))
            }
            Text(
                text = config.text,
                fontSize = style.fontSize.sp,
            )
            if (config.icon != null && config.iconPosition == IconPosition.Trailing) {
                Spacer(Modifier.width(tokens.spacing.xs.dp))
                Icon(imageVector = config.icon.toImageVector(), contentDescription = null)
            }
        }
    }
}
```

**React Renderer (TypeScript + React):**

```tsx
// types auto-generated from KButton.Config via JSON Schema or manual sync
interface ButtonConfig {
  text: string;
  variant: 'primary' | 'secondary' | 'ghost' | 'danger';
  size: 'small' | 'medium' | 'large';
  disabled: boolean;
  loading: boolean;
  icon?: string;
  iconPosition: 'leading' | 'trailing';
  onAction?: () => void;
}

interface ButtonStyle {
  backgroundColor: string;
  textColor: string;
  height: number;
  horizontalPadding: number;
  fontSize: number;
}

function resolveButtonStyle(config: ButtonConfig, tokens: DesignTokens): ButtonStyle {
  const { variant, size, disabled } = config;
  return {
    backgroundColor: disabled
      ? tokens.color.surfaceDisabled
      : tokens.color[variant === 'primary' ? 'primary' : variant === 'secondary' ? 'secondary' : variant === 'ghost' ? 'transparent' : 'danger'],
    textColor: disabled
      ? tokens.color.textDisabled
      : tokens.color[`textOn${variant.charAt(0).toUpperCase() + variant.slice(1)}`],
    height: tokens.sizing[`button${size.charAt(0).toUpperCase() + size.slice(1)}`],
    horizontalPadding: tokens.spacing[size === 'small' ? 'sm' : size === 'medium' ? 'md' : 'lg'],
    fontSize: tokens.typography[`body${size.charAt(0).toUpperCase() + size.slice(1)}`],
  };
}

const ButtonView: React.FC<{ config: ButtonConfig }> = ({ config }) => {
  const tokens = useDesignTokens();
  const style = resolveButtonStyle(config, tokens);

  return (
    <button
      onClick={config.onAction}
      disabled={!config.disabled && !config.loading ? undefined : true}
      className="uikit-button"
      style={{
        backgroundColor: style.backgroundColor,
        color: style.textColor,
        height: style.height,
        paddingInline: style.horizontalPadding,
        fontSize: style.fontSize,
        borderRadius: tokens.radius.md,
        border: 'none',
        cursor: config.disabled ? 'not-allowed' : 'pointer',
        display: 'inline-flex',
        alignItems: 'center',
        gap: tokens.spacing.xs,
      }}
    >
      {config.loading ? (
        <span className="uikit-spinner" />
      ) : (
        <>
          {config.icon && config.iconPosition === 'leading' && (
            <span className="uikit-icon">{config.icon}</span>
          )}
          {config.text}
          {config.icon && config.iconPosition === 'trailing' && (
            <span className="uikit-icon">{config.icon}</span>
          )}
        </>
      )}
    </button>
  );
};
```

### Shared ViewModel (State Machine)

```kotlin
// commonMain — одна реализация для Android и Web
class LoginViewModel(
    private val authUseCase: AuthUseCase,
    private val validateEmail: ValidateEmailUseCase,
) : ViewModel() {

    data class ScreenConfig(
        val email: KTextField.Config,
        val password: KTextField.Config,
        val submitButton: KButton.Config,
        val errorBanner: KBanner.Config?,
    )

    sealed interface Event {
        data class EmailChanged(val value: String) : Event
        data class PasswordChanged(val value: String) : Event
        data object SubmitClicked : Event
    }

    private val _state = MutableStateFlow(initialConfig())
    val state: StateFlow<ScreenConfig> = _state.asStateFlow()

    fun onEvent(event: Event) {
        when (event) {
            is Event.EmailChanged -> {
                val isValid = validateEmail(event.value)
                _state.update { current ->
                    current.copy(
                        email = current.email.copy(
                            value = event.value,
                            error = if (isValid) null else "Невалидный email"
                        ),
                        submitButton = current.submitButton.copy(
                            disabled = !isValid || current.password.value.isBlank()
                        )
                    )
                }
            }
            is Event.PasswordChanged -> {
                _state.update { current ->
                    current.copy(
                        password = current.password.copy(value = event.value),
                        submitButton = current.submitButton.copy(
                            disabled = current.email.error != null || event.value.isBlank()
                        )
                    )
                }
            }
            is Event.SubmitClicked -> {
                viewModelScope.launch {
                    _state.update { it.copy(submitButton = it.submitButton.copy(loading = true)) }
                    val result = authUseCase.login(
                        _state.value.email.value,
                        _state.value.password.value,
                    )
                    _state.update { current ->
                        current.copy(
                            submitButton = current.submitButton.copy(loading = false),
                            errorBanner = result.exceptionOrNull()?.let {
                                KBanner.Config(text = it.message ?: "Ошибка", variant = KBanner.Variant.Error)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun initialConfig() = ScreenConfig(
        email = KTextField.Config(
            label = "Email",
            placeholder = "you@example.com",
            keyboardType = KeyboardType.Email,
        ),
        password = KTextField.Config(
            label = "Пароль",
            placeholder = "••••••••",
            secure = true,
        ),
        submitButton = KButton.Config(
            text = "Войти",
            variant = KButton.Variant.Primary,
            disabled = true,
        ),
        errorBanner = null,
    )
}
```

**Android Screen:**

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    val config by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        config.errorBanner?.let { BannerView(it) }

        TextFieldView(
            config = config.email,
            onValueChange = { viewModel.onEvent(LoginViewModel.Event.EmailChanged(it)) },
        )
        Spacer(Modifier.height(16.dp))
        TextFieldView(
            config = config.password,
            onValueChange = { viewModel.onEvent(LoginViewModel.Event.PasswordChanged(it)) },
        )
        Spacer(Modifier.height(24.dp))
        ButtonView(
            config = config.submitButton.copy(
                action = { viewModel.onEvent(LoginViewModel.Event.SubmitClicked) }
            ),
        )
    }
}
```

**React Screen:**

```tsx
// ViewModel потребляется через Kotlin/Wasm или через REST API + TypeScript hook

function LoginScreen() {
  const { config, dispatch } = useLoginViewModel();
  // config: LoginViewModel.ScreenConfig (typed)

  return (
    <div className="login-screen">
      {config.errorBanner && <BannerView config={config.errorBanner} />}

      <TextFieldView
        config={config.email}
        onValueChange={(v) => dispatch({ type: 'EmailChanged', value: v })}
      />
      <TextFieldView
        config={config.password}
        onValueChange={(v) => dispatch({ type: 'PasswordChanged', value: v })}
      />
      <ButtonView
        config={{
          ...config.submitButton,
          onAction: () => dispatch({ type: 'SubmitClicked' }),
        }}
      />
    </div>
  );
}
```

---

## 4. Паттерн: Design Tokens Pipeline

### Идея

Design Tokens — единый JSON-источник визуальных характеристик, который автоматически компилируется в код каждой платформы. Дизайнеры → Figma → JSON → Kotlin + TypeScript.

### Трёхуровневая токен-система

```
Tier 1: Brand (primitive)        Tier 2: Semantic           Tier 3: Component
─────────────────────────        ──────────────────         ─────────────────
color.blue.500: #3B82F6    →    color.primary: blue.500  → button.primary.bg: color.primary
color.red.500: #EF4444     →    color.danger: red.500    → button.danger.bg: color.danger
spacing.4: 16px            →    spacing.md: spacing.4    → button.padding.md: spacing.md
font.size.14: 14px         →    typography.body: 14px    → button.fontSize.md: typography.body
```

### tokens.json (единый источник)

```json
{
  "color": {
    "primitive": {
      "blue-500": { "value": "#3B82F6" },
      "blue-600": { "value": "#2563EB" },
      "gray-100": { "value": "#F3F4F6" },
      "gray-900": { "value": "#111827" },
      "red-500":  { "value": "#EF4444" },
      "white":    { "value": "#FFFFFF" }
    },
    "semantic": {
      "primary":        { "value": "{color.primitive.blue-500}" },
      "primaryHover":   { "value": "{color.primitive.blue-600}" },
      "danger":         { "value": "{color.primitive.red-500}" },
      "surface":        { "value": "{color.primitive.white}" },
      "surfaceDisabled":{ "value": "{color.primitive.gray-100}" },
      "textPrimary":    { "value": "{color.primitive.gray-900}" },
      "textOnPrimary":  { "value": "{color.primitive.white}" },
      "textDisabled":   { "value": "{color.primitive.gray-100}" }
    }
  },
  "spacing": {
    "xs": { "value": "4px" },
    "sm": { "value": "8px" },
    "md": { "value": "16px" },
    "lg": { "value": "24px" },
    "xl": { "value": "32px" }
  },
  "radius": {
    "sm": { "value": "4px" },
    "md": { "value": "8px" },
    "lg": { "value": "16px" },
    "full": { "value": "9999px" }
  },
  "typography": {
    "bodySm":  { "value": "12px" },
    "bodyMd":  { "value": "14px" },
    "bodyLg":  { "value": "16px" },
    "headingSm": { "value": "20px" },
    "headingMd": { "value": "24px" },
    "headingLg": { "value": "32px" }
  },
  "sizing": {
    "buttonSm": { "value": "32px" },
    "buttonMd": { "value": "40px" },
    "buttonLg": { "value": "48px" }
  }
}
```

### Генерация: tokens.json → Kotlin

```kotlin
// AUTO-GENERATED — do not edit
// Source: tokens.json

@Immutable
data class ColorTokens(
    val primary: Long = 0xFF3B82F6,
    val primaryHover: Long = 0xFF2563EB,
    val danger: Long = 0xFFEF4444,
    val surface: Long = 0xFFFFFFFF,
    val surfaceDisabled: Long = 0xFFF3F4F6,
    val textPrimary: Long = 0xFF111827,
    val textOnPrimary: Long = 0xFFFFFFFF,
    val textDisabled: Long = 0xFFF3F4F6,
)

@Immutable
data class SpacingTokens(
    val xs: Int = 4,
    val sm: Int = 8,
    val md: Int = 16,
    val lg: Int = 24,
    val xl: Int = 32,
)

@Immutable
data class RadiusTokens(
    val sm: Int = 4,
    val md: Int = 8,
    val lg: Int = 16,
    val full: Int = 9999,
)

@Immutable
data class TypographyTokens(
    val bodySm: Int = 12,
    val bodyMd: Int = 14,
    val bodyLg: Int = 16,
    val headingSm: Int = 20,
    val headingMd: Int = 24,
    val headingLg: Int = 32,
)

@Immutable
data class SizingTokens(
    val buttonSm: Int = 32,
    val buttonMd: Int = 40,
    val buttonLg: Int = 48,
)

@Immutable
data class DesignTokens(
    val color: ColorTokens = ColorTokens(),
    val spacing: SpacingTokens = SpacingTokens(),
    val radius: RadiusTokens = RadiusTokens(),
    val typography: TypographyTokens = TypographyTokens(),
    val sizing: SizingTokens = SizingTokens(),
)

// CompositionLocal для Compose
val LocalDesignTokens = staticCompositionLocalOf { DesignTokens() }
```

### Генерация: tokens.json → TypeScript

```typescript
// AUTO-GENERATED — do not edit
// Source: tokens.json

export interface ColorTokens {
  primary: string;
  primaryHover: string;
  danger: string;
  surface: string;
  surfaceDisabled: string;
  textPrimary: string;
  textOnPrimary: string;
  textDisabled: string;
}

export interface SpacingTokens {
  xs: number;
  sm: number;
  md: number;
  lg: number;
  xl: number;
}

export interface RadiusTokens {
  sm: number;
  md: number;
  lg: number;
  full: number;
}

export interface TypographyTokens {
  bodySm: number;
  bodyMd: number;
  bodyLg: number;
  headingSm: number;
  headingMd: number;
  headingLg: number;
}

export interface SizingTokens {
  buttonSm: number;
  buttonMd: number;
  buttonLg: number;
}

export interface DesignTokens {
  color: ColorTokens;
  spacing: SpacingTokens;
  radius: RadiusTokens;
  typography: TypographyTokens;
  sizing: SizingTokens;
}

export const defaultTokens: DesignTokens = {
  color: {
    primary: '#3B82F6',
    primaryHover: '#2563EB',
    danger: '#EF4444',
    surface: '#FFFFFF',
    surfaceDisabled: '#F3F4F6',
    textPrimary: '#111827',
    textOnPrimary: '#FFFFFF',
    textDisabled: '#F3F4F6',
  },
  spacing: { xs: 4, sm: 8, md: 16, lg: 24, xl: 32 },
  radius: { sm: 4, md: 8, lg: 16, full: 9999 },
  typography: { bodySm: 12, bodyMd: 14, bodyLg: 16, headingSm: 20, headingMd: 24, headingLg: 32 },
  sizing: { buttonSm: 32, buttonMd: 40, buttonLg: 48 },
};

// React Context
export const DesignTokensContext = React.createContext<DesignTokens>(defaultTokens);
export const useDesignTokens = () => React.useContext(DesignTokensContext);
```

### Генерация: tokens.json → CSS Custom Properties

```css
/* AUTO-GENERATED — do not edit */
/* Source: tokens.json */

:root {
  /* Color — primitive */
  --color-blue-500: #3B82F6;
  --color-blue-600: #2563EB;
  --color-gray-100: #F3F4F6;
  --color-gray-900: #111827;
  --color-red-500: #EF4444;
  --color-white: #FFFFFF;

  /* Color — semantic */
  --color-primary: var(--color-blue-500);
  --color-primary-hover: var(--color-blue-600);
  --color-danger: var(--color-red-500);
  --color-surface: var(--color-white);
  --color-surface-disabled: var(--color-gray-100);
  --color-text-primary: var(--color-gray-900);
  --color-text-on-primary: var(--color-white);

  /* Spacing */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;

  /* Radius */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 16px;
  --radius-full: 9999px;

  /* Typography */
  --font-size-body-sm: 12px;
  --font-size-body-md: 14px;
  --font-size-body-lg: 16px;
  --font-size-heading-sm: 20px;
  --font-size-heading-md: 24px;
  --font-size-heading-lg: 32px;

  /* Sizing */
  --sizing-button-sm: 32px;
  --sizing-button-md: 40px;
  --sizing-button-lg: 48px;
}
```

### Multi-brand

```
tokens/
  ├── base.json          ← общие токены
  ├── brand-a.json       ← переопределения для бренда A
  └── brand-b.json       ← переопределения для бренда B
```

При генерации: `base.json` deep-merge с `brand-x.json` → платформенный код. Добавление нового бренда = новый JSON-файл + запуск pipeline.

### Инструменты

| Инструмент                    | Описание                                             |
| ----------------------------- | ---------------------------------------------------- |
| **Style Dictionary** (Amazon) | Трансформация токенов JSON → Kotlin / TS / CSS / XML |
| **Figma Tokens / Variables**  | Экспорт токенов напрямую из Figma                    |
| **Specify**                   | Sync дизайн-данных Figma ↔ код                       |
| **Cobalt UI**                 | Open-source CLI для W3C Design Token Format          |
| Свой Gradle-скрипт            | Простая кастомная генерация .kt + .ts из JSON        |

---

## 5. Паттерн: Headless UI + Platform Renderers

### Идея

**Headless UI** — это подход, когда компонент предоставляет **только логику и state management**, без визуальной реализации. Визуал полностью на совести потребителя.

На Web этот паттерн популяризирован **Radix Primitives**, **Headless UI (Tailwind)**, **Ark UI**, **React Aria**. Они дают поведение (focus management, keyboard navigation, ARIA), а стили — ваши.

### Как применить кроссплатформенно

```
┌───────────────────────────────────────────────────┐
│              Headless Component (KMP)              │
│                                                   │
│  class DropdownController {                       │
│    val isOpen: StateFlow<Boolean>                 │
│    val selectedItem: StateFlow<Item?>             │
│    val highlightedIndex: StateFlow<Int>           │
│                                                   │
│    fun toggle()                                   │
│    fun select(item: Item)                         │
│    fun onKeyDown(key: Key)                        │
│    fun onClickOutside()                           │
│  }                                                │
└──────────────────┬────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
  Compose Renderer       React Renderer
  ─────────────────      ──────────────
  использует               использует
  DropdownMenu {}          <Listbox>
  Material3                Radix / свой
```

### Пример: Combobox (Autocomplete)

**Shared Headless Controller (KMP):**

```kotlin
class ComboboxController<T>(
    private val items: List<T>,
    private val filterFn: (T, String) -> Boolean,
    private val labelFn: (T) -> String,
) {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isOpen = MutableStateFlow(false)
    val isOpen: StateFlow<Boolean> = _isOpen.asStateFlow()

    private val _highlightedIndex = MutableStateFlow(-1)
    val highlightedIndex: StateFlow<Int> = _highlightedIndex.asStateFlow()

    private val _selectedItem = MutableStateFlow<T?>(null)
    val selectedItem: StateFlow<T?> = _selectedItem.asStateFlow()

    val filteredItems: StateFlow<List<T>> = _query.map { q ->
        if (q.isBlank()) items else items.filter { filterFn(it, q) }
    }.stateIn(/* scope */)

    fun onQueryChange(value: String) {
        _query.value = value
        _isOpen.value = true
        _highlightedIndex.value = 0
    }

    fun onSelect(item: T) {
        _selectedItem.value = item
        _query.value = labelFn(item)
        _isOpen.value = false
    }

    fun onKeyDown(key: NavigationKey) {
        when (key) {
            NavigationKey.ArrowDown -> {
                val max = filteredItems.value.size - 1
                _highlightedIndex.update { (it + 1).coerceAtMost(max) }
            }
            NavigationKey.ArrowUp -> {
                _highlightedIndex.update { (it - 1).coerceAtLeast(0) }
            }
            NavigationKey.Enter -> {
                filteredItems.value.getOrNull(_highlightedIndex.value)?.let { onSelect(it) }
            }
            NavigationKey.Escape -> {
                _isOpen.value = false
            }
        }
    }

    fun onClickOutside() { _isOpen.value = false }
    fun onFocus() { _isOpen.value = true }
}
```

**Android Renderer:**

```kotlin
@Composable
fun <T> ComboboxView(
    controller: ComboboxController<T>,
    itemContent: @Composable (T, highlighted: Boolean) -> Unit,
) {
    val query by controller.query.collectAsState()
    val isOpen by controller.isOpen.collectAsState()
    val filtered by controller.filteredItems.collectAsState()
    val highlightedIndex by controller.highlightedIndex.collectAsState()

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = controller::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (it.isFocused) controller.onFocus() }
                .onKeyEvent { event ->
                    NavigationKey.fromComposeKey(event.key)?.let {
                        controller.onKeyDown(it); true
                    } ?: false
                },
        )

        if (isOpen && filtered.isNotEmpty()) {
            Surface(shadowElevation = 4.dp, shape = RoundedCornerShape(8.dp)) {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    itemsIndexed(filtered) { index, item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { controller.onSelect(item) }
                        ) {
                            itemContent(item, index == highlightedIndex)
                        }
                    }
                }
            }
        }
    }
}
```

**React Renderer:**

```tsx
function ComboboxView<T>({
  controller,
  renderItem,
}: {
  controller: ComboboxController<T>;
  renderItem: (item: T, highlighted: boolean) => React.ReactNode;
}) {
  const query = useStateFlow(controller.query);
  const isOpen = useStateFlow(controller.isOpen);
  const filtered = useStateFlow(controller.filteredItems);
  const highlightedIndex = useStateFlow(controller.highlightedIndex);
  const listRef = useRef<HTMLUListElement>(null);

  return (
    <div className="combobox" onBlur={() => controller.onClickOutside()}>
      <input
        value={query}
        onChange={(e) => controller.onQueryChange(e.target.value)}
        onFocus={() => controller.onFocus()}
        onKeyDown={(e) => {
          const key = mapKeyToNavigationKey(e.key);
          if (key) {
            e.preventDefault();
            controller.onKeyDown(key);
          }
        }}
        role="combobox"
        aria-expanded={isOpen}
        aria-controls="combobox-list"
        aria-activedescendant={
          highlightedIndex >= 0 ? `combobox-item-${highlightedIndex}` : undefined
        }
      />

      {isOpen && filtered.length > 0 && (
        <ul ref={listRef} id="combobox-list" role="listbox" className="combobox-dropdown">
          {filtered.map((item, index) => (
            <li
              key={index}
              id={`combobox-item-${index}`}
              role="option"
              aria-selected={index === highlightedIndex}
              onClick={() => controller.onSelect(item)}
            >
              {renderItem(item, index === highlightedIndex)}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
```

---

## 6. Паттерн: Compose Multiplatform for Web (Wasm)

### Статус: Beta (с сентября 2025)

Compose Multiplatform с таргетом **Kotlin/Wasm** позволяет запускать **тот же Compose-код** в браузере. С версии 1.9.0 — Beta-статус, с 1.10.0 — unified `@Preview`, Navigation 3, Hot Reload.

### Что доступно

- Material 3 компоненты
- Adaptive layouts
- Browser navigation (back/forward, deep links, history)
- Dark mode
- Type-safe navigation
- Interop с HTML-элементами
- Accessibility (базовый)

### Performance

Kotlin/Wasm Compose **обгоняет JavaScript** в бенчмарках и приближается к JVM:

```
  JVM   ████████████████████████████  (baseline)
  Wasm  ██████████████████████████    (~90% of JVM)
  JS    ██████████████████            (~65% of JVM)
```

### Пример: один и тот же код для Android и Web

```kotlin
// commonMain — работает и на Android, и в браузере
@Composable
fun ProductCard(product: Product, onBuy: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(product.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${product.price} ₽",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Button(onClick = onBuy) {
                    Text("Купить")
                }
            }
        }
    }
}
```

**Android:** `./gradlew :app:installDebug` — нативный Compose  
**Web:** `./gradlew :web:wasmJsBrowserRun` — Compose → Canvas в браузере

### Когда выбрать этот подход

| Подходит ✅                          | Не подходит ❌                               |
| ----------------------------------- | ------------------------------------------- |
| Маленькая команда, все знают Kotlin | Web — основная платформа с SEO-требованиями |
| Внутренние тулы, админки, дашборды  | Нужен идеальный web-нативный UX             |
| Быстрый вывод MVP на все платформы  | Большая существующая React-кодовая база     |
| UI одинаковый на Android и Web      | Критична поддержка старых браузеров         |

### Ограничения

- **SEO:** Canvas-рендеринг не индексируется поисковиками (нет DOM-нод)
- **Accessibility:** Базовая поддержка, но не на уровне нативного HTML
- **Bundle size:** Wasm binary больше минифицированного JS
- **Экосистема Web:** нет доступа к React-экосистеме (npm libraries)
- **Browser support:** нужна поддержка WasmGC (современные браузеры)

---

## 7. Паттерн: Server-Driven UI

> Источник: Airbnb Ghost Platform

### Идея

Backend описывает **структуру UI** (layout + sections + actions) через единую схему (GraphQL / JSON). Клиенты рендерят нативно по описанию. UI обновляется без релизов.

### Архитектура для Android + Web

```
┌──────────────────────────────────────────┐
│            Backend (GraphQL / REST)       │
│                                          │
│  Response:                               │
│  ├── layout: "single-column"             │
│  ├── sections:                           │
│  │   ├── { type: "hero-banner",          │
│  │   │     title: "...", image: "..." }  │
│  │   ├── { type: "product-grid",         │
│  │   │     items: [...] }                │
│  │   └── { type: "cta-button",           │
│  │         text: "Купить", action: ... } │
│  └── actions:                            │
│      └── { type: "navigate", url: "..." }│
└──────────────────┬───────────────────────┘
                   │ Единая Schema
          ┌────────┴────────┐
          ▼                 ▼
   Android (Compose)    React (Web)
   SectionRegistry      SectionRegistry
   maps type → View     maps type → Component
```

### Пример: Section Component Registry

**Shared Schema (Protocol Buffers или JSON Schema):**

```json
{
  "type": "object",
  "properties": {
    "sectionType": { "enum": ["hero-banner", "product-grid", "cta-button", "text-block"] },
    "data": { "type": "object" }
  }
}
```

**Android Section Registry:**

```kotlin
object SectionRegistry {
    private val renderers = mapOf<String, @Composable (JsonObject) -> Unit>(
        "hero-banner" to { data -> HeroBannerSection(data) },
        "product-grid" to { data -> ProductGridSection(data) },
        "cta-button" to { data -> CtaButtonSection(data) },
        "text-block" to { data -> TextBlockSection(data) },
    )

    @Composable
    fun RenderSection(type: String, data: JsonObject) {
        renderers[type]?.invoke(data)
            ?: FallbackSection(type) // graceful degradation
    }
}

@Composable
fun SDUIScreen(response: SDUIResponse) {
    LazyColumn {
        items(response.sections) { section ->
            SectionRegistry.RenderSection(section.type, section.data)
        }
    }
}
```

**React Section Registry:**

```tsx
const sectionRegistry: Record<string, React.FC<{ data: any }>> = {
  'hero-banner': HeroBannerSection,
  'product-grid': ProductGridSection,
  'cta-button': CtaButtonSection,
  'text-block': TextBlockSection,
};

function SDUIScreen({ response }: { response: SDUIResponse }) {
  return (
    <div className="sdui-screen">
      {response.sections.map((section, i) => {
        const Component = sectionRegistry[section.type];
        return Component ? (
          <Component key={i} data={section.data} />
        ) : (
          <FallbackSection key={i} type={section.type} />
        );
      })}
    </div>
  );
}
```

---

## 8. Полная архитектура: KMP + React + Compose

### Рекомендуемая структура проекта

```
project/
├── shared/                          ← Kotlin Multiplatform
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       │   ├── domain/              ← Use cases, models, interfaces
│       │   ├── data/                ← Repositories, API clients
│       │   ├── presentation/        ← ViewModels, state machines
│       │   └── designsystem/
│       │       ├── tokens/          ← DesignTokens data classes
│       │       └── components/      ← Config data classes (KButton, KCard, ...)
│       ├── androidMain/             ← Android-specific impls (Room, etc.)
│       └── wasmJsMain/             ← Wasm-specific impls (для Kotlin/Wasm вариант)
│
├── tokens/                          ← Design Tokens Source of Truth
│   ├── base.json
│   ├── brand-a.json
│   ├── brand-b.json
│   └── scripts/
│       ├── generate-kotlin.ts       ← JSON → Kotlin data classes
│       ├── generate-typescript.ts   ← JSON → TS interfaces + values
│       └── generate-css.ts          ← JSON → CSS custom properties
│
├── android/                         ← Android App
│   ├── build.gradle.kts             ← depends on :shared
│   └── src/main/
│       └── ui/
│           ├── components/          ← Thin Compose renderers
│           │   ├── ButtonView.kt
│           │   ├── CardView.kt
│           │   ├── TextFieldView.kt
│           │   └── ...
│           ├── screens/             ← Screen Composables
│           └── theme/               ← MaterialTheme setup using tokens
│
├── web/                             ← React Web App
│   ├── package.json
│   ├── src/
│   │   ├── generated/               ← Auto-generated from tokens + configs
│   │   │   ├── tokens.ts
│   │   │   ├── tokens.css
│   │   │   └── config-types.ts
│   │   ├── components/              ← Thin React renderers
│   │   │   ├── ButtonView.tsx
│   │   │   ├── CardView.tsx
│   │   │   ├── TextFieldView.tsx
│   │   │   └── ...
│   │   ├── screens/                 ← Screen components
│   │   ├── hooks/                   ← useViewModel hooks (consume KMP via Wasm or API)
│   │   └── theme/                   ← ThemeProvider using tokens
│   └── tsconfig.json
│
└── docs/
    └── component-catalog.md         ← Документация компонентов
```

### Варианты подключения React к KMP

#### Вариант A: Kotlin/Wasm bindings (максимальный шаринг)

```
shared (KMP CommonMain)
  │
  ├── Target: Android (JVM) → android app
  └── Target: WasmJS        → NPM package → React app imports
```

React потребляет ViewModel напрямую:

```typescript
// hooks/useLoginViewModel.ts
import { LoginViewModel } from '@my/shared-wasm'; // KMP compiled to Wasm

export function useLoginViewModel() {
  const [vm] = useState(() => new LoginViewModel(/* DI */));
  const [config, setConfig] = useState(vm.state.value);

  useEffect(() => {
    const unsubscribe = vm.state.collect((newConfig) => setConfig(newConfig));
    return unsubscribe;
  }, [vm]);

  return {
    config,
    dispatch: (event: LoginEvent) => vm.onEvent(event),
  };
}
```

**Плюсы:** ViewModel, валидация, бизнес-логика — 100% shared; type safety через Wasm  
**Минусы:** Wasm в Beta; bundle size; debug сложнее; не все KMP-библиотеки поддерживают Wasm target

#### Вариант B: REST/GraphQL API + TypeScript codegen (прагматичный)

```
shared (KMP) → Backend (Ktor / Spring)
                │
                └── REST/GraphQL API → React app
                    типизация через OpenAPI / GraphQL codegen
```

```typescript
// hooks/useLoginViewModel.ts — чисто клиентский аналог ViewModel
import { useReducer } from 'react';
import type { LoginScreenConfig, LoginEvent } from '../generated/config-types';

function loginReducer(state: LoginScreenConfig, event: LoginEvent): LoginScreenConfig {
  // Логика идентична shared ViewModel, но на TypeScript
  switch (event.type) {
    case 'EmailChanged':
      const isValid = validateEmail(event.value);
      return {
        ...state,
        email: { ...state.email, value: event.value, error: isValid ? null : 'Невалидный email' },
        submitButton: { ...state.submitButton, disabled: !isValid || !state.password.value },
      };
    // ...
  }
}
```

**Плюсы:** Зрелый стек; легко debug; React-native экосистема; SEO-friendly  
**Минусы:** ViewModel не шарится — дублирование логики (но Config types шарятся)

#### Вариант C: Config через API — share только state (гибрид)

Backend (на Ktor / KMP) вычисляет Config и отдаёт его на Web как JSON:

```
Client Event → Backend → ViewModel (KMP) → Config JSON → React renders
```

```typescript
// React просто рендерит Config, полученный с сервера
async function fetchConfig(event: LoginEvent): Promise<LoginScreenConfig> {
  const res = await fetch('/api/login/state', {
    method: 'POST',
    body: JSON.stringify(event),
  });
  return res.json();
}

function LoginScreen() {
  const [config, setConfig] = useState<LoginScreenConfig>(initialConfig);

  const dispatch = async (event: LoginEvent) => {
    const newConfig = await fetchConfig(event);
    setConfig(newConfig);
  };

  return (
    <div>
      <TextFieldView config={config.email} onChange={(v) => dispatch({ type: 'EmailChanged', value: v })} />
      <TextFieldView config={config.password} onChange={(v) => dispatch({ type: 'PasswordChanged', value: v })} />
      <ButtonView config={config.submitButton} onAction={() => dispatch({ type: 'SubmitClicked' })} />
    </div>
  );
}
```

**Плюсы:** 100% shared ViewModel (на сервере); React — чистый рендерер; идеальная консистентность  
**Минусы:** Latency на каждое действие; offline не работает; нагрузка на сервер

### Рекомендуемая комбинация

```
┌────────────────────────────────────────────────────────────┐
│                    УРОВНИ ШАРИНГА                           │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ① Design Tokens (JSON → Kotlin + TypeScript + CSS)       │
│     → 100% shared, zero runtime cost                       │
│                                                            │
│  ② Component Configs (Kotlin data classes)                 │
│     → 100% shared в KMP                                    │
│     → TypeScript types через JSON Schema codegen           │
│                                                            │
│  ③ Style Resolution (Config.style(tokens) → Style)        │
│     → Shared в KMP (Kotlin)                                │
│     → Портировано в TypeScript (тонкий resolveStyle())     │
│     → Или shared через Kotlin/Wasm                         │
│                                                            │
│  ④ ViewModel / State Machine                               │
│     → 100% shared в KMP (Kotlin)                           │
│     → Android: прямое использование                        │
│     → Web: через Kotlin/Wasm ИЛИ дублирование на TS       │
│            ИЛИ server-side ViewModel                        │
│                                                            │
│  ⑤ UI Renderers                                            │
│     → 0% shared (нативные для каждой платформы)            │
│     → Android: Jetpack Compose (thin wrappers)             │
│     → Web: React components (thin wrappers)                │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### Итоговые метрики

| Слой                       | Android       | Web (React)        | Shared                       |
| -------------------------- | ------------- | ------------------ | ---------------------------- |
| Domain (models, use cases) | —             | —                  | **100% KMP**                 |
| Data (repositories, API)   | ~5% platform  | ~5% platform       | **~90% KMP**                 |
| ViewModel + State          | —             | — (Wasm) / TS port | **100% KMP** / **~80% с TS** |
| Component Config           | —             | TS types (codegen) | **100% KMP**                 |
| Design Tokens              | Compose Theme | CSS vars + TS      | **100% JSON**                |
| UI Renderers               | Compose       | React              | **0% (нативный)**            |
| **Итого**                  |               |                    | **~70-80%**                  |

---

## 9. Сравнение подходов

| Критерий                 | Config-Driven + раздельные рендереры |    Compose MP Wasm     |         SDUI          |
| ------------------------ | :----------------------------------: | :--------------------: | :-------------------: |
| **Переиспользование**    |               ~70-75%                |          ~95%          |         ~60%          |
| **Android перфоманс**    |        ★★★★★ нативный Compose        | ★★★★★ нативный Compose |    ★★★★★ нативный     |
| **Web перфоманс**        |         ★★★★★ нативный React         |   ★★★★☆ Wasm Canvas    | ★★★★★ нативный React  |
| **Web SEO**              |           ★★★★★ (HTML DOM)           |     ★☆☆☆☆ (Canvas)     |   ★★★★★ (HTML DOM)    |
| **Web Accessibility**    |        ★★★★★ (нативный HTML)         |      ★★★☆☆ (beta)      | ★★★★★ (нативный HTML) |
| **React экосистема**     |            ★★★★★ (полная)            |      ☆☆☆☆☆ (нет)       |    ★★★★★ (полная)     |
| **DX скорость**          |                ★★★★☆                 |         ★★★★★          |         ★★★☆☆         |
| **Multi-brand**          |                ★★★★★                 |         ★★★☆☆          |         ★★★★★         |
| **Стоимость команды**    |       2 спеца: Kotlin + React        |     1 спец: Kotlin     |   2 спеца + backend   |
| **Offline-first**        |                ★★★★★                 |         ★★★★★          |         ★★☆☆☆         |
| **Зрелость (март 2026)** |                ★★★★★                 |  ★★★★☆ (Beta→Stable)   |         ★★★★☆         |

### Когда что выбрать

**Config-Driven + React + Compose:**
- Web — ключевая платформа (SEO, accessibility, React-библиотеки)
- Есть команда React-разработчиков
- Нужен white-label / multi-brand
- Banking, fintech, e-commerce

**Compose Multiplatform Wasm:**
- Маленькая команда (Kotlin only)
- Внутренние тулы, админки, дашборды
- SEO и accessibility не критичны
- Хочется максимальный код-шаринг с минимум усилий

**Server-Driven UI:**
- Контентные платформы (каталоги, маркетплейсы)
- A/B тестирование UI без релизов
- Много вариаций экранов (десятки брендов)
- Сильный backend-стек

---

## 10. Источники

1. **Anmol Verma** — "Beyond Shared Logic: Building a Whitelabel App with KMP"
   ProAndroidDev, Jan 2026
   https://proandroiddev.com/beyond-shared-logic-building-a-whitelabel-app-with-kotlin-multiplatform-d220a0b196b2

2. **Brad Frost** — "Creating Themeable Design Systems" + Atomic Design
   https://bradfrost.com/blog/post/creating-themeable-design-systems/

3. **Ryan Brooks (Airbnb)** — "A Deep Dive into Airbnb's Server-Driven UI System"
   https://medium.com/airbnb-engineering/a-deep-dive-into-airbnbs-server-driven-ui-system-842244c5f5

4. **JetBrains** — "Compose Multiplatform 1.9.0: Web Goes Beta"
   https://blog.jetbrains.com/kotlin/2025/09/compose-multiplatform-1-9-0-compose-for-web-beta/

5. **JetBrains** — Kotlin/Wasm Overview
   https://kotlinlang.org/docs/wasm-overview.html

6. **Radix UI** — Primitives (Headless UI for React)
   https://www.radix-ui.com/primitives/docs/overview/introduction

7. **Colin Gray (Shopify)** — "Management of Native Code and React Native at Shopify"
   https://shopify.engineering/managing-native-code-react-native

8. **React Native Team** — "New Architecture is here"
   https://reactnative.dev/blog/2024/10/23/the-new-architecture-is-here

9. **Salesforce** — Lightning Design System: Design Tokens
   https://www.lightningdesignsystem.com/design-tokens/

10. **Amazon** — Style Dictionary
    https://amzn.github.io/style-dictionary/
