# Progress Log — UIKit Variant System v3

## Session: 2026-04-03

### Phase 0: Research & Analysis (COMPLETE)

- **Status:** complete
- **Started:** 2026-04-03

Выполнено в предыдущих сессиях:

- Actions taken:
  - Прочитаны все 29 ключевых файлов системы вариантов
  - Изучены бигтех-решения: Radix UI (6 вариантов, 12-step scale), Material 3 (Filled/Tonal/Outlined/Text), Apple Liquid Glass
  - Обнаружены 8 архитектурных проблем (P1-P8)
  - Создан документ анализа `docs/VARIANT_SYSTEM_ANALYSIS.md`
  - Создан план `.plan-v3/` с task_plan, findings, progress

- Files created:
  - `docs/VARIANT_SYSTEM_ANALYSIS.md` (analysis document)
  - `.plan-v3/task_plan.md` (this plan)
  - `.plan-v3/findings.md` (research findings)
  - `.plan-v3/progress.md` (this file)

- Key findings:
  - VisualVariant enum: 4 значения (Solid/Soft/Outline/Ghost), нет Surface
  - ColorTokens: 29 полей, нет intent-tinted soft (есть только dangerSoft)
  - SurfaceStyleResolver: Outline.bg == Solid.bg (баг), Soft.bg == Solid.bg (баг)
  - ButtonStyleResolver: Soft Primary = серый surfaceContainerHigh (не intent-tinted)
  - SegmentedControlConfig: нет variant prop вообще
  - IconButtonStyleResolver: делегирует Button — автоматически получит все фиксы

### Phase 1: Foundation — VisualVariant + ColorTokens
- **Status:** complete
- Actions taken:
  - Added `Surface` to `VisualVariant` enum (between Soft and Outline)
  - Added 7 new fields to `ColorTokens`: primarySoft, primarySoftHover, neutralSoft, neutralSoftHover, borderSubtle, primaryBorder, primaryBorderHover
  - Updated `DesignTokens.DefaultLight` and `DefaultDark` with all 7 new color values
  - Build: compileKotlinJs + compileKotlinJvm — PASS
- Files modified:
  - `foundation/VisualVariant.kt`
  - `tokens/ColorTokens.kt`
  - `tokens/DesignTokens.kt`

### Phase 2: ButtonStyleResolver — Surface + Soft fix
- **Status:** complete
- Actions taken:
  - Added `VisualVariant.Surface -> surfaceColors(intent, tokens)` to resolveColors() dispatcher
  - Implemented `surfaceColors()`: Primary uses primarySoft/primaryBorder, Neutral uses neutralSoft/borderSubtle, Danger uses dangerSoft/danger border
  - Rewrote `softColors()`: Primary now uses tokens.color.primarySoft (intent-tinted, not gray surfaceContainerHigh), Neutral uses tokens.color.neutralSoft with edge-case handling for matching surfaceBg
  - SurfaceAwareColorResolver — no changes needed (still used by import, may clean up later)
  - Build: compileKotlinJs + compileKotlinJvm — PASS
- Files modified:
  - `components/atoms/button/ButtonStyleResolver.kt`

### Phase 3: SurfaceStyleResolver — bugfixes + Surface
- **Status:** complete
- Actions taken:
  - FIX P2: Outline now returns `"transparent"` bg instead of `resolveBg()`
  - FIX P3: Soft now uses `resolveSoftBg()` (shifts level down by 1), so Soft ≠ Solid
  - Surface variant uses `resolveBg()` + `borderSubtle` for border
  - Build: compileKotlinJs + compileKotlinJvm — PASS
- Files modified:
  - `components/atoms/surface/SurfaceStyleResolver.kt`

### Phase 4: SegmentedControl — variant prop
- **Status:** complete
- Actions taken:
  - Added `variant: VisualVariant = VisualVariant.Surface` to SegmentedControlConfig
  - Updated equals()/hashCode() to include variant
  - Added `resolveColors(variant, tokens)` to SegmentedControlStyleResolver with all 5 variants
  - Surface: neutralSoft track + borderSubtle; Soft: surfaceHover track, no border; Outline: transparent + outlineVariant; Solid: surfaceContainerHigh, no border; Ghost: transparent + surfaceHover thumb
  - Updated resolveDefault() to use VisualVariant.Surface
  - Build: compileKotlinJs + compileKotlinJvm — PASS
- Files modified:
  - `components/atoms/segmentedcontrol/SegmentedControlConfig.kt`
  - `components/atoms/segmentedcontrol/SegmentedControlStyleResolver.kt`

### Phase 5: React renderers
- **Status:** complete
- Actions taken:
  - Added `surface: VisualVariant.Surface` to VARIANT_MAP in Button.tsx, IconButton.tsx, Surface.tsx
  - Added `variant` prop + VARIANT_MAP to SegmentedControl.tsx (default: "surface")
  - Added `--sc-border` CSS variable to SegmentedControlView.tsx
  - Added `border: 1px solid var(--sc-border, transparent)` to .track in CSS module
  - index.ts: no changes needed (VisualVariant already exported)
  - TypeScript type check: PASS (zero errors)
- Files modified:
  - `react/src/components/atoms/button/Button.tsx`
  - `react/src/components/atoms/icon-button/IconButton.tsx`
  - `react/src/components/atoms/surface/Surface.tsx`
  - `react/src/components/atoms/segmented-control/SegmentedControl.tsx`
  - `react/src/components/atoms/segmented-control/SegmentedControlView.tsx`
  - `react/src/components/atoms/segmented-control/SegmentedControlView.module.css`

### Phase 6: Compose renderers
- **Status:** complete
- Actions taken:
  - Added `variant: VisualVariant = VisualVariant.Surface` parameter to SegmentedControl.kt convenience composable
  - Added conditional border modifier to SegmentedControlView.kt track Box (only when border ≠ "transparent")
  - Build: compileKotlinJvm (compose) — PASS
- Files modified:
  - `compose/src/.../segmentedcontrol/SegmentedControl.kt`
  - `compose/src/.../segmentedcontrol/SegmentedControlView.kt`

### Phase 7: Showcase + Verification
- **Status:** complete
- Actions taken:
  - Updated BUTTON_VARIANTS: added "surface" (5 variants total)
  - Updated SURFACE_VARIANTS: added "surface" and "ghost" (5 variants total)
  - Added SegmentedControl variant showcase with all 5 variants displayed with labels
  - Full build: compileKotlinJs + compileKotlinJvm + compose:compileKotlinJvm — PASS
  - JS bundle generation: jsProductionLibraryDistribution — PASS
  - TypeScript type check — PASS
- Files modified:
  - `apps/catalog-ui/react/src/app/second/page.tsx`

## Test Results

| Test | Expected | Actual | Pass? |
|------|----------|--------|-------|
| `compileKotlinJs` | success | success | PASS |
| `compileKotlinJvm` | success | success | PASS |
| `compileKotlinJvm` (compose) | success | success | PASS |
| React type check | success | success | PASS |
| JS bundle generation | success | success | PASS |
| Visual check: Surface variant | fill+border | pending | |
| Visual check: Outline Surface | transparent bg | pending | |
| Visual check: Soft Primary ≠ Neutral | different tint | pending | |

## Build Commands

```bash
# Kotlin common (JS + JVM)
./gradlew :core:uikit:common:compileKotlinJs :core:uikit:common:compileKotlinJvm

# Compose JVM
./gradlew :core:uikit:compose:compileKotlinJvm

# Full check
./gradlew :core:uikit:common:compileKotlinJs :core:uikit:common:compileKotlinJvm :core:uikit:compose:compileKotlinJvm
```
