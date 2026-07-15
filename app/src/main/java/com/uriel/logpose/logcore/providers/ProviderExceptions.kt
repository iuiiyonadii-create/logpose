package com.uriel.logpose.logcore.providers

import kotlin.reflect.KClass

/**
 * Thrown by [ProviderRegistry.get] when no provider has been registered
 * for the requested contract type.
 *
 * This is always a wiring bug: a consumer asked for a contract that no
 * module ever registered. It is intentionally not recoverable-by-catching
 * in normal flow; use [ProviderRegistry.getOrNull] when absence is
 * expected and acceptable.
 */
class ProviderNotRegisteredException(
    type: KClass<*>
) : RuntimeException(
    "No provider registered for ${type.qualifiedName}"
)

/**
 * Thrown by [ProviderRegistry.register] when a provider has already been
 * registered for the requested contract type.
 *
 * Two modules registering the same contract is always a wiring bug.
 */
class ProviderAlreadyRegisteredException(
    type: KClass<*>
) : RuntimeException(
    "A provider is already registered for ${type.qualifiedName}"
)

/**
 * Thrown by [ProviderRegistry.get] when a registered factory throws while
 * creating the provider instance.
 *
 * The original failure is preserved as [cause]. The failed attempt is not
 * cached by the registry, so a subsequent [ProviderRegistry.get] call for
 * the same type will retry construction.
 */
class ProviderInitializationException(
    type: KClass<*>,
    cause: Throwable
) : RuntimeException(
    "Failed to initialize provider for ${type.qualifiedName}: ${cause.message}",
    cause
)
