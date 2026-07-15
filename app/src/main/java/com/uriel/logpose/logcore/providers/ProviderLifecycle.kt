package com.uriel.logpose.logcore.providers

/**
 * Optional lifecycle hooks for a provider instance.
 *
 * A provider implements this only if it holds a resource that needs
 * explicit setup or teardown (a system handle, a media session, an open
 * connection). Providers with no such need implement nothing extra —
 * both methods default to no-op.
 *
 * [onCreate] runs exactly once, immediately after the provider's factory
 * returns the instance, before it is handed to the caller that triggered
 * creation.
 *
 * [onDispose] runs exactly once per created instance, when
 * [ProviderRegistry.dispose] is called. Providers that were registered
 * but never resolved never receive [onDispose], since they were never
 * created.
 */
interface ProviderLifecycle {

    fun onCreate() {}

    fun onDispose() {}
}
