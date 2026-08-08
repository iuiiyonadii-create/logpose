package com.uriel.logpose.features.diagnostics

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.features.voice.FeedbackManager
import com.uriel.logpose.features.voice.FeedbackPriority
import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.core.services.LogPoseHudService
import com.uriel.logpose.thamis.world.engine.WorldModelEngine

/**
 * ProactiveDiagnosticsEngine v4.5: Monitorea la telemetría en tiempo real con Inteligencia Situacional.
 * Misión #019.2: Prevención activa y recuperación de ruta.
 */
object ProactiveDiagnosticsEngine {

    private var lastFuelWarningTime = 0L
    private var lastTempWarningTime = 0L
    private var lastTireWarningTime = 0L

    fun check(snapshot: WorldSnapshot) {
        val vehicle = snapshot.vehicle
        val now = System.currentTimeMillis()

        // 1. Monitoreo de Combustible (Umbral adaptativo por velocidad)
        val fuelThreshold = if (vehicle.speedKmh > 100) 20 else 10
        if (vehicle.fuelLevelPct < fuelThreshold && (now - lastFuelWarningTime > 900_000)) {
            val msg = if (vehicle.fuelLevelPct < 10) 
                "URGENTE: Tenés solo un ${vehicle.fuelLevelPct} por ciento de nafta. ¿Querés que busque la estación más cercana?"
            else 
                "Che, te queda poca nafta (${vehicle.fuelLevelPct}%). ¿Buscamos donde cargar?"
            
            triggerWarning(msg, requiresInteraction = true, domain = "FUEL_LOW")
            lastFuelWarningTime = now
        }

        // 2. Monitoreo de Temperatura (Hardened contra falsos positivos)
        if (vehicle.engineTempCelsius > 110 && (now - lastTempWarningTime > 300_000)) {
            triggerWarning("ALERTA: Temperatura motor crítica: ${vehicle.engineTempCelsius} grados. Reducí la velocidad.", requiresInteraction = false)
            lastTempWarningTime = now
        }

        // 3. Monitoreo de Presión de Cubiertas (Misión #019.2)
        if (!vehicle.tirePressureOk && (now - lastTireWarningTime > 1800_000)) {
            triggerWarning("Ojo, detecto baja presión en una de las cubiertas. Revisalo en la próxima parada.", requiresInteraction = false)
            lastTireWarningTime = now
        }

        // 4. Monitoreo de Batería
        if (vehicle.batteryVoltage < 11.2f) {
            LogPoseLogger.w("Diagnostics: VOLTAJE DE BATERÍA CRÍTICO: ${vehicle.batteryVoltage}V")
            LogPoseHudService.updateStatus("⚠️ FALLO ELÉCTRICO")
        }
    }

    private fun triggerWarning(message: String, requiresInteraction: Boolean, domain: String? = null) {
        LogPoseLogger.i("Diagnostics: Disparando alerta proactiva: $message")
        
        // Integración Staff: Ducking y Feedback prioritario
        MusicManager.duck()
        FeedbackManager.speak(message, FeedbackPriority.CRITICAL) {
            MusicManager.unduck()
            
            if (requiresInteraction && domain != null) {
                // Preparamos al asistente para escuchar la respuesta (Si/No)
                WorldModelEngine.update("Diagnostics") { it.copy(
                    cognitive = it.cognitive.copy(
                        conversationState = "WAITING_CONFIRMATION",
                        activeIntent = "NAVIGATE_TO_STATION" // Placeholder para la intención de respuesta
                    )
                )}
            }
        }
        
        LogPoseHudService.updateStatus("⚠️ ALERTA: MOTO")
    }
}
