package com.uriel.logpose.thamis.integration.context

import java.util.concurrent.ConcurrentHashMap

/**
 * Intercambiador de información compartido entre motores.
 */
object ContextBus {
    private val data = ConcurrentHashMap<String, Any>()

    fun publish(key: String, value: Any) {
        data[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        return data[key] as? T
    }

    fun contains(key: String): Boolean = data.containsKey(key)

    fun clear() {
        data.clear()
    }
}
