package com.uriel.logpose.logcore.providers

/**
 * A unit of provider registration owned by a feature module.
 *
 * LogCore never depends on any feature. Instead, each feature module
 * (Bluetooth, Voice, Music, Automation, ...) exposes one [ProviderModule]
 * that knows how to register its own concrete provider(s) into a
 * [ProviderRegistry]. The composition root (see `app/AppContainer`)
 * collects every feature's [ProviderModule] and applies them at startup.
 *
 * This keeps the dependency direction correct: features depend on
 * `logcore.providers`, never the other way around.
 *
 * Example (illustrative — no feature module implements this yet):
 * ```
 * class MusicProviderModule(
 *     private val musicProvider: MusicProvider
 * ) : ProviderModule {
 *     override fun registerInto(registry: ProviderRegistry) {
 *         registry.register<MusicProvider> { musicProvider }
 *     }
 * }
 * ```
 */
fun interface ProviderModule {

    fun registerInto(registry: ProviderRegistry)
}
