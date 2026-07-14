package com.uriel.logpose.logcore.core.intent

/**
 * Convierte una VoiceRequest en un Intent utilizando
 * los patrones registrados en IntentPatterns.
 */
object IntentParser {

    fun parse(request: VoiceRequest): Intent {

        val text = request.text
            .lowercase()
            .trim()

        for (pattern in IntentPatterns.patterns) {

            if (pattern.phrases.any { phrase ->
                    text.contains(phrase.lowercase())
                }) {
                return pattern.intent
            }
        }

        throw IllegalArgumentException(
            "No se pudo interpretar: ${request.text}"
        )
    }
}