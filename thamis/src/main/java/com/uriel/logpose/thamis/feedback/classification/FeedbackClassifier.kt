package com.uriel.logpose.thamis.feedback.classification

import com.uriel.logpose.thamis.feedback.model.FeedbackCategory
import java.util.*

/**
 * Clasificador automático de texto de feedback.
 */
object FeedbackClassifier {

    fun classify(text: String): FeedbackCategory {
        val query = text.lowercase(Locale.getDefault())

        return when {
            query.contains("error") || query.contains("fallo") || query.contains("crash") -> FeedbackCategory.BUG
            query.contains("entiende") || query.contains("voz") || query.contains("escucha") -> FeedbackCategory.VOICE
            query.contains("comodo") || query.contains("molesta") || query.contains("interrumpe") -> FeedbackCategory.UX
            query.contains("peligro") || query.contains("seguro") || query.contains("distrae") -> FeedbackCategory.SAFETY
            query.contains("lento") || query.contains("tarda") -> FeedbackCategory.PERFORMANCE
            query.contains("confunde") || query.contains("no se que hacer") -> FeedbackCategory.CONFUSION
            query.contains("estaria bueno") || query.contains("quisiera") || query.contains("agregar") -> FeedbackCategory.REQUEST
            else -> FeedbackCategory.UX
        }
    }
}
