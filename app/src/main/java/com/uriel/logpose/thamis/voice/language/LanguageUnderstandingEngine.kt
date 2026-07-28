package com.uriel.logpose.thamis.voice.language

import com.uriel.logpose.thamis.voice.phonetic.PhoneticAnalyzer
import com.uriel.logpose.thamis.voice.recognition.SpeechNormalizer

/**
 * Motor de comprensión lingüística determinístico.
 */
object LanguageUnderstandingEngine {

    fun understand(text: String): String {
        val normalized = SpeechNormalizer.normalize(text)
        val canonical = PhoneticAnalyzer.processPhrase(normalized)
        
        // Aquí se pueden aplicar reglas gramaticales más complejas
        return canonical
    }

    /**
     * Resuelve fragmentos cortos basados en contexto.
     */
    fun resolveContextualFragment(fragment: String, currentDomain: String): String {
        return when (currentDomain) {
            "AUDIO" -> if (fragment.contains("subi") || fragment.contains("mas")) "SUBIR_VOLUMEN" else fragment
            "NAVIGATION" -> if (fragment.contains("llevame")) "NAV_TO" else fragment
            else -> fragment
        }
    }
}
