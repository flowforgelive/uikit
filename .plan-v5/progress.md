# Progress — .plan-v5

## Session 1 — Исследование и создание плана
- Проверен JS bundle sync: оба пути идентичны ✅
- Проверена KMP JS конструкторы: корректны ✅
- Проанализирован resolveColors_0: все 5 вариантов обрабатываются ✅
- Выявлена корневая причина: Soft (#D6D6D6) темнее Solid (#EAEAEA) — инверсия ⚠️
- Выявлена UX проблема: Ghost Surface невидим в покое — нужен индикатор
- Создан .plan-v5/task_plan.md
- Создан .plan-v5/findings.md
- Создан .plan-v5/progress.md

### Начинаю Phase 1: Rebuild & Verify Runtime
- Обнаружено: `core/uikit/react/node_modules/` НЕ СУЩЕСТВОВАЛ — это корневая причина "Module not found" ⚠️
- `npm install` в `core/uikit/react/` создал symlink `uikit-common → ../../common/build/dist/js/productionLibrary`
- Dev-сервер перезапущен с `rm -rf .next` → `✓ Compiled /second in 1664ms (658 modules)` ✅
- Phase 1: COMPLETE ✅

### Phase 2: KMP Object Spread
- Проверено: все KMP JS конструкторы используют `this.xxx = value` (own enumerable properties)
- `VOID === undefined` — дефолты работают
- `{...baseTokens}` корректно копирует все data properties
- Проблемы нет — Phase 2: COMPLETE ✅

### Phase 3: Ghost Surface Showcase UX
- Добавлен dashed-border wrapper для Ghost Surface карточек
- Добавлен текст "(hover)" к подписи Ghost карточек
- Phase 3: COMPLETE ✅

### Phase 4: SC Variant Colors
- Исправлено: Soft trackBg `surfaceHover (#D6D6D6)` → `primarySoft (#F0F0F0)`
- Теперь иерархия: Ghost(transparent) < Outline(transparent+border) < Surface(#F5F5F5+border) < Soft(#F0F0F0) < Solid(#EAEAEA)
- JS bundle пересобран
- Phase 4: COMPLETE ✅

### Phase 5: Verify Icons End-to-End
- HTML-ответ содержит: 12 `optionIcon`, 24 `optionContent`, 36 `sc-icon` — иконки рендерятся ✅
- Phase 5: COMPLETE ✅

### Phase 6: Final Build
- Kotlin JS+JVM: BUILD SUCCESSFUL ✅
- TypeScript: no errors ✅
- Phase 6: COMPLETE ✅

## Итоговая корневая причина
**`core/uikit/react/` не имел `node_modules/` директории**. При `transpilePackages: ["@uikit/react"]` Next.js компилирует файлы из `core/uikit/react/src/`, и webpack ищет `uikit-common` начиная от ФИЗИЧЕСКОГО расположения файла. Без `node_modules/uikit-common` symlink там — модуль не находился.
