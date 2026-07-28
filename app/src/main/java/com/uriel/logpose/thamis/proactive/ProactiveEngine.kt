package com.uriel.logpose.thamis.proactive

import com.uriel.logpose.core.Action
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.context.ContextEngine
import com.uriel.logpose.core.app.AppContainer

/**
 * FASE 26.14 — THAMIS LAB PROACTIVE INTELLIGENCE
 * Motor de alertas y sugerencias proactivas.
 * Permite que THAMIS tome la iniciativa basándose en el contexto sensorial.
 */
object ProactiveEngine {

    private const val SPEED_LIMIT_CRITICAL = 140f
    private const val BATTERY_THRESHOLD_LOW = 15
    
    private var lastSpeedAlertTimestamp = 0L
    private var lastBatteryAlertTimestamp = 0L
    private const val COOLDOWN_MS = 60_000L // 1 minuto entre alertas del mismo tipo

    /**
     * Evalúa el último estado del mundo y dispara acciones si es necesario.
     */
    fun evaluate() {
        val context = ContextEngine.getActiveContext()
        val lastSnapshot = context.snapshots.lastOrNull() ?: return
        val now = System.currentTimeMillis()

        // 1. Lógica de Velocidad Crítica (Prioridad Máxima)
        if (lastSnapshot.speedKmh > SPEED_LIMIT_CRITICAL) {
            if (now - lastSpeedAlertTimestamp > 15_000L) { // Alerta de velocidad es más frecuente (15s)
                triggerAlert("¡Cuidado con la velocidad! Bajá un poco.", isCritical = true)
                lastSpeedAlertTimestamp = now
            }
        }

        // 2. Lógica de Batería Baja
        if (lastSnapshot.batteryLevel < BATTERY_THRESHOLD_LOW) {
            if (now - lastBatteryAlertTimestamp > COOLDOWN_MS * 5) { // Batería avisa cada 5 min
                triggerAlert("Uriel, tenés menos del 15% de batería. ¿Querés que apague funciones no críticas?", isCritical = false)
                lastBatteryAlertTimestamp = now
            }
        }
        
        // 3. Lógica de Ruido (Sugerencia de seguridad)
        if (lastSnapshot.noiseLevelDb > 95f) {
            // Si el ruido es extremo, sugerir cerrar visera si la velocidad es alta
            if (lastSnapshot.speedKmh > 60f && now - lastBatteryAlertTimestamp > COOLDOWN_MS * 2) {
                triggerAlert("Mucho ruido de viento. Aseguráte de tener la visera cerrada.", isCritical = false)
            }
        }
    }

    private fun triggerAlert(message: String, isCritical: Boolean) {
        LogPoseLogger.i("ProactiveEngine: 🚨 ALERTA DISPARADA -> $message")
        // Creamos una respuesta de voz que el ActionManager procesará con prioridad crítica
        val action = Action.VoiceResponse(message)
        AppContainer.actionManager.execute(action)
    }
}
