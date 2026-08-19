package com.uriel.logpose.thamis.knowledge

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.language.PhoneticEngine

/**
 * Unidad de conocimiento de THAMIS.
 */
data class KnowledgeRule(
    val intent: Intent,
    val phrases: Set<String>
) {
    // Caché de llaves fonéticas ALF-R v4.0 calculada de forma perezosa
    val phoneticKeys: Map<String, String> by lazy {
        phrases.associateWith { PhoneticEngine.getPhoneticKey(it, 0.0f) }
    }
}
