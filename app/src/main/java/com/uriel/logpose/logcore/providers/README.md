# LogCore Providers

Typed registration and resolution of provider contracts, so LogPose
modules can expose and consume capabilities without depending on each
other's concrete classes.

Design record: `engineering/01_LogCore/Providers/ARCHITECTURE.md`
Module architecture detail: `ARCHITECTURE.md` (this folder)
Integration instructions: `INTEGRATION.md` (this folder)
History: `CHANGELOG.md` (this folder)

------------------------------------------------------------------------------

## What this module is

A small, dependency-free (pure Kotlin/JVM, no Android imports) registry:

- Feature modules register a contract (an interface they define) with an
  implementation factory.
- Any consumer resolves that contract by type and gets the registered
  implementation back.
- LogCore itself never knows what any contract or implementation is for —
  it only knows how to store and hand back typed factories.

## What this module is NOT

- Not a general-purpose DI framework. No annotations, no codegen, no
  classpath scanning.
- Not a place for concrete providers. `MusicProvider`, `BluetoothProvider`,
  etc. belong in their owning `features/*` module, never here.
- Not a state container for business logic. It resolves and disposes
  instances; it does not decide what they do.

## Quick usage

```kotlin
// 1. A feature module defines a contract and an implementation.
interface ClockProvider {
    fun nowMillis(): Long
}

class SystemClockProvider : ClockProvider {
    override fun nowMillis(): Long = System.currentTimeMillis()
}

// 2. The feature module registers it.
registry.register<ClockProvider> { SystemClockProvider() }

// 3. Any consumer resolves it, without knowing SystemClockProvider exists.
val now = registry.get<ClockProvider>().nowMillis()
```

See `INTEGRATION.md` for how a feature module wires this through
`ProviderModule` and `AppContainer`.

## Files

| File | Responsibility |
|---|---|
| `ProviderRegistry.kt` | Public contract + reified extension functions |
| `DefaultProviderRegistry.kt` | Thread-safe default implementation |
| `ProviderLifecycle.kt` | Optional `onCreate`/`onDispose` hooks |
| `ProviderModule.kt` | Registration seam for feature modules |
| `ProviderExceptions.kt` | Domain-specific resolution failures |

## Status

In Development — see `engineering/00_Project/PROJECT_STATE.md`.
