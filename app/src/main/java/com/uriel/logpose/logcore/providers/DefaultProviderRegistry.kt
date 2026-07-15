package com.uriel.logpose.logcore.providers

import kotlin.reflect.KClass

/**
 * Thread-safe default implementation of [ProviderRegistry].
 *
 * Registration is eager (the factory itself is stored immediately);
 * instantiation is lazy and cached (the factory runs once, on first
 * resolution, and the result is reused for the registry's lifetime). See
 * `engineering/01_LogCore/Providers/ARCHITECTURE.md`, section 5, for the
 * lifecycle rationale and section 7 for the threading rationale.
 *
 * All operations are guarded by a single intrinsic lock. Registration and
 * resolution are expected to be fast, non-suspending, in-memory
 * operations, so a coarse lock is the simplest correct choice — see
 * ARCHITECTURE.md section 7 for why a coroutine [kotlinx.coroutines.Mutex]
 * was deliberately not used here.
 */
class DefaultProviderRegistry : ProviderRegistry {

    private val lock = Any()

    private val factories = mutableMapOf<KClass<*>, () -> Any>()

    private val instances = mutableMapOf<KClass<*>, Any>()

    override fun <T : Any> register(type: KClass<T>, factory: () -> T) {
        synchronized(lock) {
            if (factories.containsKey(type)) {
                throw ProviderAlreadyRegisteredException(type)
            }
            factories[type] = factory
        }
    }

    override fun <T : Any> get(type: KClass<T>): T {
        return getOrNull(type) ?: throw ProviderNotRegisteredException(type)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getOrNull(type: KClass<T>): T? {
        synchronized(lock) {
            val cached = instances[type]
            if (cached != null) {
                return cached as T
            }

            val factory = factories[type] ?: return null

            val created: T = try {
                factory() as T
            } catch (error: Exception) {
                throw ProviderInitializationException(type, error)
            }

            if (created is ProviderLifecycle) {
                created.onCreate()
            }

            instances[type] = created

            return created
        }
    }

    override fun isRegistered(type: KClass<*>): Boolean {
        synchronized(lock) {
            return factories.containsKey(type)
        }
    }

    override fun dispose() {
        synchronized(lock) {
            instances.values.forEach { instance ->
                if (instance is ProviderLifecycle) {
                    instance.onDispose()
                }
            }
            instances.clear()
            factories.clear()
        }
    }
}
