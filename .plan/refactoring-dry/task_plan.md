# Рефакторинг DRY-нарушений в UIKit Common

**Цель:** Устранить 10 DRY-нарушений в `core/uikit/common`, подготовив архитектуру к масштабированию до 50+ компонентов без copy-paste.

**Принципы:**
- Не нарушать `@JsExport @Serializable` контракт (KMP/JS совместимость)
- SSR-безопасность: все resolvers — stateless, pure functions, детерминизм
- Обратная совместимость: public API компонентов не меняется
- Инкрементальный рефакторинг: каждая фаза компилируется и работает отдельно
- Все числовые значения — Double, все цвета — String (hex)

**Ограничения KMP/JS:**
- `@JsExport data class` не поддерживает интерфейсы с default-реализациями
- `@JsExport` не поддерживает sealed classes
- Наследование `@JsExport data class` невозможно
- Kotlin extension functions НЕ экспортируются в JS — но допустимы для internal использования в Kotlin

---

## Фазы

### Phase 1: InteractiveColorResolver — вынос цветовой матрицы [NOT STARTED]
**Решает:** проблемы #1 (цветовая матрица заперта в Button), #2 (disabled ColorSet), #5 (border per-intent вместо per-variant), #6 (два алгоритма surface-aware)
**Критичность:** 🔴 Высокая — максимальный эффект, блокирует масштабирование

**Что делать:**
1. Создать `foundation/InteractiveColorResolver.kt` — `@JsExport object`
2. Перенести из `ButtonStyleResolver` методы: `solidColors`, `softColors`, `surfaceColors`, `outlineColors`, `ghostColors`
3. Добавить метод `resolveDisabled(tokens) → ColorSet`
4. Обновить `ButtonStyleResolver.resolveColors()` → делегировать в `InteractiveColorResolver`
5. Обновить `IconButtonStyleResolver` → использовать `InteractiveColorResolver` напрямую вместо фиктивного `ButtonConfig`
6. Удалить `SurfaceAwareColorResolver` после интеграции его логики в `InteractiveColorResolver.softColors()`

**Файлы:**
- СОЗДАТЬ: `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/InteractiveColorResolver.kt`
- ИЗМЕНИТЬ: `ButtonStyleResolver.kt` — делегировать цвета
- ИЗМЕНИТЬ: `IconButtonStyleResolver.kt` — убрать фиктивный ButtonConfig
- УДАЛИТЬ: `SurfaceAwareColorResolver.kt` (после миграции логики)

**Детали API:**
```kotlin
@JsExport
object InteractiveColorResolver {
    fun resolve(
        variant: VisualVariant,
        intent: ColorIntent,
        tokens: DesignTokens,
        surfaceContext: SurfaceContext? = null,
    ): ColorSet
    
    fun resolveDisabled(tokens: DesignTokens): ColorSet
}
```

**Тесты:** после рефакторинга ButtonStyleResolver и IconButtonStyleResolver должны возвращать ИДЕНТИЧНЫЕ результаты.

---

### Phase 2: Vertical Layout в ComponentSizeResolver [NOT STARTED]
**Решает:** проблему #3 (vertical layout copy-paste)
**Критичность:** 🟡 Средняя

**Что делать:**
1. Добавить в `ComponentSizeResolver` data class `VerticalLayout` и метод `resolveVerticalLayout()`
2. Обновить `ButtonStyleResolver.resolve()` — использовать новый метод
3. Обновить `SegmentedControlStyleResolver.resolve()` — использовать новый метод

**Файлы:**
- ИЗМЕНИТЬ: `ComponentSizeResolver.kt` — добавить `VerticalLayout` + `resolveVerticalLayout()`
- ИЗМЕНИТЬ: `ButtonStyleResolver.kt` — использовать `resolveVerticalLayout()`
- ИЗМЕНИТЬ: `SegmentedControlStyleResolver.kt` — использовать `resolveVerticalLayout()`

**Детали API:**
```kotlin
@JsExport
@Serializable
data class VerticalLayout(
    val height: Double,
    val paddingV: Double,
)

// В ComponentSizeResolver:
fun resolveVerticalLayout(scale: ControlSizeScale, isVertical: Boolean): VerticalLayout
```

**Формула (текущая, не меняется):**
```
if (isVertical) {
    paddingV = (scale.height - scale.lineHeight) / 2.0
    height = scale.iconSize + scale.iconGap + scale.lineHeight + 2.0 * paddingV
} else {
    paddingV = 0.0
    height = scale.height
}
```

---

### Phase 3: Делегирование resolveDefault → resolve [NOT STARTED]
**Решает:** проблему #9 (дублирование в SegmentedControlStyleResolver)
**Критичность:** 🟡 Средняя — тривиальный fix

**Что делать:**
1. `resolveDefault` вызывает `resolve()` с дефолтным конфигом вместо дублирования тела

**Файлы:**
- ИЗМЕНИТЬ: `SegmentedControlStyleResolver.kt`

**До:**
```kotlin
@JsName("resolveDefault")
fun resolve(tokens: DesignTokens): ResolvedSegmentedControlStyle {
    val scale = ComponentSizeResolver.resolve(...)
    // ...11 полей скопированы из основного resolve()
}
```

**После:**
```kotlin
@JsName("resolveDefault")
fun resolve(tokens: DesignTokens): ResolvedSegmentedControlStyle =
    resolve(
        SegmentedControlConfig(options = emptyArray(), selectedId = ""),
        tokens,
    )
```

---

### Phase 4: Константа TRANSPARENT + ColorConstants [NOT STARTED]
**Решает:** проблему #4 (magic string "transparent" 25+ раз)
**Критичность:** 🟡 Средняя — тривиальный fix, улучшает читаемость

**Что делать:**
1. Создать `foundation/ColorConstants.kt` с `const val TRANSPARENT = "transparent"` и `const val SHADOW_NONE = "none"`
2. Заменить все вхождения `"transparent"` → `ColorConstants.TRANSPARENT`
3. Заменить `"none"` для shadow → `ColorConstants.SHADOW_NONE`

**Файлы:**
- СОЗДАТЬ: `core/uikit/common/src/commonMain/kotlin/com/uikit/foundation/ColorConstants.kt`
- ИЗМЕНИТЬ: `ButtonStyleResolver.kt` (~20 замен)
- ИЗМЕНИТЬ: `SegmentedControlStyleResolver.kt` (~4 замены)
- ИЗМЕНИТЬ: `SurfaceStyleResolver.kt` (~3 замены)
- ИЗМЕНИТЬ: `InteractiveColorResolver.kt` (если создан в Phase 1)

**Примечание:** Если Phase 1 выполнена первой, замены в Button/IconButton не нужны — они уже в `InteractiveColorResolver`.

---

### Phase 5: Extension DesignTokens.resolveSize() [NOT STARTED]
**Решает:** проблему #10 (boilerplate вызов ComponentSizeResolver)
**Критичность:** 🟢 Низкая — convenience

**Что делать:**
1. Добавить extension function в `ComponentSizeResolver.kt`:
   ```kotlin
   fun DesignTokens.resolveSize(size: ComponentSize): ControlSizeScale =
       ComponentSizeResolver.resolve(size, controls, scaleFactor)
   ```
2. Обновить все StyleResolver-ы использовать `tokens.resolveSize(config.size)`

**⚠️ Ограничение:** Extension functions НЕ экспортируются в JS через `@JsExport`. Это допустимо, т.к. extension используется ТОЛЬКО внутри Kotlin StyleResolvers (которые сами @JsExport, но внутренне вызывают Kotlin-код).

**Файлы:**
- ИЗМЕНИТЬ: `ComponentSizeResolver.kt` — добавить extension
- ИЗМЕНИТЬ: `ButtonStyleResolver.kt`
- ИЗМЕНИТЬ: `IconButtonStyleResolver.kt`
- ИЗМЕНИТЬ: `SegmentedControlStyleResolver.kt`

---

### Phase 6: Документирование KMP trade-offs (#7, #8) [NOT STARTED]
**Решает:** проблемы #7 (isInteractive copy-paste) и #8 (общие поля Config)
**Критичность:** 🟢 Низкая — осознанное архитектурное решение

**Что делать:**
Проблемы #7 и #8 — это **ограничения** `@JsExport @Serializable data class`:
- Нельзя наследовать интерфейсы с default implementations для JS
- Нельзя использовать sealed interfaces с @JsExport
- Extension properties/functions не видны из JS

**Решение:** признать как осознанный trade-off и задокументировать.

Для #7 (`isInteractive`):
- Оставить копию в каждом Config — это 1 строка, не критично
- Рассмотреть extension для Kotlin-only потребителей (Compose)

Для #8 (общие поля `id`, `testTag`, `visibility`):
- Оставить как есть — копирование 3-6 полей в каждый Config
- При масштабировании до 50 компонентов → рассмотреть KSP code generation

**Файлы:**
- ИЗМЕНИТЬ: `AGENTS.md` — добавить секцию "Known KMP Trade-offs"

---

## Порядок выполнения

```
Phase 1 (InteractiveColorResolver) ─── максимальный эффект
    │
Phase 2 (VerticalLayout) ─── независима, можно параллельно с 1
    │
Phase 3 (resolveDefault → resolve) ─── после Phase 2 (использует resolveVerticalLayout)
    │
Phase 4 (ColorConstants) ─── после Phase 1 (меньше файлов для правки)
    │
Phase 5 (DesignTokens.resolveSize) ─── после Phases 1-4
    │
Phase 6 (Документация) ─── в любой момент
```

## Зависимости между фазами

| Фаза | Зависит от | Блокирует |
|------|-----------|-----------|
| 1    | —         | 4 (частично) |
| 2    | —         | 3 |
| 3    | 2         | — |
| 4    | 1 (опционально) | — |
| 5    | —         | — |
| 6    | —         | — |

## Метрики успеха

- [ ] ButtonStyleResolver уменьшится с ~290 до ~60 строк
- [ ] IconButtonStyleResolver не использует фиктивный ButtonConfig
- [ ] Новый компонент с VisualVariant×ColorIntent добавляется за 5 строк (вызов InteractiveColorResolver)
- [ ] Вертикальный layout задаётся 1 вызовом вместо copy-paste
- [ ] 0 вхождений magic string "transparent" в StyleResolver-ах
- [ ] Проект компилируется после каждой фазы: `./gradlew :core:uikit:common:build`
