package com.uriel.logpose.thamis.monitoring.health

import com.uriel.logpose.thamis.monitoring.model.*
import com.uriel.logpose.thamis.monitoring.telemetry.TelemetryCollector

/**
 * Evalúa la salud de los dominios basándose en telemetría y métricas.
 */
object HealthMonitor {

    private val domains = listOf(
        "Navigation", "Communication", "Dialog", "Planning", 
        "Notification", "Journey", "WorldModel", "Authority"
    )

    fun evaluateGlobalHealth(): BrainHealth {
        val domainHealthMap = mutableMapOf<String, DomainHealth>()
        var globalScoreSum = 0

        domains.forEach { domain ->
            val health = evaluateDomain(domain)
            domainHealthMap[domain] = health
            globalScoreSum += health.healthScore
        }

        val averageScore = globalScoreSum / domains.size
        val state = when {
            averageScore >= 90 -> HealthState.EXCELLENT
            averageScore >= 75 -> HealthState.GOOD
            averageScore >= 50 -> HealthState.WARNING
            else -> HealthState.CRITICAL
        }

        return BrainHealth(averageScore, state, domainHealthMap)
    }

    private fun evaluateDomain(name: String): DomainHealth {
        val history = TelemetryCollector.getPerformanceHistory(name)
        val errors = TelemetryCollector.getErrorCount(name)
        
        val avgLatency = if (history.isNotEmpty()) history.map { it.executionTimeMs }.average().toLong() else 0L
        val availability = if (errors > 5) 0.5f else 1.0f
        
        // Cálculo simplificado de salud v1.0
        var score = 100
        if (avgLatency > 500) score -= 20
        if (errors > 0) score -= (errors * 5).coerceAtMost(50)
        
        return DomainHealth(
            domainName = name,
            availability = availability,
            latencyMs = avgLatency,
            confidence = 0.95f, // Placeholder
            errorRate = errors.toFloat(),
            stabilityScore = 1.0f, // Placeholder
            healthScore = score.coerceIn(0, 100)
        )
    }
}
