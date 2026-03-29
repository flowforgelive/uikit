# SSR + Decompose + MVIKotlin: Интеграция с Unified UIKit

> Расширение [UNIFIED_UIKIT_ARCHITECTURE.md](UNIFIED_UIKIT_ARCHITECTURE.md)  
> SSR (Next.js) · Decompose навигация · MVIKotlin MVI · Максимальный перфоманс  
> Android (Compose) + Web (React/Next.js)

---

## Содержание

1. [Обзор: зачем расширять архитектуру](#1-обзор-зачем-расширять-архитектуру)
2. [Итоговая архитектура: 7-уровневая схема](#2-итоговая-архитектура-7-уровневая-схема)
3. [Decompose — навигация и lifecycle компонентов](#3-decompose--навигация-и-lifecycle-компонентов)
4. [MVIKotlin — единый state management](#4-mvikotlin--единый-state-management)
5. [Интеграция Decompose + MVIKotlin + Config-Driven Engine](#5-интеграция-decompose--mvikotlin--config-driven-engine)
6. [SSR для Web: Next.js App Router + Config-Driven](#6-ssr-для-web-nextjs-app-router--config-driven)
7. [Двойная навигация: Decompose (KMP) ↔ Next.js Router (Web)](#7-двойная-навигация-decompose-kmp--nextjs-router-web)
8. [SDUI + SSR: серверный рендеринг динамических экранов](#8-sdui--ssr-серверный-рендеринг-динамических-экранов)
9. [Перфоманс-архитектура](#9-перфоманс-архитектура)
10. [Структура проекта](#10-структура-проекта)
11. [Примеры кода: полный flow](#11-примеры-кода-полный-flow)
12. [Стратегия миграции](#12-стратегия-миграции)
13. [Выводы и рекомендации](#13-выводы-и-рекомендации)
14. [Источники](#14-источники)

---

## 1. Обзор: зачем расширять архитектуру

### Что уже есть (UNIFIED_UIKIT_ARCHITECTURE.md)

- **Layer 0–4**: Design Tokens → Component Primitives → Config-Driven Engine → SDUI/BDUI → Platform Renderers
- **ComponentConfig** sealed interface в KMP — единица UI
- **ActionConfig** — стандартизированные действия (Navigate, ApiCall, UpdateState, ShowFeedback)
- **ScreenConfig** — описание экрана (Layout + Sections)
- Три режима: Static / Hybrid / Full SDUI

### Чего не хватает

| Проблема | Решение |
|----------|---------|
| Навигация описана как `ActionConfig.Navigate(route)` — плоская строка, нет lifecycle, нет back stack management | **Decompose** Child Stack — типобезопасная навигация с lifecycle |
| State management через `StateFlow` / `ViewModel` — нет единого MVI паттерна | **MVIKotlin** Store — Bootstrapper → Executor → Reducer → State + Labels |
| Web рендерит SPA — нет SSR, плохое SEO, медленный FCP | **Next.js App Router** — SSR / RSC / Streaming для первой загрузки |
| Config-Driven экраны генерируются на клиенте — сервер не знает про них | **Server-Side Config Resolution** — рендер Config → HTML на сервере |

---

## 2. Итоговая архитектура: 7-уровневая схема

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        App (5000+ приложений)                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Layer 6: PLATFORM ENTRY POINTS                                         │
│  ┌───────────────────────────┐  ┌────────────────────────────────────┐ │
│  │ Android: MainActivity     │  │ Web: Next.js App Router            │ │
│  │ defaultComponentContext() │  │ layout.tsx / page.tsx               │ │
│  │ setContent { RootContent }│  │ SSR → Hydration → SPA навигация   │ │
│  └───────────────────────────┘  └────────────────────────────────────┘ │
│                          ▲                                              │
│  Layer 5: PLATFORM RENDERERS (тонкие, нативные)                        │
│  ┌───────────────────────────┐  ┌────────────────────────────────────┐ │
│  │ Android (Compose)          │  │ Web (React)                        │ │
│  │ Children(stack) {}         │  │ Server Components (static UI)     │ │
│  │ subscribeAsState()         │  │ Client Components (interactive)   │ │
│  └───────────────────────────┘  └────────────────────────────────────┘ │
│                          ▲                                              │
│  Layer 4: DECOMPOSE — навигация + lifecycle (KMP shared)               │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ RootComponent → Child Stack (Home, Catalog, Details, ...)       │   │
│  │ ComponentContext: Lifecycle, StateKeeper, InstanceKeeper         │   │
│  │ @Serializable Config → автоматическое восстановление стека     │   │
│  │ WebNavigationOwner → синхронизация с browser history            │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                          ▲                                              │
│  Layer 3: MVIKotlin Store — MVI state management (KMP shared)          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Store<Intent, State, Label>                                     │   │
│  │ Bootstrapper → загрузка начальных данных                        │   │
│  │ Executor → бизнес-логика, async, API calls                      │   │
│  │ Reducer → State = f(State, Message) — pure function             │   │
│  │ Labels → one-shot events (навигация, snackbar, analytics)       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                          ▲                                              │
│  Layer 2: CONFIG-DRIVEN ENGINE (ядро, без изменений)                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ ComponentConfig / ActionConfig / ScreenConfig / SectionConfig   │   │
│  │ ComponentRegistry — O(1) lookup                                 │   │
│  │ StyleResolvers — Config × Tokens → ResolvedStyle                │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                          ▲                                              │
│  Layer 1: COMPONENT PRIMITIVES + Layer 0: DESIGN TOKENS                │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ 50-70 компонентов (Button, Card, TextField, List, ...)          │   │
│  │ tokens.json → Kotlin + TypeScript + CSS                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Ключевая карта совместимости

| Слой | Android (Compose) | Web (Next.js/React) |
|------|-------------------|---------------------|
| Layer 0: Tokens | Kotlin `DesignTokens` (codegen) | CSS Custom Properties + TS types (codegen) |
| Layer 1: Primitives | `@Composable` нативные | React компоненты (Server + Client) |
| Layer 2: Config Engine | KMP `commonMain` — общий | KMP `commonMain` ИЛИ TS-порт (JSON схема) |
| Layer 3: MVIKotlin | KMP `commonMain` → `Store` | KMP `jsMain` → Kotlin/JS ИЛИ TS-аналог (Zustand + middleware) |
| Layer 4: Decompose | KMP `commonMain` → `ComponentContext` | KMP `jsMain` + WebNavigationOwner ИЛИ Next.js App Router |
| Layer 5: Renderers | Compose `Children()` | React SSR → Hydration |
| Layer 6: Entry | `MainActivity` | `layout.tsx` + `page.tsx` |

---

## 3. Decompose — навигация и lifecycle компонентов

### Почему Decompose а не Navigation Compose

| Критерий | Decompose | Navigation Compose / React Router |
|----------|-----------|-----------------------------------|
| KMP shared navigation | ✅ Полностью в commonMain | ❌ Platform-specific |
| Lifecycle-aware | ✅ Каждый component имеет lifecycle | ⚠️ Ограниченно |
| Back stack = живые компоненты | ✅ Работают в фоне без UI | ❌ Уничтожаются |
| State preservation | ✅ Автоматически (Android), через `kotlinx-serialization` (все) | ⚠️ Только SavedStateHandle |
| Web browser history | ✅ WebNavigationOwner API | ❌ Отдельное решение |
| Типобезопасные аргументы | ✅ @Serializable Config | ⚠️ Bundle / query strings |
| Тестирование | ✅ Unit-тесты без инструментации | ❌ Нужен UI framework |

### RootComponent — точка входа навигации

```kotlin
// shared/src/commonMain/kotlin/com/uikit/navigation/RootComponent.kt

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>
    
    sealed class Child {
        class HomeChild(val component: HomeComponent) : Child()
        class CatalogChild(val component: CatalogComponent) : Child()
        class ProductDetailChild(val component: ProductDetailComponent) : Child()
        class CartChild(val component: CartComponent) : Child()
        class ProfileChild(val component: ProfileComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val deepLinkUrl: String? = null,  // для SSR / Web deep links
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { parseDeepLink(deepLinkUrl) },
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child = when (config) {
        is Config.Home -> RootComponent.Child.HomeChild(
            DefaultHomeComponent(componentContext, storeFactory, ::onNavigate)
        )
        is Config.Catalog -> RootComponent.Child.CatalogChild(
            DefaultCatalogComponent(componentContext, storeFactory, config.categoryId, ::onNavigate)
        )
        is Config.ProductDetail -> RootComponent.Child.ProductDetailChild(
            DefaultProductDetailComponent(componentContext, storeFactory, config.productId, ::onNavigate)
        )
        is Config.Cart -> RootComponent.Child.CartChild(
            DefaultCartComponent(componentContext, storeFactory, ::onNavigate)
        )
        is Config.Profile -> RootComponent.Child.ProfileChild(
            DefaultProfileComponent(componentContext, storeFactory, ::onNavigate)
        )
    }

    /** Единый роутер для ActionConfig.Navigate → Decompose navigation */
    private fun onNavigate(action: ActionConfig.Navigate) {
        when (action.presentation) {
            ActionConfig.Navigate.Presentation.Push ->
                navigation.push(action.toConfig())
            ActionConfig.Navigate.Presentation.Replace ->
                navigation.replaceCurrent(action.toConfig())
            ActionConfig.Navigate.Presentation.Back ->
                navigation.pop()
            ActionConfig.Navigate.Presentation.Modal ->
                navigation.push(action.toConfig()) // modal = overlay в UI слое
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable data object Home : Config
        @Serializable data class Catalog(val categoryId: String? = null) : Config
        @Serializable data class ProductDetail(val productId: String) : Config
        @Serializable data object Cart : Config
        @Serializable data object Profile : Config
    }
}

/** Маппинг ActionConfig.Navigate → Decompose Config (type-safe) */
private fun ActionConfig.Navigate.toConfig(): DefaultRootComponent.Config = when (route) {
    "home" -> DefaultRootComponent.Config.Home
    "catalog" -> DefaultRootComponent.Config.Catalog(params["categoryId"])
    "product" -> DefaultRootComponent.Config.ProductDetail(
        productId = params["productId"] ?: error("productId required")
    )
    "cart" -> DefaultRootComponent.Config.Cart
    "profile" -> DefaultRootComponent.Config.Profile
    else -> error("Unknown route: $route")
}
```

### Связь с ScreenConfig

Ключевая идея: **каждый Decompose Component владеет своим ScreenConfig**.

```kotlin
// shared/src/commonMain/kotlin/com/uikit/screens/CatalogComponent.kt

interface CatalogComponent {
    val screenConfig: Value<ScreenConfig>  // реактивный ScreenConfig
    val store: CatalogStore                // MVIKotlin Store
}

class DefaultCatalogComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val categoryId: String?,
    private val onNavigate: (ActionConfig.Navigate) -> Unit,
) : CatalogComponent, ComponentContext by componentContext {

    override val store: CatalogStore = CatalogStoreFactory(
        storeFactory = storeFactory,
        categoryId = categoryId,
    ).create()

    // ScreenConfig реактивно зависит от State Store
    override val screenConfig: Value<ScreenConfig> =
        store.stateToScreenConfig()

    init {
        // Подписка на Labels (one-shot events)
        store.labels.subscribeScoped { label ->
            when (label) {
                is CatalogStore.Label.NavigateToProduct ->
                    onNavigate(ActionConfig.Navigate(
                        route = "product",
                        params = mapOf("productId" to label.productId),
                    ))
                is CatalogStore.Label.ShowError ->
                    // handle error
                    Unit
            }
        }
    }
}
```

### Web: Decompose + WebNavigationOwner

```kotlin
// shared/src/commonMain/kotlin/com/uikit/navigation/RootComponent.kt

// Добавляем WebNavigationOwner для синхронизации с browser history
class DefaultRootComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    deepLinkUrl: String? = null,
) : RootComponent, WebNavigationOwner, ComponentContext by componentContext {

    // ... (всё из примера выше)

    override val webNavigation: WebNavigation<*> =
        childStackWebNavigation(
            navigator = navigation,
            stack = stack as Value<ChildStack<Config, RootComponent.Child>>,
            serializer = Config.serializer(),
            pathMapper = { child ->
                when (child.instance) {
                    is RootComponent.Child.HomeChild -> "/"
                    is RootComponent.Child.CatalogChild -> "/catalog"
                    is RootComponent.Child.ProductDetailChild -> "/product/${child.configuration.productId}"
                    is RootComponent.Child.CartChild -> "/cart"
                    is RootComponent.Child.ProfileChild -> "/profile"
                }
            },
        )
}
```

---

## 4. MVIKotlin — единый state management

### Почему MVIKotlin а не голый StateFlow

| Критерий | MVIKotlin Store | StateFlow + ViewModel |
|----------|-----------------|----------------------|
| Структурированная бизнес-логика | ✅ Intent → Executor → Message → Reducer → State | ❌ Свободная форма, легко запутаться |
| Unidirectional data flow | ✅ Enforced архитектурой | ⚠️ Зависит от дисциплины |
| Time Travel отладка | ✅ Встроенный — реплей всех событий | ❌ Нет |
| One-shot events (Labels) | ✅ Первоклассный API | ❌ SharedFlow/Channel — хак |
| Testability | ✅ Подмена StoreFactory, фейковый Executor | ⚠️ Сложнее |
| KMP native | ✅ commonMain, ноль зависимости от coroutines в ядре | ⚠️ Нужен kotlinx.coroutines везде |

### Store для экрана каталога

```kotlin
// shared/src/commonMain/kotlin/com/uikit/screens/catalog/CatalogStore.kt

internal interface CatalogStore : Store<CatalogStore.Intent, CatalogStore.State, CatalogStore.Label> {

    @Serializable
    sealed interface Intent {
        data class Search(val query: String) : Intent
        data class SelectCategory(val categoryId: String) : Intent
        data class SelectProduct(val productId: String) : Intent
        data object LoadMore : Intent
        data object Refresh : Intent
    }

    @Serializable
    data class State(
        val products: List<ProductItem> = emptyList(),
        val categories: List<CategoryItem> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,
        val error: String? = null,
        val selectedCategoryId: String? = null,
        val page: Int = 0,
    )

    sealed interface Label {
        data class NavigateToProduct(val productId: String) : Label
        data class ShowError(val message: String) : Label
    }
}
```

### StoreFactory — создание Store с Coroutines

```kotlin
// shared/src/commonMain/kotlin/com/uikit/screens/catalog/CatalogStoreFactory.kt

internal class CatalogStoreFactory(
    private val storeFactory: StoreFactory,
    private val categoryId: String?,
    private val productRepository: ProductRepository = ProductRepository(),
) {
    fun create(): CatalogStore =
        object : CatalogStore, Store<Intent, State, Label> by storeFactory.create(
            name = "CatalogStore",
            initialState = State(selectedCategoryId = categoryId),
            bootstrapper = coroutineBootstrapper {
                dispatch(Action.LoadInitial)
            },
            executorFactory = { ExecutorImpl(productRepository) },
            reducer = ReducerImpl,
        ) {}

    private sealed interface Action {
        data object LoadInitial : Action
    }

    private sealed interface Msg {
        data class ProductsLoaded(val products: List<ProductItem>, val hasMore: Boolean) : Msg
        data class CategoriesLoaded(val categories: List<CategoryItem>) : Msg
        data class MoreProductsLoaded(val products: List<ProductItem>, val hasMore: Boolean) : Msg
        data class SearchUpdated(val query: String) : Msg
        data class CategorySelected(val categoryId: String?) : Msg
        data class Error(val message: String) : Msg
        data object Loading : Msg
        data object LoadingMore : Msg
    }

    private class ExecutorImpl(
        private val productRepository: ProductRepository,
    ) : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                Action.LoadInitial -> loadProducts(page = 0)
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.Search -> {
                    dispatch(Msg.SearchUpdated(intent.query))
                    loadProducts(page = 0)
                }
                is Intent.SelectCategory -> {
                    dispatch(Msg.CategorySelected(intent.categoryId))
                    loadProducts(page = 0)
                }
                is Intent.SelectProduct -> {
                    publish(Label.NavigateToProduct(intent.productId))
                }
                Intent.LoadMore -> {
                    if (!state().isLoadingMore && state().hasMore) {
                        dispatch(Msg.LoadingMore)
                        loadProducts(page = state().page + 1, append = true)
                    }
                }
                Intent.Refresh -> loadProducts(page = 0)
            }
        }

        private fun loadProducts(page: Int, append: Boolean = false) {
            if (!append) dispatch(Msg.Loading)
            scope.launch {
                try {
                    val result = productRepository.getProducts(
                        categoryId = state().selectedCategoryId,
                        query = state().searchQuery,
                        page = page,
                    )
                    if (append) {
                        dispatch(Msg.MoreProductsLoaded(result.items, result.hasMore))
                    } else {
                        dispatch(Msg.ProductsLoaded(result.items, result.hasMore))
                    }
                } catch (e: Exception) {
                    dispatch(Msg.Error(e.message ?: "Unknown error"))
                    publish(Label.ShowError(e.message ?: "Unknown error"))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.ProductsLoaded -> copy(
                products = msg.products, hasMore = msg.hasMore,
                isLoading = false, error = null, page = 0,
            )
            is Msg.CategoriesLoaded -> copy(categories = msg.categories)
            is Msg.MoreProductsLoaded -> copy(
                products = products + msg.products, hasMore = msg.hasMore,
                isLoadingMore = false, page = page + 1,
            )
            is Msg.SearchUpdated -> copy(searchQuery = msg.query)
            is Msg.CategorySelected -> copy(selectedCategoryId = msg.categoryId)
            is Msg.Error -> copy(error = msg.message, isLoading = false, isLoadingMore = false)
            Msg.Loading -> copy(isLoading = true, error = null)
            Msg.LoadingMore -> copy(isLoadingMore = true)
        }
    }
}
```

---

## 5. Интеграция Decompose + MVIKotlin + Config-Driven Engine

### Ключевая идея: State → ScreenConfig — реактивная трансформация

Из Config-Driven архитектуры у нас есть `ScreenConfig` — описание экрана.
Из MVIKotlin у нас есть `State` — текущее состояние бизнес-логики.

**Связь**: `ScreenConfig = f(State, Tokens, StaticConfig)`

```kotlin
// shared/src/commonMain/kotlin/com/uikit/screens/catalog/CatalogScreenConfigMapper.kt

/**
 * Маппер MVIKotlin State → ScreenConfig.
 * Pure function — легко тестировать.
 */
fun CatalogStore.stateToScreenConfig(): Value<ScreenConfig> {
    // Преобразование Value<State> → Value<ScreenConfig> через map
    return stateAsValue().map { state -> state.toScreenConfig() }
}

private fun CatalogStore.State.toScreenConfig(): ScreenConfig = ScreenConfig(
    id = "catalog_screen",
    layout = LayoutConfig.SingleColumn(
        topPlacement = listOf("search", "categories"),
        mainPlacement = listOf("products"),
        bottomPlacement = emptyList(),
    ),
    toolbar = ToolbarConfig(title = "Каталог"),
    sections = buildList {
        // Поисковая строка
        add(SectionConfig(
            id = "search",
            type = "text_field",
            component = TextFieldConfig(
                id = "search_field",
                value = searchQuery,
                placeholder = "Поиск товаров...",
                leadingIcon = "search",
                action = ActionConfig.Custom(
                    type = "intent",
                    payload = buildJsonPayload("type" to "search"),
                ),
            ),
        ))

        // Категории (горизонтальный скролл)
        if (categories.isNotEmpty()) {
            add(SectionConfig(
                id = "categories",
                type = "chip_group",
                component = ChipGroupConfig(
                    id = "category_chips",
                    chips = categories.map { cat ->
                        ChipConfig(
                            id = "cat_${cat.id}",
                            label = cat.name,
                            selected = cat.id == selectedCategoryId,
                            action = ActionConfig.Custom(
                                type = "intent",
                                payload = buildJsonPayload(
                                    "type" to "selectCategory",
                                    "categoryId" to cat.id,
                                ),
                            ),
                        )
                    },
                ),
            ))
        }

        // Список товаров
        add(SectionConfig(
            id = "products",
            type = "product_grid",
            component = when {
                isLoading -> SkeletonGridConfig(id = "skeleton", count = 6)
                error != null -> ErrorConfig(
                    id = "error",
                    message = error,
                    retryAction = ActionConfig.Custom(type = "intent", payload = buildJsonPayload("type" to "refresh")),
                )
                else -> ProductGridConfig(
                    id = "product_grid",
                    items = products.map { product ->
                        ProductCardConfig(
                            id = "product_${product.id}",
                            title = product.name,
                            price = product.formattedPrice,
                            imageUrl = product.imageUrl,
                            action = ActionConfig.Custom(
                                type = "intent",
                                payload = buildJsonPayload(
                                    "type" to "selectProduct",
                                    "productId" to product.id,
                                ),
                            ),
                        )
                    },
                    isLoadingMore = isLoadingMore,
                    hasMore = hasMore,
                )
            }
        ))
    },
)
```

### Обработка ActionConfig.Custom → MVIKotlin Intent

```kotlin
// shared/src/commonMain/kotlin/com/uikit/engine/IntentDispatcher.kt

/**
 * Мост между ActionConfig (Config-Driven) и MVIKotlin Intent.
 * Когда рендерер обрабатывает ActionConfig.Custom(type="intent"),
 * он вызывает этот dispatcher для трансляции в типизированный Intent.
 */
class IntentDispatcher<Intent : Any>(
    private val store: Store<Intent, *, *>,
    private val mapper: (type: String, payload: JsonElement?) -> Intent?,
) {
    fun dispatch(action: ActionConfig.Custom) {
        mapper(action.type, action.payload)?.let { intent ->
            store.accept(intent)
        }
    }
}

// Использование в CatalogComponent:
val intentDispatcher = IntentDispatcher(store) { type, payload ->
    when (type) {
        "search" -> CatalogStore.Intent.Search(payload.getString("query"))
        "selectCategory" -> CatalogStore.Intent.SelectCategory(payload.getString("categoryId"))
        "selectProduct" -> CatalogStore.Intent.SelectProduct(payload.getString("productId"))
        "refresh" -> CatalogStore.Intent.Refresh
        "loadMore" -> CatalogStore.Intent.LoadMore
        else -> null
    }
}
```

### Диаграмма потока данных

```
┌─────────────────────────────────────────────────────────────────────┐
│                              FLOW                                    │
│                                                                      │
│  User Tap                                                            │
│    ↓                                                                 │
│  Renderer (Compose/React) → ActionConfig.Custom(type="intent")      │
│    ↓                                                                 │
│  IntentDispatcher.dispatch(action)                                   │
│    ↓                                                                 │
│  MVIKotlin Store.accept(Intent)                                      │
│    ↓                                                                 │
│  Executor → async work → dispatch(Message)                           │
│    ↓                                                                 │
│  Reducer: State = f(State, Message) — pure function                  │
│    ↓                                                                 │
│  State changed → stateToScreenConfig() → new ScreenConfig            │
│    ↓                                                                 │
│  Decompose Value<ScreenConfig> обновляется                           │
│    ↓                                                                 │
│  Compose: subscribeAsState() → recomposition                         │
│  React: useValue(component.screenConfig) → re-render                │
│    ↓                                                                 │
│  Label emitted → Decompose navigation.push/pop                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 6. SSR для Web: Next.js App Router + Config-Driven

### Стратегия: SSR для первой загрузки, SPA для последующей навигации

```
Первая загрузка (SSR):
  Browser → Next.js Server → API → ScreenConfig JSON
                              ↓
                    Server Component рендерит HTML
                              ↓
                    HTML + RSC Payload → Browser
                              ↓
                    Hydration → Client Components становятся интерактивными

Последующая навигация (SPA/CSR):
  User Click → Decompose navigation → новый Child Component
                              ↓
                    MVIKotlin Store создаётся → загрузка данных
                              ↓
                    State → ScreenConfig → React рендер (client-side)
```

### Архитектура SSR + Config-Driven

```
┌──────────────────────────────────────────────────────────────────┐
│                    Next.js Server (Node.js)                       │
│                                                                   │
│  ┌──────────────┐    ┌──────────────────────┐                    │
│  │ page.tsx      │    │ ScreenConfigLoader   │                    │
│  │ (Server Comp) │───→│ (async fetch API)    │                    │
│  │              │    │ GET /api/screen/:id   │                    │
│  └──────┬───────┘    └──────────┬───────────┘                    │
│         │                       │                                 │
│         │    ScreenConfig JSON  │                                 │
│         ↓                       ↓                                 │
│  ┌──────────────────────────────────┐                            │
│  │ ServerScreenRenderer             │                            │
│  │ (Server Component)               │                            │
│  │ ScreenConfig → static HTML       │                            │
│  │ Нет useState, нет onClick        │                            │
│  └──────────────┬───────────────────┘                            │
│                 │                                                  │
│  ┌──────────────┴───────────────────┐                            │
│  │ ClientScreenHydrator             │                            │
│  │ (Client Component: "use client") │                            │
│  │ + Decompose ComponentContext     │                            │
│  │ + MVIKotlin Store                │                            │
│  │ + Event handlers (onClick, etc.) │                            │
│  └──────────────────────────────────┘                            │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

### Реализация: Next.js App Router Pages

```tsx
// web/app/catalog/page.tsx — Server Component (по умолчанию в Next.js)

import { ServerScreenRenderer } from '@/components/ssr/ServerScreenRenderer'
import { ClientScreenHydrator } from '@/components/ssr/ClientScreenHydrator'
import { fetchScreenConfig } from '@/lib/api'

interface PageProps {
  searchParams: Promise<{ category?: string }>
}

export default async function CatalogPage({ searchParams }: PageProps) {
  const params = await searchParams
  // Загрузка ScreenConfig на сервере — быстро, рядом с API
  const screenConfig = await fetchScreenConfig('catalog', {
    categoryId: params.category,
  })

  return (
    <>
      {/* Статический HTML для SEO + быстрый FCP */}
      <ServerScreenRenderer config={screenConfig} />

      {/* Client Component для интерактивности — hydration */}
      <ClientScreenHydrator
        screenId="catalog"
        initialConfig={screenConfig}
        params={{ categoryId: params.category }}
      />
    </>
  )
}

// Metadata для SEO (генерируется на сервере)
export async function generateMetadata({ searchParams }: PageProps) {
  const params = await searchParams
  return {
    title: params.category
      ? `Каталог — ${params.category}`
      : 'Каталог товаров',
    description: 'Просмотр каталога товаров',
  }
}
```

### Server Component Renderer — статический HTML без JS

```tsx
// web/components/ssr/ServerScreenRenderer.tsx — Server Component

import type { ScreenConfig, ComponentConfig, SectionConfig } from '@uikit/types'
import { resolveTokens } from '@uikit/tokens'

/**
 * Чисто серверный рендер ScreenConfig → HTML.
 * Нет useState, нет useEffect, нет event handlers.
 * Результат = статический HTML для SEO и быстрого FCP.
 */
export function ServerScreenRenderer({ config }: { config: ScreenConfig }) {
  const tokens = resolveTokens(/* brand from env/cookie */)

  return (
    <div data-screen-id={config.id} className="screen-container">
      {config.toolbar && (
        <header className="toolbar">
          <h1>{config.toolbar.title}</h1>
        </header>
      )}
      <div className="screen-layout" data-layout={config.layout.type}>
        {config.sections.map((section) => (
          <ServerSectionRenderer
            key={section.id}
            section={section}
            tokens={tokens}
          />
        ))}
      </div>
    </div>
  )
}

function ServerSectionRenderer({
  section,
  tokens,
}: {
  section: SectionConfig
  tokens: DesignTokens
}) {
  // Рендерим каждый компонент статически
  // onClick, onChange и т.д. будут добавлены при hydration
  return (
    <section data-section-id={section.id}>
      <ServerComponentRenderer config={section.component} tokens={tokens} />
    </section>
  )
}
```

### Client Hydrator — добавляет интерактивность

```tsx
// web/components/ssr/ClientScreenHydrator.tsx
'use client'

import { useEffect, useRef } from 'react'
import { useDecomposeComponent } from '@/hooks/useDecomposeComponent'
import { ScreenRenderer } from '@uikit/renderers'
import type { ScreenConfig } from '@uikit/types'

interface Props {
  screenId: string
  initialConfig: ScreenConfig
  params: Record<string, string | undefined>
}

/**
 * Client Component — hydrates серверный HTML с:
 * - Decompose ComponentContext (lifecycle, navigation)
 * - MVIKotlin Store (state management)
 * - Event handlers (onClick, onChange, onScroll)
 */
export function ClientScreenHydrator({ screenId, initialConfig, params }: Props) {
  const { component, isReady } = useDecomposeComponent(screenId, params)
  const configValue = useScreenConfig(component, initialConfig)

  if (!isReady) {
    // Серверный HTML ещё видим — показываем его пока Store инициализируется
    return null
  }

  return (
    <ScreenRenderer
      config={configValue}
      onAction={(action) => component.handleAction(action)}
    />
  )
}

/** Hook: подписка на Decompose Value<ScreenConfig> */
function useScreenConfig(
  component: ScreenComponent | null,
  fallback: ScreenConfig,
): ScreenConfig {
  const [config, setConfig] = useState(fallback)

  useEffect(() => {
    if (!component) return
    const unsubscribe = component.screenConfig.subscribe((newConfig) => {
      setConfig(newConfig)
    })
    return () => unsubscribe()
  }, [component])

  return config
}
```

---

## 7. Двойная навигация: Decompose (KMP) ↔ Next.js Router (Web)

### Проблема

- **Decompose** управляет навигацией в KMP shared code → Child Stack
- **Next.js App Router** управляет URL-based routing → file-system pages
- Нужна **синхронизация** между ними

### Стратегия A: Decompose как единственный роутер (рекомендуется для SPA)

```
Next.js: только один page — app/[[...slug]]/page.tsx (catch-all)
         SSR первой страницы по URL
         Далее — Decompose управляет навигацией
         WebNavigationOwner синхронизирует URL
```

```tsx
// web/app/[[...slug]]/page.tsx — catch-all route

import { DecomposeRoot } from '@/components/DecomposeRoot'
import { fetchScreenConfigForUrl } from '@/lib/api'

export default async function CatchAllPage({
  params,
}: {
  params: Promise<{ slug?: string[] }>
}) {
  const { slug } = await params
  const url = '/' + (slug?.join('/') ?? '')

  // SSR: загружаем начальный ScreenConfig для URL
  const initialScreenConfig = await fetchScreenConfigForUrl(url)

  return (
    <DecomposeRoot
      initialUrl={url}
      initialScreenConfig={initialScreenConfig}
    />
  )
}
```

```tsx
// web/components/DecomposeRoot.tsx
'use client'

import { useEffect, useMemo } from 'react'
import { DefaultComponentContext } from '@decompose/core'
import { withWebHistory } from '@decompose/web'
import { DefaultRootComponent } from '@uikit/shared'
import { DefaultStoreFactory } from '@mvikotlin/main'
import { RootContent } from './RootContent'

export function DecomposeRoot({
  initialUrl,
  initialScreenConfig,
}: {
  initialUrl: string
  initialScreenConfig: ScreenConfig
}) {
  const root = useMemo(() => {
    // Создаём Decompose root component с web history
    return withWebHistory { url, stateKeeper ->
      DefaultRootComponent(
        componentContext = DefaultComponentContext(
          lifecycle = LifecycleRegistry(),
          stateKeeper = stateKeeper,
        ),
        storeFactory = DefaultStoreFactory(),
        deepLinkUrl = url ?? initialUrl,
      )
    }
  }, [])

  return <RootContent component={root} initialScreenConfig={initialScreenConfig} />
}
```

### Стратегия B: Next.js Router + Decompose per-page (рекомендуется для SSR-heavy)

```
Next.js: стандартная file-system маршрутизация
         Каждая page.tsx — Server Component с SSR
         Decompose Component создаётся per-page для state management
         Навигация между страницами = Next.js <Link> (prefetch)
```

```tsx
// web/app/catalog/page.tsx
export default async function CatalogPage() { /* SSR */ }

// web/app/product/[id]/page.tsx
export default async function ProductPage() { /* SSR */ }

// web/app/cart/page.tsx
export default async function CartPage() { /* SSR */ }
```

```tsx
// web/hooks/usePageComponent.ts
'use client'

import { useMemo, useEffect } from 'react'
import { DefaultComponentContext } from '@decompose/core'

/**
 * Создаёт Decompose Component для конкретной страницы.
 * Lifecycle привязан к монтированию/размонтированию React компонента.
 */
export function usePageComponent<T>(
  factory: (componentContext: ComponentContext, storeFactory: StoreFactory) => T,
): T {
  const component = useMemo(() => {
    const lifecycle = new LifecycleRegistry()
    const componentContext = new DefaultComponentContext(lifecycle)
    const storeFactory = new DefaultStoreFactory()
    const comp = factory(componentContext, storeFactory)
    lifecycle.resume()
    return { comp, lifecycle }
  }, [])

  useEffect(() => {
    return () => component.lifecycle.destroy()
  }, [])

  return component.comp
}
```

### Выбор стратегии

| Фактор | Стратегия A (Decompose SPA) | Стратегия B (Next.js Pages) |
|--------|----------------------------|----------------------------|
| SEO | Только первая страница SSR | Каждая страница SSR ✅ |
| TTI (Time to Interactive) | Быстрый после первой загрузки | Каждая страница = hydration |
| Shared KMP навигация | ✅ Полностью общий код с Android | ⚠️ Навигация разная |
| Code splitting | ⚠️ Весь JS один bundle | ✅ Per-page chunks |
| Переходы между страницами | Мгновенные (SPA) | Быстрые (prefetch) |
| Сложность | Средняя | Низкая |

**Рекомендация**: **Стратегия B** для 5000+ приложений — лучший SEO, simpler mental model, проще для большой команды. Decompose используется per-page для state management, а не для навигации.

---

## 8. SDUI + SSR: серверный рендеринг динамических экранов

### Проблема

SDUI экраны приходят с сервера в виде JSON → обычно рендерятся client-side. Но мы хотим SSR для SEO и FCP.

### Решение: Server-Side Config Resolution

```
┌────────────┐     ┌────────────────┐     ┌──────────────┐
│ Browser     │────→│ Next.js Server │────→│ SDUI API     │
│ GET /page   │     │ (page.tsx SSR) │     │ GET /screen  │
└────────────┘     └────────┬───────┘     └──────┬───────┘
                            │                     │
                            │  ScreenConfig JSON  │
                            ↓                     │
                   ┌──────────────────┐           │
                   │ Server Component │←──────────┘
                   │ renders HTML     │
                   │ from ScreenConfig│
                   └────────┬─────────┘
                            │ HTML + RSC Payload
                            ↓
                   ┌──────────────────┐
                   │ Client Hydration │
                   │ + MVIKotlin      │
                   │ + Interactivity  │
                   └──────────────────┘
```

### Server-Side SDUI Renderer

```tsx
// web/app/sdui/[screenId]/page.tsx — SSR для SDUI экранов

import { ServerScreenRenderer } from '@/components/ssr/ServerScreenRenderer'
import { ClientScreenHydrator } from '@/components/ssr/ClientScreenHydrator'

interface Props {
  params: Promise<{ screenId: string }>
}

export default async function SDUIPage({ params }: Props) {
  const { screenId } = await params

  // Загрузка SDUI конфига на сервере — быстро, без CORS, с кэшированием
  const response = await fetch(
    `${process.env.API_BASE_URL}/api/sdui/screen/${screenId}`,
    {
      next: { revalidate: 60 }, // ISR: ревалидация каждые 60 секунд
      headers: { 'X-Brand': process.env.BRAND_ID ?? 'default' },
    },
  )
  const sduiResponse = await response.json()
  const screenConfig = sduiResponse.screen

  return (
    <>
      {/* SSR рендер SDUI экрана — полный HTML для SEO */}
      <ServerScreenRenderer config={screenConfig} />

      {/* Hydration — добавляет клики, анимации, real-time updates */}
      <ClientScreenHydrator
        screenId={screenId}
        initialConfig={screenConfig}
        params={{}}
      />
    </>
  )
}

// ISR (Incremental Static Regeneration) — для часто посещаемых экранов
export async function generateStaticParams() {
  // Предгенерация самых популярных SDUI экранов при build
  const popularScreens = await fetch(`${process.env.API_BASE_URL}/api/sdui/popular`)
  const screens = await popularScreens.json()
  return screens.map((s: { id: string }) => ({ screenId: s.id }))
}
```

### SSR Кеширование для SDUI

```tsx
// web/lib/api.ts

import { unstable_cache } from 'next/cache'

/**
 * Загрузка ScreenConfig с кэшированием:
 * - revalidate: 60 — ISR, пересоздаётся раз в минуту
 * - tags: ['screen', screenId] — инвалидация по тегу
 */
export const fetchScreenConfig = unstable_cache(
  async (screenId: string, params?: Record<string, string | undefined>) => {
    const url = new URL(`${process.env.API_BASE_URL}/api/screen/${screenId}`)
    if (params) {
      Object.entries(params).forEach(([k, v]) => {
        if (v) url.searchParams.set(k, v)
      })
    }
    const res = await fetch(url.toString())
    return res.json() as Promise<ScreenConfig>
  },
  ['screen-config'],
  { revalidate: 60, tags: ['screen'] },
)
```

---

## 9. Перфоманс-архитектура

### Android (Compose + Decompose + MVIKotlin)

| Оптимизация | Как работает | Влияние |
|-------------|-------------|---------|
| **Decompose: Components в back stack живы** | Нет пересоздания при навигации назад | Мгновенный «назад» |
| **MVIKotlin: Reducer на main thread** | State обновляется синхронно, UI перерисовывается в следующем frame | 0 frames dropped на state update |
| **MVIKotlin: Executor → background** | Тяжёлая работа на `Dispatchers.Default` | UI thread свободен |
| **Config immutability** | `@Serializable data class` = structural equality | Compose пропускает recomposition |
| **`@Stable` ScreenConfig** | Compose Compiler оптимизирует | Минимум recomposition |
| **ComponentRegistry O(1)** | HashMap lookup по type | Нет reflection, нет linear scan |
| **StyleResolver pure function** | `Config × Tokens → Style` кэшируется через `remember` | Zero allocation на hot path |
| **LazyColumn + keys** | `key = section.id` для стабильных items | Нет diff на весь список |

### Web (Next.js + React + SSR)

| Оптимизация | Как работает | Влияние |
|-------------|-------------|---------|
| **SSR → HTML сразу** | FCP < 1с — контент виден до загрузки JS | SEO + воспринимаемая скорость |
| **Streaming SSR** | `Suspense` границы, постепенная отправка HTML | LCP < 2с даже для тяжёлых страниц |
| **React Server Components** | Статический UI (лейауты, заголовки, текст) — 0 KB JS | Меньше bundle = быстрее hydration |
| **Selective Hydration** | Только интерактивные компоненты гидратируются | TTI < 3с |
| **ISR для SDUI** | `revalidate: 60` — кэш на edge | Нет запроса к API на каждый визит |
| **Code splitting** | Next.js auto-splits по routes | Загружается только нужный код |
| **`React.memo` + config equality** | Shallow compare для React.memo | Минимум re-renders |
| **CSS Custom Properties** | Tokens как CSS variables — 1 CSS файл для всех тем | Zero runtime стоимость переключения брендов |

### Метрики целевые

| Метрика | Android (Compose) | Web (SSR) |
|---------|-------------------|-----------|
| **Cold start → first frame** | < 800ms | — |
| **FCP (First Contentful Paint)** | — | < 1.0с |
| **LCP (Largest Contentful Paint)** | — | < 2.0с |
| **TTI (Time to Interactive)** | — | < 3.0с |
| **Navigation (screen → screen)** | < 100ms | < 200ms (SPA) / < 500ms (SSR page) |
| **State update → UI** | 1 frame (16ms) | 1 React reconciliation cycle |
| **Bundle size (JS)** | — | < 150KB gzipped (per page) |

---

## 10. Структура проекта

```
uikit/
├── shared/                                    ← KMP shared module
│   ├── src/
│   │   ├── commonMain/
│   │   │   ├── kotlin/com/uikit/
│   │   │   │   ├── tokens/
│   │   │   │   │   └── DesignTokens.kt          ← generated
│   │   │   │   ├── config/                       ← Layer 2: Config-Driven
│   │   │   │   │   ├── ComponentConfig.kt
│   │   │   │   │   ├── ActionConfig.kt
│   │   │   │   │   ├── ScreenConfig.kt
│   │   │   │   │   └── SectionConfig.kt
│   │   │   │   ├── registry/
│   │   │   │   │   └── ComponentRegistry.kt
│   │   │   │   ├── navigation/                   ← Layer 4: Decompose
│   │   │   │   │   ├── RootComponent.kt
│   │   │   │   │   ├── DefaultRootComponent.kt
│   │   │   │   │   └── DeepLinkParser.kt
│   │   │   │   ├── screens/                      ← Decompose + MVIKotlin per screen
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeComponent.kt
│   │   │   │   │   │   ├── HomeStore.kt
│   │   │   │   │   │   ├── HomeStoreFactory.kt
│   │   │   │   │   │   └── HomeScreenMapper.kt
│   │   │   │   │   ├── catalog/
│   │   │   │   │   │   ├── CatalogComponent.kt
│   │   │   │   │   │   ├── CatalogStore.kt
│   │   │   │   │   │   ├── CatalogStoreFactory.kt
│   │   │   │   │   │   └── CatalogScreenMapper.kt
│   │   │   │   │   └── product/
│   │   │   │   │       ├── ProductDetailComponent.kt
│   │   │   │   │       ├── ProductDetailStore.kt
│   │   │   │   │       ├── ProductDetailStoreFactory.kt
│   │   │   │   │       └── ProductDetailScreenMapper.kt
│   │   │   │   ├── sdui/                         ← Layer 3: SDUI Engine
│   │   │   │   │   ├── SDUIClient.kt
│   │   │   │   │   └── SDUIResponse.kt
│   │   │   │   └── engine/
│   │   │   │       ├── IntentDispatcher.kt
│   │   │   │       └── ActionRouter.kt
│   │   │   └── resources/
│   │   ├── androidMain/                          ← Android-specific
│   │   └── jsMain/                               ← Kotlin/JS (для Web, если используется KMP)
│   └── build.gradle.kts
│
├── android/                                      ← Android app
│   ├── app/
│   │   └── src/main/kotlin/.../
│   │       ├── MainActivity.kt                   ← defaultComponentContext()
│   │       └── ui/
│   │           ├── RootContent.kt                ← Compose: Children(stack)
│   │           └── renderers/                    ← Component Renderers
│   │               ├── ButtonRenderer.kt
│   │               ├── CardRenderer.kt
│   │               └── ScreenRenderer.kt
│   └── build.gradle.kts
│
├── web/                                          ← Next.js app
│   ├── app/                                      ← App Router
│   │   ├── layout.tsx                            ← Root layout (Server Component)
│   │   ├── page.tsx                              ← Home page (SSR)
│   │   ├── catalog/
│   │   │   └── page.tsx                          ← Catalog (SSR + Hydration)
│   │   ├── product/
│   │   │   └── [id]/
│   │   │       └── page.tsx                      ← Product Detail (SSR)
│   │   ├── cart/
│   │   │   └── page.tsx
│   │   ├── sdui/
│   │   │   └── [screenId]/
│   │   │       └── page.tsx                      ← Dynamic SDUI screens (SSR)
│   │   └── api/                                  ← Route Handlers (BFF)
│   │       └── screen/
│   │           └── [id]/
│   │               └── route.ts
│   ├── components/
│   │   ├── ssr/
│   │   │   ├── ServerScreenRenderer.tsx          ← Server Component
│   │   │   └── ClientScreenHydrator.tsx          ← Client Component
│   │   └── renderers/                            ← React Component Renderers
│   │       ├── ButtonRenderer.tsx
│   │       ├── CardRenderer.tsx
│   │       └── ScreenRenderer.tsx
│   ├── hooks/
│   │   ├── usePageComponent.ts
│   │   └── useScreenConfig.ts
│   ├── lib/
│   │   ├── api.ts                                ← fetchScreenConfig (cached)
│   │   └── tokens.ts
│   ├── next.config.js
│   └── package.json
│
└── tokens/                                       ← Design Tokens (JSON source)
    ├── foundation.json
    ├── semantic.json
    ├── component.json
    └── brands/
        ├── default.json
        └── brand-alpha.json
```

---

## 11. Примеры кода: полный flow

### Android: MainActivity → Decompose → Compose

```kotlin
// android/app/src/main/kotlin/.../MainActivity.kt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = DefaultStoreFactory(),
            deepLinkUrl = intent.data?.toString(),
        )

        setContent {
            UIKitTheme {
                RootContent(component = root)
            }
        }
    }
}
```

```kotlin
// android/app/src/main/kotlin/.../ui/RootContent.kt

@Composable
fun RootContent(component: RootComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade() + scale()),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.HomeChild ->
                HomeScreen(instance.component)
            is RootComponent.Child.CatalogChild ->
                CatalogScreen(instance.component)
            is RootComponent.Child.ProductDetailChild ->
                ProductDetailScreen(instance.component)
            is RootComponent.Child.CartChild ->
                CartScreen(instance.component)
            is RootComponent.Child.ProfileChild ->
                ProfileScreen(instance.component)
        }
    }
}
```

```kotlin
// android/app/src/main/kotlin/.../ui/screens/CatalogScreen.kt

@Composable
fun CatalogScreen(component: CatalogComponent) {
    val screenConfig by component.screenConfig.subscribeAsState()

    // Универсальный ScreenRenderer — тот же что в UNIFIED_UIKIT_ARCHITECTURE
    ScreenRenderer(
        config = screenConfig,
        onAction = { action ->
            when (action) {
                is ActionConfig.Custom -> component.intentDispatcher.dispatch(action)
                is ActionConfig.Navigate -> { /* handled by component via Label */ }
                else -> { /* other actions */ }
            }
        },
    )
}
```

### Web: Next.js SSR → React Hydration

```tsx
// web/app/product/[id]/page.tsx — Server Component

import { ServerScreenRenderer } from '@/components/ssr/ServerScreenRenderer'
import { ProductClientWrapper } from './ProductClientWrapper'
import { fetchScreenConfig } from '@/lib/api'

interface Props {
  params: Promise<{ id: string }>
}

export default async function ProductPage({ params }: Props) {
  const { id } = await params
  const screenConfig = await fetchScreenConfig('product_detail', { productId: id })

  return (
    <>
      <ServerScreenRenderer config={screenConfig} />
      <ProductClientWrapper productId={id} initialConfig={screenConfig} />
    </>
  )
}

// SEO metadata
export async function generateMetadata({ params }: Props) {
  const { id } = await params
  const product = await fetchProduct(id)
  return {
    title: product?.name ?? 'Товар',
    description: product?.description ?? '',
    openGraph: { images: product?.imageUrl ? [product.imageUrl] : [] },
  }
}
```

```tsx
// web/app/product/[id]/ProductClientWrapper.tsx
'use client'

import { usePageComponent } from '@/hooks/usePageComponent'
import { ScreenRenderer } from '@/components/renderers/ScreenRenderer'
import type { ScreenConfig } from '@uikit/types'

export function ProductClientWrapper({
  productId,
  initialConfig,
}: {
  productId: string
  initialConfig: ScreenConfig
}) {
  const component = usePageComponent((ctx, storeFactory) =>
    new DefaultProductDetailComponent(ctx, storeFactory, productId)
  )

  const config = useScreenConfig(component, initialConfig)

  return (
    <ScreenRenderer
      config={config}
      onAction={(action) => component.handleAction(action)}
    />
  )
}
```

---

## 12. Стратегия миграции

### Фаза 1: Добавление MVIKotlin (2–3 недели)

1. Добавить зависимость `mvikotlin`, `mvikotlin-main`, `mvikotlin-extensions-coroutines`
2. Для каждого экрана создать `Store` (Interface + Factory)
3. Перенести бизнес-логику из ViewModel/StateFlow в `Executor`
4. Перенести state transformations в `Reducer`
5. Навигационные события → `Labels`
6. **Не трогать UI** — рендереры остаются

### Фаза 2: Добавление Decompose (2–3 недели)

1. Добавить зависимость `decompose`, `decompose-extensions-compose`
2. Создать `RootComponent` с `ChildStack`
3. Обернуть каждый экран в Decompose Component
4. Заменить `ActionConfig.Navigate(route)` → `navigation.push(Config)`
5. Перенести deep linking в `parseDeepLink()`
6. Для Web: добавить `WebNavigationOwner`

### Фаза 3: Добавление SSR (3–4 недели)

1. Создать Next.js проект с App Router
2. Перенести React renderers → разделить Server/Client Components
3. Реализовать `ServerScreenRenderer` (статический HTML)
4. Реализовать `ClientScreenHydrator` (интерактивность)
5. Добавить `fetchScreenConfig` с кэшированием
6. Настроить ISR для SDUI экранов
7. Проверить Core Web Vitals (FCP, LCP, TTI)

### Фаза 4: Оптимизация (1–2 недели)

1. Профилирование: Android Compose Layout Inspector, Chrome DevTools
2. Настройка Streaming SSR с `<Suspense>` boundary
3. Prefetching для Next.js `<Link>`
4. MVIKotlin TimeTravelStoreFactory в debug builds
5. Нагрузочное тестирование SDUI API

---

## 13. Выводы и рекомендации

### Совместимость — полная

| Компонент | Совместим с UIKit? | Как интегрируется |
|-----------|-------------------|-------------------|
| **Decompose** | ✅ | NavigationOwner для Child Stack; маппинг `ActionConfig.Navigate → Config` |
| **MVIKotlin** | ✅ | Store заменяет ViewModel; `State → ScreenConfig` маппер; `Labels → navigation` |
| **SSR (Next.js)** | ✅ | Server Components рендерят ScreenConfig → HTML; Client Components гидратируют |
| **SDUI + SSR** | ✅ | JSON с сервера → SSR рендер → hydration с MVIKotlin |

### Что даёт каждый компонент

```
Decompose:
  ✅ Типобезопасная навигация в shared KMP коде
  ✅ Lifecycle-aware компоненты (back stack работает)
  ✅ Автоматическое сохранение/восстановление стека
  ✅ Web browser history синхронизация
  ✅ Unit-тесты навигации без инструментации

MVIKotlin:
  ✅ Предсказуемый state flow (Intent → State)
  ✅ Reducer = pure function → легко тестировать
  ✅ Time Travel отладка
  ✅ Labels для one-shot events (навигация, ошибки)
  ✅ Structured concurrency через CoroutineExecutor

SSR (Next.js):
  ✅ SEO — поисковики видят полный HTML
  ✅ FCP < 1с — контент виден до загрузки JS
  ✅ ISR — кэш SDUI экранов на edge
  ✅ React Server Components — 0 KB JS для статичного UI
  ✅ Streaming — постепенная отдача HTML

Перфоманс:
  ✅ Android: 1 frame state update, мгновенная навигация «назад»
  ✅ Web: SSR + Hydration + Code splitting + ISR
  ✅ Общий: MVIKotlin Reducer на main thread, async в Executor
```

### Рекомендуемая архитектура (итого)

```
Android: Decompose (навигация) + MVIKotlin (state) + Config-Driven (UI)
    ↕ общий KMP код: Config, Store, Component
Web:     Next.js (SSR + routing) + MVIKotlin* (state) + Config-Driven (UI)

* MVIKotlin на Web через KMP jsMain ИЛИ аналог на TypeScript
```

---

## 14. Источники

- **Decompose**: https://arkivanov.github.io/Decompose/ — lifecycle-aware components + navigation (v3.5.0)
- **MVIKotlin**: https://arkivanov.github.io/MVIKotlin/ — MVI state management framework (v4.3.0)
- **Next.js App Router**: https://nextjs.org/docs/app — SSR, RSC, Streaming, ISR
- **Decompose + MVIKotlin TodoApp**: https://github.com/IlyaGulya/TodoAppDecomposeMviKotlin — reference integration
- **Decompose Web Navigation API**: https://arkivanov.github.io/Decompose/navigation/web-navigation/
- **React Server Components**: https://react.dev/reference/rsc/server-components
- **Yandex DivKit**: https://divkit.tech — SDUI reference (JSON → native)
- **Airbnb Ghost Platform**: https://medium.com/airbnb-engineering — SDUI architecture inspiration
- **UNIFIED_UIKIT_ARCHITECTURE.md** — базовая архитектура Config-Driven + SDUI + Design Tokens
