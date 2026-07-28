package com.uriel.logpose.thamis.orchestration.priority

import com.uriel.logpose.thamis.orchestration.model.OrchestrationDomain

/**
 * Define las prioridades base para cada dominio.
 */
object OrchestrationPriority {
    
    private val basePriorities = mapOf(
        OrchestrationDomain.EMERGENCY to 1000,
        OrchestrationDomain.SAFETY to 900,
        OrchestrationDomain.NAVIGATION to 800,
        OrchestrationDomain.COMMUNICATION to 700,
        OrchestrationDomain.SYSTEM to 600,
        OrchestrationDomain.MULTIMEDIA to 500,
        OrchestrationDomain.INFORMATION to 400
    )

    fun getBasePriority(domain: OrchestrationDomain): Int {
        return basePriorities[domain] ?: 0
    }
}
