package com.uriel.logpose.thamis.planning.scheduler

import com.uriel.logpose.thamis.planning.model.*
import com.uriel.logpose.thamis.planning.queue.PlanningQueue
import com.uriel.logpose.thamis.planning.rules.PlanningRuleEngine
import com.uriel.logpose.thamis.world.engine.WorldModelEngine

/**
 * Orquestador temporal para la ejecución de los planes.
 */
object PlanningScheduler {

    /**
     * Evalúa el próximo plan en la cola y decide si es el momento de ejecutarlo.
     */
    fun tick(): PlanningDecision {
        val world = WorldModelEngine.getCurrentSnapshot()
        val nextPlan = PlanningQueue.peek() ?: return PlanningDecision.IGNORE

        val decision = PlanningRuleEngine.evaluate(nextPlan, world)

        if (decision == PlanningDecision.EXECUTE_NOW) {
            // En un sistema real, aquí dispararíamos la ejecución
            PlanningQueue.poll()
        }

        return decision
    }
}
