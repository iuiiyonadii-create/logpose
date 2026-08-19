package com.uriel.logpose.thamis.security.metrics

/**
 * KPIs de seguridad y auditoría.
 */
object SecurityMetrics {
    var totalAudits = 0
    var infoAlerts = 0
    var warningAlerts = 0
    var criticalAlerts = 0
    var resourceAccessCount = 0

    fun recordAudit() { totalAudits++ }
    fun recordAccess() { resourceAccessCount++ }
}
