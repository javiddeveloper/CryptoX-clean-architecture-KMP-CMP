# Design System (`core:designsystem`)

The visual foundation for CryptoX: **tokens**, a **theme**, and a library of
`CryptoX*` **components**. It contains no feature logic, no ViewModels, and no
networking — it is safe to depend on from anywhere in the UI layer.

> The module's own quick-reference lives at
> [`core/designsystem/DESIGNSYSTEM.md`](../core/designsystem/DESIGNSYSTEM.md).
> This page adds the "why" and how to consume it.

## Tokens

Design decisions are expressed as tokens, never as hardcoded values in components.

| Token group | Where | Notes |
|-------------|-------|-------|
| **Colors** | `CryptoXColors` via `LocalCryptoXColors.current` | Dark-first, with light variant. Crypto semantics: `profitGreen`, `lossRed`, `neutral`, `accent`, `surface`, `surfaceElevated`, plus `*Bg` tints and gradients. |
| **Typography** | `CryptoXTypography` / `MaterialTheme.typography` | Display / title / body / label scale. Prices use a **mono / tabular** style so digits align. |
| **Spacing** | `CryptoXSpacing` | 4dp-based: `xs=4, sm=8, md=16, lg=24, xl=32`, plus page-level (`pageHorizontal`, `pageVertical`). |
| **Shapes** | `CryptoXShapes` + `CornerRadius` | `small/medium/large` corner radii matching card style. |
| **Sizes** | `IconSize`, `Elevation` | Named icon sizes and elevation steps. |

## Theme

```kotlin
CryptoXTheme(darkTheme = true) {
    // content — tokens are provided via CompositionLocals + Material3 mapping
}
```

`CryptoXTheme` provides `LocalCryptoXColors`, maps tokens onto a Material 3
`ColorScheme`, and installs `CryptoXTypography` and `CryptoXShapes`. Read extended
colors with `LocalCryptoXColors.current`; use `MaterialTheme.colorScheme` for
plain Material needs.

## Components

All are prefixed `CryptoX`. Signatures are the shared contract features build
against:

| Component | Purpose |
|-----------|---------|
| `CryptoXScaffold`, `CryptoXTopBar` | Screen skeleton and app bar (with optional back + actions). |
| `CryptoXCard` | Elevated surface for grouped content; optional `onClick`. |
| `CryptoXPriceText` | A price rendered with the mono/tabular style + currency symbol. |
| `CryptoXChangeBadge` | Green/red percentage badge with directional arrow. |
| `CryptoXSparkline` | Compact trend line for list rows. |
| `CryptoXCoinListItem` | Combined row: icon, name/symbol, sparkline, price, change. |
| `CryptoXLoadingShimmer` | Placeholder shimmer for loading states. |
| `CryptoXErrorState`, `CryptoXEmptyState` | Full-screen error (with retry) and empty states. |
| `CryptoXPrimaryButton`, `CryptoXSearchField` | Primary CTA and search input. |

## Rules

1. **No hardcoded colors/spacing/shapes in features.** Always pull from tokens.
2. **Numbers use the mono typography** (`CryptoXPriceText` already does this).
3. Keep this module free of feature logic, ViewModels, and I/O.
4. Every public component has KDoc and at least one preview.

## Previews

`ThemePreview.kt` and `ComponentsPreview.kt` render the tokens and every
component in **light and dark** for at-a-glance review.
