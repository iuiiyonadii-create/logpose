package com.uriel.logpose.thamis.language

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.normalizer.LanguageNormalizer

/**
 * RioplatenseLinguisticNormalizer: Semantic mapping for Argentinian speech variations.
 * v71.1: Collapses equivalent commands into unified intents without losing raw text.
 */
object RioplatenseLinguisticNormalizer {

    private val MUSIC_SYNONYMS = setOf("poné", "pone", "poneme", "ponéme", "reproducí", "repro", "ponete", "largame", "mandale", "ponelo", "ponela", "pongas", "ponga", "ponen", "pones", "ponia")
    private val VOLUME_UP_SYNONYMS = setOf("subí", "sube", "subime", "más", "mas")
    private val VOLUME_DOWN_SYNONYMS = setOf("bajá", "baja", "bajame", "menos")
    private val CALL_SYNONYMS = setOf("llamá", "llama", "llamame", "marcar", "marcale", "marcame", "marcarle")
    private val NAV_SYNONYMS = setOf("llevame", "lleváme", "andá", "anda", "guiame", "guiáme", "encara", "encará", "ir", "vamos", "vaya", "vayas")

    /**
     * Map a raw transcript to its semantic intent with global trigger scanning.
     * v71.8: Scan the entire phrase for action triggers, not just the first token.
     */
    fun normalizeToIntent(text: String): Intent {
        val cleanText = cleanFillers(text.lowercase())
        val words = cleanText.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.isEmpty()) return Intent.UNKNOWN

        // Scaneamos toda la frase buscando disparadores (Rescate de comandos naturales)
        val hasMusicTrigger = words.any { it in MUSIC_SYNONYMS }
        val hasCallTrigger = words.any { it in CALL_SYNONYMS }
        val hasNavTrigger = words.any { it in NAV_SYNONYMS }
        
        return when {
            hasMusicTrigger -> Intent.PLAY_MUSIC
            hasCallTrigger -> Intent.CALL_CONTACT
            hasNavTrigger -> Intent.NAVIGATE
            cleanText.contains("subi") || cleanText.contains("sube") || cleanText.contains("volumen") -> Intent.SET_VOLUME
            cleanText.contains("baja") || cleanText.contains("bajá") -> Intent.SET_VOLUME
            cleanText.contains("viaje") || cleanText.contains("navegacion") || cleanText.contains("navegación") -> Intent.STOP_NAVIGATION
            cleanText.contains("siguiente") || cleanText.contains("pasa") -> Intent.NEXT_TRACK
            cleanText.contains("pausa") || cleanText.contains("parar") || cleanText.contains("detener") -> Intent.PAUSE_MUSIC
            else -> Intent.UNKNOWN
        }
    }

    /**
     * Cleans common rioplatense fillers while preserving entities.
     */
    fun cleanFillers(text: String): String {
        return text.lowercase()
            .replace(Regex("\\b(che|posta|viste|nada|esteee|este|bueno|okay|ok|dale)\\b"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
