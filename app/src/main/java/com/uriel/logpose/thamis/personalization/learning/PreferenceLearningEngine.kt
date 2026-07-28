package com.uriel.logpose.thamis.personalization.learning

import com.uriel.logpose.thamis.personalization.model.PreferenceType
import com.uriel.logpose.thamis.personalization.preference.PreferenceManager

/**
 * Analiza el comportamiento del usuario para aprender preferencias implícitas.
 */
object PreferenceLearningEngine {

    fun recordInteraction(domain: String, success: Boolean) {
        // En v1.0, simulamos aprendizaje: si hay muchos éxitos, sube la confianza del estilo actual
        if (success) {
            val current = PreferenceManager.getPreference(PreferenceType.MESSAGE_STYLE)
            if (current != null) {
                PreferenceManager.setPreference(
                    PreferenceType.MESSAGE_STYLE, 
                    current.value, 
                    (current.confidence + 0.05f).coerceAtMost(1.0f)
                )
            }
        }
    }
}
