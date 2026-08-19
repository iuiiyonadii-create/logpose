package com.uriel.logpose.thamis.personalization.adaptation

import com.uriel.logpose.thamis.personalization.model.PreferenceType
import com.uriel.logpose.thamis.personalization.preference.PreferenceManager

/**
 * Aplica la personalización a las respuestas del sistema.
 */
object AdaptationEngine {

    fun adaptMessage(original: String, type: PreferenceType): String {
        val pref = PreferenceManager.getPreference(type) ?: return original
        
        return if (pref.value == "SHORT") {
            original.substringBefore(".")
        } else {
            original
        }
    }
}
