package com.uriel.logpose.thamis.feedback.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.feedback.analysis.FeedbackAnalyzer
import com.uriel.logpose.thamis.feedback.collector.FeedbackCollector
import com.uriel.logpose.thamis.feedback.insight.InsightGenerator
import com.uriel.logpose.thamis.feedback.learning.ImprovementPlanner
import com.uriel.logpose.thamis.feedback.model.ImprovementProposal

/**
 * Motor principal del ciclo de aprendizaje basado en feedback real.
 */
object FeedbackIntelligenceEngine {

    fun processCycle(): List<ImprovementProposal> {
        val events = FeedbackCollector.getAll()
        if (events.isEmpty()) return emptyList()

        LogPoseLogger.i("THAMIS_FEEDBACK: Iniciando análisis de ciclo sobre ${events.size} reportes.")

        val stats = FeedbackAnalyzer.analyze(events)
        LogPoseLogger.d("THAMIS_FEEDBACK: Estadísticas: $stats")

        val insights = InsightGenerator.generateInsights(events)
        val proposals = ImprovementPlanner.planImprovements(insights)

        LogPoseLogger.i("THAMIS_INSIGHT: Se generaron ${proposals.size} propuestas de mejora.")
        
        return proposals
    }
}
