package com.uriel.logpose.thamis.validation

import com.uriel.logpose.thamis.intent.Intent

/**
 * Analiza sesiones de validación para generar informes cuantitativos.
 */
object ValidationAnalyzer {

    fun analyze(events: List<ValidationEvent>, version: String): ValidationReport {
        val total = events.size
        val matches = events.count { it.shadowResult.isMatch }
        
        // Simulación de detección de divergencias positivas/negativas
        // En v1: Consideramos positiva si Legacy es UNKNOWN y THAMIS tiene confianza > 0.8
        val positive = events.count { 
            it.legacyIntent == Intent.UNKNOWN && it.thamisDecision.isConclusive 
        }

        val critical = events.count { 
            !it.shadowResult.isMatch && (it.thamisDecision.winningEvaluation?.risk?.level ?: 0f) > 0.8f 
        }

        return ValidationReport(
            engineVersion = version,
            totalSamples = total,
            totalAccuracy = if (total > 0) (matches.toFloat() / total) * 100f else 0f,
            matchCount = matches,
            positiveDivergences = positive,
            negativeDivergences = 0, // Placeholder para análisis manual
            criticalErrors = critical,
            recommendations = listOf("Validación inicial completada. Se requiere análisis manual de divergencias.")
        )
    }
}
