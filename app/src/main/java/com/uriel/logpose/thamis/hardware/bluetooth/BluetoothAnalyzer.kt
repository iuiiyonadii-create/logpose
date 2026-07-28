package com.uriel.logpose.thamis.hardware.bluetooth

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.hardware.model.ConnectionSnapshot

/**
 * Analiza el estado y estabilidad de la pila Bluetooth.
 */
object BluetoothAnalyzer {
    private val connectionLog = mutableListOf<ConnectionSnapshot>()

    fun recordConnectionEvent(state: String, duration: Long) {
        val snapshot = ConnectionSnapshot(
            state = state,
            connectionTimeMs = duration,
            errorCount = if (state == "ERROR") 1 else 0,
            stabilityIndex = if (state == "CONNECTED") 1.0f else 0.5f
        )
        connectionLog.add(snapshot)
        if (connectionLog.size > 100) connectionLog.removeAt(0)
        
        LogPoseLogger.d("THAMIS_BLUETOOTH: Cambio de estado detectado -> $state")
    }

    fun getStabilityScore(): Float {
        if (connectionLog.isEmpty()) return 1.0f
        return connectionLog.map { it.stabilityIndex }.average().toFloat()
    }
}
