package com.uriel.logpose.thamis.communication.trace

import com.uriel.logpose.thamis.communication.model.CommunicationDecision
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Traza detallada de una decisión de comunicación.
 */
data class CommunicationTrace(
    val decision: CommunicationDecision,
    val worldSnapshot: WorldSnapshot,
    val timestamp: Long = System.currentTimeMillis()
)

object CommunicationAudit {
    private val log = mutableListOf<CommunicationTrace>()

    fun record(trace: CommunicationTrace) {
        log.add(trace)
        if (log.size > 100) log.removeAt(0)
    }

    fun getLogs(): List<CommunicationTrace> = log.toList()
}
