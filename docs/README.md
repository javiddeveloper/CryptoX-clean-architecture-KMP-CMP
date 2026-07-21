# CryptoX — Documentation

CryptoX is a **Kotlin Multiplatform** (Android-first) crypto portfolio & market
tracker built entirely with **Compose Multiplatform** (zero XML), following
**Clean Architecture** and the **MVI** pattern.

This folder is the entry point for understanding the codebase. Start with the
architecture overview, then drill into the area you care about.

## Index

| Document | What's inside |
|----------|---------------|
| [ARCHITECTURE.md](ARCHITECTURE.md) | The big picture: layers, module graph, data flow, tech stack. |
| [MODULES.md](MODULES.md) | Every Gradle module explained — responsibility, key files, dependencies. |
| [MVI.md](MVI.md) | How the `BaseViewModel` MVI contract works, with a worked example. |
| [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) | Tokens, theme, and the `CryptoX*` component library. |
| [GETTING_STARTED.md](GETTING_STARTED.md) | Build, run, test, and how to add a new feature module. |

## At a glance

- **UI:** Compose Multiplatform + Material 3, a `CryptoX*` design-system layer on top.
- **State:** unidirectional MVI (`Intent → PartialState → State`, `Effect` for one-off events).
- **DI:** Koin.
- **Navigation:** type-safe `@Serializable` destinations (Navigation-Compose).
- **Data:** currently a `FakeCoinRepository`; the real network/database layer plugs in behind `CoinRepository`.

## Reference

The project mirrors the module layout and MVI conventions of the internal
**TaminHamrahCMP** project. Per-area implementation briefs live in
[`/plans`](../plans). Where a plan and the shipped code differ, the code wins —
see [ARCHITECTURE.md](ARCHITECTURE.md#deviations-from-the-plans).
