package com.uriel.logpose.thamis.voice.phonetic

/**
 * Analiza variaciones fonéticas, especialmente para el español Rioplatense/Argentino.
 */
object PhoneticAnalyzer {

    private val variations = mapOf(
        "poneme" to "pone",
        "poné" to "pone",
        "pon" to "pone",
        "llama" to "llamar",
        "llamá" to "llamar",
        "decile" to "decir",
        "decime" to "decir",
        "contesta" to "contestar",
        "contestá" to "contestar"
    )

    /**
     * Devuelve una versión canónica de la palabra para facilitar el reconocimiento de intención.
     */
    fun getCanonical(word: String): String {
        return variations[word] ?: word
    }

    /**
     * Procesa una frase completa normalizando verbos y variaciones regionales.
     */
    fun processPhrase(phrase: String): String {
        return phrase.split(" ").joinToString(" ") { getCanonical(it) }
    }
}
