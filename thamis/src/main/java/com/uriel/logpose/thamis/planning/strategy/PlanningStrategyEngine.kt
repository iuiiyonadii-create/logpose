package com.uriel.logpose.thamis.planning.strategy

import com.uriel.logpose.thamis.planning.model.PlanningStrategy
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Selecciona la estrategia óptima basada en el estado actual del mundo.
 */
object PlanningStrategyEngine {

    fun selectStrategy(world: WorldSnapshot): PlanningStrategy {
        val speed = world.vehicle.speedKmh
        val risk = world.vehicle.riskLevel.name
        
        return when {
            risk == "CRITICAL" -> PlanningStrategy.SAFEST
            speed > 110f -> PlanningStrategy.LOWEST_INTERRUPTION
            world.systems.device.batteryPct < 15 -> PlanningStrategy.ENERGY_SAVING
            else -> PlanningStrategy.FASTEST
        }
    }
}
