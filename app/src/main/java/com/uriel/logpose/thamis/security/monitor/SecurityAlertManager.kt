package com.uriel.logpose.thamis.security.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.security.model.SecurityAlert
import com.uriel.logpose.thamis.security.model.SecurityAlertLevel

/**
 * Gestor de alertas de seguridad y violaciones de política.
 */
object SecurityAlertManager {
    private val alerts = mutableListOf<SecurityAlert>()

    fun raiseAlert(level: SecurityAlertLevel, message: String, module: String) {
        val alert = SecurityAlert(level, message, module)
        alerts.add(alert)
        if (alerts.size > 100) alerts.removeAt(0)
        
        when (level) {
            SecurityAlertLevel.CRITICAL -> LogPoseLogger.e("THAMIS_SECURITY_CRITICAL: [$module] $message")
            SecurityAlertLevel.WARNING -> LogPoseLogger.w("THAMIS_SECURITY_WARNING: [$module] $message")
            else -> LogPoseLogger.i("THAMIS_SECURITY_INFO: [$module] $message")
        }
    }

    fun getActiveAlerts(): List<SecurityAlert> = alerts.toList()
}
