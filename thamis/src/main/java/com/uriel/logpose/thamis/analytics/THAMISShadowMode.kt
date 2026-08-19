package com.uriel.logpose.thamis.analytics

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.decision.ThamisDecision
import com.uriel.logpose.thamis.decision.DecisionType
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * THAMIS Shadow Mode: El comparador de inteligencia.
 * Ejecuta THAMIS en paralelo y mide su precisión contra el sistema legado.
 */
object THAMISShadowMode {

    private const val TAG = "THAMIS_SHADOW"
    
    private val totalRequests = AtomicInteger(0)
    private val matchCount = AtomicInteger(0)
    private val divergenceCount = AtomicInteger(0)
    private val totalProcessingTime = AtomicLong(0)

    fun record(legacyIntent: Intent, thamisDecision: ThamisDecision, processingTimeMs: Long) {
        val total = totalRequests.incrementAndGet()
        totalProcessingTime.addAndGet(processingTimeMs)

        val isMatch = legacyIntent == thamisDecision.intent
        
        if (isMatch) {
            matchCount.incrementAndGet()
            LogPoseLogger.i(TAG, "✅ [MATCH] Legacy: $legacyIntent | THAMIS: ${thamisDecision.intent} (${thamisDecision.confidence})")
        } else {
            divergenceCount.incrementAndGet()
            LogPoseLogger.w(TAG, "⚠️ [DIVERGENCE] Legacy: $legacyIntent | THAMIS: ${thamisDecision.intent}")
            LogPoseLogger.d(TAG, "   Motivo THAMIS: ${thamisDecision.reason}")
        }

        if (total % 5 == 0) {
            reportStats()
        }
    }

    private fun reportStats() {
        val total = totalRequests.get().toDouble()
        val matches = matchCount.get()
        val matchRate = if (total > 0) (matches / total) * 100 else 0.0
        val avgTime = if (total > 0) totalProcessingTime.get() / total else 0.0

        LogPoseLogger.i(TAG, "--- ESTADÍSTICAS SHADOW MODE ---")
        LogPoseLogger.i(TAG, "Total Comandos: ${total.toInt()}")
        LogPoseLogger.i(TAG, "Match Rate: %.2f%%".format(matchRate))
        LogPoseLogger.i(TAG, "Tiempo Medio: %.2fms".format(avgTime))
        LogPoseLogger.i(TAG, "--------------------------------")
    }

    fun getMatchRate(): Float {
        val total = totalRequests.get()
        if (total == 0) return 0f
        return (matchCount.get().toFloat() / total.toFloat()) * 100f
    }
}
