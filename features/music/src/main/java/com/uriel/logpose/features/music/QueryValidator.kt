package com.uriel.logpose.features.music

/**
 * QueryValidator V3: Bloquea números en español para evitar búsquedas fallidas.
 */
object QueryValidator {

    private val GARBAGE_WORDS = setOf(
        "ok", "okay", "um", "eh", "ah", "oh", "hm",
        "musica", "cancion", "tema", "numero", "opcion",
        "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez",
        "one", "two", "three", "four", "five", "si", "dale",
        "pone", "pon", "reproduce", "reproduci", "reproducir", "play"
    )

    fun validate(rawQuery: String): String? {
        val query = rawQuery.lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .trim()

        if (query in GARBAGE_WORDS) return null
        if (query.length < 3) return null

        val tokens = query.split(Regex("\\s+")).filter { it !in GARBAGE_WORDS && it.length >= 2 }
        if (tokens.isEmpty()) return null
        
        return tokens.joinToString(" ")
    }
}
