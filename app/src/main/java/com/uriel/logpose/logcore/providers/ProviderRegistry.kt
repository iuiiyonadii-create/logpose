package com.uriel.logpose.logcore.providers

import kotlin.reflect.KClass

/**
 * Typed registry for resolving provider contracts across LogPose modules,
 * without those modules depending on each other's concrete classes.
 *
 * See `engineering/01_LogCore/Providers/ARCHITECTURE.md` for the full
 * design rationale.
 *
 * Consumers should prefer the reified extension functions below
 * ([register], [get], [getOrNull], [isRegistered]) over calling the
 * [KClass]-based members directly.
 */
interface ProviderRegistry {

    /**
     * Registers a lazy factory for [type]. The factory is not invoked
     * until the first [get] or [getOrNull] call for [type].
     *
     * @throws ProviderAlreadyRegisteredException if [type] is already
     * registered.
     */
    fun <T : Any> register(type: KClass<T>, factory: () -> T)

    /**
     * Resolves the provider registered for [type], creating it on first
     * access and caching it for the lifetime of this registry.
     *
     * @throws ProviderNotRegisteredException if [type] was never
     * registered.
     * @throws ProviderInitializationException if the registered factory
     * throws while creating the instance.
     */
    fun <T : Any> get(type: KClass<T>): T

    /**
     * Same as [get], but returns `null` instead of throwing when [type]
     * was never registered. Initialization failures still throw.
     */
    fun <T : Any> getOrNull(type: KClass<T>): T?

    /**
     * Returns `true` if a factory is registered for [type], regardless of
     * whether it has been resolved yet.
     */
    fun isRegistered(type: KClass<*>): Boolean

    /**
     * Disposes every instantiated provider that implements
     * [ProviderLifecycle], then clears the registry. The registry must
     * not be used after this call.
     */
    fun dispose()
}

/** Registers a lazy factory for [T]. See [ProviderRegistry.register]. */
inline fun <reified T : Any> ProviderRegistry.register(noinline factory: () -> T) {
    register(T::class, factory)
}

/** Resolves the provider registered for [T]. See [ProviderRegistry.get]. */
inline fun <reified T : Any> ProviderRegistry.get(): T {
    return get(T::class)
}

/**
 * Resolves the provider registered for [T], or `null` if none is
 * registered. See [ProviderRegistry.getOrNull].
 */
inline fun <reified T : Any> ProviderRegistry.getOrNull(): T? {
    return getOrNull(T::class)
}

/** Returns `true` if a factory is registered for [T]. */
inline fun <reified T : Any> ProviderRegistry.isRegistered(): Boolean {
    return isRegistered(T::class)
}
