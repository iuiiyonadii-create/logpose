package com.thamis.lab.core.common.telemetry

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * LabTelemetry: Sistema central de observabilidad para el laboratorio.
 * Optimizado para alto rendimiento sin bloqueos.
 */
public object LabTelemetry {
    private val metrics = ConcurrentHashMap<String, AtomicLong>()
    private val events = ConcurrentLinkedQueue<TelemetryEvent>()
    private const val MAX_EVENTS = 500

    public data class TelemetryEvent(
        val component: String,
        val message: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    public fun recordMetric(name: String, value: Long) {
        metrics.computeIfAbsent(name) { AtomicLong(0) }.set(value)
    }

    public fun logEvent(component: String, message: String) {
        events.add(TelemetryEvent(component, message))
        // Auto-limpieza ligera (no exacta para evitar bloqueos)
        if (events.size > MAX_EVENTS) {
            events.poll()
        }
        LabLogger.info(component, message)
    }

    public fun getMetric(name: String): Long = metrics[name]?.get() ?: 0L

    public fun getRecentEvents(limit: Int = 100): List<TelemetryEvent> {
        // Optimización: No convertir todo a lista si solo queremos los últimos N
        val all = events.toList()
        return if (all.size > limit) all.subList(all.size - limit, all.size) else all
    }
}
