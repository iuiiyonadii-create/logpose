package com.uriel.logpose.thamis.integrations

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 25.20 — THAMIS REAL WORLD INTEGRATION LAYER
 * FASE 6 & 7: VEHICLE CONNECTOR & SAFETY RULES
 */
object VehicleConnector {

    data class VehicleInfo(
        val speedKmh: Int,
        val fuelLevel: Float,
        val engineTemp: Float
    )

    /**
     * THAMIS puede recibir información, pero NUNCA controlar conducción.
     */
    fun onVehicleDataReceived(info: VehicleInfo) {
        LogPoseLogger.d("VehicleConnector: Datos del vehículo: Velocidad ${info.speedKmh} km/h")
    }

    /**
     * Regla de seguridad: THAMIS no puede acelerar, frenar ni doblar.
     */
    fun isSafetyCompliantAction(action: String): Boolean {
        val criticalActions = listOf("accelerate", "brake", "steer")
        return !criticalActions.contains(action.lowercase())
    }
}
