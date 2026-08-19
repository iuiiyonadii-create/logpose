package com.uriel.logpose.thamis.optimization.analysis

import com.uriel.logpose.thamis.optimization.model.OptimizationResult

/**
 * Evalúa el impacto de las optimizaciones realizadas.
 */
object OptimizationAnalyzer {

    fun analyzeResult(result: OptimizationResult): String {
        return if (result.improvementPct > 0) {
            "Mejora del ${result.improvementPct}% detectada. Impacto: ${result.systemImpact}"
        } else {
            "No se detectó mejora significativa tras la optimización."
        }
    }
}
