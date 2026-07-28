package com.uriel.logpose.thamis.journeyintelligence.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.journeyintelligence.context.JourneyContextAnalyzer
import com.uriel.logpose.thamis.journeyintelligence.model.JourneyInsight
import com.uriel.logpose.thamis.journeyintelligence.pattern.JourneyPatternAnalyzer

/**
 * Motor central de inteligencia de viaje v1.0.
 */
object JourneyIntelligenceEngine {

    fun analyzeCurrentJourney(distanceMeters: Double, timeMs: Long, routeId: String?): List<JourneyInsight> {
        val insights = mutableListOf<JourneyInsight>()
        
        // 1. Análisis de Contexto
        val pattern = routeId?.let { JourneyPatternAnalyzer.detectPattern(it) }
        val context = JourneyContextAnalyzer.analyze(distanceMeters, timeMs, pattern != null)

        LogPoseLogger.d("THAMIS_JOURNEY_CONTEXT: Tipo de viaje detectado: ${context.type}")

        // 2. Generación de Insights v1.0
        if (pattern != null) {
            insights.add(JourneyInsight(
                observation = "Ruta habitual detectada",
                benefit = "Conocimiento del tráfico local",
                recommendation = "Te sugiero habilitar modo SILENCIO para este tramo conocido"
            ))
        }

        return insights
    }
}
