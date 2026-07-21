# Getting Started

## Prerequisites

- JDK 11+
- Android SDK (compileSdk 35, minSdk 24)
- For iOS: macOS + Xcode (open `iosApp` in Xcode)

## Build & run

```bash
# Android debug APK
./gradlew :androidApp:assembleDebug

# Install on a connected device/emulator
./gradlew :androidApp:installDebug
```

Launching the Android app opens the **demo host** (`CryptoXDemoApp`): the Market
list with fake data → tap a coin → Coin Detail with an interactive chart → back.

iOS: open [`/iosApp`](../iosApp) in Xcode and run.

## Tests

```bash
# Feature ViewModel tests (JVM/Android unit tests)
./gradlew :feature:market:testDebugUnitTest
./gradlew :feature:detail:testDebugUnitTest

# Everything
./gradlew test
```

Tests run each ViewModel against `FakeCoinRepository` on a virtual-time
dispatcher (`runTest`), asserting state with plain assertions and effects with
Turbine.

## Project layout

```
CryptoX/
├── androidApp/            Android app + demo NavHost host
├── iosApp/                Xcode / SwiftUI entry point
├── shared/                KMP shared code + iOS entry
├── core/
│   ├── designsystem/      CryptoX* components, theme, tokens
│   ├── mvi/               BaseViewModel
│   ├── domain/            models + CoinRepository interface
│   └── data/              FakeCoinRepository + Koin dataModule
├── feature/
│   ├── market/            market list (MVI)
│   └── detail/            coin detail + Canvas chart (MVI)
├── plans/                 per-area implementation briefs
└── docs/                  ← you are here
```

## Adding a new feature module

1. **Create the module** `feature/<name>` with a `build.gradle.kts` modeled on
   `feature/market/build.gradle.kts` (Compose + serialization plugins; depend on
   `core:designsystem`, `core:domain`, `core:mvi`).
2. **Register it** in `settings.gradle.kts`: `include(":feature:<name>")`.
3. **Contract** — `ui/contract/<Name>Contract.kt`: `UiState` (+ `PartialState`),
   `Intent`, `Effect`.
4. **ViewModel** — extend `BaseViewModel<…>`; implement `handleIntent`,
   `reduceState`, `createErrorState`. Depend only on `CoinRepository` (domain).
5. **Screen** — a stateless `<Name>Screen(state, onIntent)` plus a `<Name>Route`
   that resolves the ViewModel via `koinViewModel()` and handles effects.
6. **DI** — `di/<Name>Module.kt` with `viewModelOf(::<Name>ViewModel)`.
7. **Navigation** — a `@Serializable` destination and a
   `NavGraphBuilder.<name>Screen(...)` extension.
8. **Wire it** in the host (`CryptoXApp` modules + `CryptoXDemoApp` NavHost).
9. **Test** the ViewModel with `FakeCoinRepository`.

See [MVI.md](MVI.md#checklist-for-a-new-screen) for the condensed checklist.

## Wiring real data later

Everything above depends on the `CoinRepository` *interface*. To go live, add a
real implementation in `core:data` (network + database) and change the binding in
`di/DataModule.kt`:

```kotlin
val dataModule = module {
    single<CoinRepository> { RealCoinRepository(get(), get()) }  // was FakeCoinRepository()
}
```

No feature or UI code changes.

## Conventions

- **Zero XML** — Compose only.
- **Tokens only** — no hardcoded colors/spacing (see [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md)).
- **MVI everywhere** — UI reads `state`, sends `intent`s; navigation/messages are `effect`s.
- Every screen must handle **loading / error / empty** states.
- Each source file carries the project copyright header.
