# AGENT 5 — CI/CD with GitHub Actions (CryptoX)
> **Recommended model tier: WEAK/MEDIUM** — mostly YAML configuration; verify by running, not by trusting.
> Can run in parallel with all other agents (touches only `.github/`, root gradle config, and README badges).
> Prerequisite to be USEFUL: Phase 0 project skeleton exists and `./gradlew build` passes locally.

---

## 1. Context
- Project: **CryptoX** — Kotlin Multiplatform (Android-first, iOS/Desktop compile-only), Gradle with Version Catalog + Convention Plugins in `build-logic/`.
- Repo is PUBLIC and is a professional showcase: CI must look clean — green badges, fast runs, readable workflow files with comments.
- Your scope: `.github/workflows/`, quality tooling config (detekt/ktlint), README badges. Nothing else.

## 2. Deliverables

### 2.1 Workflow 1 — `ci.yml` (Pull Request + push to main)
Jobs (fail fast, run in parallel where possible):

1. **lint** — detekt + ktlint (`./gradlew detekt ktlintCheck`). Upload reports as artifacts.
2. **unit-tests** — `./gradlew testDebugUnitTest allTests` (KMP common tests included). Publish test report artifact; annotate failures on the PR.
3. **build-android** — `./gradlew :composeApp:assembleDebug`. Upload the debug APK as an artifact (reviewers can download & install).
4. **build-desktop** — `./gradlew :composeApp:packageDistributionForCurrentOS` (compile check only, ubuntu runner).
5. **build-ios** *(optional job, `continue-on-error: true`)* — `./gradlew :composeApp:compileKotlinIosArm64` on `macos-latest`. Keep optional: macOS minutes are expensive; it must never block a merge in MVP.

Required practices:
- `concurrency` group per branch with `cancel-in-progress: true` (no wasted runs on force-push).
- Gradle caching via `gradle/actions/setup-gradle` (official action) — target: warm PR run under ~8 minutes.
- JDK 17 via `actions/setup-java` (temurin).
- Trigger paths-ignore for `**.md` and `docs/**` (doc changes should not burn CI minutes).

### 2.2 Workflow 2 — `release.yml` (on tag `v*`)
1. Run full test suite.
2. Build **signed release APK + AAB**:
   - Keystore delivered via GitHub Secrets: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` (decode base64 → file at build time; never commit the keystore).
3. Create a **GitHub Release** with auto-generated changelog (conventional commits) and attach APK/AAB.
4. *(Stub for later, commented out)*: Firebase App Distribution / Play Console upload step — documented but disabled, so the growth path is visible to readers.

### 2.3 Workflow 3 — `nightly.yml` (schedule, cron weekly)
- Full build on all targets + dependency vulnerability scan (`gradle dependencyUpdates` report or `dependency-review-action` on PRs).
- Purpose: catch breakage from the outside world without blocking daily work.

### 2.4 Quality config
- `detekt.yml` tuned sensibly (no absurd defaults that spam warnings), `.editorconfig` for ktlint.
- Pre-commit hook script in `scripts/pre-commit` (optional install, documented) running ktlintFormat + detekt on changed files.

### 2.5 README badges
Add at top of README: CI status, latest release, Kotlin version, license. Badges must point at THIS repo's workflows.

## 3. Rules
1. Use only official/first-party actions (`actions/*`, `gradle/actions/*`) — this is a security-conscious showcase; no random third-party actions.
2. Pin action versions (e.g. `@v4`), never `@master`.
3. Every workflow file starts with a comment block explaining what it does and when it runs — the workflows are part of the portfolio and will be read by humans.
4. Secrets are referenced, never echoed; add a comment listing which secrets the repo owner must configure.
5. No deployment to stores in MVP — release = GitHub Release artifacts only.

## 4. Definition of Done
- A PR with a deliberate test failure turns red with a readable annotation; fixing it turns green.
- Debug APK downloadable from a PR run's artifacts.
- Tagging `v0.1.0` produces a GitHub Release with signed APK attached.
- Warm CI run < ~8 min; badges render in README.

## 5. Owner setup checklist (manual, one-time — for Javid, not the agent)
- [ ] Generate a release keystore locally; base64-encode; add the 4 secrets in repo Settings → Secrets → Actions.
- [ ] Enable "Require status checks" branch protection on `main` (lint + unit-tests + build-android).
- [ ] First green run: verify cache is being reused on the second run.
