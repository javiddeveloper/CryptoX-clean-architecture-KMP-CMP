# AGENT 3 — Feature Screens: Portfolio + Settings + App Shell (CryptoX)
> **Recommended model tier: MEDIUM** (Portfolio P/L logic needs care; Settings + navigation shell are routine — a weaker model can do 3.3/3.4 if split further)
> Runs in parallel with Agent 1 (design system) and Agent 2 (Market/Detail).
> You code AGAINST the shared contract below. Design components exist (Agent 1 builds them) — import and use; do NOT restyle or duplicate them.

---

## 1. Context
- Project: **CryptoX** — KMP crypto tracker, Compose Multiplatform only, **zero XML**, Clean Architecture, **MVI**.
- Visual/UX reference: **TaminX** (screenshots attached): match its bottom navigation style, settings list style, and dialog/sheet patterns.
- Your scope: `feature/portfolio`, `feature/settings`, and the app shell (`composeApp`: navigation + bottom bar + theme wiring).
- Data: FAKE repositories (realistic data, Flow-based). Real data layer comes later.

## 2. Shared Contract (frozen — identical in all 3 agent files)

### 2.1 Design system components available to you
```kotlin
CXScaffold, CXTopBar, CXCard, CXPriceText, CXChangeBadge, CXSparkline,
CXCoinListItem, CXLoadingShimmer, CXErrorState, CXEmptyState,
CXPrimaryButton, CXSearchField, CryptoXTheme
```
(Signatures in `agent-1-design-system.md`. Temporary thin stubs allowed in `stubs/` marked `// TODO replace with designsystem`.)

### 2.2 MVI base — identical definition to Agent 2's file (package `core.mvi`):
```kotlin
interface UiState; interface UiIntent; interface UiEffect
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(initialState: S) : ViewModel() {
    val state: StateFlow<S>; val effects: Flow<E>
    abstract fun onIntent(intent: I)
    protected fun setState(reducer: S.() -> S); protected fun sendEffect(effect: E)
}
```

### 2.3 Domain interfaces you depend on (fake them)
```kotlin
data class Holding(val coinId: String, val symbol: String, val name: String, val iconUrl: String, val amount: Double, val avgBuyPrice: Double)
data class PortfolioSummary(val totalValue: Double, val totalCost: Double, val profitLoss: Double, val profitLossPercent: Double)
enum class AppCurrency { USD, EUR } ; enum class AppTheme { LIGHT, DARK, SYSTEM }
data class UserPreferences(val currency: AppCurrency, val theme: AppTheme)

interface PortfolioRepository {
    fun observeHoldings(): Flow<List<Holding>>
    fun observeSummary(): Flow<PortfolioSummary>   // combines holdings with live prices
    suspend fun addHolding(coinId: String, amount: Double, buyPrice: Double): Result<Unit>
    suspend fun updateHolding(coinId: String, amount: Double, buyPrice: Double): Result<Unit>
    suspend fun removeHolding(coinId: String): Result<Unit>
}
interface PreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun setCurrency(currency: AppCurrency)
    suspend fun setTheme(theme: AppTheme)
}
```

### 2.4 Routes exposed by Agent 2 (wire them, don't build them)
```kotlin
@Composable fun MarketRoute(onNavigateToDetail: (String) -> Unit)
@Composable fun DetailRoute(coinId: String, onBack: () -> Unit)
```
Until Agent 2 delivers, use placeholder screens behind the same signatures.

## 3. Deliverables

### 3.1 `feature/portfolio`
- **PortfolioContract**: state = holdings, summary, isLoading, error, editingHolding?; intents = Load, AddClicked, EditClicked(holding), DeleteClicked(coinId), ConfirmDelete, SaveHolding(...); effects = ShowUndoSnackbar, ShowError.
- **PortfolioViewModel**: observes holdings + summary Flows; delete with confirm; P/L values derived, never stored in UI.
- **PortfolioScreen**: summary header card (total value big, P/L with CXChangeBadge), holdings list (each row: coin info + amount + current value + per-holding P/L), swipe-to-delete with confirm, FAB → add/edit bottom sheet (coin picker from fake list, amount + buy price inputs with validation), CXEmptyState when no holdings ("Add your first coin").

### 3.2 `feature/settings`
- **SettingsContract** + ViewModel over PreferencesRepository.
- **SettingsScreen**: TaminX-style grouped list — Currency selector (USD/EUR), Theme selector (Light/Dark/System, applies instantly), About section (app version, link placeholders).

### 3.3 App Shell (`composeApp`)
- Compose Navigation graph: bottom bar with 3 tabs — **Market / Portfolio / Settings** (match TaminX bottom-nav style); Detail as a pushed route on top (bottom bar hidden).
- Theme wiring: `CryptoXTheme(darkTheme = from preferences)` at the root, reacting to Settings changes live.
- Koin module wiring for all fakes + ViewModels.

### 3.4 Fakes & Tests
- `FakePortfolioRepository` (in-memory MutableStateFlow, seeded with 3 holdings) and `FakePreferencesRepository`.
- ViewModel tests: Portfolio (add updates summary, delete removes, validation rejects bad input), Settings (theme change emits new state).

## 4. Rules
1. MVI strictly; effects only for one-off events.
2. Tokens only — no hardcoded colors/spacing.
3. Loading / error / empty states mandatory.
4. Do not modify Agent 2's feature modules or Agent 1's design system.

## 5. Definition of Done
- Full app shell runs: 3 tabs navigate, theme switch applies live, portfolio add/edit/delete works end-to-end on fake data.
- Placeholder Market/Detail swap cleanly for Agent 2's real routes (same signatures).
- Tests pass.
