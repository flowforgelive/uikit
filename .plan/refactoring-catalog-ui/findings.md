# Findings & Decisions

## Requirements
- Рефакторинг apps/catalog-ui (React + Compose) — вынести общие layout/showcase компоненты
- Все отступы/размеры из `DesignTokens` — никакого хардкода
- Единые компоненты для обёрток, секций, подзаголовков
- Визуально ничего не должно измениться
- Удалить мёртвый код после вынесения

## Текущее состояние (до рефакторинга)

### Размеры файлов
| Файл | Строк | Проблема |
|------|-------|----------|
| `CatalogApp.kt` | 1460 | Все 3 экрана + все showcases + все иконки + shared controls |
| `second/page.tsx` | 677 | Все component showcases + SVG иконки + Section дубль |
| `foundation/page.tsx` | 530 | Все foundation showcases + Section дубль |
| `page.tsx` (first) | 66 | Хардкод px-значений |

### Дублированный код

#### Section wrapper — ИДЕНТИЧЕН в 2 файлах
```tsx
// foundation/page.tsx:21-63 и second/page.tsx:109-138
function Section({ id, title, children, tokens }) {
  return (
    <section id={id} style={{ width: "100%" }}>
      <div style={{ display: "flex", alignItems: "center", gap: toRem(tokens.spacing.lg), marginBlockEnd: toRem(tokens.spacing.lg) }}>
        <h2 ...>{title}</h2>
        <div style={{ flex: 1, height: "1px", background: tokens.color.outlineVariant }} />
      </div>
      {children}
    </section>
  );
}
```

#### Layout-каркас страницы — ИДЕНТИЧЕН в 2 файлах
```tsx
// foundation/page.tsx:450-530 и second/page.tsx:580-677
<div style={{ minHeight: "100vh", backgroundColor: tokens.color.surface, ... }}>
  {/* Top bar */}
  <div style={{ display: "flex", padding: `md xl`, ... }}>
    <Button text="← Назад" variant="ghost" ... />
    {/* dir + theme controls */}
  </div>
  <main style={{ paddingBlockStart: xxl, paddingBlockEnd: xxxxl, paddingInline: xl }}>
    <div style={{ paddingBlock: lg, textAlign: "center" }}> {/* title */} </div>
    <div style={{ maxWidth: "960px", marginInline: "auto", gap: xxxl }}> {/* sections */} </div>
  </main>
</div>
```

#### SubSection title — дублируется в каждом showcase (~6 раз)
```tsx
<h3 style={{
  fontSize: toRem(tokens.typography.headline.fontSize),
  fontWeight: 600,
  color: tokens.color.textPrimary,
  marginBlockEnd: toRem(tokens.spacing.sm),
  textTransform: "capitalize"
}}>
```

### Хардкод значений

#### React page.tsx (first page)
| Хардкод | Должно быть | Токен |
|---------|-------------|-------|
| `gap: "24px"` | `gap: toRem(tokens.spacing.xl)` | spacing.xl = 24 |
| `top: "16px"` | `top: toRem(tokens.spacing.lg)` | spacing.lg = 16 |
| `insetInlineEnd: "16px"` | `insetInlineEnd: toRem(tokens.spacing.lg)` | spacing.lg = 16 |
| `gap: "8px"` | `gap: toRem(tokens.spacing.sm)` | spacing.sm = 8 |
| `width: "7.5rem"` | — фиксированная ширина для SegmentedControl | оставить или перевести в toRem |
| `width: "15rem"` | — фиксированная ширина для ThemeSwitcher | оставить или перевести в toRem |

#### Compose CatalogApp.kt (FirstScreen)
| Хардкод | Должно быть | Токен |
|---------|-------------|-------|
| `Spacer(Modifier.height(24.dp))` | `Spacer(Modifier.height(tokens.spacing.xl.dp))` | spacing.xl = 24 |
| `Spacer(Modifier.height(12.dp))` | `Spacer(Modifier.height(tokens.spacing.md.dp))` | spacing.md = 12 |
| `.padding(16.dp)` | `.padding(tokens.spacing.lg.dp)` | spacing.lg = 16 |
| `.padding(32.dp)` | `.padding(tokens.spacing.xxl.dp)` | spacing.xxl = 32 |
| `Arrangement.spacedBy(8.dp)` | `Arrangement.spacedBy(tokens.spacing.sm.dp)` | spacing.sm = 8 |

### SVG иконки в second/page.tsx (9 штук, ~50 строк)
- `searchIcon`, `starIcon`, `checkIcon`, `closeIcon`, `plusIcon`, `settingsIcon`, `arrowLeftIcon`, `arrowRightIcon`, `chevronRightIcon`
- Плюс общие `svgStyle` и `svgProps`
- Плюс массив `ICON_BUTTON_SAMPLES`

### Compose иконки в CatalogApp.kt
Используют Material Icons (Icons.Filled.*) — не нужно выносить в отдельный файл, но стоит сгруппировать.

## Technical Decisions

| Decision | Rationale |
|----------|-----------|
| CatalogPage принимает `title`, `subtitle`, `topBarControls` slot, `children` | Максимально гибкий — foundation и components страницы отличаются набором контролов в top bar |
| Section — отдельный компонент без tokens prop | Использует `useDesignTokens()` / `LocalDesignTokens.current` внутри себя, чтобы не протаскивать токены |
| Showcases получают tokens как prop | Они уже получают tokens, менять не нужно — consistency |
| Иконки React — именованные экспорты из `icons/index.tsx` | Проще импортировать: `import { searchIcon, starIcon } from "../components/icons"` |
| Compose showcases — отдельные файлы в `showcases/` пакете | Плоская структура, не разделяем на foundation/components — все showcase по имени понятны |
| Compose screens — в `screens/` пакете | Параллельно React app/ структуре |
| `width: "7.5rem"` / `"15rem"` оставляем | Это фиксированные ширины SegmentedControl, не токен |
| DIR_OPTIONS / SIZE_OPTIONS и подобные константы — в файлах страниц | Они специфичны для страниц, не нужно выносить |

## Layout Tokens Reference (единая спецификация для обеих платформ)

### Top Bar
- vertical padding: `tokens.spacing.md` (12dp)
- horizontal padding: `tokens.spacing.xl` (24dp)
- gap between controls: `tokens.spacing.sm` (8dp)

### Content Area
- padding-top: `tokens.spacing.xxl` (32dp)
- padding-bottom: `tokens.spacing.xxxxl` (64dp)
- padding-inline: `tokens.spacing.xl` (24dp)
- maxWidth: 960dp

### Title
- padding-block: `tokens.spacing.lg` (16dp)
- subtitle margin-top: `tokens.spacing.sm` (8dp)

### Sections
- gap between sections: `tokens.spacing.xxxl` (48dp)
- section header gap: `tokens.spacing.lg` (16dp)
- section header margin-bottom: `tokens.spacing.lg` (16dp)
- divider: 1px `tokens.color.outlineVariant`

### SubSection Title (h3)
- fontSize: `tokens.typography.headline.fontSize`
- fontWeight: 600
- color: `tokens.color.textPrimary`
- marginBlockEnd: `tokens.spacing.sm` (8dp)

### First Page
- gap between items: `tokens.spacing.xl` (24dp)
- controls position top/end: `tokens.spacing.lg` (16dp)
- controls gap: `tokens.spacing.sm` (8dp)

## Issues Encountered
| Issue | Resolution |
|-------|------------|
|       |            |
