package com.uriel.logpose.core.parser.normalization

import java.text.Normalizer

object CommandNormalizer {

    private val stopWords = setOf(
        "por",
        "favor",
        "porfavor",
        "che",
        "eh",
        "emm",
        "mmm",
        "hola",
        "ok",
        "okay",
        "bueno",
        "entonces",
        "me",
        "podria",
        "podrias",
        "podes",
        "podés",
        "quiero",
        "quisiera",
        "el",
        "la",
        "los",
        "las",
        "un",
        "una",
        "unos",
        "unas",
        "a"
    )

    fun normalize(text: String): String {

        val cleaned = Normalizer.normalize(
            text,
            Normalizer.Form.NFD
        )
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .replace("[^a-z0-9\\s]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        val tokens = cleaned
            .split(" ")
            .filter {
                it.isNotBlank() &&
                        it !in stopWords
            }

        val withoutWakeWord = stripWakeWordPrefix(tokens)

        val normalized = withoutWakeWord.joinToString(" ")

        return CommandAliasRepository.resolve(normalized)
    }

    private fun stripWakeWordPrefix(tokens: List<String>): List<String> {
        if (tokens.isEmpty()) return tokens

        // Manejar prefijos fonéticos de wake-word como "log pose", "log", "pose", "los pose", etc.
        val first = tokens.first()
        if (tokens.size >= 2 && (first == "log" || first == "los" || first == "look") && (tokens[1] == "pose" || tokens[1] == "pone" || tokens[1] == "pos" || tokens[1] == "puse")) {
            return tokens.drop(2)
        }
        if (first == "logpose" || first == "logpone" || first == "lospose" || first == "log") {
            return tokens.drop(1)
        }
        return tokens
    }
}