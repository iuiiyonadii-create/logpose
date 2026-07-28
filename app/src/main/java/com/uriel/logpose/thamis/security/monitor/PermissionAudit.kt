package com.uriel.logpose.thamis.security.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.security.model.ResourceType
import com.uriel.logpose.thamis.security.model.SecurityEvent

/**
 * Control y auditoría de uso de permisos del sistema.
 */
object PermissionAudit {
    private val auditLog = mutableListOf<SecurityEvent>()

    fun recordAccess(module: String, resource: ResourceType, reason: String, durationMs: Long? = null) {
        val event = SecurityEvent(
            module = module,
            resource = resource,
            action = "ACCESS",
            reason = reason,
            durationMs = durationMs,
            result = "SUCCESS"
        )
        auditLog.add(event)
        if (auditLog.size > 500) auditLog.removeAt(0)
        
        LogPoseLogger.i("THAMIS_PERMISSION: [${resource.name}] utilizado por $module. Motivo: $reason")
    }

    fun getLogs(): List<SecurityEvent> = auditLog.toList()
}
