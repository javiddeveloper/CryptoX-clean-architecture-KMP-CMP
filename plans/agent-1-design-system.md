# AGENT 1 — Design System Foundation (CryptoX)
> **Recommended model tier: STRONG** (architecture + design decisions live here)
> Runs in parallel with Agent 2 (Market/Detail screens) and Agent 3 (Portfolio/Settings + App Shell).
> All three agents follow the SHARED CONTRACT below. Do not deviate from it — the other agents are coding against it right now.

---

## 1. Context
- Project: **CryptoX** — a Kotlin Multiplatform (Android-first) crypto portfolio tracker, Compose Multiplatform only, **zero XML**.
- Visual/UX reference: **TaminX** (screenshots attached to this session). CryptoX must follow the same design language: navigation style, spacing rhythm, card style, list patterns, header patterns.
- Your scope: module `core/designsystem` ONLY. You do not build feature screens.

## 2. Deliverables
Create the `core/designsystem` module containing:

### 2.1 Design Tokens
- `CryptoXColors` — light + dark palettes (dark is primary). Extract palette direction from TaminX screenshots; add crypto semantics:
  - `profitGreen`, `lossRed`, `neutral`, `surface`, `surfaceElevated`, `accent`.
- `CryptoXTypography` — type scale matching TaminX hierarchy (display / title / body / label / mono for numbers).
- `CryptoXSpacing` — 4dp-based scale: `xs=4, sm=8, md=16, lg=24, xl=32`.
- `CryptoXShapes` — corner radii matching TaminX cards.

### 2.2 Theme
- `CryptoXTheme(darkTheme: Boolean, content: @Composable () -> Unit)` exposing tokens via `CompositionLocal`s + Material3 mapping.

### 2.3 Core Components (exact signatures — SHARED CONTRACT)
Other agents call these TODAY. Signatures are frozen:

```kotlin
@Composable fun CXScaffold(topBar: @Composable () -> Unit = {}, bottomBar: @Composable () -> Unit = {}, content: @Composable (PaddingValues) -> Unit)
@Composable fun CXTopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {})
@Composable fun CXCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit)
@Composable fun CXPriceText(price: Double, currencySymbol: String, modifier: Modifier = Modifier, style: TextStyle? = null)
@Composable fun CXChangeBadge(changePercent: Double, modifier: Modifier = Modifier) // green/red with arrow
@Composable fun CXSparkline(points: List<Double>, isPositive: Boolean, modifier: Modifier = Modifier)
@Composable fun CXCoinListItem(iconUrl: String, name: String, symbol: String, price: Double, currencySymbol: String, changePercent: Double, sparkline: List<Double>, onClick: () -> Unit)
@Composable fun CXLoadingShimmer(modifier: Modifier = Modifier)
@Composable fun CXErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier)
@Composable fun CXEmptyState(title: String, subtitle: String, modifier: Modifier = Modifier)
@Composable fun CXPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true)
@Composable fun CXSearchField(query: String, onQueryChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier)
```

### 2.4 Preview Gallery
- A `@Preview` file per component group (light + dark) so the design can be reviewed at a glance.

## 3. Rules
1. Match TaminX visual language first, Material defaults second.
2. No feature logic, no ViewModels, no networking — pure UI module.
3. Numbers (prices) always use the mono/tabular typography token.
4. All colors/spacing/typography come from tokens — zero hardcoded values inside components.
5. Every public component gets KDoc + at least one Preview.

## 4. Definition of Done
- Module compiles standalone.
- Preview gallery renders all components in light + dark.
- All contract signatures implemented exactly as written.
- A short `DESIGNSYSTEM.md` documenting tokens + component usage.
