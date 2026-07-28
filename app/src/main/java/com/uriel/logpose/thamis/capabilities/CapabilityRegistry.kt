package com.uriel.logpose.thamis.capabilities

import java.util.concurrent.ConcurrentHashMap

/**
 * Registro centralizado de todas las capacidades cognitivas de THAMIS.
 */
object CapabilityRegistry {
    private val capabilities = ConcurrentHashMap<CapabilityType, CapabilityDescriptor>()

    fun register(descriptor: CapabilityDescriptor) {
        capabilities[descriptor.type] = descriptor
    }

    fun getCapability(type: CapabilityType): CapabilityDescriptor? = capabilities[type]

    fun getAllCapabilities(): List<CapabilityDescriptor> = capabilities.values.toList()

    fun updateStatus(type: CapabilityType, newStatus: CapabilityStatus) {
        val current = capabilities[type] ?: return
        capabilities[type] = current.copy(status = newStatus)
    }
}
