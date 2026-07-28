package com.uriel.logpose.features.voice

/**
 * EntityReconstructor: El "Artesano" de Thamis.
 * Reconstruye entidades cortadas o mal escuchadas usando prefijos y lógica fuzzy.
 */
object EntityReconstructor {

    fun reconstruct(sanitizedToken: String, vocabulary: List<String>): String {
        if (sanitizedToken.isBlank()) return ""
        val normalized = sanitizedToken.lowercase().trim()

        // 1. Match de Prefijo Exacto (Caso "ysy" -> "ysy a")
        if (normalized.length >= 3) {
            val prefixMatches = vocabulary.filter { it.lowercase().startsWith(normalized) }
            if (prefixMatches.size == 1) return prefixMatches.first()
        }

        // 2. Fallback a NoiseAwareMatcher para reconstrucción fuzzy
        val match = NoiseAwareMatcher.findBestMatch(
            normalized, 
            vocabulary, 
            voskConfidence = 0.5f, 
            noiseLevel = VoskVoiceEngine.getAmbientNoiseLevel()
        )

        return match?.bestMatch ?: sanitizedToken
    }
}
