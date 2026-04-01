# SSR Constraints for UIKit Kotlin Module

This document describes constraints that **must** be followed when modifying the Kotlin common module (`core/uikit/common/`) to ensure compatibility with React Server-Side Rendering (SSR) in Node.js.

## Why SSR Matters

The UIKit React layer runs in two environments:
1. **Node.js** (SSR) — Next.js renders components to HTML on the server
2. **Browser** (hydration + SPA) — React hydrates server HTML and handles interactivity

Kotlin JS output (`uikit-common`) is loaded in **both** environments. Code that works in the browser may break on the server.

## Rules

### 1. No Mutable State in `object` Singletons

Kotlin `object` declarations (singletons) are **cached in the Node.js process** between HTTP requests. Any mutable state will "leak" between different users' requests.

```kotlin
// ❌ WRONG — state shared between requests
@JsExport
object ButtonStyleResolver {
    private var cache = mutableMapOf<String, ResolvedButtonStyle>() // LEAKED!
    fun resolve(config: ButtonConfig, tokens: DesignTokens): ResolvedButtonStyle { ... }
}

// ✅ CORRECT — stateless pure function
@JsExport
object ButtonStyleResolver {
    fun resolve(config: ButtonConfig, tokens: DesignTokens): ResolvedButtonStyle { ... }
}
```

### 2. No Side Effects in `companion object`

`companion object` values (like `DesignTokens.DefaultLight`) are evaluated **once** when the module is first imported. They must be pure data without side effects.

```kotlin
// ❌ WRONG — side effect at import time
companion object {
    val Default = DesignTokens(...).also { println("Tokens loaded!") } // console pollution
}

// ❌ WRONG — platform-specific API
companion object {
    val Default = DesignTokens(
        color = ColorTokens(primary = getPlatformColor()) // no platform APIs!
    )
}

// ✅ CORRECT — pure data
companion object {
    val DefaultLight = DesignTokens(color = ColorTokens(primary = "#1A1A1A"), ...)
    val DefaultDark = DesignTokens(color = ColorTokens(primary = "#FFFFFF"), ...)
}
```

### 3. StyleResolvers Must Be Pure Functions

All `*StyleResolver.resolve()` methods must follow: `(config, tokens[, context]) → style`.

- **No** reading from global state or environment
- **No** accessing `window`, `document`, or `navigator`
- **No** file I/O, network calls, or random numbers
- **No** caching with mutable maps
- **Deterministic**: same inputs → same output, always

### 4. All `@JsExport data class` Must Be Serializable

Data classes used across the Kotlin-JS boundary should:
- Use only primitive types or other `@JsExport` classes
- Have `@Serializable` annotation (for future SDUI JSON support)
- Not depend on platform-specific types (Java `Color`, `Date`, etc.)

### 5. SurfaceContext Is a Shared Type

`SurfaceContext` (from `com.uikit.foundation`) is used as:
- A Kotlin data class in StyleResolvers
- A React Context value type (directly from `uikit-common`)

If you add fields to `SurfaceContext`, the React layer will automatically pick up the change because it imports the Kotlin-generated TypeScript type.

### 6. Adding New Components

When adding a new component with a StyleResolver:

1. Create the `*StyleResolver` as a stateless `object` with pure `resolve()` function
2. The React layer should have **two** versions:
   - `ComponentView.tsx` — Client Component with `"use client"`, uses Context for tokens
   - `ComponentView.server.tsx` — Server Component (optional), takes `tokens` as required prop

## Architecture Overview

```
Node.js SSR:
  layout.tsx (Server Component)
    → reads cookies → passes initialTheme/initialResolved/initialDir
    → UIKitThemeScript (inline script → sets data-theme before paint)
    → UIKitThemeProvider (Client Component boundary)
        → children rendered with correct tokens

Kotlin JS in Node.js:
  DesignTokens.Companion.DefaultLight/DefaultDark — cached singletons (OK: pure data)
  ButtonStyleResolver.getInstance().resolve(...) — stateless (OK: pure function)
  new SurfaceContext(0, "#F9F9F9") — immutable data class (OK)
```

## Testing SSR

```bash
# Build and verify
./gradlew :core:uikit:common:jsBrowserProductionLibraryDistribution
./gradlew :apps:catalog-ui:react:build   # production build with SSR

# Check for hydration mismatches in browser console
# Look for: "Text content does not match server-rendered HTML"
# Look for: "Hydration failed because..."
```
