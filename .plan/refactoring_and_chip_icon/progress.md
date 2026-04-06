# Progress: Вложенные цвета + Icon → Button → Chip

## Session 1 — 2026-04-06

### Планирование
- [x] Анализ NESTED_INTERACTIVE_COLORS_GUIDE.md — выбрана Стратегия E (Hybrid: Tonal Staircase + Step Down Rule)
- [x] Анализ InteractiveColorResolver — поддерживает только 1 уровень адаптации для Soft
- [x] Анализ IconButton vs Button — ~500 LOC дублирования, merge через `isIconOnly`
- [x] Анализ Icon primitive — slot-based сейчас, name-based для BDUI позже
- [x] Анализ Chip — Surface(Soft) + Icon + Text + CloseButton(Ghost), отдельные chipProportions
- [x] Прочтение ROADMAP.md — Icon = Primitive #1, Chip = Composite #9
- [x] Создан task_plan.md с 7 фазами
- [x] Создан findings.md с детальным анализом
- [x] Создан progress.md

### Файлы создані
- `.plan/refactoring_and_chip_icon/task_plan.md`
- `.plan/refactoring_and_chip_icon/findings.md`
- `.plan/refactoring_and_chip_icon/progress.md`

### Решения
1. **Tonal Staircase** вместо StateLayerCalculator — предрассчитанные токены достаточны для 3 уровней
2. **CSS `:has()` anti-stacking** — 96%+ поддержка в 2026, чистый подход
3. **Icon как slot** — ReactNode/Composable, с опциональным `name` для BDUI
4. **Button `text = ""`** → icon-only mode — устраняет потребность в IconButton
5. **Chip с chipProportions** — компактнее Button, отдельный heightRatio/paddingHRatio
6. **Deprecation IconButton** перед удалением — плавный migration path

### Следующие шаги
- [ ] Phase 1: nestingDepth в SurfaceContext + NestingColorStrategy
- [ ] Phase 3: Icon Primitive (может параллельно с Phase 1)

### Phase 1: nestingDepth + NestingColorStrategy — COMPLETE
- [x] `SurfaceContext.kt` — добавлен `nestingDepth: Int = 0`
- [x] `ColorPair.kt` — новый data class `@JsExport @Serializable`
- [x] `NestingColorStrategy.kt` — `@JsExport object` с тональной лестницей для Primary/Neutral/Danger
- [x] `InteractiveColorResolver.kt` — softColors() переписан: использует NestingColorStrategy вместо if-проверок
- [x] `SurfaceContext.tsx` (React) — default с nestingDepth=0
- [x] `SurfaceView.tsx` (React) — пробрасывает parentSurface.nestingDepth
- [x] `SurfaceView.kt` (Compose) — пробрасывает parentSurfaceContext.nestingDepth
- [x] Компиляция JVM ✅, JS ✅, Compose ✅, тесты ✅

### Phase 3: Icon Primitive — COMPLETE
- [x] `IconConfig.kt` — data class: name, size, customSize, color, ariaHidden, id, testTag, visibility
- [x] `IconStyleResolver.kt` — object: resolve(config, tokens) → ResolvedIconStyle(size, color)
- [x] `ColorPair.kt` — вспомогательный data class для NestingColorStrategy
- [x] React `Icon.tsx` — convenience: size string → ComponentSize enum
- [x] React `IconView.tsx` — config-based: span обёртка с CSS vars
- [x] React `IconView.module.css` — flexbox container, svg auto-sizing
- [x] Compose `Icon.kt` — convenience Composable
- [x] Compose `IconView.kt` — config-based: Box + LocalContentColor
- [x] `index.ts` — экспорты Icon, IconView, IconConfig, IconStyleResolver
- [x] TS declarations: IconConfig, ResolvedIconStyle, IconStyleResolver — все присутствуют
- [x] Компиляция JVM ✅, JS ✅, Compose ✅, тесты ✅

### Phase 5: IconButton → Button icon-only — COMPLETE
- [x] `ButtonConfig.kt` — `text: String = ""` (optional), `ariaLabel: String? = null`, `isIconOnly` computed
- [x] `ButtonStyleResolver.kt` — `isIconOnly` в SizeSet, `effectivePaddingH = (height - iconSize) / 2`
- [x] React `Button.tsx` — `text?: string` optional, `ariaLabel` prop
- [x] React `ButtonView.tsx` — icon-only content rendering, `aria-label`, `.iconOnly` CSS class
- [x] React `ButtonView.module.css` — `.iconOnly { width, min-height, padding: 0 }`
- [x] Compose `Button.kt` — `text: String = ""`, `ariaLabel` param
- [x] Compose `ButtonView.kt` — `config.isIconOnly` → `.size(height.dp)` вместо `.defaultMinSize`, icon-only в `ButtonContent`
- [x] React `IconButton.tsx` — deprecation wrapper → `<Button iconEnd={icon} />`
- [x] Compose `IconButton.kt` — `@Deprecated` annotation → делегирует к `Button`
- [x] Компиляция JVM ✅, JS ✅, Compose ✅, тесты ✅

### Phase 2: Anti-stacking hover — COMPLETE
- [x] React `ButtonView.module.css` — hover/active selectors: `:not(:has([data-interactive]:hover))`
- [x] React `ButtonView.tsx` — `data-interactive` атрибут на `<button>` когда `isInteractive`
- [x] Compose `LocalChildHoverState.kt` — новый CompositionLocal для child→parent hover signaling
- [x] Compose `ButtonView.kt` — `LaunchedEffect(isHovered)` уведомляет parent, `childHoverState` подавляет hover
- [x] Компиляция Compose ✅, тесты ✅

### Phase 6: Chip Composite — COMPLETE
- [x] `ChipConfig.kt` — data class: text, variant, intent, size, hasLeadingIcon, dismissible, selected, disabled, loading
- [x] `ChipStyleResolver.kt` — object: chip proportions (heightRatio=2.0, radiusFraction=0.5), selected→Solid shift
- [x] React `Chip.tsx` — convenience: string-based API
- [x] React `ChipView.tsx` — config-based: data-interactive, anti-stacking hover, close button с SVG ×
- [x] React `ChipView.module.css` — стили с `:has()` anti-stacking, pill shape, close button
- [x] Compose `Chip.kt` — convenience Composable
- [x] Compose `ChipView.kt` — config-based: anti-stacking hover, Row layout, dismiss button
- [x] `index.ts` — экспорты Chip, ChipView, ChipConfig, ChipStyleResolver
- [x] TS declarations: ChipConfig, ChipSizes, ResolvedChipStyle, ChipStyleResolver — все присутствуют
- [x] Компиляция JVM ✅, JS ✅, Compose ✅, тесты ✅

### Phase 7: Удаление IconButton + Catalog — COMPLETE
- [x] Удалены каталоги: `common/iconbutton/`, `react/icon-button/`, `compose/iconbutton/`
- [x] `index.ts` — убраны: IconButton, IconButtonView, IconButtonConfig, IconButtonStyleResolver
- [x] React `IconButtonShowcase.tsx` — заменён на `Button` с `iconEnd` + `ariaLabel`
- [x] React `HeightAlignmentShowcase.tsx` — `IconButton` → `Button` icon-only
- [x] Compose `IconButtonShowcase.kt` — `IconButton` → `Button` с `iconEnd` + `iconPosition`
- [x] Compose `HeightAlignmentShowcase.kt` — `IconButton` → `Button` icon-only
- [x] TS declarations: IconButton полностью удалён (0 упоминаний)
- [x] Компиляция JVM ✅, JS ✅, Compose ✅, Catalog ✅, тесты ✅

## ALL PHASES COMPLETE ✅
