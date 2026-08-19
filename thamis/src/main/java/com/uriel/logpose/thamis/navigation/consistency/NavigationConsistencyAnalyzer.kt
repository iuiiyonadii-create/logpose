package com.uriel.logpose.thamis.navigation.consistency

import com.uriel.logpose.thamis.navigation.model.NavigationDecision
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Analiza la consistencia de las decisiones de navegación.
 */
object NavigationConsistencyAnalyzer {
    private const val TAG = "THAMIS_CONSISTENCY"

    data class Inconsistency(
        val type: String,
        val description: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val inconsistencies = mutableListOf<Inconsistency>()

    fun analyze(decision: NavigationDecision, previousDecision: NavigationDecision?) {
        if (previousDecision == null) return

        // 1. Detección de cambios de intención repentinos
        if (decision.goal.goalType != previousDecision.goal.goalType && 
            System.currentTimeMillis() - previousDecision.goal.confidence < 2000) { // Simulación de tiempo corto
            recordInconsistency("SUDDEN_INTENT_CHANGE", "Cambio de ${previousDecision.goal.goalType} a ${decision.goal.goalType} en menos de 2s")
        }

        // 2. Oscilaciones de confianza
        val diff = Math.abs(decision.confidence - previousDecision.confidence)
        if (diff > 0.4f) {
            recordInconsistency("CONFIDENCE_OSCILLATION", "Variación de confianza extrema: $diff")
        }
    }

    private fun recordInconsistency(type: String, description: String) {
        val inc = Inconsistency(type, description)
        inconsistencies.add(inc)
        LogPoseLogger.w("[$TAG] DETECTED: $type - $description")
    }

    fun getInconsistencies(): List<Inconsistency> = inconsistencies.toList()
}
