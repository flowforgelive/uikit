# Task Plan: Рефакторинг размеров и pixel-perfect соответствие React ↔ Compose

## Goal
Устранить все hardcoded значения в View-слое (React CSS + Compose), привести SegmentedControl к стандарту Button, внедрить автоматические lint-проверки, обеспечив pixel-perfect соответствие между платформами при масштабировании на 10 000+ приложений.

## Current Phase
Phase 7

## Phases

### Phase 1: Расширение SegmentedControlSizes в Common
- [ ] Добавить `fontWeight: Int` в `SegmentedControlSizes`
- [ ] Добавить `letterSpacing: Double` в `SegmentedControlSizes`
- [ ] Пробросить `scale.fontWeight` и `scale.letterSpacing` в `resolve()`
- [ ] Пробросить в overload `resolveDefault()`
- [ ] Перенести `TRACK_PADDING` → `InteractiveControlTokens.segmentedControlTrackPadding`
- [ ] Добавить `spinnerStrokeWidth: Double = 2.0` в `DesignTokens`
- **Файлы**: `SegmentedControlStyleResolver.kt`, `InteractiveControlTokens.kt`, `DesignTokens.kt`
- **Status:** ✅ complete

### Phase 2: Исправление React SegmentedControl
- [ ] CSS: заменить `padding-inline: 12px` → `var(--sc-padding-h)`
- [ ] CSS: заменить `font-weight: 500` → `var(--sc-font-weight)`
- [ ] CSS: добавить `letter-spacing: var(--sc-letter-spacing)`
- [ ] TSX: добавить `--sc-padding-h`, `--sc-font-weight`, `--sc-letter-spacing` в inline style
- [ ] CSS: заменить spinner `border: 2px` → `var(--btn-spinner-stroke)` в ButtonView.module.css
- [ ] CSS: заменить spinner `border: 2px` → `var(--ib-spinner-stroke)` в IconButtonView.module.css
- [ ] TSX: передать spinner stroke width через CSS-переменные в ButtonView.tsx и IconButtonView.tsx
- **Файлы**: `SegmentedControlView.module.css`, `SegmentedControlView.tsx`, `ButtonView.module.css`, `ButtonView.tsx`, `IconButtonView.module.css`, `IconButtonView.tsx`
- **Status:** ✅ complete

### Phase 3: Исправление Compose Views
- [ ] SegmentedControlView.kt: добавить `fontWeight = FontWeight(style.sizes.fontWeight)` во все `Text()`
- [ ] SegmentedControlView.kt: добавить `letterSpacing = style.sizes.letterSpacing.sp` во все `Text()`
- [ ] ButtonView.kt: заменить `strokeWidth = 2.dp` → `tokens.spinnerStrokeWidth.dp`
- [ ] IconButtonView.kt: заменить `strokeWidth = 2.dp` → `tokens.spinnerStrokeWidth.dp`
- [ ] SurfaceView.kt: заменить `.shadow(4.dp, shape)` → resolved elevation из токенов
- **Файлы**: `SegmentedControlView.kt`, `ButtonView.kt`, `IconButtonView.kt`, `SurfaceView.kt`
- **Status:** ✅ complete

### Phase 4: Выравнивание данных каталога (catalog-ui)
- [ ] Унифицировать регистр текста кнопок: React и Compose должны показывать одинаковые строки
- [ ] Проверить что оба каталога используют одинаковые данные / конфигурации
- **Файлы**: `apps/catalog-ui/react/`, `apps/catalog-ui/compose/`
- **Status:** ✅ complete

### Phase 5: Gradle task `verifyNoHardcodedValues`
- [ ] Создать Gradle task для поиска `\d+\.(dp|sp)` в Compose View файлах
- [ ] Создать Gradle task для поиска hardcoded px в React `.module.css` (вне `var()`)
- [ ] Подключить к `:apps:catalog-ui:compose:run` и `:apps:catalog-ui:react:dev`
- [ ] Добавить whitelist для допустимых случаев (0.dp, 0f)
- **Файлы**: `build.gradle.kts` (root или core/uikit/)
- **Status:** ✅ complete

### Phase 6: Контрактные тесты StyleResolver
- [ ] Тест: все поля `ControlSizeScale` покрыты `SegmentedControlSizes`
- [ ] Тест: все поля `ControlSizeScale` покрыты `ButtonSizeSet` (удостовериться что уже OK)
- [ ] Тест: `SegmentedControlStyleResolver.resolve()` возвращает корректные fontWeight/letterSpacing
- **Файлы**: `common/src/commonTest/`
- **Status:** ✅ complete

### Phase 7: Сборка, тестирование, валидация
- [ ] `./gradlew :core:uikit:common:build` — common компилируется
- [ ] `./gradlew :apps:catalog-ui:compose:run` — Compose каталог визуально ОК
- [ ] `./gradlew :apps:catalog-ui:react:dev` — React каталог визуально ОК
- [ ] `verifyNoHardcodedValues` task проходит без ошибок
- [ ] Контрактные тесты проходят
- **Status:** ✅ complete

## Key Questions
1. Нужен ли `SurfaceStyleResolver` для elevation → dp маппинг, или CSS shadow достаточно хранить как строку? → Нужен маппинг: CSS shadow string → Compose elevation Dp
2. Можно ли `fontWeight: 600` SegmentedControl или для него нужен отличающийся вес? → Используем `ControlSizeScale.fontWeight` = 600 (единый стандарт)
3. Нужно ли `paddingH` использовать как `padding-inline` опций SegmentedControl или для track? → Для опций (в track уже `trackPadding`)

## Decisions Made
| Decision | Rationale |
|----------|-----------|
| fontWeight 600 для SegmentedControl | Единый стандарт с ButtonView — оба берут из `ControlSizeScale` |
| paddingH = padding-inline опций | Track padding — это отдельный `trackPadding`, paddingH — горизонтальный отступ текста каждой опции |
| TRACK_PADDING → InteractiveControlTokens | Даёт возможность перекрыть для кастомных тем; единый источник правды |
| spinnerStrokeWidth в DesignTokens | Используется глобально (Button, IconButton), не привязан к одному компоненту |
| Gradle task вместо pre-commit hook | Интегрируется в CI, работает на всех машинах без настройки git hooks |

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
| — | — | — |

## Зависимости между фазами
```
Phase 1 (Common) ──┬──→ Phase 2 (React)  ──┐
                    └──→ Phase 3 (Compose) ──┤
Phase 4 (Catalog) ─────────────────────────→─┤
                                              ├──→ Phase 7 (Валидация)
Phase 5 (Gradle task) ───────────────────→───┤
Phase 6 (Тесты) ─────────────────────────→───┘
```

## Notes
- Phase 1 — блокирующий: без изменений в Common нельзя начинать Phase 2 и 3
- Phase 2 и 3 можно делать параллельно после Phase 1
- Phase 4, 5, 6 независимы и могут выполняться в любом порядке
- Все css fallback-значения (var(--x, 2px)) пока оставляем — они безопасны как дефолты
- `border: 1px solid` в SegmentedControl track — допустимо (= tokens.borderWidth default)
