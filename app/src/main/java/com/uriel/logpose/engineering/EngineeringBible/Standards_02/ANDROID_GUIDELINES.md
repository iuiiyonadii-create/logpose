# ANDROID_GUIDELINES

Engineering Bible — Standards_02
Status: OFFICIAL
Applies to: every Android-facing file under `com.uriel.logpose`

Project configuration reference (`app/build.gradle.kts`):
- `namespace` / `applicationId`: `com.uriel.logpose`
- `minSdk = 26`, `targetSdk = 36`, `compileSdk = 36`
- `sourceCompatibility` / `targetCompatibility`: Java 11
- Jetpack Compose enabled (`buildFeatures.compose = true`)

------------------------------------------------------------------------------

## Purpose

This document turns the "Android Engineering Rules" section of
`MASTER_PROMPT_LOGPOSE_v2` into concrete guidelines for every Android-facing
file, and defines how the official module boundaries
(`app / core / data / domain / features / logcore / logprobe / thamis`)
map to Android-specific concerns.

------------------------------------------------------------------------------

## 1. Where Android Code Is Allowed

Android SDK classes (`android.*`, `androidx.*`) may be used only inside:

- `app/` (entry point: `MainActivity`, `AppContainer`, navigation, UI)
- `core/` (infrastructure that legitimately wraps Android — e.g.
  `core/permissions`, `core/services`, `core/notifications`)
- `data/` (persistence implementations that use Android storage APIs)
- `features/*` (each feature's own Android-facing layer)
- `logcore/` **only** where it is infrastructure (e.g. `AndroidTools.kt`,
  `IntentTools.kt`, `BundleTools.kt`, `UriTools.kt`, `PermissionTools.kt`
  are accepted exceptions because they thinly wrap Android primitives
  without any business logic)
- `logprobe/` (diagnostic module, frozen)
- `ui/` (Compose screens, theme, viewmodels)

`domain/` must **never** import `android.*` or `androidx.*`. It must
compile as pure Kotlin/JVM. This is non-negotiable — it is what keeps
business logic portable and testable without an emulator.

`thamis/` must remain independent from Android UI. Android SDK usage
inside `thamis/` is only acceptable for infrastructure concerns explicitly
approved in that module's PRD; cognitive/decision logic must stay
platform-independent.

## 2. Layering Inside a Feature

Every file must sit in exactly one of these conceptual layers, and a
layer must not reach into the internals of another:

- **UI** — Composables, screens (`ui/screen`), theme (`ui/theme`).
- **Application** — ViewModels (`ui/viewmodel`) that coordinate UI state.
- **Infrastructure** — Providers, services, platform wrappers
  (`core/`, `logcore/`).
- **Business** — Domain entities, use cases, repository contracts
  (`domain/`).
- **Persistence** — Data sources, cache, database, preferences (`data/`).

## 3. Jetpack Compose

- A Composable function describes UI and only UI. It reads state and
  emits events; it does not compute business rules inline.
- Never call a repository, data source, or provider directly from a
  Composable. All data access goes through a `ViewModel`.
- Prefer immutable UI state: model screen state as a `data class` or
  `sealed interface` exposed as `StateFlow`/`State`, never as mutable
  fields read directly by the UI.
- Keep Composables small and named after what they render (see
  `NAMING_CONVENTIONS.md` Section 3: `*Screen` for a full screen,
  a descriptive name for a reusable component).
- Preview functions (`@Preview`) are encouraged for every non-trivial
  Composable and must use fake/sample state, never a live ViewModel.

## 4. ViewModels

- A `ViewModel` coordinates: it reads from domain/repository contracts
  and exposes state to the UI. It does not own business rules — those
  belong in `domain/`.
- Never perform persistence directly inside a `ViewModel` (no direct
  database/file/network calls); go through a repository contract.
- Keep `ViewModel`s scoped to one screen or one clearly bounded feature
  area. A `ViewModel` that accumulates responsibilities across unrelated
  screens is a God ViewModel and must be split.
- Coroutines launched from a `ViewModel` must use `viewModelScope`.

## 5. Services

- Foreground/background `Service` subclasses contain only service
  lifecycle behavior (start, stop, binding, notification management).
- Business rules invoked from a `Service` must live in `domain/` or the
  feature's own logic layer, injected into the service, not written
  inline inside it.
- Keep `Service` classes lightweight; if a service accumulates parsing,
  persistence, and business logic inline, extract each into its own
  class named per `NAMING_CONVENTIONS.md`.
- Diagnostic services in `logprobe/pro` (e.g. `AccessibilityProbeService`,
  `NotificationProbeService`) are the reference pattern: the service
  delegates capture/parsing to dedicated `Probe`/`Collector`/`Parser`
  classes rather than doing the work inline.

## 6. Permissions

- Permission request/check logic is isolated in one place per concern —
  see `core/permissions` and `logcore/tools/PermissionTools.kt`.
- Never duplicate permission-checking logic across multiple features.
  A feature that needs a permission already handled by
  `core/permissions` or `PermissionTools` must reuse it, not
  reimplement it.
- Runtime permission rationale and request flow belongs in the `app`/`ui`
  layer (where user-facing dialogs live), not in `domain` or `logcore`.

## 7. Notifications

- Notification channel creation and management is centralized in
  `core/notifications`. Features requiring notifications call into that
  module rather than constructing `NotificationChannel`/`NotificationManager`
  independently.

## 8. Resources and Configuration

- String, dimension, and color resources are scoped and named per feature
  where practical (see `NAMING_CONVENTIONS.md` Section 8).
- Hardcoded user-facing strings inside Composables are forbidden once a
  feature has a `strings.xml` entry point; use string resources.
- `minSdk = 26` is the compatibility floor. Any API requiring a higher
  `minSdk` must be guarded (`Build.VERSION.SDK_INT` check) or routed
  through `AndroidTools.kt`/`core/compat` if a compatibility shim already
  exists there.

## 9. Testing on Android

- Prefer plain JVM unit tests for anything in `domain/` and pure Kotlin
  parts of `logcore/` — they must not require an Android instrumentation
  environment.
- Instrumented tests (`androidx.test.runner.AndroidJUnitRunner`, already
  configured in `app/build.gradle.kts`) are reserved for code that
  genuinely needs a device/emulator (UI, Services, ContentProviders).

## 10. Pre-Delivery Android Checklist

Before an Android-facing file is considered complete, verify:

- ✓ No `android.*`/`androidx.*` import inside `domain/`
- ✓ Composables contain no business logic
- ✓ ViewModel does not touch persistence directly
- ✓ Services delegate business/parsing logic to dedicated classes
- ✓ No duplicated permission-handling logic
- ✓ New API usage respects `minSdk = 26` (guarded or shimmed)
- ✓ User-facing strings are resources, not hardcoded literals

------------------------------------------------------------------------------
END OF DOCUMENT
