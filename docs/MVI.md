# MVI Pattern

Every screen in CryptoX is driven by one `BaseViewModel` (in `core:mvi`). It
gives each feature a single, predictable state stream and a clean separation
between *durable state* and *one-off events*.

## The four type parameters

```kotlin
BaseViewModel<STATE, PARTIAL_STATE, EVENT, INTENT>
```

| Param | Role | Example (`feature:market`) |
|-------|------|----------------------------|
| `INTENT` | Everything the UI can request | `MarketIntent.Load`, `QueryChanged`, `Refresh`, `CoinClicked` |
| `PARTIAL_STATE` | Atomic changes produced while handling an intent | `Loading(true)`, `Coins(list)`, `Error(msg)` |
| `STATE` | The immutable snapshot the UI renders | `MarketUiState(coins, isLoading, query, error, …)` |
| `EVENT` | One-off side effects (never replayed) | `NavigateToDetail(id)`, `ShowError(msg)` |

## The cycle

```
Intent ──▶ handleIntent(): Flow<PartialState> ──▶ reduceState(state, partial): State ──▶ uiState
                        │
                        └──▶ sendEvent(Effect) ──▶ events   (navigation, snackbars)
```

1. The UI calls `sendIntent(intent)`.
2. `handleIntent` turns the intent into a **`Flow<PARTIAL_STATE>`** — it may emit
   many partials (e.g. `Loading(true)` → `Coins(...)` → `Loading(false)`) and can
   stay open to observe a repository `Flow`.
3. Each partial is folded into the current state by `reduceState`.
4. The new state is published on `uiState: StateFlow<STATE>`.
5. Anything that must happen exactly once — navigation, a transient error — is
   pushed via `sendEvent` onto `events: Flow<EVENT>`.

Errors thrown inside a `handleIntent` flow are caught centrally and converted
through `createErrorState(message)`, so a screen can't crash the stream.

## Why partial states?

Because a single intent often changes several independent fields over time.
Emitting small partials and reducing them keeps each mutation explicit and keeps
`reduceState` a pure, exhaustive `when` — easy to read and to test.

## Worked example — `feature:market`

**Contract:**

```kotlin
data class MarketUiState(
    val coins: ImmutableList<Coin> = persistentListOf(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val query: String = "",
    val error: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Refreshing(val isRefreshing: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Coins(val coins: List<Coin>) : PartialState
        data class Query(val query: String) : PartialState
    }
}

sealed interface MarketIntent {
    data object Load : MarketIntent
    data object Refresh : MarketIntent
    data class QueryChanged(val query: String) : MarketIntent
    data class CoinClicked(val id: String) : MarketIntent
}

sealed interface MarketEffect {
    data class NavigateToDetail(val id: String) : MarketEffect
    data class ShowError(val message: String) : MarketEffect
}
```

**ViewModel (the important parts):**

```kotlin
override fun handleIntent(intent: MarketIntent): Flow<PartialState> = when (intent) {
    is MarketIntent.Load        -> handleLoad()          // long-lived: observes repo + debounced search
    is MarketIntent.Refresh     -> handleRefresh()
    is MarketIntent.QueryChanged -> handleQueryChanged(intent.query)
    is MarketIntent.CoinClicked -> {
        sendEvent(MarketEffect.NavigateToDetail(intent.id))  // effect, not state
        emptyFlow()
    }
}

override fun reduceState(state: MarketUiState, partial: PartialState) = when (partial) {
    is PartialState.Coins   -> state.copy(coins = partial.coins.toImmutableList(), isLoading = false, error = null)
    is PartialState.Loading -> state.copy(isLoading = partial.isLoading)
    // … one branch per partial …
}
```

`Load` starts a flow that keeps a `MutableStateFlow<String>` query pipeline open,
`debounce(300ms)` + `flatMapLatest { searchCoins(it) }`, so both the initial list
and live search flow through the same reactive stream.

**Screen** collects state with lifecycle awareness and routes effects:

```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()

LaunchedEffect(Unit) { viewModel.sendIntent(MarketIntent.Load) }
LaunchedEffect(viewModel) {
    viewModel.events.collect { effect ->
        when (effect) {
            is MarketEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
            is MarketEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
        }
    }
}
```

## Testing MVI

ViewModels are tested against a `FakeCoinRepository` with virtual time:

```kotlin
@BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)   // StandardTestDispatcher

@Test
fun queryChanged_filtersCoins() = runTest(dispatcher) {
    val vm = MarketViewModel(FakeCoinRepository(networkDelayMs = 0))
    vm.sendIntent(MarketIntent.Load);           advanceUntilIdle()
    vm.sendIntent(MarketIntent.QueryChanged("bit")); advanceUntilIdle()

    assertTrue(vm.uiState.value.coins.all { it.name.contains("bit", true) || it.symbol.contains("bit", true) })
}
```

Effects are asserted with Turbine: `viewModel.events.test { assertIs<...>(awaitItem()) }`.

## Checklist for a new screen

1. Define `XUiState` (+ `PartialState`), `XIntent`, `XEffect` in `ui/contract`.
2. Extend `BaseViewModel<XUiState, PartialState, XEffect, XIntent>`; implement
   `handleIntent`, `reduceState`, `createErrorState`.
3. Build `XRoute` (Koin VM + effect handling) and a stateless `XScreen(state, onIntent)`.
4. Add `xModule` (Koin) and a `Navigation.kt` destination.
5. Write ViewModel tests with `FakeCoinRepository`.
