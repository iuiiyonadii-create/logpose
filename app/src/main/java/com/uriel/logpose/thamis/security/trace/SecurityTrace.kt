package com.uriel.logpose.thamis.security.trace

import com.uriel.logpose.thamis.security.model.SecurityTrace

/**
 * Almacén circular de trazas de seguridad.
 */
object SecurityAuditLog {
    private val logs = mutableListOf<SecurityTrace>()

    fun record(trace: SecurityTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<SecurityTrace> = logs.toList()
}
