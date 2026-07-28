package com.uriel.logpose.thamis.language

/**
 * Diccionario de sinónimos para expandir la comprensión de THAMIS.
 */
object SynonymDictionary {

    private val synonyms = mapOf(
        "reproducir" to setOf("pone", "inicia", "play", "escuchar", "mandale", "reproduci"),
        "detener" to setOf("pausa", "parar", "cortar", "para", "detene", "frena"),
        "llamar" to setOf("comunicar", "marcar", "telefonear", "llamame", "llamalo", "llamale"),
        "ir" to setOf("navegar", "llegar", "llevame", "anda", "rumbear", "encara"),
        "ayuda" to setOf("socorro", "auxilio", "aguante", "una mano")
    )

    fun getSynonyms(word: String): Set<String> {
        return synonyms[word] ?: emptySet()
    }
}