# CryptoX Design System

This module (`core/designsystem`) provides the foundational UI components, theme, and design tokens for the CryptoX application. It is completely independent of feature logic or ViewModels.

## Tokens

The tokens are accessible via CompositionLocals.

- **Colors:** `LocalCryptoXColors.current` provides semantics tailored for the crypto domain (e.g., `profitGreen`, `lossRed`, `accent`, `surfaceElevated`).
- **Typography:** `MaterialTheme.typography` provides the base type scale. For numbers and prices, use `cryptoXMonoTypography()`.
- **Shapes/Spacing:** Predefined in `CryptoXShapes`, `CornerRadius`, `CryptoXSpacing`, and `IconSize`.

## Core Components

All components are prefixed with `CryptoX`. Examples include:

- `CryptoXScaffold` & `CryptoXTopBar` for screen layouts.
- `CryptoXCard` for elevating content.
- `CryptoXPriceText` & `CryptoXChangeBadge` & `CryptoXSparkline` for coin data.
- `CryptoXCoinListItem` as a combined item for lists.
- `CryptoXLoadingShimmer`, `CryptoXErrorState`, `CryptoXEmptyState` for state management.
- `CryptoXPrimaryButton` & `CryptoXSearchField` for user interaction.

## Rules
1. Never use hardcoded colors. Use `LocalCryptoXColors.current` or `MaterialTheme.colorScheme`.
2. Prices and balances must use `cryptoXMonoTypography()`.
3. Do not add networking, database, or feature logic into this module.

## Previews
Check out `ThemePreview.kt` and `ComponentsPreview.kt` for visual reference of light and dark modes.
