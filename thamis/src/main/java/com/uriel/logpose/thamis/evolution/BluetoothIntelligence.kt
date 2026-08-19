package com.uriel.logpose.thamis.evolution

import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.util.concurrent.ConcurrentLinkedQueue

data class BluetoothEvent(
    val type: BluetoothEventType,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 0,
    val extra: String = ""
)

enum class BluetoothEventType {
    SCO_START_REQUEST,
    SCO_CONNECTED,
    SCO_DISCONNECTED,
    A2DP_RESTORED,
    CONNECTION_LOST,
    RECONNECTION_SUCCESS
}

/**
 * BluetoothFieldIntelligence: Monitor de bajo nivel para estabilidad de intercomunicadores.
 */
object BluetoothIntelligence {

    private val eventHistory = ConcurrentLinkedQueue<BluetoothEvent>()
    private var lastScoRequest: Long = 0

    fun recordEvent(type: BluetoothEventType, extra: String = "", forcedDuration: Long = -1) {
        val now = System.currentTimeMillis()
        var duration: Long = 0

        when (type) {
            BluetoothEventType.SCO_START_REQUEST -> {
                lastScoRequest = now
            }
            BluetoothEventType.SCO_CONNECTED -> {
                duration = if (forcedDuration >= 0) {
                    forcedDuration
                } else if (lastScoRequest > 0) {
                    now - lastScoRequest
                } else {
                    0
                }
                
                if (duration > 0) {
                    LogPoseLogger.d("BT_INTEL: Handshake SCO completado en ${duration}ms")
                }
            }
            else -> {}
        }

        eventHistory.add(BluetoothEvent(type, now, duration, extra))
        if (eventHistory.size > 500) eventHistory.poll()
    }

    fun getEventHistory() = eventHistory.toList()

    fun analyzeStability(): BluetoothStabilityReport {
        val history = eventHistory.toList()
        val disconnects = history.count { it.type == BluetoothEventType.CONNECTION_LOST }
        val avgScoHandshake = history.filter { it.type == BluetoothEventType.SCO_CONNECTED && it.durationMs > 0 }
            .map { it.durationMs }
            .average().takeIf { !it.isNaN() } ?: 0.0

        return BluetoothStabilityReport(
            disconnectCount = disconnects,
            avgScoHandshakeMs = avgScoHandshake.toLong(),
            isCritical = disconnects > 3 || avgScoHandshake > 2000
        )
    }
}

data class BluetoothStabilityReport(
    val disconnectCount: Int,
    val avgScoHandshakeMs: Long,
    val isCritical: Boolean
)
