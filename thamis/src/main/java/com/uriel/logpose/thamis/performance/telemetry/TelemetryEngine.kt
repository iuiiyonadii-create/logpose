package com.uriel.logpose.thamis.performance.telemetry

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.performance.model.PerformanceEvent
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Recolector central de eventos de telemetría.
 */
object TelemetryEngine {
    private val eventHistory = CopyOnWriteArrayList<PerformanceEvent>()

    fun record(event: PerformanceEvent) {
        eventHistory.add(event)
        if (eventHistory.size > 1000) eventHistory.removeAt(0)
        
        when (event) {
            is PerformanceEvent.ModuleFailed -> LogPoseLogger.e("THAMIS_TELEMETRY: [${event.module}] FAILED - ${event.error}")
            is PerformanceEvent.LatencyDetected -> LogPoseLogger.w("THAMIS_TELEMETRY: [${event.module}] HIGH LATENCY - ${event.latencyMs}ms")
            is PerformanceEvent.ResourceWarning -> LogPoseLogger.w("THAMIS_TELEMETRY: RESOURCE WARNING [${event.resource}] - ${event.description}")
            else -> {}
        }
    }

    fun getHistory(): List<PerformanceEvent> = eventHistory.toList()

    fun clear() = eventHistory.clear()
}
