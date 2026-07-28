package com.uriel.logpose.thamis_ai.nlu

/**
 * Cleans and standardizes raw speech input.
 */
class LanguageNormalizer {

    fun normalize(text: String): String {
        return text.lowercase()
            .trim()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
    }
}
