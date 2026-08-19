package com.uriel.logpose.thamis.calibration

import com.uriel.logpose.thamis.cognitive.model.Evidence
import com.uriel.logpose.thamis.shadow.ShadowResult

/**
 * Agrupa y analiza patrones en los resultados del Shadow Mode.
 */
object ShadowAnalytics {

    fun analyzeEvidenceUsage(results: List<ShadowResult>): Map<Evidence.Source, Int> {
        val usageMap = mutableMapOf<Evidence.Source, Int>()
        results.forEach { result ->
            result.thamisDecision.winningEvaluation?.hypothesis?.evidences?.forEach { evidence ->
                val count = usageMap.getOrDefault(evidence.source, 0)
                usageMap[evidence.source] = count + 1
            }
        }
        return usageMap
    }

    fun findCriticalErrors(results: List<ShadowResult>): Int {
        // En v1: Consideramos error crítico si THAMIS difiere en una llamada o acción física de alto riesgo
        return results.count { result ->
            !result.isMatch && result.thamisDecision.winningEvaluation?.risk?.level ?: 0f > 0.8f
        }
    }
}
