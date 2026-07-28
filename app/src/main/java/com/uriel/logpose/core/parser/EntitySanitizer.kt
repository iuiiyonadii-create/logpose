package com.uriel.logpose.core.parser

import com.uriel.logpose.features.voice.MusicVocabulary

/**
 * EntitySanitizer V3: Implacable contra verbos residuales duplicados por Vosk.
 * Protege nombres artísticos del vocabulario centralizado.
 */
object EntitySanitizer {

    private val VERB_RESIDUES = setOf(
        "poné", "pone", "pon", "poner",
        "reproduce", "reproducí", "reproducir", "reproduci",
        "tocá", "toca", "tocar",
        "dale", "play", "pausa", "repetir",
        "abrir", "abre", "abrí", "abri", "abrigos",
        "iniciar", "iniciá", "inicia",
        "lanzar", "lanzá", "lanza",
        "subir", "sube", "suba",
        "bajar", "baje", "baja",
        "siguiente", "anterior",
        "se", "me", "te", "lo", "la", "los", "las", "le", "les",
        "un", "una", "unos", "unas",
        "el", "del", "al", "de", "a", "con", "por", "para", "en", "y", "o", "que"
    )

    private val PHONETIC_GARBAGE = mapOf(
        "abrigos" to "",
        "sapo" to "whatsapp",
        "brigos" to "",
        "brí" to "",
        "thought" to "",
        "adult" to "",
        "one" to "",
        "quitan" to "",
        "obe" to "ysy",
        "xt" to "ysy a",
        "x" to "",
        "t" to "a"
    )

    fun sanitize(raw: String): String {
        if (raw.isBlank()) return ""

        var text = MusicVocabulary.normalize(raw)

        // 1. Reemplazar alucinaciones fonéticas conocidas
        PHONETIC_GARBAGE.forEach { (garbage, replacement) ->
            text = text.replace(garbage, replacement)
        }

        // 2. Iterar hasta estabilización
        var previous: String
        var iterations = 0
        do {
            previous = text
            text = cleanPass(text)
            iterations++
        } while (text != previous && iterations < 10)

        // 3. Protección de vocabulario (No destruir lo que ya conocemos)
        val rawCheck = MusicVocabulary.findBestMatch(raw)
        val sanitizedCheck = MusicVocabulary.findBestMatch(text)

        if (rawCheck != null && (sanitizedCheck == null || rawCheck.second > sanitizedCheck.second)) {
            if (rawCheck.second >= 0.70) return rawCheck.first
        }

        return text.trim()
    }

    private fun cleanPass(input: String): String {
        val tokens = input.split(Regex("\\s+")).toMutableList()
        val filtered = tokens.filter { token ->
            token !in VERB_RESIDUES && token.length >= 2
        }
        if (filtered.isEmpty()) {
            return tokens.maxByOrNull { it.length } ?: ""
        }
        return filtered.joinToString(" ")
    }
}
