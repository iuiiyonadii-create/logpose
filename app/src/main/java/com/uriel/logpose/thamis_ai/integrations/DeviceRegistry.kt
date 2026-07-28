package com.uriel.logpose.thamis_ai.integrations

/**
 * Persists a list of known and authorized devices.
 */
class DeviceRegistry {
    private val authorizedIds = mutableSetOf<String>()

    fun register(id: String) { authorizedIds.add(id) }
    fun isRegistered(id: String) = authorizedIds.contains(id)
}
