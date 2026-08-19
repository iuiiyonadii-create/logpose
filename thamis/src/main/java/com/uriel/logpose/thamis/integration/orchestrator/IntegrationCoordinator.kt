package com.uriel.logpose.thamis.integration.orchestrator

import com.uriel.logpose.thamis.integration.priority.GlobalPriorityEngine
import com.uriel.logpose.thamis.integration.model.GlobalPriority

/**
 * Coordinador encargado de resolver conflictos entre dominios activos.
 */
object IntegrationCoordinator {

    fun resolveConflict(domainA: String, priorityA: GlobalPriority, domainB: String, priorityB: GlobalPriority): String {
        return if (GlobalPriorityEngine.isHigherThan(priorityA, priorityB)) {
            domainA
        } else {
            domainB
        }
    }
}
