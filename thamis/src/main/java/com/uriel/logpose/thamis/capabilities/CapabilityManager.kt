package com.uriel.logpose.thamis.capabilities

/**
 * Orquestador de las capacidades cognitivas.
 */
object CapabilityManager {
    fun initialize() {
        // Registro inicial de capacidades
        CapabilityRegistry.register(CapabilityDescriptor(
            name = "MULTIMEDIA",
            type = CapabilityType.MULTIMEDIA,
            version = "3.1",
            status = CapabilityStatus.AVAILABLE,
            authorityEnabled = true,
            provider = "Spotify",
            validator = "MusicAuthorityValidator",
            actuator = "MusicActuator",
            availability = true
        ))
        
        CapabilityRegistry.register(CapabilityDescriptor(
            name = "NAVIGATION",
            type = CapabilityType.NAVIGATION,
            version = "1.0",
            status = CapabilityStatus.AVAILABLE,
            authorityEnabled = true,
            provider = "ProviderAbstraction",
            validator = "NavigationAuthorityValidator",
            actuator = "NavigationActuator",
            availability = true
        ))

        CapabilityRegistry.register(CapabilityDescriptor(
            name = "COMMUNICATION",
            type = CapabilityType.COMMUNICATION,
            version = "1.0",
            status = CapabilityStatus.AVAILABLE,
            authorityEnabled = true,
            provider = "CommProviderFactory",
            validator = "CommValidator",
            actuator = "CommunicationActuator",
            availability = true
        ))
    }

    fun isAuthorityEnabled(type: CapabilityType): Boolean {
        return CapabilityRegistry.getCapability(type)?.authorityEnabled ?: false
    }
}
