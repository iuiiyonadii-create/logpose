package com.uriel.logpose.thamis.proactive.timing

import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Determina el momento adecuado para iniciar una comunicación proactiva.
 */
object CommunicationTimingEngine {

    fun isGoodTime(world: WorldSnapshot): Boolean {
        // Un buen momento es cuando no hay navegación compleja, no hay llamada y la velocidad es estable
        return !world.systems.communication.isCallActive && 
               !world.systems.navigation.isNavigating && 
               world.vehicle.speedKmh < 100f
    }
}
