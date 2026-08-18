package com.uriel.logpose.thamis

import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.util.concurrent.atomic.AtomicInteger

/**
 * DeepSleepManager: Implementa la "Regla de los 2 Fallos".
 * Si Thamis no entiende dos veces seguidas, el sistema entra en modo reposo
 * para proteger la CPU del ruido del viento.
 */
object DeepSleepManager {
    private val failureCount = AtomicInteger(0)
    private var sleepUntil = 0L
    private var backoffMultiplier = 1

    fun registerFailure() {
        val count = failureCount.incrementAndGet()
        if (count >= 2) {
            // v75.0: Backoff Exponencial Staff (60s -> 120s -> 240s...)
            val baseDuration = 60_000L
            val duration = baseDuration * backoffMultiplier
            sleepUntil = System.currentTimeMillis() + duration
            
            LogPoseLogger.w("DeepSleep", "🚨 Regla de 2 Fallos activada (Ciclo: $backoffMultiplier). Sueño: ${duration/1000}s.")
            
            // Notificamos al Lab
            val sleepTrace = org.json.JSONObject().apply {
                put("type", "SYSTEM_SLEEP")
                put("status", "ENTERING_DEEP_SLEEP")
                put("duration_ms", duration)
                put("multiplier", backoffMultiplier)
            }
            com.uriel.logpose.thamis.cognitive.CognitivePipeline.sendTelemetryProxy(sleepTrace)
            
            if (backoffMultiplier < 8) backoffMultiplier *= 2
        }
    }

    fun registerSuccess() {
        if (sleepUntil != 0L) {
             val wakeTrace = org.json.JSONObject().apply {
                put("type", "SYSTEM_SLEEP")
                put("status", "WAKING_UP")
            }
            com.uriel.logpose.thamis.cognitive.CognitivePipeline.sendTelemetryProxy(wakeTrace)
        }
        failureCount.set(0)
        sleepUntil = 0
        backoffMultiplier = 1
    }

    fun isSleeping(): Boolean {
        if (sleepUntil == 0L) return false
        val now = System.currentTimeMillis()
        if (now > sleepUntil) {
            sleepUntil = 0
            failureCount.set(0)
            return false
        }
        return true
    }
}
