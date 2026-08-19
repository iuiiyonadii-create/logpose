package com.uriel.logpose.thamis.world.model

/**
 * Capa de compatibilidad para evitar romper módulos existentes (Multimedia, Navigation, etc).
 */
typealias WorldState = WorldSnapshot

val WorldSnapshot.driving get() = vehicle
val WorldSnapshot.audio get() = systems.audio
val WorldSnapshot.navigation get() = systems.navigation
val WorldSnapshot.communication get() = systems.communication
val WorldSnapshot.device get() = systems.device
val WorldSnapshot.journey get() = systems.device // Placeholder mapping for journey if needed
