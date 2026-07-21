# AGENT 2 — Feature Screens: Market + Coin Detail (CryptoX)
> **Recommended model tier: MEDIUM/STRONG** (MVI + chart drawing need care; list UI is routine)
> Runs in parallel with Agent 1 (design system) and Agent 3 (Portfolio/Settings + App Shell).
> You code AGAINST the shared contract below. Components in the contract exist (Agent 1 builds them) — import and use them; do NOT create your own versions.

---

## 1. Context
- Project: **CryptoX** — KMP crypto tracker, Compose Multiplatform only, **zero XML**, Clean Architecture, **MVI** pattern.
- Visual/UX reference: **TaminX** (screenshots attached): match its list style, header style, and screen transitions.
- Your scope: modules `feature/market` and `feature/detail` ONLY.
- Data: use FAKE repositories for now (realistic hardcoded data + simulated delay + Flow emissions). Real data layer is wired later — depend only on domain interfaces.

## 2. Shared Contract (frozen — identical in all 3 agent files)

### 2.1 Design system components available to you
```kotlin
CXScaffold, CXTopBar, CXCard, CXPriceText, CXChangeBadge, CXSparkline,
CXCoinListItem, CXLoadingShimmer, CXErrorState, CXEmptyState,
CXPrimaryButton, CXSearchField, CryptoXTheme
```
(Exact signatures are in `agent-1-design-system.md`. If you need one temporarily, create a THIN stub in a `stubs/` package marked `// TODO replace with designsystem` — never restyle it.)

### 2.2 MVI base (create in your module if absent, package `core.mvi` — identical for all agents)
```kotlin
interface UiState
interface UiIntent
interface UiEffect
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(initialState: S) : ViewModel() {
    val state: StateFlow<S>; val effects: Flow<E>
    abstract fun onIntent(intent: I)
    protected fun setState(reducer: S.() -> S); protected fun sendEffect(effect: E)
}
```

### 2.3 Domain interfaces you depend on (do not implement for real — fake them)
```kotlin
data class Coin(val id: String, val symbol: String, val name: String, val iconUrl: String, val price: Double, val changePercent24h: Double, val sparkline7d: List<Double>)
data class CoinDetail(val coin: Coin, val marketCap: Double, val volume24h: Double, val high24h: Double, val low24h: Double, val description: String)
data class PricePoint(val timestamp: Long, val price: Double)
enum class ChartRange { DAY, WEEK, MONTH, YEAR }

interface CoinRepository {
    fun observeMarket(): Flow<List<Coin>>
    suspend fun refreshMarket(): Result<Unit>
    suspend fun getCoinDetail(id: String): Result<CoinDetail>
    suspend fun getChart(id: String, range: ChartRange): Result<List<PricePoint>>
    fun searchCoins(query: String): Flow<List<Coin>>
}
```

## 3. Deliverables

### 3.1 `feature/market`
- **MarketContract**: `MarketState` (coins, isLoading, isRefreshing, query, error), `MarketIntent` (Load, Refresh, QueryChanged, CoinClicked), `MarketEffect` (NavigateToDetail(id), ShowError(msg)).
- **MarketViewModel**: observes repository Flow; debounced search (300ms) via `searchCoins`; pull-to-refresh.
- **MarketScreen**: search field on top (CXSearchField), coin list (CXCoinListItem), shimmer while loading, CXErrorState on failure, CXEmptyState for no search results.

### 3.2 `feature/detail`
- **DetailContract** (state includes selected ChartRange + chart points + detail + loading/error).
- **DetailViewModel**: loads detail + chart; range switching re-fetches chart.
- **DetailScreen**: header (icon, name, CXPriceText, CXChangeBadge), interactive line chart drawn with **Canvas in Compose** (touch scrub showing price at point is a bonus), range selector chips (1D/7D/1M/1Y), stats grid in CXCards, description section.

### 3.3 Fakes & Tests
- `FakeCoinRepository` with 20+ realistic coins, simulated network delay, occasional failure flag for testing error states.
- ViewModel tests: at least 3 for Market (load success, search filters, refresh error) and 2 for Detail (load, range switch).

## 4. Rules
1. MVI strictly: UI reads state, sends intents; effects only for navigation/one-off messages.
2. No hardcoded colors/spacing — tokens only.
3. Loading / error / empty states are mandatory on every screen.
4. Navigation is NOT your job: expose screens as `@Composable fun MarketRoute(onNavigateToDetail: (String) -> Unit)` etc. Agent 3 wires them.

## 5. Definition of Done
- Both screens run in preview/demo activity with fake data.
- All states (loading/success/error/empty) reachable and visually correct.
- Tests pass. Zero dependencies on Agent 3's modules.
