# CHANGELOG — logcore/providers

## v0.1.0 — Initial implementation

Added:

- `ProviderRegistry` contract with `register`, `get`, `getOrNull`,
  `isRegistered`, `dispose`, plus reified extension functions.
- `DefaultProviderRegistry` — thread-safe, lazy-singleton implementation.
- `ProviderLifecycle` — optional `onCreate`/`onDispose` hooks.
- `ProviderModule` — registration seam for feature modules.
- `ProviderNotRegisteredException`, `ProviderAlreadyRegisteredException`,
  `ProviderInitializationException`.
- Full unit test suite (`DefaultProviderRegistryTest`).
- `AppContainer` now owns a `ProviderRegistry` instance, ready for feature
  modules to register into once they expose a `ProviderModule`.

Context:

- Preceded by an architectural cleanup (see
  `engineering/00_Project/PROJECT_STATE.md` and
  `engineering/90_Legacy/README.md`) that archived an earlier,
  non-compiling LogCore orchestrator experiment which had — correctly —
  anticipated a `MusicProvider` interface, but incorrectly placed a
  concrete provider inside `logcore`. This implementation supersedes that
  experiment while correcting that placement error: `logcore/providers`
  ships the registration mechanism only, never a concrete provider.

No known defects. No TODOs. No placeholders.
