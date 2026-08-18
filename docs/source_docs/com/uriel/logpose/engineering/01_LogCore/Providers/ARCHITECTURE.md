# LogCore Providers — Design (Phase 2)

Status: OFFICIAL
Module: logcore/providers
Governs implementation in: app/src/main/java/com/uriel/logpose/logcore/providers/

------------------------------------------------------------------------------

# 1. PROBLEM

LogPose needs a way for feature modules (Bluetooth, Voice, Music,
Automation, and later EOS) to expose a capability to the rest of the
system — and for consumers (other features, `core/`, eventually THAMIS) to
obtain that capability — without any of them depending on each other's
concrete classes.

Two independent pieces of prior art in the repository already converge on
the same answer (see `engineering/90_Legacy/`):

- `docs/architecture/ARCHITECTURE.md` (legacy): a `Services → Providers →
  Android` pipeline, where a Provider is the adapter between a Service and
  the platform.
- The abandoned `androidTest/logcore` experiment (legacy): a `MusicProvider`
  interface, resolved and wrapped by a `MusicService`.

Both describe "Provider" as: **a contract for a single capability,
implemented by whatever concretely provides it** (an Android system API, a
third-party app integration, or a fake for testing) — resolved by name/type
rather than by direct reference.

The legacy experiment made one architectural mistake this design corrects:
it placed the concrete `MusicProvider` interface *inside* `logcore`. That
violates the PRD's Must-Not list and MASTER_PROMPT's LogCore Rules
("LogCore must never know Music"). LogCore Providers must therefore ship
the *mechanism* (registry, lifecycle, contracts for the mechanism itself),
never any concrete provider. Concrete providers (`MusicProvider`,
`BluetoothProvider`, ...) belong in their owning `features/*` module and
are registered into LogCore from there.

------------------------------------------------------------------------------

# 2. RESPONSIBILITY

`logcore/providers` owns exactly one responsibility: **typed registration
and resolution of provider contracts**, with lifecycle management.

It does not define what a "Bluetooth provider" or "Music provider" looks
like. It defines how *any* provider is registered, found, and disposed.

------------------------------------------------------------------------------

# 3. ARCHITECTURE

```
Feature module (e.g. features/music)
    defines:  MusicProvider (interface)
              SpotifyMusicProvider : MusicProvider
    supplies: a ProviderModule that registers SpotifyMusicProvider
              under the MusicProvider contract

Composition root (app/AppContainer)
    owns:     a ProviderRegistry instance
    applies:  every feature's ProviderModule at startup

Any consumer (core/, another feature, a ViewModel)
    calls:    registry.get<MusicProvider>()
    receives: the registered implementation, without knowing which one
```

Dependency direction (per MASTER_PROMPT's Dependency Rules — always
inward, LogCore knows nothing above it):

```
features/*  ──depends on──>  logcore/providers  <──depends on──  app/
(defines & registers              (mechanism only,          (composes:
 concrete providers)                zero feature knowledge)   applies modules)
```

`logcore/providers` has zero imports from `features/`, `data/`, `domain/`,
`app/`, or `thamis/`. It has zero Android imports. It compiles as plain
Kotlin/JVM, satisfying "LogCore should compile without Android whenever
possible."

------------------------------------------------------------------------------

# 4. CONTRACTS / PUBLIC API

## ProviderRegistry (interface)

The only type consumers and feature modules interact with.

- `fun <T : Any> register(type: KClass<T>, factory: () -> T)`
  Registers a lazy factory for a contract type. Throws
  `ProviderAlreadyRegisteredException` if `type` is already registered
  (fail fast on wiring bugs — two modules trying to provide the same
  contract is a programming error, not a runtime condition to tolerate
  silently).

- `fun <T : Any> get(type: KClass<T>): T`
  Resolves an instance, creating it on first access (see Lifecycle below).
  Throws `ProviderNotRegisteredException` if `type` was never registered.
  Throws `ProviderInitializationException` (wrapping the original cause)
  if the factory throws during creation.

- `fun <T : Any> getOrNull(type: KClass<T>): T?`
  Same as `get`, but returns `null` instead of throwing
  `ProviderNotRegisteredException` — for genuinely optional providers.
  Initialization failures still throw (a broken provider is never a silent
  `null`; an absent one is).

- `fun <T : Any> isRegistered(type: KClass<T>): Boolean`

- `fun dispose()`
  Disposes every instantiated (not merely registered) provider that
  implements `ProviderLifecycle`, then clears the registry. After
  `dispose()`, the registry is unusable — mirrors Android component
  teardown (e.g. `Service.onDestroy`).

Reified inline extension functions (`registry.register<T> { ... }`,
`registry.get<T>()`, `registry.getOrNull<T>()`, `registry.isRegistered<T>()`)
are provided for ergonomic call sites, so callers never touch `KClass`
directly. This is the only place reflection-adjacent (`KClass`) machinery
is used, and it is compile-time reified generics, not runtime classpath
scanning — no approval-requiring reflection per the Kotlin Engineering
Rules.

## ProviderLifecycle (interface, optional)

```
interface ProviderLifecycle {
    fun onCreate() {}
    fun onDispose() {}
}
```

A provider implements this only if it needs setup/teardown (e.g. opening
and closing a system resource). Both methods default to no-op, so trivial
providers implement nothing extra.

## ProviderModule (fun interface)

```
fun interface ProviderModule {
    fun registerInto(registry: ProviderRegistry)
}
```

The seam through which a feature module contributes its providers without
LogCore ever importing that feature. A composition root collects a
`List<ProviderModule>` and applies each one at startup.

## Exceptions

- `ProviderNotRegisteredException` — thrown by `get()` for an unknown type.
- `ProviderAlreadyRegisteredException` — thrown by `register()` on a
  duplicate type.
- `ProviderInitializationException` — thrown by `get()` when a factory
  fails; wraps the original cause via `cause`.

All three are domain-specific (per "Avoid generic Exception whenever
possible. Prefer domain-specific failures"), extend `RuntimeException`
(these are programmer-error conditions, not recoverable-by-catching
business flows), and carry a message identifying the offending type name.

------------------------------------------------------------------------------

# 5. LIFECYCLE

Registration is eager (the factory function itself is stored immediately);
instantiation is lazy (the factory only runs on the first `get()`/`getOrNull()`
call for that type). Rationale: registering many providers at app startup
should be near-free; paying construction cost (opening a Bluetooth adapter
handle, a media session, etc.) only happens for providers actually used.
This directly serves "Local processing first" / minimize CPU and memory
usage, and Android's own recommendation to defer hardware/service
acquisition until needed.

Once created, an instance is cached for the registry's lifetime (singleton
scope). No other scope (e.g. per-call factory) is introduced: nothing in
the current codebase needs more than one instance per contract, and adding
scope selection now would be a premature abstraction (YAGNI). If a future
module needs non-singleton scope, that is a new, additive capability, not
a redesign of this API.

`dispose()` calls `onDispose()` only on providers that were actually
created (never-resolved registrations have nothing to dispose), in
creation order is not guaranteed — providers must not depend on teardown
ordering relative to each other. If cross-provider teardown ordering is
ever needed, that provider should own the ordering internally (e.g. a
provider that composes two others), not the registry.

------------------------------------------------------------------------------

# 6. REGISTRATION / DEPENDENCY RESOLUTION STRATEGY

Explicit, code-based registration only. No classpath scanning, no
annotation processing, no reflection-based auto-discovery. Every
registration is a line of Kotlin someone wrote and can `Cmd+Click` to.

This is a deliberate rejection of a heavier DI framework (Dagger/Hilt),
consistent with:

- PROJECT_CONTEXT's Dependency Management rule: prefer Kotlin/Android SDK
  solutions before external dependencies; a ~150-line typed map satisfies
  the requirement, no library needed.
- `docs/Knowledge/rejected/Dagger.md` and `.../Hilt.md` (legacy, but
  consistent with this decision — both were already rejected for this
  project before this module existed).
- "One person must be able to develop, maintain and deploy the entire
  project" — a hand-rolled registry has zero build-time codegen, zero
  annotation-processor overhead, and a debugger can step through it
  exactly like any other code.

Resolution is by exact type (`KClass`), not by name/string, so a typo in a
contract name is a compile error, not a runtime `ClassNotFoundException`-
style failure.

------------------------------------------------------------------------------

# 7. THREADING

`DefaultProviderRegistry` is safe to call from any thread:

- `register()` and the internal factory map are guarded by a single
  intrinsic lock (`synchronized`). Registration happens at app startup,
  effectively single-threaded, so contention is a non-issue.
- `get()`/`getOrNull()` use double-checked locking around the same lock to
  avoid a provider's factory running twice under concurrent first-access
  from two threads (e.g. a UI thread and a background coroutine both
  resolving `BluetoothProvider` at the same moment).
- No coroutines, `Mutex`, or `GlobalScope` are used. The critical sections
  are pure in-memory map operations plus, at most, a provider factory
  invocation — expected to be fast and non-suspending. If a future
  provider's construction becomes genuinely expensive/suspending, that
  provider should do its own async initialization internally and expose a
  ready/not-ready state, rather than the registry adopting coroutines for
  every provider's sake (keeps `logcore` dependency-free of
  kotlinx-coroutines, which today is only a transitive dependency via
  Compose, not a declared one).

------------------------------------------------------------------------------

# 8. ERROR HANDLING

| Situation | Behavior |
|---|---|
| `get<T>()`, `T` never registered | `ProviderNotRegisteredException` (fail fast — this is a wiring bug) |
| `getOrNull<T>()`, `T` never registered | returns `null` (expected/recoverable path) |
| `register<T>()`, `T` already registered | `ProviderAlreadyRegisteredException` (fail fast — duplicate wiring is a bug) |
| Factory throws during first `get<T>()` | `ProviderInitializationException(cause = original)`; the failed attempt is **not** cached, so a later `get<T>()` retries construction (useful if the failure was transient, e.g. hardware not yet ready) |
| `dispose()` called twice | second call is a safe no-op (registry is already empty) |

No exception is ever swallowed. No error path returns a generic
`Exception` or a bare `null` where failure and absence are actually
different conditions.

------------------------------------------------------------------------------

# 9. FOLDER STRUCTURE

```
app/src/main/java/com/uriel/logpose/logcore/providers/
    ProviderRegistry.kt              (public contract + reified extensions)
    DefaultProviderRegistry.kt       (thread-safe default implementation)
    ProviderLifecycle.kt             (optional lifecycle contract)
    ProviderModule.kt                (registration seam for feature modules)
    ProviderExceptions.kt            (the three domain exceptions)
    README.md
    ARCHITECTURE.md
    CHANGELOG.md
    INTEGRATION.md

app/src/test/java/com/uriel/logpose/logcore/providers/
    DefaultProviderRegistryTest.kt
```

Five source files, five responsibilities, no God object. Matches the
existing `logcore/tools` convention of one clear file per concern.

------------------------------------------------------------------------------

# 10. INTEGRATION STRATEGY

`app/AppContainer.kt` becomes the composition root: it owns one
`ProviderRegistry` instance for the process, created during
`AppContainer.initialize()`, and would apply each feature's
`ProviderModule` list at that point.

No feature currently exposes a `ProviderModule` (Bluetooth, Voice, Music,
Automation are all Pending/not-yet-migrated per PROJECT_STATE.md), so this
delivery wires the registry itself into `AppContainer` — proving real
compilation and a real integration point — without inventing a concrete
provider on `features/`' behalf, which would exceed this module's
responsibility and require decisions (e.g. how `BluetoothRepository`
should be reshaped as a provider) that belong to the Bluetooth module's
own PRD.

Future modules integrate by: (1) defining their contract interface(s) in
their own `features/<name>/` package, (2) implementing them, (3) exposing
one `ProviderModule`, (4) adding that module to the list `AppContainer`
applies. LogCore Providers never changes for this to happen.

------------------------------------------------------------------------------

# 11. TESTING STRATEGY

`logcore/providers` is pure Kotlin/JVM with no Android dependency, so it
is tested entirely with JVM unit tests (`src/test/`, already configured via
`testImplementation(libs.junit)` — no new dependency needed).

Covered by `DefaultProviderRegistryTest.kt`:

- register + get returns the same (singleton) instance across repeated
  calls.
- get() before register() throws `ProviderNotRegisteredException`.
- getOrNull() before register() returns null.
- duplicate register() throws `ProviderAlreadyRegisteredException`.
- a factory that throws is wrapped in `ProviderInitializationException`,
  and a subsequent get() retries the factory rather than replaying the
  failure.
- a provider implementing `ProviderLifecycle` receives `onCreate()` on
  first resolution and `onDispose()` on `dispose()`.
- a provider that is registered but never resolved does not receive
  `onDispose()` (nothing to dispose).
- isRegistered() reflects registration state correctly before/after
  register().
- concurrent first-access from multiple threads creates the provider
  exactly once (factory invocation count == 1).

------------------------------------------------------------------------------

# 12. WHY THIS IS THE SIMPLEST SUFFICIENT DESIGN

Per the Architectural Decision Framework, alternatives considered:

- **A full DI framework (Dagger/Hilt).** Rejected: already rejected in
  project history (`docs/Knowledge/rejected/`), adds build-time codegen
  and a learning-curve dependency a single developer must maintain forever,
  for a problem a ~150-line class solves completely at this project's
  current and near-future scale.

- **String-keyed service locator.** Rejected: loses compile-time safety;
  typos become runtime failures instead of compile errors.

- **Multiple scopes (singleton/factory/per-feature).** Rejected for now:
  nothing in the codebase needs more than singleton scope; adding scope
  selection is a premature abstraction that can be added later without
  breaking the existing API (new optional parameter with a default),
  satisfying Open/Closed without paying the cost today.

- **Eager instantiation at registration time.** Rejected: wastes memory/CPU
  on providers that may never be used (e.g. an Automation provider on a
  session where Automation is never triggered), against the project's
  low-end-device performance goals.
