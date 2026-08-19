package com.uriel.logpose.thamis.calibration

import com.uriel.logpose.thamis.shadow.ShadowResult

/**
 * El orquestador de la calibración cognitiva.
 * Evalúa si THAMIS está listo para gobernar o si necesita más entrenamiento.
 */
object CalibrationEngine {

    fun analyze(results: List<ShadowResult>, engineVersion: String): CalibrationReport {
        val total = results.size
        val matches = results.count { it.isMatch }
        val divergences = total - matches
        val accuracy = if (total > 0) (matches.toFloat() / total) * 100f else 0f
        
        val criticalErrors = ShadowAnalytics.findCriticalErrors(results)
        
        return CalibrationReport(
            engineVersion = engineVersion,
            totalSamples = total,
            matchCount = matches,
            divergenceCount = divergences,
            accuracyPercentage = accuracy,
            criticalErrors = criticalErrors,
            recommendations = generateRecommendations(results, accuracy, criticalErrors),
            bestDecisions = results.filter { it.isMatch }.take(5),
            worstDecisions = results.filter { !it.isMatch }.take(5)
        )
    }

    private fun generateRecommendations(results: List<ShadowResult>, accuracy: Float, criticalErrors: Int): List<CalibrationRecommendation> {
        val list = mutableListOf<CalibrationRecommendation>()
        
        if (accuracy < 90f) {
            list.add(CalibrationRecommendation(
                CalibrationRecommendation.Type.DECREASE_CONFIDENCE,
                "La precisión es baja ($accuracy%).",
                "Aumentar umbrales de EXECUTE para evitar errores."
            ))
        }

        if (criticalErrors > 0) {
            list.add(CalibrationRecommendation(
                CalibrationRecommendation.Type.IMPROVE_CONTEXT,
                "Se detectaron $criticalErrors errores críticos.",
                "Revisar el RiskEvaluator para acciones de alto impacto."
            ))
        }

        return list
    }
}
