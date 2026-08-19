package com.uriel.logpose.thamis.security.model

import java.util.*

/**
 * Representa un evento de seguridad o acceso a recursos auditado.
 */
data class SecurityEvent(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val module: String,
    val resource: ResourceType,
    val action: String,
    val reason: String,
    val durationMs: Long?,
    val result: String
)

enum class ResourceType {
    MICROPHONE,
    BLUETOOTH,
    LOCATION,
    NOTIFICATIONS,
    NETWORK,
    INTERNAL_STORAGE,
    SYSTEM_CONFIG
}

/**
 * Niveles de alerta de seguridad.
 */
enum class SecurityAlertLevel {
    INFO,
    WARNING,
    CRITICAL
}

/**
 * Reporte de salud de seguridad del sistema.
 */
data class SecurityReport(
    val summary: String,
    val statusMap: Map<ResourceType, String>,
    val alerts: List<SecurityAlert>,
    val timestamp: Long = System.currentTimeMillis()
)

data class SecurityAlert(
    val level: SecurityAlertLevel,
    val message: String,
    val module: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Trazabilidad granular de auditoría.
 */
data class SecurityTrace(
    val event: String,
    val module: String,
    val permission: String?,
    val reason: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
