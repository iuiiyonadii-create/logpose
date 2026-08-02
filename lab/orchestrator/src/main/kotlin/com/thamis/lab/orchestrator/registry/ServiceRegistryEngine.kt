package com.thamis.lab.orchestrator.registry

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.ConcurrentHashMap

public data class RegisteredServiceDescriptor(
    public val serviceName: String,
    public val interfaceType: String,
    public val version: String,
    public val isHealthy: Boolean
)

/**
 * Service Registry Engine providing automatic service discovery, dependency registration, and health monitoring.
 */
public class ServiceRegistryEngine {
    private val TAG = "ServiceRegistryEngine"
    private val registry = ConcurrentHashMap<String, RegisteredServiceDescriptor>()

    public fun registerService(descriptor: RegisteredServiceDescriptor) {
        registry[descriptor.serviceName] = descriptor
        LabLogger.info(TAG, "Registered service '${descriptor.serviceName}' (${descriptor.interfaceType}) v${descriptor.version}.")
    }

    public fun getRegisteredServices(): List<RegisteredServiceDescriptor> = registry.values.toList()
}
