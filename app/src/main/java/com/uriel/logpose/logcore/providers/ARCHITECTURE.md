# ARCHITECTURE — logcore/providers

For the full design rationale (alternatives considered, why this shape was
chosen), see `engineering/01_LogCore/Providers/ARCHITECTURE.md`. This file
documents the code as it exists, for anyone reading the module directly.

------------------------------------------------------------------------------

## Position in the dependency graph

```
features/*  ──depends on──>  logcore/providers  <──depends on──  app/
```

`logcore/providers` imports nothing from `features/`, `data/`, `domain/`,
`app/`, or `thamis/`, and nothing from `android.*`. It is compiled and
tested as plain Kotlin/JVM.

## Types

**`ProviderRegistry`** (interface) — the only type most code touches.
Defines `register`, `get`, `getOrNull`, `isRegistered`, `dispose` against a
`KClass<T>`, plus reified extension functions of the same names that infer
the `KClass` from a type parameter, so call sites never mention `KClass`.

**`DefaultProviderRegistry`** — the sole implementation. Two maps:
`factories: MutableMap<KClass<*>, () -> Any>` and
`instances: MutableMap<KClass<*>, Any>`. Both guarded by one `synchronized`
lock. See inline KDoc for the full behavior of each method.

**`ProviderLifecycle`** — optional interface a provider implements for
setup/teardown. Both methods default to no-op.

**`ProviderModule`** — `fun interface` a feature module implements to
register its own providers without `logcore` depending on that feature.

**`ProviderExceptions.kt`** — `ProviderNotRegisteredException`,
`ProviderAlreadyRegisteredException`, `ProviderInitializationException`.
All three are `RuntimeException`s representing programmer/wiring errors,
not recoverable business conditions.

## Lifecycle summary

1. `register<T> { factory }` — stores the factory. No instance created yet.
2. First `get<T>()` or `getOrNull<T>()` — runs the factory once, calls
   `onCreate()` if the result implements `ProviderLifecycle`, caches it.
3. Subsequent `get<T>()` calls — return the cached instance.
4. `dispose()` — calls `onDispose()` on every *created* instance, then
   clears both maps. The registry is not usable afterward.

## Threading

Every `ProviderRegistry` operation is safe to call from any thread. A
single intrinsic lock (`synchronized`) serializes registration and
resolution, including the first (creating) call to `get`/`getOrNull`, so a
factory is guaranteed to run at most once even under concurrent first
access from multiple threads.

## Extension points

Adding a new provider to the system never requires changing this module.
A feature module:

1. Defines its own contract interface.
2. Implements it.
3. Exposes a `ProviderModule` that calls `registry.register<Contract> { impl }`.
4. Is added to the list of modules the composition root applies.

## Non-goals (see engineering/ ARCHITECTURE.md section 12 for why)

- No scopes beyond singleton (no per-call/factory scope).
- No reflection-based auto-discovery.
- No coroutine/suspend API surface.
