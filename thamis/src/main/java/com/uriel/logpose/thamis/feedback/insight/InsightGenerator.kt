package com.uriel.logpose.thamis.feedback.insight

import com.uriel.logpose.thamis.feedback.model.FeedbackCategory
import com.uriel.logpose.thamis.feedback.model.ProductInsight
import com.uriel.logpose.thamis.feedback.model.UserFeedbackEvent

/**
 * Genera conclusiones técnicas a partir del feedback analizado.
 */
object InsightGenerator {

    fun generateInsights(events: List<UserFeedbackEvent>): List<ProductInsight> {
        val insights = mutableListOf<ProductInsight>()
        
        val voiceErrors = events.filter { it.category == FeedbackCategory.VOICE }
        if (voiceErrors.size > 5) {
            insights.add(ProductInsight(
                observation = "Alta tasa de malentendidos vocales",
                impact = "Frustración en la interacción",
                frequency = voiceErrors.size,
                recommendation = "Mejorar el PhoneticAnalyzer para el voseo argentino"
            ))
        }

        val safetyConcerns = events.filter { it.category == FeedbackCategory.SAFETY }
        if (safetyConcerns.isNotEmpty()) {
            insights.add(ProductInsight(
                observation = "Reportes de distracción en ruta",
                impact = "Riesgo de seguridad crítico",
                frequency = safetyConcerns.size,
                recommendation = "Aumentar ventana de silencio en modo Highway"
            ))
        }

        return insights
    }
}
