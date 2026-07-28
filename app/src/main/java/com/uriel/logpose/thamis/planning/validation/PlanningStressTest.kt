package com.uriel.logpose.thamis.planning.validation

import com.uriel.logpose.thamis.planning.planner.PlanningEngine
import com.uriel.logpose.thamis.planning.model.PlanningPriority

/**
 * Suite de simulación para validar la planificación bajo presión.
 */
class PlanningStressTest {

    fun runScenario() {
        val goals = listOf("NAV_HOME", "PLAY_ROCKSTAR", "CALL_MAMA", "READ_WHATSAPP", "GPS_ALARM")
        
        goals.forEach { goal ->
            PlanningEngine.plan(goal, PlanningPriority.NORMAL)
        }
    }
}
