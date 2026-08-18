# INTEGRATION GUIDE — logcore/providers

How to plug a new provider into the system, and how the app composition
root wires the registry.

------------------------------------------------------------------------------

## For a feature module (e.g. a future Bluetooth or Music migration)

### Step 1 — Define the contract in your own feature package

```kotlin
package com.uriel.logpose.features.music

interface MusicProvider {
    fun play()
    fun pause()
    fun next()
    fun previous()
    fun isPlaying(): Boolean
}
```

### Step 2 — Implement it

```kotlin
package com.uriel.logpose.features.music

class SpotifyMusicProvider(
    private val context: android.content.Context
) : MusicProvider {
    override fun play() { /* Spotify integration */ }
    override fun pause() { /* Spotify integration */ }
    override fun next() { /* Spotify integration */ }
    override fun previous() { /* Spotify integration */ }
    override fun isPlaying(): Boolean = false // placeholder for illustration only
}
```

If the implementation needs setup/teardown, implement `ProviderLifecycle`
as well:

```kotlin
class SpotifyMusicProvider(
    private val context: android.content.Context
) : MusicProvider, ProviderLifecycle {
    override fun onCreate() { /* open connection */ }
    override fun onDispose() { /* close connection */ }
    // ...
}
```

### Step 3 — Expose a ProviderModule

```kotlin
package com.uriel.logpose.features.music

import com.uriel.logpose.logcore.providers.ProviderModule
import com.uriel.logpose.logcore.providers.ProviderRegistry
import com.uriel.logpose.logcore.providers.register

class MusicProviderModule(
    private val context: android.content.Context
) : ProviderModule {
    override fun registerInto(registry: ProviderRegistry) {
        registry.register<MusicProvider> { SpotifyMusicProvider(context) }
    }
}
```

### Step 4 — Add it to the composition root

In `app/AppContainer.kt`, add the new module to the list applied during
initialization (see below).

------------------------------------------------------------------------------

## For a consumer (any module that wants to use a provider)

```kotlin
import com.uriel.logpose.logcore.providers.get

val music = appContainer.providerRegistry.get<MusicProvider>()
music.play()
```

If the provider is genuinely optional, use `getOrNull<T>()` instead of
`get<T>()`.

------------------------------------------------------------------------------

## Composition root wiring (`app/AppContainer.kt`)

`AppContainer` owns one `ProviderRegistry` for the process lifetime,
exposed as `providerRegistry`. As feature modules migrate to expose a
`ProviderModule`, add them to the list applied in `initialize()`:

```kotlin
private val providerModules: List<ProviderModule> = listOf(
    // MusicProviderModule(context),
    // BluetoothProviderModule(context),
)

fun initialize() {
    providerModules.forEach { it.registerInto(providerRegistry) }
}
```

No feature module currently exposes a `ProviderModule` (Bluetooth, Voice,
Music, and Automation are all Pending per
`engineering/00_Project/PROJECT_STATE.md`), so this list starts empty.
Nothing about `logcore/providers` needs to change for a feature to adopt
it later.

------------------------------------------------------------------------------

## Testing a consumer in isolation

Because `ProviderRegistry` is an interface, tests can supply a fresh
`DefaultProviderRegistry()` and register fakes directly — no Android
dependency, no mocking framework required:

```kotlin
val registry = DefaultProviderRegistry()
registry.register<MusicProvider> { FakeMusicProvider() }

val sut = SomeConsumer(registry)
// assert on sut's behavior against the fake
```
