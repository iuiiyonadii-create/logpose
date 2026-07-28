package com.uriel.logpose.thamis.notification.audit

import com.uriel.logpose.thamis.notification.model.NotificationDecision
import com.uriel.logpose.thamis.notification.model.NotificationContext

/**
 * Traza auditable de una decisión de notificación.
 */
data class NotificationTrace(
    val context: NotificationContext,
    val decision: NotificationDecision,
    val timestamp: Long = System.currentTimeMillis()
)

object NotificationAudit {
    private val traceLog = mutableListOf<NotificationTrace>()

    fun record(trace: NotificationTrace) {
        traceLog.add(trace)
        if (traceLog.size > 200) traceLog.removeAt(0)
    }

    fun getTraces(): List<NotificationTrace> = traceLog.toList()
}
