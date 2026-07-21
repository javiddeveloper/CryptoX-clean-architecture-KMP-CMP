# Modules

Every module, what it owns, and what it depends on. Paths are relative to the
repository root.

## `androidApp`

The Android application and, for now, the demo host that wires the two feature
screens together end to end.

- `CryptoXApp.kt` — `Application` that starts Koin with `dataModule`,
  `marketModule`, `detailModule`.
- `CryptoXDemoApp.kt` — a `NavHost` (Market → Detail) wrapped in `CryptoXTheme`.
- `MainActivity.kt` — edge-to-edge activity hosting `CryptoXDemoApp()`.

> The production app shell (bottom bar, full nav graph, Portfolio/Settings) is
> the responsibility of the plan-3 app-shell module and will replace the demo host.

**Depends on:** `shared`, `core:designsystem`, `core:data`, `feature:market`, `feature:detail`.

---

## `shared`

Kotlin Multiplatform shared code and the iOS entry point (`MainViewController`).
Holds cross-platform glue and platform `expect/actual` (e.g. `Platform`). App-wide
composables and shared utilities live here.

**Depends on:** Compose runtime/foundation/material3 (no feature or data modules).

---

## `core:designsystem`

The visual foundation — **pure UI, no logic**. See [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md).

- **Theme:** `CryptoXTheme`, `CryptoXColors` (dark-first + light), `CryptoXTypography`,
  `CryptoXShapes`, `CryptoXSpacing`.
- **Components (`CryptoX*`):** `CryptoXScaffold`, `CryptoXTopBar`, `CryptoXCard`,
  `CryptoXPriceText`, `CryptoXChangeBadge`, `CryptoXSparkline`, `CryptoXCoinListItem`,
  `CryptoXLoadingShimmer`, `CryptoXErrorState`, `CryptoXEmptyState`,
  `CryptoXPrimaryButton`, `CryptoXSearchField`.
- **Previews:** `ThemePreview.kt`, `ComponentsPreview.kt` (light + dark).

**Depends on:** Compose only. Standalone — no other project modules.

---

## `core:mvi`

The MVI foundation shared by every feature. One file:

- `BaseViewModel<STATE, PARTIAL_STATE, EVENT, INTENT>` — channel-based intent
  processing, `scan`-based reduction, `uiState: StateFlow`, `events: Flow`.

See [MVI.md](MVI.md).

**Depends on:** `androidx.lifecycle` ViewModel (multiplatform), coroutines.

---

## `core:domain`

The contract layer — **models and interfaces only, no implementations**.

- `model/Coin.kt` — `Coin`, `CoinDetail`, `PricePoint`, `ChartRange`.
- `repository/CoinRepository.kt` — the single data abstraction features depend on:

  ```kotlin
  interface CoinRepository {
      fun observeMarket(): Flow<List<Coin>>
      suspend fun refreshMarket(): Result<Unit>
      suspend fun getCoinDetail(id: String): Result<CoinDetail>
      suspend fun getChart(id: String, range: ChartRange): Result<List<PricePoint>>
      fun searchCoins(query: String): Flow<List<Coin>>
  }
  ```

**Depends on:** coroutines only.

---

## `core:data`

The data layer. Today it is a fake; tomorrow it hides a real network + database
stack behind the same interface.

- `fake/FakeCoinRepository.kt` — 23 realistic coins, simulated network delay,
  generated sparklines & charts, and a `failNextRefresh` flag for exercising
  error states.
- `di/DataModule.kt` — `dataModule` binds `CoinRepository` → `FakeCoinRepository`.

**Depends on:** `core:domain`, Koin, coroutines.

---

## `feature:market`

The market list screen. Package `com.cryptox.feature.market`.

- `ui/contract/MarketContract.kt` — `MarketUiState` (+ `PartialState`),
  `MarketIntent`, `MarketEffect`.
- `ui/MarketViewModel.kt` — observes the repository, **debounced search (300 ms)**,
  pull-to-refresh.
- `ui/MarketScreen.kt` — `MarketRoute` (Koin + effects) and `MarketScreen`
  (search field, coin list, shimmer / error / empty states, pull-to-refresh).
- `di/MarketModule.kt`, `Navigation.kt` (`MarketDestination`, `marketScreen`).
- `commonTest` — load-success, search-filter, refresh-error tests.

**Depends on:** `core:designsystem`, `core:domain`, `core:mvi` (+ `core:data` in tests).

---

## `feature:detail`

The coin detail screen. Package `com.cryptox.feature.detail`.

- `ui/contract/DetailContract.kt` — state (detail + chart + selected range +
  loading/error), intents, effects.
- `ui/DetailViewModel.kt` — loads detail then chart; range switching re-fetches.
- `ui/DetailScreen.kt` — header, range chips, stats grid in `CryptoXCard`s,
  description.
- `ui/PriceChart.kt` — an **interactive line chart drawn with Compose `Canvas`**,
  with touch-scrub price readout.
- `ui/Formatters.kt` — compact (`1.23B`) and price formatting.
- `di/DetailModule.kt`, `Navigation.kt` (`DetailDestination(coinId)`, `detailScreen`).
- `commonTest` — load and range-switch tests.

**Depends on:** `core:designsystem`, `core:domain`, `core:mvi` (+ `core:data` in tests).

---

## `iosApp`

The Xcode project / SwiftUI entry point for the iOS build. Consumes the shared
Compose UI through the KMP frameworks.
